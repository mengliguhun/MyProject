/*
 * Copyright (C) 2008 ZXing authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.zxing.client.android;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import com.google.zxing.ResultPoint;
import com.google.zxing.client.android.camera.CameraManager;

import java.util.ArrayList;
import java.util.List;

/**
 * This view is overlaid on top of the camera preview. It adds the viewfinder rectangle and partial
 * transparency outside it, as well as the laser scanner animation and result points.
 *
 * @author dswitkin@google.com (Daniel Switkin)
 */
public final class ViewfinderView extends View {

    private static final long ANIMATION_DELAY = 80L;
    private static final int CURRENT_POINT_OPACITY = 0xA0;
    private static final int MAX_RESULT_POINTS = 20;
    private static final int POINT_SIZE = 6;
    /**
     * 四个绿色边角对应的长度
     */
    private static final int CORNER_LENGTH = 20;
    /**
     * 四个绿色边角对应的宽度
     */
    private static final int CORNER_WIDTH = 5;
    /**
     * 扫描框中的中间线的宽度
     */
    private static final int MIDDLE_LINE_WIDTH = 3;
    /**
     * 扫描框中的中间线的与扫描框左右的间隙
     */
    private static final int MIDDLE_LINE_PADDING = 5;
    /**
     * 中间滑动线的最顶端位置
     */
    private int slideTop;

    /**
     * 中间那条线每次刷新移动的距离
     */
    private static final int SPEEN_DISTANCE = 10;

    private CameraManager cameraManager;
    private final Paint paint;
    private Bitmap resultBitmap;
    private final int maskColor;
    private final int resultColor;
    private List<ResultPoint> possibleResultPoints;

    // This constructor is used when the class is built from an XML resource.
    public ViewfinderView(Context context, AttributeSet attrs) {
        super(context, attrs);

        // Initialize these once for performance rather than calling them every time in onDraw().
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        Resources resources = getResources();
        maskColor = resources.getColor(R.color.viewfinder_mask);
        resultColor = resources.getColor(R.color.result_view);
        possibleResultPoints = new ArrayList<>(5);
    }

    public void setCameraManager(CameraManager cameraManager) {
        this.cameraManager = cameraManager;
    }

    @SuppressLint("DrawAllocation")
    @Override
    public void onDraw(Canvas canvas) {
        if (cameraManager == null) {
            return; // not ready yet, early draw before done configuring
        }
        Rect frame = cameraManager.getFramingRect();
        Rect previewFrame = cameraManager.getFramingRectInPreview();
        if (frame == null || previewFrame == null) {
            return;
        }
        int width = canvas.getWidth();
        int height = canvas.getHeight();

        // Draw the exterior (i.e. outside the framing rect) darkened
        paint.setColor(resultBitmap != null ? resultColor : maskColor);
        canvas.drawRect(0, 0, width, frame.top, paint);
        canvas.drawRect(0, frame.top, frame.left, frame.bottom + 1, paint);
        canvas.drawRect(frame.right + 1, frame.top, width, frame.bottom + 1, paint);
        canvas.drawRect(0, frame.bottom + 1, width, height, paint);

        if (resultBitmap != null) {
            // Draw the opaque result bitmap over the scanning rectangle
            paint.setAlpha(CURRENT_POINT_OPACITY);
            canvas.drawBitmap(resultBitmap, null, frame, paint);
        } else {
            //画出四个角
            paint.setColor(Color.WHITE);
            //左上角
            canvas.drawRect(frame.left-CORNER_WIDTH, frame.top-CORNER_WIDTH, frame.left + CORNER_LENGTH, frame.top, paint);
            canvas.drawRect(frame.left-CORNER_WIDTH,frame.top -CORNER_WIDTH, frame.left, frame.top + CORNER_LENGTH, paint);

            //右上角
            canvas.drawRect(frame.right - CORNER_LENGTH, frame.top-CORNER_WIDTH, frame.right+CORNER_WIDTH, frame.top, paint);
            canvas.drawRect(frame.right, frame.top-CORNER_WIDTH, frame.right+CORNER_WIDTH, frame.top + CORNER_LENGTH, paint);

            //左下角
            canvas.drawRect(frame.left-CORNER_WIDTH, frame.bottom - CORNER_LENGTH, frame.left, frame.bottom+CORNER_WIDTH, paint);
            canvas.drawRect(frame.left-CORNER_WIDTH, frame.bottom, frame.left + CORNER_LENGTH, frame.bottom+CORNER_WIDTH,paint);

            //右下角
            canvas.drawRect(frame.right - CORNER_LENGTH, frame.bottom, frame.right+CORNER_WIDTH, frame.bottom+CORNER_WIDTH, paint);
            canvas.drawRect(frame.right, frame.bottom - CORNER_LENGTH, frame.right+CORNER_WIDTH, frame.bottom+CORNER_WIDTH, paint);

            //绘制扫描框下面文本
            paint.setTextAlign(Paint.Align.CENTER);
            paint.setTextSize(setTextSize(TypedValue.COMPLEX_UNIT_DIP,13));
            paint.setColor(Color.WHITE);
            canvas.drawText("将二维码图案放入框内，即可自动扫描",width/2,frame.bottom +setTextSize(TypedValue.COMPLEX_UNIT_DIP,50) ,paint);

            //绘制中间的线,每次刷新界面，中间的线往下移动SPEEN_DISTANCE
            paint.setColor(Color.WHITE);
            slideTop += SPEEN_DISTANCE;
            if(slideTop >= frame.bottom - frame.top){
                slideTop = 0;
            }
            canvas.drawRect(frame.left + MIDDLE_LINE_PADDING, frame.top + slideTop - MIDDLE_LINE_WIDTH/2,
                    frame.right - MIDDLE_LINE_PADDING,frame.top + slideTop + MIDDLE_LINE_WIDTH/2, paint);
            // bitmap
//            Rect lineRect = new Rect();
//            lineRect.left = frame.left;
//            lineRect.right = frame.right;
//            lineRect.top = frame.top +slideTop;
//            lineRect.bottom = frame.top + slideTop + 18;
//            canvas.drawBitmap(((BitmapDrawable)(getResources().getDrawable(R.drawable.qrcode_scan_line))).getBitmap(), null, lineRect, paint);

            // Request another update at the animation interval, but only repaint the laser line,
            // not the entire viewfinder mask.
            postInvalidateDelayed(ANIMATION_DELAY,
                    frame.left - POINT_SIZE,
                    frame.top - POINT_SIZE,
                    frame.right + POINT_SIZE,
                    frame.bottom + POINT_SIZE);
        }
    }

    public void drawViewfinder() {
        Bitmap resultBitmap = this.resultBitmap;
        this.resultBitmap = null;
        if (resultBitmap != null) {
            resultBitmap.recycle();
        }
        invalidate();
    }

    /**
     * Draw a bitmap with the result points highlighted instead of the live scanning display.
     *
     * @param barcode An image of the decoded barcode.
     */
    public void drawResultBitmap(Bitmap barcode) {
        resultBitmap = barcode;
        invalidate();
    }

    public void addPossibleResultPoint(ResultPoint point) {
        List<ResultPoint> points = possibleResultPoints;
        synchronized (points) {
            points.add(point);
            int size = points.size();
            if (size > MAX_RESULT_POINTS) {
                // trim it
                points.subList(0, size - MAX_RESULT_POINTS / 2).clear();
            }
        }
    }

    public float setTextSize(int unit, float size) {
        Context c = getContext();
        Resources r;

        if (c == null)
            r = Resources.getSystem();
        else
            r = c.getResources();

       return TypedValue.applyDimension(unit, size, r.getDisplayMetrics());
    }
}
