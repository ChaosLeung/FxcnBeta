package org.chaos.fx.cnbeta;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

/**
 * @author Chaos
 *         2015/11/15.
 */
public class ContentActivity extends AppCompatActivity {

    private static final String KEY_SID = "sid";

    public static void start(Context context, int sid) {
        Intent intent = new Intent(context, ContentActivity.class);
        intent.putExtra(KEY_SID, sid);
        context.startActivity(intent);
    }

    private int mSid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mSid = getIntent().getIntExtra(KEY_SID, -1);
    }
}
