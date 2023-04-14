package com.scatl.util;

import android.app.Activity;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


/**
 *https://www.jianshu.com/p/20b960ac2af7
 */
public class TheftProofMark {
    private String mText;
    private int mTextColor;
    private float mTextSize;
    private float mRotation;

    private static TheftProofMark sInstance;

    private TheftProofMark() {
        mText = "";
        mTextColor = 0x01ff0000;
        mTextSize = 16;
        mRotation = -25;
    }

    public static TheftProofMark getInstance() {
        if (sInstance == null) {
            synchronized (TheftProofMark.class) {
                sInstance = new TheftProofMark();
            }
        }
        return sInstance;
    }

    public TheftProofMark setText(String text) {
        mText = text;
        return sInstance;
    }

    public TheftProofMark setTextColor(int color) {
        mTextColor = color;
        return sInstance;
    }

    public TheftProofMark setTextSize(float size) {
        mTextSize = size;
        return sInstance;
    }

    public TheftProofMark setRotation(float degrees) {
        mRotation = degrees;
        return sInstance;
    }

    public void show(Activity activity) {
        show(activity, mText);
    }

    public void show(Activity activity, String text) {
        WatermarkDrawable drawable = new WatermarkDrawable();
        drawable.mText = text;
        drawable.mTextColor = mTextColor;
        drawable.mTextSize = mTextSize;
        drawable.mRotation = mRotation;

        ViewGroup rootView = activity.findViewById(android.R.id.content);
        FrameLayout layout = new FrameLayout(activity);
        layout.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        layout.setBackground(drawable);
        rootView.addView(layout, 1);
    }

    private static class WatermarkDrawable extends Drawable {
        private final Paint mPaint;
        private String mText;
        private int mTextColor;
        private float mTextSize;
        private float mRotation;

        private WatermarkDrawable() {
            mPaint = new Paint();
        }

        @Override
        public void draw(@NonNull Canvas canvas) {
            int width = getBounds().right;
            int height = getBounds().bottom;
            int diagonal = (int) Math.sqrt(width * width + height * height); // 对角线的长度

            mPaint.setColor(mTextColor);
            mPaint.setTextSize(mTextSize);
            mPaint.setAntiAlias(true);
            float textWidth = mPaint.measureText(mText);

            canvas.drawColor(0x00000000);
            canvas.rotate(mRotation);

            int index = 0;
            float fromX;
            for (int positionY = diagonal / 20; positionY <= diagonal; positionY += diagonal / 20) {
                fromX = -width + (index++ % 2) * textWidth;
                for (float positionX = fromX; positionX < width; positionX += textWidth * 1.2) {
                    canvas.drawText(mText, positionX, positionY, mPaint);
                }
            }

            canvas.save();
            canvas.restore();
        }

        @Override
        public void setAlpha(@IntRange(from = 0, to = 255) int alpha) {
        }

        @Override
        public void setColorFilter(@Nullable ColorFilter colorFilter) {
        }

        @Override
        public int getOpacity() {
            return PixelFormat.TRANSLUCENT;
        }

    }
}
