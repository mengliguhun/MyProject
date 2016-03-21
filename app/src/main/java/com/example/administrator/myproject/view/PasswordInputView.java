package com.example.administrator.myproject.view;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.example.administrator.myproject.R;

/**
 * TODO: document your custom view class.
 */
public class PasswordInputView extends View {

    private Paint mTextPaint;//绘制Text输入内容的画笔
    private int mTextColor = Color.BLACK;//字体颜色
    private Paint mBorderPaint;//边框背景颜色
    private int mBorderColor = Color.BLUE;
    private float mBorderWidth = 4;//边框大小
    private int mBackgroundColor = Color.WHITE;//背景色
    private Paint mDividerPaint;//分割线
    private float mDividerWidth = 2;//分割线宽度
    private int mDividerColor = Color.BLUE;//分割线颜色
    private float mBorderRadius = 45;//绘制圆角角度
    private float mTextWidth;
    private float mTextHeight;
    private float mTextSize = 16;
    private int mNum = 4;//输入内容长度


    private EditText mInputView;//设置EditText用于获取焦点弹出键盘
    public EditText getInputView() {
        return mInputView;
    }

    public void setInputView(EditText mInputView) {
        if (mInputView == null){
            return;
        }
        this.mInputView = mInputView;
        mInputView.setFilters(new InputFilter[]{new InputFilter.LengthFilter(mNum)});
        mInputView.addTextChangedListener(textWatcher);
    }
    //谈键盘
    private void forceInputViewGetFocus() {
        if (mInputView == null){
            return;
        }
        mInputView.setFocusable(true);
        mInputView.setFocusableInTouchMode(true);
        mInputView.requestFocus();
        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(mInputView, InputMethodManager.SHOW_IMPLICIT);
    }
    private OnClickListener mOnClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            forceInputViewGetFocus();
        }
    };
    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

            invalidate();//内容发生改变时重新绘制

        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    public PasswordInputView(Context context) {
        super(context);
        init(null, 0);
    }

    public PasswordInputView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public PasswordInputView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    public int getTextColor() {
        return mTextColor;
    }

    public void setTextColor(int mTextColor) {
        this.mTextColor = mTextColor;
        invalidate();
    }

    public void setBackgroundColor(int mBackgroundColor) {
        this.mBackgroundColor = mBackgroundColor;
        invalidate();
    }

    public int getBackgroundColor() {
        return mBackgroundColor;
    }

    public int getBorderColor() {
        return mBorderColor;
    }

    public void setBorderColor(int mBorderColor) {
        this.mBorderColor = mBorderColor;
        mBorderPaint.setColor(mBorderColor);
        invalidate();
    }

    public float getBorderWidth() {
        return mBorderWidth;
    }

    public void setBorderWidth(float mBorderWidth) {
        this.mBorderWidth = mBorderWidth;
        invalidate();
    }

    public float getDividerWidth() {
        return mDividerWidth;
    }

    public void setDividerWidth(float mDividerWidth) {
        this.mDividerWidth = mDividerWidth;
        invalidate();
    }

    public int getDividerColor() {
        return mDividerColor;
    }

    public void setDividerColor(int mDividerColor) {
        this.mDividerColor = mDividerColor;
        invalidate();
    }

    public float getBorderRadius() {
        return mBorderRadius;
    }

    public void setBorderRadius(float mBorderRadius) {
        this.mBorderRadius = mBorderRadius;
        invalidate();
    }

    public int getNum() {
        return mNum;
    }

    public void setNum(int mNum) {
        this.mNum = mNum;
        if (mInputView == null){
            return;
        }
        mInputView.setFilters(new InputFilter[]{new InputFilter.LengthFilter(mNum)});
        invalidate();
    }

    private void init(AttributeSet attrs, int defStyle) {
        // Load attributes
        final TypedArray a = getContext().obtainStyledAttributes(
                attrs, R.styleable.PasswordInputView, defStyle, 0);
        mTextColor = a.getColor(
                R.styleable.PasswordInputView_borderColor,
                mTextColor);
        mBorderColor = a.getColor(
                R.styleable.PasswordInputView_borderColor,
                mBorderColor);
        mBackgroundColor = a.getColor(
                R.styleable.PasswordInputView_backGroundColor,
                mBackgroundColor);
        mDividerColor = a.getColor(
                R.styleable.PasswordInputView_borderColor,
                mDividerColor);
        // Use getDimensionPixelSize or getDimensionPixelOffset when dealing with
        // values that should fall on pixel boundaries.
        mDividerWidth = a.getDimension(
                R.styleable.PasswordInputView_dividerW,
                mDividerWidth);
        mBorderWidth = a.getDimension(
                R.styleable.PasswordInputView_borderW,
                mBorderWidth);
        mBorderRadius= a.getDimension(
                R.styleable.PasswordInputView_borderRadius,
                mBorderRadius);

        a.recycle();

        // Set up a default TextPaint object
        mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

        setTextSize(mTextSize);
        mBorderPaint= new Paint();
        mBorderPaint.setColor(mBorderColor);
        mBorderPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        mDividerPaint= new Paint();
        mBorderPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        mDividerPaint.setColor(mDividerColor);

        setOnClickListener(mOnClickListener);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int width = getWidth();
        int height = getHeight();

        RectF rect = new RectF(0,0,width,height);
        //背景
        mBorderPaint.setColor(mBackgroundColor);
        canvas.drawRoundRect(rect, mBorderRadius, mBorderRadius, mBorderPaint);
        //外边框
        mBorderPaint.setStrokeWidth(mBorderWidth);
        mBorderPaint.setColor(mBorderColor);
        mBorderPaint.setStyle(Paint.Style.STROKE);
        canvas.drawRoundRect(rect,mBorderRadius,mBorderRadius,mBorderPaint);

        // 分割线
        mDividerPaint.setStrokeWidth(mDividerWidth);
        for (int i = 1; i < mNum; i++) {
            canvas.drawLine(width/mNum*i,0+mBorderWidth/2,width/mNum*i,height-mBorderWidth/2,mDividerPaint);
        }
        //绘制内容
        if (mInputView !=null){
            if (!TextUtils.isEmpty(mInputView.getText())){
                String text = mInputView.getText().toString();
                // Draw the text.
                for (int i = 0; i < Math.min(text.length(),mNum); i++) {
                    String s= text.substring(i,i+1);
                    mTextWidth = mTextPaint.measureText(s);
                    Paint.FontMetrics fontMetrics = mTextPaint.getFontMetrics();
                    mTextHeight = fontMetrics.bottom;
                    canvas.drawText(s,
                            width/mNum*i+width/mNum/2-mTextWidth/2,
                            height/ 2 +mTextHeight,
                            mTextPaint);
                }

            }
        }

    }

    private void setTextSize(float size) {
        Context c = getContext();
        Resources r;

        if (c == null)
            r = Resources.getSystem();
        else
            r = c.getResources();

        if (size != mTextPaint.getTextSize()) {
            mTextPaint.setTextSize(TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_SP, size, r.getDisplayMetrics()));
            requestLayout();
            invalidate();
        }
    }

}
