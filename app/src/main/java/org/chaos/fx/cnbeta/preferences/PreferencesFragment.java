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
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.preference.DialogPreference;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.widget.Toast;

import org.chaos.fx.cnbeta.BuildConfig;
import org.chaos.fx.cnbeta.R;

import java.io.File;

/**
 * @author Chaos
 *         15/02/2017
 */

public class PreferencesFragment extends PreferenceFragmentCompat implements
        Preference.OnPreferenceClickListener, PreferenceFragmentCompat.OnPreferenceDisplayDialogCallback,
        Preference.OnPreferenceChangeListener {

    private static final String TAG = "PreferencesFragment";

    private static final String DIALOG_FRAGMENT_TAG = "PreferencesFragment.DIALOG";

    private static final String KEY_HELP_AND_FEEDBACK = "help_and_feedback";
    private static final String KEY_RELEASE_NOTE = "release_note";
    private static final String KEY_LICENSE = "license";
    private static final String KEY_NIGHT_MODE = "night_mode";
    private static final String KEY_VERSION_NAME = "version_name";
    private static final String KEY_CLEAR_CACHE = "clear_cache";

    private static final String PICASSO_CACHE_FLODER = "picasso-cache";

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        // Note: http://stackoverflow.com/a/38548386/3351990
        // DialogFragment keeps holding a reference to PreferencesFragment which
        // doesn't exist anymore when device rotation. In this case, we have to
        // reset DialogFragment's target to be the new PreferencesFragment instance.
        Fragment dialog = getFragmentManager().findFragmentByTag(DIALOG_FRAGMENT_TAG);
        if (dialog != null) {
            dialog.setTargetFragment(this, 0);
        }
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.pref_general, rootKey);

        findPreference(KEY_VERSION_NAME).setSummary(BuildConfig.VERSION_NAME);
        findPreference(KEY_HELP_AND_FEEDBACK).setOnPreferenceClickListener(this);
        findPreference(KEY_NIGHT_MODE).setOnPreferenceChangeListener(this);
        findPreference(PreferenceKeys.CONTENT_TEXT_LEVEL).setOnPreferenceChangeListener(this);
        findPreference(KEY_CLEAR_CACHE).setOnPreferenceClickListener(this);
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        String key = preference.getKey();
        if (KEY_HELP_AND_FEEDBACK.equals(key)) {
            // may be used in the future
//            startActivity(new Intent(getActivity(), FeedbackActivity.class));
            composeEmail(getString(R.string.email), getString(R.string.feedback_subject));
            return true;
        } else if (KEY_CLEAR_CACHE.equals(key)) {
            clearCache();
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
        } else if (PreferenceKeys.CONTENT_TEXT_LEVEL.equals(key)) {
            ListPreference p = (ListPreference) preference;
            PreferenceHelper.getInstance().setContentTextLevel(p.findIndexOfValue((String) newValue));
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
                return false;
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

    private void composeEmail(String address, String subject) {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:")); // only email apps should handle this
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{address});
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    private void clearCache() {
        File picassoCacheDir = new File(getContext().getCacheDir(), PICASSO_CACHE_FLODER);
        int messageId = R.string.clear_cache_failed;
        if (!picassoCacheDir.exists() || deleteDir(picassoCacheDir)) {
            messageId = R.string.clear_cache_success;
        }
        Toast.makeText(getActivity(), messageId, Toast.LENGTH_SHORT).show();
    }

    public static boolean deleteDir(File dir) {
        if (dir.isDirectory()) {
            String[] files = dir.list();
            for (String file : files) {
                if (!deleteDir(new File(dir, file))) {
                    return false;
                }
            }
        }
        return dir.delete();
    }
}
