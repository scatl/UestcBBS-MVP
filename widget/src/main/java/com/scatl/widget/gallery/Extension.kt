package com.scatl.widget.gallery

import android.net.Uri
import android.widget.ImageView
import androidx.annotation.DrawableRes
import androidx.annotation.RawRes
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import java.lang.reflect.Field

/**
 * Created by sca_tl at 2023/4/20 11:44
 */
fun ImageView.load(url: String) {
    Glide.with(context).load(url).into(this)
}

fun ImageView.load(@RawRes @DrawableRes resId: Int) {
    Glide.with(context).load(resId).into(this)
}

fun ImageView.load(uri: Uri?) {
    Glide.with(context).load(uri).into(this)
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