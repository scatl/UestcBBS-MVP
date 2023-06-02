package com.scatl.util

import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2

/**
 * Created by sca_tl at 2023/5/26 15:21
 */
fun ViewPager2.desensitize() {
    try {
        val recyclerViewField = ViewPager2::class.java.getDeclaredField("mRecyclerView")
        recyclerViewField.isAccessible = true

        val touchSlopField = RecyclerView::class.java.getDeclaredField("mTouchSlop")
        touchSlopField.isAccessible = true

        val recyclerView = recyclerViewField.get(this) as RecyclerView
        val touchSlop = touchSlopField.get(recyclerView) as Int
        touchSlopField.set(recyclerView, touchSlop * 2)
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

fun Context?.startServiceCompat(intent: Intent) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        this?.startForegroundService(intent)
    } else {
        this?.startService(intent)
    }
}