package com.scatl.uestcbbs.module.user.presenter;

import android.content.Context;

import com.scatl.uestcbbs.api.ApiConstant;
import com.scatl.uestcbbs.base.BasePresenter;
import com.scatl.uestcbbs.entity.AlbumListBean;
import com.scatl.uestcbbs.entity.PhotoListBean;
import com.scatl.uestcbbs.helper.ExceptionHelper;
import com.scatl.uestcbbs.helper.rxhelper.Observer;
import com.scatl.uestcbbs.helper.rxhelper.SubscriptionManager;
import com.scatl.uestcbbs.module.user.model.UserModel;
import com.scatl.uestcbbs.module.user.view.UserPhotoView;
import com.scatl.uestcbbs.util.SharePrefUtil;

import io.reactivex.disposables.Disposable;

/**
 * author: sca_tl
 * description:
 * date: 2020/3/14 14:08
 */
public class UserPhotoPresenter extends BasePresenter<UserPhotoView> {
    private UserModel userModel = new UserModel();

    public void getUserPhotoList(int uid, int albumId, Context context) {
        userModel.getPhotoList(uid, albumId,
                SharePrefUtil.getToken(context),
                SharePrefUtil.getSecret(context), new Observer<PhotoListBean>() {
                    @Override
                    public void OnSuccess(PhotoListBean photoListBean) {
                        if (photoListBean.rs == ApiConstant.Code.SUCCESS_CODE) {
                            view.onGetUserPhotoSuccess(photoListBean);
                        }
                        if (photoListBean.rs == ApiConstant.Code.ERROR_CODE) {
                            view.onGetUserPhotoError(photoListBean.head.errInfo);
                        }
                    }

                    @Override
                    public void onError(ExceptionHelper.ResponseThrowable e) {
                        view.onGetUserPhotoError(e.message);
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
