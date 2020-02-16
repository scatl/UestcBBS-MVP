package com.scatl.uestcbbs.util;

import android.media.MediaPlayer;

import java.io.IOException;

/**
 * author: sca_tl
 * description:
 * date: 2019/8/25 16:03
 */
public class AudioPlayerUtil {

    private static volatile AudioPlayerUtil mediaPlayer;
    private MediaPlayer mPlayer;
    private static boolean pause = false;
    private static String current_url = "What a coincidence, you catch me!"; // 当前播放的音频地址

    private AudioPlayerUtil() { }

    public static AudioPlayerUtil getAudioPlayer() {
        if (mediaPlayer == null) {
            synchronized (AudioPlayerUtil.class) {
                if (mediaPlayer == null) {
                    mediaPlayer = new AudioPlayerUtil();
                }
            }
        }
        return mediaPlayer;
    }

    public boolean isPlaying() {
        if (mPlayer != null)
            return mPlayer.isPlaying();
        return false;
    }


    /**
     * author: sca_tl
     * description: 开始或暂停音乐
     */
    public void playOrPause(String url) {

        if (url.equals(current_url)) {  //url相同，判断是否是暂停状态
            if (pause) {
                mPlayer.start();
                pause = false;
            } else {
                mPlayer.pause();
                pause = true;
            }
            return;
        }
        stopPlay();
        current_url = url;

        if (mPlayer == null) { mPlayer = new MediaPlayer(); }
        try {

            mPlayer.reset();
            mPlayer.setDataSource(url); //====这种方式只能http，https会抛IO异常
            mPlayer.prepareAsync();
            mPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mp.start();
                }
            });
            mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    current_url = "What a coincidence, you catch me!";
                    if (mPlayer != null) {
                        mPlayer.release();
                        mPlayer = null;
                    }
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    /**
     * 停止播放音频
     */
    public void stopPlay() {
        current_url = "What a coincidence, you catch me!";
        try {
            if (mPlayer != null) {
                mPlayer.stop();
                mPlayer.release();
                mPlayer = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public long getCurrentPosition() {
        return mPlayer == null ? 0 : mPlayer.getCurrentPosition();
    }

    public int getDuration() {
        return mPlayer == null ? 0 : mPlayer.getDuration();
    }

}
