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

package org.chaos.fx.cnbeta.widget;

import android.content.res.Resources;

import com.squareup.picasso.Picasso;

import org.chaos.fx.cnbeta.R;
import org.chaos.fx.cnbeta.data.ArticlesRepository;
import org.chaos.fx.cnbeta.net.model.ArticleSummary;

/**
 * @author Chaos
 *         2015/11/14.
 */
public abstract class BaseArticleAdapter extends ListAdapter<ArticleSummary, ArticleHolder> {

    public BaseArticleAdapter() {
        super(R.layout.article_item);
    }

    @Override
    protected void convert(ArticleHolder holder, ArticleSummary summary) {
        holder.title.setText(summary.getTitle());
        String thumb = summary.getThumb();
        if (!thumb.startsWith("https") && thumb.startsWith("http")) {
            thumb = thumb.replaceFirst("http", "https");
        }
        Picasso.with(mContext).load(thumb).into(holder.image);
        Resources res = mContext.getResources();
        if (ArticlesRepository.getInstance().hasReadArticle(summary)) {
            holder.title.setTextColor(res.getColor(R.color.card_text_has_read));
            holder.summary.setTextColor(res.getColor(R.color.card_time_text_has_read));
        } else {
            holder.title.setTextColor(res.getColor(R.color.card_text));
            holder.summary.setTextColor(res.getColor(R.color.card_time_text));
        }
    }
}
