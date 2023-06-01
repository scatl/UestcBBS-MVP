package com.scatl.widget.download

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.content.FileProvider
import androidx.documentfile.provider.DocumentFile
import com.scatl.util.FilePathUtil
import com.scatl.util.FileUtil
import com.scatl.util.OkHttpDns
import com.scatl.util.SSLUtil
import com.scatl.widget.R
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.concurrent.TimeUnit

/**
 * Created by sca_tl at 2023/5/31 15:56
 */
class DownloadTask(val context: Context) {

    companion object {
        const val CHANNEL_NAME = "文件下载通知"
        const val CHANNEL_ID = "download_notification"
        const val GROUP_ID = 1000
        const val GROUP_KEY = "download_group"

        fun get(context: Context): DownloadTask {
            return DownloadTask(context)
        }
    }

    private val notificationManager by lazy {
        (context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).apply {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                createNotificationChannel(
                    NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT)
                )
            }
        }
    }

    private val time by lazy { System.currentTimeMillis() }
    private val notifyId by lazy { System.currentTimeMillis().toInt() }

    var mTaskTag: String = ""
        private set
    private var mUrl: String = ""
    private var mFileName: String = ""
    private var mCookies: String = ""
    var mProgress = 0
        private set

    fun setUrl(url: String?) = apply {
        mUrl = url?:""
    }

    fun setFileName(name: String?) = apply {
        mFileName = name?:"downloadFile"
    }

    fun setCookies(cookies: String?) = apply {
        mCookies = cookies?:""
    }

    fun ready(): String {
        mTaskTag = System.currentTimeMillis().toString()
        return mTaskTag
    }

    fun start() {
        if (mUrl.isEmpty()) {
            Toast.makeText(context, "下载链接为空", Toast.LENGTH_SHORT).show()
        }

        Toast.makeText(context, "文件后台下载中...", Toast.LENGTH_SHORT).show()
        sendNotification("准备下载...",0, true)

        val clientBuilder = OkHttpClient.Builder()
            .dns(OkHttpDns())
            .sslSocketFactory(SSLUtil.getSSLSocketFactory(), SSLUtil.getTrustManager())
            .hostnameVerifier(SSLUtil.getHostNameVerifier())
            .connectTimeout(30, TimeUnit.SECONDS)
            .addInterceptor {
                val builder = it.request().newBuilder().apply {
                    addHeader("Cookie", mCookies)
                }
                it.proceed(builder.build())
            }

        val request: Request = Request.Builder().get().url(mUrl).build()
        clientBuilder
            .build()
            .newCall(request)
            .enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    sendNotification(
                        content = "下载失败：" + e.message,
                        progress = 0,
                        indeterminate = false,
                    )
                }

                override fun onResponse(call: Call, response: Response) {
                    val contentLength = response.body?.contentLength()?:1
                    val inputStream = response.body?.byteStream()

                    val outputStream = if (DownLoadUtil.isDownloadFolderUriAccessible(context)) {
                        DownLoadUtil.getExistFile(context, mFileName)?.delete()
                        val file = DocumentFile
                            .fromTreeUri(context, Uri.parse(DownLoadUtil.getDownloadFolderUri(context)))
                            ?.createFile(FilePathUtil.getMimeType(mFileName), mFileName)
                        file?.uri?.let { context.contentResolver.openOutputStream(it) }
                    } else {
                        val file = File(context.getExternalFilesDir(DownloadManager.DOWNLOAD_FOLDER), mFileName)
                        FileOutputStream(file)
                    }

                    outputStream?.use {
                        inputStream?.use { input ->
                            val copiedLength = input.copyTo(it, 10 * 1024)

                            val progress = (copiedLength * 100 / contentLength).toInt()
                            mProgress = progress

                            sendNotification(
                                content = "$progress%, ${(FileUtil.formatFileSize(contentLength))}",
                                progress = progress
                            )
                        }
                    }
                }
            })
    }

    private fun sendNotification(content: String?,
                                 progress: Int,
                                 indeterminate: Boolean = false) {

        var pendingIntent: PendingIntent? = null

        if (progress >= 100) {
            Handler(Looper.getMainLooper()).post {
                Toast.makeText(context, "文件下载成功：${mFileName}", Toast.LENGTH_SHORT).show()
            }

            pendingIntent = PendingIntent.getActivity(context, 0, getViewIntent(),
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        }

        val notification = NotificationCompat
            .Builder(context, CHANNEL_ID)
            .setWhen(time)
            .setSmallIcon(R.drawable.ic_notification_icon1)
            .setAutoCancel(false)
            .setContentTitle((if (progress >= 100) "下载完成：" else "下载中：").plus(mFileName))
            .setGroup(GROUP_KEY)
            .setContentIntent(pendingIntent)
            .setStyle(NotificationCompat.BigTextStyle().bigText(content))
            .setProgress(100, progress, indeterminate)
            .build()
        notificationManager.notify(notifyId, notification)
        notificationManager.notify(GROUP_ID, buildGroup(time))
    }

    private fun buildGroup(time: Long) =
        NotificationCompat
            .Builder(context, CHANNEL_ID)
            .setContentTitle("下载通知")
            .setWhen(time)
            .setSmallIcon(R.drawable.ic_notification_icon1)
            .setShowWhen(true)
            .setGroup(GROUP_KEY)
            .setGroupSummary(true)
            .build()

    private fun getViewIntent(): Intent {
        return if (DownLoadUtil.isDownloadFolderUriAccessible(context)) {
            //DocumentFile分享不需要FileProvider
            val documentFile = DownLoadUtil.getExistFile(context, mFileName)
            Intent(Intent.ACTION_VIEW).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
                setDataAndType(documentFile?.uri, documentFile?.type)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
        } else {
            val file = File(context.getExternalFilesDir(DownloadManager.DOWNLOAD_FOLDER), mFileName)
            val intentUri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                FileProvider.getUriForFile(context, "com.scatl.widget.download.downloadFileProvider", file)
            } else {
                Uri.fromFile(file)
            }

            Intent(Intent.ACTION_VIEW).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
                setDataAndType(intentUri, FilePathUtil.getMimeType(file))
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
        }
    }

}