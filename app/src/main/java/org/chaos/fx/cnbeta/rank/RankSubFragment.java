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
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.chaos.fx.cnbeta.R;
import org.chaos.fx.cnbeta.details.ContentActivity;
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
     * {@code type} 应为 {@link org.chaos.fx.cnbeta.net.CnBetaApi.RankType}
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

    private String mType;

    @BindView(R.id.swipe_recycler_view) SwipeLinearRecyclerView mArticlesView;

    private RankSubAdapter mArticleAdapter;

    private int mPreClickPosition;
    private RankSubContract.Presenter mPresenter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mType = getArguments().getString(KEY_TYPE);
        mPresenter = new RankSubPresenter(mType);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.layout_swipe_recycler_view, container, false);
        ButterKnife.bind(this, rootView);

        mArticleAdapter = new RankSubAdapter(getActivity(), mArticlesView.getRecyclerView(), mType);
        mArticleAdapter.setOnItemClickListener(new BaseAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                mPreClickPosition = position;
                ArticleSummary summary = mArticleAdapter.get(position);
                ContentActivity.start(getActivity(), summary.getSid(), summary.getTopicLogo());
            }
        });
        mArticlesView.setAdapter(mArticleAdapter);

        mArticlesView.setOnRefreshListener(this);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
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
    }

    @Override
    public void addArticles(List<ArticleSummary> summaries) {
        if (!mArticleAdapter.containsAll(summaries)) {
            mArticleAdapter.clear();
            mArticleAdapter.addAll(0, summaries);
        } else {
            showNoMoreContent();
        }
    }
}