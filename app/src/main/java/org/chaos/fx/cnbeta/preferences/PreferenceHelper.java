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

package org.chaos.fx.cnbeta.preferences;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatDelegate;

/**
 * @author Chaos
 *         14/02/2017
 */

public class PreferenceHelper {

    private static PreferenceHelper singleton;

    public static void initialize(Context context) {
        if (singleton == null) {
            synchronized (PreferenceHelper.class) {
                if (singleton == null) {
                    singleton = new PreferenceHelper(context);
                }
            }
        }
    }


    private SharedPreferences mPreferences;
    private WifiManager mWifiManager;

    private PreferenceHelper(Context context) {
        mPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        mWifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
    }

    public static PreferenceHelper getInstance() {
        return singleton;
    }

    public boolean inSafeDataMode() {
        boolean inSafeDataMode = mPreferences.getBoolean(PreferenceKeys.SAFE_DATA_MODE, false);
        if (inSafeDataMode) {
            if (mWifiManager.isWifiEnabled()) {
                WifiInfo wifiInfo = mWifiManager.getConnectionInfo();
                return wifiInfo == null || wifiInfo.getNetworkId() == -1;
            } else {
                return true;
            }
        }
        return false;
    }

    public boolean inMobileApiMode() {
        return mPreferences.getBoolean(PreferenceKeys.MOBILE_API_MODE, false);
    }

    public void setNightMode(boolean night) {
        mPreferences.edit().putBoolean(PreferenceKeys.NIGHT_MODE, night).apply();
        AppCompatDelegate.setDefaultNightMode(night ?
                AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO);
    }

    public boolean inNightMode() {
        return mPreferences.getBoolean(PreferenceKeys.NIGHT_MODE, false);
    }

    public boolean inAnimationMode() {
        return mPreferences.getBoolean(PreferenceKeys.ANIMATION_MODE, true);
    }

    public void setContentTextLevel(int level) {
        mPreferences.edit().putInt(PreferenceKeys.CONTENT_TEXT_LEVEL_INT, level).apply();
    }

    public int getContentTextLevel() {
        return mPreferences.getInt(PreferenceKeys.CONTENT_TEXT_LEVEL_INT, 1);
    }
}
