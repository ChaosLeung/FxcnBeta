package org.chaos.fx.cnbeta.skin;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.View;

/**
 * @author Chaos
 * 2018/6/25
 */
public class SkinMaterialViewInflater extends skin.support.design.app.SkinMaterialViewInflater {
    @Override
    public View createView(@NonNull Context context, String name, @NonNull AttributeSet attrs) {
        if ("android.support.design.widget.BottomNavigationView".equals(name)) {
            return new SkinMaterialBottomNavigationView(context, attrs);
        }
        return super.createView(context, name, attrs);
    }
}
