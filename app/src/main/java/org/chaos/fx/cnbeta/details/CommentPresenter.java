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

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;


/**
 * @author Chaos
 *         10/14/16
 */

class CommentPresenter implements CommentContract.Presenter {

    private int mSid;
    private CommentContract.View mView;

    private String mToken;

    private CompositeDisposable mDisposables;

    CommentPresenter(int sid, String token) {
        mSid = sid;
        mToken = token;
        mDisposables = new CompositeDisposable();
    }

    @Override
    public void refreshComments(int page) {
        mDisposables.add(CnBetaApiHelper.comments(mSid, page)
                .subscribeOn(Schedulers.io())
                .map(new Function<CnBetaApi.Result<List<Comment>>, List<Comment>>() {
                    @Override
                    public List<Comment> apply(CnBetaApi.Result<List<Comment>> listResult) {
                        if (!listResult.isSuccess()) {
                            throw new RequestFailedException();
                        }
                        return listResult.result;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<Comment>>() {
                    @Override
                    public void accept(List<Comment> result) throws Exception {
                        if (!result.isEmpty()) {
                            mView.addComments(result);
                        } else {
                            mView.showNoMoreComments();
                        }

                        mView.hideProgress();
                        mView.showNoCommentTipsIfNeed();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable e) throws Exception {
                        mView.showLoadingFailed();
                        mView.hideProgress();
                        mView.showNoCommentTipsIfNeed();
                    }
                }));
    }

    @Override
    public void against(final Comment c) {
        mDisposables.add(CnBetaApiHelper.againstComment(mToken, mSid, c.getTid())
                .subscribeOn(Schedulers.io())
                .map(new Function<WebApi.Result, WebApi.Result>() {
                    @Override
                    public WebApi.Result apply(WebApi.Result result) {
                        if (!result.isSuccess()) {
                            throw new RequestFailedException();
                        }
                        return result;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<WebApi.Result>() {
                    @Override
                    public void accept(WebApi.Result result) throws Exception {
                        c.setAgainst(c.getAgainst() + 1);
                        mView.notifyItemChanged(c);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable e) throws Exception {
                        mView.showOperationFailed();
                    }
                }));
    }

    @Override
    public void support(final Comment c) {
        mDisposables.add(CnBetaApiHelper.supportComment(mToken, mSid, c.getTid())
                .map(new Function<WebApi.Result, WebApi.Result>() {
                    @Override
                    public WebApi.Result apply(WebApi.Result result) {
                        if (!result.isSuccess()) {
                            throw new RequestFailedException();
                        }
                        return result;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<WebApi.Result>() {
                    @Override
                    public void accept(WebApi.Result result) throws Exception {
                        c.setSupport(c.getSupport() + 1);
                        mView.notifyItemChanged(c);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable e) throws Exception {
                        mView.showOperationFailed();
                    }
                }));
    }

    @Override
    public void addComment() {
        mView.showCommentDialog(0);
    }

    @Override
    public void replyComment(Comment c) {
        mView.showCommentDialog(c.getTid());
    }

    @Override
    public void publishComment(String content, String captcha, int pid) {
        mDisposables.add(CnBetaApiHelper.replyComment(mToken, content, captcha, mSid, pid)
                .subscribeOn(Schedulers.io())
                .map(new Function<WebApi.Result, WebApi.Result>() {
                    @Override
                    public WebApi.Result apply(WebApi.Result result) {
                        if (result.isSuccess()) {
                            return result;
                        } else {
                            throw new RequestFailedException();
                        }
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<WebApi.Result>() {
                    @Override
                    public void accept(WebApi.Result result) throws Exception {
                        mView.showAddCommentSucceed();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable e) throws Exception {
                        mView.showAddCommentFailed(e.getMessage());
                    }
                }));
    }

    @Override
    public void updateToken(String token) {
        mToken = token;
    }

    @Override
    public void subscribe(CommentContract.View view) {
        mView = view;
    }

    @Override
    public void unsubscribe() {
        mDisposables.clear();
    }
}
