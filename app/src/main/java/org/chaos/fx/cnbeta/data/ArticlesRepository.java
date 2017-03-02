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

package org.chaos.fx.cnbeta.data;

import android.content.Context;
import android.support.annotation.NonNull;

import org.chaos.fx.cnbeta.net.model.ArticleSummary;
import org.chaos.fx.cnbeta.net.model.ArticleSummaryDao;
import org.chaos.fx.cnbeta.net.model.HasReadArticle;
import org.chaos.fx.cnbeta.net.model.HasReadArticleDao;
import org.greenrobot.greendao.query.Query;

import java.util.List;


/**
 * @author Chaos
 *         01/03/2017
 */

public class ArticlesRepository implements ArticlesDataSource {

    private static ArticlesRepository singleton;

    public static void initialize(Context context) {
        if (singleton == null) {
            synchronized (ArticlesRepository.class) {
                if (singleton == null) {
                    singleton = new ArticlesRepository(context);
                }
            }
        }
    }

    public static ArticlesDataSource getInstance() {
        return singleton;
    }

    private ArticlesDbHelper mDbHelper;

    private ArticlesRepository(@NonNull Context context) {
        mDbHelper = new ArticlesDbHelper(context);
    }

    @Override
    public void getSummaries(@NonNull LoadSummaryCallback callback) {
        ArticleSummaryDao dao = mDbHelper.getSummaryDao();
        Query<ArticleSummary> query = dao.queryBuilder().build();
        List<ArticleSummary> results = query.list();

        if (results.isEmpty()) {
            callback.onDataNotAvailable();
        } else {
            callback.onTasksLoaded(results);
        }
    }

    @Override
    public void saveSummaries(@NonNull List<ArticleSummary> summaries) {
        ArticleSummaryDao dao = mDbHelper.getSummaryDao();
        dao.insertInTx(summaries);
    }

    @Override
    public void readArticle(@NonNull HasReadArticle article) {
        HasReadArticleDao dao = mDbHelper.getHasReadDao();
        dao.insertOrReplace(article);
    }

    @Override
    public void readArticle(int sid) {
        readArticle(new HasReadArticle(sid));
    }

    @Override
    public boolean hasReadArticle(@NonNull ArticleSummary summary) {
        return hasReadArticle(summary.getSid());
    }

    @Override
    public boolean hasReadArticle(int sid) {
        HasReadArticleDao dao = mDbHelper.getHasReadDao();
        Query<HasReadArticle> query = dao.queryBuilder().where(HasReadArticleDao.Properties.Sid.eq(sid)).build();
        return query.unique() != null;
    }

    @Override
    public void deleteAllSummaries() {
        ArticleSummaryDao dao = mDbHelper.getSummaryDao();
        dao.deleteAll();
    }
}
