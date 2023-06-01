package com.scatl.widget.download

import android.app.Service
import android.content.Intent
import android.os.IBinder

/**
 * Created by sca_tl at 2023/2/28 15:58
 */
class DownloadService : Service() {

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        DownloadTask
            .get(this)
            .setUrl(intent?.getStringExtra("url"))
            .setFileName(intent?.getStringExtra("name"))
            .setCookies(intent?.getStringExtra("cookies"))
            .start()
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

}