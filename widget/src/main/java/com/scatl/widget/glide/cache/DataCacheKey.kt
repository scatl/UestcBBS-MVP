package com.scatl.widget.glide.cache

import com.bumptech.glide.load.Key
import java.security.MessageDigest

/**
 * Created by sca_tl at 2023/5/16 14:18
 */
class DataCacheKey(val sourceKey: Key, private val signature: Key) : Key {

    override fun equals(other: Any?): Boolean {
        if (other is DataCacheKey) {
            return sourceKey == other.sourceKey && signature == other.signature
        }
        return false
    }

    override fun hashCode(): Int {
        var result = sourceKey.hashCode()
        result = 31 * result + signature.hashCode()
        return result
    }

    override fun toString(): String {
        return "DataCacheKey{sourceKey=$sourceKey, signature=$signature}"
    }

    override fun updateDiskCacheKey(messageDigest: MessageDigest) {
        sourceKey.updateDiskCacheKey(messageDigest)
        signature.updateDiskCacheKey(messageDigest)
    }
}