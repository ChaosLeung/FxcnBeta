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
