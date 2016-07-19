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

import org.chaos.fx.cnbeta.net.CnBetaApi;
import org.chaos.fx.cnbeta.net.CnBetaApiHelper;
import org.chaos.fx.cnbeta.net.WebApi;
import org.chaos.fx.cnbeta.net.exception.RequestFailedException;
import org.chaos.fx.cnbeta.net.model.Comment;

import java.util.List;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * @author Chaos
 *         10/14/16
 */

public class CommentPresenter implements CommentContract.Presenter {

    private int mSid;
    private CommentContract.View mCommentView;

    private String mToken;

    private CompositeSubscription mSubscriptions;

    public CommentPresenter(int sid, String token, CommentContract.View commentView) {
        mSid = sid;
        mToken = token;
        mCommentView = commentView;
        mSubscriptions = new CompositeSubscription();
    }

    @Override
    public void refreshComments(int page) {
        mSubscriptions.add(CnBetaApiHelper.comments(mSid, page)
                .subscribeOn(Schedulers.io())
                .map(new Func1<CnBetaApi.Result<List<Comment>>, List<Comment>>() {
                    @Override
                    public List<Comment> call(CnBetaApi.Result<List<Comment>> listResult) {
                        if (!listResult.isSuccess()) {
                            throw new RequestFailedException();
                        }
                        return listResult.result;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<List<Comment>>() {
                    @Override
                    public void onCompleted() {
                        mCommentView.hideProgress();
                        mCommentView.showNoCommentTipsIfNeed();
                    }

                    @Override
                    public void onError(Throwable e) {
                        mCommentView.showLoadingFailed();
                        mCommentView.hideProgress();
                        mCommentView.showNoCommentTipsIfNeed();
                    }

                    @Override
                    public void onNext(List<Comment> result) {
                        if (!result.isEmpty()) {
                            mCommentView.addComments(result);
                        } else {
                            mCommentView.showNoMoreComments();
                        }
                    }
                }));
    }

    @Override
    public void against(final Comment c) {
        mSubscriptions.add(CnBetaApiHelper.againstComment(mToken, mSid, c.getTid())
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
                .subscribe(new Subscriber<WebApi.Result>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        mCommentView.showOperationFailed();
                    }

                    @Override
                    public void onNext(WebApi.Result result) {
                        c.setAgainst(c.getAgainst() + 1);
                        mCommentView.notifyItemChanged(c);
                    }
                }));
    }

    @Override
    public void support(final Comment c) {
        mSubscriptions.add(CnBetaApiHelper.supportComment(mToken, mSid, c.getTid())
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
                        mCommentView.showOperationFailed();
                    }

                    @Override
                    public void onNext(WebApi.Result result) {
                        c.setSupport(c.getSupport() + 1);
                        mCommentView.notifyItemChanged(c);
                    }
                }));
    }

    @Override
    public void addComment() {
        mCommentView.showCommentDialog(0);
    }

    @Override
    public void replyComment(Comment c) {
        mCommentView.showCommentDialog(c.getTid());
    }

    @Override
    public void publishComment(String content, String captcha, int pid) {
        mSubscriptions.add(CnBetaApiHelper.replyComment(mToken, content, captcha, mSid, pid)
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
                        mCommentView.showAddCommentFailed(e.getMessage());
                    }

                    @Override
                    public void onNext(WebApi.Result o) {
                        mCommentView.showAddCommentSucceed();
                    }
                }));
    }

    @Override
    public void updateToken(String token) {
        mToken = token;
    }

    @Override
    public void subscribe() {

    }

    @Override
    public void unsubscribe() {
        mSubscriptions.clear();
    }
}
