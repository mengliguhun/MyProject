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

	private int mWidth = 0;
	private int mHeight = 0;

	private int mPercent = 0;

	public CustomProgressBar(Context context, AttributeSet attrs) {
		super(context, attrs);
		mPaint = new Paint();
		mPaint.setAntiAlias(true);
		mPaint.setStrokeWidth(10);
		mPaint.setColor(Color.RED);

		mPaintBg = new Paint();
		mPaintBg.setAntiAlias(true);
		mPaintBg.setStrokeWidth(10);
		mPaintBg.setColor(Color.GRAY);
		
		
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
		drawBackground(canvas);
		drawProcess(canvas);
	}

	public void init(int width, int height) {
		mWidth = width;
		mHeight = height;
	}

	public void setProgress(int percent) {
		mPercent = percent;
		invalidate();
	}

	public void setColor(int color) {
		mPaint = new Paint();
		mPaint.setAntiAlias(true);
		mPaint.setStrokeWidth(10);
		mPaint.setColor(color);
		invalidate();
	}
	public void setBgColor(int color) {
		mPaintBg = new Paint();
		mPaintBg.setAntiAlias(true);
		mPaintBg.setStrokeWidth(10);
		mPaintBg.setColor(color);

		invalidate();
	}
	private void drawBackground(Canvas canvas){
		RectF rect = new RectF(0, 0,  mWidth,  mHeight);
		canvas.drawRect(rect, mPaintBg);
	}
	private void drawProcess(Canvas canvas){
		if(mPercent >= 100){
			mPercent = 100;
		}else if(mPercent <= 0){
			mPercent = 0;
		}
		float f = (float)(mPercent * mWidth) / 100;
		
		RectF rect = new RectF(0, 0,  f, mHeight);
		
		canvas.drawRect(rect, mPaint);
		
	}
}
