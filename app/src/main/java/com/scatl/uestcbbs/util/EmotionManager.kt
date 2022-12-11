package com.scatl.uestcbbs.util

import android.content.Context
import android.widget.GridView
import com.scatl.uestcbbs.custom.emoticon.EmoticonGridViewAdapter

/**
 * Created by tanlei02 on 2022/12/6 20:47
 */
class EmotionManager {

    val emotions = mutableMapOf<String, List<EmotionItem>>()

    companion object {
        val INSTANCE by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
            EmotionManager()
        }
    }

    fun init(context: Context) {
        for (i in 0..7) {
            val emotionItems = mutableListOf<EmotionItem>()
            context.assets.list("emotion/" + (i + 1))?.let {
                for (j in it.indices) {
                    val aPath = "file:///android_asset/emotion/" + (i + 1) + "/" + it[j]
                    val rPath = i.toString()
                    emotionItems.add(EmotionItem(it[j], aPath, rPath))
                }
            }
            emotions[(i + 1).toString()] = emotionItems
        }
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