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

import android.content.Context;
import android.support.annotation.CallSuper;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.chaos.fx.cnbeta.R;
import org.chaos.fx.cnbeta.net.model.ArticleSummary;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * @author Chaos
 *         2015/11/14.
 */
public abstract class BaseArticleAdapter extends ListAdapter<ArticleSummary, BaseArticleAdapter.ArticleHolder> {

    public BaseArticleAdapter(Context context, RecyclerView bindView) {
        super(context, bindView);
    }

    @Override
    protected ArticleHolder onCreateHolderInternal(ViewGroup parent, int viewType) {
        return new ArticleHolder(LayoutInflater.from(getContext()).inflate(R.layout.article_item, parent, false));
    }

    @CallSuper
    @Override
    protected void onBindHolderInternal(ArticleHolder holder, int position) {
        ArticleSummary summary = get(position);
        holder.title.setText(summary.getTitle());
        Picasso.with(getContext()).load(summary.getThumb()).into(holder.image);
    }

    @Override
    protected int getItemCountInternal() {
        return listSize();
    }

    public class ArticleHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.title) public TextView title;
        @Bind(R.id.summary) public TextView summary;
        @Bind(R.id.image) public ImageView image;

        public ArticleHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
