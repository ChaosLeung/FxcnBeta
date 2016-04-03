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

package org.chaos.fx.cnbeta.net.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * @author Chaos
 *         4/2/16
 */
public class WebCaptcha {
    @SerializedName("hash1")
    @Expose
    private Integer hash1;
    @SerializedName("hash2")
    @Expose
    private Integer hash2;
    @SerializedName("url")
    @Expose
    private String url;

    public Integer getHash1() {
        return hash1;
    }

    public void setHash1(Integer hash1) {
        this.hash1 = hash1;
    }

    public Integer getHash2() {
        return hash2;
    }

    public void setHash2(Integer hash2) {
        this.hash2 = hash2;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return "WebCaptcha{" +
                "hash1=" + hash1 +
                ", hash2=" + hash2 +
                ", url='" + url + '\'' +
                '}';
    }
}
