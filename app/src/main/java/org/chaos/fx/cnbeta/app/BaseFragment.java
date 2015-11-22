package org.chaos.fx.cnbeta.app;

import android.app.Activity;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;

/**
 * @author Chaos
 *         2015/11/15.
 */
public class BaseFragment extends Fragment {

    public ActionBar getSupportActionBar() {
        Activity activity = getActivity();
        if (activity != null && activity instanceof AppCompatActivity) {
            ActionBar actionBar = ((AppCompatActivity) activity).getSupportActionBar();
            if (actionBar != null) {
                return actionBar;
            }
        }
        return null;
    }

    public void setActionBarTitle(String title) {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(title);
        }
    }

    public void setActionBarTitle(@StringRes int strRes) {
        setActionBarTitle(getString(strRes));
    }
}
