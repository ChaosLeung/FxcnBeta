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

package org.chaos.fx.cnbeta.hotcomment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
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
import org.chaos.fx.cnbeta.app.BaseFragment;
import org.chaos.fx.cnbeta.details.ContentActivity;
import org.chaos.fx.cnbeta.net.model.HotComment;
import org.chaos.fx.cnbeta.widget.FxRecyclerView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author Chaos
 *         2015/11/15.
 */
public class HotCommentFragment extends BaseFragment implements HotCommentContract.View,
        SwipeRefreshLayout.OnRefreshListener {

    public static HotCommentFragment newInstance() {
        return new HotCommentFragment();
    }

    @BindView(R.id.swipe) SwipeRefreshLayout mSwipeRefreshLayout;
    @BindView(R.id.recycler_view) FxRecyclerView mRecyclerView;
    @BindView(R.id.no_content) TextView mNoContentTipsView;

    private HotCommentAdapter mAdapter;

    private HotCommentContract.Presenter mPresenter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_hot_comment, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ButterKnife.bind(this, view);

        mAdapter = new HotCommentAdapter();
        mAdapter.openLoadAnimation(BaseQuickAdapter.SLIDEIN_BOTTOM);

        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
        mRecyclerView.addOnItemTouchListener(new OnItemClickListener() {
            @Override
            public void onSimpleItemClick(BaseQuickAdapter adapter, View view, int position) {
                HotComment comment = mAdapter.get(position);

                if (comment.getSid() == 0) {
                    showSnackBar(R.string.error_invalid_sid);
                } else {
                    RecyclerView.ViewHolder holder = mRecyclerView.findViewHolderForAdapterPosition(position);

                    View tv = holder.itemView.findViewById(R.id.title);
                    ActivityOptionsCompat options =
                            ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity(),
                                    Pair.create(holder.itemView, getString(R.string.transition_details_background)),
                                    Pair.create(tv, getString(R.string.transition_details_title)));
                    ContentActivity.start(getActivity(), comment.getSid(), comment.getTitle(), null, options);
                }
            }
        });

        mSwipeRefreshLayout.setColorSchemeColors(
                ResourcesCompat.getColor(getResources(), R.color.colorAccent, getContext().getTheme()));
        mSwipeRefreshLayout.setOnRefreshListener(this);

        mPresenter = new HotCommentPresenter();
        mPresenter.subscribe(this);
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
        mPresenter.unsubscribe();
    }

    private void showSnackBar(@StringRes int strId) {
        Snackbar.make(mRecyclerView, strId, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void onRefresh() {
        mPresenter.loadHotComments();
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

    @Override
    public void addComments(List<HotComment> comments) {
        if (!mAdapter.containsAll(comments)) {
            mAdapter.clear();
            mAdapter.addAll(0, comments);
            showNothingTipsIfNeed();
        } else {
            showNoMoreContent();
        }
    }

    public void showNothingTipsIfNeed() {
        mNoContentTipsView.setVisibility(mAdapter.isEmpty() ? View.VISIBLE : View.GONE);
    }
}
