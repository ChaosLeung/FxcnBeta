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
import org.chaos.fx.cnbeta.net.CnBetaApiHelper;
import org.chaos.fx.cnbeta.net.WebApi;
import org.chaos.fx.cnbeta.net.exception.RequestFailedException;
import org.chaos.fx.cnbeta.net.exception.RequestRateLimitingException;
import org.chaos.fx.cnbeta.net.model.Comment;
import org.chaos.fx.cnbeta.net.model.WebCommentResult;
import org.chaos.fx.cnbeta.util.ModelUitl;
import org.chaos.fx.cnbeta.widget.SwipeLinearRecyclerView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * @author Chaos
 *         4/2/16
 */
public class CommentFragment extends BaseFragment implements
        SwipeLinearRecyclerView.OnRefreshListener {

    private static final String KEY_SID = "sid";
    private static final String KEY_COMMENTS = "comments";

    public static CommentFragment newInstance(int sid, WebCommentResult result) {
        Bundle args = new Bundle();
        args.putInt(KEY_SID, sid);
        args.putParcelableArrayList(KEY_COMMENTS, ModelUitl.toCommentList(result));
        CommentFragment fragment = new CommentFragment();
        fragment.setArguments(args);
        return fragment;
    }

    private int mSid;
    private ArrayList<Comment> mComments;
    private OnCommentUpdateListener mOnCommentUpdateListener;

    @Bind(R.id.no_content) TextView mNoContentTipView;

    @Bind(R.id.swipe_recycler_view) SwipeLinearRecyclerView mCommentView;
    private CommentAdapter mCommentAdapter;

    private Subscription mCommentSubscription;
    private Subscription mSupportSubscription;
    private Subscription mAgainstSubscription;
    private Subscription mReplySubscription;

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

        mSid = getArguments().getInt(KEY_SID);
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
                        support(c);
                        break;
                    case R.id.against:
                        against(c);
                        break;
                    case R.id.reply:
                        replyComment(c);
                        break;
                }
            }
        });
        mCommentAdapter.addAll(mComments);
        mCommentView.setAdapter(mCommentAdapter);
        mCommentView.setOnRefreshListener(this);
        hideOrShowTip();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mSupportSubscription != null) {
            mSupportSubscription.unsubscribe();
        }
        if (mAgainstSubscription != null) {
            mAgainstSubscription.unsubscribe();
        }
        if (mCommentSubscription != null) {
            mCommentSubscription.unsubscribe();
        }
        if (mReplySubscription != null) {
            mReplySubscription.unsubscribe();
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
        refreshComments();
    }

    private void refreshComments() {
        mCommentSubscription = CnBetaApiHelper.getComment(mSid)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<List<Comment>>() {
                    @Override
                    public void onCompleted() {
                        hideProgress();
                        hideOrShowTip();
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (e instanceof RequestRateLimitingException) {
                            showSnackBar(R.string.request_rate_limiting);
                        } else {
                            showSnackBar(R.string.load_articles_failed);
                        }
                        hideProgress();
                        hideOrShowTip();
                    }

                    @Override
                    public void onNext(List<Comment> comments) {
                        mCommentAdapter.clear();
                        mCommentAdapter.addAll(comments);
                        mOnCommentUpdateListener.onCommentUpdated(comments.size());
                    }
                });
    }

    private String getToken() {
        return ((ContentActivity) getActivity()).getToken();
    }

    private void support(final Comment c) {
        mSupportSubscription = CnBetaApiHelper.supportComment(getToken(), mSid, c.getTid())
                .map(new Func1<WebApi.Result, WebApi.Result>() {
                    @Override
                    public WebApi.Result call(WebApi.Result result) {
                        if (!result.isSuccess()) {
                            throw new RequestFailedException();
                        }
                        return result;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<WebApi.Result>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        showSnackBar(R.string.operation_failed);
                    }

                    @Override
                    public void onNext(WebApi.Result result) {
                        c.setSupport(c.getSupport() + 1);
                        mCommentAdapter.notifyItemChanged(mCommentAdapter.indexOf(c));
                    }
                });
    }

    private void against(final Comment c) {
        final Subscriber<WebApi.Result> subscriber = new Subscriber<WebApi.Result>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                showSnackBar(R.string.operation_failed);
            }

            @Override
            public void onNext(WebApi.Result result) {
                c.setAgainst(c.getAgainst() + 1);
                mCommentAdapter.notifyItemChanged(mCommentAdapter.indexOf(c));
            }
        };
        mAgainstSubscription = CnBetaApiHelper.againstComment(getToken(), mSid, c.getTid())
                .subscribeOn(Schedulers.io())
                .map(new Func1<WebApi.Result, WebApi.Result>() {
                    @Override
                    public WebApi.Result call(WebApi.Result result) {
                        if (!result.isSuccess()) {
                            throw new RequestFailedException();
                        }
                        return result;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);
    }

    private void replyComment(final Comment c) {
        showCommentDialog(c.getTid());
    }

    private void addComment() {
        showCommentDialog(0);
    }

    /**
     * 显示评论 dialog, 给用户添加/回复评论
     *
     * @param pid 对应评论的 id, 若为 0, 则为添加评论
     */
    private void showCommentDialog(final int pid) {
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

                publishComment(comment, captcha, pid);

                commentDialog.dismiss();
            }
        });
        commentDialog.show(getChildFragmentManager(), "CommentDialog");
    }

    private void publishComment(String content, String captcha, int pid) {
        mReplySubscription = CnBetaApiHelper.replyComment(getToken(), content, captcha, mSid, pid)
                .subscribeOn(Schedulers.io())
                .map(new Func1<WebApi.Result, WebApi.Result>() {
                    @Override
                    public WebApi.Result call(WebApi.Result result) {
                        if (result.isSuccess()) {
                            return result;
                        } else {
                            throw new RequestFailedException();
                        }
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<WebApi.Result>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        showSnackBar(String.format(getString(R.string.add_comment_failed_format), e.getMessage()));
                    }

                    @Override
                    public void onNext(WebApi.Result o) {
                        showSnackBar(R.string.add_comment_succeed);
                    }
                });
    }

    private void hideProgress() {
        mCommentView.setRefreshing(false);
    }

    private void hideOrShowTip() {
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
            addComment();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public interface OnCommentUpdateListener {
        void onCommentUpdated(int count);
    }
}
