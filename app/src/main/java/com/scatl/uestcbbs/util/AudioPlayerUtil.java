package com.scatl.uestcbbs.util;

import android.media.MediaPlayer;
import android.net.Uri;

import com.scatl.uestcbbs.App;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * author: sca_tl
 * description:
 * date: 2019/8/25 16:03
 */
public class AudioPlayerUtil {

    private static volatile AudioPlayerUtil mediaPlayer;
    private MediaPlayer mPlayer;
    private static boolean pause = false;
    private static boolean prepared = false;
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

    public String getCurrentUrl() {
        return current_url;
    }

    /**
     * author: sca_tl
     * description: 开始或暂停音乐
     */
    public void playOrPause(String url) {

        if (url.equals(current_url) && mPlayer != null) {  //url相同，判断是否是暂停状态
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
            Map<String, String> header = new HashMap<>();
            header.put("Cookie", RetrofitUtil.getCookies());
            mPlayer.reset();
            mPlayer.setDataSource(App.getContext(), Uri.parse(url), header); //====这种方式只能http，https会抛IO异常
            mPlayer.prepareAsync();
            mPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    prepared = true;
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
        prepared = false;
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
        return mPlayer == null || !prepared ? 0 : mPlayer.getCurrentPosition();
    }

    public int getDuration() {
        return mPlayer == null || !prepared ? 0 : mPlayer.getDuration();
    }

}
