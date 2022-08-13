package com.scatl.uestcbbs;

import android.app.Application;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Looper;

import androidx.appcompat.app.AppCompatDelegate;

import com.google.android.material.color.DynamicColors;
import com.just.agentweb.AgentWebConfig;
import com.scatl.uestcbbs.annotation.ToastType;
import com.scatl.uestcbbs.api.ApiConstant;
import com.scatl.uestcbbs.util.CommonUtil;
import com.scatl.uestcbbs.util.Constant;
import com.scatl.uestcbbs.util.DebugUtil;
import com.scatl.uestcbbs.util.SharePrefUtil;
import com.scatl.uestcbbs.util.ToastUtil;
import com.tencent.bugly.crashreport.CrashReport;


import org.litepal.LitePal;

import es.dmoral.toasty.Toasty;
import io.reactivex.functions.Consumer;
import io.reactivex.plugins.RxJavaPlugins;
import xyz.doikki.videoplayer.exo.ExoMediaPlayerFactory;
import xyz.doikki.videoplayer.player.AndroidMediaPlayerFactory;
import xyz.doikki.videoplayer.player.VideoViewConfig;
import xyz.doikki.videoplayer.player.VideoViewManager;

/**
 * author: sca_tl
 * description:
 * date: 2020/1/24 14:24
 */
public class MyApplication extends Application {

    private static final String TAG = "MyApplication";

    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();

        VideoViewManager.setConfig(VideoViewConfig.newBuilder()
                .setPlayerFactory(ExoMediaPlayerFactory.create())
                .build());

//        DynamicColors.applyToActivitiesIfAvailable(this);
        CrashReport.initCrashReport(getApplicationContext(), Constant.BUGLY_ID, false);
        LitePal.initialize(this);
        setUiMode();
        CommonUtil.isDownloadPermissionAccessible(context);

        RxJavaPlugins.setErrorHandler(throwable -> { });

        Toasty.Config
                .getInstance()
                .setToastTypeface(Typeface.DEFAULT)
                .setTextSize(14)
                .supportDarkTheme(true)
                .apply();

        if (SharePrefUtil.isLogin(getContext())) {
            if (SharePrefUtil.isSuperLogin(this, SharePrefUtil.getName(getContext()))) {
                for (String s : SharePrefUtil.getCookies(this, SharePrefUtil.getName(getContext()))) {
                    AgentWebConfig.syncCookie(ApiConstant.BBS_BASE_URL, s);
                }
            }
        }
    }

    private void setUiMode() {
        if (SharePrefUtil.getUiModeFollowSystem(getApplicationContext())) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        } else {
            if (SharePrefUtil.isNightMode(getApplicationContext())) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }
        }
    }

    public static Context getContext() {
        return context;
    }
}
