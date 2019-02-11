/*
 * Copyright 2018 Chaos
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

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import org.chaos.fx.cnbeta.preferences.PreferenceHelper;

import skin.support.SkinCompatManager;

public class SkinReceiver extends BroadcastReceiver {

    public static final String ACTION_THEME_SWITCH = "org.chaos.fx.cnbeta.ACTION_SWITCH_THEME";
    public static final String EXTRA_NIGHT_THEME = "is_night_theme";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (ACTION_THEME_SWITCH.equals(intent.getAction())) {
            boolean isNight = intent.getBooleanExtra(EXTRA_NIGHT_THEME, false);
            PreferenceHelper.getInstance().setNightMode(isNight);
            if (isNight) {
                SkinCompatManager.getInstance().loadSkin("night", null, SkinCompatManager.SKIN_LOADER_STRATEGY_BUILD_IN);
            } else {
                SkinCompatManager.getInstance().restoreDefaultTheme();
            }
        }
    }
}
