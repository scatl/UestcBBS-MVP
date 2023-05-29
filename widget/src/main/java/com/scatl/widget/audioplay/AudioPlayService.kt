package com.scatl.widget.audioplay

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.scatl.util.TimeUtil
import com.scatl.widget.R
import kotlin.concurrent.thread

class AudioPlayService : Service() {

    private var iAmGroot = true
    private var mWhen = 0L
    private lateinit var notificationManager: NotificationManager

    companion object {
        const val CHANNEL_NAME = "音频播放后台服务"
        const val CHANNEL_ID = 3000
    }

    override fun onCreate() {
        super.onCreate()
        mWhen = System.currentTimeMillis()
        notificationManager = (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).apply {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val mChannel = NotificationChannel(CHANNEL_ID.toString(), CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT)
                createNotificationChannel(mChannel)
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notification = createNotification("音频准备中，请稍候...", prepared = false)
            startForeground(CHANNEL_ID, notification)
        }
    }

    private fun createNotification(title: String, progress: Int = 0, prepared: Boolean): Notification {
        val notificationBuilder =
            NotificationCompat.Builder(this, CHANNEL_ID.toString())
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setContentTitle(title)
                .setWhen(mWhen)
                .setOngoing(true)
                .setSmallIcon(R.drawable.ic_notification_icon1)
                .setProgress(10000, progress, false)

        val actionStop = NotificationCompat.Action.Builder(0, "停止", getIntent("stop")).build()
        notificationBuilder.addAction(actionStop)

        if (prepared) {
            if (AudioPlayer.INSTANCE.isPlaying()) {
                val actionPause = NotificationCompat.Action.Builder(0, "暂停", getIntent("pause")).build()
                notificationBuilder.addAction(actionPause)
            } else {
                val actionPause = NotificationCompat.Action.Builder(0, "继续", getIntent("pause")).build()
                notificationBuilder.addAction(actionPause)
            }
        }

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
            AudioPlayer.INSTANCE.playOrPause(url)
            startLoop()
        }

        return START_STICKY
    }

    private fun startLoop() {
        thread {
            while (iAmGroot) {
                try {
                    val duration = AudioPlayer.INSTANCE.getDuration().toLong()
                    val currentPosition = AudioPlayer.INSTANCE.getCurrentPosition().toLong()

                    val title = if (AudioPlayer.INSTANCE.prepared) {
                        "音频播放中：${TimeUtil.formatMsToMinutes(currentPosition)}/${TimeUtil.formatMsToMinutes(duration)}"
                    } else {
                        "音频准备中，请稍候..."
                    }
                    val progress = ((currentPosition.toFloat() / duration.toFloat()) * 10000f).toInt()
                    val notification = createNotification(title, progress, AudioPlayer.INSTANCE.prepared)
                    notificationManager.notify(CHANNEL_ID, notification)

                    if (AudioPlayer.INSTANCE.isStopped()) {
                        stopSelf()
                    }
                    Thread.sleep(1000)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        iAmGroot = false
        notificationManager.cancel(CHANNEL_ID)
    }

    override fun onBind(intent: Intent?) = null

}