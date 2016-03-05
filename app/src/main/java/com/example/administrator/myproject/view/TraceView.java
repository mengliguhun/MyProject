package com.example.administrator.myproject.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.widget.TextView;

/**
 * Created by Administrator on 2015/11/26.
 */
public class TraceView extends View {
    public static final int  DIRECTION_LEFT = 0 ;
    public static final int  DIRECTION_RIGHT= 1 ;
    private int mDirection;
    private float mProgress=0;
    private int mTextWidth;
    private int mTextStartX;
    private Paint mPaint;
    private Rect textBounds = new Rect();
    private int mTextOriginColor = Color.BLUE;
    private int mTextChangeColor = Color.GREEN;
    private String mText="";
    private int mTextSize = sp2px(12);

    private int dp2px(float dpVal) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                dpVal, getResources().getDisplayMetrics());
    }

    private int sp2px(float dpVal) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,
                dpVal, getResources().getDisplayMetrics());
    }
    public TraceView(Context context,String text) {
        super(context);
        initViews();
    }
    public TraceView(Context context) {
        super(context);
        initViews();
    }
    public TraceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initViews();
    }
    private void initViews(){
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setTextSize(mTextSize);

    }
    private void measureText(){
        mTextWidth = (int) mPaint.measureText(mText);
        mPaint.getTextBounds(mText, 0, mText.length(), textBounds);

    }

    public void setText(String mText) {
        this.mText = mText;
        requestLayout();
        invalidate();
    }

    public void setTextSize(int mTextSize) {
        this.mTextSize = mTextSize;
        mPaint.setTextSize(mTextSize);
        requestLayout();
        invalidate();
    }

    public void setProgress(float mProgress) {
        this.mProgress = mProgress;
        invalidate();
    }

    public void setDirection(int mDirection) {
        this.mDirection = mDirection;
        invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        measureText();
        int width = measureWidth(widthMeasureSpec);
        int height = measureHeight(heightMeasureSpec);
        setMeasuredDimension(width, height);

        mTextStartX = getWidth()/2 - mTextWidth/2;

    }
    private int measureWidth(int widthMeasureSpec){
        int result =0;
        int size = MeasureSpec.getSize(widthMeasureSpec);
        int mode = MeasureSpec.getMode(widthMeasureSpec);
        switch (mode){
            case MeasureSpec.EXACTLY:
                result = size;
                break;
            case MeasureSpec.AT_MOST:
            case MeasureSpec.UNSPECIFIED:
                result =mTextWidth;
                result += getPaddingLeft() + getPaddingRight();
                break;
        }
        result = mode == MeasureSpec.AT_MOST ? Math.min(result,size):result;
        return result;
    }
    private int measureHeight(int heightMeasureSpec){
        int result =0;
        int size = MeasureSpec.getSize(heightMeasureSpec);
        int mode = MeasureSpec.getMode(heightMeasureSpec);
        switch (mode){
            case MeasureSpec.EXACTLY:
                result = size;
                break;
            case MeasureSpec.AT_MOST:
            case MeasureSpec.UNSPECIFIED:
                result =mTextWidth;
                result += getPaddingTop() + getPaddingBottom();
                break;
        }
        result = mode == MeasureSpec.AT_MOST ? Math.min(result,size):result;
        return result;
    }
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawColorText(canvas);
    }
    private void drawColorText(Canvas canvas){
        int r = (int) (mProgress * mTextWidth + mTextStartX);
//        int t = (int) (mProgress * mTextHeight + mTextStartY);

//        if (mDirection == DIRECTION_LEFT) {
//            drawChangeLeft(canvas, r);
//            drawOriginLeft(canvas, r);
//        } else if (mDirection == DIRECTION_RIGHT) {
//            drawOriginRight(canvas, r);
//            drawChangeRight(canvas, r);
//        }
        /*
        注意进度统一性
         */
        if (mDirection == DIRECTION_LEFT){
            drawText_h(canvas, mTextOriginColor,mTextStartX ,(int) (mTextStartX + (1-mProgress) * mTextWidth));
            drawText_h(canvas, mTextChangeColor,(int) (mTextStartX + (1 - mProgress) * mTextWidth),mTextStartX + mTextWidth);
        }
        else{

            drawText_h(canvas,mTextOriginColor, (int) (mTextStartX + mProgress * mTextWidth), mTextStartX + mTextWidth);
            drawText_h(canvas, mTextChangeColor,mTextStartX, (int) (mTextStartX + mProgress * mTextWidth));
        }
    }
    /**
     * 剪切绘制范围内的内容
     * @param canvas
     * @param startX
     * @param endX
     * @param color
     */
    private void drawText(Canvas canvas,int startX,int endX,int color){

        mPaint.setColor(color);
        canvas.save(Canvas.CLIP_SAVE_FLAG);
        canvas.clipRect(startX,0,endX,getMeasuredHeight());
        canvas.drawText(mText, mTextStartX,
                getMeasuredHeight() / 2
                        - ((mPaint.descent() + mPaint.ascent()) / 2), mPaint);
//        canvas.drawText(mText,mTextStartX,getMeasuredHeight()/2 + textBounds.height()/2,mPaint);
        canvas.restore();
    }
    private void drawText_h(Canvas canvas, int color, int startX, int endX) {
        mPaint.setColor(color);
        if (debug) {
            mPaint.setStyle(Paint.Style.STROKE);
            canvas.drawRect(startX, 0, endX, getMeasuredHeight(), mPaint);
        }
        canvas.save(Canvas.CLIP_SAVE_FLAG);
        canvas.clipRect(startX, 0, endX, getMeasuredHeight());// left, top,
        // right, bottom
        canvas.drawText(mText, mTextStartX,
                getMeasuredHeight() / 2
                        - ((mPaint.descent() + mPaint.ascent()) / 2), mPaint);
        canvas.restore();
    }
    private void drawChangeLeft(Canvas canvas, int r) {
        drawText_h(canvas, mTextChangeColor, mTextStartX,
                (int) (mTextStartX + mProgress * mTextWidth));
    }

    private void drawOriginLeft(Canvas canvas, int r) {
        drawText_h(canvas, mTextOriginColor, (int) (mTextStartX + mProgress
                * mTextWidth), mTextStartX + mTextWidth);
    }

    private void drawChangeRight(Canvas canvas, int r) {
        drawText_h(canvas, mTextChangeColor,
                (int) (mTextStartX + (1 - mProgress) * mTextWidth), mTextStartX
                        + mTextWidth);
    }

    private void drawOriginRight(Canvas canvas, int r) {
        drawText_h(canvas, mTextOriginColor, mTextStartX,
                (int) (mTextStartX + (1 - mProgress) * mTextWidth));
    }
    private boolean debug = false;


}
