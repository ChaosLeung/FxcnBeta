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

import com.chad.library.adapter.base.provider.BaseItemProvider;
import com.squareup.picasso.Picasso;

import org.chaos.fx.cnbeta.R;
import org.chaos.fx.cnbeta.data.ArticlesRepository;
import org.chaos.fx.cnbeta.net.model.ArticleSummary;
import org.chaos.fx.cnbeta.preferences.PreferenceHelper;

import java.util.ArrayList;

/**
 * @author Chaos
 *         2015/11/14.
 */
public abstract class BaseArticleAdapter extends MultipleItemListAdapter<ArticleSummary, ArticleHolder> {

    private static final int TYPE_ITEM_IMAGE_ALIGN_END = 1;
    private static final int TYPE_ITEM_IMAGE_ALIGN_START = 2;

    public BaseArticleAdapter() {
        super(new ArrayList<ArticleSummary>());
        finishInitialize();
    }

    @Override
    protected int getViewType(ArticleSummary articleSummary) {
        return PreferenceHelper.getInstance().isListItemAlignStart()
                ? TYPE_ITEM_IMAGE_ALIGN_START : TYPE_ITEM_IMAGE_ALIGN_END;
    }

    @Override
    public void registerItemProvider() {
        mProviderDelegate.registerProvider(new ArticleImageAlignEndItem());
        mProviderDelegate.registerProvider(new ArticleImageAlignStartItem());
    }

    @Override
    protected void convert(ArticleHolder holder, ArticleSummary summary) {
        holder.title.setText(summary.getTitle());
        String thumb = summary.getThumb();
        if (!thumb.startsWith("https") && thumb.startsWith("http")) {
            thumb = thumb.replaceFirst("http", "https");
        }
        Picasso.get().load(thumb).into(holder.image);
        Resources res = mContext.getResources();
        if (ArticlesRepository.getInstance().hasReadArticle(summary)) {
            holder.title.setTextColor(res.getColor(R.color.card_text_has_read));
            holder.summary.setTextColor(res.getColor(R.color.card_time_text_has_read));
        } else {
            holder.title.setTextColor(res.getColor(R.color.card_text));
            holder.summary.setTextColor(res.getColor(R.color.card_time_text));
        }
    }

    private static class ArticleImageAlignEndItem extends BaseItemProvider<ArticleSummary, ArticleHolder> {

        @Override
        public int viewType() {
            return TYPE_ITEM_IMAGE_ALIGN_END;
        }

        @Override
        public int layout() {
            return R.layout.article_item_image_align_end;
        }

        @Override
        public void convert(ArticleHolder helper, ArticleSummary data, int position) {
            // nothing
        }
    }

    private static class ArticleImageAlignStartItem extends BaseItemProvider<ArticleSummary, ArticleHolder> {

        @Override
        public int viewType() {
            return TYPE_ITEM_IMAGE_ALIGN_START;
        }

        @Override
        public int layout() {
            return R.layout.article_item_image_align_start;
        }

        @Override
        public void convert(ArticleHolder helper, ArticleSummary data, int position) {
            // nothing
        }
    }
}
