package com.example.administrator.myproject.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.example.administrator.myproject.R;
import com.example.administrator.myproject.utils.GraphicUtils;
import com.example.administrator.myproject.utils.Utils;
import com.google.android.gms.analytics.ecommerce.Product;

/**
 * TODO: document your custom view class.
 */
public class BezierView extends View {

    private Paint mPaint;
    private Path mPath;
    private float diffRadius = 2;
    private float startRadius;
    private float endRadius;
    //起始圆心 在屏幕中的坐标起点
    private float startCircleX;
    private float startCircleY;
    //触摸点 相对view
    private float touchX;
    private float touchY;
    //控制点 相对view
    private float centerX;
    private float centerY;

    private int targetWidth;
    private int targetHeight;

    public BezierView(Context context) {
        super(context);
        init(null, 0);
    }

    public BezierView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public BezierView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {
        mPath = new Path();
        mPaint = new Paint();
        mPaint.setColor(Color.RED);
        mPaint.setAntiAlias(true);

    }

    public void  initPoints(float startX,float startY,float x,float y,int width,int height){

        endRadius = Math.min(width,height)/2;
        startRadius = endRadius-diffRadius;
        startCircleX = startX + width/2;
        startCircleY = startY + height/2 - getStatusBarHeight();
        touchX = startCircleX;
        touchY = startCircleY;

        centerX = startCircleX/2 +touchX/2;
        centerY = startCircleY/2 +touchY/2;

        invalidate();
    }
    public void updatePoints(float x,float y){
        touchX = x;
        touchY = y - getStatusBarHeight();
        centerX = startCircleX/2 +touchX/2;
        centerY = startCircleY/2 +touchY/2;

        invalidate();
    }
    public void dragStart(View target,float x,float y){
        final int[] locations = new int[2];
        target.getLocationOnScreen(locations);

        windowManagerAddView(getContext(),this);
        this.initPoints(locations[0],locations[1],x,y,target.getWidth(),target.getHeight());

    }
    public void dragFinish(){
        windowManagerRemoveView();
    }
    @Override
    protected void onDraw(Canvas canvas) {

        calculate();
        canvas.drawCircle(startCircleX,startCircleY,startRadius,mPaint);
        canvas.drawCircle(touchX,touchY,endRadius,mPaint);
        canvas.drawPath(mPath,mPaint);

    }

    private void calculate(){
        float distance = (float) Math.sqrt(Math.pow(touchX - startCircleX,2)+Math.pow(touchY - startCircleY,2));
        startRadius = -distance/15 +endRadius;
        if (startRadius <5){
            startRadius = 5;
        }
        //根据两个圆心算出三角函数角度
        double angle = Math.atan((touchY - startCircleY)/(touchX - startCircleX));
        float offsetX = (float) (startRadius*Math.sin(angle));
        float offsetY = (float) (startRadius*Math.cos(angle));
        float x1 = startCircleX - offsetX;
        float y1 = startCircleY + offsetY;
        float x4 = startCircleX + offsetX;
        float y4 = startCircleY - offsetY;

        offsetX = (float) (endRadius*Math.sin(angle));
        offsetY = (float) (endRadius*Math.cos(angle));
        float x2 = touchX - offsetX;
        float y2 = touchY + offsetY;
        float x3 = touchX + offsetX;
        float y3 = touchY - offsetY;

        mPath.reset();
        mPath.moveTo(x1,y1);
        mPath.quadTo(centerX,centerY,x2,y2);
        mPath.lineTo(x3,y3);
        mPath.quadTo(centerX,centerY,x4,y4);
        mPath.lineTo(x1,y1);

    }
    WindowManager windowManager;
    private void windowManagerAddView(Context context,View view){
        WindowManager.LayoutParams params = new WindowManager.LayoutParams();

        params.format = PixelFormat.TRANSLUCENT;
        params.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;//后面窗口仍然可以处理点设备事件
        windowManager = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
        windowManager.addView(view, params);
    }
    private void windowManagerRemoveView(){
        if (windowManager != null){
            windowManager.removeView(this);
        }
    }
    /**
     * 获取状态栏高度
     * @return
     */
    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }
}
