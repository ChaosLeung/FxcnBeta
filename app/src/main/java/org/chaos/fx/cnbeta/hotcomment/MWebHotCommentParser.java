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

package org.chaos.fx.cnbeta.hotcomment;

import org.chaos.fx.cnbeta.net.model.HotComment;
import org.chaos.fx.cnbeta.util.HtmlParser;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Chaos
 *         05/03/2017
 */

class MWebHotCommentParser implements HtmlParser<List<HotComment>> {

    private static final String QUERY_COMMENT_LIST =
            "div[data-role=content] > div.module > ul.module_list";
    private static final String QUERY_COMMENT = ".module_hot_cmt";
    private static final String QUERY_COMMENT_CONTENT = ".jh_title.jh_text";
    private static final String QUERY_COMMENT_FOOTER = ".jh_footer.jh_text";
    private static final String QUERY_USERNAME = "strong";
    private static final String QUERY_TITLE = "a";
    private static final String QUERY_ARTICLE_URL_TAG = "a";
    private static final String QUERY_ARTICLE_URL = "href";

    @Override
    public List<HotComment> parse(String html) {
        Document dom = Jsoup.parse(html);
        Elements lists = dom.body().select(QUERY_COMMENT_LIST);
        if (lists != null) {
            Element commentList = lists.get(0);
            Elements comments = commentList.select(QUERY_COMMENT);

            List<HotComment> newComments = new ArrayList<>(comments.size());

            Elements result;
            for (Element e : comments) {
                HotComment comment = new HotComment();

                result = e.select(QUERY_COMMENT_CONTENT);
                int sid = 0;
                if (result != null) {
                    comment.setComment(result.get(0).text());
                    // 有些评论的 URL 在评论内容中
                    String url = result.select(QUERY_ARTICLE_URL_TAG).attr(QUERY_ARTICLE_URL);
                    if (url != null) {
                        url = url.substring(url.lastIndexOf('/') + 1);
                        sid = Integer.parseInt(url.substring(0, url.lastIndexOf('.')));
                    }
                }

                result = e.select(QUERY_COMMENT_FOOTER);
                if (result != null) {
                    comment.setUsername(result.select(QUERY_USERNAME).text() + "网友");

                    Elements titleElements = result.select(QUERY_TITLE);
                    comment.setTitle(titleElements.text());

                    if (sid == 0) {// 有些评论的 URL 则在标题中，坑爹 CB
                        String url = titleElements.attr(QUERY_ARTICLE_URL);
                        String path = URI.create(url).getPath();
                        if (path != null) {
                            String idStr = path.substring(path.lastIndexOf('/') + 1);
                            sid = Integer.parseInt(idStr.substring(0, idStr.lastIndexOf('.')));
                        }
                    }
                    comment.setSid(sid);
                }

                newComments.add(comment);
            }

            return newComments;
        }
        return Collections.emptyList();
    }

    @Override
    public void parse(Callback<List<HotComment>> callback) {
        // no-op
    }
}
