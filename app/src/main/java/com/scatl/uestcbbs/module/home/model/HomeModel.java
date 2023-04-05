package com.scatl.uestcbbs.module.home.model;

import com.scatl.uestcbbs.api.ApiConstant;
import com.scatl.uestcbbs.api.ApiService;
import com.scatl.uestcbbs.entity.BingPicBean;
import com.scatl.uestcbbs.entity.HotPostBean;
import com.scatl.uestcbbs.entity.NoticeBean;
import com.scatl.uestcbbs.entity.SimplePostListBean;
import com.scatl.uestcbbs.helper.rxhelper.Observer;
import com.scatl.uestcbbs.util.RetrofitCookieUtil;
import com.scatl.uestcbbs.util.RetrofitUtil;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class HomeModel {
    public void getBannerData(Observer<BingPicBean> observer) {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ApiConstant.BING_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
        retrofit.create(ApiService.class)
                .getBingPic()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

    public void getSimplePostList(int page,
                                  int pageSize,
                                  int boardId,
                                  String sortby,
                                  Observer<SimplePostListBean> observer) {
        Observable<SimplePostListBean> observable = RetrofitUtil
                .getInstance()
                .getApiService()
                .getSimplePostList(page, pageSize, boardId, sortby);
        observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

    public void getHotPostList(int page,
                               int pageSize,
                               int moduleId,
                               Observer<HotPostBean> observer) {
        Observable<HotPostBean> observable = RetrofitUtil
                .getInstance()
                .getApiService()
                .getHotPostList(page, pageSize, moduleId);
        observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

    public void getNotice(Observer<NoticeBean> observer) {
        Observable<NoticeBean> observable = RetrofitUtil
                .getInstance()
                .getApiService()
                .getNotice();
        observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

    public void getGrabSofa(Observer<String> observer) {
        Observable<String> observable = RetrofitUtil
                .getInstance()
                .getApiService()
                .grabSofa();
        observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

    public void getTaoTieCollection(int page, String op, String order, Observer<String> observer) {
        Observable<String> observable = RetrofitUtil
                .getInstance()
                .getApiService()
                .taoTieCollection(page, op, order);
        observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

    public void myCollection(Observer<String> observer) {
        Observable<String> observable = RetrofitCookieUtil
                .getInstance()
                .getApiService()
                .myCollection();
        observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

    public void getOnLineUSer(Observer<String> observer) {
        Observable<String> observable = RetrofitCookieUtil
                .getInstance()
                .getApiService()
                .getHomeInfo();
        observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);

    }
}
