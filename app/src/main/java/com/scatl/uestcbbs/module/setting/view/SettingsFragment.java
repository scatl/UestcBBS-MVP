package com.scatl.uestcbbs.module.setting.view;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.preference.Preference;
import androidx.preference.SwitchPreferenceCompat;

import com.scatl.uestcbbs.R;
import com.scatl.uestcbbs.base.BasePreferenceFragment;
import com.scatl.uestcbbs.base.BasePresenter;
import com.scatl.uestcbbs.entity.UpdateBean;
import com.scatl.uestcbbs.module.setting.presenter.SettingsPresenter;
import com.scatl.uestcbbs.module.update.view.UpdateFragment;
import com.scatl.uestcbbs.util.CommonUtil;
import com.scatl.uestcbbs.util.Constant;
import com.scatl.uestcbbs.util.SharePrefUtil;
import com.scatl.uestcbbs.util.TimeUtil;

/**
 * author: sca_tl
 * description:
 * date: 2020/1/27 13:23
 */
public class SettingsFragment extends BasePreferenceFragment implements SettingsView{

    private SettingsPresenter settingsPresenter;

    @Override
    protected BasePresenter initPresenter() {
        return new SettingsPresenter();
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        getPreferenceManager().setSharedPreferencesName("settings");
        addPreferencesFromResource(R.xml.pref_settings);

        init();
    }

    @Override
    public boolean onPreferenceTreeClick(Preference preference) {
        if (preference.getKey().equals(getString(R.string.clear_cache))) {
            settingsPresenter.clearCache(mActivity);
        }

        if (preference.getKey().equals(getString(R.string.home_style))) {
           // changeHomeStyleDialog();
        }

        if (preference.getKey().equals(getString(R.string.app_update))) {
            //checkUpdate(true);
            settingsPresenter.getUpdate();
        }

        if (preference.getKey().equals(getString(R.string.app_suggestion))) {
//            Intent intent = new Intent(mActivity, SuggestionActivity.class);
//            startActivity(intent);
        }

        if (preference.getKey().equals(getString(R.string.app_about))) {
            Intent intent = new Intent(mActivity, AboutActivity.class);
            startActivity(intent);
        }

        if (preference.getKey().equals(getString(R.string.auto_load_more))) {
            SharePrefUtil.setAutoLoadMore(mActivity, SharePrefUtil.isAutoLoadMore(mActivity));
        }


        return super.onPreferenceTreeClick(preference);
    }

    /**
     * author: sca_tl
     * description:
     */
    private void init() {

        settingsPresenter = (SettingsPresenter) presenter;

        ((SwitchPreferenceCompat)findPreference(getString(R.string.auto_load_more))).setChecked(SharePrefUtil.isAutoLoadMore(mActivity));
        findPreference(getString(R.string.app_update)).setSummary("当前版本：" + CommonUtil.getVersionName(mActivity));
        //checkUpdate(false);
    }

    @Override
    public void getUpdateSuccess(UpdateBean updateBean) {
        if (updateBean.versionCode > CommonUtil.getVersionCode(mActivity)) {
            Bundle bundle = new Bundle();
            bundle.putSerializable(Constant.IntentKey.DATA, updateBean);
            UpdateFragment.getInstance(bundle)
                    .show(getChildFragmentManager(), TimeUtil.getStringMs());
        } else {
            showSnackBar(getView(), "已经是最新版本啦");
        }
    }

    @Override
    public void getUpdateFail(String msg) {

    }

    @Override
    public void onClearCacheSuccess() {
        showSnackBar(getView(), "清理缓存成功");
    }
}
