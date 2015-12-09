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

package org.chaos.fx.cnbeta.util;

import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.StringRes;

import org.chaos.fx.cnbeta.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * @author Chaos
 *         2015/11/14.
 */
public class TimeStringHelper {

    private static Resources sResources;
    private static SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);

    private static final long SECOND = 1000;
    private static final long MINUTE = 60 * SECOND;
    private static final long HOUR = 60 * MINUTE;
    private static final long DAY = 24 * HOUR;
    private static final long WEEK = 7 * DAY;
    private static final long MONTH = (long) (30.5 * DAY);
    private static final long YEAR = 365 * DAY;

    public static void initialize(Context context) {
        sResources = context.getApplicationContext().getResources();
    }

    public static String getTimeString(String pubTime) {
        try {
            long deltaTime = System.currentTimeMillis() - parseFormattedTimeStr(pubTime);
            if (deltaTime >= YEAR) {
                int year = (int) (deltaTime / YEAR);
                if (year == 1) {
                    pubTime = getResString(R.string.time_year);
                } else {
                    pubTime = getResString(R.string.time_years, year);
                }
            } else if (deltaTime >= MONTH) {
                int month = (int) (deltaTime / MONTH);
                if (month == 1) {
                    pubTime = getResString(R.string.time_month);
                } else {
                    pubTime = getResString(R.string.time_months, month);
                }
            } else if (deltaTime >= WEEK) {
                int week = (int) (deltaTime / WEEK);
                if (week == 1) {
                    pubTime = getResString(R.string.time_week);
                } else {
                    pubTime = getResString(R.string.time_weeks, week);
                }
            } else if (deltaTime >= DAY) {
                int day = (int) (deltaTime / DAY);
                if (day == 1) {
                    pubTime = getResString(R.string.time_day);
                } else {
                    pubTime = getResString(R.string.time_days, day);
                }
            } else if (deltaTime >= HOUR) {
                int hour = (int) (deltaTime / HOUR);
                if (hour == 1) {
                    pubTime = getResString(R.string.time_hour);
                } else {
                    pubTime = getResString(R.string.time_hours, hour);
                }
            } else if (deltaTime >= MINUTE) {
                int minute = (int) (deltaTime / MINUTE);
                if (minute == 1) {
                    pubTime = getResString(R.string.time_minute);
                } else {
                    pubTime = getResString(R.string.time_minutes, minute);
                }
            } else {
                int second = (int) (deltaTime / SECOND);
                if (second <= 1) {
                    pubTime = getResString(R.string.time_second);
                } else {
                    pubTime = getResString(R.string.time_seconds, second);
                }
            }
        } catch (ParseException e) {
            // no-op
        }
        return pubTime;
    }

    private static long parseFormattedTimeStr(String formattedTime) throws ParseException {
        return sDateFormat.parse(formattedTime).getTime();
    }

    private static String getResString(@StringRes int strId, Object... args) {
        return String.format(sResources.getString(strId), args);
    }
}
