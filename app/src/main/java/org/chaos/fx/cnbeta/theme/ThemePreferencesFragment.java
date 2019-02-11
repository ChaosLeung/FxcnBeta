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

import android.annotation.SuppressLint;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.PreferenceManager;
import android.view.View;
import android.widget.TimePicker;

import org.chaos.fx.cnbeta.R;
import org.chaos.fx.cnbeta.preferences.PreferenceHelper;
import org.chaos.fx.cnbeta.preferences.PreferenceKeys;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import skin.support.SkinCompatManager;
import skin.support.content.res.SkinCompatResources;
import skin.support.observe.SkinObservable;
import skin.support.observe.SkinObserver;

public class ThemePreferencesFragment extends PreferenceFragmentCompat implements
        Preference.OnPreferenceClickListener, Preference.OnPreferenceChangeListener,
        SharedPreferences.OnSharedPreferenceChangeListener, SkinObserver {

    private static final String TAG = "ThemePreferencesFragmen";

    private static final String DIALOG_FRAGMENT_TAG = "ThemePreferencesFragment.DIALOG";

    private static final String NIGHT_THEME_TIME_LABEL = "night_theme_time_label";

    @SuppressLint("SimpleDateFormat") private SimpleDateFormat mTimeFormat = new SimpleDateFormat("HH:mm");
    private final TimeZone GMT = TimeZone.getTimeZone("GMT");

    private Preference mStartTimePreference;
    private Preference mEndTimePreference;
    private Preference mTimeLabelPreference;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        updateDivider();
        PreferenceManager.getDefaultSharedPreferences(getActivity()).registerOnSharedPreferenceChangeListener(this);
        SkinCompatManager.getInstance().addObserver(this);

        Fragment f = getFragmentManager().findFragmentByTag(DIALOG_FRAGMENT_TAG);
        if (f != null) {
            getFragmentManager().beginTransaction().remove(f).commitAllowingStateLoss();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        SkinCompatManager.getInstance().deleteObserver(this);
        PreferenceManager.getDefaultSharedPreferences(getActivity()).unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (PreferenceKeys.AUTO_SWITCH_THEME.equals(preference.getKey())) {
            boolean checked = (boolean) newValue;
            onStateChanged(checked);
            return true;
        }
        return false;
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        if (PreferenceKeys.NIGHT_MODE_START_TIME.equals(preference.getKey())) {
            showTimePicker(PreferenceHelper.getInstance().getNightModeStartTime(), new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                    long timeInMillis = TimeUnit.HOURS.toMillis(hourOfDay) + TimeUnit.MINUTES.toMillis(minute);
                    PreferenceHelper.getInstance().setNightModeStartTime(timeInMillis);
                }
            });
            return true;
        } else if (PreferenceKeys.NIGHT_MODE_END_TIME.equals(preference.getKey())) {
            showTimePicker(PreferenceHelper.getInstance().getNightModeEndTime(), new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                    long timeInMillis = TimeUnit.HOURS.toMillis(hourOfDay) + TimeUnit.MINUTES.toMillis(minute);
                    PreferenceHelper.getInstance().setNightModeEndTime(timeInMillis);
                }
            });
            return true;
        }
        return false;
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.pref_theme, rootKey);

        mTimeFormat.setTimeZone(GMT);

        mStartTimePreference = findPreference(PreferenceKeys.NIGHT_MODE_START_TIME);
        mEndTimePreference = findPreference(PreferenceKeys.NIGHT_MODE_END_TIME);
        mTimeLabelPreference = findPreference(NIGHT_THEME_TIME_LABEL);

        updateTimeSummary(true);
        updateTimeSummary(false);

        mStartTimePreference.setOnPreferenceClickListener(this);
        mEndTimePreference.setOnPreferenceClickListener(this);
        findPreference(PreferenceKeys.AUTO_SWITCH_THEME).setOnPreferenceChangeListener(this);

        onStateChanged(PreferenceHelper.getInstance().isAutoSwitchTheme());
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (PreferenceKeys.NIGHT_MODE_START_TIME.equals(key)) {
            updateTimeSummary(true);
            updateThemeIfNeed();
        } else if (PreferenceKeys.NIGHT_MODE_END_TIME.equals(key)) {
            updateTimeSummary(false);
            updateThemeIfNeed();
        } else if (PreferenceKeys.AUTO_SWITCH_THEME.equals(key)) {
            if (!PreferenceHelper.getInstance().isAutoSwitchTheme()) {
                ThemeHelper.cancelThemeAlarm(getActivity());
            }
            updateThemeIfNeed();
        }
    }

    private void updateThemeIfNeed() {
        if (PreferenceHelper.getInstance().isAutoSwitchTheme()) {
            ThemeHelper.cancelThemeAlarm(getActivity());
            ThemeHelper.alarmToSwitchTheme(getActivity());
        }
    }

    private void updateTimeSummary(boolean isStart) {
        if (isStart) {
            updateTimeSummary(mStartTimePreference, PreferenceHelper.getInstance().getNightModeStartTime());
        } else {
            updateTimeSummary(mEndTimePreference, PreferenceHelper.getInstance().getNightModeEndTime());
        }
    }

    private void updateTimeSummary(Preference preference, long timeInMillis) {
        preference.setSummary(mTimeFormat.format(new Date(timeInMillis)));
    }

    @Override
    public void updateSkin(SkinObservable observable, Object o) {
        Context host = getActivity();
        if (host == null) {
            return;
        }

        updateDivider();
    }

    private void updateDivider() {
        Context host = getActivity();
        if (host != null) {
            setDivider(SkinCompatResources.getDrawable(host, R.drawable.list_divider));
        }
    }

    private void onStateChanged(boolean isEnable) {
        mStartTimePreference.setEnabled(isEnable);
        mEndTimePreference.setEnabled(isEnable);
        mTimeLabelPreference.setEnabled(isEnable);
    }

    private void showTimePicker(long timeInMills, TimePickerDialog.OnTimeSetListener listener) {
        Calendar calendar = Calendar.getInstance(GMT);
        calendar.setTimeInMillis(timeInMills);
        TimePickerDialogFragment f = TimePickerDialogFragment.newInstance(calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE));
        f.setTimeSetListener(listener);
        f.setTargetFragment(this, 0);
        f.show(getFragmentManager(), DIALOG_FRAGMENT_TAG);
    }
}
