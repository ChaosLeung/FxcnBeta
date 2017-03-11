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
import org.chaos.fx.cnbeta.net.model.WebCommentResult;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Chaos
 *         7/6/16
 */
public class ModelUtil {

    public static List<Comment> toCommentList(WebCommentResult result) {
        Map<String, Comment> commentMap = result.getComments();
        if (commentMap == null || commentMap.isEmpty()) {
            return Collections.emptyList();
        }
        return new ArrayList<>(commentMap.values());
    }

    public static Map<String, Comment> toWebCommentMap(List<Comment> comments) {
        if (comments == null || comments.isEmpty()) {
            return Collections.emptyMap();
        }
        Map<String, Comment> commentMap = new HashMap<>();
        for (Comment c : comments) {
            commentMap.put(Integer.toString(c.getTid()), c);
        }
        return commentMap;
    }
}
