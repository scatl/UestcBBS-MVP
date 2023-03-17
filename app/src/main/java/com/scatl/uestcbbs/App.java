package com.scatl.uestcbbs;

import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.model.GlideUrl;
import com.google.android.material.color.DynamicColors;
import com.just.agentweb.AgentWebConfig;
import com.scatl.uestcbbs.annotation.ToastType;
import com.scatl.uestcbbs.api.ApiConstant;
import com.scatl.uestcbbs.base.BaseEvent;
import com.scatl.uestcbbs.helper.glidehelper.OkHttpUrlLoader;
import com.scatl.uestcbbs.http.OkHttpDns;
import com.scatl.uestcbbs.util.CommonUtil;
import com.scatl.uestcbbs.util.Constant;
import com.scatl.uestcbbs.util.DebugUtil;
import com.scatl.uestcbbs.util.EmotionManager;
import com.scatl.uestcbbs.util.SSLUtil;
import com.scatl.uestcbbs.util.SharePrefUtil;
import com.scatl.uestcbbs.util.ToastUtil;
import com.scwang.smartrefresh.header.MaterialHeader;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.DefaultRefreshFooterCreator;
import com.scwang.smartrefresh.layout.api.DefaultRefreshHeaderCreator;
import com.scwang.smartrefresh.layout.api.DefaultRefreshInitializer;
import com.scwang.smartrefresh.layout.api.RefreshFooter;
import com.scwang.smartrefresh.layout.api.RefreshHeader;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.tencent.bugly.crashreport.CrashReport;


import org.greenrobot.eventbus.EventBus;
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

    private static final String TAG = "APP";

    private static Context context;

    static {
        SmartRefreshLayout.setDefaultRefreshInitializer((context, layout) -> {
            ClassicsFooter.REFRESH_FOOTER_PULLING = "上拉加载更多";
            ClassicsFooter.REFRESH_FOOTER_RELEASE = "释放立即加载";
            ClassicsFooter.REFRESH_FOOTER_REFRESHING = "正在刷新...";
            ClassicsFooter.REFRESH_FOOTER_LOADING = "正在拼命加载";
            ClassicsFooter.REFRESH_FOOTER_FINISH = "加载成功 ^_^";
            ClassicsFooter.REFRESH_FOOTER_FAILED = "哦豁，加载失败 -_-";
            ClassicsFooter.REFRESH_FOOTER_NOTHING = "啊哦，没有更多数据了";

            layout.setReboundDuration(300);
            layout.setFooterHeight(30);
            layout.setDisableContentWhenLoading(false);
            layout.setEnableAutoLoadMore(SharePrefUtil.isAutoLoadMore(context));
            layout.setEnableLoadMoreWhenContentNotFull(false);
        });

        SmartRefreshLayout.setDefaultRefreshHeaderCreator((context, layout) -> {
            layout.setEnableHeaderTranslationContent(false);
            return new MaterialHeader(context).setColorSchemeResources(
                    android.R.color.holo_blue_bright,
                    android.R.color.holo_green_light,
                    android.R.color.holo_orange_light,
                    android.R.color.holo_red_light);
        });

        SmartRefreshLayout.setDefaultRefreshFooterCreator((context, layout) -> {
            return new ClassicsFooter(context).setDrawableArrowSize(14);
        });
    }

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();

//        EmotionManager.Companion.getINSTANCE().init(context);

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

        Glide.get(this)
                .getRegistry()
                .replace(GlideUrl.class, InputStream.class, new OkHttpUrlLoader.Factory((Call.Factory) get()));
    }

    private OkHttpClient get() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .dns(new OkHttpDns());
        if (SharePrefUtil.isIgnoreSSLVerifier(context)) {
            builder.sslSocketFactory(SSLUtil.getSSLSocketFactory(), SSLUtil.getTrustManager())
                    .hostnameVerifier(SSLUtil.getHostNameVerifier());
        }
        return builder.build();
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

        int mode = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        SharePrefUtil.setNightMode(context, mode == Configuration.UI_MODE_NIGHT_YES);
    }

    public static Context getContext() {
        return context;
    }
}
