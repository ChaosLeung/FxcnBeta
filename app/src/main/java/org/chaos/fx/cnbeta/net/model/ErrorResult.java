package org.chaos.fx.cnbeta.net.model;

import com.google.gson.annotations.SerializedName;

/**
 * @author Chaos
 *         2015/11/05.
 */
public class ErrorResult {

    private static final String FIELD_ERROR_CODE = "error_code";
    private static final String FIELD_ERROR_MSG = "error_msg";

    @SerializedName(FIELD_ERROR_CODE)
    private int mErrorCode;
    @SerializedName(FIELD_ERROR_MSG)
    private String mErrorMsg;

    @Override
    public String toString() {
        return "errorCode = " + mErrorMsg + ", errorMsg = " + mErrorMsg;
    }
}
