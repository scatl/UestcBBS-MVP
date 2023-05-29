package com.scatl.uestcbbs.module.setting.view;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
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
import com.scatl.util.SystemUtil;
import com.scatl.widget.download.DownLoadUtil;

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
            FileUtil.deleteDir(mActivity.getCacheDir(), false);
            FileUtil.deleteDir(mActivity.getExternalFilesDir(Constant.AppPath.TEMP_PATH), false);
            preference.setSummary("当前缓存大小：0.00MB");
        }

        if (preference.getKey().equals(getString(R.string.app_update))) {
            settingsPresenter.getUpdate(SystemUtil.getVersionCode(mActivity), false);
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
                Uri uri = Uri.parse(DownLoadUtil.getDownloadFolderUri(mActivity));
                int flags = Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION;
                getContext().getContentResolver().releasePersistableUriPermission(uri, flags);
                DownLoadUtil.setDownloadFolderUri(getContext(), "");
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

        settingsPresenter.getCacheSize(mActivity);

        Preference k = findPreference(getString(R.string.app_update));
        String versionName = SystemUtil.getVersionName(mActivity);
        if (k != null) {
            k.setSummary("当前版本：" + versionName + (versionName.contains("beta") ? "，感谢参与测试" : ""));
        }
        refreshSAFStatus();
    }

    private void refreshSAFStatus() {
        try {
            Preference j = findPreference(getString(R.string.release_saf_access));
            if (!TextUtils.isEmpty(DownLoadUtil.getDownloadFolderUri(mActivity))) {
                String folder = DownLoadUtil.getDownloadFolder(mActivity);
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
        if (updateBean.updateInfo.isValid && updateBean.updateInfo.apkVersionCode > SystemUtil.getVersionCode(mActivity)) {
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
    public void getCacheSizeSuccess(String msg) {
        Preference k = findPreference(getString(R.string.clear_cache));
        if (k != null) {
            k.setSummary("当前缓存大小：" + msg);
        }
    }

    @Override
    public void getCacheSizeFail(String msg) {
        Preference k = findPreference(getString(R.string.clear_cache));
        if (k != null) {
            k.setSummary("暂时无法获取缓存大小");
        }
    }

}
