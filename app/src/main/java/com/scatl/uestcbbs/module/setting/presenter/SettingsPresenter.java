package com.scatl.uestcbbs.module.setting.presenter;

import android.content.Context;
import android.widget.Button;

import androidx.appcompat.app.AlertDialog;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
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
            }
        });
    }

    public void getCacheSize(Context context) {
        settingModel.getCacheSize(context, new Observer<String>() {
            @Override
            public void OnSuccess(String s) {
                view.getCacheSizeSuccess(s);
            }

            @Override
            public void onError(ExceptionHelper.ResponseThrowable e) {
                view.getCacheSizeFail(e.message);
            }

            @Override
            public void OnCompleted() {

            }

            @Override
            public void OnDisposable(Disposable d) {
                disposable.add(d);
            }
        });
    }
}
