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

package org.chaos.fx.cnbeta.home;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.chaos.fx.cnbeta.MainActivity;
import org.chaos.fx.cnbeta.R;
import org.chaos.fx.cnbeta.app.BaseFragment;
import org.chaos.fx.cnbeta.details.ContentActivity;
import org.chaos.fx.cnbeta.net.model.ArticleSummary;
import org.chaos.fx.cnbeta.widget.BaseAdapter;
import org.chaos.fx.cnbeta.widget.SwipeLinearRecyclerView;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * @author Chaos
 *         2015/11/14.
 */
public class ArticlesFragment extends BaseFragment
        implements SwipeLinearRecyclerView.OnRefreshListener,
        SwipeLinearRecyclerView.OnLoadMoreListener,
        MainActivity.OnActionBarDoubleClickListener,
        ArticlesContract.View {

    private static final String KEY_TOPIC_ID = "topic_id";

    private static final int STORE_MAX_COUNT = 50;

    private static Handler sHandler = new Handler();

    private static final long RESET_ACTION_BAR_TITLE_DELAY_TIME = 3000;

    public static ArticlesFragment newInstance(String topicId) {
        ArticlesFragment fragment = new ArticlesFragment();
        Bundle bundle = new Bundle();
        bundle.putString(KEY_TOPIC_ID, topicId);
        fragment.setArguments(bundle);
        return fragment;
    }

    private Runnable mResetActionBarTitleRunnable = new Runnable() {
        @Override
        public void run() {
            setActionBarTitle(R.string.nav_home);
        }
    };

    @Bind(R.id.swipe_recycler_view)
    SwipeLinearRecyclerView mArticlesView;

    private ArticleAdapter mArticleAdapter;

    private ArticlesContract.Presenter mPresenter;

    private int mPreClickPosition;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setActionBarTitle(R.string.nav_home);
        String topicId = getArguments().getString(KEY_TOPIC_ID, "null");
        mPresenter = new ArticlesPresenter(topicId, this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.layout_swipe_recycler_view, container, false);
        ButterKnife.bind(this, rootView);

        mArticleAdapter = new ArticleAdapter(getActivity(), mArticlesView.getRecyclerView());
        mArticleAdapter.setOnItemClickListener(new BaseAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                mPreClickPosition = position;
                ArticleSummary summary = mArticleAdapter.get(position);
                ContentActivity.start(getActivity(), summary.getSid(), summary.getTopicLogo());
            }
        });
        mArticleAdapter.addFooterView(
                LayoutInflater.from(getActivity()).inflate(
                        R.layout.layout_loading, mArticlesView, false));
        mArticlesView.setAdapter(mArticleAdapter);
        mArticlesView.getRecyclerView().addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                float verticalOffset = recyclerView.computeVerticalScrollOffset();
                float heightRatio = verticalOffset / getResources().getDisplayMetrics().heightPixels;
                if (heightRatio <= 0.3f) {
                    sHandler.post(mResetActionBarTitleRunnable);
                }
                if (Math.round(heightRatio) >= 6 && dy <= -180) {
                    getSupportActionBar().setTitle(R.string.double_click_move_to_top);
                    sHandler.removeCallbacks(mResetActionBarTitleRunnable);
                    sHandler.postDelayed(mResetActionBarTitleRunnable, RESET_ACTION_BAR_TITLE_DELAY_TIME);
                }
            }
        });

        mArticlesView.setOnRefreshListener(this);
        mArticlesView.setOnLoadMoreListener(this);
        return rootView;
    }

    @Override
    public void onPause() {
        super.onPause();
        mPresenter.unsubscribe();
        ((MainActivity) getActivity()).removeOnActionBarDoubleClickListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.subscribe();
        ((MainActivity) getActivity()).addOnActionBarDoubleClickListener(this);
        mArticleAdapter.notifyItemChanged(mPreClickPosition);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        sHandler.removeCallbacks(mResetActionBarTitleRunnable);
        int size = mArticleAdapter.listSize();
        List<ArticleSummary> storeArticles = mArticleAdapter.getList().subList(0, size >= STORE_MAX_COUNT ? STORE_MAX_COUNT : size);
        mPresenter.saveArticles(storeArticles);
    }

    @Override
    public void onRefresh() {
        int sid;
        if (mArticleAdapter.getItemCount() == 0) {
            sid = -1;
        } else {
            sid = mArticleAdapter.get(0).getSid();
        }
        mPresenter.loadNewArticles(sid);
    }

    @Override
    public void onLoadMore() {
        mPresenter.loadOldArticles(mArticleAdapter.get(mArticleAdapter.getItemCount() - 1).getSid());
    }

    private void showSnackBar(@StringRes int strId) {
        Snackbar.make(mArticlesView, strId, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void onActionBarDoubleClick() {
        sHandler.removeCallbacks(mResetActionBarTitleRunnable);
        setActionBarTitle(R.string.nav_home);
        mArticlesView.getRecyclerView().scrollToPosition(0);
    }

    @Override
    public void setRefreshing(boolean refreshing) {
        mArticlesView.setRefreshing(refreshing);
    }

    @Override
    public boolean isRefreshing() {
        return mArticlesView.isRefreshing();
    }

    @Override
    public void setLoading(boolean loading) {
        mArticlesView.setLoading(loading);
        mArticleAdapter.getFooterView().setVisibility(loading ? View.VISIBLE : View.INVISIBLE);

        if (loading) {
            mArticleAdapter.notifyItemInserted(mArticleAdapter.getItemCount());
            mArticlesView.getRecyclerView().smoothScrollToPosition(mArticleAdapter.getItemCount() - 1);
        }
    }

    @Override
    public boolean isLoading() {
        return mArticlesView.isLoading();
    }

    @Override
    public void addArticles(List<ArticleSummary> articles, boolean addToTop) {
        if (addToTop) {
            mArticleAdapter.addAll(0, articles);
            mArticlesView.getRecyclerView().scrollToPosition(0);
        } else {
            mArticleAdapter.getList().addAll(articles);
            mArticleAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void clearArticles() {
        mArticleAdapter.clear();
        mArticleAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean isEmpty() {
        return mArticleAdapter.isEmpty();
    }

    @Override
    public void showNoArticles() {
        showSnackBar(R.string.no_more_articles);
    }

    @Override
    public void showLoadingArticlesError() {
        showSnackBar(R.string.load_articles_failed);
        setLoading(false);
        setRefreshing(false);
    }
}
