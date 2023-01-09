package com.scatl.uestcbbs.util

import android.app.*
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.documentfile.provider.DocumentFile
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.scatl.uestcbbs.App
import com.scatl.uestcbbs.R
import com.scatl.uestcbbs.annotation.ToastType
import com.scatl.uestcbbs.services.DownloadService
import com.scatl.uestcbbs.base.BaseActivity
import com.scatl.uestcbbs.base.BaseEvent
import com.scatl.uestcbbs.base.BaseVBActivity
import com.scatl.uestcbbs.module.post.view.VideoPreviewActivity
import org.greenrobot.eventbus.EventBus
import java.net.URLDecoder


/**
 * author: sca_tl
 * date: 2021/12/11 11:50
 * description:
 */
object DownloadUtil {

    @JvmStatic
    fun prepareDownload(context: Context?, fileName: String?, fileUrl: String?) {
        if (context == null) {
            ToastUtil.showToast(App.getContext(), "context is null", ToastType.TYPE_ERROR)
            return
        }
        if (fileUrl == null) {
            ToastUtil.showToast(App.getContext(), "url is null", ToastType.TYPE_ERROR)
            return
        }
        if (ServiceUtil.isServiceRunning(context, DownloadService.SERVICE_NAME)) {
            ToastUtil.showToast(App.getContext(), "请等待当前文件下载完成", ToastType.TYPE_ERROR)
            return
        }

        val name = fileName?: "downloadFile"

        if (!CommonUtil.isDownloadPermissionAccessible(context)) {
            if (context is BaseActivity<*> || context is BaseVBActivity<*, *, *>) {
                val dialog: AlertDialog = MaterialAlertDialogBuilder(context)
                    .setPositiveButton("好的", null)
                    .setNegativeButton("取消", null)
                    .setTitle("设置下载目录")
                    .setMessage(context.getString(R.string.get_download_permission))
                    .create()
                dialog.setOnShowListener {
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE).apply {
                            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION
                                    or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                                    or Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION)
                        }
                        SharePrefUtil.setDownloadFileName(context, name)
                        SharePrefUtil.setDownloadFileUrl(context, fileUrl)
                        (context as AppCompatActivity).startActivityForResult(intent, Constant.RequestCode.REQUEST_DOWNLOAD_PERMISSION)
                        dialog.dismiss()
                    }
                }
                dialog.show()
            }
        } else {
            val folder = URLDecoder.decode(SharePrefUtil.getDownloadFolderUri(context), "UTF-8").replace("content://com.android.externalstorage.documents/tree/primary:", "")
            var existFile: DocumentFile? = null
            val message = DocumentFile
                .fromTreeUri(context, Uri.parse(SharePrefUtil.getDownloadFolderUri(context)))
                ?.listFiles()
                ?.find { it.name == name }
                ?.let {
                    existFile = it
                    context.getString(R.string.download_need_overwrite_file, name, folder)
                }
                ?: context.getString(R.string.download_file, name, folder)

            val dialog: AlertDialog = MaterialAlertDialogBuilder(context)
                .setPositiveButton(if (existFile != null) "覆盖下载" else "下载", null)
                .setNegativeButton("取消", null)
                .setTitle("下载文件")
                .setMessage(message)
                .create()
            dialog.setOnShowListener {
                dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                    .setOnClickListener {
                        existFile?.delete()
                        startDownload(context, fileUrl, name)
                        dialog.dismiss()
                    }
            }
            dialog.show()
        }

    }

    @JvmStatic
    fun startDownload(context: Context, url: String, name: String) {

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(DownloadService.NOTIFICATION_ID)

        ToastUtil.showToast(context, "文件后台下载中...", ToastType.TYPE_NORMAL)
        val intent = Intent(context, DownloadService().javaClass).apply {
            putExtra(Constant.IntentKey.URL, url)
            putExtra(Constant.IntentKey.FILE_NAME, name)
        }

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            context.startForegroundService(intent)
//        } else {
        context.startService(intent)
//        }

    }

    @JvmStatic
    fun sendNotification(context: Context,
                         id: String,
                         title: String,
                         content: String,
                         progress: Int) {

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        var pendingIntent: PendingIntent? = null

        if (progress == 100) {
            val uri = DocumentFile
                .fromTreeUri(context, Uri.parse(SharePrefUtil.getDownloadFolderUri(context)))
                ?.listFiles()
                ?.find { it.name == title }
                ?.uri

            val intent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(uri, FileUtils.getMimeType(context, uri))
            }

            pendingIntent = PendingIntent.getActivity(context, 111, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

            EventBus.getDefault().post(BaseEvent<Int>(BaseEvent.EventCode.DOWNLOAD_FILE_COMPLETED))
        }

        val notification = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(id, DownloadService.CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT)
            notificationManager.createNotificationChannel(channel)
            Notification.Builder(context, id)
                .setContentTitle(title)
                .setContentText(content)
                .setGroupSummary(true)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.ic_notification_icon1)
                .setAutoCancel(false)
                .setContentIntent(pendingIntent)
                .setProgress(100, progress, false)
                .build()
        } else {
            NotificationCompat.Builder(context, id)
                .setContentTitle(title)
                .setContentText(content)
                .setGroupSummary(true)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.ic_notification_icon1)
                .setAutoCancel(false)
                .setContentIntent(pendingIntent)
                .setProgress(100, progress, false)
                .build()
        }

        notificationManager.notify(id.toInt(), notification)
    }
}