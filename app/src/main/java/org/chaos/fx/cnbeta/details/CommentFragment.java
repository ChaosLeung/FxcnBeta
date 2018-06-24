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
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemChildClickListener;

import org.chaos.fx.cnbeta.R;
import org.chaos.fx.cnbeta.app.BaseFragment;
import org.chaos.fx.cnbeta.net.model.Comment;
import org.chaos.fx.cnbeta.preferences.PreferenceHelper;
import org.chaos.fx.cnbeta.widget.FxRecyclerView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author Chaos
 *         4/2/16
 */
public class CommentFragment extends BaseFragment implements
        SwipeRefreshLayout.OnRefreshListener, CommentContract.View {

    private static final String KEY_SID = "sid";

    private static final int ONE_PAGE_COMMENT_COUNT = 10;

    public static CommentFragment newInstance(int sid) {
        Bundle args = new Bundle();
        args.putInt(KEY_SID, sid);
        CommentFragment fragment = new CommentFragment();
        fragment.setArguments(args);
        return fragment;
    }

    private OnCommentUpdateListener mOnCommentUpdateListener;

    @BindView(R.id.no_content) TextView mNoContentTipView;

    @BindView(R.id.swipe) SwipeRefreshLayout mSwipeRefreshLayout;
    @BindView(R.id.recycler_view) FxRecyclerView mRecyclerView;
    private CommentAdapter mAdapter;

    private CommentContract.Presenter mPresenter;

    private String mTmpSN;
    private String mTmpTokenForReadComment;

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

        mAdapter = new CommentAdapter();
        if (PreferenceHelper.getInstance().inAnimationMode()) {
            mAdapter.openLoadAnimation(BaseQuickAdapter.SLIDEIN_BOTTOM);
        }

        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
        mRecyclerView.addOnItemTouchListener(new OnItemChildClickListener() {
            @Override
            public void onSimpleItemChildClick(BaseQuickAdapter adapter, View v, int position) {
                if (!mPresenter.isCommentEnable()) {
                    return;
                }

                Comment c = mAdapter.get(position);
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

        mSwipeRefreshLayout.setColorSchemeColors(
                ResourcesCompat.getColor(getResources(), R.color.colorAccent, getContext().getTheme()));
        mSwipeRefreshLayout.setOnRefreshListener(this);
        showNoCommentTipsIfNeed();

        mPresenter = new CommentPresenter(getArguments().getInt(KEY_SID));
        mPresenter.subscribe(this);
        if (mTmpSN != null && mTmpTokenForReadComment != null) {
            handleSetupMessage(mTmpSN, mTmpTokenForReadComment);
            mTmpSN = null;
            mTmpTokenForReadComment = null;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mPresenter.unsubscribe();
    }

    public void handleSetupMessage(String sn, String tokenForReadComment) {
        if (mPresenter == null) {
            mTmpSN = sn;
            mTmpTokenForReadComment = tokenForReadComment;
        } else {
            mPresenter.setSN(sn);
            mPresenter.setReadCommentToken(tokenForReadComment);
            mPresenter.loadComments();
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        if (!hidden) {
            setActionBarTitle(R.string.comment);
        }
    }

    @Override
    public void onRefresh() {
        int size = mAdapter.size();
        int page = (size / ONE_PAGE_COMMENT_COUNT) + 1;
        mPresenter.refreshComments(page);
    }

    @Override
    public void showCommentDialog(final int pid) {
        final CommentDialog commentDialog = CommentDialog.newInstance(mPresenter.getOperationToken());
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
        mSwipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void showNoCommentTipsIfNeed() {
        mNoContentTipView.setVisibility(mAdapter.isEmpty() ? View.VISIBLE : View.GONE);
    }

    private void showSnackBar(@StringRes int strId) {
        showSnackBar(getString(strId));
    }

    private void showSnackBar(CharSequence c) {
        Snackbar.make(mRecyclerView, c, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // 屏蔽评论菜单
//        inflater.inflate(mPresenter.isCommentEnable() ? R.menu.comment_menu : R.menu.closed_comment_menu, menu);
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
        int previousSize = mAdapter.size();
        for (Comment comment : comments) {
            if (!mAdapter.contains(comment)) {
                mAdapter.add(0, comment);
            }
        }

        if (mAdapter.size() != previousSize) {
            mRecyclerView.scrollToPosition(0);
        } else if (previousSize != 0) {
            showNoMoreComments();
        }

        if (previousSize == 0) {
            getActivity().supportInvalidateOptionsMenu();
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
    public void notifyCommentChanged(Comment c) {
        mAdapter.notifyItemChanged(mAdapter.indexOf(c));
    }

    @Override
    public void updateCommentCount() {
        mOnCommentUpdateListener.onCommentUpdated(mAdapter.size());
    }

    public interface OnCommentUpdateListener {
        void onCommentUpdated(int count);
    }
}
