package com.scatl.uestcbbs.module.home.view;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.google.android.material.appbar.AppBarLayout;
import com.scatl.uestcbbs.R;
import com.scatl.uestcbbs.annotation.ToastType;
import com.scatl.uestcbbs.base.BaseEvent;
import com.scatl.uestcbbs.base.BaseFragment;
import com.scatl.uestcbbs.base.BaseIndicatorAdapter;
import com.scatl.uestcbbs.base.BasePresenter;
import com.scatl.uestcbbs.helper.glidehelper.GlideLoader4Common;
import com.scatl.uestcbbs.module.account.view.AccountManagerActivity;
import com.scatl.uestcbbs.module.home.adapter.HomeMainViewPagerAdapter;
import com.scatl.uestcbbs.module.search.view.SearchActivity;
import com.scatl.uestcbbs.module.user.view.UserDetailActivity;
import com.scatl.uestcbbs.util.Constant;
import com.scatl.uestcbbs.util.DebugUtil;
import com.scatl.uestcbbs.util.SharePrefUtil;
import com.scatl.uestcbbs.util.ToastUtil;

import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.ViewPagerHelper;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;

import org.greenrobot.eventbus.EventBus;


public class HomeMainFragment extends BaseFragment implements AppBarLayout.OnOffsetChangedListener {

    private static final String TAG = "HomeMainFragment";

    private MagicIndicator magicIndicator;
    private ViewPager viewPager;
    private AppBarLayout appBarLayout;
    private ImageView userAvatar;
    private RelativeLayout searchLayout;

    private int before = 0;

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
        appBarLayout = view.findViewById(R.id.home_main_app_bar);
        userAvatar = view.findViewById(R.id.home_main_user_avatar);
        searchLayout = view.findViewById(R.id.home_main_search_layout);
    }

    @Override
    protected void initView() {

        userAvatar.setOnClickListener(this);
        searchLayout.setOnClickListener(this);
        appBarLayout.addOnOffsetChangedListener(this);

        viewPager.setOffscreenPageLimit(6);
        viewPager.setAdapter(new HomeMainViewPagerAdapter(getChildFragmentManager(),
                FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT));
        viewPager.setCurrentItem(0);


        String[] titles = {"最新发表", "最新回复", "热门", "精华", "淘专辑", "抢沙发"};
        CommonNavigator commonNavigator = new CommonNavigator(mActivity);
        commonNavigator.setAdapter(new BaseIndicatorAdapter(titles, 16, viewPager));
        magicIndicator.setNavigator(commonNavigator);
        ViewPagerHelper.bind(magicIndicator, viewPager);

        if (SharePrefUtil.isLogin(mActivity)){
            GlideLoader4Common.simpleLoad(mActivity, SharePrefUtil.getAvatar(mActivity), userAvatar);
        }
    }

    @Override
    protected BasePresenter initPresenter() {
        return null;
    }

    @Override
    protected void onClickListener(View v) {
        if (v.getId() == R.id.home_main_user_avatar) {
            if (SharePrefUtil.isLogin(mActivity)) {
                Intent intent = new Intent(mActivity, UserDetailActivity.class);
                intent.putExtra(Constant.IntentKey.USER_ID, SharePrefUtil.getUid(mActivity));
                startActivity(intent);
            } else {
                startActivity(new Intent(mActivity, AccountManagerActivity.class));
            }

        }
        if (v.getId() == R.id.home_main_search_layout) {
            startActivity(new Intent(mActivity, SearchActivity.class));
        }
    }

    @Override
    protected boolean registerEventBus() {
        return true;
    }

    @Override
    protected void receiveEventBusMsg(BaseEvent baseEvent) {
        super.receiveEventBusMsg(baseEvent);
        if (baseEvent.eventCode == BaseEvent.EventCode.LOGIN_SUCCESS) {
            GlideLoader4Common.simpleLoad(mActivity, SharePrefUtil.getAvatar(mActivity), userAvatar);
        }
        if (baseEvent.eventCode == BaseEvent.EventCode.LOGOUT_SUCCESS) {
            GlideLoader4Common.simpleLoad(mActivity, R.drawable.ic_default_avatar, userAvatar);
        }
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
        int after = Math.abs(verticalOffset);

        if (after == 0) {
            //EventBus.getDefault().post(new BaseEvent<>(BaseEvent.EventCode.HOME_NAVIGATION_HIDE, false));
        } else if (after == appBarLayout.getTotalScrollRange()) {
            EventBus.getDefault().post(new BaseEvent<>(BaseEvent.EventCode.HOME_NAVIGATION_HIDE, true));
        } else if (after - before > 0){
            EventBus.getDefault().post(new BaseEvent<>(BaseEvent.EventCode.HOME_NAVIGATION_HIDE, true));
        } else {
            EventBus.getDefault().post(new BaseEvent<>(BaseEvent.EventCode.HOME_NAVIGATION_HIDE, false));
        }
        before = after;
    }
}
