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

import java.io.IOException;

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
                            return responseBody.string();
                        } catch (IOException e) {
                            throw Exceptions.propagate(e);
                        }
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String result) throws Exception {
                        mView.setupDetailsFragment(result);
                        String sn = CnBetaApiHelper.getSNFromArticleBody(result);
                        mView.setupCommentFragment(sn);
                        mView.showLoadingView(false);
                        mView.showLoadingError(false);
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
    public void subscribe(ContentContract.View view) {
        mView = view;
        loadArticleHtml();
    }

    @Override
    public void unsubscribe() {
        mDisposable.dispose();
    }
}
