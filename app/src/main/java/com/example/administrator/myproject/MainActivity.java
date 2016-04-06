package com.example.administrator.myproject;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TextView;

import com.example.administrator.myproject.adapter.MyFragmentStateViewPagerAdapter;
import com.example.administrator.myproject.adapter.MyFragmentViewPagerAdapter;
import com.example.administrator.myproject.fragment.FragmentItem;
import com.example.administrator.myproject.fragment.IndexPage;
import com.example.administrator.myproject.view.PagerSlidingTabStrip;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
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
//        //创建okHttpClient对象
//        OkHttpClient mOkHttpClient = new OkHttpClient();
////创建一个Request
//        final Request request = new Request.Builder()
//                .url("http://m2.qiushibaike.com/article/list/text?page=1&count=30")
//                .build();
////new call
//        Call call = mOkHttpClient.newCall(request);
////请求加入调度
//        call.enqueue(new Callback()
//        {
//            @Override
//            public void onFailure(Request request, IOException e) {
//                String htmlStr =  "";
//            }
//            @Override
//            public void onResponse(final Response response) throws IOException
//            {
//                String htmlStr =  response.body().string();
//                response.body().string();
//            }
//        });
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
        tabLayout.setupWithViewPager(viewPager);
        viewPager.setCurrentItem(1);//解决默认0不选中
        for (int i = 0; i < tabLayout.getTabCount(); i++) {
            TabLayout.Tab tab = tabLayout.getTabAt(i);
            if (tab != null) {
                tab.setCustomView(getTabView(i));
            }
        }
        viewPager.setCurrentItem(0);

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
