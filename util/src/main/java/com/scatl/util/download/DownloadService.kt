package com.scatl.util.download

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.content.FileProvider
import androidx.documentfile.provider.DocumentFile
import com.scatl.util.R
import com.scatl.util.common.FileUtil
import com.scatl.util.common.OkHttpDns
import com.scatl.util.common.SSLUtil
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.File
import java.io.IOException
import java.net.URLDecoder
import java.util.concurrent.TimeUnit


/**
 * Created by sca_tl at 2023/2/28 15:58
 */
class DownloadService : Service() {

    companion object {
        const val CHANNEL_NAME = "文件下载通知"
        const val CHANNEL_ID = "download_notification"
        const val GROUP_ID = 1000
        const val GROUP_KEY = "download_group"
    }

    private val notificationManager by lazy {
        (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).apply {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                createNotificationChannel(
                    NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT)
                )
            }
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val url = intent?.getStringExtra("url") ?: ""
        val fileName = intent?.getStringExtra("name") ?: "downloadFile"
        val cookies = intent?.getStringExtra("cookies") ?: ""
        val notifyId = System.currentTimeMillis().toInt()
        val `when` = System.currentTimeMillis()

        startDownload(`when`, notifyId, url, fileName, cookies)
        sendNotification(`when`, notifyId,"准备下载...",0, true, fileName)

        return START_STICKY
    }

    private fun startDownload(`when`: Long, notifyId: Int, url: String, fileName: String, cookie: String) {
        val clientBuilder = OkHttpClient.Builder()
            .dns(OkHttpDns())
            .addInterceptor {
                val builder = it.request().newBuilder()
                builder.addHeader("Cookie", cookie)
                it.proceed(builder.build())
            }
            .connectTimeout(30, TimeUnit.SECONDS)

        clientBuilder
            .sslSocketFactory(SSLUtil.getSSLSocketFactory(), SSLUtil.getTrustManager())
            .hostnameVerifier(SSLUtil.getHostNameVerifier())

        val request: Request = Request.Builder()
            .get()
            .url(url)
            .build()
        clientBuilder
            .build()
            .newCall(request)
            .enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    sendNotification(
                        `when` = `when`,
                        notifyId = notifyId,
                        content = "下载失败：" + e.message,
                        progress = 0,
                        indeterminate = false,
                        fileName = fileName
                    )
                }

                override fun onResponse(call: Call, response: Response) {
                    val contentLength = response.body?.contentLength()?:1
                    val inputStream = response.body?.byteStream()

                    val file = DocumentFile
                        .fromTreeUri(this@DownloadService, Uri.parse(DownLoadUtil.getDownloadFolderUri(this@DownloadService)))
                        ?.createFile("", fileName)
                    val outputStream = file?.uri?.let { contentResolver.openOutputStream(it) }

                    val bytes = ByteArray(10240)
                    var len: Int = 0

                    while (inputStream?.read(bytes)?.also { len = it } != -1) {
                        outputStream?.write(bytes, 0, len)
                        val length = file?.length() ?: 0
                        val progress = (length * 100 / contentLength).toInt()

                        sendNotification(
                            `when` = `when`,
                            notifyId = notifyId,
                            content = "$progress%, ${(FileUtil.formatFileSize(contentLength))}",
                            progress = progress,
                            fileName = fileName
                        )
                    }
                }
            })
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    private fun sendNotification(`when`: Long,
                                 notifyId: Int,
                                 content: String?,
                                 progress: Int,
                                 indeterminate: Boolean = false,
                                 fileName: String = "") {

        var pendingIntent: PendingIntent? = null

        if (progress >= 100) {
            Handler(Looper.getMainLooper()).post {
                Toast.makeText(this, "文件下载成功：${fileName}", Toast.LENGTH_SHORT).show()
            }
            //DocumentFile分享不需要FileProvider
            val documentFile = DownLoadUtil.getExistFile(this, fileName)
            val intent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(documentFile?.uri, documentFile?.type)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        }

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setWhen(`when`)
            .setSmallIcon(R.drawable.ic_notification_icon1)
            .setAutoCancel(false)
            .setContentTitle((if (progress >= 100) "下载完成：" else "下载中：").plus(fileName))
            .setGroup(GROUP_KEY)
            .setContentIntent(pendingIntent)
            .setStyle(NotificationCompat.BigTextStyle().bigText(content))
            .setProgress(100, progress, indeterminate)
            .build()
        notificationManager.notify(notifyId, notification)
        notificationManager.notify(GROUP_ID, buildGroup(`when`))
    }

    private fun buildGroup(`when`: Long) =
        NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("下载通知")
            .setWhen(`when`)
            .setSmallIcon(R.drawable.ic_notification_icon1)
            .setShowWhen(true)
            .setGroup(GROUP_KEY)
            .setGroupSummary(true)
            .build()

}