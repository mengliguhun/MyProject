package com.example.administrator.myproject.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.AttributeSet;

import com.example.administrator.myproject.R;
import com.example.administrator.myproject.utils.GraphicUtils;

/**
 * 右下角有徽标的圆形ImageView
 * Created by hubin on 16/6/25.
 */
public class BadgeCircleImageView extends CircleImageView {

    private final int DEFAULT_SIZE = 12;
    private Bitmap badge;

    private int badgeSize = DEFAULT_SIZE;

    private final Paint mBitmapPaint = new Paint();

    public BadgeCircleImageView(Context context) {
        this(context, null);
    }

    public BadgeCircleImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BadgeCircleImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.BadgeCircleImageView, defStyle, 0);
        try {
            int resId = a.getResourceId(R.styleable.BadgeCircleImageView_footer_img_res,0);
            badgeSize = a.getInt(R.styleable.BadgeCircleImageView_footer_img_size_dp,DEFAULT_SIZE);
            if(resId != 0){
                initBadgeImg(resId, badgeSize);
            }
        }finally {
            a.recycle();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(badge != null){
            canvas.drawBitmap(badge,
                    this.getWidth()- GraphicUtils.dip2px(getContext(), badgeSize),
                    this.getHeight()-GraphicUtils.dip2px(getContext(), badgeSize),
                    this.mBitmapPaint);
        }
    }

    private void initBadgeImg(int resId, int badgeSize){
        this.badgeSize = badgeSize;
        if(resId != 0){
            badge = BitmapFactory
                    .decodeResource(getContext().getResources(), resId);
            int width = badge.getWidth();
            int height = badge.getHeight();
            // 设置想要的大小
            int newWidth = GraphicUtils.dip2px(getContext(), badgeSize);
            int newHeight = GraphicUtils.dip2px(getContext(), badgeSize);
            // 计算缩放比例
            float scaleWidth = ((float) newWidth) / width;
            float scaleHeight = ((float) newHeight) / height;
            // 取得想要缩放的matrix参数
            Matrix matrix = new Matrix();
            matrix.postScale(scaleWidth, scaleHeight);
            // 得到新的图片
            badge = Bitmap.createBitmap(badge, 0, 0, width, height, matrix, true);
        }else{
            badge = null;
        }
    }

    public void showBadge(int resId, int badgeSize) {
        initBadgeImg(resId, badgeSize);
        invalidate();
    }

    public void showBadge(int resId) {
        initBadgeImg(resId, DEFAULT_SIZE);
        invalidate();
    }
}
