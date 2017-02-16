package com.example.administrator.myproject.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

public class CustomProgressBar extends View {
	private Paint mPaint;
	private Paint mPaintBg;
	private final int mCircleLineStrokeWidth = 10;
	private int mWidth = 0;
	private int mHeight = 0;
	private RectF mRectF;
	private int mPercent = 0;

	public CustomProgressBar(Context context, AttributeSet attrs) {
		super(context, attrs);

		mRectF = new RectF();

		mPaint = new Paint();
		mPaint.setAntiAlias(true);
		mPaint.setStrokeWidth(mCircleLineStrokeWidth);
		mPaint.setColor(Color.GREEN);
		mPaint.setStyle(Paint.Style.STROKE);

		mPaintBg = new Paint();
		mPaintBg.setAntiAlias(true);
		mPaintBg.setStrokeWidth(mCircleLineStrokeWidth);
		mPaintBg.setColor(Color.WHITE);
		mPaintBg.setStyle(Paint.Style.STROKE);
		
	}

	public CustomProgressBar(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.onDraw(canvas);
		mWidth = getWidth();
	    mHeight = getHeight();
		// 位置
		mRectF.left = mCircleLineStrokeWidth / 2; // 左上角x
		mRectF.top = mCircleLineStrokeWidth / 2; // 左上角y
		mRectF.right = mWidth - mCircleLineStrokeWidth / 2; // 左下角x
		mRectF.bottom = mHeight - mCircleLineStrokeWidth / 2; // 右下角y

		canvas.drawArc(mRectF, -90, 360, false, mPaintBg);

		if(mPercent >= 100){
			mPercent = 100;
		}else if(mPercent <= 0){
			mPercent = 0;
		}

		canvas.drawArc(mRectF, -90,  ((float) mPercent / 100) * 360, false, mPaint);
	}

	public void setProgress(int percent) {
		mPercent = percent;
		invalidate();
	}

	public void setColor(int color) {
		mPaint = new Paint();
		mPaint.setAntiAlias(true);
		mPaint.setStrokeWidth(mCircleLineStrokeWidth);
		mPaint.setColor(color);
		invalidate();
	}
	public void setBgColor(int color) {
		mPaintBg = new Paint();
		mPaintBg.setAntiAlias(true);
		mPaintBg.setStrokeWidth(mCircleLineStrokeWidth);
		mPaintBg.setColor(color);

		invalidate();
	}

}
