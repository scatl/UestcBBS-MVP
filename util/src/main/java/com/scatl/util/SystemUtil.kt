package com.scatl.util

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.text.TextUtils
import androidx.core.content.FileProvider
import java.io.File

object SystemUtil {

    @JvmStatic
    fun getVersionCode(context: Context): Int {
        try {
            return context.packageManager.getPackageInfo(context.packageName, 0).versionCode
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        return 0
    }

    @JvmStatic
    fun getVersionName(context: Context): String? {
        try {
            return context.packageManager.getPackageInfo(context.packageName, 0).versionName
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        return ""
    }

    @JvmStatic
    fun installApk(context: Context?, apkFile: File?) {
        if (context == null || apkFile == null) {
            return
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            val apkUri = FileProvider.getUriForFile(context, "com.scatl.uestcbbs.fileprovider", apkFile)
            val intent = Intent(Intent.ACTION_VIEW).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                setDataAndType(apkUri, "application/vnd.android.package-archive")
            }
            context.startActivity(intent)
        } else {
            val intent = Intent(Intent.ACTION_VIEW).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
                setDataAndType(Uri.fromFile(apkFile), "application/vnd.android.package-archive")
            }
            context.startActivity(intent)
        }
    }

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

    fun goToAppNotificationChannelSetting(context: Context?, channelId: String) {
        if (Build.VERSION.SDK_INT < 26) {
            return
        }
        val intent = Intent().apply {
            action = Settings.ACTION_CHANNEL_NOTIFICATION_SETTINGS
            putExtra(Settings.EXTRA_APP_PACKAGE, context?.packageName)
            putExtra(Settings.EXTRA_CHANNEL_ID, channelId)
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

    @JvmStatic
    fun isHarmonyOs(): Boolean {
        return try {
            val buildExClass = Class.forName("com.huawei.system.BuildEx")
            val osBrand = buildExClass.getMethod("getOsBrand").invoke(buildExClass)
            osBrand?.toString()?.contains("harmony", ignoreCase = true)?: false
        } catch (e: Throwable) {
            false
        }
    }

    @JvmStatic
    fun getHarmonyVersionCode(): Int {
        return getProp("hw_sc.build.os.apiversion", "0")?.toInt() ?: 0
    }

    @JvmStatic
    private fun getProp(property: String, defaultValue: String): String? {
        try {
            val spClz = Class.forName("android.os.SystemProperties")
            val method = spClz.getDeclaredMethod("get", String::class.java)
            val value = method.invoke(spClz, property) as String
            return if (TextUtils.isEmpty(value)) {
                defaultValue
            } else value
        } catch (e: Throwable) {
            e.printStackTrace()
        }
        return defaultValue
    }
}