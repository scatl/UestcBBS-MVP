package com.scatl.uestcbbs.module.post.view;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.appbar.MaterialToolbar;
import com.jaeger.library.StatusBarUtil;
import com.scatl.uestcbbs.R;
import com.scatl.uestcbbs.base.BaseActivity;
import com.scatl.uestcbbs.base.BasePresenter;
import com.scatl.uestcbbs.util.Constant;
import com.scatl.uestcbbs.util.DownloadUtil;
import com.scatl.uestcbbs.util.RetrofitCookieUtil;

import java.util.HashMap;
import java.util.Map;

import xyz.doikki.videocontroller.StandardVideoController;
import xyz.doikki.videoplayer.player.VideoView;

public class VideoPreviewActivity extends BaseActivity {

    private static final String TAG = "VideoPreviewActivity";

    private Uri mVideoUri;
    private String mVideoName;

    VideoView mVideoView;
    MaterialToolbar toolbar;

    @Override
    protected void getIntent(Intent intent) {
        if (intent != null) {
            String uriStr = getIntent().getStringExtra(Constant.IntentKey.URL);
            mVideoName = getIntent().getStringExtra(Constant.IntentKey.FILE_NAME);
            if (uriStr != null) {
                mVideoUri = Uri.parse(uriStr);
            }
        }
    }

    @Override
    protected int setLayoutResourceId() {
        return R.layout.activity_video_preview;
    }

    @Override
    protected void findView() {
        mVideoView = findViewById(R.id.video_view);
        toolbar = findViewById(R.id.toolbar);
    }

    @Override
    protected void initView() {
        init();
    }

    @Override
    protected void setStatusBar() {
        StatusBarUtil.setColor(this, Color.parseColor("#000000"));
        StatusBarUtil.setDarkMode(this);
    }

    @Override
    protected BasePresenter initPresenter() {
        return null;
    }

    private void init() {
        if (mVideoUri != null) {
            Map<String, String> headers = new HashMap<>();
            headers.put("Cookie", RetrofitCookieUtil.getCookies());
            mVideoView.setUrl(mVideoUri.toString(), headers);
            StandardVideoController controller = new StandardVideoController(this);
            controller.addDefaultControlComponent(mVideoName, false);
            mVideoView.setVideoController(controller); //设置控制器
            mVideoView.start();
        }
    }

    @Override
    protected void onOptionsSelected(MenuItem item) {
        if (item.getItemId() == R.id.download) {
            DownloadUtil.prepareDownload(VideoPreviewActivity.this, mVideoName, mVideoUri.toString());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mVideoView.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mVideoView.pause();
    }

    @Override
    protected void onDestroy() {
        mVideoView.release();
        super.onDestroy();
    }
}