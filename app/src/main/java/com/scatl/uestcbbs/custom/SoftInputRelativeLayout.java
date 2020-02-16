package com.scatl.uestcbbs.custom;

import android.content.Context;
import android.graphics.Rect;
import android.os.Build;
import android.util.AttributeSet;
import android.view.WindowInsets;
import android.widget.RelativeLayout;

/**
 * author: sca_tl
 * description: 去除fitsystemwindow=true后的paddingTop
 * date: 2020/1/29 18:17
 */
public class SoftInputRelativeLayout extends RelativeLayout {
    public SoftInputRelativeLayout(Context context) {
        super(context);
    }

    public SoftInputRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SoftInputRelativeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public SoftInputRelativeLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected boolean fitSystemWindows(Rect insets) {
        insets.left = 0;
        insets.top = 0;
        insets.right = 0;
        return super.fitSystemWindows(insets);
    }

    @Override
    public WindowInsets onApplyWindowInsets(WindowInsets insets) {
        return super.onApplyWindowInsets(insets.replaceSystemWindowInsets(0, 0, 0, insets.getSystemWindowInsetBottom()));
    }

}
