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

package org.chaos.fx.cnbeta.rank;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.chaos.fx.cnbeta.R;
import org.chaos.fx.cnbeta.app.BaseFragment;
import org.chaos.fx.cnbeta.net.MobileApi;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author Chaos
 *         2015/11/15.
 */
public class RanksFragment extends BaseFragment {

    @BindView(R.id.container) ViewPager mViewPager;

    public static RanksFragment newInstance() {
        return new RanksFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_rank, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ButterKnife.bind(this, view);

        SectionsPagerAdapter pagerAdapter = new SectionsPagerAdapter(getChildFragmentManager());
        mViewPager.setAdapter(pagerAdapter);
        mViewPager.setOffscreenPageLimit(pagerAdapter.getCount() - 1);

        TabLayout tabLayout = (TabLayout) getActivity().findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
    }

    private class SectionsPagerAdapter extends FragmentPagerAdapter {

        private final String[] RANK_TYPES = new String[]{
                MobileApi.TYPE_COUNTER,
                MobileApi.TYPE_DIG,
                MobileApi.TYPE_COMMENTS
        };

        private final String[] RANK_TITLES;

        private SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
            RANK_TITLES = getResources().getStringArray(R.array.rank_types);
        }

        @Override
        public Fragment getItem(int position) {
            return RankSubFragment.newInstance(RANK_TYPES[position]);
        }

        @Override
        public int getCount() {
            return RANK_TYPES.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return RANK_TITLES[position];
        }
    }
}
