package org.chaos.fx.cnbeta.home;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import org.chaos.fx.cnbeta.ContentActivity;
import org.chaos.fx.cnbeta.R;
import org.chaos.fx.cnbeta.app.BaseFragment;
import org.chaos.fx.cnbeta.net.CnBetaApi;
import org.chaos.fx.cnbeta.net.CnBetaApiHelper;
import org.chaos.fx.cnbeta.net.model.ArticleSummary;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * @author Chaos
 *         2015/11/14.
 */
public class ArticlesFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener {

    private static final String KEY_TOPIC_ID = "topic_id";

    @Bind(R.id.articles) RecyclerView mArticlesView;
    @Bind(R.id.swipe) SwipeRefreshLayout mSwipeLayout;
    @Bind(R.id.progress) ProgressBar mProgressBar;

    private LinearLayoutManager mLayoutManager;
    private ArticleAdapter mArticleAdapter;

    private boolean isLoading = true;

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
        getSupportActionBar().setTitle(R.string.nav_home);
        mTopicId = getArguments().getString(KEY_TOPIC_ID, "null");
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
                    if (mLayoutManager.findLastVisibleItemPosition() + 1
                            == mLayoutManager.getItemCount() && dy > 0) {
                        onLoadMore();
                    }
                }
            }
        });
        mArticleAdapter = new ArticleAdapter(getActivity(), mArticlesView);
        mArticleAdapter.setOnItemClickListener(new ArticleAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                ArticleSummary summary = mArticleAdapter.getArticles().get(position);
                ContentActivity.start(getActivity(), summary.getSid());
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
        mSwipeLayout.post(new Runnable() {
            @Override
            public void run() {
                mSwipeLayout.setRefreshing(true);
            }
        });
    }

    @Override
    public void onDestroyView() {
        mCall.cancel();
        clearSwipeLayout();
        super.onDestroyView();
    }

    /**
     * refreshing 的时候切换 fragment 会导致当前 fragment 的视图覆盖在新 fragment 之上，这是其中一个解决方案
     * 更多可以查看 <a href="http://t.cn/RUntUGt">StackOverFlow<a/>
     */
    private void clearSwipeLayout() {
        mSwipeLayout.setRefreshing(false);
        mSwipeLayout.destroyDrawingCache();
        mSwipeLayout.clearAnimation();
    }

    private void initArticles() {
        mCall = CnBetaApiHelper.topicArticles(mTopicId);
        mCall.enqueue(mApiCallback);
    }

    @Override
    public void onRefresh() {
        if (!isLoading) {
            isLoading = true;
            if (mArticleAdapter.getItemCount() == 0) {
                initArticles();
            } else {
                mCall = CnBetaApiHelper.newArticles(
                        mTopicId,
                        mArticleAdapter.getArticles().get(0).getSid());
                mCall.enqueue(mApiCallback);
            }
        }
    }

    public void onLoadMore() {
        if (!isLoading) {
            isLoading = true;
            mProgressBar.setVisibility(View.VISIBLE);
            mCall = CnBetaApiHelper.oldArticles(
                    mTopicId,
                    mArticleAdapter.getArticles().get(mArticleAdapter.getItemCount() - 1).getSid());
            mCall.enqueue(mApiCallback);
        }
    }

    private void showSnackBar(@StringRes int strId) {
        Snackbar.make(mSwipeLayout, strId, Snackbar.LENGTH_SHORT).show();
    }

    private class ArticleCallback implements Callback<CnBetaApi.Result<List<ArticleSummary>>> {

        @Override
        public void onResponse(Response<CnBetaApi.Result<List<ArticleSummary>>> response,
                               Retrofit retrofit) {
            List<ArticleSummary> result = response.body().result;
            if (!result.isEmpty()) {
                if (mSwipeLayout.isRefreshing()) {
                    mArticleAdapter.addArticles(0, result);
                    mArticleAdapter.notifyItemRangeInserted(0, result.size());
                } else {
                    int preSize = mArticleAdapter.getItemCount();
                    mArticleAdapter.addArticles(response.body().result);
                    mArticleAdapter.notifyItemRangeInserted(preSize, result.size());
                }
            } else {
                showSnackBar(R.string.no_more_articles);
            }
            resetStatus();
        }

        @Override
        public void onFailure(Throwable t) {
            if (isVisible()) {
                showSnackBar(R.string.load_articles_failed);
            }
            resetStatus();
        }

        private void resetStatus() {
            isLoading = false;
            mSwipeLayout.setRefreshing(false);
            mProgressBar.setVisibility(View.GONE);
        }
    }
}
