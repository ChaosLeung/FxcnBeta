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

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.Toolbar;
import android.util.SparseArray;
import android.util.SparseIntArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import org.chaos.fx.cnbeta.home.ArticlesFragment;
import org.chaos.fx.cnbeta.hotarticles.Top10Fragment;
import org.chaos.fx.cnbeta.hotcomment.HotCommentFragment;
import org.chaos.fx.cnbeta.preferences.PreferenceHelper;
import org.chaos.fx.cnbeta.preferences.PreferenceKeys;
import org.chaos.fx.cnbeta.preferences.PreferencesActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements
        SharedPreferences.OnSharedPreferenceChangeListener, ReselectedDispatcher {

    private static final int PAGE_HOME = 0;
    private static final int PAGE_HOT_ARTICLES = 1;
    private static final int PAGE_HOT_COMMENTS = 2;

    private static SparseIntArray INDEX_ID_MAPPING = new SparseIntArray();

    static {
        INDEX_ID_MAPPING.put(PAGE_HOME, R.id.nav_home);
        INDEX_ID_MAPPING.put(PAGE_HOT_ARTICLES, R.id.nav_hot_articles);
        INDEX_ID_MAPPING.put(PAGE_HOT_COMMENTS, R.id.nav_hot_comments);
    }

    @BindView(R.id.toolbar) Toolbar mToolbar;
    @BindView(R.id.shadow_container) View mBottomBarShadow;
    @BindView(R.id.bottom_bar) BottomNavigationView mBottomBar;
    @BindView(R.id.pager) ViewPager mViewPager;

    private String[] mPageTitles;

    private SharedPreferences mDefaultPreferences;

    private SparseArray<OnReselectListener> mOnReselectListeners;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_content);

        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);

        mPageTitles = new String[]{getString(R.string.nav_home),
                getString(R.string.nav_hot_articles), getString(R.string.nav_hot_comments)};
        mOnReselectListeners = new SparseArray<>(mPageTitles.length);

        mViewPager.setAdapter(new PagerAdapter(getSupportFragmentManager()));
        mViewPager.setOffscreenPageLimit(mPageTitles.length);
        mViewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                mBottomBar.setSelectedItemId(INDEX_ID_MAPPING.get(position));
            }
        });
        mBottomBar.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                mViewPager.setCurrentItem(INDEX_ID_MAPPING.keyAt(INDEX_ID_MAPPING.indexOfValue(item.getItemId())), false);
                return true;
            }
        });
        mBottomBar.setOnNavigationItemReselectedListener(new BottomNavigationView.OnNavigationItemReselectedListener() {
            @Override
            public void onNavigationItemReselected(@NonNull MenuItem item) {
                OnReselectListener l = mOnReselectListeners.get(item.getItemId());
                if (l != null) {
                    l.onReselect();
                }
            }
        });

        if (Build.VERSION.SDK_INT < 21) {
            mBottomBarShadow.setVisibility(View.GONE);
        }

        mDefaultPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mDefaultPreferences.registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mDefaultPreferences.unregisterOnSharedPreferenceChangeListener(this);
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

    @Override
    public void addOnReselectListener(@IdRes int interestItemId, OnReselectListener l) {
        mOnReselectListeners.put(interestItemId, l);
    }

    @Override
    public void removeOnReselectListener(OnReselectListener l) {
        int key = mOnReselectListeners.indexOfValue(l);
        mOnReselectListeners.remove(key);
    }

    private class PagerAdapter extends FragmentPagerAdapter {

        private PagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case PAGE_HOME:
                    return ArticlesFragment.newInstance("null");
                case PAGE_HOT_ARTICLES:
                    return Top10Fragment.newInstance();
                case PAGE_HOT_COMMENTS:
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
