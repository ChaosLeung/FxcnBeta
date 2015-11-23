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
import android.widget.ProgressBar;

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
    @Bind(R.id.load_more_progress) ProgressBar mProgressBar;

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
        mSwipeLayout.setOnRefreshListener(this);
        mProgressBar.setEnabled(false);
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
            mProgressBar.setVisibility(loading ? VISIBLE : GONE);
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
        mProgressBar.setEnabled(true);
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
                        if (llm.findLastVisibleItemPosition() + 1
                                == llm.getItemCount() && dy > 50) {
                            setLoading(true);
                            mOnLoadMoreListener.onLoadMore();
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
        mProgressBar.setVisibility(GONE);
        mProgressBar.setEnabled(false);
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
