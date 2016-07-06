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

public class NewsContent {

    @SerializedName("hometext")
    private String mHomeText;
    @SerializedName("thumb")
    private String mThumb;
    @SerializedName("score")
    private String mScore;
    @SerializedName("comments")
    private int mCommentCount;
    @SerializedName("sid")
    private int mSid;
    @SerializedName("keywords")
    private String mKeyword;
    @SerializedName("aid")
    private String mAuthor;
    @SerializedName("source")
    private String mSource;
    @SerializedName("bodytext")
    private String mBodyText;
    @SerializedName("state")
    private int mStatus;
    @SerializedName("topic")
    private int mTopic;
    @SerializedName("title")
    private String mTitle;
    @SerializedName("counter")
    private int mCounter;
    @SerializedName("time")
    private String mTime;

    public void setHomeText(String homeText) {
        mHomeText = homeText;
    }

    public String getHomeText() {
        return mHomeText;
    }

    public void setThumb(String thumb) {
        mThumb = thumb;
    }

    public String getThumb() {
        return mThumb;
    }

    public void setScore(String score) {
        mScore = score;
    }

    public String getScore() {
        return mScore;
    }

    public void setCommentCount(int commentCount) {
        mCommentCount = commentCount;
    }

    public int getCommentCount() {
        return mCommentCount;
    }

    public void setSid(int sid) {
        mSid = sid;
    }

    public int getSid() {
        return mSid;
    }

    public void setKeyword(String keyword) {
        mKeyword = keyword;
    }

    public String getKeyword() {
        return mKeyword;
    }

    public void setAuthor(String author) {
        mAuthor = author;
    }

    public String getAuthor() {
        return mAuthor;
    }

    public void setSource(String source) {
        mSource = source;
    }

    public String getSource() {
        return mSource;
    }

    public void setBodyText(String bodyText) {
        mBodyText = bodyText;
    }

    public String getBodyText() {
        return mBodyText;
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

    public void setTime(String time) {
        mTime = time;
    }

    public String getTime() {
        return mTime;
    }

    @Override
    public String toString() {
        return "NewsContent{" +
                "mHomeText='" + mHomeText + '\'' +
                ", mThumb='" + mThumb + '\'' +
                ", mScore='" + mScore + '\'' +
                ", mCommentCount=" + mCommentCount +
                ", mSid=" + mSid +
                ", mKeyword='" + mKeyword + '\'' +
                ", mAuthor='" + mAuthor + '\'' +
                ", mSource='" + mSource + '\'' +
                ", mBodyText='" + mBodyText + '\'' +
                ", mStatus=" + mStatus +
                ", mTopic=" + mTopic +
                ", mTitle='" + mTitle + '\'' +
                ", mCounter=" + mCounter +
                ", mTime='" + mTime + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof NewsContent) {
            NewsContent n = (NewsContent) o;
            if (n.mSid == this.mSid) {
                return true;
            }
        }
        return false;
    }
}
