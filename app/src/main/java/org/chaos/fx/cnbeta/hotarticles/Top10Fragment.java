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

package org.chaos.fx.cnbeta.hotarticles;

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
import org.chaos.fx.cnbeta.widget.NonAnimation;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import skin.support.SkinCompatManager;
import skin.support.observe.SkinObservable;
import skin.support.observe.SkinObserver;

/**
 * @author Chaos
 *         2015/11/15.
 */
public class Top10Fragment extends BaseFragment implements Top10Contract.View,
        SwipeRefreshLayout.OnRefreshListener, ReselectedDispatcher.OnReselectListener, SkinObserver {

    public static Top10Fragment newInstance() {
        return new Top10Fragment();
    }

    @BindView(R.id.swipe) SwipeRefreshLayout mSwipeRefreshLayout;
    @BindView(R.id.recycler_view) FxRecyclerView mRecyclerView;
    @BindView(R.id.no_content) TextView mNoContentTipsView;

    private Top10Adapter mAdapter;

    private Top10Presenter mPresenter;

    private int mPreClickPosition;

    private ReselectedDispatcher mReselectedDispatcher;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_top10, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ButterKnife.bind(this, view);

        mReselectedDispatcher = (ReselectedDispatcher) getActivity();
        mReselectedDispatcher.addOnReselectListener(R.id.nav_hot_articles, this);

        mAdapter = new Top10Adapter();

        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addItemDecoration(new SkinItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
        mRecyclerView.addOnItemTouchListener(new OnItemClickListener() {
            @Override
            public void onSimpleItemClick(BaseQuickAdapter adapter, View view, int position) {
                RecyclerView.ViewHolder holder = mRecyclerView.findViewHolderForAdapterPosition(position);
                mPreClickPosition = position;
                ArticleSummary summary = mAdapter.get(position);

                View tv = holder.itemView.findViewById(R.id.title);
                ActivityOptionsCompat options =
                        ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity(),
                                Pair.create(holder.itemView, getString(R.string.transition_details_background)),
                                Pair.create(tv, getString(R.string.transition_details_title)));
                ContentActivity.start(getActivity(), summary.getSid(), summary.getTitle(),
                        summary.getTopicLogo(), options);
            }
        });

        mSwipeRefreshLayout.setColorSchemeColors(
                ResourcesCompat.getColor(getResources(), R.color.colorAccent, getContext().getTheme()));
        mSwipeRefreshLayout.setOnRefreshListener(this);

        mPresenter = new Top10Presenter();
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
        mPresenter.unsubscribe();
        mReselectedDispatcher.removeOnReselectListener(this);
    }

    @Override
    public void onRefresh() {
        mPresenter.loadTop10Articles();
    }

    @Override
    public void showRefreshing(boolean refreshing) {
        mSwipeRefreshLayout.setRefreshing(refreshing);
    }

    @Override
    public void showNoMoreContent() {
        showNothingTipsIfNeed();
    }

    @Override
    public void showLoadFailed() {
        showNothingTipsIfNeed();
    }

    @Override
    public void addArticleSummary(List<ArticleSummary> summaries) {
        if (!mAdapter.containsAll(summaries)) {
            mAdapter.clear();
            mAdapter.addAll(0, summaries);
            showNothingTipsIfNeed();
        } else {
            showNoMoreContent();
        }
    }

    public void showNothingTipsIfNeed() {
        mNoContentTipsView.setVisibility(mAdapter.isEmpty() ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onReselect() {
        if (mRecyclerView.computeVerticalScrollOffset() == 0) {
            onRefresh();
        } else {
            mRecyclerView.smoothScrollToFirstItem();
        }
    }

    @Override
    public void updateSkin(SkinObservable observable, Object o) {
        mAdapter.notifyDataSetChanged();
    }
}
