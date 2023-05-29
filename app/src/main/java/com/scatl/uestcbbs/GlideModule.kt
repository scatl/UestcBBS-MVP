package com.scatl.uestcbbs

import android.content.Context
import com.bumptech.glide.GlideBuilder
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.load.engine.cache.InternalCacheDiskCacheFactory
import com.bumptech.glide.module.AppGlideModule

/**
 * Created by sca_tl at 2023/5/26 9:18
 */
@GlideModule
class GlideModule: AppGlideModule() {

    companion object {
        const val DEFAULT_DISK_CACHE_SIZE = 500 * 1024 * 1024L
    }

    override fun applyOptions(context: Context, builder: GlideBuilder) {
        builder.setDiskCache(InternalCacheDiskCacheFactory(context, DEFAULT_DISK_CACHE_SIZE))
        super.applyOptions(context, builder)
    }

    override fun isManifestParsingEnabled() = false
}