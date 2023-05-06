package com.scatl.uestcbbs.module.collection.model;

import com.scatl.uestcbbs.helper.rxhelper.Observer;
import com.scatl.uestcbbs.util.RetrofitCookieUtil;
import com.scatl.uestcbbs.util.RetrofitUtil;

import java.util.HashMap;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import retrofit2.Response;

/**
 * author: sca_tl
 * date: 2020/5/5 19:18
 * description:
 */
public class CollectionModel {
    public void getCollectionDetail(int ctid, int page, Observer<String> observer) {
        Observable<String> observable = RetrofitCookieUtil
                .getInstance()
                .getApiService()
                .collectionDetail(ctid, page);
        observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

    public void subscribeCollection(int ctid, String op, String formash, Observer<String> observer) {
        Observable<String> observable = RetrofitCookieUtil
                .getInstance()
                .getApiService()
                .subscribeCollection(ctid, op, formash);
        observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

    public void addToCollection(int tid, Observer<String> observer) {
        Observable<String> observable = RetrofitCookieUtil
                .getInstance()
                .getApiService()
                .addToCollection(tid);
        observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

    public void confirmAddToCollection(String formhash, String reason, int tid, int ctid, Observer<String> observer) {
        Map<String, String> map = new HashMap<>();
        map.put("ctid", ctid + "");
        map.put("reason", reason);
        map.put("tids[]", tid + "");
        map.put("inajax", "0");
        map.put("handlekey", "");
        map.put("formhash", formhash);
        map.put("addthread", "1");
        map.put("submitaddthread", "");

        Observable<String> observable = RetrofitCookieUtil
                .getInstance()
                .getApiService()
                .confirmAddToCollection(RetrofitCookieUtil.generateRequestBody(map));
        observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

    public void createCollection(String formhash, String title, String desc, String keyword, Observer<String> observer) {
        Map<String, String> map = new HashMap<>();
        map.put("title", title);
        map.put("desc", desc);
        map.put("keyword", keyword);
        map.put("submitcollection", "1");
        map.put("op", "");
        map.put("ctid", "0");
        map.put("formhash", formhash);
        map.put("collectionsubmit", "submit");

        Observable<String> observable = RetrofitCookieUtil
                .getInstance()
                .getApiService()
                .createCollection(RetrofitCookieUtil.generateRequestBody(map));
        observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

    public void deleteCollectionPost(String formhash, int tid, int ctid, Observer<String> observer) {
        Map<String, String> map = new HashMap<>();
        map.put("delthread[]", tid + "");
        map.put("ctid", ctid + "");
        map.put("formhash", formhash);

        Observable<String> observable = RetrofitCookieUtil
                .getInstance()
                .getApiService()
                .deleteCollectionPost(RetrofitCookieUtil.generateRequestBody(map));
        observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

    public void deleteCollection(String formhash, int ctid, Observer<String> observer) {
        Observable<String> observable = RetrofitCookieUtil
                .getInstance()
                .getApiService()
                .deleteCollection(ctid, formhash);
        observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

    public void getCollectionList(int page, String op, String order, Observer<String> observer) {
        Observable<String> observable = RetrofitUtil
                .getInstance()
                .getApiService()
                .getCollectionList(page, op, order);
        observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

}
