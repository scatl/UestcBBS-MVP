package com.scatl.uestcbbs.widget.imageview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatImageView;

import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.scatl.uestcbbs.R;
import com.scatl.uestcbbs.util.ImageUtil;

public class RoundImageView extends AppCompatImageView {

    private float width, height;

    private String absolutePath;

    private Path path;

    private int cornerRadius;

    public RoundImageView(Context context) {
        this(context, null);
        init();
    }

    public RoundImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        init();
        TypedArray typedValue = context.obtainStyledAttributes(attrs, R.styleable.RoundImageView);
        cornerRadius = typedValue.getInt(R.styleable.RoundImageView_cornerAngel, 20);
        typedValue.recycle();
    }

    public RoundImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
        TypedArray typedValue = context.obtainStyledAttributes(attrs, R.styleable.RoundImageView);
        cornerRadius = typedValue.getInt(R.styleable.RoundImageView_cornerAngel, 20);
        typedValue.recycle();
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        width = getWidth();
        height = getHeight();
    }

    private void init() {
        path = new Path();
    }

    @Override
    protected void onDraw(Canvas canvas) {

        if (width >= 100 && height > 30) {

            //四个圆角
            path.moveTo(cornerRadius, 0);
            path.lineTo(width - cornerRadius, 0);
            path.quadTo(width, 0, width, cornerRadius);
            path.lineTo(width, height - cornerRadius);
            path.quadTo(width, height, width - cornerRadius, height);
            path.lineTo(cornerRadius, height);
            path.quadTo(0, height, 0, height - cornerRadius);
            path.lineTo(0, cornerRadius);
            path.quadTo(0, 0, cornerRadius, 0);

            if (getDrawable() instanceof GifDrawable) {
                setScaleType(ScaleType.CENTER_CROP);
            } else {
                setScaleType(ScaleType.MATRIX);
            }

            canvas.clipPath(path);
        }

        try {

            super.onDraw(canvas);

        } catch (RuntimeException e) {

            Drawable drawable = getDrawable();

            if (drawable != null) {
                Bitmap bitmap = ImageUtil.drawable2Bitmap(drawable);
                if (bitmap.getHeight() / bitmap.getWidth() >= 5) {

                    bitmap.setHeight(bitmap.getHeight() / 5);

                    setScaleType(ScaleType.CENTER_CROP);
                    setImageBitmap(bitmap);
                } else {
                    //FIXME
                    Bitmap new_bitmap = Bitmap.createScaledBitmap(bitmap, bitmap.getWidth() / 2, bitmap.getHeight() / 2, true);
                    new_bitmap.setHeight(new_bitmap.getHeight());

                    setScaleType(ScaleType.CENTER_CROP);
                    setImageBitmap(new_bitmap);
                }
            }
        }
    }

    public String getAbsolutePath() {
        return absolutePath;
    }

    public void setAbsolutePath(String absolutePath) {
        this.absolutePath = absolutePath;
    }

}
