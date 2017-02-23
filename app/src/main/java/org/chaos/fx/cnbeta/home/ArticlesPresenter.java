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

package org.chaos.fx.cnbeta.home;

import org.chaos.fx.cnbeta.net.MobileApi;
import org.chaos.fx.cnbeta.net.CnBetaApiHelper;
import org.chaos.fx.cnbeta.net.exception.RequestFailedException;
import org.chaos.fx.cnbeta.net.model.ArticleSummary;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

/**
 * @author Chaos
 *         7/19/16
 */
class ArticlesPresenter implements ArticlesContract.Presenter {

    private final String mTopicId;

    private ArticlesContract.View mView;

    private final Realm mRealm;

    private CompositeDisposable mDisposables;

    private boolean mFirstLoad = true;

    ArticlesPresenter(String topicId) {
        mTopicId = topicId;
        mRealm = Realm.getDefaultInstance();
        mDisposables = new CompositeDisposable();
    }

    @Override
    public void subscribe(ArticlesContract.View view) {
        mView = view;
        if (mFirstLoad) {
            mView.addArticles(getLocalArticles(), true);// load articles from local Repository
            mView.setRefreshing(true);
            doRequest(CnBetaApiHelper.articles());
        }
    }

    @Override
    public void unsubscribe() {
        mDisposables.clear();
    }

    private List<ArticleSummary> getLocalArticles() {
        RealmResults<ArticleSummary> results = mRealm.where(ArticleSummary.class).findAll();
        results.sort("mSid", Sort.DESCENDING);
        return new ArrayList<>(results);
    }

    @Override
    public void loadNewArticles(int sid) {
        if (!mView.isLoading()) {
            mView.setRefreshing(true);

            Observable<MobileApi.Result<List<ArticleSummary>>> observable;

            if (mView.isEmpty()) {
                observable = CnBetaApiHelper.topicArticles(mTopicId);
            } else {
                observable = CnBetaApiHelper.newArticles(mTopicId, sid);
            }

            doRequest(observable);
        }
    }

    @Override
    public void loadOldArticles(int sid) {
        if (!mView.isRefreshing()) {
            mView.setLoading(true);

            doRequest(CnBetaApiHelper.oldArticles(mTopicId, sid));
        }
    }

    private void doRequest(Observable<MobileApi.Result<List<ArticleSummary>>> observable) {
        mDisposables.clear();

        mDisposables.add(observable
                .map(new Function<MobileApi.Result<List<ArticleSummary>>, List<ArticleSummary>>() {
                    @Override
                    public List<ArticleSummary> apply(MobileApi.Result<List<ArticleSummary>> listResult) {
                        if (!listResult.isSuccess()) {
                            throw new RequestFailedException();
                        }
                        return listResult.result;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<ArticleSummary>>() {
                    @Override
                    public void accept(List<ArticleSummary> result) throws Exception {
                        processArticles(result);

                        mView.setRefreshing(false);
                        mView.setLoading(false);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable e) throws Exception {
                        mView.showLoadingArticlesError();
                    }
                }));
    }

    private void processArticles(List<ArticleSummary> result) {
        if (!result.isEmpty()) {
            if (mFirstLoad) {
                mFirstLoad = false;
                mView.clearArticles();
            }

            if (mView.isRefreshing()) {// refreshing, add items to top
                mView.addArticles(result, true);
            } else if (mView.isLoading()) {// loading, add items to bottom
                mView.addArticles(result, false);
            }
        } else {
            mView.showNoArticles();
        }
    }

    @Override
    public void saveArticles(List<ArticleSummary> articles) {
        if (!articles.isEmpty()) {
            mRealm.beginTransaction();
            mRealm.where(ArticleSummary.class).lessThan("mSid", articles.get(articles.size() - 1).getSid()).findAll().deleteAllFromRealm();
            mRealm.copyToRealmOrUpdate(articles);
            mRealm.commitTransaction();
        }
    }
}
