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

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.chaos.fx.cnbeta.R;
import org.chaos.fx.cnbeta.app.BaseFragment;
import org.chaos.fx.cnbeta.net.model.Comment;
import org.chaos.fx.cnbeta.net.model.WebCommentResult;
import org.chaos.fx.cnbeta.util.ModelUitl;
import org.chaos.fx.cnbeta.widget.SwipeLinearRecyclerView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author Chaos
 *         4/2/16
 */
public class CommentFragment extends BaseFragment implements
        SwipeLinearRecyclerView.OnRefreshListener, CommentContract.View {

    private static final String KEY_SID = "sid";
    private static final String KEY_COMMENTS = "comments";
    private static final String KEY_TOKEN = "token";
    private static final int ONE_PAGE_COMMENT_COUNT = 10;

    public static CommentFragment newInstance(int sid, WebCommentResult result) {
        Bundle args = new Bundle();
        args.putInt(KEY_SID, sid);
        args.putString(KEY_TOKEN, result.getToken());
        args.putParcelableArrayList(KEY_COMMENTS, ModelUitl.toCommentList(result));
        CommentFragment fragment = new CommentFragment();
        fragment.setArguments(args);
        return fragment;
    }

    private ArrayList<Comment> mComments;
    private OnCommentUpdateListener mOnCommentUpdateListener;

    @BindView(R.id.no_content) TextView mNoContentTipView;

    @BindView(R.id.swipe_recycler_view) SwipeLinearRecyclerView mCommentView;
    private CommentAdapter mCommentAdapter;

    private CommentContract.Presenter mPresenter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPresenter = new CommentPresenter(getArguments().getInt(KEY_SID), getArguments().getString(KEY_TOKEN));
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        setHasOptionsMenu(true);
        mOnCommentUpdateListener = (OnCommentUpdateListener) activity;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_comment, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ButterKnife.bind(this, view);

        mComments = getArguments().getParcelableArrayList(KEY_COMMENTS);

        mCommentAdapter = new CommentAdapter(getActivity(), mCommentView.getRecyclerView());
        mCommentAdapter.addFooterView(
                getActivity().getLayoutInflater().inflate(R.layout.layout_loading, mCommentView, false));
        mCommentAdapter.setOnItemChildClickListener(new CommentAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(View v, int position) {
                Comment c = mCommentAdapter.get(position);
                switch (v.getId()) {
                    case R.id.support:
                        mPresenter.support(c);
                        break;
                    case R.id.against:
                        mPresenter.against(c);
                        break;
                    case R.id.reply:
                        mPresenter.replyComment(c);
                        break;
                }
            }
        });
        mCommentAdapter.addAll(mComments);
        mCommentView.setAdapter(mCommentAdapter);
        mCommentView.setOnRefreshListener(this);
        showNoCommentTipsIfNeed();

        mPresenter.subscribe(this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mPresenter.unsubscribe();
        mCommentAdapter.setOnItemChildClickListener(null);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        if (!hidden) {
            setActionBarTitle(R.string.comment);
        }
    }

    @Override
    public void onRefresh() {
        int size = mComments.size();
        int page = (size / ONE_PAGE_COMMENT_COUNT) + 1;
        mPresenter.refreshComments(page);
    }

    @Override
    public void showCommentDialog(final int pid) {
        final CommentDialog commentDialog = new CommentDialog();
        commentDialog.setPositiveListener(new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String captcha = commentDialog.getCaptcha();
                String comment = commentDialog.getComment();

                if (TextUtils.isEmpty(captcha)) {
                    commentDialog.captchaError(R.string.error_captcha_should_not_empty);
                    return;
                }

                if (TextUtils.isEmpty(comment)) {
                    commentDialog.commentError(R.string.error_comment_should_not_empty);
                    return;
                }

                mPresenter.publishComment(comment, captcha, pid);

                commentDialog.dismiss();
            }
        });
        commentDialog.show(getChildFragmentManager(), "CommentDialog");
    }

    @Override
    public void hideProgress() {
        mCommentView.setRefreshing(false);
    }

    @Override
    public void showNoCommentTipsIfNeed() {
        mNoContentTipView.setVisibility(mCommentAdapter.isEmpty() ? View.VISIBLE : View.GONE);
    }

    private void showSnackBar(@StringRes int strId) {
        showSnackBar(getString(strId));
    }

    private void showSnackBar(CharSequence c) {
        Snackbar.make(mCommentView, c, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.comment_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.add_comment) {
            mPresenter.addComment();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void addComments(List<Comment> comments) {
        int previousSize = mCommentAdapter.listSize();
        for (Comment comment : comments) {
            if (!mCommentAdapter.getList().contains(comment)) {
                mCommentAdapter.add(0, comment);
            }
        }
        if (mCommentAdapter.listSize() != previousSize) {
            mCommentView.getRecyclerView().scrollToPosition(0);
            mOnCommentUpdateListener.onCommentUpdated(mCommentAdapter.listSize());
        } else {
            showNoMoreComments();
        }
    }

    @Override
    public void showLoadingFailed() {
        showSnackBar(R.string.load_articles_failed);
    }

    @Override
    public void showNoMoreComments() {
        showSnackBar(R.string.no_more_comments);
    }

    @Override
    public void showAddCommentSucceed() {
        showSnackBar(R.string.add_comment_succeed);
    }

    @Override
    public void showAddCommentFailed(String error) {
        showSnackBar(String.format(getString(R.string.add_comment_failed_format), error));
    }

    @Override
    public void showOperationFailed() {
        showSnackBar(R.string.operation_failed);
    }

    @Override
    public void notifyItemChanged(Comment c) {
        mCommentAdapter.notifyItemChanged(mCommentAdapter.indexOf(c));
    }

    public interface OnCommentUpdateListener {
        void onCommentUpdated(int count);
    }
}
