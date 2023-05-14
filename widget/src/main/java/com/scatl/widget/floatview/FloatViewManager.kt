package com.scatl.widget.floatview

import android.app.Activity
import android.app.Application
import android.view.ViewGroup

/**
 * created by sca_tl at 2023/5/14 12:07
 */
class FloatViewManager {

    var mContext: Application? = null
    var mFloatLifecycle: FloatLifecycle? = null
    var mLayoutId = 0
    var mLayoutParam: ViewGroup.LayoutParams? = null
    var mFilterActivities: Array<Class<out Activity>>? = null
    var mFloatingView: IFloatingView? = null
    var mListener: FloatViewListener? = null

    companion object {
        val INSTANCE by lazy(LazyThreadSafetyMode.SYNCHRONIZED) { FloatViewManager() }
    }

    fun get(): IFloatingView? {
        return mFloatingView
    }

    fun with(application: Application) = apply {
        mContext = application
        mFloatLifecycle = FloatLifecycle().bind(mContext)
    }

    fun setLayoutId(layoutId: Int) = apply {
        mLayoutId = layoutId
    }

    fun setLayoutParam(layoutParam: ViewGroup.LayoutParams?) = apply {
        mLayoutParam = layoutParam
    }

    fun setFilter(activities: Array<Class<out Activity>>) = apply {
        mFilterActivities = activities
    }

    fun listener(listener: FloatViewListener?) = apply {
        this.mListener = listener
    }

    fun build() {
        mFloatingView = FloatingViewImpl(this)
    }

}