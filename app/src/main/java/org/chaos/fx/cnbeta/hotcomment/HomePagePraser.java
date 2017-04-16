/*
 * Copyright 2017 Chaos Leong
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

import org.chaos.fx.cnbeta.util.HtmlParser;
import org.jsoup.Jsoup;

/**
 * @author Chaos Leong
 *         01/04/2017
 */

class HomePagePraser implements HtmlParser<String> {
    @Override
    public String parse(String html) {
        return Jsoup.parse(html).select("meta[name=csrf-token]").attr("content");
    }

    @Override
    public void parse(Callback<String> callback) {

    }
}
