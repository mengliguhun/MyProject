package com.example.administrator.myproject.activity;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewParent;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnticipateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.view.animation.PathInterpolator;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.administrator.myproject.R;
import com.example.administrator.myproject.adapter.AutoPollAdapter;
import com.example.administrator.myproject.animation.PathAnimation;
import com.example.administrator.myproject.utils.AnimationUtil;
import com.example.administrator.myproject.view.AutoPollRecyclerView;
import com.example.administrator.myproject.view.BezierView;
import com.example.administrator.myproject.view.PasswordInputView;

import java.util.ArrayList;
import java.util.List;

public class CustomViewsActivity extends BaseActivity {
    private EditText mInputView;
    private PasswordInputView mCodeView;
    private Toolbar toolbar;
    private TextView mBezierViewTest;
    private BezierView mBezierView;
    private ViewParent mParent;
    private AutoPollRecyclerView autoPollRecyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_views);

        initViews();
        bindViews();



    }

    @Override
    public void initViews() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("自定义View");
        setSupportActionBar(toolbar);

        autoPollRecyclerView = (AutoPollRecyclerView) findViewById(R.id.list);
        autoPollRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        List<String> list = new ArrayList<>();
        list.add("1222222");
        list.add("ddd1222222");
        list.add("ccdd1222222");
        list.add("ccd1222222");
        list.add("ssss1222222");
        list.add("weee1222222");
        list.add("33rff1222222");
        list.add("vgr1222222");
        list.add("rtgg1222222");
        list.add("cgr1222222");
        list.add("eef 1222222");
        list.add("r b1222222");
        list.add("n bcer1222222");

        autoPollRecyclerView.setAdapter(new AutoPollAdapter(this,list));
        autoPollRecyclerView.start();

        mInputView = (EditText) findViewById(R.id.edit);
        mCodeView = (PasswordInputView) findViewById(R.id.text);
        mCodeView.setInputView(mInputView);
        mCodeView.setBorderRadius(20);

        mBezierViewTest = (TextView) findViewById(R.id.test);
        mParent = mBezierViewTest.getParent();

        TranslateAnimation translateAnimation = new TranslateAnimation(100,300,100,300);
        ScaleAnimation scaleAnimation = new ScaleAnimation(1,0f,1,0f);

        final AnimationSet set = new AnimationSet(true);
        set.addAnimation(scaleAnimation);
        set.addAnimation(translateAnimation);
        set.setInterpolator(new OvershootInterpolator());
        set.setDuration(3000);

        final PathAnimation pathAnimation = new PathAnimation(0,300,0,300);
        pathAnimation.setDuration(3000);

        AnimatorSet animatorSet = new AnimatorSet();


//        ObjectAnimator scale = ObjectAnimator.ofFloat()

        final ImageView imageView = (ImageView) findViewById(R.id.icon);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageView.startAnimation(pathAnimation);
            }
        });
    }

    @Override
    public void bindViews() {
        mBezierViewTest.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        mBezierView = new BezierView(CustomViewsActivity.this);

                        if (mParent != null){
                            mParent.requestDisallowInterceptTouchEvent(true);
                        }

                        mBezierView.dragStart(mBezierViewTest);

                        break;
                    case MotionEvent.ACTION_MOVE:
                        mBezierViewTest.setVisibility(View.INVISIBLE);
                        mBezierView.updatePoints(event.getRawX(),event.getRawY());
                        break;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        if (mParent != null){
                            mParent.requestDisallowInterceptTouchEvent(false);
                        }
                        mBezierViewTest.setVisibility(View.VISIBLE);
                        mBezierView.dragFinish();
                        break;
                }
                return true;
            }
        });
    }

    @Override
    public void initData() {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        autoPollRecyclerView.stop();
    }
}
