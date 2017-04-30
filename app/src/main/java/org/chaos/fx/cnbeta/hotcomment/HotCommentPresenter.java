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
import org.chaos.fx.cnbeta.net.MobileApi;
import org.chaos.fx.cnbeta.net.WebApi;
import org.chaos.fx.cnbeta.net.exception.RequestFailedException;
import org.chaos.fx.cnbeta.net.model.HotComment;
import org.chaos.fx.cnbeta.util.HtmlParser;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.exceptions.Exceptions;
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
        mView.showRefreshing(true);
        mDisposable = getHotCommentFromWebAPI()
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
        loadHotComments();
    }

    @Override
    public void unsubscribe() {
        if (mDisposable != null) {
            mDisposable.dispose();
        }
    }

    private Observable<List<HotComment>> getHotCommentFromMobileAPI() {
        return CnBetaApiHelper.hotComment()
                .map(new Function<MobileApi.Result<List<HotComment>>, List<HotComment>>() {
                    @Override
                    public List<HotComment> apply(MobileApi.Result<List<HotComment>> result) throws Exception {
                        if (!result.isSuccess()) {
                            throw new RequestFailedException();
                        }
                        return result.result;
                    }
                });
    }

    private Observable<List<HotComment>> getHotCommentFromWebMobileAPI() {
        return CnBetaApiHelper.getHotCommentsByPage(1) // 暂时只加载第一页
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
                });
    }

    private Observable<List<HotComment>> getHotCommentFromWebAPI() {
        return CnBetaApiHelper.getHomeHtml()
                .map(new Function<ResponseBody, String>() {
                    @Override
                    public String apply(ResponseBody responseBody) throws Exception {
                        try {
                            return responseBody.string();
                        } catch (IOException e) {
                            throw Exceptions.propagate(e);
                        }
                    }
                })
                .map(new Function<String, String>() {
                    @Override
                    public String apply(String s) throws Exception {
                        return new HomePagePraser().parse(s);
                    }
                })
                .flatMap(new Function<String, ObservableSource<WebApi.Result<List<HotComment>>>>() {
                    @Override
                    public ObservableSource<WebApi.Result<List<HotComment>>> apply(String token) throws Exception {
                        return CnBetaApiHelper.getHotComments(token, 1);
                    }
                })
                .map(new Function<WebApi.Result<List<HotComment>>, List<HotComment>>() {
                    @Override
                    public List<HotComment> apply(WebApi.Result<List<HotComment>> result) throws Exception {
                        if (!result.isSuccess()) {
                            throw new RequestFailedException();
                        }
                        return result.result;
                    }
                })
                .map(new Function<List<HotComment>, List<HotComment>>() {
                    @Override
                    public List<HotComment> apply(List<HotComment> hotComments) throws Exception {
                        for (HotComment c : hotComments) {
                            c.setComment(Jsoup.parse(c.getComment()).text());
                            Document d = Jsoup.parse(c.getTitle());
                            c.setTitle(d.select("a").text());
                            c.setUsername(d.select("strong").text() + "网友");
                        }
                        return hotComments;
                    }
                });
    }
}
