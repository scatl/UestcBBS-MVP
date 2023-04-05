package com.scatl.uestcbbs.module.user.presenter;

import android.content.Context;

import com.scatl.uestcbbs.api.ApiConstant;
import com.scatl.uestcbbs.base.BasePresenter;
import com.scatl.uestcbbs.entity.UserPostBean;
import com.scatl.uestcbbs.helper.ExceptionHelper;
import com.scatl.uestcbbs.helper.rxhelper.Observer;
import com.scatl.uestcbbs.module.user.model.UserModel;
import com.scatl.uestcbbs.module.user.view.UserPostView;
import com.scatl.uestcbbs.util.SharePrefUtil;

import io.reactivex.disposables.Disposable;

/**
 * author: sca_tl
 * description:
 * date: 2020/2/4 16:21
 */
public class UserPostPresenter extends BasePresenter<UserPostView> {
    private UserModel userModel = new UserModel();

    public void userPost(int page,
                         int pageSize,
                         int uid,
                         String type,
                         Context context) {
        userModel.getUserPost(page, pageSize, uid, type,
                new Observer<UserPostBean>() {
                    @Override
                    public void OnSuccess(UserPostBean userPostBean) {
                        if (userPostBean.rs == ApiConstant.Code.SUCCESS_CODE) {
                            view.onGetUserPostSuccess(userPostBean);
                        }

                        if (userPostBean.rs == ApiConstant.Code.ERROR_CODE) {
                            view.onGetUserPostError(userPostBean.head.errInfo);
                        }
                    }

                    @Override
                    public void onError(ExceptionHelper.ResponseThrowable e) {
                        view.onGetUserPostError(e.message);
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
