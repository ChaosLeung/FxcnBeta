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

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.preference.PreferenceDialogFragmentCompat;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.view.View;
import android.widget.TextView;

import org.chaos.fx.cnbeta.R;
import org.jsoup.Jsoup;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author Chaos
 *         20/02/2017
 */

public class ReleaseNoteDialogFragment extends PreferenceDialogFragmentCompat {

    public static ReleaseNoteDialogFragment newInstance(String key) {
        Bundle args = new Bundle();
        args.putString(ARG_KEY, key);
        ReleaseNoteDialogFragment fragment = new ReleaseNoteDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }

    private TextView mTextView;

    @Override
    protected void onBindDialogView(View view) {
        super.onBindDialogView(view);

        mTextView = (TextView) view.findViewById(R.id.release_note);
        mTextView.setText(ReleaseNoteParser.parse(readReleaseNote()));
    }

    @Override
    public void onDialogClosed(boolean positiveResult) {
        // no-op
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

class ReleaseNoteParser {

    static CharSequence parse(String html) {
        Elements elements = Jsoup.parse(html).body().children();
        SpannableStringBuilder ssb = new SpannableStringBuilder();
        for (int i = 0; i < elements.size(); i += 2) {
            String version = elements.get(i).text();
            addSpanFromText(ssb, version, new RelativeSizeSpan(1.2f), new StyleSpan(Typeface.BOLD));
            ssb.append("\n");

            Elements ol = elements.get(i + 1).getElementsByTag("li");
            StringBuilder sb = new StringBuilder();
            for (int j = 0; j < ol.size(); j++) {
                sb.append("  •  ").append(ol.get(j).text()).append("\n");
            }
            ssb.append(sb).append("\n");
        }
        if (ssb.length() > 0) {
            ssb.delete(ssb.length() - 2, ssb.length());
        }
        return ssb;
    }

    private static void addSpanFromText(SpannableStringBuilder target, CharSequence text, Object... spans) {
        int where = target.length();
        target.append(text);
        int len = target.length();
        if (where != len) {
            for (Object span : spans) {
                target.setSpan(span, where, len, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
    }
}
