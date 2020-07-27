package com.cyy.flowlayout;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * @author chenyy
 * @date 2020/7/27
 */

public class FlowLayout extends ViewGroup {
    private static final String TAG = FlowLayout.class.getSimpleName();
    private List<List<View>> childViews = new ArrayList<>();
    private List<View> lineViews = new ArrayList<>();
    private List<Integer> lineHeights = new ArrayList<>();

    public FlowLayout(Context context) {
        this(context, null);
    }

    public FlowLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FlowLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * layout宽度固定，高度计算
     * @param widthMeasureSpec
     * @param heightMeasureSpec
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        childViews.clear();
        lineViews.clear();
        lineHeights.clear();

        int sizeWidth = getMeasuredWidth();
        int sizeHeight = MeasureSpec.getSize(heightMeasureSpec);
        int modeHeight = MeasureSpec.getMode(heightMeasureSpec);

        int lineWidth = 0;
        int lineHeight = 0;
        int height = 0;

        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++){
            View child = getChildAt(i);
            if (child.getVisibility() == GONE){
                continue;
            }
            measureChild(child, widthMeasureSpec, heightMeasureSpec);
            MarginLayoutParams lp = (MarginLayoutParams) child.getLayoutParams();
            int childWidth = child.getMeasuredWidth() + lp.leftMargin + lp.rightMargin;
            int childHeight = child.getMeasuredHeight() + lp.topMargin + lp.bottomMargin;

            //判断是否需要换行
            if (lineWidth + childWidth > sizeWidth - getPaddingLeft() - getPaddingRight()){
                //对上一行进行view和高度处理
                childViews.add(lineViews);
                lineHeights.add(lineHeight);
                height += lineHeight;
                //重置
                lineWidth = 0;
                lineHeight = 0;
                lineViews = new ArrayList<>();
            } else {
                lineViews.add(child);
                lineWidth += childWidth;
                lineHeight = Math.max(lineHeight, childHeight);
            }

            //最后一行高度累加
            if (i == childCount - 1){
                childViews.add(lineViews);
                lineHeights.add(lineHeight);
                height += lineHeight;
            }
        }
        //判断高度的测量模式
        if (modeHeight == MeasureSpec.EXACTLY){
            height = sizeHeight;
        } else if (modeHeight == MeasureSpec.AT_MOST){
            height += getPaddingTop() + getPaddingBottom();
        } else if (modeHeight == MeasureSpec.UNSPECIFIED){
            height += getPaddingTop() + getPaddingBottom();
        }
        Log.i(TAG, "height: " + height);
        setMeasuredDimension(sizeWidth, height);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        //从左上角依次layout，需要知道每一行的View、总的View以及每一行的行高
        int top = getPaddingTop();
        int left;

        for (int i = 0; i < lineHeights.size(); i++){
            List<View> viewList = childViews.get(i);
            left = getPaddingLeft();
            for (int j = 0; j < viewList.size(); j++){
                View child = viewList.get(j);
                MarginLayoutParams lp = (MarginLayoutParams) child.getLayoutParams();
                int childLeft = left + lp.leftMargin;
                int childTop = top + lp.topMargin;
                int childRight = childLeft + child.getMeasuredWidth();
                int childBottom = childTop + child.getMeasuredHeight();
                child.layout(childLeft, childTop, childRight, childBottom);
                left += child.getMeasuredWidth() + lp.leftMargin + lp.rightMargin;
            }
            top += lineHeights.get(i);
        }
    }

    @Override
    protected LayoutParams generateDefaultLayoutParams() {
        return new MarginLayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
    }

    @Override
    protected LayoutParams generateLayoutParams(LayoutParams p) {
        return new MarginLayoutParams(p);
    }

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new MarginLayoutParams(getContext(), attrs);
    }

    @Override
    protected boolean checkLayoutParams(LayoutParams p) {
        return p instanceof MarginLayoutParams;
    }
}
