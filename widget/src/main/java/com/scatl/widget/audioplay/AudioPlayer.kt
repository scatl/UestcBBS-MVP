package com.scatl.widget.audioplay

import android.media.MediaPlayer
import java.io.IOException

/**
 * Created by sca_tl at 2023/5/21 10:34
 */
class AudioPlayer {

    var mCurrentUrl: String? = ""
        private set
    private var pause = false
    var prepared = false
        private set
    private var mMediaPlayer: MediaPlayer? = null

    companion object {
        val INSTANCE by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
            AudioPlayer()
        }
    }

    fun isPlaying(url: String?) = url == mCurrentUrl && mMediaPlayer?.isPlaying == true

    fun isPlaying() = mMediaPlayer?.isPlaying == true

    fun isStopped() = mMediaPlayer == null

    fun playOrPause(url: String?) {
        if (url.isNullOrBlank()) {
            return
        }
        if (url == mCurrentUrl) {
            pause = if (pause) {
                mMediaPlayer?.start()
                false
            } else {
                mMediaPlayer?.pause()
                true
            }
            return
        }
        stopPlay()
        mCurrentUrl = url
        if (mMediaPlayer == null) {
            mMediaPlayer = MediaPlayer()
        }
        try {
            mMediaPlayer?.apply {
                reset()
                setDataSource(url)
                prepareAsync()
                setOnPreparedListener { mp ->
                    mp.start()
                    prepared = true
                }
                setOnCompletionListener {
                    stopPlay()
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    fun stopPlay() {
        mCurrentUrl = ""
        prepared = false
        try {
            mMediaPlayer?.stop()
            mMediaPlayer?.release()
            mMediaPlayer = null
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun getCurrentPosition(): Int {
        return if (!prepared) 0 else mMediaPlayer?.currentPosition ?:0
    }

    fun getDuration(): Int {
        return if (!prepared) 0 else mMediaPlayer?.duration ?:0
    }

}