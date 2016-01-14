package com.example.administrator.myproject.utils;

import android.animation.Keyframe;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.annotation.SuppressLint;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;

public class AnimationUtil {
	
	private static final int ANIMATION_TIME = 300;
	
	/**
	 * Custom animation that animates in from right
	 *
	 * @return Animation the Animation object
	 */
	public static Animation inFromRightAnimation() {
		Animation inFromRight = new TranslateAnimation(
				Animation.RELATIVE_TO_PARENT, 1.0f,
				Animation.RELATIVE_TO_PARENT, 0.0f,
				Animation.RELATIVE_TO_PARENT, 0.0f,
				Animation.RELATIVE_TO_PARENT, 0.0f);
		return setProperties(inFromRight);
	}

	/**
	 * Custom animation that animates out to the right
	 *
	 * @return Animation the Animation object
	 */
	public static Animation outToRightAnimation() {
		Animation outToRight = new TranslateAnimation(
				Animation.RELATIVE_TO_PARENT, 0.0f,
				Animation.RELATIVE_TO_PARENT, 1.0f,
				Animation.RELATIVE_TO_PARENT, 0.0f,
				Animation.RELATIVE_TO_PARENT, 0.0f);
		return setProperties(outToRight);
	}

	/**
	 * Custom animation that animates in from left
	 *
	 * @return Animation the Animation object
	 */
	public static Animation inFromLeftAnimation() {
		Animation inFromLeft = new TranslateAnimation(
				Animation.RELATIVE_TO_PARENT, -1.0f,
				Animation.RELATIVE_TO_PARENT, 0.0f,
				Animation.RELATIVE_TO_PARENT, 0.0f,
				Animation.RELATIVE_TO_PARENT, 0.0f);
		return setProperties(inFromLeft);
	}

	/**
	 * Custom animation that animates out to the left
	 *
	 * @return Animation the Animation object
	 */
	public static Animation outToLeftAnimation() {
		Animation outtoLeft = new TranslateAnimation(
				Animation.RELATIVE_TO_PARENT, 0.0f,
				Animation.RELATIVE_TO_PARENT, -1.0f,
				Animation.RELATIVE_TO_PARENT, 0.0f,
				Animation.RELATIVE_TO_PARENT, 0.0f);
		return setProperties(outtoLeft);
	}

	/**
	 * Helper method that sets some common properties
	 *
	 * @param animation
	 *            the animation to give common properties
	 * @return the animation with common properties
	 */
	public static Animation setProperties(Animation animation) {
		animation.setDuration(ANIMATION_TIME);
		animation.setInterpolator(new AccelerateInterpolator());
		return animation;
	}
	
//	public static ObjectAnimator tada(View view) {
//	    return tada(view, 1f);
//	}

//	public static ObjectAnimator tada(View view, float shakeFactor) {
//
//	    PropertyValuesHolder pvhScaleX = PropertyValuesHolder.ofKeyframe(View.SCALE_X,
//	            Keyframe.ofFloat(0f, 1f),
//	            Keyframe.ofFloat(.1f, .9f),
//	            Keyframe.ofFloat(.2f, .9f),
//	            Keyframe.ofFloat(.3f, 1.1f),
//	            Keyframe.ofFloat(.4f, 1.1f),
//	            Keyframe.ofFloat(.5f, 1.1f),
//	            Keyframe.ofFloat(.6f, 1.1f),
//	            Keyframe.ofFloat(.7f, 1.1f),
//	            Keyframe.ofFloat(.8f, 1.1f),
//	            Keyframe.ofFloat(.9f, 1.1f),
//	            Keyframe.ofFloat(1f, 1f)
//	    );
//
//	    PropertyValuesHolder pvhScaleY = PropertyValuesHolder.ofKeyframe(View.SCALE_Y,
//	            Keyframe.ofFloat(0f, 1f),
//	            Keyframe.ofFloat(.1f, .9f),
//	            Keyframe.ofFloat(.2f, .9f),
//	            Keyframe.ofFloat(.3f, 1.1f),
//	            Keyframe.ofFloat(.4f, 1.1f),
//	            Keyframe.ofFloat(.5f, 1.1f),
//	            Keyframe.ofFloat(.6f, 1.1f),
//	            Keyframe.ofFloat(.7f, 1.1f),
//	            Keyframe.ofFloat(.8f, 1.1f),
//	            Keyframe.ofFloat(.9f, 1.1f),
//	            Keyframe.ofFloat(1f, 1f)
//	    );
//
//	    PropertyValuesHolder pvhRotate = PropertyValuesHolder.ofKeyframe(View.ROTATION,
//	            Keyframe.ofFloat(0f, 0f),
//	            Keyframe.ofFloat(.1f, -3f * shakeFactor),
//	            Keyframe.ofFloat(.2f, -3f * shakeFactor),
//	            Keyframe.ofFloat(.3f, 3f * shakeFactor),
//	            Keyframe.ofFloat(.4f, -3f * shakeFactor),
//	            Keyframe.ofFloat(.5f, 3f * shakeFactor),
//	            Keyframe.ofFloat(.6f, -3f * shakeFactor),
//	            Keyframe.ofFloat(.7f, 3f * shakeFactor),
//	            Keyframe.ofFloat(.8f, -3f * shakeFactor),
//	            Keyframe.ofFloat(.9f, 3f * shakeFactor),
//	            Keyframe.ofFloat(1f, 0)
//	    );
//
//	    return ObjectAnimator.ofPropertyValuesHolder(view, pvhScaleX, pvhScaleY, pvhRotate).
//	            setDuration(1000);
//	}
//    public static ObjectAnimator nope(View view) {  
//        int delta = view.getResources().getDimensionPixelOffset(R.dimen.spacing_medium);  
//      
//        PropertyValuesHolder pvhTranslateX = PropertyValuesHolder.ofKeyframe(View.TRANSLATION_X,  
//                Keyframe.ofFloat(0f, 0),  
//                Keyframe.ofFloat(.10f, -delta),  
//                Keyframe.ofFloat(.26f, delta),  
//                Keyframe.ofFloat(.42f, -delta),  
//                Keyframe.ofFloat(.58f, delta),  
//                Keyframe.ofFloat(.74f, -delta),  
//                Keyframe.ofFloat(.90f, delta),  
//                Keyframe.ofFloat(1f, 0f)  
//        );  
//      
//        return ObjectAnimator.ofPropertyValuesHolder(view, pvhTranslateX).  
//                setDuration(500);  
//    }  
}
