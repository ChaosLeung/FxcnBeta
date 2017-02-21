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
import android.support.v4.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.chaos.fx.cnbeta.R;
import org.chaos.fx.cnbeta.app.BaseFragment;
import org.chaos.fx.cnbeta.details.ContentActivity;
import org.chaos.fx.cnbeta.net.model.HotComment;
import org.chaos.fx.cnbeta.widget.BaseAdapter;
import org.chaos.fx.cnbeta.widget.SwipeLinearRecyclerView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author Chaos
 *         2015/11/15.
 */
public class HotCommentFragment extends BaseFragment implements HotCommentContract.View, SwipeLinearRecyclerView.OnRefreshListener {

    public static HotCommentFragment newInstance() {
        return new HotCommentFragment();
    }

    @BindView(R.id.swipe_recycler_view) SwipeLinearRecyclerView mHotCommentView;

    private HotCommentAdapter mHotCommentAdapter;

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

        mHotCommentAdapter = new HotCommentAdapter(getActivity(), mHotCommentView.getRecyclerView());
        mHotCommentAdapter.setOnItemClickListener(new BaseAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                HotComment comment = mHotCommentAdapter.get(position);

                View tv = v.findViewById(R.id.title);
                ActivityOptionsCompat options =
                        ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity(),
                                Pair.create(v, getString(R.string.transition_details_background)),
                                Pair.create(tv, getString(R.string.transition_details_title)));
                ContentActivity.start(getActivity(), comment.getSid(), comment.getSubject(), null, options);
            }
        });

        mHotCommentView.setAdapter(mHotCommentAdapter);

        mHotCommentView.setOnRefreshListener(this);

        mPresenter = new HotCommentPresenter();
        mPresenter.subscribe(this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mPresenter.unsubscribe();
    }

    private void showSnackBar(@StringRes int strId) {
        Snackbar.make(mHotCommentView, strId, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void onRefresh() {
        mPresenter.loadHotComments();
    }

    @Override
    public void showRefreshing(boolean refreshing) {
        mHotCommentView.setRefreshing(refreshing);
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
    public void addComments(List<HotComment> comments) {
        if (!mHotCommentAdapter.containsAll(comments)) {
            mHotCommentAdapter.clear();
            mHotCommentAdapter.addAll(0, comments);
        } else {
            showNoMoreContent();
        }
    }
}
