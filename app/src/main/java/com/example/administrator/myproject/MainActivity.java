package com.example.administrator.myproject;

import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.example.administrator.myproject.adapter.MyFragmentStateViewPagerAdapter;
import com.example.administrator.myproject.adapter.MyFragmentViewPagerAdapter;
import com.example.administrator.myproject.fragment.FragmentItem;
import com.example.administrator.myproject.fragment.IndexPage;
import com.example.administrator.myproject.view.PagerSlidingTabStrip;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener,FragmentItem.OnFragmentInteractionListener {
    private FloatingActionButton fab;
    private DrawerLayout drawer;
    private Toolbar toolbar;
    private NavigationView navigationView;
    private PagerSlidingTabStrip tabs;
    private ViewPager viewPager;
    private MyFragmentViewPagerAdapter adapter;
    private List<String> titles = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
        bindViews();
        initData();
    }

    @Override
    public void initViews() {
        super.initViews();
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.nav_view);

        tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        tabs.setIndicatorHeight(4);
        tabs.setIndicatorColor(getResources().getColor(R.color.green_light));
        tabs.setTextColor(getResources().getColor(R.color.text_color_gray));
        tabs.setTabCurrentTextColor(getResources().getColor(R.color.green_light));

        viewPager = (ViewPager) findViewById(R.id.pager);
    }

    @Override
    public void bindViews() {

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);


    }

    @Override
    public void initData() {

        titles.add("搞文");
        titles.add("搞图");
        titles.add("视频");

        adapter = new MyFragmentViewPagerAdapter(getSupportFragmentManager(),titles);
        viewPager.setAdapter(adapter);
        tabs.setViewPager(viewPager);
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camara) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}