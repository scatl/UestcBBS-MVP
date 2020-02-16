package com.scatl.uestcbbs.module.home.presenter;

import android.content.Context;

import com.scatl.uestcbbs.api.ApiConstant;
import com.scatl.uestcbbs.base.BasePresenter;
import com.scatl.uestcbbs.entity.BingPicBean;
import com.scatl.uestcbbs.entity.HotPostBean;
import com.scatl.uestcbbs.entity.SimplePostListBean;
import com.scatl.uestcbbs.helper.ExceptionHelper;
import com.scatl.uestcbbs.helper.rxhelper.Observer;
import com.scatl.uestcbbs.helper.rxhelper.SubscriptionManager;
import com.scatl.uestcbbs.module.home.model.HomeModel;
import com.scatl.uestcbbs.module.home.view.HomeView;
import com.scatl.uestcbbs.util.SharePrefUtil;

import io.reactivex.disposables.Disposable;

public class HomePresenter extends BasePresenter<HomeView> {

    private HomeModel homeModel = new HomeModel();

    public void getBannerData() {
        homeModel.getBannerData(new Observer<BingPicBean>() {
            @Override
            public void OnSuccess(BingPicBean bingPicBean) {
                view.getBannerDataSuccess(bingPicBean);
            }

            @Override
            public void onError(ExceptionHelper.ResponseThrowable e) {

            }

            @Override
            public void OnCompleted() {

            }

            @Override
            public void OnDisposable(Disposable d) {
                SubscriptionManager.getInstance().add(d);
            }
        });
    }

    public void getSimplePostList(int page, int pageSize, String sortby, Context context){
        homeModel.getSimplePostList(page, pageSize, 0, sortby,
                SharePrefUtil.getToken(context),
                SharePrefUtil.getSecret(context), new Observer<SimplePostListBean>() {
                    @Override
                    public void OnSuccess(SimplePostListBean simplePostListBean) {
                        if (simplePostListBean.rs == ApiConstant.Code.SUCCESS_CODE) {
                            view.getSimplePostDataSuccess(simplePostListBean);
                        }
                        if (simplePostListBean.rs == ApiConstant.Code.ERROR_CODE) {
                            view.getSimplePostDataError(simplePostListBean.head.errInfo);
                        }
                    }

                    @Override
                    public void onError(ExceptionHelper.ResponseThrowable e) {
                        view.getSimplePostDataError(e.message);
                    }

                    @Override
                    public void OnCompleted() {

                    }

                    @Override
                    public void OnDisposable(Disposable d) {
                        SubscriptionManager.getInstance().add(d);
                    }
                });
    }


}
