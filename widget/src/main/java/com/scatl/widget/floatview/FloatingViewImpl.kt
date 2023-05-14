package com.scatl.widget.floatview

import android.app.Activity
import android.app.Application
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.view.ViewCompat
import com.scatl.widget.R
import java.lang.ref.WeakReference

/**
 * created by sca_tl at 2023/5/14 12:15
 */
class FloatingViewImpl(manger: FloatViewManager) : IFloatingView, FloatLifecycle.Callback {

    private var mApplication: Application? = null
    private var customFloatingView: CustomFloatingView? = null
    private var mFilterActivities: Array<Class<out Activity>>?

    private var mContainer: WeakReference<FrameLayout?>? = null
    private var isShow = false

    init {
        mApplication = manger.mContext
        mFilterActivities = manger.mFilterActivities
        customFloatingView = CustomFloatingView(manger.mContext!!, manger.mLayoutId, manger.mLayoutParam)
        customFloatingView?.setFloatViewListener(manger.mListener)
        manger.mFloatLifecycle?.listener(this)
    }

    override fun show() {
        isShow = true
        attach(getContainer())
    }

    override fun hide() {
        if (isShow) {
            isShow = false
            detach(getContainer())
        }
    }

    override fun activityAttach(activity: Activity?) {
        if (!isFilterActivity(activity)) {
            attach(getActivityRoot(activity))
        }
    }

    override fun activityDetach(activity: Activity?) {
        if (!isFilterActivity(activity)) {
            detach(getActivityRoot(activity))
        }
    }

    private fun isFilterActivity(activity: Activity?): Boolean {
        if (mFilterActivities == null) {
            return false
        }
        for (mFilterActivity in mFilterActivities!!) {
            if (mFilterActivity.name == activity?.javaClass?.name) {
                return true
            }
        }
        return false
    }

    private fun addViewToWindow() {
        if (getContainer() == null) {
            return
        }
        if (customFloatingView?.parent != null) {
            (customFloatingView?.parent as? ViewGroup?)?.removeView(customFloatingView)
        }
        if (isShow && !ViewCompat.isAttachedToWindow(customFloatingView as View)) {
            getContainer()?.addView(customFloatingView)
        }
    }

    private fun getContainer() = mContainer?.get()

    private fun getActivityRoot(activity: Activity?) =
        activity?.window?.decorView?.findViewById<View>(android.R.id.content) as? FrameLayout?

    private fun attach(container: FrameLayout?) {
        if (getContainer() != null) {
            mContainer?.clear()
        }
        mContainer = WeakReference(container)
        addViewToWindow()
    }

    private fun detach(container: FrameLayout?) {
        if (container != null && customFloatingView?.parent == container) {
            container.removeView(customFloatingView)
        }
        if (getContainer() != null && getContainer() == container) {
            mContainer?.clear()
            mContainer = null
        }
    }
}