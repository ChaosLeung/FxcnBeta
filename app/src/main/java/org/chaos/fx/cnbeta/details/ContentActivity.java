/*
 * Copyright 2016 Chaos
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

package org.chaos.fx.cnbeta.details;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import org.chaos.fx.cnbeta.R;
import org.chaos.fx.cnbeta.data.ArticlesRepository;
import org.chaos.fx.cnbeta.net.model.NewsContent;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.imid.swipebacklayout.lib.app.SwipeBackActivity;

/**
 * @author Chaos
 *         2015/11/15.
 */
public class ContentActivity extends SwipeBackActivity implements ContentContract.View,
        DetailsFragment.OnShowCommentListener, CommentFragment.OnCommentUpdateListener {

    private static final String TAG = "ContentActivity";

    private static final String KEY_SID = "sid";
    private static final String KEY_TITLE = "title";
    private static final String KEY_TOPIC_LOGO = "topic_logo";

    public static void start(Context context, int sid, String title,
                             String topicLogoLink, ActivityOptionsCompat options) {
        Intent intent = new Intent(context, ContentActivity.class);
        intent.putExtra(KEY_SID, sid);
        intent.putExtra(KEY_TITLE, title);
        intent.putExtra(KEY_TOPIC_LOGO, topicLogoLink);
        ActivityCompat.startActivity(context, intent, options.toBundle());
    }

    private int mSid;
    private String mTitle;
    private String mLogoLink;

    @BindView(R.id.pager) ViewPager mViewPager;

    @BindView(R.id.error_container) View mErrorLayout;
    @BindView(R.id.loading_view) ProgressBar mLoadingView;

    private SectionsPagerAdapter mPagerAdapter;

    private ContentContract.Presenter mPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content);

        ButterKnife.bind(this);

        supportPostponeEnterTransition();

        mSid = getIntent().getIntExtra(KEY_SID, -1);
        mTitle = getIntent().getStringExtra(KEY_TITLE);
        mLogoLink = getIntent().getStringExtra(KEY_TOPIC_LOGO);

        if (mSid != -1) {
            ArticlesRepository.getInstance().readArticle(mSid);
        }

        setupActionBar();
        setupViewPager();

        mPresenter = new ContentPresenter(mSid);
        mPresenter.subscribe(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.unsubscribe();
    }

    private void setupActionBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            setTitle(R.string.details);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    private void setupViewPager() {
        mPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mPagerAdapter);
        mViewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                setSwipeBackEnable(position == 0);
                setTitle(mPagerAdapter.getPageTitle(position));
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                scrollToFinishActivity();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onShowComment() {
        mViewPager.setCurrentItem(1, true);
    }

    @Override
    public void onCommentUpdated(int count) {
        if (mPagerAdapter.detailsFragment.isAdded()) {
            mPagerAdapter.detailsFragment.setCommentCount(count);
        }
    }

    @OnClick(R.id.error_button)
    public void requestArticleHtml() {
        mPresenter.loadArticleContent();
    }

    @Override
    public void showLoadingView(boolean show) {
        mLoadingView.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    @Override
    public void showLoadingError(boolean show) {
        mErrorLayout.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    @Override
    public void setupDetailsFragment(NewsContent content) {
        mPagerAdapter.detailsFragment.handleNewsContent(content);
    }

    @Override
    public void setupCommentFragment(String sn, String tokenForReadComment) {
        mPagerAdapter.commentFragment.handleSetupMessage(sn, tokenForReadComment);
    }

    @Override
    public void onActivityReenter(int resultCode, Intent data) {
        super.onActivityReenter(resultCode, data);
        mPagerAdapter.detailsFragment.onFragmentReenter(data);
    }

    private class SectionsPagerAdapter extends FragmentPagerAdapter {

        private final String[] contentTitles = new String[]{getString(R.string.details), getString(R.string.comment)};
        DetailsFragment detailsFragment;
        CommentFragment commentFragment;

        private SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
            commentFragment = CommentFragment.newInstance(mSid);
            detailsFragment = DetailsFragment.newInstance(mSid, mTitle, mLogoLink);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return detailsFragment;
                case 1:
                    return commentFragment;
            }
            return new Fragment();
        }

        @Override
        public int getCount() {
            return contentTitles.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return contentTitles[position];
        }
    }
}
