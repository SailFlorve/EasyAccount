package com.hdu.easyaccount.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v7.widget.ViewStubCompat;

import java.util.List;

/**
 * 切换收入和支出的FragmentPagerAdapter
 */
public class RecordFragmentPagerAdapter extends FragmentPagerAdapter {

    private List<String> titles;
    private List<Fragment> views;

    public RecordFragmentPagerAdapter(FragmentManager fm, List<String> titles, List<Fragment> fragments) {
        super(fm);
        this.titles = titles;
        this.views = fragments;
    }

    @Override
    public Fragment getItem(int position) {
        return views.get(position);
    }

    @Override
    public int getCount() {
        return views.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return titles.get(position);
    }
}
