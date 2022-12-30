package com.scatl.uestcbbs.widget;

import android.content.Context;

import androidx.recyclerview.widget.LinearSmoothScroller;

/**
 * author: sca_tl
 * description: from：https://www.jianshu.com/p/bde672af4e11
 * date: 2019/07/21 19:24
 */
public class LinearSmoothScroll extends LinearSmoothScroller {
    public LinearSmoothScroll(Context context) {
        super(context);
    }

    @Override
    protected int getVerticalSnapPreference() {
        return SNAP_TO_START;
    }
}
