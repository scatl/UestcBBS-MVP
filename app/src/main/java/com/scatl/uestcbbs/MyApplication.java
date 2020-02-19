package com.scatl.uestcbbs;

import android.app.Application;

import androidx.appcompat.app.AppCompatDelegate;

import com.scatl.uestcbbs.util.Constant;
import com.scatl.uestcbbs.util.SharePrefUtil;
import com.scatl.uestcbbs.util.ToastUtil;
import com.tencent.bugly.crashreport.CrashReport;

import org.litepal.LitePal;

/**
 * author: sca_tl
 * description:
 * date: 2020/1/24 14:24
 */
public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        if (SharePrefUtil.isNightMode(getApplicationContext())) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        CrashReport.initCrashReport(getApplicationContext(), Constant.BUGLY_ID, true);
        LitePal.initialize(this);

    }

}
