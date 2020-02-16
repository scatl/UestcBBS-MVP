package com.scatl.uestcbbs.custom.postview;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;

/**
 * author: sca_tl
 * description:
 * date: 2019/07/20 9:56
 */
public class GlideUrlDrawable extends Drawable implements Drawable.Callback {

    private Drawable drawable;

    public void setDrawable(Drawable drawable) {
        if (this.drawable != null) { this.drawable.setCallback(null); }
        drawable.setCallback(this);
        this.drawable = drawable;
    }


    @Override
    public void draw(Canvas canvas) {
        if (drawable != null) {
            drawable.draw(canvas);
        }
    }

    @Override
    public void setAlpha(int i) {
        if (drawable != null) drawable.setAlpha(i);
    }

    @Override
    public void setColorFilter(ColorFilter colorFilter) {
        if (drawable != null) drawable.setColorFilter(colorFilter);
    }

    @Override
    public int getOpacity() {
        if (drawable != null) return drawable.getOpacity();
        return PixelFormat.UNKNOWN;
    }

    @Override
    public void invalidateDrawable(Drawable drawable) {
        if (getCallback() != null) getCallback().invalidateDrawable(drawable);
    }

    @Override
    public void scheduleDrawable(Drawable drawable, Runnable runnable, long l) {
        if (getCallback() != null) getCallback().scheduleDrawable(drawable, runnable, l);
    }

    @Override
    public void unscheduleDrawable(Drawable drawable, Runnable runnable) {
        if (getCallback() != null) getCallback().unscheduleDrawable(drawable, runnable);
    }
}
