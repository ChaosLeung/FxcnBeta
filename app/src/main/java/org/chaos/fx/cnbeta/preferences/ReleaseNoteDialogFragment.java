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
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.preference.PreferenceDialogFragmentCompat;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.chaos.fx.cnbeta.R;

import java.io.IOException;
import java.io.InputStream;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author Chaos
 *         20/02/2017
 */

public class ReleaseNoteDialogFragment extends PreferenceDialogFragmentCompat
        implements LoaderManager.LoaderCallbacks<CharSequence> {

    private static final String ARG_KEY = "key";

    public static ReleaseNoteDialogFragment newInstance(String key) {
        Bundle args = new Bundle();
        args.putString(ARG_KEY, key);
        ReleaseNoteDialogFragment fragment = new ReleaseNoteDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @BindView(R.id.release_note) TextView mTextView;
    @BindView(R.id.loading_view) ProgressBar mProgressBar;

    @Override
    protected void onBindDialogView(View view) {
        super.onBindDialogView(view);

        ButterKnife.bind(this, view);

        mProgressBar.setVisibility(View.VISIBLE);
        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public void onDialogClosed(boolean positiveResult) {
        // no-op
    }

    @Override
    public Loader<CharSequence> onCreateLoader(int id, Bundle args) {
        return new ReleaseNoteLoader(getContext());
    }

    @Override
    public void onLoadFinished(Loader<CharSequence> loader, CharSequence data) {
        mTextView.setText(data);
        mProgressBar.setVisibility(View.GONE);
    }

    @Override
    public void onLoaderReset(Loader<CharSequence> loader) {
        // no-op
    }

    private static class ReleaseNoteLoader extends AsyncTaskLoader<CharSequence> {

        private ReleaseNoteLoader(Context context) {
            super(context);
        }

        @Override
        protected void onStopLoading() {
            cancelLoad();
        }

        @Override
        protected void onStartLoading() {
            forceLoad();
        }

        @Override
        public CharSequence loadInBackground() {
            return ReleaseNoteParser.parse(readReleaseNote());
        }

        private String readReleaseNote() {
            try {
                InputStream is = getContext().getAssets().open("release_note.html");
                int size = is.available();

                byte[] buffer = new byte[size];
                is.read(buffer);
                is.close();

                return new String(buffer);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return "";
        }
    }
}
