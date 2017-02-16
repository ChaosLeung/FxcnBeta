/*
 * Copyright 2017 Chaos
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
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;

import org.chaos.fx.cnbeta.R;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.imid.swipebacklayout.lib.app.SwipeBackActivity;

/**
 * @author Chaos
 *         15/02/2017
 */

public class ImagePagerActivity extends SwipeBackActivity implements ViewPager.OnPageChangeListener {

    private static final String EXTRA_IMAGE_URLS = "image_urls";
    private static final String EXTRA_CURRENT_IMAGE = "current_image";

    public static void start(Context context, String[] imageUrls, int currentImageIdx) {
        Intent starter = new Intent(context, ImagePagerActivity.class);
        starter.putExtra(EXTRA_IMAGE_URLS, imageUrls);
        starter.putExtra(EXTRA_CURRENT_IMAGE, currentImageIdx);
        context.startActivity(starter);
    }

    @BindView(R.id.pager) ViewPager mViewPager;

    private String[] mImageUrls;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_pager);

        ButterKnife.bind(this);

        mImageUrls = getIntent().getStringArrayExtra(EXTRA_IMAGE_URLS);
        int currentItem = getIntent().getIntExtra(EXTRA_CURRENT_IMAGE, 0);

        mViewPager.setAdapter(new ImagePagerAdapter(getSupportFragmentManager()));
        mViewPager.addOnPageChangeListener(this);
        mViewPager.setCurrentItem(currentItem);

        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        setActionBarTitleByIndex(currentItem);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mViewPager.removeOnPageChangeListener(this);
    }

    private void setActionBarTitleByIndex(int idx) {
        setTitle(String.format(Locale.getDefault(), "%d / %d", idx + 1, mImageUrls.length));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            scrollToFinishActivity();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        // no-op
    }

    @Override
    public void onPageSelected(int position) {
        setActionBarTitleByIndex(position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        // no-op
    }

    private class ImagePagerAdapter extends FragmentPagerAdapter {

        private ImagePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return ImageFragment.newInstance(mImageUrls[position]);
        }

        @Override
        public int getCount() {
            return mImageUrls.length;
        }
    }
}
