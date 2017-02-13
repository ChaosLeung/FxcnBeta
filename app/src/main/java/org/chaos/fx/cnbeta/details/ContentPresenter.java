/*
 * Copyright 2017 Chaos
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
import org.chaos.fx.cnbeta.net.WebApi;
import org.chaos.fx.cnbeta.net.exception.RequestFailedException;
import org.chaos.fx.cnbeta.net.model.ClosedComment;
import org.chaos.fx.cnbeta.net.model.WebCommentResult;
import org.chaos.fx.cnbeta.util.ModelUtil;

import java.io.IOException;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.exceptions.Exceptions;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;

/**
 * @author Chaos
 *         13/02/2017
 */

class ContentPresenter implements ContentContract.Presenter {

    private ContentContract.View mView;
    private Disposable mDisposable;

    private int mSid;
    private String mHtmlBody;
    private WebCommentResult mWebCommentResult;

    ContentPresenter(int sid) {
        mSid = sid;
    }

    @Override
    public void loadArticleHtml() {
        mView.showLoadingView(true);
        mView.showLoadingError(false);

        mDisposable = CnBetaApiHelper.getArticleHtml(mSid)
                .subscribeOn(Schedulers.io())
                .map(new Function<ResponseBody, String>() {
                    @Override
                    public String apply(ResponseBody responseBody) {
                        try {
                            mHtmlBody = responseBody.string();
                            return CnBetaApiHelper.getSNFromArticleBody(mHtmlBody);
                        } catch (IOException e) {
                            throw Exceptions.propagate(e);
                        }
                    }
                })
                .flatMap(new Function<String, Observable<WebApi.Result<WebCommentResult>>>() {
                    @Override
                    public Observable<WebApi.Result<WebCommentResult>> apply(String sn) {
                        return CnBetaApiHelper.getCommentJson(mSid, sn);
                    }
                })
                .map(new Function<WebApi.Result<WebCommentResult>, WebCommentResult>() {
                    @Override
                    public WebCommentResult apply(WebApi.Result<WebCommentResult> result) {
                        if (result.isSuccess()) {
                            return result.result;
                        } else {
                            throw new RequestFailedException();
                        }
                    }
                })
                .flatMap(new Function<WebCommentResult, Observable<WebCommentResult>>() {
                    @Override
                    public Observable<WebCommentResult> apply(final WebCommentResult result) throws Exception {
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
                .retry(3)
                .subscribe(new Consumer<WebCommentResult>() {
                    @Override
                    public void accept(WebCommentResult result) throws Exception {
                        mWebCommentResult = result;

                        mView.showLoadingView(false);
                        mView.setupChildViews();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable e) throws Exception {
                        mView.showLoadingView(false);
                        mView.showLoadingError(true);
                    }
                });
    }

    @Override
    public String getArticleToken() {
        return mWebCommentResult.getToken();
    }

    @Override
    public String getHtmlBody() {
        return mHtmlBody;
    }

    @Override
    public WebCommentResult getWebComments() {
        return mWebCommentResult;
    }

    @Override
    public void subscribe(ContentContract.View view) {
        mView = view;
        loadArticleHtml();
    }

    @Override
    public void unsubscribe() {
        mDisposable.dispose();
    }
}
