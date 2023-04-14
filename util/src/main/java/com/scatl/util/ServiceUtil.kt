package com.scatl.util

import android.app.ActivityManager
import android.content.Context

/**
 * created by sca_tl at 2023/3/18 20:24
 */
object ServiceUtil {

    @JvmStatic
    fun isServiceRunning(context: Context, serviceName: String?): Boolean {
        if (serviceName == null) {
            return false
        }
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val runningServiceInfos = activityManager.getRunningServices(20) as ArrayList<ActivityManager.RunningServiceInfo>
        for (i in runningServiceInfos.indices) {
            if (runningServiceInfos[i].service.className == serviceName) {
                return true
            }
        }
        return false
    }

}