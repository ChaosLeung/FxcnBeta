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
public class Comment {

    @SerializedName("username")
    private String mUsername;
    @SerializedName("pid")
    private int mPid;
    @SerializedName("created_time")
    private String mCreatedTime;
    @SerializedName("support")
    private int mSupport;
    @SerializedName("against")
    private int mAgainst;
    @SerializedName("content")
    private String mContent;
    @SerializedName("tid")
    private int mTid;

    public void setUsername(String username) {
        mUsername = username;
    }

    public String getUsername() {
        return mUsername;
    }

    public void setPid(int pid) {
        mPid = pid;
    }

    public int getPid() {
        return mPid;
    }

    public void setCreatedTime(String createdTime) {
        mCreatedTime = createdTime;
    }

    public String getCreatedTime() {
        return mCreatedTime;
    }

    public void setSupport(int support) {
        mSupport = support;
    }

    public int getSupport() {
        return mSupport;
    }

    public void setAgainst(int against) {
        mAgainst = against;
    }

    public int getAgainst() {
        return mAgainst;
    }

    public void setContent(String content) {
        mContent = content;
    }

    public String getContent() {
        return mContent;
    }

    public void setTid(int tid) {
        mTid = tid;
    }

    public int getTid() {
        return mTid;
    }

    @Override
    public String toString() {
        return "username = " + mUsername +
                ", pid = " + mPid +
                ", createdTime = " + mCreatedTime +
                ", support = " + mSupport +
                ", against = " + mAgainst +
                ", content = " + mContent +
                ", tid = " + mTid;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof Comment) {
            Comment c = (Comment) o;
            return c.mTid == this.mTid;
        }
        return false;
    }
}
