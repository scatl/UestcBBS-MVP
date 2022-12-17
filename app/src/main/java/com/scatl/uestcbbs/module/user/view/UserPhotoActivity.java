package com.scatl.uestcbbs.module.user.view;

import android.content.Intent;

import androidx.appcompat.widget.Toolbar;

import com.scatl.uestcbbs.R;
import com.scatl.uestcbbs.annotation.ToastType;
import com.scatl.uestcbbs.base.BaseActivity;
import com.scatl.uestcbbs.callback.OnRefresh;
import com.scatl.uestcbbs.entity.PhotoListBean;
import com.scatl.uestcbbs.module.user.presenter.UserPhotoPresenter;
import com.scatl.uestcbbs.util.Constant;
import com.scatl.uestcbbs.util.RefreshUtil;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.RefreshState;

import java.util.ArrayList;

public class UserPhotoActivity extends BaseActivity<UserPhotoPresenter> implements UserPhotoView {

    private SmartRefreshLayout refreshLayout;
    private Toolbar toolbar;

    private int uid, albumId;
    private String albumName;

    @Override
    protected void getIntent(Intent intent) {
        super.getIntent(intent);
        if (intent != null) {
            uid = intent.getIntExtra(Constant.IntentKey.USER_ID, Integer.MAX_VALUE);
            albumId = intent.getIntExtra(Constant.IntentKey.ALBUM_ID, Integer.MAX_VALUE);
            albumName = intent.getStringExtra(Constant.IntentKey.ALBUM_NAME);
        }
    }

    @Override
    protected int setLayoutResourceId() {
        return R.layout.activity_user_photo;
    }

    @Override
    protected void findView() {
        toolbar = findViewById(R.id.toolbar);
        refreshLayout = findViewById(R.id.user_photo_refresh);
    }

    @Override
    protected void initView() {
        super.initView();
        refreshLayout.setEnableLoadMore(false);
        refreshLayout.autoRefresh(0, 300, 1, false);
    }

    @Override
    protected UserPhotoPresenter initPresenter() {
        return new UserPhotoPresenter();
    }

    @Override
    protected void setOnRefreshListener() {
        RefreshUtil.setOnRefreshListener(this, refreshLayout, new OnRefresh() {
            @Override
            public void onRefresh(RefreshLayout refreshLayout) {
                presenter.getUserPhotoList(uid, albumId, UserPhotoActivity.this);
            }

            @Override
            public void onLoadMore(RefreshLayout refreshLayout) { }
        });
    }


    @Override
    public void onGetUserPhotoSuccess(PhotoListBean photoListBean) {
        if (refreshLayout.getState() == RefreshState.Refreshing) {
            refreshLayout.finishRefreshWithNoMoreData();
        }
        toolbar.setTitle(albumName + "（" + photoListBean.list.size() + "）");

        ArrayList<String> urls = new ArrayList<>();
        for (int i = 0; i < photoListBean.list.size(); i ++) {
            urls.add(photoListBean.list.get(i).thumb_pic);
        }

    }

    @Override
    public void onGetUserPhotoError(String msg) {
        if (refreshLayout.getState() == RefreshState.Refreshing) {
            refreshLayout.finishRefresh();
        }
        showToast(msg, ToastType.TYPE_ERROR);
    }


}
