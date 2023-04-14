package com.scatl.widget.video

import android.content.Context
import android.content.Intent
import android.widget.Toast

/**
 * Created by sca_tl at 2023/3/2 16:13
 */
class VideoPreViewManager {

    private var mUrl: String? = ""
    private var mName: String? = ""
    private var mCookies: String? = ""
    private lateinit var mContext: Context

    companion object {
        val INSTANCE by lazy(LazyThreadSafetyMode.SYNCHRONIZED) { VideoPreViewManager() }
    }

    fun with(context: Context) = apply {
        mContext = context.applicationContext
    }

    fun setUrl(url: String?) = apply {
        mUrl = url
    }

    fun setName(name: String?) = apply {
        mName = name
    }

    fun setCookies(cookies: String?) = apply {
        mCookies = cookies
    }

    fun start() {
        if (mUrl.isNullOrEmpty()) {
            Toast.makeText(mContext, "链接无效", Toast.LENGTH_SHORT).show()
            return
        }
        mContext.startActivity(
            Intent(mContext, VideoPreviewActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
                putExtra("name", mName)
                putExtra("url", mUrl)
                putExtra("cookies", mCookies)
            }
        )
    }

}