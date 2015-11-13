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
}
