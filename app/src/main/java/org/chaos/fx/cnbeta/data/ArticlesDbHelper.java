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

import org.chaos.fx.cnbeta.net.model.ArticleSummaryDao;
import org.chaos.fx.cnbeta.net.model.DaoMaster;
import org.chaos.fx.cnbeta.net.model.DaoSession;
import org.chaos.fx.cnbeta.net.model.HasReadArticleDao;
import org.greenrobot.greendao.database.Database;

/**
 * @author Chaos
 *         01/03/2017
 */

public class ArticlesDbHelper {

    private static final String DB_NAME = "fx_cnbeta_db";
    private DaoSession mDaoSession;

    public ArticlesDbHelper(Context context) {
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(context, DB_NAME);
        Database db = helper.getWritableDb();
        mDaoSession = new DaoMaster(db).newSession();
    }

    public DaoSession getDaoSession() {
        return mDaoSession;
    }

    public ArticleSummaryDao getSummaryDao() {
        return getDaoSession().getArticleSummaryDao();
    }

    public HasReadArticleDao getHasReadDao() {
        return getDaoSession().getHasReadArticleDao();
    }
}
