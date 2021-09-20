package com.scatl.uestcbbs.module.post.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.VideoView;

import com.scatl.uestcbbs.MyApplication;
import com.scatl.uestcbbs.R;
import com.scatl.uestcbbs.util.CommonUtil;
import com.scatl.uestcbbs.util.Constant;
import com.scatl.uestcbbs.util.DebugUtil;
import com.scatl.uestcbbs.util.RetrofitCookieUtil;
import com.scatl.uestcbbs.util.SharePrefUtil;
import com.scatl.uestcbbs.util.TimeUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class VideoPreviewActivity extends AppCompatActivity implements View.OnClickListener, View.OnTouchListener,
        SeekBar.OnSeekBarChangeListener, MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener, MediaPlayer.OnErrorListener {

    private static final String TAG = "VideoPreviewActivity";

    private ImageView mPlayOrPauseBtn;
    private ImageView mCloseBtn;
    private ImageView mCenterPlayBtn;
    private SeekBar mSeekBar;
    private TextView mTimestamp;
    private VideoView mVideoView;
    private MediaPlayer mMediaPlayer;
    private View mRootLayout;
    private ProgressHandler mProgressHandler;

//    private PlayerView mPlayerView;
//    private SimpleExoPlayer mPlayer;

    /**
     * 双击事件间隔
     */
    private static final int DOUBLE_CLICK_INTERVAL = 300;

    /**
     * 视频当前播放位置
     */
    private int mPosition;

    /**
     * 经过变换过后的视频宽高
     */
    private int mVideoWidth;
    private int mVideoHeight;

    /**
     * 视频播放地址
     */
    private Uri mVideoUri;

    /**
     * 视频暂停/播放状态
     */
    private boolean mIsPause;

    private String totalTimeStr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_preview);

        findView();
        parseIntent();
        init();
    }

    private void findView() {
        mPlayOrPauseBtn = findViewById(R.id.video_preview_play_or_pause_btn);
        mSeekBar = findViewById(R.id.video_preview_seekbar);
        mCloseBtn = findViewById(R.id.view_preview_back);
        mTimestamp = findViewById(R.id.view_preview_timestamp);
        mVideoView = findViewById(R.id.video_view);
        mCenterPlayBtn = findViewById(R.id.video_preview_center_play_btn);
        mRootLayout = findViewById(R.id.video_preview_root_layout);
    }

    private void parseIntent() {
        Intent intent = getIntent();
        if (intent != null) {
            String uriStr = getIntent().getStringExtra(Constant.IntentKey.URL);
            if (uriStr != null) {
                mVideoUri = Uri.parse(uriStr);
            }
        }
    }

    private void init() {
        mProgressHandler = new ProgressHandler();
        setListener();
        if (mVideoUri != null) {
            Map<String, String> headers = new HashMap<>();
            headers.put("Cookie", RetrofitCookieUtil.getCookies());

            mVideoView.setVideoURI(mVideoUri, headers);
        }

        play(false);
    }

    private void setListener() {
        mPlayOrPauseBtn.setOnClickListener(this);
        mCenterPlayBtn.setOnClickListener(this);
        mCloseBtn.setOnClickListener(this);
        mRootLayout.setOnTouchListener(this);
        mVideoView.setOnPreparedListener(this);
        mVideoView.setOnCompletionListener(this);
        mVideoView.setOnErrorListener(this);
        mSeekBar.setOnSeekBarChangeListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.video_preview_play_or_pause_btn) {
            reversePlayStatus();
        } else if (v.getId() == R.id.view_preview_back) {
            finish();
        } else if (v.getId() == R.id.video_preview_center_play_btn) {
            play(false);
        }
    }

    /**
     * 反转播放状态
     */
    private void reversePlayStatus() {
        if (mVideoView.isPlaying()) {
            pause();
        } else if (mIsPause) {
            play(false);
        }
    }

    /**
     * 播放视频
     */
    private void play(boolean seekTo) {
        if (mVideoView != null) {
            mVideoView.start();
            mIsPause = false;
            mPlayOrPauseBtn.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_pause, getTheme()));
            mCenterPlayBtn.setVisibility(View.GONE);
            if (seekTo) {
                mVideoView.seekTo(mPosition);
            }
        }
    }

    /**
     * 暂停视频
     */
    private void pause() {
        if (mVideoView != null) {
            mPosition = mVideoView.getCurrentPosition();
            mVideoView.pause();
            mIsPause = true;
            mPlayOrPauseBtn.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_play, getTheme()));
            mCenterPlayBtn.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        if (mediaPlayer != null) {
            this.mMediaPlayer = mediaPlayer;
            this.totalTimeStr = TimeUtil.getFormatTime(mVideoView.getDuration());

            resizeVideo();
            mSeekBar.setMax(mVideoView.getDuration());

            //启动定时器，监听视频进度
            mProgressHandler.post(task);
        }
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        mIsPause = true;
        mPosition = mVideoView.getDuration();
        mPlayOrPauseBtn.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_play, getTheme()));
        mCenterPlayBtn.setVisibility(View.VISIBLE);
        mSeekBar.setProgress(mSeekBar.getMax());
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (fromUser) {
            if (mVideoView != null) {
                mPosition = progress;
                mVideoView.seekTo(progress);

                //拖动进度条时，暂停播放，防止画面闪烁
                mVideoView.pause();
            }
        }

        if (mVideoView != null) {
            mTimestamp.setText(String.format("%s/%s", TimeUtil.getFormatTime(mVideoView.getCurrentPosition()), totalTimeStr));
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        if (mVideoView != null) {
            play(false);
        }
    }

    /**
     * 修改 VideoView 的大小,以用来适应屏幕
     */
    private void resizeVideo() {
        mVideoWidth = mMediaPlayer.getVideoWidth();
        mVideoHeight = mMediaPlayer.getVideoHeight();

        int deviceWidth = CommonUtil.screenWidth(this, false);
        int deviceHeight = CommonUtil.screenHeight(this, false);

        //屏幕是竖屏，先按照宽度计算比例
        float wRatio = (float) mVideoWidth / (float) deviceWidth;
        //按照比例计算新的视频高度
        int newHeight = (int) (mVideoHeight / wRatio);
        if (newHeight > deviceHeight) {
            //若新的视频高度大于屏幕高度，按照高度计算比例
            float hRatio = (float) mVideoHeight / (float) deviceHeight;
            mVideoHeight = deviceHeight;
            mVideoWidth /= hRatio;
        } else {
            //否则视频宽度与屏幕保持一致
            mVideoWidth = deviceWidth;
            mVideoHeight = newHeight;
        }

        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(mVideoWidth, mVideoHeight);
        layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        mVideoView.setLayoutParams(layoutParams);
    }

    private class ProgressHandler extends Handler {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (mVideoView.isPlaying()) {
                mSeekBar.setProgress(mVideoView.getCurrentPosition());
            }
        }
    }

    private final Runnable task = new Runnable() {
        @Override
        public void run() {
            Message message = mProgressHandler.obtainMessage();
            mProgressHandler.sendMessage(message);

            mProgressHandler.postDelayed(task, 1000);
        }
    };

    /**
     * 双击，滑动手势
     */
    long currentClickTime, lastClickTime;
    float startX;
    boolean handleSeekTo;
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startX = event.getRawX();
                lastClickTime = currentClickTime;
                currentClickTime = System.currentTimeMillis();
                break;
            case MotionEvent.ACTION_MOVE:
                float distance = event.getRawX() - startX;
                mPosition += distance / CommonUtil.screenWidth(this, false) * mVideoView.getDuration();
                mPosition = mPosition <= 0 ? 0 : Math.min(mPosition, mVideoView.getDuration());
                mSeekBar.setProgress(mPosition);//非人为拖动
                onProgressChanged(mSeekBar, mPosition, true);
                startX = event.getRawX();
                handleSeekTo = true;
                break;
            case MotionEvent.ACTION_UP:
                if (handleSeekTo) {
                    play(true);
                    handleSeekTo = false;
                } else {
                    if (currentClickTime - lastClickTime < DOUBLE_CLICK_INTERVAL) {
                        reversePlayStatus();
                    } else {
                        mRootLayout.performClick();
                    }
                }
                break;
            default:
                break;
        }

        return true;
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        DebugUtil.e(TAG, what+"、" + extra);
        return false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        play(true);
    }

    @Override
    protected void onPause() {
        super.onPause();
        pause();
    }

    @Override
    protected void onDestroy() {
        if (mVideoView != null) {
            mVideoView.stopPlayback();
        }
        if (mProgressHandler != null) {
            mProgressHandler.removeCallbacks(task);
        }
        super.onDestroy();
    }
}