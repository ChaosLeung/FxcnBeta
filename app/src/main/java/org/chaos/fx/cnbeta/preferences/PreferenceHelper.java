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

import java.util.concurrent.TimeUnit;

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
//        return mPreferences.getBoolean(PreferenceKeys.MOBILE_API_MODE, false);
        // 由于 cnBeta 一部分文章用 WebApi 加载时跳转过多时会出现 564 错误，
        // 另外由于实名制 cnBeta 需登录评论，所以 WebApi 已经没有太多存在的必要
        // 所以直接使用 MobileApi 就好
        return true;
    }

    public void setNightMode(boolean night) {
        mPreferences.edit().putBoolean(PreferenceKeys.NIGHT_MODE, night).apply();
    }

    public boolean inNightMode() {
        return mPreferences.getBoolean(PreferenceKeys.NIGHT_MODE, false);
    }

    public boolean isListItemAlignStart() {
        return mPreferences.getBoolean(PreferenceKeys.LIST_ITEM_IMAGE_ALIGN_START, false);
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

    public boolean inHideBarsAutomaticallyMode() {
        return mPreferences.getBoolean(PreferenceKeys.HIDE_BARS_AUTOMATICALLY_MODE, true);
    }

    public void setAutoSwitchTheme(boolean autoSwitch) {
        mPreferences.edit().putBoolean(PreferenceKeys.AUTO_SWITCH_THEME, autoSwitch).apply();
    }

    public boolean isAutoSwitchTheme() {
        return mPreferences.getBoolean(PreferenceKeys.AUTO_SWITCH_THEME, false);
    }

    public void setNightModeStartTime(long millis) {
        mPreferences.edit().putLong(PreferenceKeys.NIGHT_MODE_START_TIME, millis).apply();
    }

    public long getNightModeStartTime() {
        return mPreferences.getLong(PreferenceKeys.NIGHT_MODE_START_TIME, TimeUnit.HOURS.toMillis(19));
    }

    public void setNightModeEndTime(long millis) {
        mPreferences.edit().putLong(PreferenceKeys.NIGHT_MODE_END_TIME, millis).apply();
    }

    public long getNightModeEndTime() {
        return mPreferences.getLong(PreferenceKeys.NIGHT_MODE_END_TIME, TimeUnit.HOURS.toMillis(7));
    }
}
