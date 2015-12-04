package org.chaos.fx.cnbeta.help;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.umeng.fb.FeedbackAgent;
import com.umeng.fb.fragment.FeedbackFragment;

import org.chaos.fx.cnbeta.R;

import butterknife.Bind;
import butterknife.ButterKnife;
import me.imid.swipebacklayout.lib.app.SwipeBackActivity;

/**
 * @author Chaos
 *         2015/11/29.
 */
public class FeedbackActivity extends SwipeBackActivity implements TextWatcher {

    private static final int MAX_CONTENT_LEN = 200;

    private FeedbackFragment mFeedbackFragment;
    private EditText mUmengEdit;
    private Button mUmengSendBtn;

    @Bind(R.id.name) EditText mNameEdit;
    @Bind(R.id.contact_info) EditText mContactEdit;
    @Bind(R.id.feedback) EditText mContentEdit;
    @Bind(R.id.fb_current_length) TextView mCurrentLenView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);

        setupActionBar();

        ButterKnife.bind(this);
        mContentEdit.addTextChangedListener(this);

        mFeedbackFragment = (FeedbackFragment) getSupportFragmentManager().findFragmentById(R.id.container);
        if (savedInstanceState == null) {
            mFeedbackFragment = FeedbackFragment.newInstance(
                    new FeedbackAgent(this).getDefaultConversation().getId());
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, mFeedbackFragment)
                    .commit();
        }
    }

    private static final String TAG = "FeedbackActivity";
    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy() called with: " + "");
    }

    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
        View rootView = mFeedbackFragment.getView();
        if (rootView != null) {
            mUmengEdit = (EditText) rootView.findViewById(R.id.umeng_fb_send_content);
            mUmengSendBtn = (Button) rootView.findViewById(R.id.umeng_fb_send_btn);
        }
    }

    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(R.string.nav_help);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        mFeedbackFragment.refresh();
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
            mUmengEdit.setText(String.format("{name:%1$s,contact_info:%2$s,feedback:%3$s}",
                    mNameEdit.getText().toString(),
                    mContactEdit.getText().toString(),
                    content));
            mUmengSendBtn.performClick();
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
