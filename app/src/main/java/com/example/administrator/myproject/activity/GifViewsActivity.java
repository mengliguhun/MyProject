package com.example.administrator.myproject.activity;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.administrator.myproject.R;
import com.gif.gifcache.ImageWorker;

public class GifViewsActivity extends BaseActivity {
    private Toolbar toolbar;
    private LinearLayout linearLayout;
    private String []urls = new String[] {
            "http://img.zcool.cn/community/019a7a55b34b096ac725ca50e2a140.gif",
            "http://scimg.jb51.net/allimg/160719/2-160G9224433162.gif",
            "http://img.gaoxiaogif.cn/GaoxiaoGiffiles/images/2015/06/14/shanyanggeshandaniu.gif",
            "http://www.xiaohuazu.com/uploads/allimg/140626/1SZ54941-0.gif",
            "http://www.xiaohuazu.com/uploads/allimg/140430/1606191020-0.gif",
            "http://www.xiaohuazu.com/uploads/allimg/121216/130452CW-0.gif",
            "http://www.xiaohuazu.com/uploads/allimg/121216/1135122491-0.gif",
            "http://www.xiaohuazu.com/uploads/allimg/121207/1-12120G946064W.gif",
            "http://s1.dwstatic.com/group1/M00/B1/C4/a129e6b71a0a5709acdd684a7df2b023.gif",
            "http://s1.dwstatic.com/group1/M00/0F/F5/686d4480239412191134063c4e3d15ac.gif",
    };

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
        toolbar.setTitle("GifView");
        setSupportActionBar(toolbar);

        linearLayout = (LinearLayout) findViewById(R.id.linear);

    }

    @Override
    public void bindViews() {
    }

    @Override
    public void initData() {
        // 获取屏幕宽高（方法1）
        int screenWidth = getWindowManager().getDefaultDisplay().getWidth(); // 屏幕宽（像素，如：480px）
        int screenHeight = getWindowManager().getDefaultDisplay().getHeight(); // 屏幕高（像素，如：800p）

        Log.e("ddddd","screenWidth:"+screenWidth +",screenHeight:"+screenHeight);
        for (int i = 0; i< urls.length; i++){
            ImageView imageView = new ImageView(this);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(screenWidth, (int) (screenWidth*0.75));
            imageView.setLayoutParams(params);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            linearLayout.addView(imageView);
            ImageWorker.getInstance(this).loadImage(urls[i], imageView,R.mipmap.ic_insert_photo_black);
//            Glide.with(this).load(urls[i]).placeholder(R.mipmap.ic_insert_photo_black).into(imageView);
        }



    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}
