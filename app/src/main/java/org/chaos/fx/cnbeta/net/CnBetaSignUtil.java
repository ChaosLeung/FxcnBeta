package org.chaos.fx.cnbeta.net;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * CB 反编译出来的代码
 *
 * @author Chaos
 *         2015/11/01.
 */
public class CnBetaSignUtil {

    private static final char[] factors = "0123456789ABCDEF".toCharArray();

    public static String articlesSign(long timestamp) {
        return generateDigestStr("app_key=10000&format=json&method=Article.Lists&timestamp=" +
                timestamp + "&v=1.0&mpuffgvbvbttn3Rc");
    }

    public static String topicArticlesSign(long timestamp, String topicId) {
        return generateDigestStr("app_key=10000&format=json&method=Article.Lists&timestamp=" +
                timestamp + "&topicid=" + topicId + "&v=1.0&mpuffgvbvbttn3Rc");
    }

    public static String newArticlesSign(long timestamp, String topicId, int startSid) {
        return generateDigestStr("app_key=10000&format=json&method=Article.Lists&start_sid=" +
                startSid + "&timestamp=" + timestamp +
                "&topicid=" + topicId + "&v=1.0&mpuffgvbvbttn3Rc");
    }

    public static String oldArticlesSign(long timestamp, String topicId, int endSid) {
        return generateDigestStr("app_key=10000&end_sid=" + endSid +
                "&format=json&method=Article.Lists&timestamp=" + timestamp +
                "&topicid=" + topicId + "&v=1.0&mpuffgvbvbttn3Rc");
    }

    public static String articleContentSign(long timestamp, int sid) {
        return generateDigestStr("app_key=10000&format=json&method=Article.NewsContent&sid=" + sid +
                "&timestamp=" + timestamp + "&v=1.0&mpuffgvbvbttn3Rc");
    }

    /**
     * 获取评论列表 api 的 sign
     *
     * @param timestamp 时间戳
     * @param sid       文章 id
     * @param page      评论分页，一页20条
     * @return 加密字符串
     */
    public static String commentsSign(long timestamp, int sid, int page) {
        return generateDigestStr("app_key=10000&format=json&method=Article.Comment&page=" + page +
                "&pageSize=20&sid=" + sid + "&timestamp=" + timestamp + "&v=1.0&mpuffgvbvbttn3Rc");
    }

    public static String addCommentSign(long timestamp, int sid, String content) {
        return generateDigestStr("app_key=10000&content=" + content +
                "&format=json&method=Article.DoCmt&op=publish&sid=" + sid +
                "&timestamp=" + timestamp + "&v=1.0&mpuffgvbvbttn3Rc");
    }

    public static String replyCommentSign(long timestamp, int sid, int pid, String content) {
        return generateDigestStr("app_key=10000&content=" + content +
                "&format=json&method=Article.DoCmt&op=publish&pid=" + pid +
                "&sid=" + sid + "&timestamp=" + timestamp + "&v=1.0&mpuffgvbvbttn3Rc");
    }

    public static String supportCommentSign(long timestamp, int sid) {
        return generateDigestStr(
                "app_key=10000&format=json&method=Article.DoCmt&op=support&sid=" + sid +
                        "&tid=1&timestamp=" + timestamp + "&v=1.0&mpuffgvbvbttn3Rc");
    }

    public static String againstCommentSign(long timestamp, int sid) {
        return generateDigestStr(
                "app_key=10000&format=json&method=Article.DoCmt&op=against&sid=" + sid +
                        "&tid=0&timestamp=" + timestamp + "&v=1.0&mpuffgvbvbttn3Rc");
    }

    public static String hotCommentSign(long timestamp) {
        return generateDigestStr(
                "app_key=10000&format=json&method=Article.RecommendComment&timestamp=" + timestamp +
                        "&v=1.0&mpuffgvbvbttn3Rc");
    }

    public static String todayRankSign(long timestamp, String type) {
        return generateDigestStr(
                "app_key=10000&format=json&method=Article.TodayRank&timestamp=" + timestamp +
                        "&type=" + type + "&v=1.0&mpuffgvbvbttn3Rc");
    }

    public static String top10Sign(long timestamp) {
        return generateDigestStr(
                "app_key=10000&format=json&method=Article.Top10&timestamp=" + timestamp +
                        "&v=1.0&mpuffgvbvbttn3Rc");
    }

    public static String topicsSign(long timestamp) {
        return generateDigestStr(
                "app_key=10000&format=json&method=Article.NavList&timestamp=" + timestamp +
                        "&v=1.0&mpuffgvbvbttn3Rc");
    }

    public static String generateDigestStr(String signStr) {
        String digestStr;
        try {
            MessageDigest md5Digest = MessageDigest.getInstance("MD5");
            md5Digest.update(signStr.getBytes());
            digestStr = generateSignStr(md5Digest.digest()).toLowerCase();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return "";
        }
        return digestStr;
    }

    public static String generateSignStr(byte[] digest) {
        StringBuilder sb = new StringBuilder(2 * digest.length);
        for (byte b : digest) {
            sb.append(factors[(0xf0 & b) >>> 4]);
            sb.append(factors[0xf & b]);
        }
        return sb.toString();
    }
}
