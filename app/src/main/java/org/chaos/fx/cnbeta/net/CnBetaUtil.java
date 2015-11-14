package org.chaos.fx.cnbeta.net;

import org.chaos.fx.cnbeta.net.model.ArticleSummary;
import org.chaos.fx.cnbeta.net.model.Comment;
import org.chaos.fx.cnbeta.net.model.HotComment;
import org.chaos.fx.cnbeta.net.model.NewsContent;
import org.chaos.fx.cnbeta.net.model.Topic;

import java.util.List;

import retrofit.Call;

/**
 * @author Chaos
 *         2015/11/03.
 */
public class CnBetaUtil {

    public static String getTypeString(int type) {
        return type == CnBetaApi.TYPE_COMMENTS ? "comments" : type == CnBetaApi.TYPE_COUNTER ? "counter" : "dig";
    }

    public static Call<CnBetaApi.Result<List<ArticleSummary>>> articles(CnBetaApi api) {
        long timestamp = System.currentTimeMillis();
        return api.articles(timestamp, CnBetaSignUtil.articlesSign(timestamp));
    }

    public static Call<CnBetaApi.Result<List<ArticleSummary>>> topicArticles(CnBetaApi api, String topicId) {
        long timestamp = System.currentTimeMillis();
        return api.topicArticles(
                timestamp,
                CnBetaSignUtil.topicArticlesSign(timestamp, topicId),
                topicId);
    }

    public static Call<CnBetaApi.Result<List<ArticleSummary>>> newArticles(CnBetaApi api, String topicId, int startSid) {
        long timestamp = System.currentTimeMillis();
        return api.newArticles(
                timestamp,
                CnBetaSignUtil.newArticlesSign(timestamp, topicId, startSid),
                topicId,
                startSid);
    }

    public static Call<CnBetaApi.Result<List<ArticleSummary>>> oldArticles(CnBetaApi api,
                                                                           String topicId,
                                                                           int endSid) {
        long timestamp = System.currentTimeMillis();
        return api.oldArticles(
                timestamp,
                CnBetaSignUtil.oldArticlesSign(timestamp, topicId, endSid),
                topicId,
                endSid);
    }

    public static Call<CnBetaApi.Result<NewsContent>> articleContent(CnBetaApi api,
                                                                     int sid) {
        long timestamp = System.currentTimeMillis();
        return api.articleContent(
                timestamp,
                CnBetaSignUtil.articleContentSign(timestamp, sid),
                sid);
    }

    public static Call<CnBetaApi.Result<List<Comment>>> comments(CnBetaApi api,
                                                                 int sid,
                                                                 int page) {
        long timestamp = System.currentTimeMillis();
        return api.comments(
                timestamp,
                CnBetaSignUtil.commentsSign(timestamp, sid, page),
                sid,
                page);
    }

    public static Call<CnBetaApi.Result<Object>> addComment(CnBetaApi api,
                                                            int sid,
                                                            String content) {
        long timestamp = System.currentTimeMillis();
        return api.addComment(
                timestamp,
                CnBetaSignUtil.addCommentSign(timestamp, sid, content),
                sid,
                content);
    }

    public static Call<CnBetaApi.Result<Object>> replyComment(CnBetaApi api,
                                                              int sid,
                                                              int pid,
                                                              String content) {
        long timestamp = System.currentTimeMillis();
        return api.replyComment(
                timestamp,
                CnBetaSignUtil.replyCommentSign(timestamp, sid, pid, content),
                sid,
                pid,
                content);
    }

    public static Call<CnBetaApi.Result<String>> supportComment(CnBetaApi api,
                                                                int sid) {
        long timestamp = System.currentTimeMillis();
        return api.supportComment(
                timestamp,
                CnBetaSignUtil.supportCommentSign(timestamp, sid),
                sid);
    }

    public static Call<CnBetaApi.Result<String>> againstComment(CnBetaApi api,
                                                                int sid) {
        long timestamp = System.currentTimeMillis();
        return api.againstComment(
                timestamp,
                CnBetaSignUtil.againstCommentSign(timestamp, sid),
                sid);
    }

    public static Call<CnBetaApi.Result<List<HotComment>>> hotComment(CnBetaApi api) {
        long timestamp = System.currentTimeMillis();
        return api.hotComment(timestamp, CnBetaSignUtil.hotCommentSign(timestamp));
    }

    public static Call<CnBetaApi.Result<List<ArticleSummary>>> todayRank(CnBetaApi api,
                                                                         String type) {
        long timestamp = System.currentTimeMillis();
        return api.todayRank(
                timestamp,
                CnBetaSignUtil.todayRankSign(timestamp, type),
                type);
    }

    public static Call<CnBetaApi.Result<List<ArticleSummary>>> top10(CnBetaApi api) {
        long timestamp = System.currentTimeMillis();
        return api.top10(timestamp, CnBetaSignUtil.top10Sign(timestamp));
    }

    public static Call<CnBetaApi.Result<List<Topic>>> topics(CnBetaApi api) {
        long timestamp = System.currentTimeMillis();
        return api.topics(timestamp, CnBetaSignUtil.topicsSign(timestamp));
    }
}
