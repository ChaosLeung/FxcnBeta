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
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.util.Pair;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;

import org.chaos.fx.cnbeta.R;
import org.chaos.fx.cnbeta.ReselectedDispatcher;
import org.chaos.fx.cnbeta.app.BaseFragment;
import org.chaos.fx.cnbeta.details.ContentActivity;
import org.chaos.fx.cnbeta.net.model.ArticleSummary;
import org.chaos.fx.cnbeta.preferences.PreferenceHelper;
import org.chaos.fx.cnbeta.skin.SkinItemDecoration;
import org.chaos.fx.cnbeta.widget.FxRecyclerView;
import org.chaos.fx.cnbeta.widget.LoadingView;
import org.chaos.fx.cnbeta.widget.NonAnimation;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import skin.support.SkinCompatManager;
import skin.support.observe.SkinObservable;
import skin.support.observe.SkinObserver;

/**
 * @author Chaos
 *         2015/11/14.
 */
public class ArticlesFragment extends BaseFragment
        implements SwipeRefreshLayout.OnRefreshListener, BaseQuickAdapter.RequestLoadMoreListener,
        ArticlesContract.View, ReselectedDispatcher.OnReselectListener, SkinObserver {

    private static final String KEY_TOPIC_ID = "topic_id";

    private static final int STORE_MAX_COUNT = 50;

    public static ArticlesFragment newInstance(String topicId) {
        ArticlesFragment fragment = new ArticlesFragment();
        Bundle bundle = new Bundle();
        bundle.putString(KEY_TOPIC_ID, topicId);
        fragment.setArguments(bundle);
        return fragment;
    }

    @BindView(R.id.swipe) SwipeRefreshLayout mSwipeRefreshLayout;
    @BindView(R.id.recycler_view) FxRecyclerView mRecyclerView;
    @BindView(R.id.no_content) TextView mNoContentTipsView;

    private ArticleAdapter mAdapter;

    private ArticlesContract.Presenter mPresenter;

    private int mPreClickPosition;

    private ReselectedDispatcher mReselectedDispatcher;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_articles, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ButterKnife.bind(this, view);

        mReselectedDispatcher = (ReselectedDispatcher) getActivity();
        mReselectedDispatcher.addOnReselectListener(R.id.nav_home, this);

        mAdapter = new ArticleAdapter();
        mAdapter.setOnLoadMoreListener(this, mRecyclerView);
        mAdapter.setLoadMoreView(new LoadingView());

        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addItemDecoration(new SkinItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
        mRecyclerView.addOnItemTouchListener(new OnItemClickListener() {
            @Override
            public void onSimpleItemClick(BaseQuickAdapter adapter, View view, int position) {
                RecyclerView.ViewHolder holder = mRecyclerView.findViewHolderForAdapterPosition(position);
                mPreClickPosition = position;
                ArticleSummary summary = mAdapter.get(position);

                ActivityOptionsCompat options = null;
                if (holder != null) {
                    View tv = holder.itemView.findViewById(R.id.title);
                    options = ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity(),
                            Pair.create(tv, getString(R.string.transition_details_title)),
                            Pair.create(holder.itemView, getString(R.string.transition_details_background)));
                }
                ContentActivity.start(getActivity(), summary.getSid(), summary.getTitle(),
                        summary.getTopicLogo(), options);
            }
        });

        mSwipeRefreshLayout.setColorSchemeColors(
                ResourcesCompat.getColor(getResources(), R.color.colorAccent, getContext().getTheme()));
        mSwipeRefreshLayout.setOnRefreshListener(this);

        String topicId = getArguments().getString(KEY_TOPIC_ID, "null");
        mPresenter = new ArticlesPresenter(topicId);
        mPresenter.subscribe(this);

        SkinCompatManager.getInstance().addObserver(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        mAdapter.notifyItemChanged(mPreClickPosition);

        if (PreferenceHelper.getInstance().inAnimationMode()) {
            mAdapter.openLoadAnimation(BaseQuickAdapter.SLIDEIN_BOTTOM);
        } else {
            mAdapter.openLoadAnimation(NonAnimation.INSTANCE);
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && mAdapter != null) {
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        SkinCompatManager.getInstance().deleteObserver(this);

        mReselectedDispatcher.removeOnReselectListener(this);

        int size = mAdapter.size();
        List<ArticleSummary> storeArticles =
                mAdapter.subList(0, size >= STORE_MAX_COUNT ? STORE_MAX_COUNT : size);
        mPresenter.saveArticles(storeArticles);

        mPresenter.unsubscribe();
    }

    @Override
    public void onRefresh() {
        int sid;
        if (mAdapter.isEmpty()) {
            sid = -1;
        } else {
            sid = mAdapter.get(0).getSid();
        }
        mPresenter.loadNewArticles(sid);
    }

    @Override
    public void onLoadMoreRequested() {
        mPresenter.loadOldArticles(mAdapter.get(mAdapter.size() - 1).getSid());
    }

    @Override
    public void setRefreshing(boolean refreshing) {
        mSwipeRefreshLayout.setRefreshing(refreshing);
        mAdapter.setEnableLoadMore(!refreshing);
    }

    @Override
    public boolean isRefreshing() {
        return mSwipeRefreshLayout.isRefreshing();
    }

    @Override
    public void setLoading(boolean loading) {
        mSwipeRefreshLayout.setEnabled(!loading);
        if (!loading) {
            mAdapter.loadMoreComplete();
        }
    }

    @Override
    public boolean isLoading() {
        return mAdapter.isLoading();
    }

    @Override
    public void addArticles(List<ArticleSummary> articles, boolean addToTop) {
        if (addToTop) {
            mAdapter.addAll(0, articles);
            mRecyclerView.scrollToPosition(0);
        } else {
            mAdapter.addAll(articles);
        }
        showNothingTipsIfNeed();
    }

    @Override
    public void clearArticles() {
        mAdapter.clear();
    }

    @Override
    public boolean isEmpty() {
        return mAdapter.isEmpty();
    }

    @Override
    public void showNoArticles() {
        showNothingTipsIfNeed();
    }

    @Override
    public void showLoadingArticlesError() {
        showNothingTipsIfNeed();
        setLoading(false);
        setRefreshing(false);
    }

    @Override
    public void onReselect() {
        if (mRecyclerView.computeVerticalScrollOffset() == 0) {
            onRefresh();
        } else {
            mRecyclerView.smoothScrollToFirstItem();
        }
    }

    public void showNothingTipsIfNeed() {
        mNoContentTipsView.setVisibility(mAdapter.isEmpty() ? View.VISIBLE : View.GONE);
    }

    @Override
    public void updateSkin(SkinObservable observable, Object o) {
        mAdapter.notifyDataSetChanged();
    }
}
