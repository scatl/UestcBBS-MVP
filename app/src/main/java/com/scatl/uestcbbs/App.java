package com.scatl.uestcbbs;

import android.app.Application;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Looper;

import androidx.appcompat.app.AppCompatDelegate;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.model.GlideUrl;
import com.google.android.material.color.DynamicColors;
import com.just.agentweb.AgentWebConfig;
import com.scatl.uestcbbs.annotation.ToastType;
import com.scatl.uestcbbs.api.ApiConstant;
import com.scatl.uestcbbs.helper.glidehelper.OkHttpUrlLoader;
import com.scatl.uestcbbs.util.CommonUtil;
import com.scatl.uestcbbs.util.Constant;
import com.scatl.uestcbbs.util.DebugUtil;
import com.scatl.uestcbbs.util.EmotionManager;
import com.scatl.uestcbbs.util.SSLUtil;
import com.scatl.uestcbbs.util.SharePrefUtil;
import com.scatl.uestcbbs.util.ToastUtil;
import com.tencent.bugly.crashreport.CrashReport;


import org.litepal.LitePal;

import java.io.InputStream;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import es.dmoral.toasty.Toasty;
import io.reactivex.functions.Consumer;
import io.reactivex.plugins.RxJavaPlugins;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import xyz.doikki.videoplayer.exo.ExoMediaPlayerFactory;
import xyz.doikki.videoplayer.player.AndroidMediaPlayerFactory;
import xyz.doikki.videoplayer.player.VideoViewConfig;
import xyz.doikki.videoplayer.player.VideoViewManager;

/**
 * author: sca_tl
 * description:
 * date: 2020/1/24 14:24
 */
public class App extends Application {

    private static final String TAG = "MyApplication";

    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();

        EmotionManager.Companion.getINSTANCE().init(context);

        VideoViewManager
                .setConfig(VideoViewConfig.newBuilder()
                        .setPlayerFactory(ExoMediaPlayerFactory.create())
                        .build());

        if (SharePrefUtil.isThemeFollowWallpaper(context) && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            DynamicColors.applyToActivitiesIfAvailable(this);
        }

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

        if (SharePrefUtil.isIgnoreSSLVerifier(context)) {
            Glide.get(this)
                    .getRegistry()
                    .replace(GlideUrl.class, InputStream.class, new OkHttpUrlLoader.Factory((Call.Factory) get()));

        }

    }

    private OkHttpClient get() {
        return new OkHttpClient.Builder()
                .sslSocketFactory(SSLUtil.getSSLSocketFactory(), SSLUtil.getTrustManager())
                .hostnameVerifier(SSLUtil.getHostNameVerifier())
                .build();
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
