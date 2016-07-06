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

import org.chaos.fx.cnbeta.net.model.Comment;
import org.chaos.fx.cnbeta.net.model.WebComment;
import org.chaos.fx.cnbeta.net.model.WebCommentResult;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 * @author Chaos
 *         7/6/16
 */
public class ModelUitl {

    public static Comment toComment(WebComment webComment) {
        Comment c = new Comment();
        c.setAgainst(webComment.getAgainstCount());
        c.setSupport(webComment.getSupportCount());
        c.setCreatedTime(webComment.getDate());
        c.setTid(webComment.getTid());
        c.setPid(webComment.getPid());
        c.setContent(webComment.getComment());
        c.setUsername(String.format("%s网友", webComment.getAddress()));
        return c;
    }

    public static ArrayList<Comment> toCommentList(WebCommentResult result) {
        ArrayList<Comment> comments = new ArrayList<>();
        Collection<WebComment> webComments = result.getComments().values();
        Iterator<WebComment> wci = webComments.iterator();
        for (int i = 0; i < webComments.size() && wci.hasNext(); i++) {
            comments.add(toComment(wci.next()));
        }
        return comments;
    }
}
