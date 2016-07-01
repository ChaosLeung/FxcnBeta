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
import org.chaos.fx.cnbeta.net.CnBetaApi;
import org.chaos.fx.cnbeta.net.CnBetaApiHelper;
import org.chaos.fx.cnbeta.net.model.ArticleSummary;
import org.chaos.fx.cnbeta.widget.BaseAdapter;
import org.chaos.fx.cnbeta.widget.SwipeLinearRecyclerView;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * @author Chaos
 *         2015/11/14.
 */
public class ArticlesFragment extends BaseFragment
        implements SwipeLinearRecyclerView.OnRefreshListener,
        SwipeLinearRecyclerView.OnLoadMoreListener,
        MainActivity.OnActionBarDoubleClickListener {

    private static final String KEY_TOPIC_ID = "topic_id";

    private static final int STORE_MAX_COUNT = 50;

    @Bind(R.id.swipe_recycler_view)
    SwipeLinearRecyclerView mArticlesView;

    private ArticleAdapter mArticleAdapter;

    private String mTopicId;

    private ArticleCallback mApiCallback = new ArticleCallback();
    private Call<CnBetaApi.Result<List<ArticleSummary>>> mCall;

    private volatile boolean initialized = false;

    public static ArticlesFragment newInstance(String topicId) {
        ArticlesFragment fragment = new ArticlesFragment();
        Bundle bundle = new Bundle();
        bundle.putString(KEY_TOPIC_ID, topicId);
        fragment.setArguments(bundle);
        return fragment;
    }

    private static Handler sHandler = new Handler();

    private static final long RESET_ACTION_BAR_TITLE_DELAY_TIME = 3000;
    private Runnable mResetActionBarTitleRunnable = new Runnable() {
        @Override
        public void run() {
            setActionBarTitle(R.string.nav_home);
        }
    };

    private Realm mRealm;
    private int mPreClickPosition;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setActionBarTitle(R.string.nav_home);
        mTopicId = getArguments().getString(KEY_TOPIC_ID, "null");
        mRealm = Realm.getDefaultInstance();
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

        RealmResults<ArticleSummary> results = mRealm.allObjects(ArticleSummary.class);
        results.sort("mSid", Sort.DESCENDING);
        mArticleAdapter.addAll(0, results);

        mArticlesView.post(new Runnable() {
            @Override
            public void run() {
                mArticlesView.setRefreshing(true);
                mCall = CnBetaApiHelper.articles();
                mCall.enqueue(mApiCallback);
            }
        });
        return rootView;
    }

    @Override
    public void onPause() {
        super.onPause();
        ((MainActivity) getActivity()).removeOnActionBarDoubleClickListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        ((MainActivity) getActivity()).addOnActionBarDoubleClickListener(this);
        mArticleAdapter.notifyItemChanged(mPreClickPosition);
    }

    @Override
    public void onDestroyView() {
        sHandler.removeCallbacks(mResetActionBarTitleRunnable);
        if (mCall != null) {
            mCall.cancel();
        }
        int size = mArticleAdapter.listSize();
        List<ArticleSummary> storeArticles = mArticleAdapter.getList().subList(0, size >= STORE_MAX_COUNT ? STORE_MAX_COUNT : size);
        if (!storeArticles.isEmpty()) {
            mRealm.beginTransaction();
            mRealm.where(ArticleSummary.class).lessThan("mSid", storeArticles.get(storeArticles.size() - 1).getSid()).findAll().clear();
            mRealm.copyToRealmOrUpdate(storeArticles);
            mRealm.commitTransaction();
        }
        super.onDestroyView();
    }

    @Override
    public void onRefresh() {
        if (mArticleAdapter.getItemCount() == 0) {
            mCall = CnBetaApiHelper.topicArticles(mTopicId);
            mCall.enqueue(mApiCallback);
        } else {
            mCall = CnBetaApiHelper.newArticles(
                    mTopicId,
                    mArticleAdapter.get(0).getSid());
            mCall.enqueue(mApiCallback);
        }
    }

    @Override
    public void onLoadMore() {
        mArticleAdapter.getFooterView().setVisibility(View.VISIBLE);
        mArticleAdapter.notifyItemInserted(mArticleAdapter.getItemCount());
        mArticlesView.getRecyclerView().smoothScrollToPosition(mArticleAdapter.getItemCount() - 1);
        mCall = CnBetaApiHelper.oldArticles(
                mTopicId,
                mArticleAdapter.get(mArticleAdapter.getItemCount() - 2).getSid());
        mCall.enqueue(mApiCallback);
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

    private class ArticleCallback implements Callback<CnBetaApi.Result<List<ArticleSummary>>> {

        @Override
        public void onResponse(Call<CnBetaApi.Result<List<ArticleSummary>>> call,
                               Response<CnBetaApi.Result<List<ArticleSummary>>> response) {
            if (response.code() == 200) {
                List<ArticleSummary> result = response.body().result;
                if (!result.isEmpty()) {
                    if (mArticlesView.isRefreshing()) {
                        synchronized (this) {
                            if (!initialized) {
                                initialized = true;
                                mArticleAdapter.clear();
                            }
                        }
                        mArticleAdapter.getList().addAll(0, result);
                        mArticleAdapter.notifyDataSetChanged();
                        mArticlesView.getRecyclerView().scrollToPosition(0);
                    } else {
                        mArticleAdapter.addAll(response.body().result);
                    }
                } else {
                    showSnackBar(R.string.no_more_articles);
                }
            } else {
                showSnackBar(R.string.load_articles_failed);
            }
            resetStatus();
        }

        @Override
        public void onFailure(Call<CnBetaApi.Result<List<ArticleSummary>>> call, Throwable t) {
            if (isVisible()) {
                showSnackBar(R.string.load_articles_failed);
            }
            resetStatus();
        }

        private void resetStatus() {
            mArticlesView.setRefreshing(false);
            mArticlesView.setLoading(false);
            mArticleAdapter.getFooterView().setVisibility(View.GONE);
            mArticleAdapter.notifyItemRemoved(mArticleAdapter.getItemCount());
        }
    }
}
