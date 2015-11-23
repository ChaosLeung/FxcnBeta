package org.chaos.fx.cnbeta.net.model;

import com.google.gson.annotations.SerializedName;

/**
 * @author Chaos
 *         2015/11/03.
 */
public class Comment {

    private static final String FIELD_USERNAME = "username";
    private static final String FIELD_PID = "pid";
    private static final String FIELD_CREATED_TIME = "created_time";
    private static final String FIELD_SUPPORT = "support";
    private static final String FIELD_AGAINST = "against";
    private static final String FIELD_CONTENT = "content";
    private static final String FIELD_TID = "tid";


    @SerializedName(FIELD_USERNAME)
    private String mUsername;
    @SerializedName(FIELD_PID)
    private int mPid;
    @SerializedName(FIELD_CREATED_TIME)
    private String mCreatedTime;
    @SerializedName(FIELD_SUPPORT)
    private int mSupport;
    @SerializedName(FIELD_AGAINST)
    private int mAgainst;
    @SerializedName(FIELD_CONTENT)
    private String mContent;
    @SerializedName(FIELD_TID)
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
            if (c.mTid == this.mTid) {
                return true;
            }
        }
        return false;
    }
}
