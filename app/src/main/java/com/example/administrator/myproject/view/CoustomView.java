package com.example.administrator.myproject.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

@SuppressLint("DrawAllocation")
public class CoustomView extends View {
	private Paint mPaint;
	private int bigRadius;//大圆半径
	private int smallRadius;//小圆半径
	private float movingAngle =0;//小球移动角度
	private CirclePoint [] points = new CirclePoint[8];
	public CoustomView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		initPaint();
	}

	public CoustomView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		initPaint();
	}
	private void initPaint() {
		// TODO Auto-generated method stub
		mPaint = new Paint();
		mPaint.setAntiAlias(true);
		mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
		mPaint.setStrokeWidth(1);
		mPaint.setColor(Color.GREEN);
	}
	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.onDraw(canvas);
		
		//确定大圆半径最大范围
		int maxRadius= getWidth() < getHeight() ? getWidth()/2 : getHeight()/2;
		bigRadius = maxRadius *3/4;
		//绘制圆上的小球
		smallRadius = maxRadius *1/8;;
		float angle = 0;
		for (int i = 0; i < 8; i++) {
			float x = (float) (getWidth()/2 + bigRadius* Math.cos(Math.toRadians(angle)));
			float y = (float) (getHeight()/2 + bigRadius* Math.sin(Math.toRadians(angle)));
			canvas.drawCircle(x, y, smallRadius, mPaint);
			angle += 45;
			
			if (points[7] == null) {
				CirclePoint point = new CirclePoint(x, y);
				points[i] = point;
			}
		}
		movingAngle += 3;
		
		drawMovingCircle(canvas, movingAngle, bigRadius);
		
		invalidate();
	}

	/**
	 * 移动的小球
	 * @param canvas
	 * @param angle
	 * @param bigRadius
     */
	private void drawMovingCircle(Canvas canvas, float angle, int bigRadius) {
		float x = (float) (getWidth()/2 + bigRadius* Math.cos(Math.toRadians(angle)));
		float y = (float) (getHeight()/2 + bigRadius* Math.sin(Math.toRadians(angle)));
		canvas.drawCircle(x, y, bigRadius *1/8, mPaint);
		
		for (int i = 0; i < points.length; i++) {
			if (isIntersect(new CirclePoint(x, y), points[i])) {
				canvas.drawCircle(points[i].x, points[i].y, smallRadius +5, mPaint);
			}
		}

	}

	/**
	 * 两圆相交
	 * @param circlePoint
	 * @param bigCirclePoint
     * @return
     */
	private boolean isIntersect(CirclePoint circlePoint,CirclePoint bigCirclePoint) {
		float distance = (float) Math.sqrt((circlePoint.x - bigCirclePoint.x)*(circlePoint.x - bigCirclePoint.x) + (circlePoint.y - bigCirclePoint.y)*(circlePoint.y - bigCirclePoint.y));;
		return distance <  (bigRadius *1/8 + smallRadius);
	}
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// TODO Auto-generated method stub
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}

	class CirclePoint{
		float x,y;
		public CirclePoint(float x,float y) {
			// TODO Auto-generated constructor stub
			this.x = x;
			this.y = y;
		}
	}
}
