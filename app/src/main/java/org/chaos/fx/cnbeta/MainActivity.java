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
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.Toolbar;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuItem;

import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnTabReselectListener;
import com.roughike.bottombar.OnTabSelectListener;

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

    @BindView(R.id.toolbar) Toolbar mToolbar;
    @BindView(R.id.bottom_bar) BottomBar mBottomBar;
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
                mBottomBar.selectTabAtPosition(position, true);
            }
        });
        mBottomBar.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelected(@IdRes int tabId) {
                mViewPager.setCurrentItem(mBottomBar.findPositionForTabWithId(tabId), false);
            }
        });
        mBottomBar.setOnTabReselectListener(new OnTabReselectListener() {
            @Override
            public void onTabReSelected(@IdRes int tabId) {
                OnReselectListener l = mOnReselectListeners.get(tabId);
                if (l != null) {
                    l.onReselect();
                }
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
                case 0:
                    return ArticlesFragment.newInstance("null");
                case 1:
                    return Top10Fragment.newInstance();
                case 2:
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
