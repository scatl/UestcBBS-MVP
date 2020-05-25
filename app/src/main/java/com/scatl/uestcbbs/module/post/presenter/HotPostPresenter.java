package com.scatl.uestcbbs.module.post.presenter;

import android.content.Context;

import com.scatl.uestcbbs.api.ApiConstant;
import com.scatl.uestcbbs.base.BasePresenter;
import com.scatl.uestcbbs.entity.HotPostBean;
import com.scatl.uestcbbs.helper.ExceptionHelper;
import com.scatl.uestcbbs.helper.rxhelper.Observer;
import com.scatl.uestcbbs.module.post.model.PostModel;
import com.scatl.uestcbbs.module.post.view.HotPostView;
import com.scatl.uestcbbs.util.SharePrefUtil;

import io.reactivex.disposables.Disposable;


/**
 * author: sca_tl
 * description:
 * date: 2020/2/12 17:40
 */
public class HotPostPresenter extends BasePresenter<HotPostView> {

    private PostModel postModel = new PostModel();

    public void getHotPostList(int page, int pageSize, Context context){
        postModel.getHotPostList(page, pageSize, 2,
                SharePrefUtil.getToken(context),
                SharePrefUtil.getSecret(context), new Observer<HotPostBean>() {
                    @Override
                    public void OnSuccess(HotPostBean hotPostBean) {
                        if (hotPostBean.rs == ApiConstant.Code.SUCCESS_CODE) {
                            view.getHotPostDataSuccess(hotPostBean);
                        }
                        if (hotPostBean.rs == ApiConstant.Code.ERROR_CODE) {
                            view.getHotPostDataError(hotPostBean.head.errInfo);
                        }
                    }

                    @Override
                    public void onError(ExceptionHelper.ResponseThrowable e) {
                        view.getHotPostDataError(e.message);
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
