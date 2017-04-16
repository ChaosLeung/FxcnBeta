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
 *         2015/11/03.
 */
public class HotComment {

    /**
     * Mobile Client API
     *
     * cid : 101847
     * comment : 不用说不定 小县城里这种店的老板都是同一个
     * sid : 0
     * username : 匿名人士
     * subject : 即将消失的“外籍车” 正在回归的“打车难”
     *
     *
     * Web API
     *
     * sign : array
     * data_id : 102057
     * datafrom : article
     * from_id : 598011
     * title : <a href="http://www.cnbeta.com/articles/tech/598011.htm" target="_blank">压制一个能管得了的滴滴的结果就是路边多了很多管不了的黑车。</a>
     * description : 来自<strong>北京</strong>的匿名人士对新闻:<a href="http://www.cnbeta.com/articles/tech/598011.htm" target="_blank">即将消失的“外籍车” 正在回归的“打车难”</a>的评论
     * link : http://www.cnbeta.com/articles/tech/598011.htm
     * thumb :
     * relation : []
     * relation_link : []
     * relation_thumb : []
     * relation_desc : []
     * label : {"name":"科技","class":"tech"}
     */

    @SerializedName("username")
    private String mUsername;
    @SerializedName(value = "subject", alternate = {"description"})
    private String mTitle;
    @SerializedName(value = "comment", alternate = {"title"})
    private String mComment;
    @SerializedName(value = "sid", alternate = {"from_id"})
    private int mSid;

    public void setUsername(String username) {
        mUsername = username;
    }

    public String getUsername() {
        return mUsername;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setComment(String comment) {
        mComment = comment;
    }

    public String getComment() {
        return mComment;
    }

    public void setSid(int sid) {
        mSid = sid;
    }

    public int getSid() {
        return mSid;
    }

    @Override
    public String toString() {
        return "username = " + mUsername +
                ", subject = " + mTitle +
                ", comment = " + mComment +
                ", sid = " + mSid;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof HotComment) {
            HotComment h = (HotComment) o;
            if (h.mComment.equals(this.mComment)) {
                return true;
            }
        }
        return false;
    }
}
