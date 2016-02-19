package com.example.administrator.myproject.view;

import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Gallery;

public class CustomGallery extends Gallery implements OnTouchListener {
	private Context mContext;
	private int mSwitchTime = 3000; // 图片切换时间

	private boolean runflag = false;
	private Timer mTimer; // 自动滚动的定时器
	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (!runflag) {
				return;
			}
			int position = getSelectedItemPosition();
			if (position >= (getCount() - 1)) {
				setSelection(1, true); // 跳转到第1张图片

			} else if (position == 0) {
				setSelection(getCount() - 2, true);// 跳转到最后一张张图片

			} else {
				onScroll(null, null, 1, 0);
				onKeyDown(KeyEvent.KEYCODE_DPAD_RIGHT, null);
//				setSelection(position, true);
			}
		}
	};

	public CustomGallery(Context context) {
		super(context);
		init();
	}

	public CustomGallery(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	private boolean isScrollingLeft(MotionEvent e1, MotionEvent e2) {

		return e2.getX() > e1.getX();

	}

	private void init() {
		mTimer = new Timer();
		this.setOnTouchListener(this);  
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {

		if (getSelectedItemPosition() >= (getCount() - 1)) {
			setSelection(1, true); // 跳转到第1张图片

		} else if (getSelectedItemPosition() == 0) {
			setSelection(getCount() - 1, true);// 跳转到最后一张张图片

		}
		int keyCode;
		// 这样能够实现每次滑动只滚动一张图片的效果
		if (isScrollingLeft(e1, e2)) {
			keyCode = KeyEvent.KEYCODE_DPAD_LEFT;

		} else {
			keyCode = KeyEvent.KEYCODE_DPAD_RIGHT;

		}
		onKeyDown(keyCode, null);

		return true;

	}
	
	/**
	 * 开始自动循环切换
	 */
	public void startAutoSwitch() {
		setRunFlag(true);
		startAutoScroll();
	}

	/**
	 * 停止自动循环切换
	 */
	public void stopAutoSwitch() {
		setRunFlag(true);
	}

	/**
	 * 开始自动滚动
	 */
	private void startAutoScroll() {
		mTimer.schedule(new TimerTask() {

			public void run() {
				if (runflag && getCount()>3) {
					Message msg = new Message();
					if (getSelectedItemPosition() < (getCount() - 1)) {
						msg.what = getSelectedItemPosition() + 1;
					} else {
						msg.what = 0;
					}
					handler.sendMessage(msg);
				}
			}
		}, mSwitchTime, mSwitchTime);

	}

	public void setRunFlag(boolean flag) {
		runflag = flag;
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		if (MotionEvent.ACTION_UP == event.getAction()
				|| MotionEvent.ACTION_CANCEL == event.getAction()) {
			// 重置自动滚动任务
			setRunFlag(true);
		} else {
			// 停止自动滚动任务
			setRunFlag(false);
		}
		return false;
	}
}
