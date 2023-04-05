package com.scatl.uestcbbs.module.user.presenter;

import android.content.Context;

import com.scatl.uestcbbs.api.ApiConstant;
import com.scatl.uestcbbs.base.BasePresenter;
import com.scatl.uestcbbs.entity.UserFriendBean;
import com.scatl.uestcbbs.helper.ExceptionHelper;
import com.scatl.uestcbbs.helper.rxhelper.Observer;
import com.scatl.uestcbbs.module.user.model.UserModel;
import com.scatl.uestcbbs.module.user.view.UserFriendView;
import com.scatl.uestcbbs.util.SharePrefUtil;

import io.reactivex.disposables.Disposable;

/**
 * author: sca_tl
 * description:
 * date: 2020/2/5 16:42
 */
public class UserFriendPresenter extends BasePresenter<UserFriendView> {

    private UserModel userModel = new UserModel();

    public void getUserFriend(int uid, String type, Context context) {
        userModel.getUserFriend(1, 1000, uid, type, new Observer<UserFriendBean>() {
            @Override
            public void OnSuccess(UserFriendBean userFriendBean) {
                if (userFriendBean.rs == ApiConstant.Code.SUCCESS_CODE) {
                    view.onGetUserFriendSuccess(userFriendBean);
                }

                if (userFriendBean.rs == ApiConstant.Code.ERROR_CODE) {
                    view.onGetUserFriendError(userFriendBean.head.errInfo);
                }
            }

            @Override
            public void onError(ExceptionHelper.ResponseThrowable e) {
                view.onGetUserFriendError(e.message);
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
