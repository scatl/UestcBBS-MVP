package com.scatl.uestcbbs.custom;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.viewpager2.widget.ViewPager2;

/**
 * author: sca_tl
 * description:
 * date: 2020/1/5 13:14
 */
public class ScaleInTransformer implements ViewPager2.PageTransformer {

    private float mMinScale = 0.85f;

    @Override
    public void transformPage(@NonNull View page, float position) {
        //page.setElevation(- Math.abs(position));
        int pageWidth = page.getWidth();
        int pageHeight = page.getHeight();
        page.setPivotX((float) (pageHeight / 2));
        page.setPivotY((float) (pageWidth) / 2);


        if (position < -1) {
            page.setScaleX(mMinScale);
            page.setScaleY(mMinScale);
            page.setPivotX(pageWidth);
        } else if (position <= 1) {
            if (position < 0) {
                float scaleFactor = (1 + position) * (1 - mMinScale) + mMinScale;
                page.setScaleX(scaleFactor);
                page.setScaleY(scaleFactor);
                page.setPivotX(pageWidth * (0.5f + 0.5f * - position));
            } else {
                float scaleFactor = (1 - position) * (1 - mMinScale) + mMinScale;
                page.setScaleX(scaleFactor);
                page.setScaleY(scaleFactor);
                page.setPivotX(pageWidth * ((1 - position) * 0.5f));
            }
        } else {
            page.setPivotX(0f);
            page.setScaleX(mMinScale);
            page.setScaleY(mMinScale);
        }
    }
}
