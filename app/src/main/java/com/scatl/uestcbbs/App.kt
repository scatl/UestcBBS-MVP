package com.scatl.uestcbbs

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.content.res.Configuration
import android.graphics.Typeface
import android.os.Build
import androidx.appcompat.app.AppCompatDelegate
import com.bumptech.glide.Glide
import com.bumptech.glide.load.model.GlideUrl
import com.google.android.material.color.DynamicColors
import com.just.agentweb.AgentWebConfig
import com.scatl.uestcbbs.api.ApiConstant
import com.scatl.uestcbbs.http.OkHttpUrlLoader
import com.scatl.uestcbbs.manager.BlackListManager
import com.scatl.uestcbbs.manager.DayQuestionManager
import com.scatl.uestcbbs.manager.ForumListManager
import com.scatl.uestcbbs.util.Constant
import com.scatl.uestcbbs.manager.EmotionManager
import com.scatl.uestcbbs.util.SharePrefUtil
import com.scatl.util.OkHttpDns
import com.scatl.util.SSLUtil
import com.scatl.widget.download.DownLoadUtil
import com.scatl.widget.glide.progress.GlideProgressInterceptor
import com.scwang.smart.refresh.footer.ClassicsFooter
import com.scwang.smart.refresh.header.MaterialHeader
import com.scwang.smart.refresh.layout.SmartRefreshLayout
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.tencent.bugly.crashreport.CrashReport
import es.dmoral.toasty.Toasty
import io.reactivex.plugins.RxJavaPlugins
import okhttp3.Call
import okhttp3.OkHttpClient
import org.litepal.LitePal
import xyz.doikki.videoplayer.exo.ExoMediaPlayerFactory
import xyz.doikki.videoplayer.player.VideoViewConfig
import xyz.doikki.videoplayer.player.VideoViewManager
import java.io.InputStream
import kotlin.concurrent.thread

/**
 * Created by sca_tl at 2023/2/27 19:22
 */
class App: Application() {

    companion object {
        @SuppressLint("StaticFieldLeak")
        @JvmStatic
        lateinit var mContext: Context

        @JvmStatic
        fun getContext() = mContext
    }

    init {
        SmartRefreshLayout.setDefaultRefreshInitializer { context: Context?, layout: RefreshLayout ->
            ClassicsFooter.REFRESH_FOOTER_PULLING = "上拉加载更多"
            ClassicsFooter.REFRESH_FOOTER_RELEASE = "释放立即加载"
            ClassicsFooter.REFRESH_FOOTER_REFRESHING = "正在刷新..."
            ClassicsFooter.REFRESH_FOOTER_LOADING = "正在拼命加载"
            ClassicsFooter.REFRESH_FOOTER_FINISH = "加载成功😀"
            ClassicsFooter.REFRESH_FOOTER_FAILED = "哦豁，加载失败🙁"
            ClassicsFooter.REFRESH_FOOTER_NOTHING = "啊哦，没有更多数据了"
            layout.setReboundDuration(300)
            layout.setFooterHeight(30f)
            layout.setDisableContentWhenLoading(false)
            layout.setEnableAutoLoadMore(true)
            layout.setEnableLoadMoreWhenContentNotFull(false)
        }

        SmartRefreshLayout.setDefaultRefreshHeaderCreator { context: Context?, layout: RefreshLayout ->
            layout.setEnableHeaderTranslationContent(false)
            MaterialHeader(context).setColorSchemeResources(
                android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light
            )
        }

        SmartRefreshLayout.setDefaultRefreshFooterCreator { context: Context?, layout: RefreshLayout? ->
            ClassicsFooter(context).setDrawableArrowSize(14f)
        }
    }

    override fun onCreate() {
        super.onCreate()

        mContext = applicationContext

        VideoViewManager.setConfig(
            VideoViewConfig
                .newBuilder()
                .setPlayerFactory(ExoMediaPlayerFactory.create())
                .build()
        )

        if (SharePrefUtil.isThemeFollowWallpaper(mContext) && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            DynamicColors.applyToActivitiesIfAvailable(this)
        }

        CrashReport.initCrashReport(applicationContext, Constant.BUGLY_ID, false)

        LitePal.initialize(this)

        setUiMode()

        DownLoadUtil.isDownloadFolderUriAccessible(mContext)

        RxJavaPlugins.setErrorHandler { throwable: Throwable? -> }

        Toasty
            .Config
            .getInstance()
            .setToastTypeface(Typeface.DEFAULT)
            .setTextSize(14)
            .supportDarkTheme(true)
            .apply()

        if (SharePrefUtil.isLogin(mContext)) {
            if (SharePrefUtil.isSuperLogin(this, SharePrefUtil.getName(mContext))) {
                for (s in SharePrefUtil.getCookies(this, SharePrefUtil.getName(mContext))) {
                    AgentWebConfig.syncCookie(ApiConstant.BBS_BASE_URL, s)
                }
            }
        }

        BlackListManager.INSTANCE.init()
        ForumListManager.INSTANCE.init()
        DayQuestionManager.init(mContext)
        thread { EmotionManager.INSTANCE.init(mContext) }
    }

    private fun setUiMode() {
        if (SharePrefUtil.isUiModeFollowSystem(applicationContext)) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        } else {
            if (SharePrefUtil.isNightMode(applicationContext)) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }
        val mode = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        SharePrefUtil.setNightMode(mContext, mode == Configuration.UI_MODE_NIGHT_YES)
    }

}