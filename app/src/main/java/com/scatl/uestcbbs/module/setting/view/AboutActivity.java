package com.scatl.uestcbbs.module.setting.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

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

    @Override
    protected void onClickListener(View view) {

    }
}
