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

import android.support.v4.util.ArrayMap;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * @author Chaos
 *         6/29/16
 */

public class CnBetaHttpClientProvider {

    public static OkHttpClient newCnBetaHttpClient() {
        return new OkHttpClient.Builder()
                .cookieJar(new CookieJar() {

                    private Map<String, List<Cookie>> mCookies = new ArrayMap<>();

                    @Override
                    public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
                        mCookies.put(url.toString(), cookies);
                    }

                    @Override
                    public List<Cookie> loadForRequest(HttpUrl url) {
                        if (url.toString().contains(WebApi.HOST_URL + "/captcha.htm") || url.toString().contains(WebApi.HOST_URL + "/comment")) {
                            return mCookies.get(WebApi.HOST_URL + "/cmt");
                        } else {
                            return Collections.emptyList();
                        }
                    }
                })
                .addInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        Request newRequest = chain.request().newBuilder()
                                .addHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")
                                .addHeader("Origin", "http://www.cnbeta.com")
                                .addHeader("Referer", "http://www.cnbeta.com/")
                                .addHeader("X-Requested-With", "XMLHttpRequest")
                                .build();
                        return chain.proceed(newRequest);
                    }
                })
                .build();
    }
}
