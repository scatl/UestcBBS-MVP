package com.scatl.uestcbbs.services.heartmsg.model;

import com.scatl.uestcbbs.entity.HeartMsgBean;
import com.scatl.uestcbbs.entity.LoginBean;
import com.scatl.uestcbbs.helper.rxhelper.Observer;
import com.scatl.uestcbbs.util.RetrofitUtil;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * author: sca_tl
 * description:
 * date: 2020/1/24 13:46
 */
public class HeartMsgModel {

    public void getHeartMsg(String token,
                            String secret,
                            String sdkVersion,
                            Observer<HeartMsgBean> observer) {
        Observable<HeartMsgBean> observable = RetrofitUtil
                .getInstance()
                .getApiService()
                .getHeartMsg(sdkVersion, token, secret);
        observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

}
