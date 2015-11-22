package org.chaos.fx.cnbeta.hotcomment;

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
import org.chaos.fx.cnbeta.net.CnBetaApi;
import org.chaos.fx.cnbeta.net.CnBetaApiHelper;
import org.chaos.fx.cnbeta.net.model.HotComment;

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
public class HotCommentFragment extends BaseFragment {

    public static HotCommentFragment newInstance() {
        return new HotCommentFragment();
    }

    @Bind(R.id.hot_comments) RecyclerView mHotCommentView;

    private HotCommentAdapter mHotCommentAdapter;

    private Call<CnBetaApi.Result<List<HotComment>>> mCall;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setActionBarTitle(R.string.nav_hot_comments);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_hot_comment, container, false);
        ButterKnife.bind(this, rootView);

        mHotCommentView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mHotCommentView.addItemDecoration(
                new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST));

        mHotCommentAdapter = new HotCommentAdapter(getActivity(), mHotCommentView);
        mHotCommentAdapter.setOnItemClickListener(new HotCommentAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                HotComment comment = mHotCommentAdapter.getComments().get(position);
                ContentActivity.start(getActivity(), comment.getSid(), null);
            }
        });

        mHotCommentView.setAdapter(mHotCommentAdapter);
        loadHotComments();
        return rootView;
    }

    @Override
    public void onDestroyView() {
        mCall.cancel();
        super.onDestroyView();
    }

    private void loadHotComments() {
        mCall = CnBetaApiHelper.hotComment();
        mCall.enqueue(new Callback<CnBetaApi.Result<List<HotComment>>>() {
            @Override
            public void onResponse(Response<CnBetaApi.Result<List<HotComment>>> response, Retrofit retrofit) {
                List<HotComment> result = response.body().result;
                if (!result.isEmpty()) {
                    mHotCommentAdapter.getComments().addAll(0, result);
                    mHotCommentAdapter.notifyItemRangeInserted(0, result.size());
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
        Snackbar.make(mHotCommentView, strId, Snackbar.LENGTH_SHORT).show();
    }
}
