package com.scatl.uestcbbs.module.setting.model;

import com.scatl.uestcbbs.entity.UpdateBean;
import com.scatl.uestcbbs.helper.rxhelper.Observer;
import com.scatl.uestcbbs.util.RetrofitUtil;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;


/**
 * author: sca_tl
 * description:
 * date: 2020/2/14 18:13
 */
public class SettingModel {
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
}
