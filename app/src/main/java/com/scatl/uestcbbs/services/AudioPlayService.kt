package com.scatl.uestcbbs.services

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.scatl.uestcbbs.R
import com.scatl.uestcbbs.receivers.AudioPlayReceiver
import com.scatl.uestcbbs.util.AudioPlayerUtil
import com.scatl.util.TimeUtil
import kotlin.concurrent.thread

class AudioPlayService : Service() {

    private var iAmGroot = true
    private lateinit var notificationManager: NotificationManager

    companion object {
        const val CHANNEL_NAME = "音频播放后台服务"
        const val CHANNEL_ID = 3000
    }

    override fun onCreate() {
        super.onCreate()
        notificationManager = (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).apply {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val mChannel = NotificationChannel(CHANNEL_ID.toString(), CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT)
                createNotificationChannel(mChannel)
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notification = createNotification("音频播放中：00.00/00.00")
            startForeground(CHANNEL_ID, notification)
        }
    }

    private fun createNotification(title: String, progress: Int = 0): Notification {
        val notificationBuilder =
            NotificationCompat.Builder(this, CHANNEL_ID.toString())
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setContentTitle(title)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.ic_notification_icon1)
                .setProgress(10000, progress, false)

        val actionStop = NotificationCompat.Action.Builder(0, "停止", getIntent("stop")).build()
        notificationBuilder.addAction(actionStop)

        val actionPause = NotificationCompat.Action.Builder(0, "暂停", getIntent("pause")).build()
        notificationBuilder.addAction(actionPause)

        return notificationBuilder.build()
    }

    private fun getIntent(action: String): PendingIntent {
        val intent = Intent(this, AudioPlayReceiver::class.java).apply {
            setAction(action)
        }
        return PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_MUTABLE)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val url = intent?.getStringExtra("url") ?: ""

        if (url.isBlank()) {
            stopSelf()
        } else {
            AudioPlayerUtil.getAudioPlayer().playOrPause(url)
            thread {
                while (iAmGroot) {
                    try {
                        val duration = AudioPlayerUtil.getAudioPlayer().duration.toLong()
                        val p = AudioPlayerUtil.getAudioPlayer().currentPosition

                        val notification = createNotification(
                            "音频播放中：${TimeUtil.formatMsToMinutes(p)}/${TimeUtil.formatMsToMinutes(duration)}",
                        ((p.toFloat() / duration.toFloat()) * 10000f).toInt())
                        notificationManager.notify(CHANNEL_ID, notification)
                        Thread.sleep(1000)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        }

        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        iAmGroot = false
        notificationManager.cancel(CHANNEL_ID)
    }

    override fun onBind(intent: Intent?) = null

}