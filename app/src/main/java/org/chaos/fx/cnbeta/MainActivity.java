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
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.IdRes;
import android.support.annotation.StringDef;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnTabSelectListener;

import org.chaos.fx.cnbeta.home.ArticlesFragment;
import org.chaos.fx.cnbeta.hotarticles.Top10Fragment;
import org.chaos.fx.cnbeta.hotcomment.HotCommentFragment;
import org.chaos.fx.cnbeta.rank.RanksFragment;
import org.chaos.fx.cnbeta.preferences.PreferencesActivity;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private BottomBar mBottomBar;

    private List<OnActionBarDoubleClickListener> mActionBarDoubleClickListeners = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_content);

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

        mBottomBar = (BottomBar) findViewById(R.id.bottom_bar);
        mBottomBar.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelected(@IdRes int tabId) {
                switch (tabId) {
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
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_settings:
                startActivity(new Intent(this, PreferencesActivity.class));
                break;
        }
        return super.onOptionsItemSelected(item);
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
