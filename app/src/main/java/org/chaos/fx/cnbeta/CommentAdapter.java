package org.chaos.fx.cnbeta;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.chaos.fx.cnbeta.net.model.Comment;
import org.chaos.fx.cnbeta.util.TimeStringHelper;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * @author Chaos
 *         2015/11/22.
 */
public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder> {

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;

    private Context mContext;

    private View mHeaderView;
    private RecyclerView mBindView;

    private OnItemClickListener mOnItemClickListener;
    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (mBindView != null && mOnItemClickListener != null) {
                mOnItemClickListener.onItemClick(v, mBindView.getChildAdapterPosition(v));
            }
        }
    };

    private List<Comment> mComments = new ArrayList<>();

    public CommentAdapter(Context context, RecyclerView bindView) {
        mContext = context;
        mBindView = bindView;
    }

    public void addHeaderView(@NonNull View view) {
        mHeaderView = view;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mOnItemClickListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_HEADER) {
            return new ViewHolder(mHeaderView);
        } else {
            return new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.article_comment_item, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (position > 0) {
            Comment c = mComments.get(position - 1);
            holder.comment.setText(c.getContent());
            holder.time.setText(TimeStringHelper.getTimeString(c.getCreatedTime()));
            holder.username.setText(TextUtils.isEmpty(c.getUsername())
                    ? mContext.getString(R.string.anonymous) : c.getUsername());
            holder.replyComment.setVisibility(View.GONE);
            if (c.getPid() > 0) {
                Comment replyComment = new Comment();
                replyComment.setTid(c.getPid());
                int replyIndex = mComments.indexOf(replyComment);
                if (replyIndex >= 0) {
                    holder.replyComment.setText(mComments.get(replyIndex).getContent());
                    holder.replyComment.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (mHeaderView != null && position == 0) {
            return TYPE_HEADER;
        }
        return TYPE_ITEM;
    }

    @Override
    public int getItemCount() {
        return mComments.size() + (mHeaderView != null ? 1 : 0);
    }

    public List<Comment> getComments() {
        return mComments;
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.comment) TextView comment;
        @Bind(R.id.username) TextView username;
        @Bind(R.id.reply_comment) TextView replyComment;
        @Bind(R.id.time) TextView time;

        public ViewHolder(View itemView) {
            super(itemView);
            if (itemView != mHeaderView) {
                ButterKnife.bind(this, itemView);
                itemView.setOnClickListener(mOnClickListener);
            }
        }
    }

    public interface OnItemClickListener {
        void onItemClick(View v, int position);
    }
}
