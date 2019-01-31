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

package org.chaos.fx.cnbeta.rank;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
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
import org.chaos.fx.cnbeta.details.ContentActivity;
import org.chaos.fx.cnbeta.net.MobileApi;
import org.chaos.fx.cnbeta.net.model.ArticleSummary;
import org.chaos.fx.cnbeta.preferences.PreferenceHelper;
import org.chaos.fx.cnbeta.widget.FxRecyclerView;
import org.chaos.fx.cnbeta.widget.NonAnimation;
import org.chaos.fx.cnbeta.skin.SkinItemDecoration;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author Chaos
 *         2015/11/17.
 */
public class RankSubFragment extends Fragment implements RankSubContract.View,
        SwipeRefreshLayout.OnRefreshListener {

    private static final String KEY_TYPE = "type";

    /**
     * {@code type} 应为 {@link MobileApi.RankType}
     * 然而直接加了这 annotation AS 语法提示飘红，所以直接砍了
     *
     * @param type 排行类型
     * @return 对应类型的 Fragment
     */
    public static RankSubFragment newInstance(String type) {
        RankSubFragment subFragment = new RankSubFragment();
        Bundle data = new Bundle();
        data.putString(KEY_TYPE, type);
        subFragment.setArguments(data);
        return subFragment;
    }

    @BindView(R.id.swipe) SwipeRefreshLayout mSwipeRefreshLayout;
    @BindView(R.id.recycler_view) FxRecyclerView mRecyclerView;
    @BindView(R.id.no_content) TextView mNoContentTipsView;

    private RankSubAdapter mAdapter;

    private int mPreClickPosition;
    private RankSubContract.Presenter mPresenter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_sub_rank, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ButterKnife.bind(this, view);

        String type = getArguments().getString(KEY_TYPE);

        mAdapter = new RankSubAdapter(type);

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

        mPresenter = new RankSubPresenter(type);
        mPresenter.subscribe(this);
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
        if (isVisibleToUser && mAdapter != null) {
            mAdapter.notifyDataSetChanged();
        }
        super.setUserVisibleHint(isVisibleToUser);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mPresenter.unsubscribe();
    }

    private void showSnackBar(@StringRes int strId) {
        Snackbar.make(mRecyclerView, strId, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void onRefresh() {
        mPresenter.loadArticles();
    }

    @Override
    public void showRefreshing(boolean refreshing) {
        mSwipeRefreshLayout.setRefreshing(refreshing);
    }

    @Override
    public void showLoadFailed() {
        showNothingTipsIfNeed();
        showSnackBar(R.string.load_articles_failed);
    }

    @Override
    public void showNoMoreContent() {
        showNothingTipsIfNeed();
        showSnackBar(R.string.no_more_articles);
    }

    public void showNothingTipsIfNeed() {
        mNoContentTipsView.setVisibility(mAdapter.isEmpty() ? View.VISIBLE : View.GONE);
    }

    @Override
    public void addArticles(List<ArticleSummary> summaries) {
        if (!mAdapter.containsAll(summaries)) {
            mAdapter.clear();
            mAdapter.addAll(0, summaries);
            showNothingTipsIfNeed();
        } else {
            showNoMoreContent();
        }
    }
}