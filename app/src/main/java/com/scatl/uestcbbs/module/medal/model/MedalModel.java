package com.scatl.uestcbbs.module.medal.model;

import com.scatl.uestcbbs.helper.rxhelper.Observer;
import com.scatl.uestcbbs.util.RetrofitCookieUtil;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * author: sca_tl
 * date: 2020/10/7 18:59
 * description:
 */
public class MedalModel {
    public void getMedalCenter(Observer<String> observer) {
        Observable<String> observable = RetrofitCookieUtil
                .getInstance()
                .getApiService()
                .getMedal();
        observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);

    }

    public void getMineMedal(Observer<String> observer) {
        Observable<String> observable = RetrofitCookieUtil
                .getInstance()
                .getApiService()
                .getMineMedal();
        observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);

    }
}
