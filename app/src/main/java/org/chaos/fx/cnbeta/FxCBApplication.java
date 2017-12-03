/*
 * Copyright 2015 Chaos
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

package org.chaos.fx.cnbeta;

import android.app.Application;
import android.support.v7.app.AppCompatDelegate;

import com.squareup.leakcanary.LeakCanary;

import org.chaos.fx.cnbeta.data.ArticlesRepository;
import org.chaos.fx.cnbeta.net.CnBetaApiHelper;
import org.chaos.fx.cnbeta.preferences.PreferenceHelper;
import org.chaos.fx.cnbeta.util.TimeStringHelper;
import org.chaos.fx.cnbeta.wxapi.WXApiProvider;

/**
 * @author Chaos
 *         2015/11/14.
 */
public class FxCBApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        if (LeakCanary.isInAnalyzerProcess(this)) {
            return;
        }
        LeakCanary.install(this);
        CnBetaApiHelper.initialize();
        TimeStringHelper.initialize(this);
        ArticlesRepository.initialize(this);
        WXApiProvider.initialize(this);
        PreferenceHelper.initialize(this);
        AppCompatDelegate.setDefaultNightMode(PreferenceHelper.getInstance().inNightMode() ?
                AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO);
    }
}
