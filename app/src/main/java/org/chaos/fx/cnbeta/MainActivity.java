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
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.StringDef;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;

import org.chaos.fx.cnbeta.help.FeedbackActivity;
import org.chaos.fx.cnbeta.home.ArticlesFragment;
import org.chaos.fx.cnbeta.hotarticles.Top10Fragment;
import org.chaos.fx.cnbeta.hotcomment.HotCommentFragment;
import org.chaos.fx.cnbeta.rank.RanksFragment;
import org.chaos.fx.cnbeta.settings.SettingsActivity;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    @Bind(R.id.drawer_layout) DrawerLayout mDrawer;
    @Bind(R.id.nav_content) NavigationView mNavigationView;

    private List<OnActionBarDoubleClickListener> mActionBarDoubleClickListeners = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setOnClickListener(new View.OnClickListener() {

            private long preBarClickTime;

            @Override
            public void onClick(View v) {
                long currentTime = SystemClock.elapsedRealtime();
                if (currentTime - preBarClickTime > 750) {
                    preBarClickTime = currentTime;
                } else {
                    for (OnActionBarDoubleClickListener listener : mActionBarDoubleClickListeners) {
                        listener.onActionBarDoubleClick();
                    }
                }
            }
        });

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {

            private final int originAlpha;
            private final int originRed;
            private final int originGreen;
            private final int originBlue;

            {
                final int originColor = getResources().getColor(R.color.colorPrimaryDark);
                originAlpha = Color.alpha(originColor);
                originRed = Color.red(originColor);
                originGreen = Color.green(originColor);
                originBlue = Color.blue(originColor);
            }

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    Window window = getWindow();
                    window.setStatusBarColor(
                            Color.argb((int) Math.floor(originAlpha * (1 - slideOffset)),
                                    originRed,
                                    originGreen,
                                    originBlue));
                }
            }
        };
        mDrawer.addDrawerListener(toggle);
        toggle.syncState();

        mNavigationView.setNavigationItemSelectedListener(this);
        mNavigationView.setCheckedItem(R.id.nav_home);
        switchPage(PAGE_HOME);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.nav_home:
                switchPage(PAGE_HOME);
                break;
            case R.id.nav_rank:
                switchPage(PAGE_RANK);
                break;
            case R.id.nav_hot_articles:
                switchPage(PAGE_HOT_ARTICLES);
                break;
            case R.id.nav_hot_comments:
                switchPage(PAGE_HOT_COMMENT);
                break;
            case R.id.nav_help:
                startActivity(new Intent(this, FeedbackActivity.class));
                break;
            case R.id.nav_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                break;
        }

        mDrawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @StringDef({PAGE_HOME, PAGE_RANK, PAGE_HOT_ARTICLES, PAGE_HOT_COMMENT})
    @Retention(RetentionPolicy.SOURCE)
    @interface Page {
    }

    private static final String PAGE_HOME = "HomeFragment";
    private static final String PAGE_RANK = "RankFragment";
    private static final String PAGE_HOT_ARTICLES = "HotArticlesFragment";
    private static final String PAGE_HOT_COMMENT = "HotCommentFragment";

    private String mCurrentPageTag;

    private void switchPage(@Page String pageTag) {
        if (pageTag.equals(mCurrentPageTag)) {
            return;
        }

        mCurrentPageTag = pageTag;
        Fragment fragment = null;
        if (PAGE_HOME.equals(pageTag)) {
            fragment = ArticlesFragment.newInstance("null");
        } else if (PAGE_HOT_ARTICLES.equals(pageTag)) {
            fragment = Top10Fragment.newInstance();
        } else if (PAGE_HOT_COMMENT.equals(pageTag)) {
            fragment = HotCommentFragment.newInstance();
        } else if (PAGE_RANK.equals(pageTag)) {
            fragment = RanksFragment.newInstance();
        }
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, fragment)
                .commit();
    }

    public interface OnActionBarDoubleClickListener {
        void onActionBarDoubleClick();
    }

    public void addOnActionBarDoubleClickListener(OnActionBarDoubleClickListener listener) {
        if (!mActionBarDoubleClickListeners.contains(listener)) {
            mActionBarDoubleClickListeners.add(listener);
        }
    }

    public void removeOnActionBarDoubleClickListener(OnActionBarDoubleClickListener listener) {
        mActionBarDoubleClickListeners.remove(listener);
    }
}
