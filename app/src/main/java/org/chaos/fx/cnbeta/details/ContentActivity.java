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
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

import org.chaos.fx.cnbeta.R;
import org.chaos.fx.cnbeta.net.CnBetaApiHelper;
import org.chaos.fx.cnbeta.net.WebApi;
import org.chaos.fx.cnbeta.net.model.HasReadArticle;
import org.chaos.fx.cnbeta.net.model.WebComment;

import java.io.IOException;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.realm.Realm;
import me.imid.swipebacklayout.lib.app.SwipeBackActivity;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * @author Chaos
 *         2015/11/15.
 */
public class ContentActivity extends SwipeBackActivity implements
        ContentFragment.OnShowCommentListener {

    private static final String TAG = "ContentActivity";

    private static final String KEY_SID = "sid";
    private static final String KEY_TOPIC_LOGO = "topic_logo";

    public static void start(Context context, int sid, String topicLogoLink) {
        Intent intent = new Intent(context, ContentActivity.class);
        intent.putExtra(KEY_SID, sid);
        intent.putExtra(KEY_TOPIC_LOGO, topicLogoLink);
        context.startActivity(intent);
    }

    private int mSid;
    private String mLogoLink;

    private String mSN;
    private WebComment mWebComment;

    @Bind(R.id.pager)
    ViewPager mViewPager;

    private SectionsPagerAdapter mPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content);

        ButterKnife.bind(this);

        mSid = getIntent().getIntExtra(KEY_SID, -1);
        mLogoLink = getIntent().getStringExtra(KEY_TOPIC_LOGO);

        if (mSid != -1) {
            Realm realm = Realm.getDefaultInstance();
            realm.beginTransaction();
            HasReadArticle readArticle = new HasReadArticle();
            readArticle.setSid(mSid);
            realm.copyToRealmOrUpdate(readArticle);
            realm.commitTransaction();
        }

        setupActionBar();
        setupViewPager();

        requestArticleHtml();
    }

    private void setupActionBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            setTitle(R.string.content);
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

    private void requestArticleHtml() {
        CnBetaApiHelper.getArticleHtml(mSid).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {
                        String html = response.body().string();
                        mSN = CnBetaApiHelper.getSNFromArticleBody(html);
                        requestCommentJson();
                    } catch (IOException e) {
                        Log.e(TAG, "IOException throws when getting article html", e);
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e(TAG, "request article html string failed", t);
            }
        });
    }

    private void requestCommentJson() {
        CnBetaApiHelper.getCommentJson(mSid, mSN).enqueue(new Callback<WebApi.Result<WebComment>>() {
            @Override
            public void onResponse(Call<WebApi.Result<WebComment>> call, Response<WebApi.Result<WebComment>> response) {
                if (response.isSuccessful()) {
                    mWebComment = response.body().result;
                }
            }

            @Override
            public void onFailure(Call<WebApi.Result<WebComment>> call, Throwable t) {
                Log.e(TAG, "request web comment failed", t);
            }
        });
    }

    String getToken() {
        if (mWebComment == null) {
            return null;
        }
        return mWebComment.getToken();
    }

    private class SectionsPagerAdapter extends FragmentPagerAdapter {

        private final String[] contentTitles = new String[]{getString(R.string.content), getString(R.string.comment)};

        private SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return ContentFragment.newInstance(mSid, mLogoLink);
                case 1:
                    return CommentFragment.newInstance(mSid);
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
