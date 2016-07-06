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

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * @author Chaos
 *         2015/11/01.
 */
public class ArticleSummary extends RealmObject {

    @SerializedName("pubtime")
    private String mPublishTime;
    @SerializedName("thumb")
    private String mThumb;
    @SerializedName("comments")
    private int mComment;
    @PrimaryKey
    @SerializedName("sid")
    private int mSid;
    @SerializedName("topic_logo")
    private String mTopicLogo;
    @SerializedName("summary")
    private String mSummary;
    @SerializedName("topic")
    private int mTopic;
    @SerializedName("title")
    private String mTitle;
    @SerializedName("counter")
    private int mCounter;

    public void setPublishTime(String publishTime) {
        mPublishTime = publishTime;
    }

    public String getPublishTime() {
        return mPublishTime;
    }

    public void setThumb(String thumb) {
        mThumb = thumb;
    }

    public String getThumb() {
        return mThumb;
    }

    public void setComment(int comment) {
        mComment = comment;
    }

    public int getComment() {
        return mComment;
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

    @Override
    public String toString() {
        return "ArticleSummary{" +
                "mPublishTime='" + mPublishTime + '\'' +
                ", mThumb='" + mThumb + '\'' +
                ", mComment=" + mComment +
                ", mSid=" + mSid +
                ", mTopicLogo='" + mTopicLogo + '\'' +
                ", mSummary='" + mSummary + '\'' +
                ", mTopic=" + mTopic +
                ", mTitle='" + mTitle + '\'' +
                ", mCounter=" + mCounter +
                '}';
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
