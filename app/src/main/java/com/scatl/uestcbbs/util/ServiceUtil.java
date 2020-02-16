package com.scatl.uestcbbs.util;

import android.app.ActivityManager;
import android.content.Context;

import java.util.ArrayList;

/**
 * author: sca_tl
 * description:
 * date: 2019/8/13 17:18
 */
public class ServiceUtil {

    public static boolean isServiceRunning(Context context, String serviceName) {
        if (serviceName == null) return false;
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ArrayList<ActivityManager.RunningServiceInfo> runningServiceInfos = (ArrayList<ActivityManager.RunningServiceInfo>) activityManager.getRunningServices(20);
        for (int i = 0; i < runningServiceInfos.size(); i ++) {
            if (runningServiceInfos.get(i).service.getClassName().equals(serviceName)) {
                return true;
            }
        }
        return false;
    }
}
