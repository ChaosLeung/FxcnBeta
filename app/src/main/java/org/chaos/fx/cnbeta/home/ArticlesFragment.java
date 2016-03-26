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
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.chaos.fx.cnbeta.ContentActivity;
import org.chaos.fx.cnbeta.MainActivity;
import org.chaos.fx.cnbeta.R;
import org.chaos.fx.cnbeta.app.BaseFragment;
import org.chaos.fx.cnbeta.net.CnBetaApi;
import org.chaos.fx.cnbeta.net.CnBetaApiHelper;
import org.chaos.fx.cnbeta.net.model.ArticleSummary;
import org.chaos.fx.cnbeta.widget.BaseAdapter;
import org.chaos.fx.cnbeta.widget.SwipeLinearRecyclerView;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
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

    @Bind(R.id.swipe_recycler_view)
    SwipeLinearRecyclerView mArticlesView;

    private ArticleAdapter mArticleAdapter;

    private String mTopicId;

    private ArticleCallback mApiCallback = new ArticleCallback();
    private Call<CnBetaApi.Result<List<ArticleSummary>>> mCall;

    public static ArticlesFragment newInstance(String topicId) {
        ArticlesFragment fragment = new ArticlesFragment();
        Bundle bundle = new Bundle();
        bundle.putString(KEY_TOPIC_ID, topicId);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setActionBarTitle(R.string.nav_home);
        mTopicId = getArguments().getString(KEY_TOPIC_ID, "null");
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
                ArticleSummary summary = mArticleAdapter.get(position);
                ContentActivity.start(getActivity(), summary.getSid(), summary.getTopicLogo());
            }
        });
        mArticlesView.setAdapter(mArticleAdapter);

        mArticlesView.setOnRefreshListener(this);
        mArticlesView.setOnLoadMoreListener(this);
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
    public void onPause() {
        super.onPause();
        ((MainActivity) getActivity()).removeOnActionBarDoubleClickListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        ((MainActivity) getActivity()).addOnActionBarDoubleClickListener(this);
    }

    @Override
    public void onDestroyView() {
        if (mCall != null) {
            mCall.cancel();
        }
        super.onDestroyView();
    }

    private void initArticles() {
        mCall = CnBetaApiHelper.topicArticles(mTopicId);
        mCall.enqueue(mApiCallback);
    }

    @Override
    public void onRefresh() {
        if (mArticleAdapter.getItemCount() == 0) {
            initArticles();
        } else {
            mCall = CnBetaApiHelper.newArticles(
                    mTopicId,
                    mArticleAdapter.get(0).getSid());
            mCall.enqueue(mApiCallback);
        }
    }

    @Override
    public void onLoadMore() {
        mCall = CnBetaApiHelper.oldArticles(
                mTopicId,
                mArticleAdapter.get(mArticleAdapter.getItemCount() - 1).getSid());
        mCall.enqueue(mApiCallback);
    }

    private void showSnackBar(@StringRes int strId) {
        Snackbar.make(mArticlesView, strId, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void onActionBarDoubleClick() {
        mArticlesView.getRecyclerView().scrollToPosition(0);
    }

    private class ArticleCallback implements Callback<CnBetaApi.Result<List<ArticleSummary>>> {

        @Override
        public void onResponse(Call<CnBetaApi.Result<List<ArticleSummary>>> call,
                               Response<CnBetaApi.Result<List<ArticleSummary>>> response) {
            List<ArticleSummary> result = response.body().result;
            if (!result.isEmpty()) {
                if (mArticlesView.isRefreshing()) {
                    mArticleAdapter.addAll(0, result);
                } else {
                    mArticleAdapter.addAll(response.body().result);
                }
            } else {
                showSnackBar(R.string.no_more_articles);
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
        }
    }
}
