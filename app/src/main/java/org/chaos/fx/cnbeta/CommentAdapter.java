package org.chaos.fx.cnbeta;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.chaos.fx.cnbeta.net.model.Comment;
import org.chaos.fx.cnbeta.util.TimeStringHelper;
import org.chaos.fx.cnbeta.widget.ListAdapter;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * @author Chaos
 *         2015/11/22.
 */
public class CommentAdapter extends ListAdapter<Comment, CommentAdapter.ViewHolder> {

    public CommentAdapter(Context context, RecyclerView bindView) {
        super(context, bindView);
    }

    @Override
    public ViewHolder onCreateHolderInternal(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(getContext()).inflate(R.layout.article_comment_item, parent, false));
    }

    @Override
    public void onBindHolderInternal(ViewHolder holder, int position) {
        Comment c = get(position);
        holder.comment.setText(c.getContent());
        holder.time.setText(TimeStringHelper.getTimeString(c.getCreatedTime()));
        holder.username.setText(TextUtils.isEmpty(c.getUsername())
                ? getContext().getString(R.string.anonymous) : c.getUsername());
        holder.replyComment.setVisibility(View.GONE);
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

    static class ViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.comment) TextView comment;
        @Bind(R.id.username) TextView username;
        @Bind(R.id.reply_comment) TextView replyComment;
        @Bind(R.id.time) TextView time;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
