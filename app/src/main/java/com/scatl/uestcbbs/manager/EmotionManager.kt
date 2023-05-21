package com.scatl.uestcbbs.manager

import android.content.Context

/**
 * Created by sca_tl on 2022/12/6 20:47
 */
class EmotionManager {

    private val emotions = mutableMapOf<String, List<EmotionItem>>()

    companion object {
        val INSTANCE by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
            EmotionManager()
        }
    }

    fun init(context: Context?) {
        for (i in 0..8) {
            val emotionItems = mutableListOf<EmotionItem>()
            context?.assets?.list("emotion/" + (i + 1))?.let {
                for (j in it.indices) {
                    val aPath = "file:///android_asset/emotion/" + (i + 1) + "/" + it[j]
                    val rPath = "emotion/" + (i + 1) + "/" + it[j]
                    emotionItems.add(EmotionItem(it[j], aPath, rPath))
                }
            }
            emotions[(i + 1).toString()] = emotionItems
        }
    }

    fun getEmotionByName(name: String?): EmotionItem? {
        if (name.isNullOrEmpty()) {
            return null
        }
        emotions.forEach {
            it.value.forEach { item ->
                if (item.name == name) {
                    return item
                }
            }
        }
        return null
    }

    fun getLocalPath(url: String): String {
        val index = url.lastIndexOf("/")
        if (index != -1) {
            val tmp = "_" + url.substring(index + 1, url.length).replace(".gif", "].gif")
            emotions.forEach {
                it.value.forEach { item ->
                    if (item.name.contains(tmp)) {
                        return "file:///android_asset/emotion/" + (it.key) + "/" + item.name
                    }
                }
            }
        }
        return ""
    }

    data class EmotionItem(
        var name: String,
        var aPath: String,
        var rPath: String
    )
}