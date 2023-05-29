package com.scatl.widget.glide.cache

import android.content.Context
import com.bumptech.glide.disklrucache.DiskLruCache
import com.bumptech.glide.load.engine.cache.DiskCache
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.signature.EmptySignature
import java.io.File

/**
 * Created by sca_tl at 2023/5/16 14:17
 */
object GlideUtil {

    @JvmStatic
    fun getCacheFile(context: Context?, url: String?): File? {
        if (context == null) {
            return null
        }
        try {
            val dataCacheKey = DataCacheKey(GlideUrl(url), EmptySignature.obtain())
            val safeKeyGenerator = SafeKeyGenerator()
            val safeKey = safeKeyGenerator.getSafeKey(dataCacheKey)
            val file = File(context.cacheDir, DiskCache.Factory.DEFAULT_DISK_CACHE_DIR)
            val diskLruCache = DiskLruCache.open(file, 1, 1, DiskCache.Factory.DEFAULT_DISK_CACHE_SIZE.toLong())
            val value = diskLruCache[safeKey]
            return value?.getFile(0)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

}