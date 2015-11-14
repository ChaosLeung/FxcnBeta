package org.chaos.fx.cnbeta;

import android.app.Application;

import org.chaos.fx.cnbeta.util.TimeStringHelper;

/**
 * @author Chaos
 *         2015/11/14.
 */
public class FxCBApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        TimeStringHelper.initialize(this);
    }
}
