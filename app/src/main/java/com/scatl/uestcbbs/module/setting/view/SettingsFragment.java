package com.scatl.uestcbbs.module.setting.view;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.text.TextUtils;

import androidx.preference.Preference;

import com.scatl.uestcbbs.R;
import com.scatl.uestcbbs.annotation.ToastType;
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
import com.scatl.uestcbbs.util.ToastUtil;

import org.greenrobot.eventbus.EventBus;

import java.net.URLDecoder;

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

        if (preference.getKey().equals(getString(R.string.release_saf_access))){
            try {
                Uri uri = Uri.parse(SharePrefUtil.getDownloadFolderUri(mActivity));
                int flags = Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION;
                getContext().getContentResolver().releasePersistableUriPermission(uri, flags);
                SharePrefUtil.setDownloadFolderUri(getContext(), "");
                ToastUtil.showToast(mActivity, "撤销成功", ToastType.TYPE_SUCCESS);
                refreshSAFStatus();
            } catch (Exception e) {
                ToastUtil.showToast(mActivity, "撤销失败:" +e.getMessage(), ToastType.TYPE_ERROR);
            }
        }

        return super.onPreferenceTreeClick(preference);
    }

    private void init() {

        settingsPresenter = (SettingsPresenter) presenter;

        Preference k = findPreference(getString(R.string.app_update));
        String versionName = CommonUtil.getVersionName(mActivity);
        if (k != null) {
            k.setSummary("当前版本：" + versionName + (versionName.contains("beta") ? "，感谢参与测试" : ""));
        }
        refreshSAFStatus();
    }

    private void refreshSAFStatus() {
        try {
            Preference j = findPreference(getString(R.string.release_saf_access));
            if (!TextUtils.isEmpty(SharePrefUtil.getDownloadFolderUri(mActivity))) {
                String folder = URLDecoder.decode(SharePrefUtil.getDownloadFolderUri(mActivity), "UTF-8").replace("content://com.android.externalstorage.documents/tree/primary:", "");
                if (j != null) {
                    j.setSummary(getString(R.string.release_saf_access_summary, folder));
                    j.setEnabled(true);
                }
            } else {
                if (j != null) {
                    j.setSummary("暂时没有申请权限");
                    j.setEnabled(false);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void getUpdateSuccess(UpdateBean updateBean) {
        if (updateBean.updateInfo.isValid && updateBean.updateInfo.apkVersionCode > CommonUtil.getVersionCode(mActivity)) {
            Bundle bundle = new Bundle();
            bundle.putSerializable(Constant.IntentKey.DATA_1, updateBean);
            UpdateFragment.getInstance(bundle)
                    .show(getChildFragmentManager(), TimeUtil.getStringMs());
        } else {
            ToastUtil.showToast(mActivity, "已经是最新版本啦", ToastType.TYPE_NORMAL);
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
