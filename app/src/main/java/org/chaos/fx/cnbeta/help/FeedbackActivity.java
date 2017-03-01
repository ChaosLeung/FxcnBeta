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

package org.chaos.fx.cnbeta.help;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.chaos.fx.cnbeta.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.imid.swipebacklayout.lib.app.SwipeBackActivity;

/**
 * @author Chaos
 *         2015/11/29.
 */
public class FeedbackActivity extends SwipeBackActivity implements TextWatcher {

    private static final int MAX_CONTENT_LEN = 200;

    @BindView(R.id.name) EditText mNameEdit;
    @BindView(R.id.contact_info) EditText mContactEdit;
    @BindView(R.id.feedback) EditText mContentEdit;
    @BindView(R.id.fb_current_length) TextView mCurrentLenView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);

        setupActionBar();

        ButterKnife.bind(this);
        mContentEdit.addTextChangedListener(this);
    }

    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(R.string.nav_help);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.feedback_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                scrollToFinishActivity();
                return true;
            case R.id.send:
                sendFeedback();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void sendFeedback() {
        String content = mContentEdit.getText().toString().trim();
        if (TextUtils.isEmpty(content)) {
            Toast.makeText(this, R.string.feedback_content_should_not_empty, Toast.LENGTH_SHORT).show();
        } else {
            // TODO: 01/03/2017 send feedback
            Toast.makeText(this, R.string.sent, Toast.LENGTH_SHORT).show();
            mContentEdit.setText("");
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        // no-op
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        mCurrentLenView.setText(Integer.toString(MAX_CONTENT_LEN - start - count));
    }

    @Override
    public void afterTextChanged(Editable s) {
        // no-op
    }
}
