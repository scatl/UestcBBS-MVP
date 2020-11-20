package com.scatl.uestcbbs.module.houqin.model;

import com.scatl.uestcbbs.api.ApiConstant;
import com.scatl.uestcbbs.api.ApiService;
import com.scatl.uestcbbs.entity.HouQinReportListBean;
import com.scatl.uestcbbs.entity.HouQinReportTopicBean;
import com.scatl.uestcbbs.helper.rxhelper.Observer;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * author: sca_tl
 * date: 2020/10/23 20:40
 * description:
 */
public class HouQinModel {
    public void getAllReportList(int pageNo, Observer<HouQinReportListBean> observer) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ApiConstant.HOUQIN_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
        retrofit.create(ApiService.class)
                .getAllReportPosts(pageNo)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

    public void getReportTopic(int topicId, Observer<HouQinReportTopicBean> observer) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ApiConstant.HOUQIN_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
        retrofit.create(ApiService.class)
                .getHouQinReportTopic(topicId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

}
