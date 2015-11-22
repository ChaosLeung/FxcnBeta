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

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * @author Chaos
 *         2015/11/14.
 */
public class HotCommentAdapter extends RecyclerView.Adapter<HotCommentAdapter.ViewHolder> {

    protected Context mContext;
    private List<HotComment> mComments = new ArrayList<>();
    private RecyclerView mBindView;

    private OnItemClickListener mOnItemClickListener;
    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (mBindView != null && mOnItemClickListener != null) {
                mOnItemClickListener.onItemClick(mBindView.getChildAdapterPosition(v));
            }
        }
    };

    public HotCommentAdapter(Context context, RecyclerView bindView) {
        mContext = context;
        mBindView = bindView;
    }

    public List<HotComment> getComments() {
        return mComments;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mOnItemClickListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.hot_comment_item, parent, false));
    }

    @Override
    public final void onBindViewHolder(ViewHolder holder, int position) {
        HotComment comment = getComments().get(position);
        holder.title.setText(comment.getSubject());
        String username = TextUtils.isEmpty(comment.getUsername())
                ? mContext.getString(R.string.anonymous) : comment.getUsername();
        holder.comment.setText(String.format(
                mContext.getString(R.string.hot_comment_content), username, comment.getComment()));
    }

    @Override
    public int getItemCount() {
        return mComments.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.title) public TextView title;
        @Bind(R.id.comment) public TextView comment;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(mOnClickListener);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }
}
