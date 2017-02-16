package com.example.administrator.myproject.view;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.myproject.ApplicationController;
import com.example.administrator.myproject.R;


public class HaloToast {
	private static Toast sToast = null;
	public static void systemShow(Context c, String s) {
		Toast.makeText(c.getApplicationContext(), s, Toast.LENGTH_SHORT).show();
	}
	public static void show(String s) {
		show(ApplicationController.getInstance(), s, Toast.LENGTH_SHORT);
	}
	public static void show(Context c, String s) {
		show(c, s, Toast.LENGTH_SHORT);
	}

	public static void show(Context c, int res) {
		show(c, res, Toast.LENGTH_SHORT);
	}

	public static void show(Context c, int res, int duration) {
		show(c, c.getString(res), duration);
	}

	public static void show(Context c, String s, int duration) {
		View layout = getToastView(c.getApplicationContext());
		TextView text = (TextView) layout.findViewById(R.id.text);
		text.setVisibility(View.VISIBLE);
		text.setText(s);
		showToast(layout, duration);
	}

	private static View getToastView(Context c) {
		return LayoutInflater.from(c).inflate(R.layout.toast, null);
	}

	private static void showToast(View view, int duration) {
		showToast(view, duration, Gravity.CENTER, 0);
	}

	private static void showToast(View view, int duration, int gravity,
			int yOffset) {
		if (sToast == null) {
			sToast = new Toast(view.getContext());
		}
		sToast.setGravity(gravity, 0, yOffset);
		sToast.setDuration(duration);
		sToast.setView(view);
		sToast.show();
	}
}
