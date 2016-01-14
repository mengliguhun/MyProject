package com.example.administrator.myproject.utils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader.TileMode;
import android.net.Uri;
import android.util.DisplayMetrics;
import android.view.View;

public final class GraphicUtils {
    // common method
    public static int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    // ui controls
    public static Bitmap createRoundImage(Context context,
            Bitmap originalImage, Bitmap mask) {
        RectF clipRect = new RectF();
        int width = originalImage.getWidth();
        int height = originalImage.getHeight();

        Bitmap roundBitmap = Bitmap.createBitmap(width, height,
            Config.ARGB_8888);
        Canvas canvas = new Canvas(roundBitmap);
        canvas.drawBitmap(originalImage, 0, 0, null);
        clipRect.set(0, 0, width, height);
        canvas.drawBitmap(mask, null, clipRect, null);
        return roundBitmap;
    }

    public static Bitmap createRoundImage(Context context,
            Bitmap originalImage, int dp) {
        final int CONNER = dip2px(context, dp);
        RectF clipRect = new RectF();
        int width = originalImage.getWidth();
        int height = originalImage.getHeight();

        Bitmap roundBitmap = Bitmap.createBitmap(width, height,
            Config.ARGB_8888);
        Canvas canvas = new Canvas(roundBitmap);
        Path path = new Path();
        clipRect.set(0, 0, width, height);
        path.addRoundRect(clipRect, CONNER, CONNER, Path.Direction.CCW);
        canvas.clipPath(path);
        canvas.drawBitmap(originalImage, null, clipRect, null);
        return roundBitmap;
    }

    public static Bitmap createReflectedImage(Context context,
            Bitmap originalImage, Bitmap mask) {
        final int reflectionGap = 1;
        final int CONNER = dip2px(context, 5);
        Path path = new Path();
        RectF clipRect = new RectF();

        int width = originalImage.getWidth();
        int height = originalImage.getHeight();

        Matrix matrix = new Matrix();
        matrix.preScale(1, -1);

        Bitmap reflectionImage = Bitmap.createBitmap(originalImage, 0,
            height * 3 / 4, width, height / 4, matrix, false);
        Bitmap bitmapWithReflection = Bitmap.createBitmap(width,
            (height + height / 4), Config.ARGB_8888);

        Canvas canvas = new Canvas(bitmapWithReflection);
        canvas.drawBitmap(originalImage, 0, 0, null);
        clipRect.set(0, 0, width, height);
        canvas.drawBitmap(mask, null, clipRect, null);

        canvas.save();
        clipRect.set(0, height, width, (height + height / 4 + CONNER)
                + reflectionGap);
        path.addRoundRect(clipRect, CONNER, CONNER, Path.Direction.CCW);
        canvas.clipPath(path);
        Paint p1 = new Paint();
        p1.setColor(Color.LTGRAY);
        canvas.drawRect(clipRect, p1);
        canvas.restore();

        canvas.save();
        clipRect.set(1, height + 1, width - 1, (height + height / 4 + CONNER)
                + reflectionGap);
        path.reset();
        path.addRoundRect(clipRect, CONNER, CONNER, Path.Direction.CCW);
        canvas.clipPath(path);
        canvas.drawBitmap(reflectionImage, 0, height + reflectionGap, null);
        canvas.restore();

        Paint paint = new Paint();
        LinearGradient shader = new LinearGradient(0, height, 0,
                bitmapWithReflection.getHeight() + reflectionGap, 0x50000000,
                0x00000000, TileMode.CLAMP);
        paint.setShader(shader);
        paint.setXfermode(new PorterDuffXfermode(Mode.DST_IN));
        canvas.drawRect(0, height, width, bitmapWithReflection.getHeight()
                + reflectionGap, paint);
        return bitmapWithReflection;
    }

    public final static int LOCATION_LEFT_TOP = 0;
    public final static int LOCATION_BOTTOM_RIGHT = 1;

    public static final Point getViewPosition(View v, int t) {
        int[] locations = new int[2];
        v.getLocationInWindow(locations);
        Point p = new Point(locations[0], locations[1]);
        if (t == LOCATION_LEFT_TOP)
            return p;
        if (t == LOCATION_BOTTOM_RIGHT) {
            int width = v.getWidth();
            int height = v.getHeight();
            p.x += width;
            p.y += height;
            return p;
        }
        return new Point(0, 0);
    }

    public static Bitmap getBitmapByBitmap(Bitmap backBitmap, Bitmap mask,
            int x, int y) {
        Bitmap output = Bitmap.createBitmap(backBitmap.getWidth(),
            backBitmap.getHeight(), Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, backBitmap.getWidth(),
                backBitmap.getHeight());
        paint.setAntiAlias(true);
        canvas.drawBitmap(mask, x, y, paint);
        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
        canvas.drawBitmap(backBitmap, rect, rect, paint);
        return output;
    }

    public final static boolean pointInView(Point p, View v) {
        Point lt = GraphicUtils.getViewPosition(v,
            GraphicUtils.LOCATION_LEFT_TOP);
        Point br = GraphicUtils.getViewPosition(v,
            GraphicUtils.LOCATION_BOTTOM_RIGHT);
        return (p.x >= lt.x && p.x <= br.x && p.y >= lt.y && p.y <= br.y);
    }
    
    public final static Bitmap getImageScaleMessage(Context c,Bitmap bmp,int w,
            int h)
    {
    	int widthMax = GraphicUtils.dip2px(c, w);
        int heightMax = GraphicUtils.dip2px(c, h);
        Bitmap result = null;
        try {
            // result = BitmapFactory.decodeFile(path);
            BitmapFactory.Options op = new BitmapFactory.Options();
            op.inJustDecodeBounds = true;

            int s = 1;
            op.inJustDecodeBounds = false;
            int widthRatio = op.outWidth / widthMax;
            int heightRatio = op.outHeight / heightMax;
            if (widthRatio > 1 || heightRatio > 1) {
                s = Math.max(widthRatio, heightRatio);
                op.inSampleSize = s;
            }
            int newWidth = bmp.getWidth();
            int newHeight = bmp.getHeight();
            if (newWidth <= widthMax && newHeight <= heightMax)
                return bmp;

            float scaleWidth = ((float) widthMax) / newWidth;
            float scaleHeight = ((float) heightMax) / newHeight;
            float scale = Math.min(scaleWidth, scaleHeight);

            Matrix matrix = new Matrix();
            matrix.postScale(scale, scale);

            result = Bitmap.createBitmap(bmp, 0, 0, newWidth, newHeight,
                matrix, true);

            // Recycle original bitmap
            if (bmp != null)
                bmp.recycle();
        } catch (Exception e) {
            return null;
        }
        return result;
    }
    public final static Bitmap getImageThumbnail(Context c, Uri uri, int w,
            int h) {
        int widthMax = GraphicUtils.dip2px(c, w);
        int heightMax = GraphicUtils.dip2px(c, h);

        Bitmap result = null;
        try {
            String path = uri.getPath();
            // result = BitmapFactory.decodeFile(path);
            BitmapFactory.Options op = new BitmapFactory.Options();
            op.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(path, op);

            int s = 2;
            op.inJustDecodeBounds = false;
            int widthRatio = op.outWidth / widthMax;
            int heightRatio = op.outHeight / heightMax;
            if (op.outWidth < 1) {
                op.inSampleSize = 8;
            } else if (widthRatio > 1 || heightRatio > 1) {
                s = Math.max(widthRatio, heightRatio);
                op.inSampleSize = s;
            }

            Bitmap bmp = BitmapFactory.decodeFile(path, op);

            int newWidth = bmp.getWidth();
            int newHeight = bmp.getHeight();
            if (newWidth <= widthMax && newHeight <= heightMax)
                return bmp;

            float scaleWidth = ((float) widthMax) / newWidth;
            float scaleHeight = ((float) heightMax) / newHeight;
            float scale = Math.min(scaleWidth, scaleHeight);

            Matrix matrix = new Matrix();
            matrix.postScale(scale, scale);

            result = Bitmap.createBitmap(bmp, 0, 0, newWidth, newHeight,
                matrix, true);

            // Recycle original bitmap
            if (bmp != null)
                bmp.recycle();
        } catch (Exception e) {
            return null;
        }
        return result;
    }

    // TODO: some temp code here, to improve which, we need to override the
    // ListView
    // and add api atTop() and atBottom() in it.
    // f**k the stupid Android engineers...
    public final static int AT_TOP = 0;
    public final static int AT_BOTTOM = 1;
    public final static int OTHER = 2;
    
    public static final int PULL_TO_REFRESH = 3;
    public static final int RELEASE_TO_REFRESH = 4;
    public static final int REFRESHING = 5;
    public static final int FRESH_OTHER=6;

    public static Bitmap toGrayscale(Bitmap bmpOriginal) {
        int width, height;
        height = bmpOriginal.getHeight();
        width = bmpOriginal.getWidth();    

        Bitmap bmpGrayscale = Bitmap.createBitmap(width, height, Config.RGB_565);
        Canvas c = new Canvas(bmpGrayscale);
        Paint paint = new Paint();
        ColorMatrix cm = new ColorMatrix();
        cm.setSaturation(0);
        ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
        paint.setColorFilter(f);
        c.drawBitmap(bmpOriginal, 0, 0, paint);
        return bmpGrayscale;
    }
    
    public static int getColor(int c){
        return Color.rgb(c>>16, (c&0x00ffff)>>8, c&0x0000ff);
    }
    
    public final static Bitmap getImage(Context c, Uri uri) {

        Bitmap bmp = null;
        try {
            String path = uri.getPath();
            BitmapFactory.Options op = new BitmapFactory.Options();
            op.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(path, op);
            
            if (op.outHeight < 1)
                op.inSampleSize = 2;
            else if (op.outHeight > 1024 || op.outWidth > 1024) {
                op.inSampleSize = 2;
            } else
                op.inSampleSize = 1;
            op.inJustDecodeBounds = false;
            bmp = BitmapFactory.decodeFile(path, op);
            if(bmp == null){
                bmp = BitmapFactory.decodeStream(c.getContentResolver().openInputStream(uri), null, op);
            }
            return bmp;
        } catch (Exception e) {
            return bmp;
        }catch(OutOfMemoryError e1){
        	return bmp;
        }
    }
    
    public final static Bitmap getImage(Uri uri) {
        Bitmap result = null;
        try {
            String path = uri.getPath();
            BitmapFactory.Options op = new BitmapFactory.Options();
            op.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(path, op);
            
            if (op.outHeight < 1)
                op.inSampleSize = 2;
            else
                op.inSampleSize = 1;
            op.inJustDecodeBounds = false;
            Bitmap bmp = BitmapFactory.decodeFile(path, op);
            result = bmp;
            return result;
        } catch (Exception e) {
            return null;
        }catch(OutOfMemoryError e1){
            return null;
        }
    }
    
    public static boolean saveImageBitmap(Bitmap bitmap, File path, int quality,
            Bitmap.CompressFormat format) {
        boolean opRet = false;
        try {
            BufferedOutputStream bos = new BufferedOutputStream(
                    new FileOutputStream(path));
            bitmap.compress(format, quality, bos);
            bos.flush();
            bos.close();
            opRet = true;
        } 
        catch (Exception e) {
        
        }
        return opRet;
    }

}
