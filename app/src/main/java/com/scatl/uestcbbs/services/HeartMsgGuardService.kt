package com.scatl.uestcbbs.services

import android.app.Service
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.widget.Toast
import com.scatl.uestcbbs.IHeartMsgInterface
import com.scatl.uestcbbs.util.DebugUtil
import com.scatl.util.startServiceCompat

class HeartMsgGuardService : Service() {

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        bindService(Intent(this, HeartMsgService::class.java), connection, Context.BIND_IMPORTANT)
        return START_STICKY
    }

    override fun onBind(intent: Intent?) = Binder()

    class Binder: IHeartMsgInterface.Stub() {

        override fun getServiceName(): String {
            return HeartMsgGuardService::class.java.name
        }

    }

    private val connection: ServiceConnection = object : ServiceConnection {

        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {

        }

        override fun onServiceDisconnected(name: ComponentName) {
            startServiceCompat(Intent(this@HeartMsgGuardService, HeartMsgService::class.java))
            bindService(Intent(this@HeartMsgGuardService, HeartMsgService::class.java), this, Context.BIND_IMPORTANT)
        }
    }
}