package com.example.administrator.myproject;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewParent;
import android.widget.EditText;
import android.widget.TextView;

import com.example.administrator.myproject.view.BezierView;
import com.example.administrator.myproject.view.PasswordInputView;

public class CustomViewsActivity extends BaseActivity {
    private EditText mInputView;
    private PasswordInputView mCodeView;
    private Toolbar toolbar;
    private TextView mBezierViewTest;
    private BezierView mBezierView;
    private ViewParent mParent;
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

        mInputView = (EditText) findViewById(R.id.edit);
        mCodeView = (PasswordInputView) findViewById(R.id.text);
        mCodeView.setInputView(mInputView);
        mCodeView.setBorderRadius(20);

        mBezierViewTest = (TextView) findViewById(R.id.test);
        mParent = mBezierViewTest.getParent();
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

                        mBezierView.dragStart(mBezierViewTest,event.getRawX(),event.getRawY());

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
}
