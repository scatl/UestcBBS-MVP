package com.scatl.widget.dialog.notifydialog

import android.app.Activity
import android.app.Application
import android.os.Bundle

/**
 * Created by sca_tl at 2023/5/18 9:34
 */
class AppLifecycleCallback: Application.ActivityLifecycleCallbacks {

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        NotificationControlManager.INSTANCE.setCurrentActivity(activity)
    }

    override fun onActivityStarted(activity: Activity) {
    }

    override fun onActivityResumed(activity: Activity) {
        NotificationControlManager.INSTANCE.setCurrentActivity(activity)
        NotificationControlManager.INSTANCE.setActive(true)
    }

    override fun onActivityPaused(activity: Activity) {
        NotificationControlManager.INSTANCE.setActive(false)
    }

    override fun onActivityStopped(activity: Activity) {
    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
    }

    override fun onActivityDestroyed(activity: Activity) {
        NotificationControlManager.INSTANCE.dismissDialog()
    }
}
