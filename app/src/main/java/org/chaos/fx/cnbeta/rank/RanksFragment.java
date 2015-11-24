package org.chaos.fx.cnbeta.rank;

import android.os.Build;
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
import org.chaos.fx.cnbeta.net.CnBetaApi;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * @author Chaos
 *         2015/11/15.
 */
public class RanksFragment extends BaseFragment {

    @Bind(R.id.container) ViewPager mViewPager;
    @Bind(R.id.tabs) TabLayout mTabLayout;

    private float mToolbarElevation;

    public static RanksFragment newInstance() {
        return new RanksFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setActionBarTitle(R.string.nav_rank);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_rank, container, false);
        ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mToolbarElevation = getActivity().findViewById(R.id.appbar).getElevation();
            getActivity().findViewById(R.id.appbar).setElevation(0);
        }
        SectionsPagerAdapter pagerAdapter = new SectionsPagerAdapter(getChildFragmentManager());
        mViewPager.setAdapter(pagerAdapter);
        mViewPager.setOffscreenPageLimit(pagerAdapter.getCount() - 1);
        mTabLayout.setupWithViewPager(mViewPager);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getActivity().findViewById(R.id.appbar).setElevation(mToolbarElevation);
        }
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        private final String[] RANK_TYPES = new String[]{
                CnBetaApi.TYPE_COUNTER,
                CnBetaApi.TYPE_DIG,
                CnBetaApi.TYPE_COMMENTS
        };

        private final String[] RANK_TITLES;

        public SectionsPagerAdapter(FragmentManager fm) {
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
