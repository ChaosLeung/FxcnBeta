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

    private static final String FIELD_USERNAME = "username";
    private static final String FIELD_SUBJECT = "subject";
    private static final String FIELD_CID = "cid";
    private static final String FIELD_COMMENT = "comment";
    private static final String FIELD_SID = "sid";

    @SerializedName(FIELD_USERNAME)
    private String mUsername;
    @SerializedName(FIELD_SUBJECT)
    private String mSubject;
    @SerializedName(FIELD_CID)
    private int mCid;
    @SerializedName(FIELD_COMMENT)
    private String mComment;
    @SerializedName(FIELD_SID)
    private int mSid;

    public void setUsername(String username) {
        mUsername = username;
    }

    public String getUsername() {
        return mUsername;
    }

    public void setSubject(String subject) {
        mSubject = subject;
    }

    public String getSubject() {
        return mSubject;
    }

    public void setCid(int cid) {
        mCid = cid;
    }

    public int getCid() {
        return mCid;
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
                ", subject = " + mSubject +
                ", cid = " + mCid +
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
            if (h.mCid == this.mCid) {
                return true;
            }
        }
        return false;
    }
}
