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

package org.chaos.fx.cnbeta.details;

import android.util.Log;

import org.chaos.fx.cnbeta.net.model.NewsContent;
import org.chaos.fx.cnbeta.util.HtmlParser;
import org.chaos.fx.cnbeta.util.TimeStringHelper;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;

import java.text.ParseException;

/**
 * @author Chaos
 *         05/03/2017
 */

class WebContentParser implements HtmlParser<NewsContent> {

    private static final String TAG = "WebContentParser";

    private static final String QUERY_TITLE = "header.title > h1";
    private static final String QUERY_SOURCE = "span.source";
    private static final String QUERY_TIME = "div.meta > span";
    private static final String QUERY_AUTHOR = "div.article-author";
    private static final String QUERY_SUMMARY = "div.article-summary > p";
    private static final String QUERY_THUMB = "a > img[title]";
    private static final String QUERY_RELATION = "div.article-relation";
    private static final String QUERY_ADVERTISEMENT = "div.article-global";
    private static final String QUERY_CONTENT = "div.article-content";

    @Override
    public NewsContent parse(String html) {
        Element body = Jsoup.parse(html).body();

        String title = body.select(QUERY_TITLE).text();
        String source = body.select(QUERY_SOURCE).text();
        source = source.substring(3, source.length());
        String homeText = body.select(QUERY_SUMMARY).text();
        String thumb = body.select(QUERY_THUMB).attr("src").replace("http://static.cnbetacdn.com", "");

        String time = body.select(QUERY_TIME).text();
        try {
            time = TimeStringHelper.cnTime2DefaultTime(time);
        } catch (ParseException e) {
            Log.e(TAG, "parse: ", e);
        }

        Element contentElement = body.select(QUERY_CONTENT).first();
        contentElement.select(QUERY_RELATION).remove();
        contentElement.select(QUERY_ADVERTISEMENT).remove();
        String bodyText = contentElement.html();

        String author = body.select(QUERY_AUTHOR).text();
        author = author.substring(6, author.length() - 1);
        NewsContent newsContent = new NewsContent();
        newsContent.setTitle(title);
        newsContent.setTime(time);
        newsContent.setHomeText(homeText);
        newsContent.setBodyText(bodyText);
        newsContent.setThumb(thumb);
        newsContent.setSource(source);
        newsContent.setAuthor(author);
        return newsContent;
    }

    @Override
    public void parse(Callback<NewsContent> callback) {
        // no-op
    }
}
