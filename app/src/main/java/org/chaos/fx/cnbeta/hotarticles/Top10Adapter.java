/*
 * Copyright 2015 Chaos
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

package org.chaos.fx.cnbeta.hotarticles;

import org.chaos.fx.cnbeta.R;
import org.chaos.fx.cnbeta.net.model.ArticleSummary;
import org.chaos.fx.cnbeta.widget.ArticleHolder;
import org.chaos.fx.cnbeta.widget.BaseArticleAdapter;

/**
 * @author Chaos
 *         2015/11/15.
 */
class Top10Adapter extends BaseArticleAdapter {

    @Override
    protected void convert(ArticleHolder holder, ArticleSummary summary) {
        super.convert(holder, summary);

        int counter = summary.getCounter();
        int comment = summary.getComment();

        String format;
        String text;

        if (counter >= 1000) {
            if (counter % 1000 >= 100) {
                format = mContext.getString(R.string.read_1_decimal_comment_format);
            } else {
                format = mContext.getString(R.string.read_kilo_comment_format);
            }
            text = String.format(format, (float) counter / 1000, comment);
        } else {
            format = mContext.getString(R.string.read_comment_format);
            text = String.format(format, counter, comment);
        }
        holder.summary.setText(text);
    }
}
