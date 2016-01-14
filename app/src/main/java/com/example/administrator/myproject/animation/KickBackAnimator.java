package com.example.administrator.myproject.animation;

import android.animation.Animator.AnimatorListener;
import android.animation.ObjectAnimator;
import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.view.View;

public class KickBackAnimator implements TypeEvaluator<Float> {
	private final float s = 1.70158f;
	float mDuration = 0f;

	public void setDuration(float duration) {
		mDuration = duration;
	}

	public Float evaluate(float fraction, Float startValue, Float endValue) {
		float t = mDuration * fraction;
		float b = startValue.floatValue();
		float c = endValue.floatValue() - startValue.floatValue();
		float d = mDuration;
		float result = calculate(t, b, c, d);
		return result;
	}

	public Float calculate(float t, float b, float c, float d) {
		return c * ((t = t / d - 1) * t * ((s + 1) * t + s) + 1) + b;
	}
	/**
	 * 
	 * @param view
	 * @param close 是否是关闭
	 * @param values A set of values that the animation will animate between over time.
	 */
	public static void setAnimation(final View view,AnimatorListener closeListener,float... values){
	
		view.setVisibility(View.VISIBLE);
		ValueAnimator fadeAnim = ObjectAnimator.ofFloat(view, "translationY", values);
		fadeAnim.setDuration(500);
		KickBackAnimator kickAnimator = new KickBackAnimator();
		kickAnimator.setDuration(350);
		fadeAnim.setEvaluator(kickAnimator);
		fadeAnim.start();
		if (closeListener!=null) {
			fadeAnim.addListener(closeListener);
		}
		
	}
}
