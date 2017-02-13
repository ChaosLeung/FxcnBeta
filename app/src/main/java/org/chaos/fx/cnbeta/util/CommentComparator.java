/*
 * Copyright 2017 Chaos
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

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Locale;

/**
 * @author Chaos
 *         13/02/2017
 */

public class CommentComparator implements Comparator<Comment> {

    public static final CommentComparator DEFAULT_COMPARATOR = new CommentComparator();

    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);

    @Override
    public int compare(Comment o1, Comment o2) {
        try {
            // DESC
            long time1 = DATE_FORMAT.parse(o1.getCreatedTime()).getTime();
            long time2 = DATE_FORMAT.parse(o2.getCreatedTime()).getTime();
            if (time1 > time2) {
                return -1;
            } else if (time1 < time2) {
                return 1;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }
}
