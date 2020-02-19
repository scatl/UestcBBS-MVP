package com.scatl.uestcbbs.module.account.model;

import com.scatl.uestcbbs.entity.HotPostBean;
import com.scatl.uestcbbs.entity.LoginBean;
import com.scatl.uestcbbs.helper.rxhelper.Observer;
import com.scatl.uestcbbs.util.RetrofitUtil;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;


/**
 * author: sca_tl
 * description:
 * date: 2020/1/24 13:05
 */
public class LoginModel {
    public void login(String userName,
                      String userPsw,
                      Observer<LoginBean> observer) {
        Observable<LoginBean> observable = RetrofitUtil
                .getInstance()
                .getApiService()
                .login(userName, userPsw);
        observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }
}
