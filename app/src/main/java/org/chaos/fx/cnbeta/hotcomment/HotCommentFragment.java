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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.chaos.fx.cnbeta.R;
import org.chaos.fx.cnbeta.app.BaseFragment;
import org.chaos.fx.cnbeta.details.ContentActivity;
import org.chaos.fx.cnbeta.net.CnBetaApi;
import org.chaos.fx.cnbeta.net.CnBetaApiHelper;
import org.chaos.fx.cnbeta.net.exception.RequestFailedException;
import org.chaos.fx.cnbeta.net.model.HotComment;
import org.chaos.fx.cnbeta.widget.BaseAdapter;
import org.chaos.fx.cnbeta.widget.SwipeLinearRecyclerView;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * @author Chaos
 *         2015/11/15.
 */
public class HotCommentFragment extends BaseFragment implements SwipeLinearRecyclerView.OnRefreshListener {

    public static HotCommentFragment newInstance() {
        return new HotCommentFragment();
    }

    @Bind(R.id.swipe_recycler_view) SwipeLinearRecyclerView mHotCommentView;

    private HotCommentAdapter mHotCommentAdapter;

    private Subscription mSubscription;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setActionBarTitle(R.string.nav_hot_comments);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.layout_swipe_recycler_view, container, false);
        ButterKnife.bind(this, rootView);

        mHotCommentAdapter = new HotCommentAdapter(getActivity(), mHotCommentView.getRecyclerView());
        mHotCommentAdapter.setOnItemClickListener(new BaseAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                HotComment comment = mHotCommentAdapter.get(position);
                ContentActivity.start(getActivity(), comment.getSid(), null);
            }
        });

        mHotCommentView.setAdapter(mHotCommentAdapter);

        mHotCommentView.setOnRefreshListener(this);
        mHotCommentView.post(new Runnable() {
            @Override
            public void run() {
                mHotCommentView.setRefreshing(true);
                loadHotComments();
            }
        });
        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mSubscription != null) {
            mSubscription.unsubscribe();
        }
    }

    private void loadHotComments() {
        final Subscriber<List<HotComment>> subscriber = new Subscriber<List<HotComment>>() {
            @Override
            public void onCompleted() {
                mHotCommentView.setRefreshing(false);
            }

            @Override
            public void onError(Throwable e) {
                if (isVisible()) {
                    showSnackBar(R.string.load_articles_failed);
                }
                mHotCommentView.setRefreshing(false);
            }

            @Override
            public void onNext(List<HotComment> result) {
                if (!result.isEmpty() && !mHotCommentAdapter.containsAll(result)) {
                    mHotCommentAdapter.clear();
                    mHotCommentAdapter.addAll(0, result);
                } else {
                    showSnackBar(R.string.no_more_articles);
                }
            }
        };
        mSubscription = CnBetaApiHelper.hotComment()
                .subscribeOn(Schedulers.io())
                .map(new Func1<CnBetaApi.Result<List<HotComment>>, List<HotComment>>() {
                    @Override
                    public List<HotComment> call(CnBetaApi.Result<List<HotComment>> listResult) {
                        if (!listResult.isSuccess()) {
                            subscriber.onError(new RequestFailedException());
                        }
                        return listResult.result;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);
    }

    private void showSnackBar(@StringRes int strId) {
        Snackbar.make(mHotCommentView, strId, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void onRefresh() {
        loadHotComments();
    }
}
