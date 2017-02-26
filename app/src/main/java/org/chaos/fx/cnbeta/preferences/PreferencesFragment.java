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

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.preference.DialogPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;

import org.chaos.fx.cnbeta.BuildConfig;
import org.chaos.fx.cnbeta.R;
import org.chaos.fx.cnbeta.help.FeedbackActivity;

/**
 * @author Chaos
 *         15/02/2017
 */

public class PreferencesFragment extends PreferenceFragmentCompat implements
        Preference.OnPreferenceClickListener, PreferenceFragmentCompat.OnPreferenceDisplayDialogCallback,
        Preference.OnPreferenceChangeListener {

    private static final String DIALOG_FRAGMENT_TAG = "PreferencesFragment.DIALOG";

    private static final String KEY_HELP_AND_FEEDBACK = "help_and_feedback";
    private static final String KEY_RELEASE_NOTE = "release_note";
    private static final String KEY_LICENSE = "license";
    private static final String KEY_NIGHT_MODE = "night_mode";

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.pref_general, rootKey);

        findPreference("version_name").setSummary(BuildConfig.VERSION_NAME);
        findPreference(KEY_HELP_AND_FEEDBACK).setOnPreferenceClickListener(this);
        findPreference(KEY_NIGHT_MODE).setOnPreferenceChangeListener(this);
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        String key = preference.getKey();
        if (KEY_HELP_AND_FEEDBACK.equals(key)) {
            startActivity(new Intent(getActivity(), FeedbackActivity.class));
            return true;
        }
        return false;
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        String key = preference.getKey();
        if (KEY_NIGHT_MODE.equals(key)) {
            AppCompatDelegate.setDefaultNightMode((boolean) newValue ?
                    AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO);
            getActivity().recreate();
            return true;
        }
        return false;
    }

    @Override
    public boolean onPreferenceDisplayDialog(PreferenceFragmentCompat caller, Preference pref) {
        // check if dialog is already showing
        if (getFragmentManager().findFragmentByTag(DIALOG_FRAGMENT_TAG) != null) {
            return true;
        }

        if (pref instanceof DialogPreference) {
            String key = pref.getKey();
            final DialogFragment f;
            if (KEY_RELEASE_NOTE.equals(key)) {
                f = ReleaseNoteDialogFragment.newInstance(key);
            } else if (KEY_LICENSE.equals(key)) {
                f = LicenseDialogFragmentProvider.newFragment(getActivity());
            } else {
                throw new IllegalArgumentException("Tried to display dialog for unknown preference key.");
            }
            f.setTargetFragment(this, 0);
            f.show(getFragmentManager(), DIALOG_FRAGMENT_TAG);
            return true;
        }
        return false;
    }

    @Override
    public Fragment getCallbackFragment() {
        return this;
    }
}
