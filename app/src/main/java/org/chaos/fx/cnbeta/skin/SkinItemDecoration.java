package org.chaos.fx.cnbeta.skin;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import org.chaos.fx.cnbeta.R;

import skin.support.SkinCompatManager;
import skin.support.content.res.SkinCompatResources;
import skin.support.observe.SkinObservable;
import skin.support.observe.SkinObserver;
import skin.support.widget.SkinCompatHelper;

import static skin.support.widget.SkinCompatHelper.INVALID_ID;

/**
 * @author Chaos
 * 2018/6/26
 */
public class SkinItemDecoration extends DividerItemDecoration implements SkinObserver, View.OnAttachStateChangeListener {

    private static final String TAG = "SkinItemDecoration";

    private static final int[] ATTRS = new int[]{android.R.attr.listDivider};

    private boolean isRegister;

    private int mDividerResId;

    private RecyclerView mRecyclerView;
    private Context mContext;

    public SkinItemDecoration(Context context, int orientation) {
        super(context, orientation);
        mContext = context;
        final TypedArray a = context.obtainStyledAttributes(ATTRS);
        mDividerResId = a.getResourceId(0, R.drawable.list_divider);
        a.recycle();

        applyDividerResource();
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDraw(c, parent, state);
        mRecyclerView = parent;
        if (!isRegister) {
            isRegister = true;
            parent.addOnAttachStateChangeListener(this);
            SkinCompatManager.getInstance().addObserver(this);
        }
    }

    @Override
    public void onViewAttachedToWindow(View v) {
        // do nothing
    }

    @Override
    public void onViewDetachedFromWindow(View v) {
        SkinCompatManager.getInstance().deleteObserver(this);
        if (mRecyclerView != null) {
            mRecyclerView.removeOnAttachStateChangeListener(this);
        }
    }

    private void applyDividerResource() {
        mDividerResId = SkinCompatHelper.checkResourceId(mDividerResId);
        if (mDividerResId != INVALID_ID) {
            Drawable drawable = SkinCompatResources.getDrawableCompat(mContext, mDividerResId);
            if (drawable != null) {
                setDrawable(drawable);
                if (mRecyclerView != null) {
                    mRecyclerView.requestLayout();
                }
            }
        }
    }

    @Override
    public void updateSkin(SkinObservable observable, Object o) {
        applyDividerResource();
    }
}
