package com.scatl.uestcbbs.module.home.presenter;

import android.Manifest;
import android.content.Context;
import android.widget.Button;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentActivity;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.scatl.uestcbbs.App;
import com.scatl.uestcbbs.api.ApiConstant;
import com.scatl.uestcbbs.base.BasePresenter;
import com.scatl.uestcbbs.callback.OnPermission;
import com.scatl.uestcbbs.entity.BingPicBean;
import com.scatl.uestcbbs.entity.CommonPostBean;
import com.scatl.uestcbbs.entity.NoticeBean;
import com.scatl.uestcbbs.entity.SimplePostListBean;
import com.scatl.uestcbbs.helper.ExceptionHelper;
import com.scatl.uestcbbs.helper.rxhelper.Observer;
import com.scatl.uestcbbs.module.home.model.HomeModel;
import com.scatl.uestcbbs.module.home.view.HomeFragment;
import com.scatl.uestcbbs.module.home.view.HomeView;
import com.scatl.uestcbbs.util.CommonUtil;
import com.scatl.uestcbbs.util.SharePrefUtil;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

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
                disposable.add(d);
            }
        });
    }

    public void getSimplePostList(int page, int pageSize, String sortby, Context context){
        homeModel.getSimplePostList(page, pageSize, 0, sortby, new Observer<CommonPostBean>() {
            @Override
            public void OnSuccess(CommonPostBean simplePostListBean) {
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
                disposable.add(d);
            }
        });
    }

    public void getNotice() {
        homeModel.getNotice(new Observer<NoticeBean>() {
            @Override
            public void OnSuccess(NoticeBean noticeBean) {
                view.onGetNoticeSuccess(noticeBean);
            }

            @Override
            public void onError(ExceptionHelper.ResponseThrowable e) {
                view.onGetNoticeError(e.message);
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

    public void getHomePage() {
        homeModel.getOnLineUSer(new Observer<String>() {
            @Override
            public void OnSuccess(String s) {
                try {
                    Document document = Jsoup.parse(s);
                    String formHash = document.select("form[id=scbar_form]").select("input[name=formhash]").attr("value");
                    SharePrefUtil.setForumHash(App.getContext(), formHash);
                } catch (Exception e) { }

                view.onGetHomePageSuccess(s);
            }

            @Override
            public void onError(ExceptionHelper.ResponseThrowable e) { }

            @Override
            public void OnCompleted() { }

            @Override
            public void OnDisposable(Disposable d) {
                disposable.add(d);
            }
        });
    }

}
