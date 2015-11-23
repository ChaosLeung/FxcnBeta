package org.chaos.fx.cnbeta.hotcomment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.chaos.fx.cnbeta.ContentActivity;
import org.chaos.fx.cnbeta.R;
import org.chaos.fx.cnbeta.app.BaseFragment;
import org.chaos.fx.cnbeta.net.CnBetaApi;
import org.chaos.fx.cnbeta.net.CnBetaApiHelper;
import org.chaos.fx.cnbeta.net.model.HotComment;
import org.chaos.fx.cnbeta.widget.BaseAdapter;
import org.chaos.fx.cnbeta.widget.SwipeLinearRecyclerView;

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
public class HotCommentFragment extends BaseFragment implements SwipeLinearRecyclerView.OnRefreshListener{

    public static HotCommentFragment newInstance() {
        return new HotCommentFragment();
    }

    @Bind(R.id.swipe_recycler_view) SwipeLinearRecyclerView mHotCommentView;

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
                    mHotCommentAdapter.clear();
                    mHotCommentAdapter.addAll(0, result);
                    mHotCommentAdapter.notifyItemRangeInserted(0, result.size());
                } else {
                    showSnackBar(R.string.no_more_articles);
                }
                mHotCommentView.setRefreshing(false);
            }

            @Override
            public void onFailure(Throwable t) {
                if (isVisible()) {
                    showSnackBar(R.string.load_articles_failed);
                }
                mHotCommentView.setRefreshing(false);
            }
        });
    }

    private void showSnackBar(@StringRes int strId) {
        Snackbar.make(mHotCommentView, strId, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void onRefresh() {
        loadHotComments();
    }
}