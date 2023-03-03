package com.scatl.util.download

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

/**
 * Created by sca_tl at 2023/2/28 14:21
 */
class DownloadManager private constructor(val context: Context) {

    private var mUrl: String? = ""
    private var mName: String? = ""
    private var mCookies: String? = ""
//    private lateinit var mContext: Context

    companion object {
//        val INSTANCE by lazy(LazyThreadSafetyMode.SYNCHRONIZED) { DownloadManager() }
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

    fun start() {
        if (mUrl.isNullOrEmpty()) {
            Toast.makeText(context, "链接无效", Toast.LENGTH_SHORT).show()
            return
        }
        context.startActivity(
            Intent(context, DownloadActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
                putExtra("name", mName)
                putExtra("url", mUrl)
                putExtra("cookies", mCookies)
            }
        )
    }

}