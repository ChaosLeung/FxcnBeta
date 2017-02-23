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
import android.support.v4.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.chaos.fx.cnbeta.R;
import org.chaos.fx.cnbeta.details.ContentActivity;
import org.chaos.fx.cnbeta.net.MobileApi;
import org.chaos.fx.cnbeta.net.model.ArticleSummary;
import org.chaos.fx.cnbeta.widget.BaseAdapter;
import org.chaos.fx.cnbeta.widget.SwipeLinearRecyclerView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author Chaos
 *         2015/11/17.
 */
public class RankSubFragment extends Fragment implements RankSubContract.View, SwipeLinearRecyclerView.OnRefreshListener {

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

    @BindView(R.id.swipe_recycler_view) SwipeLinearRecyclerView mArticlesView;
    @BindView(R.id.no_content) TextView mNoContentTipsView;

    private RankSubAdapter mArticleAdapter;

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

        mArticleAdapter = new RankSubAdapter(getActivity(), mArticlesView.getRecyclerView(), type);
        mArticleAdapter.setOnItemClickListener(new BaseAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                mPreClickPosition = position;
                ArticleSummary summary = mArticleAdapter.get(position);

                View tv = v.findViewById(R.id.title);
                ActivityOptionsCompat options =
                        ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity(),
                                Pair.create(v, getString(R.string.transition_details_background)),
                                Pair.create(tv, getString(R.string.transition_details_title)));
                ContentActivity.start(getActivity(), summary.getSid(), summary.getTitle(),
                        summary.getTopicLogo(), options);
            }
        });
        mArticlesView.setAdapter(mArticleAdapter);

        mArticlesView.setOnRefreshListener(this);

        mPresenter = new RankSubPresenter(type);
        mPresenter.subscribe(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        mArticleAdapter.notifyItemChanged(mPreClickPosition);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mPresenter.unsubscribe();
    }

    private void showSnackBar(@StringRes int strId) {
        Snackbar.make(mArticlesView, strId, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void onRefresh() {
        mPresenter.loadArticles();
    }

    @Override
    public void showRefreshing(boolean refreshing) {
        mArticlesView.setRefreshing(refreshing);
    }

    @Override
    public void showLoadFailed() {
        showSnackBar(R.string.load_articles_failed);
    }

    @Override
    public void showNoMoreContent() {
        showSnackBar(R.string.no_more_articles);
        showNothingTipsIfNeed();
    }

    public void showNothingTipsIfNeed() {
        mNoContentTipsView.setVisibility(mArticleAdapter.isEmpty() ? View.VISIBLE : View.GONE);
    }

    @Override
    public void addArticles(List<ArticleSummary> summaries) {
        if (!mArticleAdapter.containsAll(summaries)) {
            mArticleAdapter.clear();
            mArticleAdapter.addAll(0, summaries);
        } else {
            showNoMoreContent();
        }
        showNothingTipsIfNeed();
    }
}