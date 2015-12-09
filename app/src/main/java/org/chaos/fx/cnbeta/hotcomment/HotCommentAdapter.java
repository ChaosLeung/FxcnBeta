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

package org.chaos.fx.cnbeta.hotcomment;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.chaos.fx.cnbeta.R;
import org.chaos.fx.cnbeta.net.model.HotComment;
import org.chaos.fx.cnbeta.widget.ListAdapter;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * @author Chaos
 *         2015/11/14.
 */
public class HotCommentAdapter extends ListAdapter<HotComment, HotCommentAdapter.ViewHolder> {

    public HotCommentAdapter(Context context, RecyclerView bindView) {
        super(context, bindView);
    }

    @Override
    public ViewHolder onCreateHolderInternal(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(getContext()).inflate(R.layout.hot_comment_item, parent, false));
    }

    @Override
    public final void onBindHolderInternal(ViewHolder holder, int position) {
        HotComment comment = get(position);
        holder.title.setText(comment.getSubject());
        String username = TextUtils.isEmpty(comment.getUsername())
                ? getContext().getString(R.string.anonymous) : comment.getUsername();
        holder.comment.setText(String.format(
                getContext().getString(R.string.hot_comment_content), username, comment.getComment()));
    }

    @Override
    public int getItemCountInternal() {
        return listSize();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.title) public TextView title;
        @Bind(R.id.comment) public TextView comment;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
