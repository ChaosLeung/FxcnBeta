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
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.preference.DialogPreference;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.preference.TwoStatePreference;
import android.view.View;
import android.widget.Toast;

import org.chaos.fx.cnbeta.BuildConfig;
import org.chaos.fx.cnbeta.R;
import org.chaos.fx.cnbeta.theme.ThemeHelper;
import org.chaos.fx.cnbeta.theme.ThemePreferencesActivity;

import java.io.File;

import de.psdev.licensesdialog.LicensesDialogFragment;
import skin.support.SkinCompatManager;
import skin.support.content.res.SkinCompatResources;
import skin.support.observe.SkinObservable;
import skin.support.observe.SkinObserver;

/**
 * @author Chaos
 *         15/02/2017
 */

public class PreferencesFragment extends PreferenceFragmentCompat implements
        Preference.OnPreferenceClickListener, PreferenceFragmentCompat.OnPreferenceDisplayDialogCallback,
        Preference.OnPreferenceChangeListener, SharedPreferences.OnSharedPreferenceChangeListener,
        SkinObserver {

    private static final String TAG = "PreferencesFragment";

    private static final String DIALOG_FRAGMENT_TAG = "PreferencesFragment.DIALOG";

    private static final String KEY_HELP_AND_FEEDBACK = "help_and_feedback";
    private static final String KEY_RELEASE_NOTE = "release_note";
    private static final String KEY_LICENSE = "license";
    private static final String KEY_AUTO_SWITCH_THEME = "auto_switch_theme_parent";
    private static final String KEY_VERSION_NAME = "version_name";
    private static final String KEY_CLEAR_CACHE = "clear_cache";

    private static final String PICASSO_CACHE_FOLDER = "picasso-cache";

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
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        updateDivider();
        PreferenceManager.getDefaultSharedPreferences(getActivity()).registerOnSharedPreferenceChangeListener(this);
        SkinCompatManager.getInstance().addObserver(this);
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.pref_general, rootKey);

        findPreference(KEY_VERSION_NAME).setSummary(BuildConfig.VERSION_NAME);
        findPreference(KEY_HELP_AND_FEEDBACK).setOnPreferenceClickListener(this);
        findPreference(PreferenceKeys.CONTENT_TEXT_LEVEL).setOnPreferenceChangeListener(this);
        findPreference(KEY_CLEAR_CACHE).setOnPreferenceClickListener(this);
        findPreference(KEY_AUTO_SWITCH_THEME).setOnPreferenceClickListener(this);
        findPreference(PreferenceKeys.NIGHT_MODE).setOnPreferenceClickListener(this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        SkinCompatManager.getInstance().deleteObserver(this);
        PreferenceManager.getDefaultSharedPreferences(getActivity()).unregisterOnSharedPreferenceChangeListener(this);
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
            return true;
        } else if (KEY_AUTO_SWITCH_THEME.equals(key)) {
            startActivity(new Intent(getActivity(), ThemePreferencesActivity.class));
            return true;
        } else if (PreferenceKeys.NIGHT_MODE.equals(key)) {
            ThemeHelper.disableAutoSwitch(getActivity());
            return true;
        }
        return false;
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        String key = preference.getKey();
        if (PreferenceKeys.CONTENT_TEXT_LEVEL.equals(key)) {
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
        File picassoCacheDir = new File(getContext().getCacheDir(), PICASSO_CACHE_FOLDER);
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

    private void updateDivider() {
        Context host = getActivity();
        if (host != null) {
            setDivider(SkinCompatResources.getDrawable(host, R.drawable.list_divider));
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (PreferenceKeys.NIGHT_MODE.equals(key)) {
            TwoStatePreference preference = ((TwoStatePreference) findPreference(PreferenceKeys.NIGHT_MODE));
            boolean isNight = PreferenceHelper.getInstance().inNightMode();
            preference.setChecked(isNight);
            preference.callChangeListener(isNight);
        }
    }

    @Override
    public void updateSkin(SkinObservable observable, Object o) {
        Context host = getActivity();
        if (host == null) {
            return;
        }
        Fragment f = getFragmentManager().findFragmentByTag(DIALOG_FRAGMENT_TAG);
        if (f != null && f.getClass() == LicensesDialogFragment.class) {
            ((LicensesDialogFragment) f).getLicensesDialog().switchToNight(PreferenceHelper.getInstance().inNightMode());
        }
        setDivider(SkinCompatResources.getDrawable(host, R.drawable.list_divider));
    }
}
