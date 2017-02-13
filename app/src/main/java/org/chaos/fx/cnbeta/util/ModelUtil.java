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

package org.chaos.fx.cnbeta.util;

import org.chaos.fx.cnbeta.net.model.ClosedComment;
import org.chaos.fx.cnbeta.net.model.Comment;
import org.chaos.fx.cnbeta.net.model.WebComment;
import org.chaos.fx.cnbeta.net.model.WebCommentResult;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author Chaos
 *         7/6/16
 */
public class ModelUtil {

    public static Comment toComment(WebComment webComment) {
        Comment c = new Comment();
        c.setAgainst(webComment.getAgainstCount());
        c.setSupport(webComment.getSupportCount());
        c.setCreatedTime(webComment.getDate());
        c.setTid(webComment.getTid());
        c.setPid(webComment.getPid());
        c.setContent(webComment.getComment());
        if (!"匿名人士".equals(webComment.getName())) {
            c.setUsername(webComment.getName());
        } else if (webComment.getAddress() != null && !"".equals(webComment.getAddress())) {
            c.setUsername(String.format("%s网友", webComment.getAddress()));
        }
        return c;
    }

    public static ArrayList<Comment> toCommentList(WebCommentResult result) {
        ArrayList<Comment> comments = new ArrayList<>();
        Map<String, WebComment> commentMap = result.getComments();
        if (commentMap == null || commentMap.isEmpty()) {
            return comments;
        }
        Collection<WebComment> webComments = commentMap.values();
        Iterator<WebComment> wci = webComments.iterator();
        for (int i = 0; i < webComments.size() && wci.hasNext(); i++) {
            comments.add(toComment(wci.next()));
        }
        Collections.sort(comments, CommentComparator.DEFAULT_COMPARATOR);
        return comments;
    }

    public static WebComment toWebComment(ClosedComment comment) {
        WebComment c = new WebComment();
        c.setAgainstCount(comment.getAgainst());
        c.setSupportCount(comment.getSupport());
        c.setDate(comment.getDate());
        c.setTid(comment.getTid());
        c.setComment(comment.getComment());
        c.setName(comment.getName());
        return c;
    }

    public static Map<String, WebComment> toWebCommentMap(List<ClosedComment> comments) {
        if (comments == null || comments.isEmpty()) {
            return Collections.emptyMap();
        }
        Map<String, WebComment> commentMap = new HashMap<>();
        for (ClosedComment c : comments) {
            commentMap.put(Integer.toString(c.getTid()), toWebComment(c));
        }
        return commentMap;
    }
}
