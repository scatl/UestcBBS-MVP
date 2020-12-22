package com.scatl.uestcbbs.module.setting.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;

import com.scatl.uestcbbs.R;
import com.scatl.uestcbbs.base.BaseActivity;
import com.scatl.uestcbbs.base.BasePresenter;
import com.scatl.uestcbbs.module.setting.presenter.SettingsPresenter;

public class AdvanceSettingsActivity extends BaseActivity {


    private Toolbar toolbar;

    @Override
    protected int setLayoutResourceId() {
        return R.layout.activity_advance_settings;
    }

    @Override
    protected void findView() {
        toolbar = findViewById(R.id.advance_settings_toolbar);
    }

    @Override
    protected void initView() {

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        AdvanceSettingsFragment advanceSettingsFragment = new AdvanceSettingsFragment();
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.advance_settings_framelayout, advanceSettingsFragment)
                .commit();
    }

    @Override
    protected BasePresenter initPresenter() {
        return new SettingsPresenter();
    }
}