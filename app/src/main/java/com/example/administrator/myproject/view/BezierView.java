package com.example.administrator.myproject.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
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
    private boolean isTouch;

    //起始圆心 在屏幕中的坐标起点
    private float startCircleX;
    private float startCircleY;

    //触摸点 相对view
    private float circleX;
    private float circleY;
    //触摸点 相对view
    private float touchX;
    private float touchY;
    //控制点 相对view
    private float centerX;
    private float centerY;

    private boolean isFirst = true;
    private int originWidth;
    private int originHeight;
    private float marginLeft,marginTop;
    private RelativeLayout.LayoutParams originParams;
    private RelativeLayout.LayoutParams newParams;

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

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (isFirst && w > 0 && h > 0){
            isFirst = false;
            originWidth = w;
            originHeight = h;

            endRadius = Math.min(w,h)/2;
            startRadius = endRadius-diffRadius;

            // 屏幕坐标系和view内坐标系不是同一个。
            //view在屏幕中的坐标起点
            int[] location = new int[2];
            getLocationOnScreen(location);
            int x = location[0];
            int y = location[1] - GraphicUtils.dip2px(getContext(),25);// 状态栏高度

            startCircleX = x+originWidth/2;
            startCircleY = y+originHeight/2;

            circleX = originWidth/2;
            circleY = originHeight/2;

            ViewGroup.LayoutParams params = getLayoutParams();
            if (params instanceof RelativeLayout.LayoutParams){
                originParams = (RelativeLayout.LayoutParams) params;
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);

        if (event.getAction() == MotionEvent.ACTION_DOWN){

            RectF rectF= new RectF();
            rectF.top = circleY -endRadius;
            rectF.bottom = circleY + endRadius;
            rectF.left = circleX - endRadius;
            rectF.right = circleX +endRadius;
            if (rectF.contains(event.getX(),event.getY())){
                isTouch = true;
            }

            ViewGroup layout = (ViewGroup) this.getParent();

            marginLeft = event.getRawX() - event.getX() -getLeft()+layout.getPaddingLeft();
            marginTop = event.getRawY() - event.getY() - getTop() + layout.getPaddingTop() - GraphicUtils.dip2px(getContext(),25);

            setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));
            circleX = startCircleX - marginLeft;
            circleY = startCircleY - marginTop;

            touchX = circleX;
            touchY = circleY;

        }

        if (event.getAction() == MotionEvent.ACTION_MOVE){

            touchX = event.getX();
            touchY = event.getY();

            centerX = (touchX+circleX)/2;
            centerY = (touchY+circleY)/2;



        }
        if (event.getAction() == MotionEvent.ACTION_UP ||event.getAction() == MotionEvent.ACTION_CANCEL){
            isTouch = false;
            setLayoutParams(originParams);
            startRadius = endRadius-diffRadius;
            circleX= originWidth /2;
            circleY = originHeight /2;

        }

        postInvalidate();
        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {

        if (!isTouch){
            startRadius = endRadius -diffRadius;
            canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.OVERLAY);
            canvas.drawCircle(circleX,circleY,startRadius,mPaint);
        }
        else {

            calculate();
            canvas.drawCircle(circleX,circleY,startRadius,mPaint);
            canvas.drawCircle(touchX,touchY,endRadius,mPaint);
            canvas.drawPath(mPath,mPaint);

        }

    }

    private void calculate(){
        float distance = (float) Math.sqrt(Math.pow(touchX - startCircleX,2)+Math.pow(touchY - startCircleY,2));
        startRadius = -distance/15 +endRadius;
        if (startRadius <5){
            startRadius = 5;
        }
        //根据两个圆心算出三角函数角度
        double angle = Math.atan((touchY - circleY)/(touchX - circleX));
        float offsetX = (float) (startRadius*Math.sin(angle));
        float offsetY = (float) (startRadius*Math.cos(angle));
        float x1 = circleX - offsetX;
        float y1 = circleY + offsetY;
        float x4 = circleX + offsetX;
        float y4 = circleY - offsetY;

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

}
