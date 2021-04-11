package com.scatl.uestcbbs.module.post.presenter.postdetail2;

import android.content.Context;

import com.scatl.uestcbbs.api.ApiConstant;
import com.scatl.uestcbbs.base.BasePresenter;
import com.scatl.uestcbbs.entity.PostDetailBean;
import com.scatl.uestcbbs.helper.ExceptionHelper;
import com.scatl.uestcbbs.helper.rxhelper.Observer;
import com.scatl.uestcbbs.module.post.model.PostModel;
import com.scatl.uestcbbs.module.post.view.postdetail2.P2DianZanView;
import com.scatl.uestcbbs.util.SharePrefUtil;

import io.reactivex.disposables.Disposable;

/**
 * author: sca_tl
 * date: 2021/3/31 19:50
 * description:
 */
public class P2DianZanPresenter extends BasePresenter<P2DianZanView> {
    private PostModel postModel = new PostModel();

    public void getPostDetail(int page,
                              int pageSize,
                              int order,
                              int topicId,
                              int authorId,
                              Context context) {
        postModel.getPostDetail(page, pageSize, order, topicId, authorId,
                SharePrefUtil.getToken(context),
                SharePrefUtil.getSecret(context),
                new Observer<PostDetailBean>() {
                    @Override
                    public void OnSuccess(PostDetailBean postDetailBean) {
                        if (postDetailBean.rs == ApiConstant.Code.SUCCESS_CODE) {
                            view.onGetPostDetailSuccess(postDetailBean);
                        }

                        if (postDetailBean.rs == ApiConstant.Code.ERROR_CODE) {
                            view.onGetPostDetailError(postDetailBean.head.errInfo);
                        }
                    }

                    @Override
                    public void onError(ExceptionHelper.ResponseThrowable e) {
                        view.onGetPostDetailError(e.message);
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
}
