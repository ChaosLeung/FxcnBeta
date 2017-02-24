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

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import org.chaos.fx.cnbeta.R;
import org.chaos.fx.cnbeta.net.model.Comment;
import org.chaos.fx.cnbeta.util.TimeStringHelper;
import org.chaos.fx.cnbeta.widget.ListAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author Chaos
 *         2015/11/22.
 */
class CommentAdapter extends ListAdapter<Comment, ViewHolder> {

    CommentAdapter(Context context, RecyclerView bindView) {
        super(context, bindView);
    }

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (getBindView() != null && mOnItemChildClickListener != null) {
                View childView;
                switch (v.getId()) {
                    case R.id.support:
                    case R.id.against:
                    case R.id.reply:
                        childView = (View) v.getParent();
                        break;
                    default:
                        childView = v;
                        break;
                }
                mOnItemChildClickListener.onItemChildClick(v, getBindView().getChildAdapterPosition(childView));
            }
        }
    };

    private OnItemChildClickListener mOnItemChildClickListener;

    @Override
    public ViewHolder onCreateHolderInternal(ViewGroup parent, int viewType) {
        ViewHolder holder = new ViewHolder(LayoutInflater.from(getContext()).inflate(R.layout.article_comment_item, parent, false));
        holder.support.setOnClickListener(mOnClickListener);
        holder.against.setOnClickListener(mOnClickListener);
        holder.reply.setOnClickListener(mOnClickListener);
        return holder;
    }

    @Override
    public void onBindHolderInternal(ViewHolder holder, int position) {
        Comment c = get(position);
        holder.comment.setText(c.getContent());
        holder.time.setText(TimeStringHelper.getTimeStrByDefaultTimeStr(c.getCreatedTime()));
        holder.username.setText(TextUtils.isEmpty(c.getUsername())
                ? getContext().getString(R.string.anonymous) : c.getUsername());
        holder.replyComment.setVisibility(View.GONE);
        holder.support.setText(Integer.toString(c.getSupport()));
        holder.against.setText(Integer.toString(c.getAgainst()));
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

    @Override
    protected int getItemCountInternal() {
        return getList().size();
    }

    public void setOnItemChildClickListener(OnItemChildClickListener listener) {
        mOnItemChildClickListener = listener;
    }

    interface OnItemChildClickListener {
        void onItemChildClick(View v, int position);
    }
}

class ViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.comment) TextView comment;
    @BindView(R.id.username) TextView username;
    @BindView(R.id.reply_comment) TextView replyComment;
    @BindView(R.id.time) TextView time;
    @BindView(R.id.support) TextView support;
    @BindView(R.id.against) TextView against;
    @BindView(R.id.reply) ImageButton reply;

    ViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }
}
