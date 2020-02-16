package com.scatl.uestcbbs.custom.imageview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Path;
import android.util.AttributeSet;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;

import com.scatl.uestcbbs.R;


public class ArcImageView extends AppCompatImageView {

    private Path path;
    private int arcHeight;

    public ArcImageView(Context context) {
        super(context);
        init();
    }

    public ArcImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();

        TypedArray typedValue = context.obtainStyledAttributes(attrs, R.styleable.ArcImageView);
        arcHeight = typedValue.getInt(R.styleable.ArcImageView_arcHeight, 50);
        typedValue.recycle();
    }

    public ArcImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray typedValue = context.obtainStyledAttributes(attrs, R.styleable.ArcImageView);
        arcHeight = typedValue.getInt(R.styleable.ArcImageView_arcHeight, 50);
        typedValue.recycle();
    }

    private void init() {
        path = new Path();
    }

    @Override
    protected void onDraw(Canvas canvas) {

        //path.moveTo(0, 0);
        //path.lineTo(0, getHeight());
        //path.quadTo(getWidth() / 2, getHeight()-50, getWidth(), getHeight());
        //path.lineTo(getWidth(), 0);


        path.moveTo(0, getHeight() - arcHeight);
        path.quadTo(getWidth() / 2, getHeight(), getWidth(), getHeight() - arcHeight);
        path.lineTo(getWidth(), getHeight() - arcHeight);
        path.lineTo(getWidth(), 0);
        path.lineTo(0, 0);
        path.close();
        canvas.clipPath(path);
        //canvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.FILTER_BITMAP_FLAG|Paint.ANTI_ALIAS_FLAG));
        super.onDraw(canvas);

    }

}
