package com.scatl.uestcbbs.module.search.presenter;

import android.app.AlertDialog;
import android.content.Context;

import com.scatl.uestcbbs.api.ApiConstant;
import com.scatl.uestcbbs.base.BasePresenter;
import com.scatl.uestcbbs.entity.SearchPostBean;
import com.scatl.uestcbbs.entity.SearchUserBean;
import com.scatl.uestcbbs.helper.ExceptionHelper;
import com.scatl.uestcbbs.helper.rxhelper.Observer;
import com.scatl.uestcbbs.helper.rxhelper.SubscriptionManager;
import com.scatl.uestcbbs.module.search.model.SearchModel;
import com.scatl.uestcbbs.module.search.view.SearchView;
import com.scatl.uestcbbs.util.SharePrefUtil;

import io.reactivex.disposables.Disposable;

public class SearchPresenter extends BasePresenter<SearchView> {

    private SearchModel searchModel = new SearchModel();

    public void searchUser(int page, int pageSize, String keyword, Context context) {
        searchModel.searchUser(page, pageSize, 0, keyword,
                SharePrefUtil.getToken(context),
                SharePrefUtil.getSecret(context),
                new Observer<SearchUserBean>() {
                    @Override
                    public void OnSuccess(SearchUserBean searchUserBean) {
                        if (searchUserBean.rs == ApiConstant.Code.SUCCESS_CODE) {
                            view.onSearchUserSuccess(searchUserBean);
                        }
                        if (searchUserBean.rs == ApiConstant.Code.ERROR_CODE) {
                            view.onSearchUserError(searchUserBean.head.errInfo);
                        }
                    }

                    @Override
                    public void onError(ExceptionHelper.ResponseThrowable e) {
                        view.onSearchUserError(e.message);
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

    public void searchPost(int page, int pageSize, String keyword, Context context) {
        searchModel.searchPost(page, pageSize, keyword,
                SharePrefUtil.getToken(context),
                SharePrefUtil.getSecret(context),
                new Observer<SearchPostBean>() {
                    @Override
                    public void OnSuccess(SearchPostBean searchPostBean) {
                        if (searchPostBean.rs == ApiConstant.Code.SUCCESS_CODE) {
                            view.onSearchPostSuccess(searchPostBean);
                        }
                        if (searchPostBean.rs == ApiConstant.Code.ERROR_CODE) {
                            view.onSearchPostError(searchPostBean.head.errInfo);
                        }
                    }

                    @Override
                    public void onError(ExceptionHelper.ResponseThrowable e) {
                        view.onSearchPostError(e.message);
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
