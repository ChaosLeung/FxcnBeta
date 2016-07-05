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
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
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
import org.chaos.fx.cnbeta.net.CnBetaApi;
import org.chaos.fx.cnbeta.net.CnBetaApiHelper;
import org.chaos.fx.cnbeta.net.exception.RequestFailedException;
import org.chaos.fx.cnbeta.net.model.NewsContent;
import org.chaos.fx.cnbeta.util.TimeStringHelper;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;

import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * @author Chaos
 *         4/2/16
 */
public class ContentFragment extends BaseFragment {

    private static final String KEY_SID = "sid";
    private static final String KEY_TOPIC_LOGO = "topic_logo";

    public static ContentFragment newInstance(int sid, String topicLogoLink) {
        Bundle args = new Bundle();
        args.putInt(KEY_SID, sid);
        args.putString(KEY_TOPIC_LOGO, topicLogoLink);
        ContentFragment fragment = new ContentFragment();
        fragment.setArguments(args);
        return fragment;
    }

    private int mSid;
    private String mLogoLink;

    @Bind(R.id.title) TextView title;
    @Bind(R.id.source) TextView source;
    @Bind(R.id.author) TextView author;
    @Bind(R.id.time) TextView time;
    @Bind(R.id.comment_count) TextView commentCount;
    @Bind(R.id.author_image) ImageView authorImg;

    @Bind(R.id.content_layout) LinearLayout contentLayout;

    @Bind(R.id.loading_view)
    View mLoadingBar;
    @Bind(R.id.error_layout)
    View mErrorLayout;
    @Bind(R.id.error_button)
    View mRetryButton;

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


        mRetryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadContent();
            }
        });
        commentCount.setOnClickListener(new View.OnClickListener() {
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
            case R.id.open_in_browser:
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(
                        String.format(Locale.getDefault(), "http://www.cnbeta.com/articles/%d.htm", mSid))));
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void loadContent() {
        mContentSubscription = CnBetaApiHelper.articleContent(mSid)
                .subscribeOn(Schedulers.io())
                .map(new Func1<CnBetaApi.Result<NewsContent>, NewsContent>() {
                    @Override
                    public NewsContent call(CnBetaApi.Result<NewsContent> newsContentResult) {
                        if (!newsContentResult.isSuccess()) {
                            throw new RequestFailedException();
                        }
                        return newsContentResult.result;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<NewsContent>() {
                    @Override
                    public void onCompleted() {
                        mLoadingBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError(Throwable e) {
                        mErrorLayout.setVisibility(View.VISIBLE);
                        mLoadingBar.setVisibility(View.GONE);
                    }

                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onNext(NewsContent newsContent) {
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
                        commentCount.setText(String.format(getString(R.string.content_comment_count), newsContent.getCommentCount()));

                        Document doc = Jsoup.parseBodyFragment(newsContent.getSource());
                        source.setText(findTagText(doc));

                        doc = Jsoup.parseBodyFragment(newsContent.getHomeText() + newsContent.getBodyText());
                        Elements textareas = doc.select("textarea");
                        if (!textareas.isEmpty()) {
                            textareas.first().remove();
                        }
                        addViewByNode(doc.body());
                    }
                });
    }

    private String findTagText(Node node) {
        String tagText = "";
        for (Node subNode : node.childNodes()) {
            if ("#text".equals(subNode.nodeName())) {
                return ((TextNode) subNode).text();
            } else {
                tagText = findTagText(subNode);
            }
        }
        return tagText;
    }

    private void addViewByNode(Node node) {
        StringBuilder sb = new StringBuilder();
        addView(sb, node);
        if (sb.length() > 0) {
            removeLastEnterChars(sb);
            addTextView(sb.toString());// 移除最后两个回车符
        }
    }

    private void addView(StringBuilder sb, Node node) {
        int preSBLen = sb.length();
        for (Node subNode : node.childNodes()) {
            if ("img".equals(subNode.nodeName())) {
                if (sb.length() > 0) {
                    removeLastEnterChars(sb);
                    addTextView(sb.toString());// 移除最后两个回车符
                    sb.delete(0, sb.length());
                    preSBLen = 0;
                }
                addImageView(subNode.attributes().get("src"));
            } else if ("#text".equals(subNode.nodeName())) {
                sb.append(((TextNode) subNode).text());
            } else {
                addView(sb, subNode);
            }
        }
        if (sb.length() - preSBLen > 0 && "p".equals(node.nodeName())) {
            sb.append("\n\n");
        }
    }

    private void removeLastEnterChars(StringBuilder sb) {
        if (sb.length() > 1 && sb.lastIndexOf("\n\n") == sb.length() - 2) {
            sb.delete(sb.length() - 2, sb.length());
        }
    }

    private void addTextView(String text) {
        TextView view = (TextView) getActivity().getLayoutInflater().inflate(R.layout.article_content_text_item, contentLayout, false);
        contentLayout.addView(view);
        view.setText(text);
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
