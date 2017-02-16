package com.example.administrator.myproject.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.os.Build;
import android.os.Environment;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;

/**
 * 静态评画View
 *  图片默认自动拉伸适应屏幕宽高,不能手动调整大小和图片位置
 * Created by hubin on 16/9/23.
 */
public class BasicGraffitiView extends View implements ViewTreeObserver.OnGlobalLayoutListener{

    /** 默认画笔大小 */
    private static final int DRAWSIZE = 10;
    /** 默认橡皮擦大小 */
    private static final int ERASERSIZE = 80;

    /** 需要点评的Bitmap */
    private Bitmap imgBitmap;

    /** 根据点评的Bitmap的大小,创建的空Bitmap,覆盖在点评的Bitmap上用来绘制红线 */
    private Bitmap coverBitmap;
    /** 点评操作的画布,通过此画布往coverBitmap上绘制 */
    private Canvas pathCanvas;

    /** 画笔 */
    private Paint paint;
    /** 评画红线的画笔 */
    private Paint drawPaint;
    /** 橡皮擦的画笔 */
    private Paint eraserPaint;

    /** 画笔大小 */
    private int drawSize = DRAWSIZE;
    /** 橡皮擦大小 */
    private int eraserSize = ERASERSIZE;

    /** 点评时所绘制的所有Path记录 */
    private ArrayList<PathRecord> records;

    /** 撤销某次绘制Path的历史记录,为了Redo(重做) */
    private ArrayList<PathRecord> historys;

    /** Bitmap初始显示的控制矩阵 */
    private final Matrix mBaseMatrix = new Matrix();
    /** Bitmap进行变换时操作的矩阵 */
    protected final Matrix mSuppMatrix = new Matrix();
    /** Bitmap最终显示时使用的矩阵 */
    private final Matrix mDrawMatrix = new Matrix();
    /** Bitmap在View中显示的区域Rect */
    private final RectF mDisplayRect = new RectF();
    /** 存放Matrix变换后各变换后的值 */
    private final float[] mMatrixValues = new float[9];

    /** View的宽度 */
    protected int viewWidth = 0;
    /** View的高度 */
    protected int viewHeight = 0;

    public BasicGraffitiView(Context context) {
        this(context, null);
    }

    public BasicGraffitiView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BasicGraffitiView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        // 监听View的onLayout,在那里取View得尺寸信息
        ViewTreeObserver observer = getViewTreeObserver();
        if (null != observer)
            observer.addOnGlobalLayoutListener(this);

        // 初始化绘制的所有Path记录列表
        records = new ArrayList<>();
        // 初始化撤销的Path的历史记录列表
        historys = new ArrayList<>();

        // 初始化画笔
        paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeWidth(eraserSize);

        // 初始化评画红线的画笔
        drawPaint = new Paint();
        drawPaint.setColor(Color.RED);
        drawPaint.setAntiAlias(true);
        drawPaint.setStyle(Paint.Style.STROKE);
        drawPaint.setStrokeCap(Paint.Cap.ROUND);
        drawPaint.setStrokeWidth(drawSize);

        // 初始化橡皮擦的画笔
        eraserPaint = new Paint();
        eraserPaint.setAntiAlias(true);
        eraserPaint.setColor(Color.BLACK);
        eraserPaint.setStyle(Paint.Style.STROKE);
        eraserPaint.setStrokeCap(Paint.Cap.ROUND);
        eraserPaint.setStrokeWidth(eraserSize);
        eraserPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));

    }

    /** 点评图的宽度 */
    protected int imgWidth = 0;
    /** 点评图的高度 */
    protected int imgHeight = 0;

    /**
     * 初始化操作
     */
    private void init(){
        if(mBaseMatrix != null) mBaseMatrix.reset();
        if(pathCanvas != null)pathCanvas = null;
        if (imgBitmap == null) return;

        imgWidth = imgBitmap.getWidth();
        imgHeight = imgBitmap.getHeight();

        // 如果是加载新的图片,将之前的图片recycle
        if(coverBitmap != null){
            if(!coverBitmap.isRecycled())coverBitmap.recycle();
            coverBitmap = null;
        }

        // 创建一个和点评图片相同大小的Bitmap
        coverBitmap = Bitmap.createBitmap(imgWidth, imgHeight, Bitmap.Config.ARGB_4444);

        // 初始化画布,以创建的空Bitmap为底
        pathCanvas = new Canvas(coverBitmap);
        // 先用透明色填充整个Bitmap
        pathCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);

        // 计算图片根据当前屏幕尺寸进行缩放居中所需要的矩阵变换值
        calculateMatrix();
    }

    /**
     * 图片的大小是不确定的,这里所有的图片都按照屏幕宽高进行自动缩放、居中显示
     * 计算规则:
     *  宽度比例 = 屏幕宽度 / 图片宽度
     *  如果: 宽度比例 * 图片高度 > 屏幕高度, 按照屏幕高度在宽度比例基础上再次缩放
     *  否则: 以宽度比例作为拉伸比例(matrix.postScale(scale, scale);)
     *
     *  根据拉伸后的尺寸计算图片居中的位移值(matrix.postTranslate)
     */
    private void calculateMatrix(){
        if (imgWidth<=viewWidth && imgHeight <=viewHeight){
            mBaseMatrix.postTranslate(0, (viewHeight - imgHeight)/2);
            mBaseMatrix.postTranslate((viewWidth - imgWidth)/2 ,0);
        }
        else {
            float widthScale = (float) viewWidth/ (float) imgWidth;
            float scale = widthScale;
            if(widthScale * imgHeight > viewHeight){
                float tempScale = viewHeight / (widthScale * imgHeight);
                scale *= tempScale;
            }
            mBaseMatrix.postScale(scale, scale);
            if(scale == widthScale){
                mBaseMatrix.postTranslate(0, (viewHeight - scale*imgHeight)/2);
            }else{
                mBaseMatrix.postTranslate((viewWidth - scale*imgWidth)/2 ,0);
            }
        }

        // 矩阵改变了
        reCalculate();

        // 通知View进行重绘
        postInvalidate();
    }

    /**
     * 进行一些必要值的重新计算
     */
    protected void reCalculate(){
        getDrawMatrix().getValues(mMatrixValues);
        float scale_x = mMatrixValues[Matrix.MSCALE_X];
        paint.setStrokeWidth(eraserSize * scale_x);
        eraserPaint.setStrokeWidth(eraserSize);
    }

    /**
     * 设置底图 Bitmap
     * @param src
     */
    public void setBitmap(Bitmap src){
        imgBitmap = src;
        init();
    }

    /**
     * 设置底图 图片存储路径
     * @param imageFile
     */
    public void setImage(String imageFile){
        imgBitmap = BitmapFactory.decodeFile(imageFile);
        init();
    }

    /**
     * 撤销上一步的操作
     */
    public void undo(){
        if(records != null && records.size() > 0){
            PathRecord pathRecord = records.remove(records.size()-1);
            historys.add(pathRecord);
            reDrawBitmap();
        }
    }

    /**
     * 获取当前画笔的大小
     * @return
     */
    public int getDrawSize() {
        return drawSize;
    }

    /**
     * 设置画笔的大小
     * @param drawSize
     */
    public void setDrawSize(int drawSize) {
        this.drawSize = drawSize;
        drawPaint.setStrokeWidth(drawSize);
    }

    /**
     * 获取橡皮擦大小
     * @return
     */
    public int getEraserSize() {
        return eraserSize;
    }

    /**
     * 设置橡皮擦的大小
     * @param eraserSize
     */
    public void setEraserSize(int eraserSize) {
        this.eraserSize = eraserSize;
        reCalculate();
    }

    /**
     * 重做上一步的操作
     */
    public void redo(){
        if(historys != null && historys.size() > 0){
            PathRecord pathRecord = historys.remove(historys.size()-1);
            records.add(pathRecord);

            reDrawBitmap();
        }
    }

    /**
     * 对点评的红线进行undo(撤销)、redo(重做)后,点评的Bitmap需要重新绘制
     * 这里先清空pathCanvas,然后遍历所有绘制记录生成新的点评
     */
    private void reDrawBitmap(){
        // 清空Bitmap所有内容
        pathCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
        // 重新绘制所有点评的红线
        if(records != null && records.size() > 0){
            for(PathRecord record:records){
                if(record == null)continue;
                if(record.mode == MODE_PAINT){
                    drawPaint.setStrokeWidth(record.paintSize);
                    pathCanvas.drawPath(record.record, drawPaint);
                }else if(record.mode == MODE_ERASER){
                    eraserPaint.setStrokeWidth(record.paintSize);
                    pathCanvas.drawPath(record.record, eraserPaint);
                }
            }
            // 恢复原值
            drawPaint.setStrokeWidth(drawSize);
            eraserPaint.setStrokeWidth(eraserSize);
        }
        // 点评绘制完成,通知View进行重绘
        postInvalidate();
    }

    /** 绘制类型标识 */
    private int mode = 0;
    /** 画笔 */
    public static int MODE_PAINT = 0;
    /** 橡皮擦 */
    public static int MODE_ERASER = 1;

    /**
     * 画笔是否可用
     */
    private boolean mPaintEnable;
    public void setPaintEnable(boolean enable){
        this.mPaintEnable = enable;
    }

    /**
     * 设置绘制类型
     * @param mode  0:画笔    1:橡皮擦
     */
    public void setDrawMode(int mode){
        this.mode = mode;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mPaintEnable){
            // 评画红线模式
            if(mode == MODE_PAINT){
                handlePaint(event);
            }
            // 橡皮擦模式
            else if(mode == MODE_ERASER){
                handleEraser(event);
            }
        }
        else {
            return super.onTouchEvent(event);
        }

        return true;
    }

    private Path drawPath = null;
    private PointF vPrev = new PointF();

    /**
     * 根据手势绘制评画的红线
     * @param event 触摸事件
     */
    private void handlePaint(MotionEvent event){
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                if(drawPath == null)drawPath = new Path();
                vPrev.set(event.getX(), event.getY());
                vPrev = getRealPoint(vPrev);
                drawPath.moveTo(vPrev.x, vPrev.y);
                postInvalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                PointF vPoint = new PointF(event.getX(), event.getY());
                vPoint = getRealPoint(vPoint);
                drawPath.quadTo(vPrev.x, vPrev.y, (vPoint.x + vPrev.x) / 2, (vPoint.y + vPrev.y) / 2);
                vPrev = vPoint;
                postInvalidate();
                break;
            case MotionEvent.ACTION_UP:
                PathRecord record = new PathRecord();
                record.mode = mode;
                record.record = drawPath;
                record.paintSize = drawSize;
                records.add(record);
                drawPath = null;
                break;
        }
    }

    private Path eraserPath = null;
    private Path tempEraserPath = null;
    private PointF ePrev = new PointF();
    private PointF tempEPrev = new PointF();

    /**
     * 根据手势绘制橡皮擦路径
     * @param event 触摸事件
     */
    private void handleEraser(MotionEvent event){

        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:

                if(eraserPath == null) eraserPath = new Path();
                if(tempEraserPath == null) tempEraserPath = new Path();
                ePrev.set(event.getX(), event.getY());
                tempEPrev.set(event.getX(), event.getY());
                tempEraserPath.moveTo(tempEPrev.x, tempEPrev.y);
                ePrev = getRealPoint(ePrev);
                eraserPath.moveTo(ePrev.x, ePrev.y);

                postInvalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                PointF vPoint = new PointF(event.getX(), event.getY());
                tempEraserPath.quadTo(tempEPrev.x, tempEPrev.y, (vPoint.x + tempEPrev.x) / 2, (vPoint.y + tempEPrev.y) / 2);
                tempEPrev = vPoint;
                vPoint = getRealPoint(vPoint);
                eraserPath.quadTo(ePrev.x, ePrev.y, (vPoint.x + ePrev.x) / 2, (vPoint.y + ePrev.y) / 2);
                ePrev = vPoint;

                postInvalidate();
                break;
            case MotionEvent.ACTION_UP:
                PathRecord record = new PathRecord();
                record.mode = mode;
                record.record = eraserPath;
                record.paintSize = eraserSize;
                records.add(record);

                eraserPath = null;
                tempEraserPath = null;

                postInvalidate();
                break;
        }
    }

    /**
     * 获取最终显示时所使用的Matrix
     * mDrawMatrix = mBaseMatrix + mSuppMatrix;
     * @return mDrawMatrix
     */
    protected Matrix getDrawMatrix() {
        mDrawMatrix.set(mBaseMatrix);
        mDrawMatrix.postConcat(mSuppMatrix);
        return mDrawMatrix;
    }

    /**
     * 坐标转换
     *  Bitmap经过Matrix转换后会出现拉伸、位移,如果按照手指触摸位置绘制红线会出现对应不准的情况
     * @param pointF 原始点
     * @return 转换后的点
     */
    protected PointF getRealPoint(PointF pointF){
        float[] values = new float[9];
        // 取出matrix变换后的拉伸比例、位移量等值
        getDrawMatrix().getValues(values);
        float[] result = new float[2];
        float scale_x = values[Matrix.MSCALE_X];
        float scale_y = values[Matrix.MSCALE_Y];
        float trans_x = values[Matrix.MTRANS_X];
        float trans_y = values[Matrix.MTRANS_Y];
        // 根据拉伸比例、位移量计算出真是值
        result[0] = (pointF.x - trans_x) / scale_x ;
        result[1] = (pointF.y - trans_y) / scale_y ;

        return new PointF(result[0], result[1]);

    }

    /**
     * 获取最初的拉伸比例
     * @return
     */
    public float getBasicScale() {
        return (float) Math.sqrt((float) Math.pow(getValue(mBaseMatrix, Matrix.MSCALE_X), 2) + (float) Math.pow(getValue(mBaseMatrix, Matrix.MSKEW_Y), 2));
    }

    /**
     * 获取当前的拉伸比例
     * @return
     */
    public float getScale() {
        return (float) Math.sqrt((float) Math.pow(getValue(getDrawMatrix(), Matrix.MSCALE_X), 2) + (float) Math.pow(getValue(getDrawMatrix(), Matrix.MSKEW_Y), 2));
    }

    /**
     * 获取Matrix中的Value值
     * @param matrix
     * @param whichValue
     * @return
     */
    private float getValue(Matrix matrix, int whichValue) {
        matrix.getValues(mMatrixValues);
        return mMatrixValues[whichValue];
    }

    /**
     * 对当前Bitmap进行缩放、平移操作时,不停地对边界进行检测修正并刷新View
     */
    protected void checkAndDisplayMatrix() {
        reCalculate();
        if (checkMatrixBounds()) {
            postInvalidate();
        }
    }

    /**
     * 当对Bitmap进行缩放、平移时,不断检测Bitmap边界,并进行微调
     * 使Bitmap小于View大小时处于View中心位置
     * @return
     */
    private boolean checkMatrixBounds() {

        final RectF rect = getDisplayRect(getDrawMatrix());
        if (null == rect) {
            return false;
        }

        final float height = rect.height(), width = rect.width();
        float deltaX = 0, deltaY = 0;

        final int viewHeight = getViewHeight();
        if (height <= viewHeight) {
            deltaY = (viewHeight - height) / 2 - rect.top;
        } else if (rect.top > 0) {
            deltaY = -rect.top;
        } else if (rect.bottom < viewHeight) {
            deltaY = viewHeight - rect.bottom;
        }

        final int viewWidth = getViewWidth();
        if (width <= viewWidth) {
            deltaX = (viewWidth - width) / 2 - rect.left;
        } else if (rect.left > 0) {
            deltaX = -rect.left;
        } else if (rect.right < viewWidth) {
            deltaX = viewWidth - rect.right;
        }
        // Finally actually translate the matrix
        mSuppMatrix.postTranslate(deltaX, deltaY);
        return true;
    }

    /**
     * 根据Matrix获取Bitmap显示的区域Rect
     * @param matrix
     * @return
     */
    protected RectF getDisplayRect(Matrix matrix) {
        mDisplayRect.set(0, 0, imgWidth,imgHeight);
        matrix.mapRect(mDisplayRect);
        return mDisplayRect;
    }

    /**
     * 获取View的宽(减去Padding)
     * @return
     */
    protected int getViewWidth() {
        return getWidth() - getPaddingLeft() - getPaddingRight();
    }

    /**
     * 获取View的高(减去Padding)
     * @return
     */
    protected int getViewHeight() {
        return getHeight() - getPaddingTop() - getPaddingBottom();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // 先绘制点评的图片作为背景
        if(imgBitmap!=null){
            canvas.drawBitmap(imgBitmap, getDrawMatrix(), paint);
        }

        // 绘制点评的红线和橡皮擦Path
        if(coverBitmap != null){
            // 在新创建的Bitmap上绘制红线
            if (drawPath != null){
                pathCanvas.drawPath(drawPath, drawPaint);
            }
            // 在新创建的Bitmap上绘制橡皮擦Path
            // eraserPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));
            // 被eraserPath覆盖的drawPath会被擦除
            if (eraserPath != null) {
                pathCanvas.drawPath(eraserPath, eraserPaint);
            }
            // 将绘制好的点评Bitmap绘制到View上
            canvas.drawBitmap(coverBitmap, getDrawMatrix(), paint);

            // 绘制橡皮擦Path时,透明色不直观,这里使用黑色绘制Path路径,被黑色覆盖即为擦除,当手指松开时,此黑色Path消失
            if (eraserPath != null) {
                canvas.drawPath(tempEraserPath, paint);
            }
        }
    }

    @Override
    public void onGlobalLayout() {
        // 取View宽高放入全局变量中
        viewWidth = getWidth();
        viewHeight = getHeight();
        init();
        // View尺寸取到后移除监听器
        if(viewWidth > 0 && viewHeight > 0){
            ViewTreeObserver observer = getViewTreeObserver();
            if(observer != null){
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN){
                    observer.removeOnGlobalLayoutListener(this);
                }else {
                    observer.removeGlobalOnLayoutListener(this);
                }
            }
        }
    }

    /**
     * 将Bitmap保存成文件
     * @return 是否保存成功
     */
    public boolean save(){
        Bitmap temp = Bitmap.createBitmap(imgWidth, imgHeight, Bitmap.Config.ARGB_8888);
        Canvas tempCanvas = new Canvas(temp);
        tempCanvas.drawBitmap(imgBitmap, 0, 0, paint);
        tempCanvas.drawBitmap(coverBitmap, 0, 0, paint);

        try {
            File myCaptureFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath() +"/hubin_"+ System.currentTimeMillis()+".jpeg");
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(myCaptureFile));
            temp.compress(Bitmap.CompressFormat.JPEG, 80, bos);
            bos.flush();
            bos.close();
        } catch (Exception e){
            return false;
        }
        return true;
    }

}

/** 绘制Path的记录 */
class PathRecord {
    /** 类型:0-画笔 1:橡皮擦 */
    int mode = 0;
    /** 绘制时画笔size */
    int paintSize = 0;
    /** 绘制的路径 */
    Path record;
}