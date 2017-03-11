/*
 * Copyright 2016 Chaos
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

import android.text.TextUtils;
import android.view.View;

import org.chaos.fx.cnbeta.R;
import org.chaos.fx.cnbeta.net.model.Comment;
import org.chaos.fx.cnbeta.util.TimeStringHelper;
import org.chaos.fx.cnbeta.widget.ListAdapter;

/**
 * @author Chaos
 *         2015/11/22.
 */
class CommentAdapter extends ListAdapter<Comment, CommentViewHolder> {
    CommentAdapter() {
        super(R.layout.article_comment_item);
    }

    @Override
    protected void convert(CommentViewHolder holder, Comment c) {
        holder.comment.setText(c.getContent());
        holder.time.setText(TimeStringHelper.getTimeStrByDefaultTimeStr(c.getCreatedTime()));

        String username = TextUtils.isEmpty(c.getUsername()) ?
                mContext.getString(R.string.anonymous) : c.getUsername();
        if ((mContext.getString(R.string.anonymous).equals(username)
                || mContext.getString(R.string.anonymous_web).equals(username))
                && !TextUtils.isEmpty(c.getAddress())) {
            username = String.format(mContext.getString(R.string.anonymous_format), c.getAddress());
        }
        holder.username.setText(username);

        holder.replyComment.setVisibility(View.GONE);
        String intFormat = mContext.getString(R.string.int_format);
        holder.support.setText(String.format(intFormat, c.getSupport()));
        holder.against.setText(String.format(intFormat, c.getAgainst()));
        if (c.getPid() > 0) {
            Comment replyComment = new Comment();
            replyComment.setTid(c.getPid());
            int replyIndex = indexOf(replyComment);
            if (replyIndex >= 0) {
                holder.replyComment.setText(get(replyIndex).getContent());
                holder.replyComment.setVisibility(View.VISIBLE);
            }
        }
    }
}

