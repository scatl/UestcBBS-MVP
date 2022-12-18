package com.scatl.uestcbbs.custom.emoticon;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.scatl.uestcbbs.R;
import com.scatl.uestcbbs.util.ColorUtil;
import com.scatl.uestcbbs.util.CommonUtil;

import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView;

public class EmoticonPagerTitle extends RelativeLayout implements IPagerTitleView {

    private ImageView imageView;

    public EmoticonPagerTitle(Context context) {
        super(context);
        init();
    }

    public EmoticonPagerTitle(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public EmoticonPagerTitle(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        imageView = new ImageView(getContext());
        addView(imageView);
    }

    public void setImageSource(String path) {
        Glide.with(this).load(path).into(imageView);
    }

    @Override
    public void onSelected(int index, int totalCount) {
        GradientDrawable gradientDrawable = new GradientDrawable();
        gradientDrawable.setCornerRadius(20);
        gradientDrawable.setColor(ColorUtil.getAttrColor(getContext(), R.attr.colorSurface));
        setBackground(gradientDrawable);

        LayoutParams img_params = new LayoutParams(CommonUtil.dip2px(getContext(), 25), CommonUtil.dip2px(getContext(), 25));
        img_params.leftMargin = CommonUtil.dip2px(getContext(), 10);
        img_params.rightMargin = CommonUtil.dip2px(getContext(), 10);
        img_params.addRule(RelativeLayout.CENTER_IN_PARENT);
        imageView.setLayoutParams(img_params);
    }

    @Override
    public void onDeselected(int index, int totalCount) {
        GradientDrawable gradientDrawable = new GradientDrawable();
        gradientDrawable.setCornerRadius(20);
        gradientDrawable.setColor(ColorUtil.getAttrColor(getContext(), R.attr.colorOnSurfaceInverse));
        setBackground(gradientDrawable);

        LayoutParams img_params = new LayoutParams(CommonUtil.dip2px(getContext(), 23), CommonUtil.dip2px(getContext(), 23));
        img_params.leftMargin = CommonUtil.dip2px(getContext(), 10);
        img_params.rightMargin = CommonUtil.dip2px(getContext(), 10);
        img_params.addRule(RelativeLayout.CENTER_IN_PARENT);
        imageView.setLayoutParams(img_params);
    }

    @Override
    public void onLeave(int index, int totalCount, float leavePercent, boolean leftToRight) {

    }

    @Override
    public void onEnter(int index, int totalCount, float enterPercent, boolean leftToRight) {

    }
}
