package com.scatl.uestcbbs.module.setting.presenter;

import android.content.Context;
import android.widget.Button;

import androidx.appcompat.app.AlertDialog;

import com.scatl.uestcbbs.R;
import com.scatl.uestcbbs.base.BasePresenter;
import com.scatl.uestcbbs.entity.UpdateBean;
import com.scatl.uestcbbs.helper.ExceptionHelper;
import com.scatl.uestcbbs.helper.rxhelper.Observer;
import com.scatl.uestcbbs.module.setting.model.SettingModel;
import com.scatl.uestcbbs.module.setting.view.SettingsView;
import com.scatl.uestcbbs.util.Constant;
import com.scatl.uestcbbs.util.FileUtil;

import io.reactivex.disposables.Disposable;


/**
 * author: sca_tl
 * description:
 * date: 2020/1/27 13:20
 */
public class SettingsPresenter extends BasePresenter<SettingsView> {

    private SettingModel settingModel = new SettingModel();

    public void getUpdate(int oldVersionCode, boolean isTest) {
        settingModel.getUpdate(oldVersionCode, isTest, new Observer<UpdateBean>() {
            @Override
            public void OnSuccess(UpdateBean updateBean) {
                view.getUpdateSuccess(updateBean);
            }

            @Override
            public void onError(ExceptionHelper.ResponseThrowable e) {
                view.getUpdateFail(e.message);
            }

            @Override
            public void OnCompleted() {

            }

            @Override
            public void OnDisposable(Disposable d) {
                disposable.add(d);
//                SubscriptionManager.getInstance().add(d);
            }
        });
    }


    public void clearCache(Context context, String s) {
//        String s = FileUtil.formatDirectorySize(FileUtil.getDirectorySize(context.getCacheDir())
////                + FileUtil.getDirectorySize(mActivity.getExternalFilesDir(Constant.AppPath.IMG_PATH))
//                + FileUtil.getDirectorySize(context.getExternalFilesDir(Constant.AppPath.TEMP_PATH)));

        final AlertDialog dialog = new AlertDialog.Builder(context)
                .setTitle("清理缓存")
                .setMessage(context.getResources().getString(R.string.clear_cache_disp, s))
                .setPositiveButton("确认", null)
                .setNegativeButton("取消", null).create();
        dialog.setOnShowListener(dialogInterface -> {
            Button p = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            p.setOnClickListener(v -> {
                FileUtil.deleteDir(context.getCacheDir(), false);
                //FileUtil.deleteDir(mActivity.getExternalFilesDir(Constants.AppFilePath.IMG_PATH), false);
                FileUtil.deleteDir(context.getExternalFilesDir(Constant.AppPath.TEMP_PATH), false);
                dialog.dismiss();
                view.onClearCacheSuccess();
            });
        });
        dialog.show();
    }

}
