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

import org.chaos.fx.cnbeta.net.CnBetaApi;
import org.chaos.fx.cnbeta.net.CnBetaApiHelper;
import org.chaos.fx.cnbeta.net.exception.RequestFailedException;
import org.chaos.fx.cnbeta.net.model.ArticleSummary;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * @author Chaos
 *         7/19/16
 */
public class ArticlesPresenter implements ArticlesContract.Presenter {

    private final String mTopicId;

    private final ArticlesContract.View mArticlesView;

    private final Realm mRealm;

    private CompositeSubscription mSubscriptions;

    private boolean mFirstLoad = true;

    public ArticlesPresenter(String topicId, ArticlesContract.View articlesView) {
        mTopicId = topicId;
        mArticlesView = articlesView;
        mRealm = Realm.getDefaultInstance();
        mSubscriptions = new CompositeSubscription();
    }

    @Override
    public void subscribe() {
        if (mFirstLoad) {
            mArticlesView.addArticles(getLocalArticles(), true);// load articles from local Repository
            mArticlesView.setRefreshing(true);
            doRequest(CnBetaApiHelper.articles());
        }
    }

    @Override
    public void unsubscribe() {
        mSubscriptions.clear();
    }

    private List<ArticleSummary> getLocalArticles() {
        RealmResults<ArticleSummary> results = mRealm.allObjects(ArticleSummary.class);
        results.sort("mSid", Sort.DESCENDING);
        return new ArrayList<>(results);
    }

    @Override
    public void loadNewArticles(int sid) {
        if (!mArticlesView.isLoading()) {
            mArticlesView.setRefreshing(true);

            Observable<CnBetaApi.Result<List<ArticleSummary>>> observable;

            if (mArticlesView.isEmpty()) {
                observable = CnBetaApiHelper.topicArticles(mTopicId);
            } else {
                observable = CnBetaApiHelper.newArticles(mTopicId, sid);
            }

            doRequest(observable);
        }
    }

    @Override
    public void loadOldArticles(int sid) {
        if (!mArticlesView.isRefreshing()) {
            mArticlesView.setLoading(true);

            doRequest(CnBetaApiHelper.oldArticles(mTopicId, sid));
        }
    }

    private void doRequest(Observable<CnBetaApi.Result<List<ArticleSummary>>> observable) {
        mSubscriptions.clear();
        Subscription subscription = observable
                .map(new Func1<CnBetaApi.Result<List<ArticleSummary>>, List<ArticleSummary>>() {
                    @Override
                    public List<ArticleSummary> call(CnBetaApi.Result<List<ArticleSummary>> listResult) {
                        if (!listResult.isSuccess()) {
                            throw new RequestFailedException();
                        }
                        return listResult.result;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<List<ArticleSummary>>() {
                    @Override
                    public void onCompleted() {
                        mArticlesView.setRefreshing(false);
                        mArticlesView.setLoading(false);
                    }

                    @Override
                    public void onError(Throwable e) {
                        mArticlesView.showLoadingArticlesError();
                    }

                    @Override
                    public void onNext(List<ArticleSummary> result) {
                        processArticles(result);
                    }
                });
        mSubscriptions.add(subscription);
    }

    private void processArticles(List<ArticleSummary> result) {
        if (!result.isEmpty()) {
            if (mFirstLoad) {
                mFirstLoad = false;
                mArticlesView.clearArticles();
            }

            if (mArticlesView.isRefreshing()) {// refreshing, add items to top
                mArticlesView.addArticles(result, true);
            } else if (mArticlesView.isLoading()) {// loading, add items to bottom
                mArticlesView.addArticles(result, false);
            }
        } else {
            mArticlesView.showNoArticles();
        }
    }

    @Override
    public void saveArticles(List<ArticleSummary> articles) {
        if (!articles.isEmpty()) {
            mRealm.beginTransaction();
            mRealm.where(ArticleSummary.class).lessThan("mSid", articles.get(articles.size() - 1).getSid()).findAll().clear();
            mRealm.copyToRealmOrUpdate(articles);
            mRealm.commitTransaction();
        }
    }
}
