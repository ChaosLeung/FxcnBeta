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

import android.util.Log;

import org.chaos.fx.cnbeta.net.CnBetaApiHelper;
import org.chaos.fx.cnbeta.net.MobileApi;
import org.chaos.fx.cnbeta.net.exception.RequestFailedException;
import org.chaos.fx.cnbeta.net.model.NewsContent;
import org.chaos.fx.cnbeta.preferences.PreferenceHelper;
import org.chaos.fx.cnbeta.util.HtmlParser;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;

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

    private static final String TAG = "ContentPresenter";

    private ContentContract.View mView;
    private Disposable mDisposable;
    private HtmlParser<NewsContent> mParser;

    private int mSid;

    ContentPresenter(int sid) {
        mSid = sid;
    }

    @Override
    public void loadArticleContent() {
        mView.showLoadingView(true);
        mView.showLoadingError(false);

        if (PreferenceHelper.getInstance().inMobileApiMode()) {
            loadMobileApiContent();
        } else {
            mParser = new WebContentParser();
            loadWebApiContent();
        }
    }

    private void loadMobileApiContent() {
        mDisposable = CnBetaApiHelper.articleContent(mSid)
                .subscribeOn(Schedulers.io())
                .map(new Function<MobileApi.Result<NewsContent>, NewsContent>() {
                    @Override
                    public NewsContent apply(MobileApi.Result<NewsContent> result) throws Exception {
                        if (result.isSuccess()) {
                            return result.result;
                        } else {
                            throw new RequestFailedException(result.status);
                        }
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<NewsContent>() {
                    @Override
                    public void accept(NewsContent result) throws Exception {
                        mView.setupDetailsFragment(result);
                        mView.showLoadingView(false);
                        mView.showLoadingError(false);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable e) throws Exception {
                        Log.e(TAG, "loadMobileApiContent: ", e);
                        mView.showLoadingView(false);
                        mView.showLoadingError(true);
                    }
                });
    }

    private void loadWebApiContent() {
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
                .map(new Function<String, NewsContent>() {
                    @Override
                    public NewsContent apply(String html) throws Exception {
                        Element head = Jsoup.parse(html).head();
                        String token = head.select("meta[name=\"csrf-token\"]").attr("content");
                        String sn = CnBetaApiHelper.getSNFromArticleBody(html);
                        mView.setupCommentFragment(sn, token);
                        return mParser.parse(html);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<NewsContent>() {
                    @Override
                    public void accept(NewsContent result) throws Exception {
                        mView.setupDetailsFragment(result);
                        mView.showLoadingView(false);
                        mView.showLoadingError(false);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable e) {
                        // TODO 返回 564 时，可以请求 https://hot.cnbeta.com/articles/{sid}
                        Log.e(TAG, "loadWebApiContent: ", e);
                        mView.showLoadingView(false);
                        mView.showLoadingError(true);
                    }
                });
    }

    @Override
    public void subscribe(ContentContract.View view) {
        mView = view;
        loadArticleContent();
    }

    @Override
    public void unsubscribe() {
        mDisposable.dispose();
    }
}
