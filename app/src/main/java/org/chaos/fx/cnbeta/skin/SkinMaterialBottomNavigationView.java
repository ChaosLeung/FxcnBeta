package org.chaos.fx.cnbeta.skin;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.AttributeSet;

import skin.support.widget.SkinCompatBackgroundHelper;

/**
 * @author Chaos
 * 2018/6/25
 */
public class SkinMaterialBottomNavigationView extends skin.support.design.widget.SkinMaterialBottomNavigationView {

    private SkinCompatBackgroundHelper mBackgroundTintHelper;

    public SkinMaterialBottomNavigationView(@NonNull Context context) {
        this(context, null);
    }

    public SkinMaterialBottomNavigationView(@NonNull Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SkinMaterialBottomNavigationView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mBackgroundTintHelper = new SkinCompatBackgroundHelper(this);
        mBackgroundTintHelper.loadFromAttributes(attrs, defStyleAttr);
    }

    @Override
    public void applySkin() {
        mBackgroundTintHelper.applySkin();
        super.applySkin();
    }
}
