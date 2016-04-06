package com.example.administrator.myproject.animation;

import android.animation.TypeEvaluator;

import com.example.administrator.myproject.bean.Point;

/**
 * Created by Administrator on 2016/4/6.
 */
public class PointEvaluator implements TypeEvaluator {
    private float mDuration;

    public PointEvaluator(float mDuration) {
        this.mDuration = mDuration;
    }

    @Override
    public Object evaluate(float fraction, Object startValue, Object endValue) {

        Point startPoint = (Point) startValue;
        Point endPoint = (Point) endValue;

        float t = mDuration * fraction;
        float x = calculate(t,startPoint.getX(),endPoint.getX() - startPoint.getX(),mDuration);
        float y = calculate(t,startPoint.getY(),endPoint.getY() - startPoint.getY(),mDuration);
        return new Point(x,y);
    }
    public Float calculate(float t, float b, float c, float d) {

        double s=1.70158;
        float a=c;
        if (t==0)
            return b;
        if ((t/=d)==1)
            return b+c;
        float p= (float) (d*.3);
        if (a < Math.abs(c)) {
            a=c;
            s=p/4;
        }
        else
        {
            s = p/(2*Math.PI) * Math.asin (c/a);
        }
        return a*(float) Math.pow(2,-10*t) * (float) Math.sin( (t*d-s)*(2*Math.PI)/p ) + c + b;
    }
}
