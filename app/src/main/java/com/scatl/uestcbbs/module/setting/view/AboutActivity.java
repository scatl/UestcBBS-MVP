package com.scatl.uestcbbs.module.setting.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.scatl.uestcbbs.R;
import com.scatl.uestcbbs.base.BaseActivity;
import com.scatl.uestcbbs.base.BasePresenter;
import com.scatl.uestcbbs.custom.imageview.RoundImageView;
import com.scatl.uestcbbs.util.CommonUtil;

public class AboutActivity extends BaseActivity {

    private TextView version;
    private Toolbar toolbar;
    private ImageView appIcon;

    @Override
    protected int setLayoutResourceId() {
        return R.layout.activity_about;
    }

    @Override
    protected void findView() {
        version = findViewById(R.id.about_app_version);
        toolbar = findViewById(R.id.about_toolbar);
        appIcon = findViewById(R.id.about_app_icon);
    }

    @Override
    protected void initView() {

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        version.setText(CommonUtil.getVersionName(this));
        appIcon.setOnClickListener(this::onClickListener);

        AboutFragment aboutFragment = new AboutFragment();
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.about_framelayout, aboutFragment)
                .commit();

    }

    @Override
    protected BasePresenter initPresenter() {
        return null;
    }

    final static int COUNTS = 7;// 点击次数
    final static long DURATION = 1000;// 规定有效时间
    long[] mHits = new long[COUNTS];
    @Override
    protected void onClickListener(View view) {
        if (view.getId() == R.id.about_app_icon) {
            System.arraycopy(mHits, 1, mHits, 0, mHits.length - 1);
            //为数组最后一位赋值
            mHits[mHits.length - 1] = SystemClock.uptimeMillis();
            if (mHits[0] >= (SystemClock.uptimeMillis() - DURATION)) {
                mHits = new long[COUNTS];//重新初始化数组
                startActivity(new Intent(this, AdvanceSettingsActivity.class));
            }
        }
    }
}
