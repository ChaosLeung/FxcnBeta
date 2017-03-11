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

import android.util.Log;

import org.chaos.fx.cnbeta.net.CnBetaApiHelper;
import org.chaos.fx.cnbeta.net.MobileApi;
import org.chaos.fx.cnbeta.net.WebApi;
import org.chaos.fx.cnbeta.net.exception.RequestFailedException;
import org.chaos.fx.cnbeta.net.model.Comment;
import org.chaos.fx.cnbeta.net.model.WebCommentResult;
import org.chaos.fx.cnbeta.preferences.PreferenceHelper;
import org.chaos.fx.cnbeta.util.CommentComparator;
import org.chaos.fx.cnbeta.util.ModelUtil;

import java.util.Collections;
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

    private static final String TAG = "CommentPresenter";

    private int mSid;
    private String mSN;
    private String mTokenForReadComment;
    private String mOperationToken;
    private CommentContract.View mView;

    private CompositeDisposable mDisposables;

    private boolean isLoadingClosedComments;
    private boolean isCommentEnable;

    private boolean inMobileApiMode;

    CommentPresenter(int sid) {
        mSid = sid;
        mDisposables = new CompositeDisposable();
    }

    @Override
    public void setSN(String sn) {
        mSN = sn;
    }

    @Override
    public void setReadCommentToken(String token) {
        mTokenForReadComment = token;
    }

    @Override
    public void loadComments() {
        loadWebApiComments();
    }

    private void loadWebApiComments() {
        mDisposables.add(CnBetaApiHelper.getCommentJson(mSid, mTokenForReadComment, mSN)
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
                                    .map(new Function<List<Comment>, WebCommentResult>() {
                                        @Override
                                        public WebCommentResult apply(List<Comment> comments) throws Exception {
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
                        mOperationToken = result.getToken();
                        List<Comment> comments = ModelUtil.toCommentList(result);
                        Collections.sort(comments, new CommentComparator());
                        mView.addComments(comments);

                        mView.updateCommentCount();

                        mView.showNoCommentTipsIfNeed();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable e) throws Exception {
                        Log.e(TAG, "loadWebApiComments: ", e);

                        mView.updateCommentCount();

                        mView.showNoCommentTipsIfNeed();
                    }
                }));
    }

    private void loadMobileApiComments() {
        // enable publish comment feature if current is in Mobile API mode
        isCommentEnable = true;
        refreshComments(1);
    }

    @Override
    public void refreshComments(final int page) {
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
                .flatMap(new Function<List<Comment>, ObservableSource<List<Comment>>>() {
                    @Override
                    public ObservableSource<List<Comment>> apply(List<Comment> comments) throws Exception {
                        // TODO: 06/03/2017 page == 1 且 adapter.itemCount > 0 时，可以直接返回
                        if (comments.isEmpty() && page == 1) {
                            isLoadingClosedComments = true;
                            return CnBetaApiHelper.closedComments(mSid);
                        } else {
                            return Observable.just(comments);
                        }
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<Comment>>() {
                    @Override
                    public void accept(List<Comment> result) throws Exception {
                        boolean isClosedComments = isLoadingClosedComments;
                        isLoadingClosedComments = false;
                        if (!result.isEmpty()) {
                            if (isClosedComments) {
                                isCommentEnable = false;
                            }
                            Collections.sort(result, new CommentComparator());
                            mView.addComments(result);
                        } else {
                            mView.showNoMoreComments();
                        }

                        mView.updateCommentCount();

                        mView.hideProgress();
                        mView.showNoCommentTipsIfNeed();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable e) throws Exception {
                        Log.e(TAG, "refreshComments: ", e);
                        isLoadingClosedComments = false;

                        mView.updateCommentCount();

                        mView.showLoadingFailed();
                        mView.hideProgress();
                        mView.showNoCommentTipsIfNeed();
                    }
                }));
    }

    @Override
    public void against(final Comment c) {
        if (inMobileApiMode) {
            againstByMobileApi(c);
        } else {
            againstByWebApi(c);
        }
    }

    private void againstByWebApi(final Comment c) {
        mDisposables.add(CnBetaApiHelper.againstComment(mOperationToken, mSid, c.getTid())
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
                        Log.e(TAG, "againstByWebApi: ", e);
                        mView.showOperationFailed();
                    }
                }));
    }

    private void againstByMobileApi(final Comment c) {
        mDisposables.add(CnBetaApiHelper.againstComment(c.getTid())
                .subscribeOn(Schedulers.io())
                .map(new Function<MobileApi.Result<String>, String>() {
                    @Override
                    public String apply(MobileApi.Result<String> result) {
                        if (!result.isSuccess()) {
                            throw new RequestFailedException();
                        }
                        return result.result;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String result) throws Exception {
                        c.setAgainst(c.getAgainst() + 1);
                        mView.notifyCommentChanged(c);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable e) throws Exception {
                        Log.e(TAG, "againstByMobileApi: ", e);
                        mView.showOperationFailed();
                    }
                }));
    }

    @Override
    public void support(final Comment c) {
        if (inMobileApiMode) {
            supportByMobileApi(c);
        } else {
            supportByWebApi(c);
        }
    }

    private void supportByWebApi(final Comment c) {
        mDisposables.add(CnBetaApiHelper.supportComment(mOperationToken, mSid, c.getTid())
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
                        Log.e(TAG, "supportByWebApi: ", e);
                        mView.showOperationFailed();
                    }
                }));
    }

    private void supportByMobileApi(final Comment c) {
        mDisposables.add(CnBetaApiHelper.supportComment(c.getTid())
                .map(new Function<MobileApi.Result<String>, String>() {
                    @Override
                    public String apply(MobileApi.Result<String> result) {
                        if (!result.isSuccess()) {
                            throw new RequestFailedException();
                        }
                        return result.result;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String result) throws Exception {
                        c.setSupport(c.getSupport() + 1);
                        mView.notifyCommentChanged(c);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable e) throws Exception {
                        Log.e(TAG, "supportByMobileApi: ", e);
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
        if (inMobileApiMode) {
            if (pid == 0) {
                addCommentByMobileApi(content);
            } else {
                replayCommentByMobileApi(content, pid);
            }
        } else {
            publishCommentByWebApi(content, captcha, pid);
        }
    }

    private void publishCommentByWebApi(String content, String captcha, final int pid) {
        mDisposables.add(CnBetaApiHelper.replyComment(mOperationToken, content, captcha, mSid, pid)
                .subscribeOn(Schedulers.io())
                .map(new Function<WebApi.Result, WebApi.Result>() {
                    @Override
                    public WebApi.Result apply(WebApi.Result result) {
                        if (result.isSuccess()) {
                            return result;
                        } else {
                            throw new RequestFailedException(result.error);
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
                        Log.e(TAG, "publishCommentByWebApi: pid = " + pid, e);
                        mView.showAddCommentFailed(e.getMessage());
                    }
                }));
    }

    private void replayCommentByMobileApi(String content, int pid) {
        mDisposables.add(CnBetaApiHelper.replyComment(mSid, pid, content)
                .subscribeOn(Schedulers.io())
                .map(new Function<MobileApi.Result<String>, String>() {
                    @Override
                    public String apply(MobileApi.Result<String> result) throws Exception {
                        if (result.isSuccess()) {
                            return result.result;
                        } else {
                            throw new RequestFailedException(result.result);
                        }
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        mView.showAddCommentSucceed();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable e) throws Exception {
                        Log.e(TAG, "replayCommentByMobileApi: ", e);
                        mView.showAddCommentFailed(e.getMessage());
                    }
                }));
    }

    private void addCommentByMobileApi(String content) {
        mDisposables.add(CnBetaApiHelper.addComment(mSid, content)
                .subscribeOn(Schedulers.io())
                .map(new Function<MobileApi.Result<String>, String>() {
                    @Override
                    public String apply(MobileApi.Result<String> result) throws Exception {
                        if (result.isSuccess()) {
                            return result.result;
                        } else {
                            throw new RequestFailedException(result.result);
                        }
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        mView.showAddCommentSucceed();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable e) throws Exception {
                        Log.e(TAG, "addCommentByMobileApi: ", e);
                        mView.showAddCommentFailed(e.getMessage());
                    }
                }));
    }

    @Override
    public boolean isCommentEnable() {
        return isCommentEnable;
    }

    public String getOperationToken() {
        return mOperationToken;
    }

    @Override
    public void subscribe(CommentContract.View view) {
        mView = view;

        inMobileApiMode = PreferenceHelper.getInstance().inMobileApiMode();
        if (inMobileApiMode) {
            loadMobileApiComments();
        }
    }

    @Override
    public void unsubscribe() {
        mDisposables.clear();
    }
}
