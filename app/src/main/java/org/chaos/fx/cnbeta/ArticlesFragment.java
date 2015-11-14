package org.chaos.fx.cnbeta;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import org.chaos.fx.cnbeta.net.CnBetaApi;
import org.chaos.fx.cnbeta.net.CnBetaUtil;
import org.chaos.fx.cnbeta.net.model.ArticleSummary;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * @author Chaos
 *         2015/11/14.
 */
public class ArticlesFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    @Bind(R.id.articles) RecyclerView mArticlesView;
    @Bind(R.id.swipe) SwipeRefreshLayout mSwipeLayout;
    @Bind(R.id.progress) ProgressBar mProgressBar;

    private LinearLayoutManager mLayoutManager;
    private ArticleAdapter mArticleAdapter;

    private CnBetaApi mCnBetaApi;

    private boolean isLoading = true;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCnBetaApi = ((MainActivity) getActivity()).getCnBetaApi();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_articles, container, false);
        ButterKnife.bind(this, rootView);

        mLayoutManager = new LinearLayoutManager(getActivity());
        mArticlesView.setLayoutManager(mLayoutManager);
        mArticlesView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (!isLoading) {
                    if (mLayoutManager.findLastVisibleItemPosition() + 1 == mLayoutManager.getItemCount() && dy > 0) {
                        onLoadMore();
                    }
                }
            }
        });
        mArticleAdapter = new ArticleAdapter(getActivity(), mArticlesView);
        mArticleAdapter.setOnItemClickListener(new ArticleAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {

            }
        });
        mArticlesView.setAdapter(mArticleAdapter);

        mSwipeLayout.setOnRefreshListener(this);
        initArticles();
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mSwipeLayout.setRefreshing(true);
    }

    private void initArticles() {
        CnBetaUtil.articles(mCnBetaApi).enqueue(new Callback<CnBetaApi.Result<List<ArticleSummary>>>() {
            @Override
            public void onResponse(Response<CnBetaApi.Result<List<ArticleSummary>>> response, Retrofit retrofit) {
                List<ArticleSummary> result = response.body().result;
                mArticleAdapter.addArticles(result);
                mArticleAdapter.notifyItemRangeInserted(0, result.size());
                isLoading = false;
                mSwipeLayout.setRefreshing(false);
            }

            @Override
            public void onFailure(Throwable t) {
                isLoading = false;
                mSwipeLayout.setRefreshing(false);
                showSnackBar(R.string.load_articles_failed);
            }
        });
    }

    @Override
    public void onRefresh() {
        if (!isLoading) {
            isLoading = true;
            CnBetaUtil
                    .newArticles(
                            mCnBetaApi,
                            "null",
                            mArticleAdapter.getArticles().get(0).getSid())
                    .enqueue(new Callback<CnBetaApi.Result<List<ArticleSummary>>>() {
                        @Override
                        public void onResponse(Response<CnBetaApi.Result<List<ArticleSummary>>> response,
                                               Retrofit retrofit) {
                            List<ArticleSummary> result = response.body().result;
                            if (result.size() > 0) {
                                mArticleAdapter.addArticles(0, result);
                                mArticleAdapter.notifyItemRangeInserted(0, result.size());
                            } else {
                                showSnackBar(R.string.no_more_articles);
                            }
                            isLoading = false;
                            mSwipeLayout.setRefreshing(false);
                        }

                        @Override
                        public void onFailure(Throwable t) {
                            isLoading = false;
                            mSwipeLayout.setRefreshing(false);
                            showSnackBar(R.string.load_articles_failed);
                        }
                    });
        }
    }

    public void onLoadMore() {
        if (!isLoading) {
            isLoading = true;
            mProgressBar.setVisibility(View.VISIBLE);
            CnBetaUtil
                    .oldArticles(
                            mCnBetaApi,
                            "null",
                            mArticleAdapter.getArticles().get(mArticleAdapter.getItemCount() - 1).getSid())
                    .enqueue(new Callback<CnBetaApi.Result<List<ArticleSummary>>>() {
                        @Override
                        public void onResponse(Response<CnBetaApi.Result<List<ArticleSummary>>> response,
                                               Retrofit retrofit) {
                            List<ArticleSummary> result = response.body().result;
                            if (result.size() > 0) {
                                int preSize = mArticleAdapter.getItemCount();
                                mArticleAdapter.addArticles(response.body().result);
                                mArticleAdapter.notifyItemRangeInserted(preSize, result.size());
                            } else {
                                showSnackBar(R.string.no_more_articles);
                            }
                            isLoading = false;
                            mProgressBar.setVisibility(View.GONE);
                        }

                        @Override
                        public void onFailure(Throwable t) {
                            isLoading = false;
                            showSnackBar(R.string.load_articles_failed);
                            mProgressBar.setVisibility(View.GONE);
                        }
                    });
        }
    }

    private void showSnackBar(@StringRes int strId) {
        Snackbar.make(mSwipeLayout, strId, Snackbar.LENGTH_SHORT).show();
    }
}
