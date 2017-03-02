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
import android.text.TextUtils;

import org.chaos.fx.cnbeta.net.model.NewsContent;
import org.chaos.fx.cnbeta.wxapi.WXApiProvider;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

/**
 * @author Chaos
 *         10/26/16
 */

class DetailsPresenter implements DetailsContract.Presenter {

    private int mSid;
    private String mLogoLink;

    private NewsContent mNewsContent;
    private DetailsContract.View mView;

    private Disposable mContentDisposable;

    private List<String> mImageUrls = new ArrayList<>();

    DetailsPresenter(int sid, String logo) {
        mSid = sid;
        mLogoLink = logo;
    }

    @Override
    public void shareUrlToWechat(Bitmap bitmap, boolean toTimeline) {
        WXApiProvider.shareUrl(String.format(Locale.getDefault(), "http://m.cnbeta.com/view_%d.htm", mSid),
                mNewsContent.getTitle(),
                Jsoup.parseBodyFragment(mNewsContent.getHomeText()).text(),
                bitmap, toTimeline);
    }

    @Override
    public String[] getAllImageUrls() {
        return mImageUrls.toArray(new String[mImageUrls.size()]);
    }

    @Override
    public int indexOfImage(String url) {
        return mImageUrls.indexOf(url);
    }

    @Override
    public void subscribe(DetailsContract.View view) {
        mView = view;
    }

    @Override
    public void unsubscribe() {
        mView.clearViewInContent();
        if (mContentDisposable != null) {
            mContentDisposable.dispose();
        }
    }

    @Override
    public void loadContentByNewsContent(NewsContent content) {
        mNewsContent = content;
//        parseNewsContent(content);
        mContentDisposable = Observable.just(content)
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<NewsContent>() {
                    @Override
                    public void accept(NewsContent content) throws Exception {
                        parseNewsContent(content);
                    }
                });
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
        // WebApi 转换的 NewsContent 以及 MobileApi 的 comment count 都是 0，
        // 直接交给 CommentFragment 传过来。只有 MobileApi 才直接设置
//        mView.setCommentCount(newsContent.getCommentCount());

        String source = Jsoup.parse(newsContent.getSource()).text();
        newsContent.setSource(source);
        mView.setSource(source);

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
                String link = subNode.attributes().get("src");
                mView.addImageToContent(link);
                mImageUrls.add(link);
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
