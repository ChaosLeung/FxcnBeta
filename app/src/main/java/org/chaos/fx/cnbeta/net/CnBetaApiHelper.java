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

import org.chaos.fx.cnbeta.net.model.ArticleSummary;
import org.chaos.fx.cnbeta.net.model.Comment;
import org.chaos.fx.cnbeta.net.model.HotComment;
import org.chaos.fx.cnbeta.net.model.NewsContent;
import org.chaos.fx.cnbeta.net.model.Topic;

import java.util.List;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * @author Chaos
 *         2015/11/03.
 */
public class CnBetaApiHelper {

    private static CnBetaApi sCnBetaApi;

    public static void initialize() {
        sCnBetaApi = new Retrofit.Builder()
                .baseUrl(CnBetaApi.BASE_URL)
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
                .build()
                .create(CnBetaApi.class);
    }

    public static Call<CnBetaApi.Result<List<ArticleSummary>>> articles() {
        long timestamp = System.currentTimeMillis();
        return sCnBetaApi.articles(timestamp, CnBetaSignUtil.articlesSign(timestamp));
    }

    public static Call<CnBetaApi.Result<List<ArticleSummary>>> topicArticles(String topicId) {
        long timestamp = System.currentTimeMillis();
        return sCnBetaApi.topicArticles(
                timestamp,
                CnBetaSignUtil.topicArticlesSign(timestamp, topicId),
                topicId);
    }

    public static Call<CnBetaApi.Result<List<ArticleSummary>>> newArticles(String topicId, int startSid) {
        long timestamp = System.currentTimeMillis();
        return sCnBetaApi.newArticles(
                timestamp,
                CnBetaSignUtil.newArticlesSign(timestamp, topicId, startSid),
                topicId,
                startSid);
    }

    public static Call<CnBetaApi.Result<List<ArticleSummary>>> oldArticles(String topicId,
                                                                           int endSid) {
        long timestamp = System.currentTimeMillis();
        return sCnBetaApi.oldArticles(
                timestamp,
                CnBetaSignUtil.oldArticlesSign(timestamp, topicId, endSid),
                topicId,
                endSid);
    }

    public static Call<CnBetaApi.Result<NewsContent>> articleContent(int sid) {
        long timestamp = System.currentTimeMillis();
        return sCnBetaApi.articleContent(
                timestamp,
                CnBetaSignUtil.articleContentSign(timestamp, sid),
                sid);
    }

    public static Call<CnBetaApi.Result<List<Comment>>> comments(int sid,
                                                                 int page) {
        long timestamp = System.currentTimeMillis();
        return sCnBetaApi.comments(
                timestamp,
                CnBetaSignUtil.commentsSign(timestamp, sid, page),
                sid,
                page);
    }

    public static Call<CnBetaApi.Result<Object>> addComment(int sid,
                                                            String content) {
        long timestamp = System.currentTimeMillis();
        return sCnBetaApi.addComment(
                timestamp,
                CnBetaSignUtil.addCommentSign(timestamp, sid, content),
                sid,
                content);
    }

    public static Call<CnBetaApi.Result<Object>> replyComment(int sid,
                                                              int pid,
                                                              String content) {
        long timestamp = System.currentTimeMillis();
        return sCnBetaApi.replyComment(
                timestamp,
                CnBetaSignUtil.replyCommentSign(timestamp, sid, pid, content),
                sid,
                pid,
                content);
    }

    public static Call<CnBetaApi.Result<String>> supportComment(int sid) {
        long timestamp = System.currentTimeMillis();
        return sCnBetaApi.supportComment(
                timestamp,
                CnBetaSignUtil.supportCommentSign(timestamp, sid),
                sid);
    }

    public static Call<CnBetaApi.Result<String>> againstComment(int sid) {
        long timestamp = System.currentTimeMillis();
        return sCnBetaApi.againstComment(
                timestamp,
                CnBetaSignUtil.againstCommentSign(timestamp, sid),
                sid);
    }

    public static Call<CnBetaApi.Result<List<HotComment>>> hotComment() {
        long timestamp = System.currentTimeMillis();
        return sCnBetaApi.hotComment(timestamp, CnBetaSignUtil.hotCommentSign(timestamp));
    }

    public static Call<CnBetaApi.Result<List<ArticleSummary>>> todayRank(@CnBetaApi.RankType String type) {
        long timestamp = System.currentTimeMillis();
        return sCnBetaApi.todayRank(
                timestamp,
                CnBetaSignUtil.todayRankSign(timestamp, type),
                type);
    }

    public static Call<CnBetaApi.Result<List<ArticleSummary>>> top10() {
        long timestamp = System.currentTimeMillis();
        return sCnBetaApi.top10(timestamp, CnBetaSignUtil.top10Sign(timestamp));
    }

    public static Call<CnBetaApi.Result<List<Topic>>> topics() {
        long timestamp = System.currentTimeMillis();
        return sCnBetaApi.topics(timestamp, CnBetaSignUtil.topicsSign(timestamp));
    }
}
