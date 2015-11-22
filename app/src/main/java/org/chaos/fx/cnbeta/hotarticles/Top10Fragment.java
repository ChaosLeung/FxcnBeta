package org.chaos.fx.cnbeta.hotarticles;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.chaos.fx.cnbeta.ContentActivity;
import org.chaos.fx.cnbeta.R;
import org.chaos.fx.cnbeta.app.BaseFragment;
import org.chaos.fx.cnbeta.app.DividerItemDecoration;
import org.chaos.fx.cnbeta.home.ArticleAdapter;
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
 *         2015/11/15.
 */
public class Top10Fragment extends BaseFragment {

    @Bind(R.id.articles) RecyclerView mTop10View;

    private Top10Adapter mTop10Adapter;

    private Call<CnBetaApi.Result<List<ArticleSummary>>> mCall;

    public static Top10Fragment newInstance() {
        return new Top10Fragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setActionBarTitle(R.string.nav_hot_articles);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_top10, container, false);
        ButterKnife.bind(this, rootView);
        mTop10View.setLayoutManager(new LinearLayoutManager(getActivity()));
        mTop10View.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST));

        mTop10Adapter = new Top10Adapter(getActivity(), mTop10View);
        mTop10Adapter.setOnItemClickListener(new ArticleAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                ArticleSummary summary = mTop10Adapter.getArticles().get(position);
                ContentActivity.start(getActivity(), summary.getSid(),summary.getTopicLogo());
            }
        });
        mTop10View.setAdapter(mTop10Adapter);
        loadTop10Articles();
        return rootView;
    }

    @Override
    public void onDestroyView() {
        mCall.cancel();
        super.onDestroyView();
    }

    private void loadTop10Articles() {
        mCall = CnBetaApiHelper.top10();
        mCall.enqueue(new Callback<CnBetaApi.Result<List<ArticleSummary>>>() {
            @Override
            public void onResponse(Response<CnBetaApi.Result<List<ArticleSummary>>> response, Retrofit retrofit) {
                List<ArticleSummary> result = response.body().result;
                if (!result.isEmpty()) {
                    mTop10Adapter.addArticles(0, result);
                    mTop10Adapter.notifyItemRangeInserted(0, result.size());
                } else {
                    showSnackBar(R.string.no_more_articles);
                }
            }

            @Override
            public void onFailure(Throwable t) {
                if (isVisible()) {
                    showSnackBar(R.string.load_articles_failed);
                }
            }
        });
    }

    private void showSnackBar(@StringRes int strId) {
        Snackbar.make(mTop10View, strId, Snackbar.LENGTH_SHORT).show();
    }
}
