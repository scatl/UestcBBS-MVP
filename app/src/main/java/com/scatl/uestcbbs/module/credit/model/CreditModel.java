package com.scatl.uestcbbs.module.credit.model;

import com.scatl.uestcbbs.helper.rxhelper.Observer;
import com.scatl.uestcbbs.util.RetrofitCookieUtil;

import java.util.HashMap;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class CreditModel {
    public void getMineCredit(Observer<String> observer) {
        Observable<String> observable = RetrofitCookieUtil
                .getInstance()
                .getApiService()
                .getMineCredit();
        observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

    public void getCreditHistory(int page, Observer<String> observer) {
        Observable<String> observable = RetrofitCookieUtil
                .getInstance()
                .getApiService()
                .getCreditHistory(page);
        observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

    public void getCreditFormHash(Observer<String> observer) {
        Observable<String> observable = RetrofitCookieUtil
                .getInstance()
                .getApiService()
                .getCreditFormHash();
        observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

    public void creditTransfer(String formHash, String amount, String toUserName, String password, String message, Observer<String> observer) {
        Map<String, String> map = new HashMap<>();
        map.put("formhash", formHash);
        map.put("transfersubmit", "true");
        map.put("handlekey", "transfercredit");
        map.put("transferamount", amount);
        map.put("to", toUserName);
        map.put("password", password);
        map.put("transfermessage", message);
        map.put("transfersubmit_btn", "true");

        Observable<String> observable = RetrofitCookieUtil
                .getInstance()
                .getApiService()
                .creditTransfer(RetrofitCookieUtil.generateRequestBody(map));
        observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }
}
