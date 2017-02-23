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

import org.chaos.fx.cnbeta.net.CnBetaApiHelper;
import org.chaos.fx.cnbeta.net.MobileApi;
import org.chaos.fx.cnbeta.net.WebApi;
import org.chaos.fx.cnbeta.net.exception.RequestFailedException;
import org.chaos.fx.cnbeta.net.model.ClosedComment;
import org.chaos.fx.cnbeta.net.model.Comment;
import org.chaos.fx.cnbeta.net.model.WebCommentResult;
import org.chaos.fx.cnbeta.preferences.PreferenceHelper;
import org.chaos.fx.cnbeta.util.ModelUtil;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
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
    private String mSN;
    private String mToken;
    private CommentContract.View mView;

    private CompositeDisposable mDisposables;

    private boolean isCommentEnable;

    CommentPresenter(int sid) {
        mSid = sid;
        mDisposables = new CompositeDisposable();
    }

    @Override
    public void setSN(String sn) {
        mSN = sn;
    }

    @Override
    public void loadComments() {
        loadWebApiComments();
    }

    private void loadWebApiComments() {
        mDisposables.add(CnBetaApiHelper.getCommentJson(mSid, mSN)
                .subscribeOn(Schedulers.io())
                .map(new Function<WebApi.Result<WebCommentResult>, WebCommentResult>() {
                    @Override
                    public WebCommentResult apply(WebApi.Result<WebCommentResult> result) throws Exception {
                        if (result.isSuccess()) {
                            return result.result;
                        } else {
                            throw new RequestFailedException();
                        }
                    }
                })
                .flatMap(new Function<WebCommentResult, ObservableSource<WebCommentResult>>() {
                    @Override
                    public ObservableSource<WebCommentResult> apply(final WebCommentResult result) throws Exception {
                        if (result.isOpen()) {
                            return Observable.just(result);
                        } else {
                            return CnBetaApiHelper.closedComments(mSid)
                                    .map(new Function<List<ClosedComment>, WebCommentResult>() {
                                        @Override
                                        public WebCommentResult apply(List<ClosedComment> comments) throws Exception {
                                            result.setComments(ModelUtil.toWebCommentMap(comments));
                                            return result;
                                        }
                                    });
                        }
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<WebCommentResult>() {
                    @Override
                    public void accept(WebCommentResult result) throws Exception {
                        isCommentEnable = result.isOpen();
                        mToken = result.getToken();
                        List<Comment> comments = ModelUtil.toCommentList(result);
                        mView.addComments(comments);
                        mView.showNoCommentTipsIfNeed();
                    }
                }));
    }

    private void loadMobileApiComments() {
        refreshComments(1);
    }

    @Override
    public void refreshComments(int page) {
        mDisposables.add(CnBetaApiHelper.comments(mSid, page)
                .subscribeOn(Schedulers.io())
                .map(new Function<MobileApi.Result<List<Comment>>, List<Comment>>() {
                    @Override
                    public List<Comment> apply(MobileApi.Result<List<Comment>> listResult) {
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
                        mView.notifyCommentChanged(c);
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
                        mView.notifyCommentChanged(c);
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
    public boolean isCommentEnable() {
        return isCommentEnable;
    }

    @Override
    public String getToken() {
        return mToken;
    }

    @Override
    public void subscribe(CommentContract.View view) {
        mView = view;

        if (PreferenceHelper.getInstance().inMobileApiMode()) {
            loadMobileApiComments();
        }
    }

    @Override
    public void unsubscribe() {
        mDisposables.clear();
    }
}
