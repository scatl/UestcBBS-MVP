package com.scatl.widget.dialog.notifydialog

import android.app.Activity
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Looper
import android.provider.Settings
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationManagerCompat
import com.scatl.widget.iamgeviewer.ImageViewer
import java.lang.ref.WeakReference
import java.util.concurrent.atomic.AtomicInteger

/**
 * Created by sca_tl at 2023/5/18 9:35
 */
class NotificationControlManager {

    private var currentActivityWeakRef: WeakReference<Activity>? = null
    private var currentActivityIsActive:Boolean = false
    private var dialog: NotificationDialog? = null

    companion object {
        val INSTANCE: NotificationControlManager by lazy ( mode = LazyThreadSafetyMode.SYNCHRONIZED ) {
            NotificationControlManager()
        }
    }

    fun getCurrentActivity() = currentActivityWeakRef?.get()

    fun setCurrentActivity(activity: Activity) {
        currentActivityWeakRef = WeakReference(activity)
    }

    fun setActive(isActive:Boolean){
        currentActivityIsActive = isActive
    }
    
    fun showNotificationDialog(title: String,
                               content: String,
                               filterActivity: Array<Class<out Activity>>? = null,
                               listener: OnNotificationCallback? = null) {
        if (getCurrentActivity() == null || !currentActivityIsActive) {
            return
        }
        //判断是否需要过滤页面不显示Dialog
        filterActivity?.forEach {
            if (it.name == getCurrentActivity()?.javaClass?.name) {
                return
            }
        }

        dialog = NotificationDialog(getCurrentActivity()!!, title, content)
        if (Thread.currentThread() != Looper.getMainLooper().thread) {
            getCurrentActivity()?.runOnUiThread {
                showDialog(dialog, listener)
            }
        } else {
            showDialog(dialog, listener)
        }
    }

    private fun showDialog(dialog: NotificationDialog?, listener: OnNotificationCallback?) {
        dialog?.showDialogAutoDismiss()
        dialog?.setOnNotificationClickListener(object : NotificationDialog.OnNotificationClick {
            override fun onClick() {
                listener?.onCallback()
            }
        })
    }

    fun dismissDialog() {
        if (dialog?.isShowing == true) {
            dialog?.dismiss()
        }
    }

    interface OnNotificationCallback {
        fun onCallback()
    }

}

