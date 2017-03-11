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

package org.chaos.fx.cnbeta.widget;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseViewHolder;

import org.chaos.fx.cnbeta.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * For Article
 *
 * @author Chaos
 *         10/03/2017
 */
public class ArticleHolder extends BaseViewHolder {

    @BindView(R.id.title) public TextView title;
    @BindView(R.id.summary) public TextView summary;
    @BindView(R.id.image) public ImageView image;

    public ArticleHolder(View itemView) {
        super(itemView);
        if (itemView.findViewById(R.id.loading_view) == null) {// For idiot LoadMoreView's ViewHolder
            ButterKnife.bind(this, itemView);
        }
    }
}