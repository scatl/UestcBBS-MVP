package com.scatl.uestcbbs.module.user.presenter;

import android.content.Context;

import com.scatl.uestcbbs.api.ApiConstant;
import com.scatl.uestcbbs.base.BasePresenter;
import com.scatl.uestcbbs.entity.AtUserListBean;
import com.scatl.uestcbbs.helper.ExceptionHelper;
import com.scatl.uestcbbs.helper.rxhelper.Observer;
import com.scatl.uestcbbs.helper.rxhelper.SubscriptionManager;
import com.scatl.uestcbbs.module.user.model.UserModel;
import com.scatl.uestcbbs.module.user.view.AtUserListView;
import com.scatl.uestcbbs.util.SharePrefUtil;

import io.reactivex.disposables.Disposable;


public class AtUserListPresenter extends BasePresenter<AtUserListView> {

    private UserModel userModel = new UserModel();

    public void getAtUSerList(int page, int pageSize, Context context) {
        userModel.getAtUserList(page, pageSize,
                SharePrefUtil.getToken(context),
                SharePrefUtil.getSecret(context), new Observer<AtUserListBean>() {
                    @Override
                    public void OnSuccess(AtUserListBean atUserListBean) {
                        if (atUserListBean.rs == ApiConstant.Code.SUCCESS_CODE) {
                            view.onGetAtUserListSuccess(atUserListBean);
                        }
                        if (atUserListBean.rs == ApiConstant.Code.ERROR_CODE) {
                            view.onGetAtUserListError(atUserListBean.head.errInfo);
                        }
                    }

                    @Override
                    public void onError(ExceptionHelper.ResponseThrowable e) {
                        view.onGetAtUserListError(e.message);
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
