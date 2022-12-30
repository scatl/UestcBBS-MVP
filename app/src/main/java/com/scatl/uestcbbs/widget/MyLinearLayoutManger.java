package com.scatl.uestcbbs.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * author: sca_tl
 * description: 实现准确的平滑滚动
 * date: 2019/7/27 15:21
 */
public class MyLinearLayoutManger extends LinearLayoutManager {
    public MyLinearLayoutManger(Context context) {
        super(context);
    }

    public MyLinearLayoutManger(Context context, int orientation, boolean reverseLayout) {
        super(context, orientation, reverseLayout);
    }

    public MyLinearLayoutManger(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public void smoothScrollToPosition(RecyclerView recyclerView, RecyclerView.State state, int position) {
        LinearSmoothScroll linearSmoothScroll = new LinearSmoothScroll(recyclerView.getContext()) {
            @Override
            protected float calculateSpeedPerPixel(DisplayMetrics displayMetrics) {
                //return super.calculateSpeedPerPixel(displayMetrics);
                return 15f / displayMetrics.densityDpi;
            }
        };
        linearSmoothScroll.setTargetPosition(position);
        startSmoothScroll(linearSmoothScroll);
    }
}
