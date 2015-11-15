package org.chaos.fx.cnbeta;

import android.os.Bundle;
import android.support.annotation.StringDef;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import org.chaos.fx.cnbeta.home.ArticlesFragment;
import org.chaos.fx.cnbeta.hotarticles.Top10Fragment;
import org.chaos.fx.cnbeta.hotcomment.HotCommentFragment;
import org.chaos.fx.cnbeta.rank.RankFragment;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    @Bind(R.id.drawer_layout) DrawerLayout mDrawer;
    @Bind(R.id.nav_content) NavigationView mNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawer.setDrawerListener(toggle);
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
                break;
            case R.id.nav_settings:
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

    private ArticlesFragment mHomeFragment;
    private Top10Fragment mHotArticlesFragment;
    private HotCommentFragment mHotCommentFragment;
    private RankFragment mRankFragment;

    private void switchPage(@Page String pageTag) {
        Fragment fragment = null;
        if (PAGE_HOME.equals(pageTag)) {
            if (mHomeFragment == null) {
                mHomeFragment = ArticlesFragment.newInstance("null");
            }
            fragment = mHomeFragment;
        } else if (PAGE_HOT_ARTICLES.equals(pageTag)) {
            if (mHotArticlesFragment == null) {
                mHotArticlesFragment = Top10Fragment.newInstance();
            }
            fragment = mHotArticlesFragment;
        } else if (PAGE_HOT_COMMENT.equals(pageTag)) {
            if (mHotCommentFragment == null) {
                mHotCommentFragment = HotCommentFragment.newInstance();
            }
            fragment = mHotCommentFragment;
        } else if (PAGE_RANK.equals(pageTag)) {
            if (mRankFragment == null) {
                mRankFragment = RankFragment.newInstance();
            }
            fragment = mRankFragment;
        }
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, fragment)
                .commit();
    }
}
