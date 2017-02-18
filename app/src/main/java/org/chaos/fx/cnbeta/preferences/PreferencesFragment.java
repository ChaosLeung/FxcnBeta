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
        Preference.OnPreferenceClickListener {

    private static final String KEY_HELP_AND_FEEDBACK = "help_and_feedback";

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.pref_general, rootKey);

        findPreference("version_name").setSummary(BuildConfig.VERSION_NAME);
        findPreference("author_email").setSummary("lgf42031@gmail.com");
        findPreference("help_and_feedback").setOnPreferenceClickListener(this);
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
}
