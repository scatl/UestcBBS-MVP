package com.scatl.widget.audioplay

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class AudioPlayReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        when(intent.action) {
            "stop" -> {
                AudioPlayer.INSTANCE.stopPlay()
                context.stopService(Intent(context, AudioPlayService::class.java))
            }
            "pause" -> {
                AudioPlayer.INSTANCE.playOrPause(AudioPlayer.INSTANCE.mCurrentUrl)
            }
        }
    }
}