package com.example.administrator.myproject.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

/**
 * Created by Administrator on 2016/11/25.
 */

public class GImageView  extends GifImageView{
    public GImageView(Context context) {
        super(context);
    }

    public GImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void setImageDrawable(Drawable drawable) {
        if (drawable == null || drawable instanceof GifDrawable){
            super.setImageDrawable(drawable);
        }
        else {
            try {
                GifDrawable d = new GifDrawable(drawable2Bytes(drawable));
                super.setImageDrawable(d);
            } catch (IOException e) {
                e.printStackTrace();
                super.setImageDrawable(drawable);
            }
        }
    }

    @Override
    public void setBackground(Drawable background) {
        if (background == null || background instanceof GifDrawable){
            super.setBackground(background);
        }
        else {
            try {
                GifDrawable d = new GifDrawable(drawable2Bytes(background));
                super.setBackground(d);
            } catch (IOException e) {
                e.printStackTrace();
                super.setBackground(background);
            }
        }
    }

    @Override
    public void setBackgroundDrawable(Drawable background) {
        if (background == null || background instanceof GifDrawable){
            super.setBackgroundDrawable(background);
        }
        else {
            try {
                GifDrawable d = new GifDrawable(drawable2Bytes(background));
                super.setBackgroundDrawable(d);
            } catch (IOException e) {
                e.printStackTrace();
                super.setBackgroundDrawable(background);
            }
        }
    }

    // Drawable转换成Bitmap
    public Bitmap drawable2Bitmap(Drawable drawable) {
        Bitmap bitmap = Bitmap
                .createBitmap(
                        drawable.getIntrinsicWidth(),
                        drawable.getIntrinsicHeight(),
                        drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
                                : Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight());
        drawable.draw(canvas);
        return bitmap;
    }
    // Bitmap转换成byte[]
    public byte[] Bitmap2Bytes(Bitmap bm) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
        return baos.toByteArray();
    }

    // Drawable转换成byte[]
    public byte[] drawable2Bytes(Drawable d) {
        Bitmap bitmap = this.drawable2Bitmap(d);
        return this.Bitmap2Bytes(bitmap);
    }
}
