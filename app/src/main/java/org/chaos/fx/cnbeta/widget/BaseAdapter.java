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
    private static final int TYPE_FOOTER = Integer.MIN_VALUE + 1;

    private Context mContext;
    private RecyclerView mBindView;

    private View mHeaderView;
    private View mFooterView;

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
            return new RecyclerView.ViewHolder(mHeaderView) {
            };
        } else if (viewType == TYPE_FOOTER) {
            return new RecyclerView.ViewHolder(mFooterView) {
            };
        } else {
            VH holder = onCreateHolderInternal(parent, viewType);
            holder.itemView.setOnClickListener(mOnClickListener);
            return holder;
        }
    }

    @Override
    public final void onBindViewHolder(RecyclerView.ViewHolder holder, int position, List<Object> payloads) {
        int type = getItemViewType(position);
        if (type != TYPE_HEADER && type != TYPE_FOOTER) {
            onBindHolderInternal((VH) holder, mHeaderView == null ? position : position - 1, payloads);
        } else {
            super.onBindViewHolder(holder, position, payloads);
        }
    }

    @Override
    public final void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        int type = getItemViewType(position);
        if (type != TYPE_HEADER && type != TYPE_FOOTER) {
            onBindHolderInternal((VH) holder, mHeaderView == null ? position : position - 1);
        }
    }

    @Override
    public final int getItemViewType(int position) {
        if (position == 0 && mHeaderView != null && mHeaderView.getVisibility() == View.VISIBLE) {
            return TYPE_HEADER;
        } else if (position == getItemCount() - 1 && mFooterView != null && mFooterView.getVisibility() == View.VISIBLE) {
            return TYPE_FOOTER;
        }
        return getItemViewTypeInternal(mHeaderView == null ? position : position - 1);
    }

    @Override
    public long getItemId(int position) {
        int type = getItemViewType(position);
        if (type == TYPE_HEADER) {
            return mHeaderView.getId();
        }
        if (type == TYPE_FOOTER) {
            return mFooterView.getId();
        }
        return getItemIdInternal(mHeaderView == null ? position : position - 1);
    }

    @Override
    public final int getItemCount() {
        int itemCount = getItemCountInternal();
        if (mHeaderView != null && mHeaderView.getVisibility() == View.VISIBLE) {
            itemCount++;
        }
        if (mFooterView != null && mFooterView.getVisibility() == View.VISIBLE) {
            itemCount++;
        }
        return itemCount;
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

    public View getHeaderView() {
        return mHeaderView;
    }

    public void addFooterView(View v) {
        mFooterView = v;
    }

    public View getFooterView() {
        return mFooterView;
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