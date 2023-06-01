package com.scatl.uestcbbs

import android.content.Context
import com.bumptech.glide.Glide
import com.bumptech.glide.GlideBuilder
import com.bumptech.glide.Registry
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.load.engine.cache.InternalCacheDiskCacheFactory
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.module.AppGlideModule
import com.scatl.uestcbbs.http.OkHttpUrlLoader
import com.scatl.uestcbbs.util.SharePrefUtil
import com.scatl.util.OkHttpDns
import com.scatl.util.SSLUtil
import com.scatl.widget.glide.progress.GlideProgressInterceptor
import okhttp3.Call
import okhttp3.OkHttpClient
import java.io.InputStream

/**
 * Created by sca_tl at 2023/5/26 9:18
 */
@GlideModule
class GlideModule: AppGlideModule() {

    companion object {
        const val DEFAULT_DISK_CACHE_SIZE = 1024 * 1024 * 1024L
    }

    override fun applyOptions(context: Context, builder: GlideBuilder) {
        builder.setDiskCache(InternalCacheDiskCacheFactory(context, DEFAULT_DISK_CACHE_SIZE))
        super.applyOptions(context, builder)
    }

    override fun registerComponents(context: Context, glide: Glide, registry: Registry) {
        super.registerComponents(context, glide, registry)
        registry.replace(GlideUrl::class.java, InputStream::class.java, OkHttpUrlLoader.Factory(getOkhttpClient()))
    }

    override fun isManifestParsingEnabled() = false

    private fun getOkhttpClient(): Call.Factory {
        val builder = OkHttpClient.Builder().dns(OkHttpDns())
        builder.addInterceptor(GlideProgressInterceptor())
        if (SharePrefUtil.isIgnoreSSLVerifier(App.mContext)) {
            builder
                .sslSocketFactory(SSLUtil.getSSLSocketFactory(), SSLUtil.getTrustManager())
                .hostnameVerifier(SSLUtil.getHostNameVerifier())
        }
        return builder.build()
    }
}