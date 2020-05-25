package com.scatl.uestcbbs.module.post.presenter;

import android.content.Context;

import com.scatl.uestcbbs.api.ApiConstant;
import com.scatl.uestcbbs.base.BasePresenter;
import com.scatl.uestcbbs.entity.UserPostBean;
import com.scatl.uestcbbs.helper.ExceptionHelper;
import com.scatl.uestcbbs.helper.rxhelper.Observer;
import com.scatl.uestcbbs.module.post.model.PostModel;
import com.scatl.uestcbbs.module.post.view.SelfPostView;
import com.scatl.uestcbbs.util.SharePrefUtil;

import io.reactivex.disposables.Disposable;


/**
 * author: sca_tl
 * description:
 * date: 2020/1/27 12:29
 */
public class SelfPostPresenter extends BasePresenter<SelfPostView> {

    private PostModel postModel = new PostModel();

    public void userPost(int page,
                         int pageSize,
                         String type,
                         Context context) {
        postModel.userPost(page, pageSize, type,
                SharePrefUtil.getUid(context),
                SharePrefUtil.getToken(context),
                SharePrefUtil.getSecret(context),
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
