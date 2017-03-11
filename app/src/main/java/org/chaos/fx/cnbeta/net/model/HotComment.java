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

    @SerializedName("username")
    private String mUsername;
    @SerializedName("subject")
    private String mTitle;
    @SerializedName("comment")
    private String mComment;
    @SerializedName("sid")
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
