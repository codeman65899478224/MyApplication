package com.cyy.autotextview;

import android.content.Context;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.widget.TextView;

/**
 * @author chenyy
 * @date 2020/7/21
 */

public class AutoTextView extends TextView {
    private static final String TAG = AutoTextView.class.getSimpleName();
    private TextPaint textPaint;
    private float textSize;
    private int granularity = 2;
    private float textWidth;
    private int height;
    private int lineCount = 1;

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
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        Log.i(TAG, "onMeasure");
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        textSize = getTextSize();
        autoFitText(width);
        setMeasuredDimension(width, getFontHeight());
    }

    private void autoFitText(int width) {
        textPaint.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, textSize, getResources().getDisplayMetrics()));
        Log.i(TAG, "textSize: " + textSize + " textWidth: " + textWidth + " width: " + width);
        //单行最大宽度
        int realWidth = width - getPaddingLeft() - getPaddingRight() - 2;
        int maxLines = getMaxLines();
        StaticLayout layout = new StaticLayout(getText(), textPaint, realWidth, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, true);
        lineCount = layout.getLineCount();
        Log.i(TAG, "lineCount: " + lineCount);
        if (maxLines == 1){
            textWidth = textPaint.measureText(getText(), 0, getText().length());
        } else {
            for (int i = 0; i < lineCount; i++) {
                textWidth = Math.max(textWidth, layout.getLineWidth(i));
            }
        }
        Log.i(TAG, "textWidth: " + textWidth);
        if (lineCount > maxLines){
            textSize = findSmallerTextSizeForLines(realWidth, maxLines);
        } else if (lineCount < maxLines){
            textSize = findLargerTextSizeForLines(realWidth, maxLines);
        } else {
            if (textWidth > realWidth){
                textSize = findSmallerTextSize(realWidth, maxLines);
            } else if (textWidth < realWidth){
                textSize = findLargerTextSize(realWidth, maxLines);
            }
        }
        setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
        //height = getFontHeight() * lineCount;
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        Log.i(TAG, "onLayout");
        super.onLayout(changed, left, top, right, bottom);

    }

    private float findLargerTextSize(int width, int maxLines) {
        Log.i(TAG, "findLargerTextSize");
        while (textWidth < width){
            textSize += granularity;
            textPaint.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, textSize, getResources().getDisplayMetrics()));
            StaticLayout layout = new StaticLayout(getText(), textPaint, width, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, true);
            lineCount = layout.getLineCount();
            if (maxLines == 1){
                textWidth = textPaint.measureText(getText(), 0, getText().length());
            } else {
                for (int i = 0; i < lineCount; i++) {
                    textWidth = Math.max(textWidth, layout.getLineWidth(i));
                }
            }
            Log.i(TAG, "findLarger" + " textWidth: " + textWidth + " lineCount: " + lineCount);
        }
        return textSize;
    }

    private float findLargerTextSizeForLines(int width, int maxLines){
        while (lineCount < maxLines){
            textSize += granularity;
            textPaint.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, textSize, getResources().getDisplayMetrics()));
            StaticLayout layout = new StaticLayout(getText(), textPaint, width, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, true);
            lineCount = layout.getLineCount();
            Log.i(TAG, "findLargerTextSizeForLines" + " lineCount: " + lineCount);
        }
        return textSize;
    }

    private float findSmallerTextSizeForLines(int width, int maxLines){
        while (lineCount > maxLines){
            textSize -= granularity;
            textPaint.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, textSize, getResources().getDisplayMetrics()));
            StaticLayout layout = new StaticLayout(getText(), textPaint, width, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, true);
            lineCount = layout.getLineCount();
            Log.i(TAG, "findSmallerTextSizeForLines" + " lineCount: " + lineCount);
        }
        return textSize;
    }

    private float findSmallerTextSize(int width, int maxLines) {
        Log.i(TAG, "findSmallerTextSize");
        while (textWidth > width){
            textSize -= granularity;
            textPaint.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, textSize, getResources().getDisplayMetrics()));
            StaticLayout layout = new StaticLayout(getText(), textPaint, width, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, true);
            lineCount = layout.getLineCount();
            if (maxLines == 1){
                textWidth = textPaint.measureText(getText(), 0, getText().length());
            } else {
                for (int i = 0; i < lineCount; i++) {
                    textWidth = Math.max(textWidth, layout.getLineWidth(i));
                }
            }
            Log.i(TAG, "findSmaller" + " textWidth: " + textWidth + " lineCount: " + lineCount);
        }
        return textSize;
    }

    public int getFontHeight() {
        Paint.FontMetrics fm = textPaint.getFontMetrics();
        return (int) Math.ceil(fm.descent - fm.top) + 2;
    }
}
