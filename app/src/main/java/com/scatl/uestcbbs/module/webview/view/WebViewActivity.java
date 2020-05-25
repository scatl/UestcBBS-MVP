package com.scatl.uestcbbs.module.webview.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.download.library.DownloadImpl;
import com.download.library.DownloadListener;
import com.download.library.DownloadListenerAdapter;
import com.download.library.DownloadingListener;
import com.download.library.Extra;
import com.download.library.ResourceRequest;
import com.download.library.SimpleDownloadListener;
import com.just.agentweb.AbsAgentWebSettings;
import com.just.agentweb.AgentWeb;
import com.just.agentweb.AgentWebUIControllerImplBase;
import com.just.agentweb.AgentWebView;
import com.just.agentweb.DefaultDownloadImpl;
import com.just.agentweb.IAgentWebSettings;
import com.just.agentweb.WebListenerManager;
import com.scatl.uestcbbs.R;
import com.scatl.uestcbbs.base.BaseActivity;
import com.scatl.uestcbbs.base.BasePresenter;
import com.scatl.uestcbbs.util.Constant;

import java.io.File;

public class WebViewActivity extends BaseActivity {

    private Toolbar toolbar;
    private RelativeLayout webViewContainer;

    private AgentWeb agentWeb;

    private String url;

    @Override
    protected void getIntent(Intent intent) {
        super.getIntent(intent);
        url = intent.getStringExtra(Constant.IntentKey.URL);
    }

    @Override
    protected int setLayoutResourceId() {
        return R.layout.activity_web_view;
    }

    @Override
    protected void findView() {
        toolbar = findViewById(R.id.webview_toolbar);
        webViewContainer = findViewById(R.id.webview_container);
    }

    @Override
    protected void initView() {

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        agentWeb = AgentWeb.with(this)
                .setAgentWebParent(webViewContainer, new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT))
                .useDefaultIndicator(R.color.colorPrimary)
                .setAgentWebWebSettings(getSettings())
                .createAgentWeb()
                .ready()
                .go(url);

//        agentWeb.getWebCreator().getWebView().setWebChromeClient(new WebChromeClient() {
//            @Override
//            public void onReceivedTitle(WebView view, String title) {
//                if (!TextUtils.isEmpty(title)) {
//                    webTitle.setText(title);
//                }
//                //super.onReceivedTitle(view, title);
//            }
//        });

    }

    @Override
    protected BasePresenter initPresenter() {
        return null;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (agentWeb.handleKeyEvent(keyCode, event)) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (agentWeb != null) agentWeb.destroy();
    }


    public IAgentWebSettings getSettings() {
        return new AbsAgentWebSettings() {
            private AgentWeb mAgentWeb;

            @Override
            protected void bindAgentWebSupport(AgentWeb agentWeb) {
                this.mAgentWeb = agentWeb;
            }

            @Override
            public IAgentWebSettings toSetting(WebView webView) {
                return super.toSetting(webView);
            }



            /**
             * AgentWeb 4.0.0 内部删除了 DownloadListener 监听 ，以及相关API ，将 Download 部分完全抽离出来独立一个库，
             * 如果你需要使用 AgentWeb Download 部分 ， 请依赖上 compile 'com.download.library:Downloader:4.1.1' ，
             * 如果你需要监听下载结果，请自定义 AgentWebSetting ， New 出 DefaultDownloadImpl
             * 实现进度或者结果监听，例如下面这个例子，如果你不需要监听进度，或者下载结果，下面 setDownloader 的例子可以忽略。
             * @param webView
             * @param downloadListener
             * @return WebListenerManager
             */
            @Override
            public WebListenerManager setDownloader(WebView webView, android.webkit.DownloadListener downloadListener) {
                return super.setDownloader(webView,
                        new DefaultDownloadImpl(WebViewActivity.this,
                                webView,
                                this.mAgentWeb.getPermissionInterceptor()) {

                            @Override
                            protected ResourceRequest createResourceRequest(String url) {
                                return DownloadImpl.getInstance()
                                        .with(getApplicationContext())
                                        .url(url)
                                        .quickProgress()
                                        .target(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "com.scatl.uestcbbs.fileprovider")
                                        .addHeader("", "")
                                        .setEnableIndicator(true)
                                        .autoOpenIgnoreMD5()
//                                        .closeAutoOpen()
                                        .setRetry(1)
                                        .setUniquePath(false)
                                        .setBlockMaxTime(100000L);
                            }

                            @Override
                            protected void taskEnqueue(ResourceRequest resourceRequest) {
                                resourceRequest.enqueue(new DownloadListenerAdapter() {
                                    @Override
                                    public void onStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength, Extra extra) {
                                        showToast("文件下载中，前往通知栏查看下载进度（请忽略错误页面）");
                                        super.onStart(url, userAgent, contentDisposition, mimetype, contentLength, extra);
                                    }

                                    @Override
                                    public void onProgress(String url, long downloaded, long length, long usedTime) {
                                        super.onProgress(url, downloaded, length, usedTime);
                                    }

                                    @Override
                                    public boolean onResult(Throwable throwable, Uri path, String url, Extra extra) {
                                        showToast("文件下载完成，点击通知可打开文件，或者前往Download文件夹查看文件");
                                        return super.onResult(throwable, path, url, extra);
                                    }
                                });
                            }
                        });
            }
        };
    }
}
