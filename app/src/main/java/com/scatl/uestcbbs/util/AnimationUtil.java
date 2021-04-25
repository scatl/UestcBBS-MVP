package com.scatl.uestcbbs.util;

import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.TranslateAnimation;

public class AnimationUtil {
    public static TranslateAnimation showFromRight() {
        TranslateAnimation mShowAction = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF, -1.0f,
                Animation.RELATIVE_TO_SELF, 0f,
                Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.0f);
        mShowAction.setDuration(500);
        mShowAction.setInterpolator(new AccelerateDecelerateInterpolator());
        return mShowAction;
    }

    public static TranslateAnimation showFromLeft() {
        TranslateAnimation mShowAction = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 1.0f,
                Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.0f);
        mShowAction.setDuration(500);
        mShowAction.setInterpolator(new AccelerateDecelerateInterpolator());
        return mShowAction;
    }

    public static TranslateAnimation hideToLeft() {
        TranslateAnimation mHiddenAction = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 0f,
                Animation.RELATIVE_TO_SELF, 1.0f,
                Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.0f);
        mHiddenAction.setDuration(500);
        mHiddenAction.setInterpolator(new AccelerateDecelerateInterpolator());
        return mHiddenAction;
    }

    public static TranslateAnimation hideToRight() {
        TranslateAnimation mHiddenAction = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, -1.0f,
                Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.0f);
        mHiddenAction.setDuration(500);
        mHiddenAction.setInterpolator(new AccelerateDecelerateInterpolator());
        return mHiddenAction;
    }
}
