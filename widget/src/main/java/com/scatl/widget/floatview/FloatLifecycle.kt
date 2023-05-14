package com.scatl.widget.floatview

import android.app.Activity
import android.app.Application
import android.os.Bundle

/**
 * created by sca_tl at 2023/5/14 12:12
 */
class FloatLifecycle: Application.ActivityLifecycleCallbacks {

    private var mCallback: Callback? = null

    fun bind(context: Application?): FloatLifecycle {
        context?.registerActivityLifecycleCallbacks(this)
        return this
    }

    fun listener(callback: Callback?) {
        mCallback = callback
    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) { }

    override fun onActivityStarted(activity: Activity) {}

    override fun onActivityResumed(activity: Activity) {
        mCallback?.activityAttach(activity)
    }

    override fun onActivityPaused(activity: Activity) {
        mCallback?.activityDetach(activity)
    }

    override fun onActivityStopped(activity: Activity) { }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) { }

    override fun onActivityDestroyed(activity: Activity) { }

    interface Callback {
        fun activityAttach(activity: Activity?)
        fun activityDetach(activity: Activity?)
    }
}