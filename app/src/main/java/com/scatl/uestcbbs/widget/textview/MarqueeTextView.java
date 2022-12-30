package com.scatl.uestcbbs.widget.textview;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;

public class MarqueeTextView extends AppCompatTextView {
    public MarqueeTextView(Context context) {
        super(context);
        init(context);
    }

    public MarqueeTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public MarqueeTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context){
        setEllipsize(TextUtils.TruncateAt.MARQUEE);//设置横向滚动效果
        setSingleLine(true);//设置单行显示
        setMarqueeRepeatLimit(-1);//设置滚动次数无限次
    }

    @Override
    public boolean isFocused() {//这个函数是关键 重写它 返回true
        return true;
    }
}
