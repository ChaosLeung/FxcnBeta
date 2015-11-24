
package org.chaos.fx.cnbeta.net.model;

import com.google.gson.annotations.SerializedName;

public class NewsContent {

    private static final String FIELD_UPDATETIME = "updatetime";
    private static final String FIELD_ELITE = "elite";
    private static final String FIELD_HOMETEXT = "hometext";
    private static final String FIELD_THUMB = "thumb";
    private static final String FIELD_BRIEF = "brief";
    private static final String FIELD_SCORE = "score";
    private static final String FIELD_COMMENTS = "comments";
    private static final String FIELD_SID = "sid";
    private static final String FIELD_MVIEW = "mview";
    private static final String FIELD_RATINGS_STORY = "ratings_story";
    private static final String FIELD_KEYWORDS = "keywords";
    private static final String FIELD_STYLE = "style";
    private static final String FIELD_AID = "aid";
    private static final String FIELD_IFCOM = "ifcom";
    private static final String FIELD_SOURCE = "source";
    private static final String FIELD_GOOD = "good";
    private static final String FIELD_QUEUEID = "queueid";
    private static final String FIELD_COLLECTNUM = "collectnum";
    private static final String FIELD_RATINGS = "ratings";
    private static final String FIELD_ISHOME = "ishome";
    private static final String FIELD_BODYTEXT = "bodytext";
    private static final String FIELD_STATUS = "status";
    private static final String FIELD_BAD = "bad";
    private static final String FIELD_SCORE_STORY = "score_story";
    private static final String FIELD_LISTORDER = "listorder";
    private static final String FIELD_DATA_ID = "data_id";
    private static final String FIELD_POLLID = "pollid";
    private static final String FIELD_CATID = "catid";
    private static final String FIELD_TOPIC = "topic";
    private static final String FIELD_TITLE = "title";
    private static final String FIELD_INPUTTIME = "inputtime";
    private static final String FIELD_COUNTER = "counter";
    private static final String FIELD_SHORTTITLE = "shorttitle";
    private static final String FIELD_RELATION = "relation";
    private static final String FIELD_TIME = "time";
    private static final String FIELD_USER_ID = "user_id";


    @SerializedName(FIELD_UPDATETIME)
    private int mUpdatetime;
    @SerializedName(FIELD_ELITE)
    private int mElite;
    @SerializedName(FIELD_HOMETEXT)
    private String mHometext;
    @SerializedName(FIELD_THUMB)
    private String mThumb;
    @SerializedName(FIELD_BRIEF)
    private String mBrief;
    @SerializedName(FIELD_SCORE)
    private String mScore;
    @SerializedName(FIELD_COMMENTS)
    private int mComment;
    @SerializedName(FIELD_SID)
    private int mSid;
    @SerializedName(FIELD_MVIEW)
    private int mMview;
    @SerializedName(FIELD_RATINGS_STORY)
    private int mRatingsStory;
    @SerializedName(FIELD_KEYWORDS)
    private String mKeyword;
    @SerializedName(FIELD_STYLE)
    private String mStyle;
    @SerializedName(FIELD_AID)
    private String mAid;
    @SerializedName(FIELD_IFCOM)
    private int mIfcom;
    @SerializedName(FIELD_SOURCE)
    private String mSource;
    @SerializedName(FIELD_GOOD)
    private int mGood;
    @SerializedName(FIELD_QUEUEID)
    private int mQueueid;
    @SerializedName(FIELD_COLLECTNUM)
    private int mCollectnum;
    @SerializedName(FIELD_RATINGS)
    private int mRating;
    @SerializedName(FIELD_ISHOME)
    private int mIshome;
    @SerializedName(FIELD_BODYTEXT)
    private String mBodytext;
    @SerializedName(FIELD_STATUS)
    private int mStatus;
    @SerializedName(FIELD_BAD)
    private int mBad;
    @SerializedName(FIELD_SCORE_STORY)
    private String mScoreStory;
    @SerializedName(FIELD_LISTORDER)
    private int mListorder;
    @SerializedName(FIELD_DATA_ID)
    private int mDataId;
    @SerializedName(FIELD_POLLID)
    private int mPollid;
    @SerializedName(FIELD_CATID)
    private int mCatid;
    @SerializedName(FIELD_TOPIC)
    private int mTopic;
    @SerializedName(FIELD_TITLE)
    private String mTitle;
    @SerializedName(FIELD_INPUTTIME)
    private long mInputtime;
    @SerializedName(FIELD_COUNTER)
    private int mCounter;
    @SerializedName(FIELD_SHORTTITLE)
    private String mShorttitle;
    @SerializedName(FIELD_RELATION)
    private String mRelation;
    @SerializedName(FIELD_TIME)
    private String mTime;
    @SerializedName(FIELD_USER_ID)
    private int mUserId;

    public void setUpdatetime(int updatetime) {
        mUpdatetime = updatetime;
    }

    public int getUpdatetime() {
        return mUpdatetime;
    }

    public void setElite(int elite) {
        mElite = elite;
    }

    public int getElite() {
        return mElite;
    }

    public void setHometext(String hometext) {
        mHometext = hometext;
    }

    public String getHometext() {
        return mHometext;
    }

    public void setThumb(String thumb) {
        mThumb = thumb;
    }

    public String getThumb() {
        return mThumb;
    }

    public void setBrief(String brief) {
        mBrief = brief;
    }

    public String getBrief() {
        return mBrief;
    }

    public void setScore(String score) {
        mScore = score;
    }

    public String getScore() {
        return mScore;
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

    public void setMview(int mview) {
        mMview = mview;
    }

    public int getMview() {
        return mMview;
    }

    public void setRatingsStory(int ratingsStory) {
        mRatingsStory = ratingsStory;
    }

    public int getRatingsStory() {
        return mRatingsStory;
    }

    public void setKeyword(String keyword) {
        mKeyword = keyword;
    }

    public String getKeyword() {
        return mKeyword;
    }

    public void setStyle(String style) {
        mStyle = style;
    }

    public String getStyle() {
        return mStyle;
    }

    public void setAid(String aid) {
        mAid = aid;
    }

    public String getAid() {
        return mAid;
    }

    public void setIfcom(int ifcom) {
        mIfcom = ifcom;
    }

    public int getIfcom() {
        return mIfcom;
    }

    public void setSource(String source) {
        mSource = source;
    }

    public String getSource() {
        return mSource;
    }

    public void setGood(int good) {
        mGood = good;
    }

    public int getGood() {
        return mGood;
    }

    public void setQueueid(int queueid) {
        mQueueid = queueid;
    }

    public int getQueueid() {
        return mQueueid;
    }

    public void setCollectnum(int collectnum) {
        mCollectnum = collectnum;
    }

    public int getCollectnum() {
        return mCollectnum;
    }

    public void setRating(int rating) {
        mRating = rating;
    }

    public int getRating() {
        return mRating;
    }

    public void setIshome(int ishome) {
        mIshome = ishome;
    }

    public int getIshome() {
        return mIshome;
    }

    public void setBodytext(String bodytext) {
        mBodytext = bodytext;
    }

    public String getBodytext() {
        return mBodytext;
    }

    public void setStatus(int status) {
        mStatus = status;
    }

    public int getStatus() {
        return mStatus;
    }

    public void setBad(int bad) {
        mBad = bad;
    }

    public int getBad() {
        return mBad;
    }

    public void setScoreStory(String scoreStory) {
        mScoreStory = scoreStory;
    }

    public String getScoreStory() {
        return mScoreStory;
    }

    public void setListorder(int listorder) {
        mListorder = listorder;
    }

    public int getListorder() {
        return mListorder;
    }

    public void setDataId(int dataId) {
        mDataId = dataId;
    }

    public int getDataId() {
        return mDataId;
    }

    public void setPollid(int pollid) {
        mPollid = pollid;
    }

    public int getPollid() {
        return mPollid;
    }

    public void setCatid(int catid) {
        mCatid = catid;
    }

    public int getCatid() {
        return mCatid;
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

    public void setInputtime(long inputtime) {
        mInputtime = inputtime;
    }

    public long getInputtime() {
        return mInputtime;
    }

    public void setCounter(int counter) {
        mCounter = counter;
    }

    public int getCounter() {
        return mCounter;
    }

    public void setShorttitle(String shorttitle) {
        mShorttitle = shorttitle;
    }

    public String getShorttitle() {
        return mShorttitle;
    }

    public void setRelation(String relation) {
        mRelation = relation;
    }

    public String getRelation() {
        return mRelation;
    }

    public void setTime(String time) {
        mTime = time;
    }

    public String getTime() {
        return mTime;
    }

    public void setUserId(int userId) {
        mUserId = userId;
    }

    public int getUserId() {
        return mUserId;
    }

    @Override
    public String toString() {
        return "updatetime = " + mUpdatetime +
                ", elite = " + mElite +
                ", hometext = " + mHometext +
                ", thumb = " + mThumb +
                ", brief = " + mBrief +
                ", score = " + mScore +
                ", comment = " + mComment +
                ", sid = " + mSid +
                ", mview = " + mMview +
                ", ratingsStory = " + mRatingsStory +
                ", keyword = " + mKeyword +
                ", style = " + mStyle +
                ", aid = " + mAid +
                ", ifcom = " + mIfcom +
                ", source = " + mSource +
                ", good = " + mGood +
                ", queueid = " + mQueueid +
                ", collectnum = " + mCollectnum +
                ", rating = " + mRating +
                ", ishome = " + mIshome +
                ", bodytext = " + mBodytext +
                ", status = " + mStatus +
                ", bad = " + mBad +
                ", scoreStory = " + mScoreStory +
                ", listorder = " + mListorder +
                ", dataId = " + mDataId +
                ", pollid = " + mPollid +
                ", catid = " + mCatid +
                ", topic = " + mTopic +
                ", title = " + mTitle +
                ", inputtime = " + mInputtime +
                ", counter = " + mCounter +
                ", shorttitle = " + mShorttitle +
                ", relation = " + mRelation +
                ", time = " + mTime +
                ", userId = " + mUserId;
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
