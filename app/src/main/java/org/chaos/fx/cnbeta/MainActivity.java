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

package org.chaos.fx.cnbeta;

import android.animation.ValueAnimator;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.DecelerateInterpolator;

import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnTabSelectListener;

import org.chaos.fx.cnbeta.home.ArticlesFragment;
import org.chaos.fx.cnbeta.hotarticles.Top10Fragment;
import org.chaos.fx.cnbeta.hotcomment.HotCommentFragment;
import org.chaos.fx.cnbeta.preferences.PreferenceHelper;
import org.chaos.fx.cnbeta.preferences.PreferenceKeys;
import org.chaos.fx.cnbeta.preferences.PreferencesActivity;
import org.chaos.fx.cnbeta.rank.RanksFragment;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    private static final int DURATION_EXPAND_TAB_LAYOUT = 300;
    private static final int DURATION_COLLAPSE_TAB_LAYOUT = 200;

    @BindView(R.id.toolbar) Toolbar mToolbar;
    @BindView(R.id.tabs) TabLayout mTabLayout;
    @BindView(R.id.bottom_bar) BottomBar mBottomBar;
    @BindView(R.id.pager) ViewPager mViewPager;

    private String[] mPageTitles;

    private ValueAnimator mExpandAnimator;
    private ValueAnimator mCollapseAnimator;

    private ValueAnimator.AnimatorUpdateListener mTabLayoutAnimatorListener = new ValueAnimator.AnimatorUpdateListener() {
        @Override
        public void onAnimationUpdate(ValueAnimator animation) {
            float alpha = 1;
            if (animation == mExpandAnimator) {
                alpha = animation.getAnimatedFraction();
            } else if (animation == mCollapseAnimator) {
                alpha = 1 - animation.getAnimatedFraction();
            }
            mTabLayout.setAlpha(alpha);
            mTabLayout.getLayoutParams().height = (int) animation.getAnimatedValue();
            mTabLayout.requestLayout();
        }
    };

    private SharedPreferences mDefaultPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_content);

        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);

        mPageTitles = new String[]{getString(R.string.nav_home), getString(R.string.nav_rank),
                getString(R.string.nav_hot_articles), getString(R.string.nav_hot_comments)};

        setTitle(mPageTitles[0]);
        mViewPager.setAdapter(new PagerAdapter(getSupportFragmentManager()));
        mViewPager.setOffscreenPageLimit(4);
        mViewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                if (position == 1) {// RankFragment
                    expandTabLayout();
                } else if (mTabLayout.getHeight() != 0) {
                    collapseTabLayout();
                }

                mBottomBar.selectTabAtPosition(position, true);
                setTitle(mPageTitles[position]);
            }
        });
        mBottomBar.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelected(@IdRes int tabId) {
                mViewPager.setCurrentItem(mBottomBar.findPositionForTabWithId(tabId));
            }
        });

        mDefaultPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mDefaultPreferences.registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mDefaultPreferences.unregisterOnSharedPreferenceChangeListener(this);
    }

    private void expandTabLayout() {
        if (mCollapseAnimator != null && mCollapseAnimator.isRunning()) {
            mCollapseAnimator.cancel();
        }

        int prevHeight = mTabLayout.getHeight();
        int targetHeight = getResources().getDimensionPixelSize(R.dimen.tab_layout_height);

        if (mExpandAnimator == null) {
            mExpandAnimator = ValueAnimator.ofInt(prevHeight, targetHeight);
            mExpandAnimator.setInterpolator(new DecelerateInterpolator());
            mExpandAnimator.setDuration(DURATION_EXPAND_TAB_LAYOUT);
            mExpandAnimator.addUpdateListener(mTabLayoutAnimatorListener);
        } else {
            mExpandAnimator.setIntValues(prevHeight, targetHeight);
        }
        mExpandAnimator.start();
    }

    private void collapseTabLayout() {
        if (mExpandAnimator != null && mExpandAnimator.isRunning()) {
            mExpandAnimator.cancel();
        }

        int prevHeight = mTabLayout.getHeight();
        int targetHeight = 0;
        if (mCollapseAnimator == null) {
            mCollapseAnimator = ValueAnimator.ofInt(prevHeight, targetHeight);
            mCollapseAnimator.setInterpolator(new DecelerateInterpolator());
            mCollapseAnimator.setDuration(DURATION_COLLAPSE_TAB_LAYOUT);
            mCollapseAnimator.addUpdateListener(mTabLayoutAnimatorListener);
        } else {
            mCollapseAnimator.setIntValues(prevHeight, targetHeight);
        }
        mCollapseAnimator.start();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.night_mode:
                PreferenceHelper.getInstance().setNightMode(!PreferenceHelper.getInstance().inNightMode());
                break;
            case R.id.nav_settings:
                startActivity(new Intent(this, PreferencesActivity.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (PreferenceKeys.NIGHT_MODE.equals(key)) {
            recreate();
        }
    }

    private class PagerAdapter extends FragmentPagerAdapter {

        private PagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return ArticlesFragment.newInstance("null");
                case 1:
                    return RanksFragment.newInstance();
                case 2:
                    return Top10Fragment.newInstance();
                case 3:
                    return HotCommentFragment.newInstance();
            }
            return new Fragment();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mPageTitles[position];
        }

        @Override
        public int getCount() {
            return mPageTitles.length;
        }
    }
}
