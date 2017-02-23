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

package org.chaos.fx.cnbeta.hotarticles;

import org.chaos.fx.cnbeta.net.MobileApi;
import org.chaos.fx.cnbeta.net.CnBetaApiHelper;
import org.chaos.fx.cnbeta.net.exception.RequestFailedException;
import org.chaos.fx.cnbeta.net.model.ArticleSummary;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * @author Chaos
 *         11/7/16
 */

class Top10Presenter implements Top10Contract.Presenter {
    private Top10Contract.View mView;
    private Disposable mDisposable;

    Top10Presenter() {
    }

    @Override
    public void loadTop10Articles() {
        mDisposable = CnBetaApiHelper.top10()
                .subscribeOn(Schedulers.io())
                .map(new Function<MobileApi.Result<List<ArticleSummary>>, List<ArticleSummary>>() {
                    @Override
                    public List<ArticleSummary> apply(MobileApi.Result<List<ArticleSummary>> listResult) {
                        if (!listResult.isSuccess()) {
                            throw new RequestFailedException();
                        }
                        return listResult.result;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<ArticleSummary>>() {
                    @Override
                    public void accept(List<ArticleSummary> result) throws Exception {
                        if (!result.isEmpty()) {
                            mView.addArticleSummary(result);
                        } else {
                            mView.showNoMoreContent();
                        }
                        mView.showRefreshing(false);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable e) throws Exception {
                        mView.showLoadFailed();
                        mView.showRefreshing(false);
                    }
                });
    }

    @Override
    public void subscribe(Top10Contract.View view) {
        mView = view;
        mView.showRefreshing(true);
        loadTop10Articles();
    }

    @Override
    public void unsubscribe() {
        if (mDisposable != null) {
            mDisposable.dispose();
        }
    }
}
