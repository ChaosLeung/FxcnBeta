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

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * @author Chaos
 *         2015/11/03.
 */
public class Comment implements Parcelable {

    /**
     * Closed comment
     * name :
     * comment : 你偶尔买几个游戏就是死宅 多买点建个房子放就是收藏家
     * date : 2017-01-31 18:17:55
     * tid : 13906221
     * support : 92
     * against : 2
     */

    @SerializedName(value = "username", alternate = {"name"})
    private String mUsername;
    @SerializedName("pid")
    private int mPid;
    @SerializedName(value = "created_time", alternate = {"date"})
    private String mCreatedTime;
    @SerializedName(value = "support", alternate = {"score"})
    private int mSupport;
    @SerializedName(value = "against", alternate = {"reason"})
    private int mAgainst;
    @SerializedName(value = "content", alternate = {"comment"})
    private String mContent;
    @SerializedName("tid")
    private int mTid;
    @SerializedName("host_name")
    private String mAddress;

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

    public String getAddress() {
        return mAddress;
    }

    public void setAddress(String address) {
        mAddress = address;
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mUsername);
        dest.writeInt(this.mPid);
        dest.writeString(this.mCreatedTime);
        dest.writeInt(this.mSupport);
        dest.writeInt(this.mAgainst);
        dest.writeString(this.mContent);
        dest.writeInt(this.mTid);
        dest.writeString(this.mAddress);
    }

    public Comment() {
    }

    protected Comment(Parcel in) {
        this.mUsername = in.readString();
        this.mPid = in.readInt();
        this.mCreatedTime = in.readString();
        this.mSupport = in.readInt();
        this.mAgainst = in.readInt();
        this.mContent = in.readString();
        this.mTid = in.readInt();
        this.mAddress = in.readString();
    }

    public static final Creator<Comment> CREATOR = new Creator<Comment>() {
        @Override
        public Comment createFromParcel(Parcel source) {
            return new Comment(source);
        }

        @Override
        public Comment[] newArray(int size) {
            return new Comment[size];
        }
    };
}
