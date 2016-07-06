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

import android.support.annotation.StringDef;

import com.google.gson.annotations.SerializedName;

import org.chaos.fx.cnbeta.net.model.ArticleSummary;
import org.chaos.fx.cnbeta.net.model.ClosedComment;
import org.chaos.fx.cnbeta.net.model.Comment;
import org.chaos.fx.cnbeta.net.model.HotComment;
import org.chaos.fx.cnbeta.net.model.NewsContent;
import org.chaos.fx.cnbeta.net.model.Topic;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.List;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;


/**
 * @author Chaos
 *         2015/11/01.
 */
public interface CnBetaApi {

    String BASE_URL = "http://api.cnbeta.com";
    String BASE_PARAMS = "/capi?app_key=10000&format=json&v=1.0&mpuffgvbvbttn3Rc&method=";

    /**
     * 评论最热
     */
    String TYPE_COMMENTS = "comments";

    /**
     * 阅读最多
     */
    String TYPE_COUNTER = "counter";

    /**
     * 最高推荐
     */
    String TYPE_DIG = "dig";

    @StringDef({TYPE_COMMENTS, TYPE_COUNTER, TYPE_DIG})
    @Retention(RetentionPolicy.SOURCE)
    @interface RankType {
    }

    /**
     * 文章列表 api，一次返回 20 条
     *
     * @param timestamp 时间戳
     * @param sign      规定顺序的字符串加密后的结果，参考 {@link CnBetaSignUtil#articlesSign(long)}
     * @return 成功则返回 state 字符串以及文章简略数据，失败直接崩溃 _(:зゝ∠)_
     */
    @GET(BASE_PARAMS + "Article.Lists")
    Observable<Result<List<ArticleSummary>>> articles(@Query("timestamp") long timestamp,
                                                      @Query("sign") String sign);

    /**
     * 话题相关文章
     *
     * @param timestamp 时间戳
     * @param sign      加密字符串
     * @param topicId   话题 id
     * @return 文章列表
     */
    @GET(BASE_PARAMS + "Article.Lists")
    Observable<Result<List<ArticleSummary>>> topicArticles(@Query("timestamp") long timestamp,
                                                           @Query("sign") String sign,
                                                           @Query("topicid") String topicId);

    /**
     * 最新文章
     *
     * @param timestamp 时间戳
     * @param sign      加密字符串
     * @param topicId   话题 id，无 id 时直接填 null
     * @param startSid  已加载的最新的文章的 id
     * @return 文章列表
     */
    @GET(BASE_PARAMS + "Article.Lists")
    Observable<Result<List<ArticleSummary>>> newArticles(@Query("timestamp") long timestamp,
                                                         @Query("sign") String sign,
                                                         @Query("topicid") String topicId,
                                                         @Query("start_sid") int startSid);

    /**
     * 最新文章
     *
     * @param timestamp 时间戳
     * @param sign      加密字符串
     * @param topicId   话题 id，无 id 时直接填 null
     * @param endSid    已加载的最旧的文章的 id
     * @return 文章列表
     */
    @GET(BASE_PARAMS + "Article.Lists")
    Observable<Result<List<ArticleSummary>>> oldArticles(@Query("timestamp") long timestamp,
                                                         @Query("sign") String sign,
                                                         @Query("topicid") String topicId,
                                                         @Query("end_sid") int endSid);

    /**
     * 文章详情
     *
     * @param timestamp 时间戳
     * @param sign      加密字符串
     * @param sid       文章 id
     * @return 成功则返回文章详细数据
     */
    @GET(BASE_PARAMS + "Article.NewsContent")
    Observable<Result<NewsContent>> articleContent(@Query("timestamp") long timestamp,
                                                   @Query("sign") String sign,
                                                   @Query("sid") int sid);

    /**
     * 文章评论, 一次最多只能请求10条, 返回的数据顺序为从旧到新
     *
     * @param timestamp 时间戳
     * @param sign      加密字符串
     * @param sid       文章 id
     * @param page      页数
     * @return 成功则返回详细评论数据 (包含点赞/反对数量)
     */
    @GET(BASE_PARAMS + "Article.Comment&pageSize=10")
    Observable<Result<List<Comment>>> comments(@Query("timestamp") long timestamp,
                                               @Query("sign") String sign,
                                               @Query("sid") int sid,
                                               @Query("page") int page);

    /**
     * 获取已关闭评论的文章的评论列表 (只有简单数据, 不包含评论之间的关系, 即无回复概念)
     *
     * @param timestamp 时间戳
     * @param sign      加密字符串
     * @return 成功则返回评论数据
     */
    @GET(BASE_PARAMS + "phone.Comment")
    Observable<List<ClosedComment>> closedComments(@Query("timestamp") long timestamp,
                                                   @Query("sign") String sign,
                                                   @Query("article") int article);

    /**
     * 评论
     *
     * @param timestamp 时间戳
     * @param sign      加密字符串
     * @param sid       文章 id
     * @param content   评论内容
     * @return FIXME 未知，目前一直返回评论参数错误（官方也这尿性……）
     */
    @GET(BASE_PARAMS + "Article.DoCmt&op=publish")
    Observable<Result<Object>> addComment(@Query("timestamp") long timestamp,
                                          @Query("sign") String sign,
                                          @Query("sid") int sid,
                                          @Query("content") String content);

    /**
     * 回复评论 (目前一直返回评论参数错误)
     *
     * @param timestamp 时间戳
     * @param sign      加密字符串
     * @param sid       文章 id
     * @param pid       对应评论的 id
     * @param content   回复内容
     * @return 操作状态描述
     */
    @Deprecated
    @GET(BASE_PARAMS + "Article.DoCmt&op=publish")
    Observable<Result<Object>> replyComment(@Query("timestamp") long timestamp,
                                            @Query("sign") String sign,
                                            @Query("sid") int sid,
                                            @Query("pid") int pid,
                                            @Query("content") String content);

    /**
     * 支持评论 (暂不能用, 虽然返回成功, 但是无效)
     *
     * @param timestamp 时间戳
     * @param sign      加密串
     * @param tid       评论 id
     * @return 操作状态描述
     */
    @Deprecated
    @GET(BASE_PARAMS + "Article.DoCmt&op=support&tid=1")
    Observable<Result<String>> supportComment(@Query("timestamp") long timestamp,
                                              @Query("sign") String sign,
                                              @Query("sid") int tid);

    /**
     * 反对评论 (暂不能用, 虽然返回成功, 但是无效)
     *
     * @param timestamp 时间戳
     * @param sign      加密串
     * @param tid       评论 id
     * @return 操作状态描述
     */
    @Deprecated
    @GET(BASE_PARAMS + "Article.DoCmt&op=against&tid=0")
    Observable<Result<String>> againstComment(@Query("timestamp") long timestamp,
                                              @Query("sign") String sign,
                                              @Query("sid") int tid);

    /**
     * 热门评论
     *
     * @param timestamp 时间戳
     * @param sign      加密字符串
     * @return 热门评论列表
     */
    @GET(BASE_PARAMS + "Article.RecommendComment")
    Observable<Result<List<HotComment>>> hotComment(@Query("timestamp") long timestamp,
                                                    @Query("sign") String sign);

    /**
     * 今日排行
     *
     * @param timestamp 时间戳
     * @param sign      加密字符串
     * @param type      排行类型
     * @return 文章列表
     */
    @GET(BASE_PARAMS + "Article.TodayRank")
    Observable<Result<List<ArticleSummary>>> todayRank(@Query("timestamp") long timestamp,
                                                       @Query("sign") String sign,
                                                       @Query("type") String type);

    /**
     * 本月 Top 10
     *
     * @param timestamp 时间戳
     * @param sign      加密字符串
     * @return 成功则返回本月热度最高的10篇文章
     */
    @GET(BASE_PARAMS + "Article.Top10")
    Observable<Result<List<ArticleSummary>>> top10(@Query("timestamp") long timestamp,
                                                   @Query("sign") String sign);

    /**
     * 文章主题
     *
     * @param timestamp 时间戳
     * @param sign      加密字符串
     * @return 成功则返回文章主题列表
     */
    @GET(BASE_PARAMS + "Article.NavList")
    Observable<Result<List<Topic>>> topics(@Query("timestamp") long timestamp,
                                           @Query("sign") String sign);


    class Result<T> {

        private static final String FIELD_STATUS = "status";
        private static final String FIELD_RESULT = "result";

        @SerializedName(FIELD_STATUS)
        public String status;
        @SerializedName(FIELD_RESULT)
        public T result;

        public boolean isSuccess() {
            return "success".equals(status);
        }
    }
}