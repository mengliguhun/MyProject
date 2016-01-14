package com.example.administrator.myproject.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;


import com.example.administrator.myproject.fragment.IndexPage;

import java.util.ArrayList;
import java.util.List;

/**
 * FragmentStatePagerAdapter 只会存在 前一个 ，当前，后一个
 * FragmentPagerAdapter 全部存在
 * Created by Administrator on 2015/11/20.
 */
public class MyFragmentStateViewPagerAdapter extends FragmentStatePagerAdapter {
    private List<String> titles;
    private List<Fragment> fragments = new ArrayList<>();
    public MyFragmentStateViewPagerAdapter(FragmentManager fm, List<String> titles) {
        super(fm);
        this.titles = titles;
        setTitles(titles);
    }

    @Override
    public Fragment getItem(int position) {

        return fragments.get(position);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return titles.get(position);
    }

    @Override
    public int getCount() {
        return titles == null? 0:titles.size();
    }

    public void setTitles(List<String> titles) {
        if (titles != null){
            for (String title:titles) {
                fragments.add(IndexPage.newInstance(title, title));
            }
        }
    }
}
