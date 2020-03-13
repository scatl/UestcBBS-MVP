package com.scatl.uestcbbs.module.home.view;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jaeger.library.StatusBarUtil;
import com.scatl.uestcbbs.R;
import com.scatl.uestcbbs.base.BaseFragment;
import com.scatl.uestcbbs.base.BaseIndicatorAdapter;
import com.scatl.uestcbbs.base.BasePresenter;
import com.scatl.uestcbbs.module.home.adapter.Home1ViewPagerAdapter;
import com.scatl.uestcbbs.module.home.adapter.HomeMainViewPagerAdapter;

import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.ViewPagerHelper;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;


public class HomeMainFragment extends BaseFragment {

    private MagicIndicator magicIndicator;
    private ViewPager viewPager;

    public static HomeMainFragment getInstance(Bundle bundle) {
        HomeMainFragment homeMainFragment = new HomeMainFragment();
        homeMainFragment.setArguments(bundle);
        return homeMainFragment;
    }


    @Override
    protected int setLayoutResourceId() {
        return R.layout.fragment_home_main;
    }

    @Override
    protected void findView() {
        magicIndicator = view.findViewById(R.id.home_main_indicator);
        viewPager = view.findViewById(R.id.home_main_viewpager);
    }

    @Override
    protected void initView() {
        viewPager.setOffscreenPageLimit(3);
        viewPager.setAdapter(new HomeMainViewPagerAdapter(getChildFragmentManager(),
                FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT));
        viewPager.setCurrentItem(0);

        final String[] titles = {"最新发表", "最新回复"};

        CommonNavigator commonNavigator = new CommonNavigator(mActivity);
        commonNavigator.setAdapter(new BaseIndicatorAdapter(titles, viewPager));
        magicIndicator.setNavigator(commonNavigator);
        ViewPagerHelper.bind(magicIndicator, viewPager);

    }

    @Override
    protected BasePresenter initPresenter() {
        return null;
    }
}
