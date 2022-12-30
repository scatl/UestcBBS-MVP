package com.scatl.uestcbbs.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;

/**
 * author: sca_tl
 * description:
 * date: 2019/07/21 18:37
 */
public class CustomGridView extends GridView {

    public CustomGridView(Context context) {
        super(context);
    }

    public CustomGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomGridView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec + 30);
    }

}
