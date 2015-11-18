package org.chaos.fx.cnbeta.hotcomment;

import android.os.Bundle;

import org.chaos.fx.cnbeta.R;
import org.chaos.fx.cnbeta.app.BaseFragment;

/**
 * @author Chaos
 *         2015/11/15.
 */
public class HotCommentFragment extends BaseFragment {

    public static HotCommentFragment newInstance(){
        return new HotCommentFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setTitle(R.string.nav_hot_comments);
    }
}
