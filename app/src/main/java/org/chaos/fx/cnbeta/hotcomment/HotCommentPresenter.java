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

package org.chaos.fx.cnbeta.hotcomment;

import org.chaos.fx.cnbeta.net.CnBetaApi;
import org.chaos.fx.cnbeta.net.CnBetaApiHelper;
import org.chaos.fx.cnbeta.net.exception.RequestFailedException;
import org.chaos.fx.cnbeta.net.model.HotComment;

import java.util.List;

import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * @author Chaos
 *         11/7/16
 */

public class HotCommentPresenter implements HotCommentContract.Presenter {
    private HotCommentContract.View mView;
    private Subscription mSubscription;

    public HotCommentPresenter(HotCommentContract.View view) {
        mView = view;
    }

    @Override
    public void loadHotComments() {
        mSubscription = CnBetaApiHelper.hotComment()
                .subscribeOn(Schedulers.io())
                .map(new Func1<CnBetaApi.Result<List<HotComment>>, List<HotComment>>() {
                    @Override
                    public List<HotComment> call(CnBetaApi.Result<List<HotComment>> listResult) {
                        if (!listResult.isSuccess()) {
                            throw new RequestFailedException();
                        }
                        return listResult.result;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<List<HotComment>>() {
                    @Override
                    public void onCompleted() {
                        mView.showRefreshing(false);
                    }

                    @Override
                    public void onError(Throwable e) {
                        mView.showLoadFailed();
                        mView.showRefreshing(false);
                    }

                    @Override
                    public void onNext(List<HotComment> result) {
                        if (!result.isEmpty()) {
                            mView.addComments(result);
                        } else {
                            mView.showNoMoreContent();
                        }
                    }
                });
    }

    @Override
    public void subscribe() {
        mView.showRefreshing(true);
        loadHotComments();
    }

    @Override
    public void unsubscribe() {
        if (mSubscription != null) {
            mSubscription.unsubscribe();
        }
    }
}
