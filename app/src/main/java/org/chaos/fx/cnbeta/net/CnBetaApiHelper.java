/*
 * Copyright 2015 Chaos
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

package org.chaos.fx.cnbeta.net;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;
import com.jakewharton.picasso.OkHttp3Downloader;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import org.chaos.fx.cnbeta.net.model.ArticleSummary;
import org.chaos.fx.cnbeta.net.model.Comment;
import org.chaos.fx.cnbeta.net.model.HotComment;
import org.chaos.fx.cnbeta.net.model.NewsContent;
import org.chaos.fx.cnbeta.net.model.Topic;
import org.chaos.fx.cnbeta.net.model.WebCaptcha;
import org.chaos.fx.cnbeta.net.model.WebCommentResult;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.reactivex.Observable;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * @author Chaos
 *         2015/11/03.
 */
public class CnBetaApiHelper {

    private static MobileApi sMobileApi;
    private static MWebApi sMWebApi;
    private static WebApi sWebApi;

    private static OkHttp3Downloader sCookieDownloader;

    public static void initialize() {
        sMobileApi = new Retrofit.Builder()
                .baseUrl(MobileApi.BASE_URL)
                .addConverterFactory(
                        GsonConverterFactory.create(
                                new GsonBuilder().setExclusionStrategies(new ExclusionStrategy() {
                                    @Override
                                    public boolean shouldSkipField(FieldAttributes f) {
                                        return f.getAnnotation(SerializedName.class) == null;
                                    }

                                    @Override
                                    public boolean shouldSkipClass(Class<?> clazz) {
                                        return false;
                                    }
                                }).create()))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()
                .create(MobileApi.class);

        sMWebApi = new Retrofit.Builder()
                .baseUrl(MWebApi.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()
                .create(MWebApi.class);

        OkHttpClient okHttpClient = CnBetaHttpClientProvider.newCnBetaHttpClient();

        sWebApi = new Retrofit.Builder()
                .baseUrl(WebApi.HOST_URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()
                .create(WebApi.class);

        sCookieDownloader = new OkHttp3Downloader(okHttpClient);
    }

    public static OkHttp3Downloader okHttp3Downloader() {
        return sCookieDownloader;
    }

    public static Observable<MobileApi.Result<List<ArticleSummary>>> articles() {
        long timestamp = System.currentTimeMillis();
        return sMobileApi.articles(timestamp, CnBetaSignUtil.articlesSign(timestamp));
    }

    public static Observable<MobileApi.Result<List<ArticleSummary>>> topicArticles(String topicId) {
        long timestamp = System.currentTimeMillis();
        return sMobileApi.topicArticles(
                timestamp,
                CnBetaSignUtil.topicArticlesSign(timestamp, topicId),
                topicId);
    }

    public static Observable<MobileApi.Result<List<ArticleSummary>>> newArticles(String topicId, int startSid) {
        long timestamp = System.currentTimeMillis();
        return sMobileApi.newArticles(
                timestamp,
                CnBetaSignUtil.newArticlesSign(timestamp, topicId, startSid),
                topicId,
                startSid);
    }

    public static Observable<MobileApi.Result<List<ArticleSummary>>> oldArticles(String topicId,
                                                                                 int endSid) {
        long timestamp = System.currentTimeMillis();
        return sMobileApi.oldArticles(
                timestamp,
                CnBetaSignUtil.oldArticlesSign(timestamp, topicId, endSid),
                topicId,
                endSid);
    }

    public static Observable<MobileApi.Result<NewsContent>> articleContent(int sid) {
        long timestamp = System.currentTimeMillis();
        return sMobileApi.articleContent(
                timestamp,
                CnBetaSignUtil.articleContentSign(timestamp, sid),
                sid);
    }

    /**
     * 获取评论列表
     *
     * Note: 如果评论已关闭, 获取的列表必为空
     *
     * @param sid  文章 id
     * @param page 页
     */
    public static Observable<MobileApi.Result<List<Comment>>> comments(int sid,
                                                                       int page) {
        long timestamp = System.currentTimeMillis();
        return sMobileApi.comments(
                timestamp,
                CnBetaSignUtil.commentsSign(timestamp, sid, page),
                sid,
                page);
    }

    public static Observable<List<Comment>> closedComments(int sid) {
        long timestamp = System.currentTimeMillis();
        return sMobileApi.closedComments(
                timestamp,
                CnBetaSignUtil.closedCommentsSign(timestamp, sid),
                sid);
    }

    public static Observable<MobileApi.Result<String>> addComment(int sid,
                                                                  String content) {
        long timestamp = System.currentTimeMillis();
        return sMobileApi.addComment(
                timestamp,
                CnBetaSignUtil.addCommentSign(timestamp, sid, content),
                sid,
                content);
    }

    @Deprecated
    public static Observable<MobileApi.Result<String>> replyComment(int sid,
                                                                    int pid,
                                                                    String content) {
        long timestamp = System.currentTimeMillis();
        return sMobileApi.replyComment(
                timestamp,
                CnBetaSignUtil.replyCommentSign(timestamp, sid, pid, content),
                sid,
                pid,
                content);
    }

    @Deprecated
    public static Observable<MobileApi.Result<String>> supportComment(int tid) {
        long timestamp = System.currentTimeMillis();
        return sMobileApi.supportComment(
                timestamp,
                CnBetaSignUtil.supportCommentSign(timestamp, tid),
                tid);
    }

    @Deprecated
    public static Observable<MobileApi.Result<String>> againstComment(int tid) {
        long timestamp = System.currentTimeMillis();
        return sMobileApi.againstComment(
                timestamp,
                CnBetaSignUtil.againstCommentSign(timestamp, tid),
                tid);
    }

    public static Observable<MobileApi.Result<List<HotComment>>> hotComment() {
        long timestamp = System.currentTimeMillis();
        return sMobileApi.hotComment(timestamp, CnBetaSignUtil.hotCommentSign(timestamp));
    }

    public static Observable<MobileApi.Result<List<ArticleSummary>>> todayRank(@MobileApi.RankType String type) {
        long timestamp = System.currentTimeMillis();
        return sMobileApi.todayRank(
                timestamp,
                CnBetaSignUtil.todayRankSign(timestamp, type),
                type);
    }

    public static Observable<MobileApi.Result<List<ArticleSummary>>> top10() {
        long timestamp = System.currentTimeMillis();
        return sMobileApi.top10(timestamp, CnBetaSignUtil.top10Sign(timestamp));
    }

    public static Observable<MobileApi.Result<List<Topic>>> topics() {
        long timestamp = System.currentTimeMillis();
        return sMobileApi.topics(timestamp, CnBetaSignUtil.topicsSign(timestamp));
    }

    public static final Pattern SN_PATTERN = Pattern.compile("SN:\"(.{5})\"");

    public static String getSNFromArticleBody(String s) {
        Matcher matcher = SN_PATTERN.matcher(s);
        if (matcher.find()) {
            return matcher.group(1);
        } else {
            return null;
        }
    }

    public static Observable<ResponseBody> getHomeHtml() {
        return sWebApi.getHomeHtml();
    }

    public static Observable<ResponseBody> getArticleHtml(int sid) {
        return sWebApi.getArticleHtml(sid);
    }

    /**
     * 获取网页版的评论内容
     *
     * Note: 可以使用该 API 获取到评论用的 token
     *
     * @param sid   文章 id
     * @param token 获取 Web 评论用的 token
     * @param sn    每篇文章的 sn 码
     */
    public static Observable<WebApi.Result<WebCommentResult>> getCommentJson(int sid, String token, String sn) {
        return sWebApi.getCommentJson(token, "1," + sid + "," + sn);
    }

    public static Observable<WebCaptcha> getCaptchaDataUrl(String token) {
        return sWebApi.getCaptchaDataUrl(token, System.currentTimeMillis());
    }

    public static Observable<WebApi.Result> addComment(String token, String content, String captcha, int sid) {
        return replyComment(token, content, captcha, sid, 0);
    }

    /**
     * 回复或添加评论
     *
     * @param token   comment json 中的 token
     * @param content 内容
     * @param captcha 验证码
     * @param sid     文章 id
     * @param pid     引用的评论的 id，如为新增评论，该值需为 0
     */
    public static Observable<WebApi.Result> replyComment(String token, String content, String captcha, int sid, int pid) {
        return sWebApi.addComment(token, "publish", content, captcha, sid, pid);
    }

    public static Observable<WebApi.Result> supportComment(String token, int sid, int tid) {
        return sWebApi.opForComment(token, "support", sid, tid);
    }

    public static Observable<WebApi.Result> againstComment(String token, int sid, int tid) {
        return sWebApi.opForComment(token, "against", sid, tid);
    }

    public static Observable<WebApi.Result<List<HotComment>>> getHotComments(String token, int page) {
        return sWebApi.getHotComments(page, token, System.currentTimeMillis());
    }

    public static Observable<ResponseBody> getHotCommentsByPage(int page) {
        return sMWebApi.getHotCommentsByPage(page);
    }
}
