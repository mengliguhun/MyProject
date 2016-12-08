package com.example.administrator.myproject;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.administrator.myproject.adapter.MyFragmentViewPagerAdapter;
import com.example.administrator.myproject.fragment.FragmentItem;
import com.google.zxing.client.android.QrCodeActivity;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener,FragmentItem.OnFragmentInteractionListener {
    private FloatingActionButton fab;
    private DrawerLayout drawer;
    private Toolbar toolbar;
    private NavigationView navigationView;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private MyFragmentViewPagerAdapter adapter;
    private List<String> titles = new ArrayList<>();
    private int tabImgs[] = {R.drawable.tab_icon_selector_text,R.drawable.tab_icon_selector_pic,R.drawable.tab_icon_selector_video};
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

        tabLayout = (TabLayout) findViewById(R.id.tablayout);
        tabLayout.setSelectedTabIndicatorHeight(0);
        viewPager = (ViewPager) findViewById(R.id.viewpager);
//        viewPager.setOffscreenPageLimit(3);
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

        setupWithViewPager(viewPager,tabLayout);

    }
    /**
     * 解决默认0不选中
     */
    public void setupWithViewPager(@NonNull ViewPager viewPager,TabLayout tabLayout) {
        final PagerAdapter adapter = viewPager.getAdapter();
        if (adapter == null) {
            throw new IllegalArgumentException("ViewPager does not have a PagerAdapter set");
        }
        // First we'll add Tabs, using the adapter's page titles
        for (int i = 0; i < adapter.getCount(); i++) {
            TabLayout.Tab tab = tabLayout.newTab();
            if (tab != null) {
                tab.setCustomView(getTabView(i));
            }
            tabLayout.addTab(tab);
        }

        // Now we'll add our page change listener to the ViewPager
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        // Now we'll add a tab selected listener to set ViewPager's current item
        tabLayout.setOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(viewPager));

    }
    public View getTabView(int position) {
        View v = LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
        TextView tv = (TextView) v.findViewById(R.id.textView);
        tv.setText(titles.get(position));
        ImageView img = (ImageView) v.findViewById(R.id.imageView);
        img.setImageResource(tabImgs[position]);
        return v;
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
            startActivity(new Intent(this,CustomViewsActivity.class));
        } else if (id == R.id.nav_gallery) {
            startActivity(new Intent(this,SpeexRecorderActivity.class));
        } else if (id == R.id.nav_slideshow) {
            startActivity(new Intent(this,CaptureActivity.class));
        } else if (id == R.id.nav_manage) {
            startActivity(new Intent(this,GifViewsActivity.class));
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
