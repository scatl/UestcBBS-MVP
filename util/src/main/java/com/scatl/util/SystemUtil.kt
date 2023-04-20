package com.scatl.util

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationManagerCompat

object SystemUtil {

    @JvmStatic
    fun goToAppDetailSetting(context: Context?) {
        val intent = Intent().apply {
            action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
            data = Uri.parse("package:${context?.packageName}")
            addCategory(Intent.CATEGORY_DEFAULT)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context?.startActivity(intent)
    }

    @JvmStatic
    fun goToAppNotificationSetting(context: Context?) {
        if (Build.VERSION.SDK_INT < 26) {
            return
        }
        val intent = Intent().apply {
            action = Settings.ACTION_APP_NOTIFICATION_SETTINGS
            putExtra(Settings.EXTRA_APP_PACKAGE, context?.packageName)
            putExtra(Settings.EXTRA_CHANNEL_ID, context?.applicationInfo?.uid)
            putExtra("app_package", context?.packageName)
            putExtra("app_uid", context?.applicationInfo?.uid)
        }
        context?.startActivity(intent)
    }

//    @JvmStatic
//    fun isNotificationEnable(context: Context?): Boolean {
//        if (context == null) {
//            return false
//        }
//        return NotificationManagerCompat.from(context).areNotificationsEnabled()
//    }
//
//    @JvmStatic
//    fun checkNotificationPermission(context: Context) {
//        if (Build.VERSION.SDK_INT >= 33) {
//            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_DENIED) {
//                if (!ActivityCompat.shouldShowRequestPermissionRationale(context as Activity, Manifest.permission.POST_NOTIFICATIONS)) {
//                    try {
//                        goToAppNotificationSetting(context)
//                    } catch (e: Exception) {
//                        goToAppDetailSetting(context)
//                    }
//                } else {
//                    ActivityCompat.requestPermissions(context, arrayOf(Manifest.permission.POST_NOTIFICATIONS), 100)
//                }
//            }
//        } else {
//            if (!isNotificationEnable(context)) {
//                try {
//                    goToAppNotificationSetting(context)
//                } catch (e: Exception) {
//                    goToAppDetailSetting(context)
//                }
//            }
//        }
//    }
}