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

package org.chaos.fx.cnbeta.home;

import android.content.Context;
import android.support.v7.widget.RecyclerView;

import org.chaos.fx.cnbeta.widget.BaseArticleAdapter;
import org.chaos.fx.cnbeta.net.model.ArticleSummary;
import org.chaos.fx.cnbeta.util.TimeStringHelper;

/**
 * @author Chaos
 *         2015/11/15.
 */
public class ArticleAdapter extends BaseArticleAdapter {

    public ArticleAdapter(Context context, RecyclerView bindView) {
        super(context, bindView);
    }

    @Override
    protected void onBindHolderInternal(ArticleHolder holder, int position) {
        super.onBindHolderInternal(holder, position);
        ArticleSummary summary = get(position);
        holder.summary.setText(TimeStringHelper.getTimeString(summary.getPubtime()));
    }
}
