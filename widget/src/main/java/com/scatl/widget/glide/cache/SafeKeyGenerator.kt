package com.scatl.widget.glide.cache

import com.bumptech.glide.load.Key
import com.bumptech.glide.util.LruCache
import com.bumptech.glide.util.Util
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

class SafeKeyGenerator {

    private val loadIdToSafeHash = LruCache<Key, String>(1000)

    fun getSafeKey(key: Key): String? {
        var safeKey: String?
        synchronized(loadIdToSafeHash) { safeKey = loadIdToSafeHash[key] }
        if (safeKey == null) {
            try {
                val messageDigest = MessageDigest.getInstance("SHA-256")
                key.updateDiskCacheKey(messageDigest)
                safeKey = Util.sha256BytesToHex(messageDigest.digest())
            } catch (e: NoSuchAlgorithmException) {
                e.printStackTrace()
            } catch (e: Exception) {
                e.printStackTrace()
            }
            synchronized(loadIdToSafeHash) { loadIdToSafeHash.put(key, safeKey) }
        }
        return safeKey
    }
}