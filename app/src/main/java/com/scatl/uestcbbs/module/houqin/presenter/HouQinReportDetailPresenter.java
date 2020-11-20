package com.scatl.uestcbbs.module.houqin.presenter;

import android.util.Log;

import com.scatl.uestcbbs.api.ApiConstant;
import com.scatl.uestcbbs.api.ApiService;
import com.scatl.uestcbbs.base.BasePresenter;
import com.scatl.uestcbbs.entity.HouQinReportReplyBean;
import com.scatl.uestcbbs.entity.HouQinReportTopicBean;
import com.scatl.uestcbbs.helper.ExceptionHelper;
import com.scatl.uestcbbs.helper.rxhelper.Observer;
import com.scatl.uestcbbs.module.houqin.model.HouQinModel;
import com.scatl.uestcbbs.module.houqin.view.HouQinReportDetailView;
import com.scatl.uestcbbs.util.CommonUtil;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * author: sca_tl
 * date: 2020/10/25 17:08
 * description:
 */
public class HouQinReportDetailPresenter extends BasePresenter<HouQinReportDetailView> {
    HouQinModel houQinModel = new HouQinModel();

    public void getDetail(int topicId) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ApiConstant.HOUQIN_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();

        Observable<HouQinReportTopicBean> observable1 = retrofit.create(ApiService.class).getHouQinReportTopic(topicId);
        Observable<HouQinReportReplyBean> observable2 = retrofit.create(ApiService.class).getHouQinReportReply(topicId);

        observable1
                .map(new Function<HouQinReportTopicBean, Object>() {
                    @Override
                    public Object apply(HouQinReportTopicBean houQinReportTopicBean) throws Exception {
                        view.onGetHouQinReportTopicSuccess(houQinReportTopicBean);
                        return houQinReportTopicBean;
                    }
                })
                .flatMap(new Function<Object, ObservableSource<?>>() {
                    @Override
                    public ObservableSource<?> apply(Object o) throws Exception {
                        return observable2;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Object>() {
                    @Override
                    public void OnSuccess(Object o) {
                        view.onGetHouQinReportReplySuccess((HouQinReportReplyBean) o);
                    }

                    @Override
                    public void onError(ExceptionHelper.ResponseThrowable e) {
                        view.onGetReportDetailError("获取详情失败:" + e.message);
                    }

                    @Override
                    public void OnCompleted() {

                    }

                    @Override
                    public void OnDisposable(Disposable d) {
                        disposable.add(d);
                    }
                });

    }

}
