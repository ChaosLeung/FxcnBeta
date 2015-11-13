package org.chaos.fx.cnbeta.net.model;

import com.google.gson.annotations.SerializedName;

/**
 * @author Chaos
 *         2015/11/03.
 */
public class Topic {

    private static final String FIELD_VALUE = "value";
    private static final String FIELD_TYPE = "type";
    private static final String FIELD_NAME = "name";

    @SerializedName(FIELD_VALUE)
    private int mValue;
    @SerializedName(FIELD_TYPE)
    private String mType;
    @SerializedName(FIELD_NAME)
    private String mName;

    public void setValue(int value) {
        mValue = value;
    }

    public int getValue() {
        return mValue;
    }

    public void setType(String type) {
        mType = type;
    }

    public String getType() {
        return mType;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getName() {
        return mName;
    }

    @Override
    public String toString() {
        return "value = " + mValue + ", type = " + mType + ", name = " + mName;
    }
}
