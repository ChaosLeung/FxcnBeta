package org.chaos.fx.cnbeta.widget;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * @author Chaos
 *         2015/11/23.
 */
public abstract class BaseAdapter<VH extends RecyclerView.ViewHolder>
        extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_HEADER = Integer.MIN_VALUE;

    private Context mContext;
    private RecyclerView mBindView;

    private View mHeaderView;

    private OnItemClickListener mOnItemClickListener;

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (mBindView != null && mOnItemClickListener != null) {
                mOnItemClickListener.onItemClick(v, mBindView.getChildAdapterPosition(v));
            }
        }
    };

    public BaseAdapter(Context context, RecyclerView bindView) {
        mContext = context;
        mBindView = bindView;
    }

    @Override
    public final RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_HEADER) {
            return new HeaderHolder(mHeaderView);
        } else {
            VH holder = onCreateHolderInternal(parent, viewType);
            holder.itemView.setOnClickListener(mOnClickListener);
            return holder;
        }
    }

    @Override
    public final void onBindViewHolder(RecyclerView.ViewHolder holder, int position, List<Object> payloads) {
        if (getItemViewType(position) != TYPE_HEADER) {
            onBindHolderInternal((VH) holder, mHeaderView == null ? position : position - 1, payloads);
        } else {
            super.onBindViewHolder(holder, position, payloads);
        }
    }

    @Override
    public final void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) != TYPE_HEADER) {
            onBindHolderInternal((VH) holder, mHeaderView == null ? position : position - 1);
        }
    }

    @Override
    public final int getItemViewType(int position) {
        if (mHeaderView != null && position == 0) {
            return TYPE_HEADER;
        }
        return getItemViewTypeInternal(mHeaderView == null ? position : position - 1);
    }

    @Override
    public long getItemId(int position) {
        if (mHeaderView != null && position == 0) {
            return mHeaderView.getId();
        }
        return getItemIdInternal(mHeaderView == null ? position : position - 1);
    }

    @Override
    public final int getItemCount() {
        int itemCount = getItemCountInternal();
        return mHeaderView != null ? itemCount + 1 : itemCount;
    }

    protected abstract VH onCreateHolderInternal(ViewGroup parent, int viewType);

    protected abstract void onBindHolderInternal(VH holder, int position);

    protected void onBindHolderInternal(VH holder, int position, List<Object> payloads) {
        onBindHolderInternal(holder, position);
    }

    protected int getItemViewTypeInternal(int position) {
        return 0;
    }

    protected abstract int getItemCountInternal();

    protected long getItemIdInternal(int position) {
        return RecyclerView.NO_ID;
    }

    public Context getContext() {
        return mContext;
    }

    public void addHeaderView(View v) {
        mHeaderView = v;
    }

    private class HeaderHolder extends RecyclerView.ViewHolder {

        public HeaderHolder(View itemView) {
            super(itemView);
        }
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mOnItemClickListener = listener;
    }

    public void removeOnItemClickListener() {
        mOnItemClickListener = null;
    }

    public interface OnItemClickListener {
        void onItemClick(View v, int position);
    }
}