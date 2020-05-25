package com.scatl.uestcbbs.module.user.presenter;

import android.content.Context;

import com.scatl.uestcbbs.api.ApiConstant;
import com.scatl.uestcbbs.base.BasePresenter;
import com.scatl.uestcbbs.entity.AlbumListBean;
import com.scatl.uestcbbs.helper.ExceptionHelper;
import com.scatl.uestcbbs.helper.rxhelper.Observer;
import com.scatl.uestcbbs.module.user.model.UserModel;
import com.scatl.uestcbbs.module.user.view.UserAlbumView;
import com.scatl.uestcbbs.util.SharePrefUtil;

import io.reactivex.disposables.Disposable;

/**
 * author: sca_tl
 * description:
 * date: 2020/3/14 13:05
 */
public class UserAlbumPresenter extends BasePresenter<UserAlbumView> {

    private UserModel userModel = new UserModel();

    public void getUserAlbumList(int uid, Context context) {
        userModel.getAlbumList(uid,
                SharePrefUtil.getToken(context),
                SharePrefUtil.getSecret(context), new Observer<AlbumListBean>() {
                    @Override
                    public void OnSuccess(AlbumListBean albumListBean) {
                        if (albumListBean.rs == ApiConstant.Code.SUCCESS_CODE) {
                            view.onGetAlbumListSuccess(albumListBean);
                        }
                        if (albumListBean.rs == ApiConstant.Code.ERROR_CODE) {
                            view.onGetAlbumListError(albumListBean.head.errInfo);
                        }
                    }

                    @Override
                    public void onError(ExceptionHelper.ResponseThrowable e) {
                        view.onGetAlbumListError(e.message);
                    }

                    @Override
                    public void OnCompleted() {

                    }

                    @Override
                    public void OnDisposable(Disposable d) {
                        disposable.add(d);
//                        SubscriptionManager.getInstance().add(d);
                    }
                });
    }
}
