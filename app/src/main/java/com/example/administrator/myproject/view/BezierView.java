package com.example.administrator.myproject.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.util.AttributeSet;
import android.view.View;
import android.view.WindowManager;

import com.example.administrator.myproject.animation.PointEvaluator;
import com.example.administrator.myproject.bean.Point;
import com.example.administrator.myproject.utils.GraphicUtils;

/**
 * TODO: document your custom view class.
 */
public class BezierView extends View {
    private OnDragFinishListener onDragFinishListener;
    private Paint mPaint;
    //绘图路径
    private Path mPath;
    //
    private float maxRadius;
    //两圆的半径差
    private float diffRadius = 2;
    //起始圆半径
    private float startRadius;
    //拖动圆的半径
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
    //最大距离
    private int maxDistance = 100;
    //是否超出最大距离
    private boolean isArriveMaxDistance;

    private Bitmap mDest;

    public void setMaxRadius(float maxRadius) {
        this.maxRadius = maxRadius;
    }

    public void setMaxDistance(int maxDistance) {
        this.maxDistance = maxDistance;
    }

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
        maxRadius = GraphicUtils.dip2px(getContext(),10);
    }

    /**
     * 用户按下并初始化
     * @param startX
     * @param startY
     * @param width
     * @param height
     */
    public void  initPoints(float startX,float startY,int width,int height){

        endRadius = Math.min(width,height)/2;
        if (endRadius >maxRadius){
            endRadius = maxRadius;
        }
        startRadius = endRadius-diffRadius;
        startCircleX = startX + width/2;
        startCircleY = startY + height/2 - getStatusBarHeight();
        touchX = startCircleX;
        touchY = startCircleY;

        centerX = startCircleX/2 +touchX/2;
        centerY = startCircleY/2 +touchY/2;

        invalidate();
    }
    /**
     * 开始拖动
     * @param target
     */
    public void dragStart(View target){

        convertViewToBitmap(target);

        final int[] locations = new int[2];
        target.getLocationOnScreen(locations);

        windowManagerAddView(getContext(),this);

        this.initPoints(locations[0],locations[1],target.getWidth(),target.getHeight());

    }
    /**
     * 用户移动view，并更新
     * @param x
     * @param y
     */
    public void updatePoints(float x,float y){
        touchX = x;
        touchY = y - getStatusBarHeight();
        centerX = startCircleX/2 +touchX/2;
        centerY = startCircleY/2 +touchY/2;

        invalidate();
    }

    /**
     * 拖动结束
     */
    public void dragFinish(){
        if (onDragFinishListener != null){
            onDragFinishListener.onDragFinish();
        }
        if (isArriveMaxDistance){
            windowManagerRemoveView();
        }
        else {
            startRollBackAnimation(1000);
        }

    }
    /**
     * 将view转换成bitmap
     * @param view
     * @return
     */
    private Bitmap convertViewToBitmap(View view) {
        int width = view.getWidth();
        int height = view.getHeight();
        mDest = Bitmap.createBitmap(width,height, Bitmap.Config.ARGB_8888);
        //定义一个指定位图的画布，来绘制内容
        Canvas canvas = new Canvas(mDest);
        //将view的内容绘制到bitmap上
        view.draw(canvas);
        return mDest;
    }
    /**
     * 回滚状态动画
     */
    private void startRollBackAnimation(long duration) {
        //属性动画弹性效果 缓动函数
        ValueAnimator rollBackAnim = ObjectAnimator.ofObject(new PointEvaluator(duration),new Point(touchX,touchY),new Point(startCircleX,startCircleY));
        rollBackAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                Point point = (Point) animation.getAnimatedValue();
                touchX = point.getX();
                touchY = point.getY();
                centerX = startCircleX/2 +touchX/2;
                centerY = startCircleY/2 +touchY/2;
                postInvalidate();
            }
        });

        rollBackAnim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                //可以自己定义监听，进行目标view的处理
                BezierView.this.clearAnimation();
                windowManagerRemoveView();
            }
        });
        rollBackAnim.setDuration(duration);
        rollBackAnim.start();
    }

    @Override
    protected void onDraw(Canvas canvas) {

        calculate();
        if (!isArriveMaxDistance){
            canvas.drawCircle(startCircleX,startCircleY,startRadius,mPaint);
            canvas.drawCircle(touchX,touchY,endRadius,mPaint);
            canvas.drawPath(mPath,mPaint);

        }

        canvas.drawBitmap(mDest,touchX - mDest.getWidth()/2f,touchY - mDest.getHeight()/2f,mPaint);


    }

    /**
     * 计算贝塞尔曲线
     */
    private void calculate(){
        float distance = (float) Math.sqrt(Math.pow(touchX - startCircleX,2)+Math.pow(touchY - startCircleY,2));
        startRadius = -distance/15 +endRadius;
        if (startRadius <8){
            startRadius = 8;
        }
        if (distance > GraphicUtils.dip2px(getContext(),maxDistance)){
            isArriveMaxDistance = true;
        }else {
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


    }
    /**
     * 向窗口添加view
     */
    WindowManager windowManager;
    private void windowManagerAddView(Context context,View view){
        WindowManager.LayoutParams params = new WindowManager.LayoutParams();//全屏MATCH_PARENT

        params.format = PixelFormat.TRANSLUCENT;
        params.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;//后面窗口仍然可以处理点设备事件
        windowManager = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
        windowManager.addView(view, params);
    }
    //移除view
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

    /**
     * 拖动抬起监听
     * @param onDragFinishListener
     */

    public void setOnDragFinishListener(OnDragFinishListener onDragFinishListener) {
        this.onDragFinishListener = onDragFinishListener;
    }

    /**
     * 自定义监听器
     */
    public interface OnDragFinishListener{
         void onDragFinish();
    }
}
