package com.scatl.uestcbbs.module.main.model;

import com.alibaba.fastjson.JSONObject;
import com.scatl.uestcbbs.entity.SettingsBean;
import com.scatl.uestcbbs.entity.UpdateBean;
import com.scatl.uestcbbs.helper.rxhelper.Observer;
import com.scatl.uestcbbs.util.RetrofitCookieUtil;
import com.scatl.uestcbbs.util.RetrofitUtil;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;


/**
 * author: sca_tl
 * description:
 * date: 2020/2/14 12:25
 */
public class MainModel {
    public void getUpdate(int oldVersionCode, boolean isTest, Observer<UpdateBean> observer) {
        Observable<UpdateBean> observable = RetrofitUtil
                .getInstance()
                .getApiService()
                .getUpdateInfo(oldVersionCode, isTest);
        observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);

    }

    public void getSettings(Observer<SettingsBean> observer) {
        Observable<SettingsBean> observable = RetrofitUtil
                .getInstance()
                .getApiService()
                .getSettings();
        observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);

    }
}
