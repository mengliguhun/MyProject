package com.example.administrator.myproject.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.PointF;
import android.graphics.RectF;
import android.os.Build;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.OverScroller;

/**
 * 涂鸦View
 * Created by hubin on 16/9/23.
 */
public class ScaleTransGraffitiView extends BasicGraffitiView {

    private float minimumScale = 1;
    private float mediumScale = 3;
    private float maximumScale = 9;

    /** 手势识别,用于检测快速滑动手势onFling、双击手势onDoubleTap */
    private GestureDetector gestureDetector;
    /** 用于实现快速滑动的动画 */
    private FlingRunnable mCurrentFlingRunnable;

    public ScaleTransGraffitiView(Context context) {
        this(context, null);
    }

    public ScaleTransGraffitiView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ScaleTransGraffitiView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        onDownZoomMidPoint = new PointF();

        gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {

            @Override
            public boolean onDoubleTap(MotionEvent ev) {
                try {
                    float basicScale = getBasicScale();
                    float scale = getScale();
                    float x = ev.getX();
                    float y = ev.getY();

                    if (scale < mediumScale*basicScale ) {
                        post(new AnimatedZoomRunnable(scale, mediumScale*basicScale,x, y));
                    } else if (scale >= mediumScale*basicScale && scale < maximumScale*basicScale) {
                        post(new AnimatedZoomRunnable(scale, maximumScale*basicScale,x, y));
                    } else {
                        post(new AnimatedZoomRunnable(scale, minimumScale *basicScale,x, y));
                    }
                } catch (ArrayIndexOutOfBoundsException e) {
                    // Can sometimes happen when getX() and getY() is called
                }
                return true;
            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                if(mCurrentFlingRunnable == null)mCurrentFlingRunnable = new FlingRunnable(getContext());
                mCurrentFlingRunnable.fling(getViewWidth(),
                        getViewHeight(), (int) -velocityX, (int) -velocityY);
                post(mCurrentFlingRunnable);
                return true;
            }
        });
    }

    private boolean resizeMode = true;

    public void setResizeMode(boolean mode) {
        resizeMode = mode;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (resizeMode) {
            handleImage(event);
        } else {
            super.onTouchEvent(event);
        }
        return true;
    }

    /** 手指拖动的最后X坐标位置 */
    private float lastXDrag;
    /** 手指拖动的最后Y坐标位置 */
    private float lastYDrag;

    /** 手势识别为缩放手势时,两个手指之间的距离 */
    private float onDownZoomDist;
    /** 手势识别为缩放手势时,两个手指之间的中点坐标 */
    private PointF onDownZoomMidPoint;

    /** 没有手势 */
    private static final int NONE = 0;
    /** 拖动的手势 */
    private static final int DRAG = 1;
    /** 缩放的手势 */
    private static final int ZOOM = 2;
    /** 当前手势的模式 */
    private int mode;

    /**
     * 处于缩放、平移模式时,在这里处理手势
     * @param event
     */
    private void handleImage(MotionEvent event) {

        gestureDetector.onTouchEvent(event);

        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                mode = DRAG;
                lastXDrag = event.getX();
                lastYDrag = event.getY();

                cancelFling();
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                mode = ZOOM;
                onDownZoomDist = spacing(event);
                midPoint(onDownZoomMidPoint, event);
                break;
            case MotionEvent.ACTION_MOVE:
                if (mode == ZOOM) {
                    float newDist = spacing(event);
                    float scale = newDist / onDownZoomDist;
                    onDownZoomDist = newDist;

                    // 手势是放大手势,Bitmap已经达到最大放大比例,不进行放大
                    if (scale > 1 && (getScale() >= maximumScale * getBasicScale())){
                        return;
                    }
                    mSuppMatrix.postScale(scale, scale, onDownZoomMidPoint.x, onDownZoomMidPoint.y);
                    checkAndDisplayMatrix();
                } else if (mode == DRAG) {
                    mSuppMatrix.postTranslate(event.getX() - lastXDrag, event.getY() - lastYDrag);
                    lastXDrag = event.getX();
                    lastYDrag = event.getY();

                    checkAndDisplayMatrix();
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
                mode = NONE;

                // 如果缩放过度,平滑拉伸到初始大小
                if(getScale() < getBasicScale()){
                    RectF rect = getDisplayRect(getDrawMatrix());
                    if (null != rect) {
                        post(new AnimatedZoomRunnable(getScale(), getBasicScale(),rect.centerX(), rect.centerY()));
                    }
                }

                break;
        }
    }

    // 触碰两点间距离
    private float spacing(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float) Math.sqrt(x * x + y * y);
    }

    // 取手势中心点
    private void midPoint(PointF point, MotionEvent event) {
        float x = event.getX(0) + event.getX(1);
        float y = event.getY(0) + event.getY(1);
        point.set(x / 2, y / 2);
    }

    // 取旋转角度
    protected float rotation(float x0, float y0, float x1, float y1) {
        double delta_x = (x0 - x1);
        double delta_y = (y0 - y1);
        double radians = Math.atan2(delta_y, delta_x);
        return (float) Math.toDegrees(radians);
    }

    /**
     * 当Bitmap被缩放到小于初始大小时,手指松开后,平滑拉伸到初始大小
     * 从PhotoView原封移动过来的
     */
    private class AnimatedZoomRunnable implements Runnable {

        private final float mFocalX, mFocalY;
        private final long mStartTime;
        private final float mZoomStart, mZoomEnd;
        private final int ZOOM_DURATION = 200;
        private Interpolator mInterpolator = new AccelerateDecelerateInterpolator();

        public AnimatedZoomRunnable(final float currentZoom, final float targetZoom,
                                    final float focalX, final float focalY) {
            mFocalX = focalX;
            mFocalY = focalY;
            mStartTime = System.currentTimeMillis();
            mZoomStart = currentZoom;
            mZoomEnd = targetZoom;
        }

        @Override
        public void run() {

            float t = interpolate();
            float scale = mZoomStart + t * (mZoomEnd - mZoomStart);
            float deltaScale = scale / getScale();

            mSuppMatrix.postScale(deltaScale, deltaScale, mFocalX, mFocalY);
            checkAndDisplayMatrix();
            //onScale(deltaScale, mFocalX, mFocalY);

            // We haven't hit our target scale yet, so post ourselves again
            if (t < 1f) {
                Compat.postOnAnimation(ScaleTransGraffitiView.this, this);
            }
        }

        private float interpolate() {
            float t = 1f * (System.currentTimeMillis() - mStartTime) / ZOOM_DURATION;
            t = Math.min(1f, t);
            t = mInterpolator.getInterpolation(t);
            return t;
        }
    }

    /**
     * 当Bitmap正在进行惯性滑动时,手指再次按下,此时应该停止惯性滑动
     * 从PhotoView原封移动过来的
     */
    private void cancelFling() {
        if (null != mCurrentFlingRunnable) {
            mCurrentFlingRunnable.cancelFling();
            mCurrentFlingRunnable = null;
        }
    }

    /**
     * 当Bitmap被放大时,快速滑动的惯性滑动效果,手指松开不会立即停止,而是慢慢停止
     * 从PhotoView原封移动过来的
     */
    private class FlingRunnable implements Runnable {

        private final OverScroller mScroller;
        private int mCurrentX, mCurrentY;

        public FlingRunnable(Context context) {
            mScroller = new OverScroller(context);
        }

        public void cancelFling() {
            mScroller.forceFinished(true);
        }

        public void fling(int viewWidth, int viewHeight, int velocityX,
                          int velocityY) {
            final RectF rect = getDisplayRect(getDrawMatrix());
            if (null == rect) {
                return;
            }

            final int startX = Math.round(-rect.left);
            final int minX, maxX, minY, maxY;

            if (viewWidth < rect.width()) {
                minX = 0;
                maxX = Math.round(rect.width() - viewWidth);
            } else {
                minX = maxX = startX;
            }

            final int startY = Math.round(-rect.top);
            if (viewHeight < rect.height()) {
                minY = 0;
                maxY = Math.round(rect.height() - viewHeight);
            } else {
                minY = maxY = startY;
            }

            mCurrentX = startX;
            mCurrentY = startY;

            // If we actually can move, fling the scroller
            if (startX != maxX || startY != maxY) {
                mScroller.fling(startX, startY, velocityX, velocityY, minX,
                        maxX, minY, maxY, 0, 0);
            }
        }

        @Override
        public void run() {
            if (mScroller.isFinished()) {
                return; // remaining post that should not be handled
            }

            if (mScroller.computeScrollOffset()) {

                final int newX = mScroller.getCurrX();
                final int newY = mScroller.getCurrY();

                mSuppMatrix.postTranslate(mCurrentX - newX, mCurrentY - newY);
                //setImageViewMatrix(getDrawMatrix());
                postInvalidate();

                mCurrentX = newX;
                mCurrentY = newY;

                // Post On animation
                Compat.postOnAnimation(ScaleTransGraffitiView.this, this);
            }
        }
    }

}

/**
 * 实现每秒60帧的刷新
 * 从PhotoView移动过来的
 */
class Compat {

    private static final int SIXTY_FPS_INTERVAL = 1000 / 60;

    public static void postOnAnimation(View view, Runnable runnable) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            postOnAnimationJellyBean(view, runnable);
        } else {
            view.postDelayed(runnable, SIXTY_FPS_INTERVAL);
        }
    }

    @TargetApi(16)
    private static void postOnAnimationJellyBean(View view, Runnable runnable) {
        view.postOnAnimation(runnable);
    }
}