package com.scatl.uestcbbs.module.main.view;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.viewpager2.widget.ViewPager2;

import com.airbnb.lottie.LottieAnimationView;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.scatl.uestcbbs.R;
import com.scatl.uestcbbs.base.BaseActivity;
import com.scatl.uestcbbs.base.BaseEvent;
import com.scatl.uestcbbs.base.BasePresenter;
import com.scatl.uestcbbs.entity.OpenPicBean;
import com.scatl.uestcbbs.entity.SettingsBean;
import com.scatl.uestcbbs.entity.UpdateBean;
import com.scatl.uestcbbs.helper.glidehelper.GlideLoader4Common;
import com.scatl.uestcbbs.module.main.adapter.MainViewPagerAdapter;
import com.scatl.uestcbbs.module.main.presenter.MainPresenter;
import com.scatl.uestcbbs.module.post.view.CreatePostActivity;
import com.scatl.uestcbbs.module.post.view.HotPostFragment;
import com.scatl.uestcbbs.module.update.view.UpdateFragment;
import com.scatl.uestcbbs.services.HeartMsgService;
import com.scatl.uestcbbs.util.CommonUtil;
import com.scatl.uestcbbs.util.Constant;
import com.scatl.uestcbbs.util.ServiceUtil;
import com.scatl.uestcbbs.util.SharePrefUtil;
import com.scatl.uestcbbs.util.TimeUtil;

import org.greenrobot.eventbus.EventBus;

public class MainActivity extends BaseActivity implements MainView{

    private ViewPager2 mainViewpager;
    private AHBottomNavigation ahBottomNavigation;
    private FloatingActionButton floatingActionButton;

    private MainPresenter mainPresenter;
    private MainViewPagerAdapter mainViewPagerAdapter;

    private int selected;
    private boolean shortCutMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        if ("com.scatl.uestcbbs.module.post.view.HotPostFragment".equals(getIntent().getAction())) {
            new Handler().postDelayed(() ->
                    HotPostFragment.getInstance(null)
                    .show(getSupportFragmentManager(), TimeUtil.getStringMs()), 200);
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
        ahBottomNavigation = findViewById(R.id.main_bottom_navigation_bar);
        floatingActionButton = findViewById(R.id.main_create_new_post_btn);
    }

    @Override
    protected void initView() {
        mainPresenter = (MainPresenter) presenter;

        floatingActionButton.setOnClickListener(this);

//        if (SharePrefUtil.isNightMode(this)) {
            ahBottomNavigation.setDefaultBackgroundColor(getColor(R.color.statusbar_color));
//        }
        ahBottomNavigation.setBehaviorTranslationEnabled(true);

        ahBottomNavigation.manageFloatingActionButtonBehavior(floatingActionButton);
        ahBottomNavigation.setNotificationBackgroundColor(getColor(R.color.colorPrimary));
        ahBottomNavigation.setAccentColor(getColor(R.color.colorPrimary));
        ahBottomNavigation.addItem(new AHBottomNavigationItem("首页", R.drawable.ic_home));
        ahBottomNavigation.addItem(new AHBottomNavigationItem("板块", R.drawable.ic_boardlist));
        ahBottomNavigation.addItem(new AHBottomNavigationItem("通知", R.drawable.ic_notification));
        ahBottomNavigation.addItem(new AHBottomNavigationItem("我的", R.drawable.ic_mine));

        mainViewPagerAdapter = new MainViewPagerAdapter(this);
        mainViewpager.setAdapter(mainViewPagerAdapter);
        mainViewpager.setUserInputEnabled(false);
        mainViewpager.setOffscreenPageLimit(3);
        mainViewpager.setCurrentItem(selected, false);
        if (shortCutMessage) {
            selected = 2;
            mainViewpager.setCurrentItem(selected, false);
            ahBottomNavigation.setCurrentItem(selected, false);
        } else {
            ahBottomNavigation.setCurrentItem(selected, false);
        }

        floatingActionButton.setVisibility(selected == 0 ? View.VISIBLE : View.GONE);

        startService();
        mainPresenter.getSettings();
        mainPresenter.getOpenPic();
        mainPresenter.getUpdate(CommonUtil.getVersionCode(this), false);
    }

    @Override
    protected BasePresenter initPresenter() {
        return new MainPresenter();
    }

    @Override
    protected void onClickListener(View view) {
        if (view.getId() == R.id.main_create_new_post_btn) {
            startActivity(new Intent(this, CreatePostActivity.class));
        }
    }

    private long t = 0;
    @Override
    protected void setOnItemClickListener() {
        ahBottomNavigation.setOnTabSelectedListener((position, wasSelected) -> {
            floatingActionButton.setVisibility(position == 0 ? View.VISIBLE : View.GONE);

            //双击
            if (wasSelected && System.currentTimeMillis() - t < 300) {
                //刷新首页
                EventBus.getDefault().post(new BaseEvent<>(BaseEvent.EventCode.HOME_REFRESH));
                return true;
            } else {
                t = System.currentTimeMillis();
                mainViewpager.setCurrentItem(position, false);
            }
            return true;
        });

        mainViewpager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                ahBottomNavigation.setCurrentItem(position, false);
            }
        });

    }

    @Override
    public void getUpdateSuccess(UpdateBean updateBean) {
        if (updateBean.updateInfo.isValid && updateBean.updateInfo.apkVersionCode > CommonUtil.getVersionCode(this) &&
                updateBean.updateInfo.apkVersionCode != SharePrefUtil.getIgnoreVersionCode(this)) {
            Bundle bundle = new Bundle();
            bundle.putSerializable(Constant.IntentKey.DATA, updateBean);
            UpdateFragment.getInstance(bundle).show(getSupportFragmentManager(), TimeUtil.getStringMs());
        }
    }

    @Override
    public void getUpdateFail(String msg) { }

    @Override
    public void getSettingsSuccess(SettingsBean settingsBean) {
        if (SharePrefUtil.getGraySaturation(this) != settingsBean.graySaturation) {
            SharePrefUtil.setGraySaturation(this, settingsBean.graySaturation);

            Intent intent = new Intent( MainActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    public void getSettingsFail(String msg) { }

    @Override
    public void getOpenPicSuccess(OpenPicBean openPicBean) {
        mainPresenter.showOpenPic(this, openPicBean);
//        final View dialog_view = LayoutInflater.from(this).inflate(R.layout.dialog_open_pic, new RelativeLayout(this));
//        final LottieAnimationView animationView = dialog_view.findViewById(R.id.dialog_open_pic_animation);
//        final ImageView imageView = dialog_view.findViewById(R.id.dialog_open_pic_image);
//        final CheckBox neverShow = dialog_view.findViewById(R.id.dialog_pic_open_never_show);
//        try {
//
//            if (openPicBean.isAnimation && openPicBean.isValid) {
//                animationView.setVisibility(View.VISIBLE);
//                animationView.setAnimationFromUrl(openPicBean.url);
//            } else if (openPicBean.isImage && openPicBean.isValid) {
//                imageView.setVisibility(View.VISIBLE);
//                GlideLoader4Common.simpleLoad(this, openPicBean.url, imageView);
//            }
//
//            final AlertDialog report_dialog = new AlertDialog.Builder(this, R.style.TransparentDialog)
//                    .setView(dialog_view)
//                    .create();
//            report_dialog.setOnShowListener(dialogInterface -> {
//
//            });
//            report_dialog.show();
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

    }

    @Override
    public void getOpenPicsFail(String msg) {

    }

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
            int msg_count = HeartMsgService.at_me_msg_count +
                    HeartMsgService.private_me_msg_count +
                    HeartMsgService.reply_me_msg_count + HeartMsgService.system_msg_count;
            if (msg_count != 0) {
                ahBottomNavigation.setNotification(msg_count + "", 2);
            } else {
                ahBottomNavigation.setNotification("", 2);
            }
        }

        if (baseEvent.eventCode == BaseEvent.EventCode.SWITCH_TO_MESSAGE) {
            selected = 2;
            mainViewpager.setCurrentItem(selected, false);
            ahBottomNavigation.setCurrentItem(selected, false);
        }

        if (baseEvent.eventCode == BaseEvent.EventCode.HOME_NAVIGATION_HIDE) {
            boolean hide = (Boolean) baseEvent.eventData;
            if (hide) {
                ahBottomNavigation.hideBottomNavigation(true);
            } else {
                ahBottomNavigation.restoreBottomNavigation(true);
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
            if (!ServiceUtil.isServiceRunning(this, HeartMsgService.serviceName)) {
                Intent intent = new Intent(this, HeartMsgService.class);
                startService(intent);
            }
        }
    }
}
