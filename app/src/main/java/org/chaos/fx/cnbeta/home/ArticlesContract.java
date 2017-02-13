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

import org.chaos.fx.cnbeta.BasePresenter;
import org.chaos.fx.cnbeta.BaseView;
import org.chaos.fx.cnbeta.net.model.ArticleSummary;

import java.util.List;

/**
 * @author Chaos
 *         7/19/16
 */
interface ArticlesContract {

    interface View extends BaseView {
        void setRefreshing(boolean refreshing);

        boolean isRefreshing();

        void setLoading(boolean loading);

        boolean isLoading();

        void addArticles(List<ArticleSummary> articles, boolean addToTop);

        void clearArticles();

        boolean isEmpty();

        void showNoArticles();

        void showLoadingArticlesError();
    }

    interface Presenter extends BasePresenter<View> {

        void loadNewArticles(int sid);

        void loadOldArticles(int sid);

        void saveArticles(List<ArticleSummary> articles);
    }
}
