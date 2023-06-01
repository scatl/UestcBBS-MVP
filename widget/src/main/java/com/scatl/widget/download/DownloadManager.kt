package com.scatl.widget.download

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.widget.Toast

/**
 * Created by sca_tl at 2023/2/28 14:21
 */
class DownloadManager private constructor(val context: Context) {

    private var mUrl: String? = ""
    private var mName: String? = ""
    private var mCookies: String? = ""
    private var mTitle: String? = "下载文件"
//    private lateinit var mContext: Context

    companion object {
        //        val INSTANCE by lazy(LazyThreadSafetyMode.SYNCHRONIZED) { DownloadManager() }

        const val DOWNLOAD_FOLDER = "download"

        fun with(context: Context): DownloadManager {
            return DownloadManager(context)
        }
    }

//    fun with(context: Context) = apply {
//        mContext = context.applicationContext
//    }

    fun setUrl(url: String?) = apply {
        mUrl = url
    }

    fun setName(name: String?) = apply {
        mName = name
    }

    fun setCookies(cookies: String?) = apply {
        mCookies = cookies
    }

    fun setTitle(title: String?) = apply {
        mTitle = title
    }

    /**
     * 直接下载
     * 1、已经申请了下载目录权限，直接下载到该目录里
     * 2、否则下载到外部私有目录里
     */
    fun startDirectly() {
        if (mUrl.isNullOrEmpty()) {
            Toast.makeText(context, "下载链接无效", Toast.LENGTH_SHORT).show()
            return
        }

        val intent = Intent(context, DownloadService().javaClass).apply {
            putExtra("url", mUrl)
            putExtra("name", mName)
            putExtra("cookies", mCookies)
        }
//        SystemUtil.checkNotificationPermission(context)
        (context as? Activity)?.startService(intent)
    }

    /**
     * 展示确认下载界面，没有权限的话就申请权限
     * 通过SAF选择文件夹使用DocumentFile方式下载
     */
    fun start() {
        if (mUrl.isNullOrEmpty()) {
            Toast.makeText(context, "下载链接无效", Toast.LENGTH_SHORT).show()
            return
        }
        context.startActivity(
            Intent(context, DownloadActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
                putExtra("title", mTitle)
                putExtra("name", mName)
                putExtra("url", mUrl)
                putExtra("cookies", mCookies)
            }
        )
    }

}