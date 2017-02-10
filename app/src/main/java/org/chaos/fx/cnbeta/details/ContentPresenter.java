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
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;

import org.chaos.fx.cnbeta.net.model.NewsContent;
import org.chaos.fx.cnbeta.wxapi.WXApiProvider;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;

import java.util.Locale;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

import static org.chaos.fx.cnbeta.details.ContentFragment.KEY_COMMENT_COUNT;
import static org.chaos.fx.cnbeta.details.ContentFragment.KEY_HTML_CONTENT;
import static org.chaos.fx.cnbeta.details.ContentFragment.KEY_SID;
import static org.chaos.fx.cnbeta.details.ContentFragment.KEY_TOPIC_LOGO;

/**
 * @author Chaos
 *         10/26/16
 */

public class ContentPresenter implements ContentContract.Presenter {

    private int mSid;
    private String mLogoLink;
    private String mHtmlContent;
    private int mCommentCount;

    private NewsContent mNewsContent;
    private ContentContract.View mView;

    private Disposable mContentDisposable;

    public ContentPresenter(Bundle arguments, ContentContract.View view) {
        mSid = arguments.getInt(KEY_SID);
        mLogoLink = arguments.getString(KEY_TOPIC_LOGO);
        mHtmlContent = arguments.getString(KEY_HTML_CONTENT);
        mCommentCount = arguments.getInt(KEY_COMMENT_COUNT);
        mView = view;
    }

    @Override
    public void shareUrlToWechat(Bitmap bitmap, boolean toTimeline) {
        WXApiProvider.shareUrl(String.format(Locale.getDefault(), "http://m.cnbeta.com/view_%d.htm", mSid),
                mNewsContent.getTitle(),
                Jsoup.parseBodyFragment(mNewsContent.getHomeText()).text(),
                bitmap, toTimeline);
    }

    @Override
    public void subscribe() {
        loadContent();
    }

    @Override
    public void unsubscribe() {
        mView.clearViewInContent();
        mContentDisposable.dispose();
    }

    private void loadContent() {
        mContentDisposable = Observable.just(mHtmlContent)
                .subscribeOn(Schedulers.io())
                .map(new Function<String, NewsContent>() {
                    @Override
                    public NewsContent apply(String html) {
                        NewsContent newsContent = parseHtmlContent(html);
                        newsContent.setCommentCount(mCommentCount);
                        return newsContent;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<NewsContent>() {
                    @Override
                    public void accept(NewsContent newsContent) {
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

        Element contentElement = body.getElementsByClass("content").first();
        int elementSize = contentElement.childNodes().size();
        for (int i = elementSize - 1; i >= elementSize - 3; i--) {// 移除广告
            contentElement.childNodes().get(i).remove();
        }
        String bodyText = contentElement.html();

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


        mView.loadAuthorImage(
                TextUtils.isEmpty(mLogoLink)
                        ? "http://static.cnbetacdn.com" + newsContent.getThumb()
                        : mLogoLink);
        mView.setTitle(newsContent.getTitle());
        mView.setAuthor(newsContent.getAuthor());
        mView.setTimeString(newsContent.getTime());
        mView.setCommentCount(newsContent.getCommentCount());

        mView.setSource(newsContent.getSource());

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
            mView.addTextToContent(sb.toString());
        }
    }

    private void addView(StringBuilder sb, Node node) {
        int preSBLen = sb.length();
        for (Node subNode : node.childNodes()) {
            String subNodeName = subNode.nodeName();
            if ("img".equals(subNodeName)) {
                if (sb.length() > 0) {
                    removeLastUselessChars(sb);// 移除最后两个回车符
                    if (sb.length() > 0) {
                        mView.addTextToContent(sb.toString());
                        sb.delete(0, sb.length());
                    }
                    preSBLen = 0;
                }
                mView.addImageToContent(subNode.attributes().get("src"));
            } else if ("#text".equals(subNodeName)) {
                sb.append(((TextNode) subNode).text());
            } else if ("embed".equals(subNodeName)) {// 搜狐, 土豆
                String src = subNode.attr("src");
                if (!TextUtils.isEmpty(src)) {
                    removeLastUselessChars(sb);
                    sb.append("\n\n") // 与上边文字隔开
                            .append(src);
                }
            } else if ("object".equals(subNodeName) && "FPlayer".equals(subNode.attr("id"))) {// 网易视频
                String src = subNode.attr("data");
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
}
