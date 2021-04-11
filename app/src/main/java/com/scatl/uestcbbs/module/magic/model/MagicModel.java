package com.scatl.uestcbbs.module.magic.model;

import com.scatl.uestcbbs.helper.rxhelper.Observer;
import com.scatl.uestcbbs.util.RetrofitCookieUtil;

import java.util.HashMap;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class MagicModel {
    public void getMagicShop(Observer<String> observer) {
        Observable<String> observable = RetrofitCookieUtil
                .getInstance()
                .getApiService()
                .getMagicShop();
        observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);

    }

    public void getMagicDetail(String id, Observer<String> observer) {
        Observable<String> observable = RetrofitCookieUtil
                .getInstance()
                .getApiService()
                .getMagicDetail(id);
        observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);

    }

    public void getMineMagic(Observer<String> observer) {
        Observable<String> observable = RetrofitCookieUtil
                .getInstance()
                .getApiService()
                .getMineMagic();
        observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);

    }

    public void buyMagic(String formHash, String mid, int count, Observer<String> observer) {
        Map<String, String> map = new HashMap<>();
        map.put("formhash", formHash);
        map.put("mid", mid);
        map.put("operation", "buy");
        map.put("operatesubmit", "true");
        map.put("magicnum", "" + count);

        Observable<String> observable = RetrofitCookieUtil
                .getInstance()
                .getApiService()
                .buyMagic(RetrofitCookieUtil.generateRequestBody(map));
        observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

    public void getUseMagicDetail(String magicId, Observer<String> observer) {
        Observable<String> observable = RetrofitCookieUtil
                .getInstance()
                .getApiService()
                .getUseMagicDetail(magicId);
        observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);

    }

    public void confirmUseMagic(String formHash, String magicId, Observer<String> observer) {
        Map<String, String> map = new HashMap<>();
        map.put("formhash", formHash);
        map.put("magicid", magicId);
        map.put("operation", "use");
        map.put("usesubmit", "yes");
        map.put("handlekey", "");

        Observable<String> observable = RetrofitCookieUtil
                .getInstance()
                .getApiService()
                .confirmUseMagic(RetrofitCookieUtil.generateRequestBody(map));
        observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

    public void getUseRegretMagicDetail(String id, Observer<String> observer) {
        Observable<String> observable = RetrofitCookieUtil
                .getInstance()
                .getApiService()
                .getUseRegretMagicDetail(id);
        observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

    public void confirmUseRegretMagic(String formHash, int pid, int tid, Observer<String> observer) {
        Map<String, String> map = new HashMap<>();
        map.put("formhash", formHash);
        map.put("magicid", "20");
        map.put("operation", "use");
        map.put("usesubmit", "yes");
        map.put("handlekey", "");
        map.put("idtype", "pid");
        map.put("pid", pid + "");
        map.put("ptid", tid + "");
        map.put("id", pid + ":" + tid);

        Observable<String> observable = RetrofitCookieUtil
                .getInstance()
                .getApiService()
                .confirmUseRegretMagic(RetrofitCookieUtil.generateRequestBody(map));
        observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

}
