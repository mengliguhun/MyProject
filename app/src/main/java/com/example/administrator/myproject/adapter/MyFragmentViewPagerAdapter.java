package com.example.administrator.myproject.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.administrator.myproject.fragment.BlankFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2015/11/20.
 */
public class MyFragmentViewPagerAdapter extends FragmentPagerAdapter {
    private List<String> titles;
    private List<Fragment> fragments = new ArrayList<>();
    public MyFragmentViewPagerAdapter(FragmentManager fm, List<String> titles) {
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
                fragments.add(BlankFragment.newInstance(title,title));
            }
        }
    }
}
