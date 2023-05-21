package com.scatl.uestcbbs.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.scatl.uestcbbs.services.AudioPlayService
import com.scatl.uestcbbs.util.AudioPlayerUtil

class AudioPlayReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        when(intent.action) {
            "stop" -> {
                AudioPlayerUtil.getAudioPlayer().stopPlay()
                context.stopService(Intent(context, AudioPlayService::class.java))
            }
            "pause" -> {
                AudioPlayerUtil.getAudioPlayer().playOrPause(AudioPlayerUtil.getAudioPlayer().currentUrl)
            }
        }
    }
}