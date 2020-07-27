package com.cyy.autotextview;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.TextView;

/**
 * @author chenyy
 * @date 2020/7/21
 */

public class AutoTextView extends TextView {
    private static final String TAG = AutoTextView.class.getSimpleName();
    private TextPaint textPaint;
    private Rect rect;
    private float textSize;
    private int granularity = 2;

    public AutoTextView(Context context) {
        this(context, null);
    }

    public AutoTextView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AutoTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        textPaint = new TextPaint();
        rect = new Rect();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        Log.i(TAG, "onMeasure");
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //textPaint.getTextBounds(getText().toString(), 0, getText().length(), rect);
        textSize = getTextSize();
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        Log.i(TAG, "onLayout");
        super.onLayout(changed, left, top, right, bottom);
        Log.i(TAG, "textSize: " + textSize + " textWidth: " + rect.width() + " width: " + getWidth());
        textPaint.measureText(getText(), 0, getText().length());
        if (rect.width() > getWidth()){
            findSmallerTextSize();
        } else if (rect.width() < getWidth()){
            findLargerTextSize();
        }
    }

    private void findLargerTextSize() {
        Log.i(TAG, "findLargerTextSize");
        while (rect.width() < getWidth()){
            textSize += granularity;
            setTextSize(textSize);
        }
    }

    private void findSmallerTextSize() {
        Log.i(TAG, "findSmallerTextSize");
        while (rect.width() > getWidth()){
            textSize -= granularity;
            setTextSize(textSize);
        }
    }
}
