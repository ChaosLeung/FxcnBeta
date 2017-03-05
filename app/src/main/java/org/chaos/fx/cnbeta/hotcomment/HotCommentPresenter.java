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

import android.util.Log;

import org.chaos.fx.cnbeta.net.CnBetaApiHelper;
import org.chaos.fx.cnbeta.net.model.HotComment;
import org.chaos.fx.cnbeta.util.HtmlParser;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;

/**
 * @author Chaos
 *         11/7/16
 */

class HotCommentPresenter implements HotCommentContract.Presenter {

    private static final String TAG = "HotCommentPresenter";

    private HotCommentContract.View mView;
    private Disposable mDisposable;
    private HtmlParser<List<HotComment>> mParser = new MWebHotCommentParser();

    HotCommentPresenter() {
    }

    @Override
    public void loadHotComments() {
        mDisposable = CnBetaApiHelper.getHotCommentsByPage(1) // 暂时只加载第一页
                .map(new Function<ResponseBody, String>() {
                    @Override
                    public String apply(ResponseBody responseBody) throws Exception {
                        return responseBody.string();
                    }
                })
                .map(new Function<String, List<HotComment>>() {
                    @Override
                    public List<HotComment> apply(String html) throws Exception {
                        return mParser.parse(html);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<HotComment>>() {
                    @Override
                    public void accept(List<HotComment> result) throws Exception {
                        if (!result.isEmpty()) {
                            mView.addComments(result);
                        } else {
                            mView.showNoMoreContent();
                        }
                        mView.showRefreshing(false);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable e) throws Exception {
                        Log.e(TAG, "loadHotComments: ", e);
                        mView.showLoadFailed();
                        mView.showRefreshing(false);
                    }
                });
    }

    @Override
    public void subscribe(HotCommentContract.View view) {
        mView = view;
        mView.showRefreshing(true);
        loadHotComments();
    }

    @Override
    public void unsubscribe() {
        if (mDisposable != null) {
            mDisposable.dispose();
        }
    }
}
