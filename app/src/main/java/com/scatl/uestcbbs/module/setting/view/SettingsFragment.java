package com.scatl.uestcbbs.module.setting.view;

import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;

import androidx.preference.Preference;

import com.scatl.uestcbbs.R;
import com.scatl.uestcbbs.base.BaseEvent;
import com.scatl.uestcbbs.base.BasePreferenceFragment;
import com.scatl.uestcbbs.base.BasePresenter;
import com.scatl.uestcbbs.entity.UpdateBean;
import com.scatl.uestcbbs.module.setting.presenter.SettingsPresenter;
import com.scatl.uestcbbs.module.update.view.UpdateFragment;
import com.scatl.uestcbbs.util.CommonUtil;
import com.scatl.uestcbbs.util.Constant;
import com.scatl.uestcbbs.util.FileUtil;
import com.scatl.uestcbbs.util.SharePrefUtil;
import com.scatl.uestcbbs.util.TimeUtil;

import org.greenrobot.eventbus.EventBus;

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
            new CacheThread().start();
        }

        if (preference.getKey().equals(getString(R.string.app_update))) {
            settingsPresenter.getUpdate(CommonUtil.getVersionCode(mActivity), false);
        }

//        if (preference.getKey().equals(getString(R.string.app_suggestion_contact_developer))) {
//            Intent intent = new Intent(mActivity, PrivateChatActivity.class);
//            intent.putExtra(Constant.IntentKey.USER_ID, 217992);
//            intent.putExtra(Constant.IntentKey.USER_NAME, "私信开发者：sca_tl");
//            startActivity(intent);
//        }
//
//        if (preference.getKey().equals(getString(R.string.app_suggestion_contact_web))) {
//            Intent intent = new Intent(mActivity, WebViewActivity.class);
//            intent.putExtra(Constant.IntentKey.URL, "https://support.qq.com/product/141698");
//            startActivity(intent);
//        }

        if (preference.getKey().equals(getString(R.string.app_about))) {
            Intent intent = new Intent(mActivity, AboutActivity.class);
            startActivity(intent);
        }

        if (preference.getKey().equals(getString(R.string.auto_load_more))) {
            SharePrefUtil.setAutoLoadMore(mActivity, SharePrefUtil.isAutoLoadMore(mActivity));
        }
        if (preference.getKey().equals(getString(R.string.show_home_banner))) {
            EventBus.getDefault().post(new BaseEvent<>(BaseEvent.EventCode.HOME_BANNER_VISIBILITY_CHANGE));
        }
        if (preference.getKey().equals(getString(R.string.close_all_site_top_stick_post))) {
            EventBus.getDefault().post(new BaseEvent<>(BaseEvent.EventCode.ALL_SITE_TOP_STICK_VISIBILITY_CHANGE));
        }

        return super.onPreferenceTreeClick(preference);
    }

    /**
     * author: sca_tl
     * description:
     */
    private void init() {

        settingsPresenter = (SettingsPresenter) presenter;

        //((SwitchPreferenceCompat)findPreference(getString(R.string.auto_load_more))).setChecked(SharePrefUtil.isAutoLoadMore(mActivity));
        Preference k = findPreference(getString(R.string.app_update));
        if (k != null) k.setSummary("当前版本：" + CommonUtil.getVersionName(mActivity));
        //checkUpdate(false);
    }

    @Override
    public void getUpdateSuccess(UpdateBean updateBean) {
        if (updateBean.updateInfo.isValid && updateBean.updateInfo.apkVersionCode > CommonUtil.getVersionCode(mActivity)) {
            Bundle bundle = new Bundle();
            bundle.putSerializable(Constant.IntentKey.DATA_1, updateBean);
            UpdateFragment.getInstance(bundle)
                    .show(getChildFragmentManager(), TimeUtil.getStringMs());
        } else {
            showSnackBar(getView(), "已经是最新版本啦");
        }
    }

    @Override
    public void getUpdateFail(String msg) {
        showSnackBar(getView(), "检查更新失败：" + msg);
    }

    @Override
    public void onClearCacheSuccess() {
        showSnackBar(getView(), "清理缓存成功");
    }

    private class CacheThread extends Thread {
        @Override
        public void run() {
            Looper.prepare();
            String s = FileUtil.formatDirectorySize(FileUtil.getDirectorySize(mActivity.getCacheDir())
//                + FileUtil.getDirectorySize(mActivity.getExternalFilesDir(Constant.AppPath.IMG_PATH))
                    + FileUtil.getDirectorySize(mActivity.getExternalFilesDir(Constant.AppPath.TEMP_PATH)));
            settingsPresenter.clearCache(mActivity, s);
            Looper.loop();
        }
    }
}
