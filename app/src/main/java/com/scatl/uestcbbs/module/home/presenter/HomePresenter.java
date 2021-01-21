package com.scatl.uestcbbs.module.home.presenter;

import android.Manifest;
import android.content.Context;
import android.util.Log;
import android.widget.Button;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentActivity;

import com.scatl.uestcbbs.api.ApiConstant;
import com.scatl.uestcbbs.base.BasePresenter;
import com.scatl.uestcbbs.callback.OnPermission;
import com.scatl.uestcbbs.entity.BingPicBean;
import com.scatl.uestcbbs.entity.NoticeBean;
import com.scatl.uestcbbs.entity.SimplePostListBean;
import com.scatl.uestcbbs.helper.ExceptionHelper;
import com.scatl.uestcbbs.helper.rxhelper.Observer;
import com.scatl.uestcbbs.module.home.model.HomeModel;
import com.scatl.uestcbbs.module.home.view.HomeFragment;
import com.scatl.uestcbbs.module.home.view.HomeView;
import com.scatl.uestcbbs.util.CommonUtil;
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
                disposable.add(d);
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
                        disposable.add(d);
                    }
                });
    }

    public void cleanCache(Context context){
        homeModel.cleanCache(SharePrefUtil.getToken(context),
                SharePrefUtil.getSecret(context), new Observer<String>() {
                    @Override
                    public void OnSuccess(String s) {
                        view.onCleanCacheSuccess(s);
                    }

                    @Override
                    public void onError(ExceptionHelper.ResponseThrowable e) {
                        view.onCleanCacheError(e.message);
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
                view.onGetHomePageSuccess(s);
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

    /**
     * author: sca_tl
     * description: 请求权限
     */
    public void requestPermission(FragmentActivity activity, final int action, String... permissions) {
        CommonUtil.requestPermission(activity, new OnPermission() {
            @Override
            public void onGranted() {
                view.onPermissionGranted(action);
            }

            @Override
            public void onRefusedWithNoMoreRequest() {
                view.onPermissionRefusedWithNoMoreRequest();
            }

            @Override
            public void onRefused() {
                view.onPermissionRefused();
            }
        }, permissions);
    }

    public void downDailyPicConfirm(FragmentActivity context) {
        final AlertDialog dialog = new AlertDialog.Builder(context)
                .setPositiveButton("下载", null)
                .setNegativeButton("取消", null )
                .setTitle("下载图片")
                .setMessage("确认要下载该图片吗？图片资源来自：https://cn.bing.com/")
                .create();
        dialog.setOnShowListener(dialogInterface -> {
            Button p = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            p.setOnClickListener(v -> {
                requestPermission(context,
                        HomeFragment.DOWNLOAD_PIC, Manifest.permission.READ_EXTERNAL_STORAGE);
                dialog.dismiss();

            });
        });
        dialog.show();
    }
    


}
