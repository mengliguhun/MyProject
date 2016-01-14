package com.example.administrator.myproject.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;

public class LinearLayoutForListView extends LinearLayout {
	private BaseAdapter adapter;
	private OnItemClickListener onItemClickListener = null;
	View mDivider;
	int mDividerHeight = 1;
	int resid = android.R.color.darker_gray;
	public LinearLayoutForListView(Context context) {
		super(context);
		setOrientation(VERTICAL);
	}

	public LinearLayoutForListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		setOrientation(VERTICAL);
	}

	public BaseAdapter getAdapter() {
		return adapter;
	}

	public void setAdapter(BaseAdapter adapter) {
		this.adapter = adapter;
		bindLinearLayout();
	}
	/**
	 * 
	 * @param height
	 */
	public void setDividerHeight(int height) {
		mDividerHeight = height;
	}
	public void setDividerBackgroundResource(int resid) {
		this.resid = resid;
	}
	private void bindLinearLayout() {
		this.removeAllViews();
		for (int i = 0; i < adapter.getCount(); i++) {
			View v = adapter.getView(i, null, null);
			LinearLayout layout = new LinearLayout(getContext());
			layout.setOrientation(VERTICAL);
			layout.addView(v);
			if (i < adapter.getCount()-1) {
				layout.addView(initDividerView());
			}
			
			this.addView(layout, i);
			initDividerView();
			v.setTag(i);
			if (onItemClickListener != null) {
				v.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						onItemClickListener.onItemClick(
								LinearLayoutForListView.this, v,
								(Integer) v.getTag());
					}
				});
			}
		}
	}

	private View initDividerView() {
		View view = new View(getContext());
		view.setBackgroundResource(resid);
		LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT,mDividerHeight);
		view.setLayoutParams(layoutParams);
		return view;
	}

	public OnItemClickListener getOnItemClickListener() {
		return onItemClickListener;
	}

	public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
		this.onItemClickListener = onItemClickListener;
	}

	public interface OnItemClickListener {
		public abstract void onItemClick(ViewGroup parent, View view,
										 int position);
	}

}
