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

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Unique;

/**
 * @author Chaos
 *         2015/11/01.
 */
@Entity
public class ArticleSummary {

    @SerializedName("pubtime")
    private String publishTime;
    @SerializedName("thumb")
    private String thumb;
    @SerializedName("comments")
    private int comment;
    @Unique
    @SerializedName("sid")
    private int sid;
    @SerializedName("topic_logo")
    private String topicLogo;
    @SerializedName("summary")
    private String summary;
    @SerializedName("topic")
    private int topic;
    @SerializedName("title")
    private String title;
    @SerializedName("counter")
    private int counter;


    @Generated(hash = 1441905147)
    public ArticleSummary() {
    }

    @Generated(hash = 970982629)
    public ArticleSummary(String publishTime, String thumb, int comment, int sid,
            String topicLogo, String summary, int topic, String title,
            int counter) {
        this.publishTime = publishTime;
        this.thumb = thumb;
        this.comment = comment;
        this.sid = sid;
        this.topicLogo = topicLogo;
        this.summary = summary;
        this.topic = topic;
        this.title = title;
        this.counter = counter;
    }

    public String getPublishTime() {
        return publishTime;
    }

    public void setPublishTime(String publishTime) {
        this.publishTime = publishTime;
    }

    public String getThumb() {
        return thumb;
    }

    public void setThumb(String thumb) {
        this.thumb = thumb;
    }

    public int getComment() {
        return comment;
    }

    public void setComment(int comment) {
        this.comment = comment;
    }

    public int getSid() {
        return sid;
    }

    public void setSid(int sid) {
        this.sid = sid;
    }

    public String getTopicLogo() {
        return topicLogo;
    }

    public void setTopicLogo(String topicLogo) {
        this.topicLogo = topicLogo;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public int getTopic() {
        return topic;
    }

    public void setTopic(int topic) {
        this.topic = topic;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getCounter() {
        return counter;
    }

    public void setCounter(int counter) {
        this.counter = counter;
    }

    @Override
    public String toString() {
        return "ArticleSummary{" +
                "publishTime='" + publishTime + '\'' +
                ", thumb='" + thumb + '\'' +
                ", comment=" + comment +
                ", sid=" + sid +
                ", topicLogo='" + topicLogo + '\'' +
                ", summary='" + summary + '\'' +
                ", topic=" + topic +
                ", title='" + title + '\'' +
                ", counter=" + counter +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof ArticleSummary) {
            ArticleSummary a = (ArticleSummary) o;
            if (a.sid == this.sid) {
                return true;
            }
        }
        return false;
    }
}
