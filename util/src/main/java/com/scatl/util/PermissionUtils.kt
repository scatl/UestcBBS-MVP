package com.scatl.util

import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat

/**
 * created by sca_tl at 2023/6/14 20:55
 */
object PermissionUtils {

    /**
     * 判断是否拥有指定权限
     */
    fun checkSelfPermission(context: Context, vararg permissions: String): Boolean {
        return permissions.all {
            ActivityCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
        }
    }

    /**
     * 检查应用是否声明了指定权限
     */
    fun containsPermission(context: Context, permission: String): Boolean {
        try {
            val packageManager: PackageManager = context.packageManager
            val packageInfo = packageManager.getPackageInfo(
                context.packageName,
                PackageManager.GET_PERMISSIONS
            )
            val permissions = packageInfo.requestedPermissions
            if (!permissions.isNullOrEmpty()) {
                return permissions.contains(permission)
            }
        } catch (e: Throwable) {
            e.printStackTrace()
        }
        return false
    }

}