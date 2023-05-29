package com.scatl.uestcbbs.util

import android.app.Activity
import android.app.Service
import android.content.Context
import android.widget.ImageView
import androidx.annotation.DrawableRes
import androidx.annotation.RawRes
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.scatl.uestcbbs.annotation.ToastType
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
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

fun Service?.showToast(msg: String, @ToastType type: String?) {
    ToastUtil.showToast(this, msg, type)
}

fun Context?.showToast(msg: String?, @ToastType type: String?) {
    ToastUtil.showToast(this, msg, type)
}

fun Fragment?.showToast(msg: String?, @ToastType type: String?) {
    ToastUtil.showToast(this?.context, msg, type)
}

fun ImageView.load(url: String?) {
    Glide.with(context).load(url).into(this)
}

fun ImageView.load(@RawRes @DrawableRes resId: Int) {
    Glide.with(context).load(resId).into(this)
}

fun <T:Any> Observable<T>.subscribeEx(observer: Observer<T>) {
    this.subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(observer)
}