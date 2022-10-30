package com.scatl.uestcbbs.util

import android.app.Service
import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.scatl.uestcbbs.annotation.ToastType
import es.dmoral.toasty.Toasty
import java.lang.reflect.Field

/**
 * created by sca_tl at 2022/9/27 18:58
 */

fun Collection<*>?.isNullOrEmpty(): Boolean {
    return this == null || this.isEmpty()
}

fun Collection<*>?.isNotNullAndEmpty(): Boolean {
    return !this.isNullOrEmpty()
}

fun Service.showToast(msg: String, @ToastType type: String?) {
    ToastUtil.showToast(this, msg, type)
}

fun ViewPager2.desensitize() {
    try {
        val recyclerViewField: Field = ViewPager2::class.java.getDeclaredField("mRecyclerView")
        recyclerViewField.isAccessible = true

        val touchSlopField: Field = RecyclerView::class.java.getDeclaredField("mTouchSlop")
        touchSlopField.isAccessible = true

        val recyclerView = recyclerViewField.get(this) as RecyclerView
        val touchSlop = touchSlopField.get(recyclerView) as Int
        touchSlopField.set(recyclerView, touchSlop * 2)
    } catch (ignore: java.lang.Exception) { }
}