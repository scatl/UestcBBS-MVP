package com.scatl.uestcbbs.module.main.presenter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.scatl.uestcbbs.base.BasePresenter;
import com.scatl.uestcbbs.entity.SettingsBean;
import com.scatl.uestcbbs.entity.UpdateBean;
import com.scatl.uestcbbs.helper.ExceptionHelper;
import com.scatl.uestcbbs.helper.rxhelper.Observer;
import com.scatl.uestcbbs.module.main.model.MainModel;
import com.scatl.uestcbbs.module.main.view.MainView;
import com.scatl.uestcbbs.services.HeartMsgService;
import com.scatl.uestcbbs.util.ServiceUtil;
import com.scatl.uestcbbs.util.SharePrefUtil;

import io.reactivex.disposables.Disposable;


/**
 * author: sca_tl
 * description:
 * date: 2020/2/14 12:16
 */
public class MainPresenter extends BasePresenter<MainView> {

    private MainModel mainModel = new MainModel();

    public void getUpdate(int oldVersionCode, boolean isTest) {
        mainModel.getUpdate(oldVersionCode, isTest, new Observer<UpdateBean>() {
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

    public void getSettings() {
        mainModel.getSettings(new Observer<SettingsBean>() {
            @Override
            public void OnSuccess(SettingsBean settingsBean) {
                view.getSettingsSuccess(settingsBean);
            }

            @Override
            public void onError(ExceptionHelper.ResponseThrowable e) {
                view.getSettingsFail(e.message);
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
