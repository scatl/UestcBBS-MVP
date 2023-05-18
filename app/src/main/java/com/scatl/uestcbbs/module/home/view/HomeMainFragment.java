package com.scatl.uestcbbs.module.home.view;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.scatl.uestcbbs.R;
import com.scatl.uestcbbs.base.BaseEvent;
import com.scatl.uestcbbs.base.BaseFragment;
import com.scatl.uestcbbs.base.BasePresenter;
import com.scatl.uestcbbs.callback.TabListenerAdapter;
import com.scatl.uestcbbs.helper.glidehelper.GlideLoader4Common;
import com.scatl.uestcbbs.callback.IHomeRefresh;
import com.scatl.uestcbbs.module.account.view.AccountManagerActivity;
import com.scatl.uestcbbs.module.home.adapter.HomeMainViewPagerAdapter;
import com.scatl.uestcbbs.manager.MessageManager;
import com.scatl.uestcbbs.module.search.view.SearchActivity;
import com.scatl.uestcbbs.module.user.view.UserDetailActivity;
import com.scatl.uestcbbs.util.Constant;
import com.scatl.uestcbbs.util.ExtensionKt;
import com.scatl.uestcbbs.util.SharePrefUtil;

import org.greenrobot.eventbus.EventBus;


public class HomeMainFragment extends BaseFragment implements AppBarLayout.OnOffsetChangedListener {

    private static final String TAG = "HomeMainFragment";

    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private AppBarLayout appBarLayout;
    private ImageView userAvatar;
    private RelativeLayout searchLayout;

    private int before = 0;
    private boolean shortCutHot = false;

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
        tabLayout = view.findViewById(R.id.home_main_indicator);
        viewPager = view.findViewById(R.id.home_main_viewpager);
        appBarLayout = view.findViewById(R.id.home_main_app_bar);
        userAvatar = view.findViewById(R.id.home_main_user_avatar);
        searchLayout = view.findViewById(R.id.home_main_search_layout);
    }

    @Override
    protected void getBundle(Bundle bundle) {
        if (bundle != null) {
            shortCutHot = bundle.getBoolean(Constant.IntentKey.SHORT_CUT_HOT, false);
        }
    }

    @Override
    protected void initView() {

        userAvatar.setOnClickListener(this);
        searchLayout.setOnClickListener(this);
        appBarLayout.addOnOffsetChangedListener(this);

        viewPager.setOffscreenPageLimit(5);
        ExtensionKt.desensitize(viewPager);
        viewPager.setAdapter(new HomeMainViewPagerAdapter(this));
        viewPager.setCurrentItem(0);

        tabLayout.addOnTabSelectedListener(new TabListenerAdapter() {
            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                EventBus.getDefault().post(new BaseEvent<>(BaseEvent.EventCode.HOME_REFRESH));
            }
        });

        String[] titles = {"最新发表", "最新回复", "热门", "精华", "淘专辑"};
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> tab.setText(titles[position])

        ).attach();

        if (shortCutHot) {
            viewPager.setCurrentItem(2);
            shortCutHot = false;
        }

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
        if (baseEvent.eventCode == BaseEvent.EventCode.HOME_REFRESH && viewPager.getAdapter() != null) {
            Fragment fragment = getChildFragmentManager().findFragmentByTag("f" + viewPager.getAdapter().getItemId(viewPager.getCurrentItem()));
            if (fragment instanceof IHomeRefresh) {
                ((IHomeRefresh) fragment).onRefresh();
            }
        }
//        if (baseEvent.eventCode == BaseEvent.EventCode.SET_MSG_COUNT) {
//            BadgeDrawable badgeDrawable = tabLayout.getTabAt(4).getOrCreateBadge();
//            if (MessageManager.Companion.getINSTANCE().getCollectionUpdateInfo().size() != 0) {
//                badgeDrawable.setVisible(true);
//                badgeDrawable.setNumber(MessageManager.Companion.getINSTANCE().getCollectionUpdateInfo().size());
//            } else {
//                badgeDrawable.setVisible(false);
//                badgeDrawable.clearNumber();
//            }
//        }
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
