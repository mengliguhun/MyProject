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

    private Paint mTextPaint;
    private int mTextColor = Color.BLACK;
    private Paint mBorderPaint;
    private int mBorderColor = Color.RED;
    private float mBorderWidth = 2;
    private Paint mDividerPaint;
    private float mDividerHeight = 2;
    private int mDividerColor = Color.WHITE;
    private float mBorderRadius = 45;
    private float mTextWidth;
    private float mTextHeight;
    private float mTextSize = 16;
    private int mNum = 4;

    private EditText mInputView;
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

            invalidate();

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

    public float getDividerHeight() {
        return mDividerHeight;
    }

    public void setDividerHeight(float mDividerHeight) {
        this.mDividerHeight = mDividerHeight;
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
        mDividerColor = a.getColor(
                R.styleable.PasswordInputView_borderColor,
                mDividerColor);
        // Use getDimensionPixelSize or getDimensionPixelOffset when dealing with
        // values that should fall on pixel boundaries.
        mDividerHeight = a.getDimension(
                R.styleable.PasswordInputView_dividerHeight,
                mDividerHeight);
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
        super.onDraw(canvas);
        int width = getWidth();
        int height = getHeight();

        //外边框
        RectF rect = new RectF(0,0,width,height);
        mBorderPaint.setStrokeWidth(mBorderWidth);
        canvas.drawRoundRect(rect,mBorderRadius,mBorderRadius,mBorderPaint);
        // 分割线
        for (int i = 1; i < mNum; i++) {
            canvas.drawLine(width/mNum*i,0+mBorderWidth,width/mNum*i+mDividerHeight-mBorderWidth,height,mDividerPaint);
        }
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
