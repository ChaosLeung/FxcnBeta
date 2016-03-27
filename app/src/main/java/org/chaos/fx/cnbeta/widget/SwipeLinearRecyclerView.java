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
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import org.chaos.fx.cnbeta.R;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * @author Chaos
 *         2015/11/23.
 */
public class SwipeLinearRecyclerView extends FrameLayout implements SwipeRefreshLayout.OnRefreshListener {

    @Bind(R.id.swipe) SwipeRefreshLayout mSwipeLayout;
    @Bind(R.id.recycler_view) RecyclerView mRecyclerView;

    private OnRefreshListener mOnRefreshListener;
    private OnLoadMoreListener mOnLoadMoreListener;

    private boolean isLoading = false;
    private boolean isShowLoadingBar = true;

    public SwipeLinearRecyclerView(Context context) {
        super(context);
        init();
    }

    public SwipeLinearRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SwipeLinearRecyclerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        View v = LayoutInflater.from(getContext()).inflate(R.layout.swipe_recycler_view_internal, this);
        ButterKnife.bind(this, v);
        mSwipeLayout.setEnabled(false);
        mSwipeLayout.setColorSchemeColors(getContext().getResources().getColor(R.color.colorAccent));
        mSwipeLayout.setOnRefreshListener(this);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL_LIST));

        setRefreshing(false);
        setLoading(false);
    }

    public void setAdapter(RecyclerView.Adapter adapter) {
        mRecyclerView.setAdapter(adapter);
    }

    public void setRefreshing(boolean refreshing) {
        mSwipeLayout.setRefreshing(refreshing);
    }

    public boolean isRefreshing() {
        return mSwipeLayout.isRefreshing();
    }

    public void setLoading(boolean loading) {
        isLoading = loading;
        if (isShowLoadingBar) {
            mRecyclerView.invalidateItemDecorations();
        }
    }

    public boolean isLoading() {
        return isLoading;
    }

    public void setShowLoadingBar(boolean show) {
        isShowLoadingBar = show;
    }

    public RecyclerView getRecyclerView() {
        return mRecyclerView;
    }

    public void setOnRefreshListener(@NonNull OnRefreshListener listener) {
        mOnRefreshListener = listener;
        mSwipeLayout.setEnabled(true);
    }

    public void setOnLoadMoreListener(@NonNull OnLoadMoreListener listener) {
        mOnLoadMoreListener = listener;
        RecyclerView.LayoutManager lm = mRecyclerView.getLayoutManager();
        if (lm == null) {
            throw new IllegalStateException("RecyclerView has no LayoutManager");
        }
        if (lm instanceof LinearLayoutManager) {
            final LinearLayoutManager llm = (LinearLayoutManager) lm;
            mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    if (mOnLoadMoreListener != null && !isRefreshing() && !isLoading()) {
                        if (llm.getItemCount() == 1) {
                            if (recyclerView.findViewHolderForAdapterPosition(0).itemView.getHeight()
                                    - recyclerView.getHeight() * 6 / 5 <= recyclerView.computeVerticalScrollOffset()) {
                                onLoadMore();
                            }
                        } else if (llm.getItemCount() - llm.getChildCount()
                                <= llm.findFirstVisibleItemPosition() && dy > 0) {
                            onLoadMore();
                        }
                    }
                }
            });
        } else {
            throw new RuntimeException("Unsupported LayoutManager used.");
        }
    }

    public void removeOnRefreshListener() {
        mOnRefreshListener = null;
        mSwipeLayout.setEnabled(false);
    }

    public void removeOnLoadMoreListener() {
        mOnLoadMoreListener = null;
    }

    private void onLoadMore() {
        setLoading(true);
        mOnLoadMoreListener.onLoadMore();
    }

    @Override
    public void onRefresh() {
        if (mOnRefreshListener != null && !isLoading()) {
            mOnRefreshListener.onRefresh();
        }
    }

    public interface OnRefreshListener {
        void onRefresh();
    }

    public interface OnLoadMoreListener {
        void onLoadMore();
    }
}
