package com.scatl.uestcbbs.services

import android.app.*
import android.content.Intent
import android.net.Uri
import android.os.*
import androidx.documentfile.provider.DocumentFile
import com.scatl.uestcbbs.annotation.ToastType
import com.scatl.uestcbbs.helper.ExceptionHelper
import com.scatl.uestcbbs.helper.rxhelper.Observer
import com.scatl.uestcbbs.util.*
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import okhttp3.ResponseBody

/**
 * author: sca_tl
 * date: 2021/12/5 13:00
 * description:
 */

class DownloadService : Service() {

    companion object {
        const val SERVICE_NAME = "com.scatl.uestcbbs.services.DownloadService"
        const val CHANNEL_NAME = "文件下载通知"
        const val NOTIFICATION_ID = 11111
    }

    lateinit var url: String
    lateinit var fileName: String

    var mProgress = 0

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        url = intent?.getStringExtra(Constant.IntentKey.URL) ?: ""
        fileName = intent?.getStringExtra(Constant.IntentKey.FILE_NAME) ?: "downloadFile"

        startDownload()

        return START_STICKY
    }

    private fun startDownload() {
        RetrofitCookieUtil
                .getInstance()
                .apiService
                .downloadFile(url)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(object: Observer<ResponseBody>() {

                    override fun onNext(t: ResponseBody) {

                        val contentLength = t.contentLength()
                        val inputStream = t.byteStream()

                        val file = DocumentFile
                                .fromTreeUri(this@DownloadService, Uri.parse(SharePrefUtil.getDownloadFolderUri(this@DownloadService)))
                                ?.createFile("", fileName)
                        val outputStream = file?.uri?.let { contentResolver.openOutputStream(it) }

                        val bytes = ByteArray(4096)
                        var len: Int

                        while (inputStream.read(bytes).also { len = it } != -1) {
                            outputStream?.write(bytes, 0, len)
                            val length = file?.length() ?: 0
                            val progress = (length * 100 / contentLength).toInt()

                            if (mProgress != progress) {
                                DownloadUtil.sendNotification(this@DownloadService,
                                        "$NOTIFICATION_ID",
                                        fileName,
                                        "$progress%, ${FileUtil.formatDirectorySize(contentLength)}",
                                        progress)
                            }
                            mProgress = progress
                        }
                    }

                    override fun OnSuccess(t: ResponseBody?) {}

                    override fun onError(e: ExceptionHelper.ResponseThrowable?) {
                        DownloadUtil.sendNotification(this@DownloadService,
                                "$NOTIFICATION_ID",
                                fileName,
                                "下载失败",
                                0)
                    }

                    override fun OnCompleted() {
                        DownloadUtil.sendNotification(this@DownloadService,
                                "$NOTIFICATION_ID",
                                fileName,
                                "下载成功，点击打开",
                                100)

                        Looper.prepare()
                        ToastUtil.showToast(this@DownloadService, "${fileName}下载成功！", ToastType.TYPE_SUCCESS)
                        Looper.loop()
                    }

                    override fun OnDisposable(d: Disposable) {
                    }

                })
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}
