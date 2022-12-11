package com.scatl.uestcbbs.module.setting.view;

import androidx.appcompat.widget.Toolbar;

import com.jaeger.library.StatusBarUtil;
import com.scatl.uestcbbs.R;
import com.scatl.uestcbbs.base.BaseActivity;
import com.scatl.uestcbbs.base.BasePresenter;
import com.scatl.uestcbbs.module.setting.presenter.SettingsPresenter;

public class SettingsActivity extends BaseActivity {

    private Toolbar toolbar;

    @Override
    protected int setLayoutResourceId() {
        return R.layout.activity_settings;
    }

    @Override
    protected void findView() {
        toolbar = findViewById(R.id.toolbar);
    }

    @Override
    protected void initView() {
        super.initView();
        SettingsFragment settingsFragment = new SettingsFragment();
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.settings_framelayout, settingsFragment)
                .commit();
    }

    @Override
    protected BasePresenter initPresenter() {
        return new SettingsPresenter();
    }

    @Override
    protected void setStatusBar() {
        StatusBarUtil.setTranslucent(this);
    }

}
