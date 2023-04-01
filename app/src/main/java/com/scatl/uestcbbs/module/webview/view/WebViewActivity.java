package com.scatl.uestcbbs.module.webview.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.RelativeLayout;

import com.just.agentweb.AgentWeb;
import com.scatl.uestcbbs.R;
import com.scatl.uestcbbs.base.BaseActivity;
import com.scatl.uestcbbs.base.BasePresenter;
import com.scatl.uestcbbs.util.ColorUtil;
import com.scatl.uestcbbs.util.Constant;
import com.scatl.uestcbbs.util.SharePrefUtil;
import com.scatl.util.common.TheftProofMark;

public class WebViewActivity extends BaseActivity {

    private RelativeLayout webViewContainer;
    private AgentWeb agentWeb;
    private String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            TheftProofMark
                    .getInstance()
                    .setTextColor(getColor(R.color.theft_proof_color))
                    .show(this, "UID:" + SharePrefUtil.getUid(this));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

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
        webViewContainer = findViewById(R.id.webview_container);
    }

    @Override
    protected void initView() {
        super.initView();
        agentWeb = AgentWeb.with(this)
                .setAgentWebParent(webViewContainer, new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT))
                .useDefaultIndicator(ColorUtil.getAttrColor(this, R.attr.colorPrimary))
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

    @Override
    protected boolean setTheftProof() {
        return true;
    }
}
