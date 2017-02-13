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

import com.google.gson.annotations.SerializedName;

import java.util.Map;

public class WebCommentResult {

    @SerializedName("cmntstore")
    private Map<String, WebComment> comments;
    @SerializedName("comment_num")
    private int commentCount;// 评论数量
    @SerializedName("open")
    private int open;// 评论是否已关闭
    @SerializedName("token")
    private String token;// 评论操作所需 token
    @SerializedName("sid")
    private String sid;

    public Map<String, WebComment> getComments() {
        return comments;
    }

    public void setComments(Map<String, WebComment> comments) {
        this.comments = comments;
    }

    public int getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(int commentCount) {
        this.commentCount = commentCount;
    }

    public boolean isOpen() {
        return open == 1;
    }

    public void setOpen(int open) {
        this.open = open;
    }

    public int getOpen() {
        return open;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }

    @Override
    public String toString() {
        return "WebCommentResult{" +
                "comments=" + comments +
                ", commentCount=" + commentCount +
                ", open=" + open +
                ", token='" + token + '\'' +
                ", sid='" + sid + '\'' +
                '}';
    }
}