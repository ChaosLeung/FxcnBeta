package org.chaos.fx.cnbeta.rank;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.chaos.fx.cnbeta.ContentActivity;
import org.chaos.fx.cnbeta.R;
import org.chaos.fx.cnbeta.app.BaseArticleAdapter;
import org.chaos.fx.cnbeta.home.ArticleAdapter;
import org.chaos.fx.cnbeta.net.CnBetaApi;
import org.chaos.fx.cnbeta.net.CnBetaApiHelper;
import org.chaos.fx.cnbeta.net.model.ArticleSummary;
import org.chaos.fx.cnbeta.util.TimeStringHelper;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * @author Chaos
 *         2015/11/17.
 */
public class RankSubFragment extends Fragment {

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

    @Bind(R.id.articles) RecyclerView mArticlesView;

    private RankSubAdapter mArticleAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mType = getArguments().getString(KEY_TYPE);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_rank_sub, container, false);
        ButterKnife.bind(this, rootView);
        mArticlesView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mArticleAdapter = new RankSubAdapter(getActivity(), mArticlesView);
        mArticleAdapter.setOnItemClickListener(new ArticleAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                ArticleSummary summary = mArticleAdapter.getArticles().get(position);
                ContentActivity.start(getActivity(), summary.getSid());
            }
        });
        mArticlesView.setAdapter(mArticleAdapter);
        initArticles();
        return rootView;
    }

    private void initArticles() {
        CnBetaApiHelper.todayRank(mType).enqueue(new Callback<CnBetaApi.Result<List<ArticleSummary>>>() {
            @Override
            public void onResponse(Response<CnBetaApi.Result<List<ArticleSummary>>> response, Retrofit retrofit) {
                List<ArticleSummary> result = response.body().result;
                if (!result.isEmpty()) {
                    mArticleAdapter.addArticles(0, result);
                    mArticleAdapter.notifyItemRangeInserted(0, result.size());
                } else {
                    showSnackBar(R.string.no_more_articles);
                }
            }

            @Override
            public void onFailure(Throwable t) {
                showSnackBar(R.string.load_articles_failed);
            }
        });
    }

    private void showSnackBar(@StringRes int strId) {
        Snackbar.make(mArticlesView, strId, Snackbar.LENGTH_SHORT).show();
    }

    private class RankSubAdapter extends BaseArticleAdapter {

        public RankSubAdapter(Context context, RecyclerView bindView) {
            super(context, bindView);
        }

        @Override
        protected void onBindHolderInternal(ViewHolder holder, int position) {
            ArticleSummary summary = getArticles().get(position);
            String subText = "";
            if (CnBetaApi.TYPE_COUNTER.equals(mType)) {
                subText = TimeStringHelper.getTimeString(summary.getPubtime());
            } else if (CnBetaApi.TYPE_DIG.equals(mType)) {
                subText = getSubText(R.string.read_count, summary.getCounter());
            } else if (CnBetaApi.TYPE_COMMENTS.equals(mType)) {
                subText =getSubText(R.string.comment_count, summary.getComment());
            }
            holder.time.setText(subText);
        }

        private String getSubText(@StringRes int strId, int value) {
            return String.format(mContext.getString(strId), value);
        }
    }
}