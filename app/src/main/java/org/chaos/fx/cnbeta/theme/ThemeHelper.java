/*
 * Copyright 2019 Chaos
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

package org.chaos.fx.cnbeta.theme;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;

import org.chaos.fx.cnbeta.SkinReceiver;
import org.chaos.fx.cnbeta.preferences.PreferenceHelper;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import skin.support.SkinCompatManager;

public class ThemeHelper {

    public static void switchThemeByPreference() {
        switchTheme(PreferenceHelper.getInstance().inNightMode());
    }

    private static void switchTheme(boolean isNight) {
        if (isNight) {
            SkinCompatManager.getInstance().loadSkin("night", null, SkinCompatManager.SKIN_LOADER_STRATEGY_BUILD_IN);
        } else {
            SkinCompatManager.getInstance().restoreDefaultTheme();
        }
    }

    private static final int REQUEST_SWITCH_THEME = 1;

    public static void alarmToSwitchTheme(Context c) {
        long start = PreferenceHelper.getInstance().getNightModeStartTime();
        long end = PreferenceHelper.getInstance().getNightModeEndTime();

        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        int second = calendar.get(Calendar.SECOND);
        long current = TimeUnit.HOURS.toMillis(hour) + TimeUnit.MINUTES.toMillis(minute) + TimeUnit.SECONDS.toMillis(second);

        boolean inNightTime;
        if (end > start) {
            inNightTime = current >= start && current < end;
        } else {
            inNightTime = current >= start || current < end;
        }

        long offset;
        if (inNightTime) {
            offset = end - current;
        } else {
            offset = start - current;
        }
        if (offset < 0) {
            offset += TimeUnit.DAYS.toMillis(1);
        }

        PreferenceHelper.getInstance().setNightMode(inNightTime);

        Intent intent = new Intent(c.getApplicationContext(), SkinReceiver.class);
        intent.setAction(SkinReceiver.ACTION_THEME_SWITCH);
        intent.putExtra(SkinReceiver.EXTRA_NIGHT_THEME, !inNightTime);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(c, REQUEST_SWITCH_THEME, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager am = (AlarmManager) c.getSystemService(Context.ALARM_SERVICE);

        am.setExact(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime() + offset, pendingIntent);
    }

    public static void disableAutoSwitch(Context c) {
        PreferenceHelper.getInstance().setAutoSwitchTheme(false);
        cancelThemeAlarm(c);
    }

    public static void cancelThemeAlarm(Context c) {
        Intent intent = new Intent(c.getApplicationContext(), SkinReceiver.class);
        intent.setAction(SkinReceiver.ACTION_THEME_SWITCH);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(c, REQUEST_SWITCH_THEME, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager am = (AlarmManager) c.getSystemService(Context.ALARM_SERVICE);
        am.cancel(pendingIntent);
    }
}
