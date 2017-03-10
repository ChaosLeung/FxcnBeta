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

import android.text.TextUtils;

import org.chaos.fx.cnbeta.R;
import org.chaos.fx.cnbeta.net.model.HotComment;
import org.chaos.fx.cnbeta.widget.ListAdapter;

/**
 * @author Chaos
 *         2015/11/14.
 */
class HotCommentAdapter extends ListAdapter<HotComment, HotCommentViewHolder> {

    HotCommentAdapter() {
        super(R.layout.hot_comment_item);
    }

    @Override
    protected void convert(HotCommentViewHolder holder, HotComment comment) {
        holder.title.setText(comment.getTitle());
        String username = TextUtils.isEmpty(comment.getUsername())
                ? mContext.getString(R.string.anonymous) : comment.getUsername();
        holder.comment.setText(String.format(
                mContext.getString(R.string.hot_comment_content), username, comment.getComment()));
    }

}

