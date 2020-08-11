package com.cyy.myapplication;

import android.graphics.Rect;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * @author chenyy
 * @date 2020/4/15
 */

public class GradeItemDecoration extends RecyclerView.ItemDecoration {
    /**
     * 水平间距
     */
    private int horizontalSpacing;

    /**
     * 垂直间距
     */
    private int verticalSpacing;

    public GradeItemDecoration(int horizontalSpacing, int verticalSpacing) {
        this.horizontalSpacing = horizontalSpacing;
        this.verticalSpacing = verticalSpacing;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        GridLayoutManager manager = (GridLayoutManager) parent.getLayoutManager();
        int span = manager.getSpanCount();
        int childPosition = parent.getChildAdapterPosition(view);
        int itemCount = parent.getAdapter().getItemCount();
        int orientation = manager.getOrientation();
        setGridOffset(orientation, span, outRect, childPosition, itemCount);
    }

    private void setGridOffset(int orientation, int spanCount, Rect outRect, int childPosition, int itemCount) {
        // 总共的padding值
        float totalSpace = horizontalSpacing * (spanCount - 1);
        // 分配给每个item的padding值,左边距加右边距
        float eachSpace = totalSpace / spanCount;
        // 列数
        int column = childPosition % spanCount;
        // 行数
        int row = childPosition / spanCount + 1;
        float left = 0.0f;
        float right = 0.0f;
        float top = 0.0f;
        float bottom = 0.0f;
        if (orientation == GridLayoutManager.VERTICAL) {
            // 默认 top为0
            top = 0;
            // 默认bottom为间距值
            bottom = verticalSpacing;
            left = column * eachSpace / (spanCount - 1);
            right = eachSpace - left;
            // 无边距的话  只有最后一行bottom为0
            if (itemCount / spanCount == row) {
                bottom = 0;
            }
        }
        outRect.set((int) left, (int) top, (int) right, (int) bottom);
    }
}
