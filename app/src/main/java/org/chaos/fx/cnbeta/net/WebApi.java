/*
 * Copyright 2016 Chaos
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

package org.chaos.fx.cnbeta.net;

import com.google.gson.annotations.SerializedName;

import org.chaos.fx.cnbeta.net.model.WebCaptcha;
import org.chaos.fx.cnbeta.net.model.WebCommentResult;

import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * @author Chaos
 *         3/30/16
 */
public interface WebApi {
    String HOST_URL = "http://www.cnbeta.com";
    String HOT_HOST_URL = "http://hot.cnbeta.com";
    String COMMENT = "/comment";
    String COMMENT_BASE_URL = HOST_URL + COMMENT;
    String COMMENT_JSON_URL = COMMENT_BASE_URL + "/read";
    String CAPTCHA_BASE_URL = COMMENT_BASE_URL + "/captcha";
    String CAPTCHA_URL = CAPTCHA_BASE_URL + "?refresh=1";

    @GET("/articles/{sid}.htm")
    Observable<ResponseBody> getArticleHtml(@Path("sid") int sid);

    @GET(CAPTCHA_URL)
    Observable<WebCaptcha> getCaptchaDataUrl(@Query("_csrf") String token,
                                             @Query("_") long timestamp);

    @GET(COMMENT_JSON_URL)
    Observable<Result<WebCommentResult>> getCommentJson(@Query("_csrf") String token,
                                                        @Query("op") String op);

    @FormUrlEncoded
    @POST(COMMENT + "/do")
    Observable<Result> addComment(@Field("_csrf") String token,
                                  @Field("op") String op,
                                  @Field("content") String content,
                                  @Field("seccode") String captcha,
                                  @Field("sid") int sid,
                                  @Field("pid") int pid);

    @FormUrlEncoded
    @POST(COMMENT + "/do")
    Observable<Result> opForComment(@Field("_csrf") String token,
                                    @Field("op") String op,
                                    @Field("sid") int sid,
                                    @Field("tid") int tid);

    class Result<T> {

        private static final String FIELD_STATE = "state";
        private static final String FIELD_MESSAGE = "message";
        private static final String FIELD_RESULT = "result";
        private static final String FIELD_ERROR = "error";

        @SerializedName(FIELD_STATE)
        public String state;
        @SerializedName(FIELD_MESSAGE)
        public String message;
        @SerializedName(FIELD_ERROR)
        public String error;
        @SerializedName(FIELD_RESULT)
        public T result;

        public boolean isSuccess() {
            return "success".equals(state);
        }

        public boolean isRestricted() {
            return "busy".equals(error);
        }

        @Override
        public String toString() {
            return "Result{" +
                    "state='" + state + '\'' +
                    ", message='" + message + '\'' +
                    ", error='" + error + '\'' +
                    ", result=" + result +
                    '}';
        }
    }
}
