/*
 * Copyright 2016 Chaos
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.chaos.fx.cnbeta.details;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import org.chaos.fx.cnbeta.R;
import org.chaos.fx.cnbeta.app.BaseFragment;
import org.chaos.fx.cnbeta.net.model.NewsContent;
import org.chaos.fx.cnbeta.util.TimeStringHelper;
import org.chaos.fx.cnbeta.wxapi.WXApiProvider;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;

import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * @author Chaos
 *         4/2/16
 */
public class ContentFragment extends BaseFragment {

    private static final String KEY_SID = "sid";
    private static final String KEY_TOPIC_LOGO = "topic_logo";
    private static final String KEY_HTML_CONTENT = "html_content";
    private static final String KEY_COMMENT_COUNT = "comment_count";

    public static ContentFragment newInstance(int sid, String topicLogoLink, String htmlBody, int commentCount) {
        Bundle args = new Bundle();
        args.putInt(KEY_SID, sid);
        args.putString(KEY_TOPIC_LOGO, topicLogoLink);
        args.putString(KEY_HTML_CONTENT, htmlBody);
        args.putInt(KEY_COMMENT_COUNT, commentCount);
        ContentFragment fragment = new ContentFragment();
        fragment.setArguments(args);
        return fragment;
    }

    private int mSid;
    private String mLogoLink;
    private String mHtmlContent;
    private int mCommentCount;

    @Bind(R.id.title) TextView title;
    @Bind(R.id.source) TextView source;
    @Bind(R.id.author) TextView author;
    @Bind(R.id.time) TextView time;
    @Bind(R.id.comment_count) TextView commentCountView;
    @Bind(R.id.author_image) ImageView authorImg;

    @Bind(R.id.content_layout) LinearLayout contentLayout;

    private Subscription mContentSubscription;

    private Transformation transformation = new Transformation() {
        @Override
        public Bitmap transform(Bitmap source) {
            int targetWidth = contentLayout.getWidth();

            if (source.getWidth() > 10) {
                double aspectRatio = (double) source.getHeight() / (double) source.getWidth();
                int targetHeight = (int) (targetWidth * aspectRatio);
                Bitmap result = Bitmap.createScaledBitmap(source, targetWidth, targetHeight, false);
                if (result != source) {
                    // Same bitmap is returned if sizes are the same
                    source.recycle();
                }
                return result;
            } else {
                return source;
            }
        }

        @Override
        public String key() {
            return "desiredWidth";
        }
    };

    private OnShowCommentListener mOnShowCommentListener;

    private NewsContent mNewsContent;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        setHasOptionsMenu(true);
        mOnShowCommentListener = (OnShowCommentListener) activity;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_content, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ButterKnife.bind(this, view);

        mSid = getArguments().getInt(KEY_SID);
        mLogoLink = getArguments().getString(KEY_TOPIC_LOGO);
        mHtmlContent = getArguments().getString(KEY_HTML_CONTENT);
        mCommentCount = getArguments().getInt(KEY_COMMENT_COUNT);

        commentCountView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnShowCommentListener.onShowComment();
            }
        });

        loadContent();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mContentSubscription != null) {
            mContentSubscription.unsubscribe();
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        if (!hidden) {
            setActionBarTitle(R.string.content);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.content_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.comment:
                mOnShowCommentListener.onShowComment();
                return true;
            case R.id.wechat_friends:
                shareUrlToWechat(false);
                return true;
            case R.id.wechat_timeline:
                shareUrlToWechat(true);
                return true;
            case R.id.open_in_browser:
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(
                        String.format(Locale.getDefault(), "http://www.cnbeta.com/articles/%d.htm", mSid))));
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void shareUrlToWechat(boolean toTimeline) {
        WXApiProvider.shareUrl(String.format(Locale.getDefault(), "http://m.cnbeta.com/view_%d.htm", mSid),
                mNewsContent.getTitle(),
                Jsoup.parseBodyFragment(mNewsContent.getHomeText()).text(),
                ((BitmapDrawable) authorImg.getDrawable()).getBitmap(), toTimeline);
    }

    public void updateCommentCount(int count) {
        if (commentCountView != null && isVisible()) {
            commentCountView.setText(String.format(getString(R.string.content_comment_count), count));
        }
    }

    private void loadContent() {
        mContentSubscription = Observable.just(mHtmlContent)
                .subscribeOn(Schedulers.io())
                .map(new Func1<String, NewsContent>() {
                    @Override
                    public NewsContent call(String html) {
                        NewsContent newsContent = parseHtmlContent(html);
                        newsContent.setCommentCount(mCommentCount);
                        return newsContent;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<NewsContent>() {
                    @Override
                    public void call(NewsContent newsContent) {
                        mNewsContent = newsContent;
                        parseNewsContent(newsContent);
                    }
                });
    }

    private NewsContent parseHtmlContent(String html) {
        Element body = Jsoup.parse(html).body();
        String title = body.getElementById("news_title").text();
        String source = body.select("span.where").text();
        source = source.substring(3, source.length());
        String time = body.select("span.date").text();
        String homeText = body.select("div.introduction > p").text();
        String thumb = body.select("a > img[title]").attr("src").replace("http://static.cnbetacdn.com", "");
        String bodyText = body.getElementsByClass("content").html();
        String author = body.getElementsByClass("author").text();
        author = author.substring(6, author.length() - 1);
        NewsContent newsContent = new NewsContent();
        newsContent.setTitle(title);
        newsContent.setTime(time);
        newsContent.setHomeText(homeText);
        newsContent.setBodyText(bodyText);
        newsContent.setThumb(thumb);
        newsContent.setSource(source);
        newsContent.setAuthor(author);
        return newsContent;
    }

    @SuppressLint("SetTextI18n")
    private void parseNewsContent(NewsContent newsContent) {
        newsContent.setBodyText(
                newsContent.getBodyText()
                        .replaceAll("&quot;", "\"")
                        .replaceAll("&lt;", "<")
                        .replaceAll("&gt;", ">")
                        .replaceAll("&nbsp;", " "));

        Picasso.with(getActivity())
                .load(TextUtils.isEmpty(mLogoLink)
                        ? "http://static.cnbetacdn.com" + newsContent.getThumb()
                        : mLogoLink)
                .into(authorImg);

        title.setText(newsContent.getTitle());
        author.setText("By " + newsContent.getAuthor());
        time.setText(TimeStringHelper.getTimeString(newsContent.getTime()));
        commentCountView.setText(String.format(getString(R.string.content_comment_count), newsContent.getCommentCount()));

        source.setText(newsContent.getSource());

        Document doc = Jsoup.parseBodyFragment(newsContent.getHomeText() + newsContent.getBodyText());
        Elements textareas = doc.select("textarea");
        if (!textareas.isEmpty()) {
            textareas.first().remove();
        }
        addViewByNode(doc.body());
    }

    private void addViewByNode(Node node) {
        StringBuilder sb = new StringBuilder();
        addView(sb, node);
        if (sb.length() > 0) {
            removeLastUselessChars(sb);// 移除最后两个回车符
            addTextView(sb.toString());
        }
    }

    private void addView(StringBuilder sb, Node node) {
        int preSBLen = sb.length();
        for (Node subNode : node.childNodes()) {
            if ("img".equals(subNode.nodeName())) {
                if (sb.length() > 0) {
                    removeLastUselessChars(sb);// 移除最后两个回车符
                    if (sb.length() > 0) {
                        addTextView(sb.toString());
                        sb.delete(0, sb.length());
                    }
                    preSBLen = 0;
                }
                addImageView(subNode.attributes().get("src"));
            } else if ("#text".equals(subNode.nodeName())) {
                sb.append(((TextNode) subNode).text());
            } else if ("embed".equals(subNode.nodeName())) {
                String src = subNode.attr("src");
                if (!TextUtils.isEmpty(src)) {
                    removeLastUselessChars(sb);
                    sb.append("\n\n") // 与上边文字隔开
                            .append(src);
                }
            } else {
                addView(sb, subNode);
            }
        }
        if (sb.length() - preSBLen > 0 && "p".equals(node.nodeName())) {
            sb.append("\n\n");
        }
    }

    private void removeLastUselessChars(StringBuilder sb) {
        int idx = sb.length() - 1;
        while (sb.length() > 0 &&
                (sb.charAt(idx) == '\n'
                        || sb.charAt(idx) == '\r'
                        || sb.charAt(idx) == ' ')) {
            sb.delete(sb.length() - 1, sb.length());
            idx = sb.length() - 1;
        }
    }

    private void addTextView(String text) {
        TextView view = (TextView) getActivity().getLayoutInflater().inflate(R.layout.article_content_text_item, contentLayout, false);
        contentLayout.addView(view);
        view.setText(text);
        Linkify.addLinks(view, Linkify.WEB_URLS);
    }

    private void addImageView(String link) {
        ImageView view = (ImageView) getActivity().getLayoutInflater().inflate(R.layout.article_content_img_item, contentLayout, false);
        contentLayout.addView(view);
        Picasso.with(getActivity()).load(link).transform(transformation).into(view);
    }

    public interface OnShowCommentListener {
        void onShowComment();
    }
}
