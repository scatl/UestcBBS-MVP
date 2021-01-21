package com.scatl.uestcbbs.module.home.presenter;

import android.content.Context;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.scatl.uestcbbs.annotation.PostSortByType;
import com.scatl.uestcbbs.api.ApiConstant;
import com.scatl.uestcbbs.base.BasePresenter;
import com.scatl.uestcbbs.entity.HotPostBean;
import com.scatl.uestcbbs.entity.SimplePostListBean;
import com.scatl.uestcbbs.helper.ExceptionHelper;
import com.scatl.uestcbbs.helper.rxhelper.Observer;
import com.scatl.uestcbbs.module.home.model.HomeModel;
import com.scatl.uestcbbs.module.home.view.PostListFragment;
import com.scatl.uestcbbs.module.home.view.PostListView;
import com.scatl.uestcbbs.util.Constant;
import com.scatl.uestcbbs.util.FileUtil;
import com.scatl.uestcbbs.util.SharePrefUtil;

import java.io.File;

import io.reactivex.disposables.Disposable;

/**
 * author: sca_tl
 * description:
 * date: 2020/3/2 14:15
 */
public class PostListPresenter extends BasePresenter<PostListView> {

    HomeModel homeModel = new HomeModel();

    public void getSimplePostList(int page, int pageSize, String sortby, Context context){
        homeModel.getSimplePostList(page, pageSize, 0, sortby,
                SharePrefUtil.getToken(context),
                SharePrefUtil.getSecret(context), new Observer<SimplePostListBean>() {
                    @Override
                    public void OnSuccess(SimplePostListBean simplePostListBean) {
                        if (simplePostListBean.rs == ApiConstant.Code.SUCCESS_CODE) {
                            view.onGetSimplePostSuccess(simplePostListBean);
                        }
                        if (simplePostListBean.rs == ApiConstant.Code.ERROR_CODE) {
                            view.onGetSimplePostError(simplePostListBean.head.errInfo);
                        }
                    }

                    @Override
                    public void onError(ExceptionHelper.ResponseThrowable e) {
                        view.onGetSimplePostError(e.message);
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

    public void getHotPostList(int page, int pageSize, Context context){
        homeModel.getHotPostList(page, pageSize, 2,
                SharePrefUtil.getToken(context),
                SharePrefUtil.getSecret(context), new Observer<HotPostBean>() {
                    @Override
                    public void OnSuccess(HotPostBean hotPostBean) {
                        if (hotPostBean.rs == ApiConstant.Code.SUCCESS_CODE) {
                            view.onGetHotPostSuccess(hotPostBean);
                        }
                        if (hotPostBean.rs == ApiConstant.Code.ERROR_CODE) {
                            view.onGetHotPostError(hotPostBean.head.errInfo);
                        }
                    }

                    @Override
                    public void onError(ExceptionHelper.ResponseThrowable e) {
                        view.onGetHotPostError(e.message);
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

    public void initSavedData(Context context, String type) {
        if (type.equals(PostSortByType.TYPE_NEW)) {
            String newPostData = FileUtil.readTextFile(
                    new File(context.getExternalFilesDir(Constant.AppPath.JSON_PATH),
                            Constant.FileName.HOME1_NEW_POST_JSON));
            if (JSON.isValidObject(newPostData)) {
                JSONObject jsonObject = JSONObject.parseObject(newPostData);
                view.onGetSimplePostSuccess(JSON.toJavaObject(jsonObject, SimplePostListBean.class));
//                simplePostAdapter.setNewData(JSON.toJavaObject(jsonObject, SimplePostListBean.class).list);
            }
        }

        if (type.equals(PostSortByType.TYPE_ALL)) {
            String allPostData = FileUtil.readTextFile(
                    new File(context.getExternalFilesDir(Constant.AppPath.JSON_PATH),
                            Constant.FileName.HOME1_ALL_POST_JSON));
            if (JSON.isValidObject(allPostData)) {
                JSONObject jsonObject = JSONObject.parseObject(allPostData);
                view.onGetSimplePostSuccess(JSON.toJavaObject(jsonObject, SimplePostListBean.class));
//                simplePostAdapter.setNewData(JSON.toJavaObject(jsonObject, SimplePostListBean.class).list);
            }
        }

        if (type.equals(PostSortByType.TYPE_HOT)) {
            String hotPostData = FileUtil.readTextFile(
                    new File(context.getExternalFilesDir(Constant.AppPath.JSON_PATH),
                            Constant.FileName.HOME1_HOT_POST_JSON));
            if (JSON.isValidObject(hotPostData)) {
                JSONObject jsonObject = JSONObject.parseObject(hotPostData);
                view.onGetHotPostSuccess(JSON.toJavaObject(jsonObject, HotPostBean.class));

//                hotPostAdapter.setNewData(JSON.toJavaObject(jsonObject, HotPostBean.class).list);
            }
        }

    }



}
