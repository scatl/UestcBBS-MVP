package com.scatl.uestcbbs.base;

import android.content.Context;
import android.graphics.Color;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;

import androidx.viewpager.widget.ViewPager;

import com.google.android.material.color.MaterialColors;
import com.scatl.uestcbbs.R;

import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.LinePagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.ColorTransitionPagerTitleView;

import org.greenrobot.eventbus.EventBus;

/**
 * author: sca_tl
 * description:
 * date: 2020/3/2 14:02
 */
public class BaseIndicatorAdapter extends CommonNavigatorAdapter {

    private String[] titles;
    private ViewPager viewPager;
    private int titleSize;

    public BaseIndicatorAdapter(String[] titles, int titleSize, ViewPager viewPager) {
        this.titles = titles;
        this.viewPager = viewPager;
        this.titleSize = titleSize;
    }

    @Override
    public int getCount() {
        return titles.length;
    }

    @Override
    public IPagerTitleView getTitleView(Context context, int index) {
        ColorTransitionPagerTitleView simplePagerTitleView = new ColorTransitionPagerTitleView(context);
        simplePagerTitleView.setText(titles[index]);
        simplePagerTitleView.setTextSize(titleSize);
        simplePagerTitleView.setNormalColor(Color.GRAY);
        simplePagerTitleView.setSelectedColor(MaterialColors.getColor(context, R.attr.colorPrimary, context.getColor(R.color.colorPrimary)));
        simplePagerTitleView.setOnClickListener(v -> {
            viewPager.setCurrentItem(index);
            EventBus.getDefault().post(new BaseEvent<>(BaseEvent.EventCode.VIEW_PAGER_TITLE_CLICK, index));
        });
        return simplePagerTitleView;
    }

    @Override
    public IPagerIndicator getIndicator(Context context) {
        LinePagerIndicator indicator = new LinePagerIndicator(context);
        indicator.setMode(LinePagerIndicator.MODE_WRAP_CONTENT);
        indicator.setLineHeight(7);
        indicator.setXOffset(20);
        indicator.setRoundRadius(10);
        indicator.setYOffset(5);
        indicator.setStartInterpolator(new AccelerateInterpolator());
        indicator.setEndInterpolator(new DecelerateInterpolator(2.0f));
        indicator.setColors(MaterialColors.getColor(context, R.attr.colorPrimary, context.getColor(R.color.colorPrimary)));

        return indicator;
    }



}
