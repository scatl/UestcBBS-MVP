package com.scatl.uestcbbs.module.setting.view;

import android.os.Bundle;

import androidx.preference.Preference;

import com.scatl.uestcbbs.R;
import com.scatl.uestcbbs.base.BasePreferenceFragment;
import com.scatl.uestcbbs.base.BasePresenter;
import com.scatl.uestcbbs.module.setting.presenter.SettingsPresenter;


public class AdvanceSettingsFragment extends BasePreferenceFragment {

    private SettingsPresenter settingsPresenter;

    @Override
    protected BasePresenter initPresenter() {
        return new SettingsPresenter();
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        getPreferenceManager().setSharedPreferencesName("settings");
        addPreferencesFromResource(R.xml.pref_advance_settings);

        init();
    }

    @Override
    public boolean onPreferenceTreeClick(Preference preference) {
        return super.onPreferenceTreeClick(preference);
    }

    /**
     * author: sca_tl
     * description:
     */
    private void init() {

        settingsPresenter = (SettingsPresenter) presenter;

    }

}