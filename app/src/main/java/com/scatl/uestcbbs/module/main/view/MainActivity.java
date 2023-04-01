package com.scatl.uestcbbs.module.main.view;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationBarView;
import com.jaeger.library.StatusBarUtil;
import com.scatl.uestcbbs.R;
import com.scatl.uestcbbs.base.BaseActivity;
import com.scatl.uestcbbs.base.BaseEvent;
import com.scatl.uestcbbs.base.BasePresenter;
import com.scatl.uestcbbs.entity.OpenPicBean;
import com.scatl.uestcbbs.entity.SettingsBean;
import com.scatl.uestcbbs.entity.UpdateBean;
import com.scatl.uestcbbs.module.message.MessageManager;
import com.scatl.uestcbbs.services.DayQuestionService;
import com.scatl.uestcbbs.module.main.adapter.MainViewPagerAdapter;
import com.scatl.uestcbbs.module.main.presenter.MainPresenter;
import com.scatl.uestcbbs.module.post.view.CreatePostActivity;
import com.scatl.uestcbbs.module.post.view.HotPostFragment;
import com.scatl.uestcbbs.module.update.view.UpdateFragment;
import com.scatl.uestcbbs.services.HeartMsgService;
import com.scatl.uestcbbs.util.CommonUtil;
import com.scatl.uestcbbs.util.Constant;
import com.scatl.uestcbbs.util.SharePrefUtil;
import com.scatl.uestcbbs.util.TimeUtil;
import com.scatl.util.common.ServiceUtil;

import org.greenrobot.eventbus.EventBus;

public class MainActivity extends BaseActivity implements MainView{

    private ViewPager2 mainViewpager;
    private BottomNavigationView bottomNavigationView;
    private FloatingActionButton floatingActionButton;
    CoordinatorLayout coordinatorLayout;
    private MainPresenter mainPresenter;
    private MainViewPagerAdapter mainViewPagerAdapter;

    private int selected;
    private boolean shortCutMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        if ("com.scatl.uestcbbs.module.post.view.HotPostFragment".equals(getIntent().getAction())) {
            new Handler().postDelayed(() ->
                            HotPostFragment.getInstance(null)
                                    .show(getSupportFragmentManager(), TimeUtil.getStringMs()),
                    200);
        }
        if ("com.scatl.uestcbbs.module.message.view.MessageFragment".equals(getIntent().getAction())) {
            shortCutMessage = true;
        }

        super.onCreate(savedInstanceState);
    }

    @Override
    protected void getIntent(Intent intent) {
        super.getIntent(intent);
        selected = intent.getIntExtra("selected", 0);
    }

    @Override
    protected int setLayoutResourceId() {
        return R.layout.activity_main;
    }

    @Override
    protected void findView() {
        mainViewpager = findViewById(R.id.main_viewpager);
        bottomNavigationView = findViewById(R.id.main_bottom_navigation_bar);
        floatingActionButton = findViewById(R.id.main_create_new_post_btn);
        coordinatorLayout = findViewById(R.id.main_coor_layout);
    }

    @Override
    protected void initView() {
        mainPresenter = (MainPresenter) presenter;

        floatingActionButton.setOnClickListener(this);

        mainViewPagerAdapter = new MainViewPagerAdapter(this);
        mainViewpager.setAdapter(mainViewPagerAdapter);
        mainViewpager.setUserInputEnabled(false);
        mainViewpager.setOffscreenPageLimit(3);
        mainViewpager.setCurrentItem(selected, false);
        if (shortCutMessage) {
            selected = 2;
            mainViewpager.setCurrentItem(selected, false);
            bottomNavigationView.setSelectedItemId(R.id.page_notification);
        } else {
            mainViewpager.setCurrentItem(selected, false);
            switch (selected) {
                case 0: bottomNavigationView.setSelectedItemId(R.id.page_home); break;
                case 1: bottomNavigationView.setSelectedItemId(R.id.page_board_list); break;
                case 3: bottomNavigationView.setSelectedItemId(R.id.page_mine); break;
            }
        }

        floatingActionButton.setVisibility(selected == 0 ? View.VISIBLE : View.GONE);

        startService();
        mainPresenter.getSettings();
        mainPresenter.getOpenPic();
        mainPresenter.getUpdate(CommonUtil.getVersionCode(this), false);
        mainPresenter.showDayQuestionTips(this);
    }

    @Override
    protected BasePresenter initPresenter() {
        return new MainPresenter();
    }

    @Override
    protected void onClickListener(View view) {
        if (view.getId() == R.id.main_create_new_post_btn) {
            Intent intent = new Intent(this, CreatePostActivity.class);
            startActivity(intent);
        }
    }

    @Override
    protected void setOnItemClickListener() {
        bottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.page_home:
                    floatingActionButton.show();
                    selected = 0;
                    mainViewpager.setCurrentItem(0, false);
                    break;
                case R.id.page_board_list:
                    floatingActionButton.hide();
                    selected = 1;
                    mainViewpager.setCurrentItem(1, false);
                    break;
                case R.id.page_notification:
                    floatingActionButton.hide();
                    selected = 2;
                    mainViewpager.setCurrentItem(2, false);
                    break;
                case R.id.page_mine:
                    floatingActionButton.hide();
                    selected = 3;
                    mainViewpager.setCurrentItem(3, false);
                    break;
            }
            return true;
        });
        bottomNavigationView.setOnItemReselectedListener(new NavigationBarView.OnItemReselectedListener() {
            @Override
            public void onNavigationItemReselected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.page_home) {
                    EventBus.getDefault().post(new BaseEvent<>(BaseEvent.EventCode.HOME_REFRESH));
                }
            }
        });

    }

    @Override
    public void getUpdateSuccess(UpdateBean updateBean) {
        try {
            if (updateBean.updateInfo.isValid && updateBean.updateInfo.apkVersionCode > CommonUtil.getVersionCode(this) &&
                    updateBean.updateInfo.apkVersionCode != SharePrefUtil.getIgnoreVersionCode(this)) {
                Bundle bundle = new Bundle();
                bundle.putSerializable(Constant.IntentKey.DATA_1, updateBean);
                UpdateFragment.getInstance(bundle).show(getSupportFragmentManager(), TimeUtil.getStringMs());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void getUpdateFail(String msg) { }

    @Override
    public void getSettingsSuccess(SettingsBean settingsBean) {
        try {
            if (SharePrefUtil.getGraySaturation(this) != settingsBean.graySaturation) {
                SharePrefUtil.setGraySaturation(this, settingsBean.graySaturation);

                Intent intent = new Intent( MainActivity.this, MainActivity.class);
                startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
                finish();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void getSettingsFail(String msg) { }

    @Override
    public void getOpenPicSuccess(OpenPicBean openPicBean) {
        mainPresenter.showOpenPic(this, openPicBean);
    }

    @Override
    public void getOpenPicsFail(String msg) { }

    @Override
    protected boolean registerEventBus() {
        return true;
    }

    @Override
    public void receiveEventBusMsg(BaseEvent baseEvent) {
        if (baseEvent.eventCode == BaseEvent.EventCode.NIGHT_MODE_YES) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            finish();
            Intent intent = new Intent( MainActivity.this, MainActivity.class);
            intent.putExtra("selected", 3);
            startActivity(intent);
            overridePendingTransition(R.anim.switch_night_mode_fade_in, R.anim.switch_night_mode_fade_out);
        }

        if (baseEvent.eventCode == BaseEvent.EventCode.NIGHT_MODE_NO) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            finish();
            Intent intent = new Intent( MainActivity.this, MainActivity.class);
            intent.putExtra("selected", 3);
            startActivity(intent);
            overridePendingTransition(R.anim.switch_night_mode_fade_in, R.anim.switch_night_mode_fade_out);
        }

        if (baseEvent.eventCode == BaseEvent.EventCode.SET_MSG_COUNT) {
            BadgeDrawable badgeDrawable = bottomNavigationView.getOrCreateBadge(R.id.page_notification);
            int msgCount = MessageManager.Companion.getINSTANCE().getUnreadMsgCount();
            if (msgCount == 0) {
                badgeDrawable.setVisible(false);
                badgeDrawable.clearNumber();
            } else {
                badgeDrawable.setVisible(true);
                badgeDrawable.setNumber(msgCount);
            }
        }

        if (baseEvent.eventCode == BaseEvent.EventCode.SWITCH_TO_MESSAGE) {
            selected = 2;
            mainViewpager.setCurrentItem(selected, false);
        }

        if (baseEvent.eventCode == BaseEvent.EventCode.HOME_NAVIGATION_HIDE) {
            boolean hide = (Boolean) baseEvent.eventData;
            if (hide) {
                if (bottomNavigationView.getVisibility() != View.GONE) {
                    floatingActionButton.hide();
                    bottomNavigationView.setVisibility(View.GONE);
                    bottomNavigationView.startAnimation(AnimationUtils.loadAnimation(this,R.anim.view_dismiss_y0_y1_no_alpha));
                }
            } else {
                if (bottomNavigationView.getVisibility() != View.VISIBLE) {
                    if (selected == 0){
                        floatingActionButton.show();
                    }
                    bottomNavigationView.setVisibility(View.VISIBLE);
                    bottomNavigationView.startAnimation(AnimationUtils.loadAnimation(this,R.anim.view_appear_y1_y0_no_alpha));
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopService(new Intent(this, HeartMsgService.class));
    }

    public void startService() {
        if (SharePrefUtil.isLogin(this)) {
            if (!ServiceUtil.isServiceRunning(this, HeartMsgService.SERVICE_NAME)) {
                startService(new Intent(this, HeartMsgService.class));
            }
        }

        if (SharePrefUtil.isLogin(this) &&
                SharePrefUtil.isSuperLogin(this, SharePrefUtil.getName(this)) &&
                SharePrefUtil.isAnswerQuestionBackground(this) &&
                !ServiceUtil.isServiceRunning(this, DayQuestionService.class.getName())) {
            startService(new Intent(this, DayQuestionService.class));
        }
    }

    @Override
    protected void setStatusBar() {
        StatusBarUtil.setTransparent(this);
    }
}
