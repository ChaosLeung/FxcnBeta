package org.chaos.fx.cnbeta;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
public class ArticlesFragment extends Fragment {

    @Bind(R.id.articles) RecyclerView mArticlesView;

    private ArticleAdapter mArticleAdapter;

    private CnBetaApi mCnBetaApi;

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

        mArticlesView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mArticleAdapter = new ArticleAdapter(getActivity());
        mArticlesView.setAdapter(mArticleAdapter);

        CnBetaUtil.articles(mCnBetaApi).enqueue(new Callback<CnBetaApi.Result<List<ArticleSummary>>>() {
            @Override
            public void onResponse(Response<CnBetaApi.Result<List<ArticleSummary>>> response, Retrofit retrofit) {
                mArticleAdapter.addArticles(response.body().result);
                mArticleAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Throwable t) {

            }
        });
        return rootView;
    }
}
