package com.example.administrator.myproject;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;

import com.example.administrator.myproject.imagecache.ImageWorker;

import pl.droidsonroids.gif.GifImageView;

public class GifViewsActivity extends BaseActivity {
    private Toolbar toolbar;
    private ImageView imageView;
    private GifImageView gifImageView;
    private String url = "http://img.zcool.cn/community/019a7a55b34b096ac725ca50e2a140.gif";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gif_views);

        initViews();
        bindViews();
        initData();
    }

    @Override
    public void initViews() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("自定义View");
        setSupportActionBar(toolbar);

        imageView = (ImageView) findViewById(R.id.img);

        gifImageView = (GifImageView) findViewById(R.id.gif);
    }

    @Override
    public void bindViews() {
    }

    @Override
    public void initData() {
        url = "http://img05.tooopen.com/images/20140604/sy_62331342149.jpg";
        ImageWorker.getInstance(this).setLoadingImage(R.mipmap.ic_insert_photo_black).loadImage(url, imageView);

    }

    @Override
    protected void onPause() {
        super.onPause();
        ImageWorker.getInstance(this).flushCache();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}
