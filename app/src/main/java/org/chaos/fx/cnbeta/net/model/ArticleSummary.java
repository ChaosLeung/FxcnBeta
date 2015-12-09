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

package org.chaos.fx.cnbeta.net.model;

import com.google.gson.annotations.SerializedName;

/**
 * @author Chaos
 *         2015/11/01.
 */
public class ArticleSummary {

    private static final String FIELD_PUBTIME = "pubtime";
    private static final String FIELD_THUMB = "thumb";
    private static final String FIELD_SCORE = "score";
    private static final String FIELD_COMMENTS = "comments";
    private static final String FIELD_SCORE_STORY = "score_story";
    private static final String FIELD_SID = "sid";
    private static final String FIELD_TOPIC_LOGO = "topic_logo";
    private static final String FIELD_RATINGS_STORY = "ratings_story";
    private static final String FIELD_SUMMARY = "summary";
    private static final String FIELD_TOPIC = "topic";
    private static final String FIELD_TITLE = "title";
    private static final String FIELD_COUNTER = "counter";
    private static final String FIELD_RATINGS = "ratings";

    @SerializedName(FIELD_PUBTIME)
    private String mPubtime;
    @SerializedName(FIELD_THUMB)
    private String mThumb;
    @SerializedName(FIELD_SCORE)
    private int mScore;
    @SerializedName(FIELD_COMMENTS)
    private int mComment;
    @SerializedName(FIELD_SCORE_STORY)
    private int mScoreStory;
    @SerializedName(FIELD_SID)
    private int mSid;
    @SerializedName(FIELD_TOPIC_LOGO)
    private String mTopicLogo;
    @SerializedName(FIELD_RATINGS_STORY)
    private int mRatingsStory;
    @SerializedName(FIELD_SUMMARY)
    private String mSummary;
    @SerializedName(FIELD_TOPIC)
    private int mTopic;
    @SerializedName(FIELD_TITLE)
    private String mTitle;
    @SerializedName(FIELD_COUNTER)
    private int mCounter;
    @SerializedName(FIELD_RATINGS)
    private int mRating;

    public void setPubtime(String pubtime) {
        mPubtime = pubtime;
    }

    public String getPubtime() {
        return mPubtime;
    }

    public void setThumb(String thumb) {
        mThumb = thumb;
    }

    public String getThumb() {
        return mThumb;
    }

    public void setScore(int score) {
        mScore = score;
    }

    public int getScore() {
        return mScore;
    }

    public void setComment(int comment) {
        mComment = comment;
    }

    public int getComment() {
        return mComment;
    }

    public void setScoreStory(int scoreStory) {
        mScoreStory = scoreStory;
    }

    public int getScoreStory() {
        return mScoreStory;
    }

    public void setSid(int sid) {
        mSid = sid;
    }

    public int getSid() {
        return mSid;
    }

    public void setTopicLogo(String topicLogo) {
        mTopicLogo = topicLogo;
    }

    public String getTopicLogo() {
        return mTopicLogo;
    }

    public void setRatingsStory(int ratingsStory) {
        mRatingsStory = ratingsStory;
    }

    public int getRatingsStory() {
        return mRatingsStory;
    }

    public void setSummary(String summary) {
        mSummary = summary;
    }

    public String getSummary() {
        return mSummary;
    }

    public void setTopic(int topic) {
        mTopic = topic;
    }

    public int getTopic() {
        return mTopic;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setCounter(int counter) {
        mCounter = counter;
    }

    public int getCounter() {
        return mCounter;
    }

    public void setRating(int rating) {
        mRating = rating;
    }

    public int getRating() {
        return mRating;
    }

    @Override
    public String toString() {
        return "pubtime = " + mPubtime +
                ", thumb = " + mThumb +
                ", score = " + mScore +
                ", comment = " + mComment +
                ", scoreStory = " + mScoreStory +
                ", sid = " + mSid +
                ", topicLogo = " + mTopicLogo +
                ", ratingsStory = " + mRatingsStory +
                ", summary = " + mSummary +
                ", topic = " + mTopic +
                ", title = " + mTitle +
                ", counter = " + mCounter +
                ", rating = " + mRating;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof ArticleSummary) {
            ArticleSummary a = (ArticleSummary) o;
            if (a.mSid == this.mSid) {
                return true;
            }
        }
        return false;
    }
}
