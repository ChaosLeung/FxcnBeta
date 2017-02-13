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

package org.chaos.fx.cnbeta.rank;

import org.chaos.fx.cnbeta.net.CnBetaApi;
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

class RankSubPresenter implements RankSubContract.Presenter {

    private RankSubContract.View mView;
    private String mType;
    private Disposable mDisposable;

    RankSubPresenter(String type) {
        mType = type;
    }

    @Override
    public void loadArticles() {
        mDisposable = CnBetaApiHelper.todayRank(mType)
                .subscribeOn(Schedulers.io())
                .map(new Function<CnBetaApi.Result<List<ArticleSummary>>, List<ArticleSummary>>() {
                    @Override
                    public List<ArticleSummary> apply(CnBetaApi.Result<List<ArticleSummary>> listResult) throws Exception {
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
                            mView.addArticles(result);
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
    public void subscribe(RankSubContract.View view) {
        mView = view;
        mView.showRefreshing(true);
        loadArticles();
    }

    @Override
    public void unsubscribe() {
        if (mDisposable != null) {
            mDisposable.dispose();
        }
    }
}
