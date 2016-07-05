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

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.chaos.fx.cnbeta.R;
import org.chaos.fx.cnbeta.details.ContentActivity;
import org.chaos.fx.cnbeta.net.CnBetaApi;
import org.chaos.fx.cnbeta.net.CnBetaApiHelper;
import org.chaos.fx.cnbeta.net.exception.RequestFailedException;
import org.chaos.fx.cnbeta.net.model.ArticleSummary;
import org.chaos.fx.cnbeta.util.TimeStringHelper;
import org.chaos.fx.cnbeta.widget.BaseAdapter;
import org.chaos.fx.cnbeta.widget.BaseArticleAdapter;
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
 *         2015/11/17.
 */
public class RankSubFragment extends Fragment implements SwipeLinearRecyclerView.OnRefreshListener {

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

    @Bind(R.id.swipe_recycler_view) SwipeLinearRecyclerView mArticlesView;

    private RankSubAdapter mArticleAdapter;

    private Subscription mSubscription;

    private int mPreClickPosition;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mType = getArguments().getString(KEY_TYPE);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.layout_swipe_recycler_view, container, false);
        ButterKnife.bind(this, rootView);

        mArticleAdapter = new RankSubAdapter(getActivity(), mArticlesView.getRecyclerView());
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
        mArticlesView.post(new Runnable() {
            @Override
            public void run() {
                mArticlesView.setRefreshing(true);
                initArticles();
            }
        });
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        mArticleAdapter.notifyItemChanged(mPreClickPosition);
    }

    @SuppressLint("WrongConstant")
    private void initArticles() {
        mSubscription = CnBetaApiHelper.todayRank(mType)
                .subscribeOn(Schedulers.io())
                .map(new Func1<CnBetaApi.Result<List<ArticleSummary>>, List<ArticleSummary>>() {
                    @Override
                    public List<ArticleSummary> call(CnBetaApi.Result<List<ArticleSummary>> listResult) {
                        if (!listResult.isSuccess()) {
                            throw new RequestFailedException();
                        }
                        return listResult.result;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<List<ArticleSummary>>() {
                    @Override
                    public void onCompleted() {
                        mArticlesView.setRefreshing(false);
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (isVisible()) {
                            showSnackBar(R.string.load_articles_failed);
                        }
                        mArticlesView.setRefreshing(false);
                    }

                    @Override
                    public void onNext(List<ArticleSummary> result) {
                        if (!result.isEmpty() && !mArticleAdapter.containsAll(result)) {
                            mArticleAdapter.clear();
                            mArticleAdapter.addAll(0, result);
                        } else {
                            showSnackBar(R.string.no_more_articles);
                        }
                    }
                });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mSubscription != null) {
            mSubscription.unsubscribe();
        }
    }

    private void showSnackBar(@StringRes int strId) {
        Snackbar.make(mArticlesView, strId, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void onRefresh() {
        initArticles();
    }

    private class RankSubAdapter extends BaseArticleAdapter {

        public RankSubAdapter(Context context, RecyclerView bindView) {
            super(context, bindView);
        }

        @Override
        protected void onBindHolderInternal(ArticleHolder holder, int position) {
            super.onBindHolderInternal(holder, position);
            ArticleSummary summary = get(position);
            String subText = "";
            if (CnBetaApi.TYPE_COUNTER.equals(mType)) {
                subText = TimeStringHelper.getTimeString(summary.getPublishTime());
            } else if (CnBetaApi.TYPE_DIG.equals(mType)) {
                subText = getSubText(R.string.read_count, summary.getCounter());
            } else if (CnBetaApi.TYPE_COMMENTS.equals(mType)) {
                subText = getSubText(R.string.comment_count, summary.getComment());
            }
            holder.summary.setText(subText);
        }

        private String getSubText(@StringRes int strId, int value) {
            return String.format(getContext().getString(strId), value);
        }
    }
}