/*
 * Copyright 2016 Chaos
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

package org.chaos.fx.cnbeta.details;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.chaos.fx.cnbeta.R;
import org.chaos.fx.cnbeta.app.BaseFragment;
import org.chaos.fx.cnbeta.net.CnBetaApi;
import org.chaos.fx.cnbeta.net.CnBetaApiHelper;
import org.chaos.fx.cnbeta.net.model.Comment;
import org.chaos.fx.cnbeta.widget.BaseAdapter;
import org.chaos.fx.cnbeta.widget.SwipeLinearRecyclerView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * @author Chaos
 *         4/2/16
 */
public class CommentFragment extends BaseFragment implements
        SwipeLinearRecyclerView.OnLoadMoreListener {

    private static final int ONE_PAGE_COMMENT_COUNT = 20;

    private static final String KEY_SID = "sid";

    public static CommentFragment newInstance(int sid) {
        Bundle args = new Bundle();
        args.putInt(KEY_SID, sid);
        CommentFragment fragment = new CommentFragment();
        fragment.setArguments(args);
        return fragment;
    }

    private int mSid;

    @Bind(R.id.no_content)
    TextView mNoContentTipView;

    @Bind(R.id.swipe_recycler_view)
    SwipeLinearRecyclerView mCommentView;
    private CommentAdapter mCommentAdapter;

    private Call<CnBetaApi.Result<List<Comment>>> mCommentCall;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_comment, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ButterKnife.bind(this, view);

        mSid = getArguments().getInt(KEY_SID);

        mCommentAdapter = new CommentAdapter(getActivity(), mCommentView.getRecyclerView());
        mCommentAdapter.addFooterView(
                getActivity().getLayoutInflater().inflate(R.layout.layout_loading, mCommentView, false));
        mCommentAdapter.setOnItemClickListener(new BaseAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                mCommentView.showContextMenuForChild(v);
            }
        });
        mCommentView.setAdapter(mCommentAdapter);
        mCommentView.setOnLoadMoreListener(this);
        mCommentView.setShowLoadingBar(false);
        onLoadMore();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mCommentCall != null) {
            mCommentCall.cancel();
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        if (!hidden){
            setActionBarTitle(R.string.comment);
        }
    }

    @Override
    public void onLoadMore() {
        int size = mCommentAdapter.getList().size();
        mCommentAdapter.getFooterView().setVisibility(View.VISIBLE);
        mCommentAdapter.notifyItemInserted(mCommentAdapter.getItemCount());
        mCommentView.getRecyclerView().smoothScrollToPosition(mCommentAdapter.getItemCount() - 1);
        if (size % ONE_PAGE_COMMENT_COUNT == 0) {
            loadComments(size / ONE_PAGE_COMMENT_COUNT + 1);
        } else {
            loadComments(size / ONE_PAGE_COMMENT_COUNT);
        }
    }

    private void loadComments(int page) {
        mCommentCall = CnBetaApiHelper.comments(mSid, page);
        mCommentCall.enqueue(new Callback<CnBetaApi.Result<List<Comment>>>() {
            @Override
            public void onResponse(Call<CnBetaApi.Result<List<Comment>>> call,
                                   Response<CnBetaApi.Result<List<Comment>>> response) {
                if (response.code() == 200) {
                    List<Comment> result = response.body().result;
                    if (!result.isEmpty()) {
                        int currentSize = mCommentAdapter.listSize();
                        if (currentSize % ONE_PAGE_COMMENT_COUNT != 0) {
                            mCommentAdapter.removeAll(
                                    new ArrayList<>(mCommentAdapter.subList(currentSize - currentSize % ONE_PAGE_COMMENT_COUNT, currentSize)));
                        }
                        // HeaderView 太高时，调用 notifyItemInserted 相关方法
                        // 会导致 RecyclerView 跳转到奇怪的位置
                        mCommentAdapter.getList().addAll(result);
                        mCommentAdapter.notifyDataSetChanged();
                    } else {
                        showSnackBar(R.string.no_more_comments);
                    }
                } else {
                    showSnackBar(R.string.load_articles_failed);
                }
                hideProgress();
                hideOrShowTip();
            }

            @Override
            public void onFailure(Call<CnBetaApi.Result<List<Comment>>> call, Throwable t) {
                showSnackBar(R.string.load_articles_failed);
                hideProgress();
                hideOrShowTip();
            }
        });
    }

    private void hideProgress() {
        mCommentView.setLoading(false);
        if (mCommentAdapter.getFooterView().getVisibility() == View.VISIBLE) {
            mCommentAdapter.getFooterView().setVisibility(View.GONE);
            mCommentAdapter.notifyItemRemoved(mCommentAdapter.getItemCount());
        }
    }

    private void hideOrShowTip() {
        mNoContentTipView.setVisibility(mCommentAdapter.isEmpty() ? View.VISIBLE : View.GONE);
    }

    private void showSnackBar(@StringRes int strId) {
        Snackbar.make(mCommentView, strId, Snackbar.LENGTH_SHORT).show();
    }
}
