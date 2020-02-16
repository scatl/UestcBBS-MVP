package com.scatl.uestcbbs.module.update.model;

import com.scatl.uestcbbs.api.ApiConstant;
import com.scatl.uestcbbs.api.ApiService;
import com.scatl.uestcbbs.helper.rxhelper.Observer;

import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * author: sca_tl
 * description:
 * date: 2019/12/18 19:58
 */
public class UpdateModel {

    public void downloadApk(String url, Observer<ResponseBody> observer) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ApiConstant.BASE_ADDITIONAL_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
        retrofit.create(ApiService.class)
                .downloadFile(url)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())  //注意是io线程，主线程会出错
                .subscribe(observer);
    }
}
