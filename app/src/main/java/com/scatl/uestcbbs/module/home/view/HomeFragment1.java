package com.scatl.uestcbbs.module.home.view;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.scatl.uestcbbs.R;
import com.scatl.uestcbbs.base.BaseEvent;
import com.scatl.uestcbbs.base.BaseFragment;
import com.scatl.uestcbbs.base.BaseIndicatorAdapter;
import com.scatl.uestcbbs.base.BasePresenter;
import com.scatl.uestcbbs.module.home.adapter.Home1ViewPagerAdapter;
import com.scatl.uestcbbs.module.user.adapter.UserDetailIndicatorAdapter;
import com.scatl.uestcbbs.module.user.adapter.UserPostViewPagerAdapter;

import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.ViewPagerHelper;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;

import org.greenrobot.eventbus.EventBus;

public class HomeFragment1 extends BaseFragment {

    private MagicIndicator magicIndicator;
    private ViewPager viewPager;

    public static HomeFragment1 getInstance(Bundle bundle) {
        HomeFragment1 homeFragment1 = new HomeFragment1();
        homeFragment1.setArguments(bundle);
        return homeFragment1;
    }

    @Override
    protected int setLayoutResourceId() {
        return R.layout.fragment_home1;
    }

    @Override
    protected void findView() {
        magicIndicator = view.findViewById(R.id.home1_indicator);
        viewPager = view.findViewById(R.id.home1_viewpager);
    }

    @Override
    protected void initView() {
        viewPager.setOffscreenPageLimit(3);
        viewPager.setAdapter(new Home1ViewPagerAdapter(getChildFragmentManager(),
                FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT));
        viewPager.setCurrentItem(0);

        final String[] titles = {"最新发表", "最新回复", "近期热门"};

        CommonNavigator commonNavigator = new CommonNavigator(mActivity);
        commonNavigator.setAdapter(new BaseIndicatorAdapter(titles, viewPager));
        magicIndicator.setNavigator(commonNavigator);
        ViewPagerHelper.bind(magicIndicator, viewPager);

    }

    @Override
    protected BasePresenter initPresenter() {
        return null;
    }

    @Override
    protected boolean registerEventBus() {
        return true;
    }

    @Override
    public void onEventBusReceived(BaseEvent baseEvent) {
        if (baseEvent.eventCode == BaseEvent.EventCode.HOME_REFRESH) {
            EventBus.getDefault().post(new BaseEvent<>(BaseEvent.EventCode.HOME1_REFRESH, viewPager.getCurrentItem()));
        }
    }
}
