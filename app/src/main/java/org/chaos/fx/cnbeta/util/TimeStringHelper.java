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
import android.support.annotation.PluralsRes;

import org.chaos.fx.cnbeta.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * @author Chaos
 *         2015/11/14.
 */
public class TimeStringHelper {

    private static Resources sResources;
    private static final SimpleDateFormat DEFAULT_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss zzz", Locale.CHINA);
    private static final SimpleDateFormat CN_DATE_FORMAT = new SimpleDateFormat("yyyy年MM月dd日 HH:mm zzz", Locale.CHINA);

    private static final String CHINA_TIMEZONE_SUFFIX = " GMT+08:00";

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

    public static String getTimeStrByDefaultTimeStr(String pubTime) {
        try {
            return getTimeString(parseFormattedTimeStr(pubTime));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String getTimeString(long time) {
        String pubTime;
        long deltaTime = System.currentTimeMillis() - time;
        if (deltaTime >= YEAR) {
            int year = (int) (deltaTime / YEAR);
            pubTime = getResString(R.plurals.time_year, year);
        } else if (deltaTime >= MONTH) {
            int month = (int) (deltaTime / MONTH);
            pubTime = getResString(R.plurals.time_month, month);
        } else if (deltaTime >= WEEK) {
            int week = (int) (deltaTime / WEEK);
            pubTime = getResString(R.plurals.time_week, week);
        } else if (deltaTime >= DAY) {
            int day = (int) (deltaTime / DAY);
            pubTime = getResString(R.plurals.time_day, day);
        } else if (deltaTime >= HOUR) {
            int hour = (int) (deltaTime / HOUR);
            pubTime = getResString(R.plurals.time_hour, hour);
        } else if (deltaTime >= MINUTE) {
            int minute = (int) (deltaTime / MINUTE);
            pubTime = getResString(R.plurals.time_minute, minute);
        } else if (deltaTime >= 0) {
            pubTime = sResources.getString(R.string.time_second);
        } else {
            pubTime = sResources.getString(R.string.time_unknown);
        }
        return pubTime;
    }

    public static long parseFormattedTimeStr(String formattedTime) throws ParseException {
        if (!formattedTime.endsWith(CHINA_TIMEZONE_SUFFIX)) {
            formattedTime += CHINA_TIMEZONE_SUFFIX;
        }
        return DEFAULT_DATE_FORMAT.parse(formattedTime).getTime();
    }

    public static long parseCNFormattedTimeStr(String cnFormattedTime) throws ParseException {
        if (!cnFormattedTime.endsWith(CHINA_TIMEZONE_SUFFIX)) {
            cnFormattedTime += CHINA_TIMEZONE_SUFFIX;
        }
        return CN_DATE_FORMAT.parse(cnFormattedTime).getTime();
    }

    public static String cnTime2DefaultTime(String cnFormattedTime) throws ParseException {
        long time = parseCNFormattedTimeStr(cnFormattedTime);
        return DEFAULT_DATE_FORMAT.format(new Date(time));
    }

    private static String getResString(@PluralsRes int pluralsId, int value) {
        return sResources.getQuantityString(pluralsId, value, value);
    }
}
