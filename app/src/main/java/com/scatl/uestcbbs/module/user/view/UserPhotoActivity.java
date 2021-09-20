package com.scatl.uestcbbs.module.user.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.scatl.uestcbbs.R;
import com.scatl.uestcbbs.annotation.ToastType;
import com.scatl.uestcbbs.base.BaseActivity;
import com.scatl.uestcbbs.base.BasePresenter;
import com.scatl.uestcbbs.callback.OnRefresh;
import com.scatl.uestcbbs.entity.PhotoListBean;
import com.scatl.uestcbbs.module.user.presenter.UserPhotoPresenter;
import com.scatl.uestcbbs.util.Constant;
import com.scatl.uestcbbs.util.ImageUtil;
import com.scatl.uestcbbs.util.RefreshUtil;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.RefreshState;

import java.util.ArrayList;
import java.util.List;

import cn.bingoogolapple.photopicker.widget.BGANinePhotoLayout;

public class UserPhotoActivity extends BaseActivity implements UserPhotoView {

    private SmartRefreshLayout refreshLayout;
    private Toolbar toolbar;
    private BGANinePhotoLayout ninePhotoLayout;

    private UserPhotoPresenter userPhotoPresenter;

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
        toolbar = findViewById(R.id.user_photo_toolbar);
        refreshLayout = findViewById(R.id.user_photo_refresh);
        ninePhotoLayout = findViewById(R.id.user_photo_layout);
    }

    @Override
    protected void initView() {
        userPhotoPresenter = (UserPhotoPresenter) presenter;

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        refreshLayout.setEnableLoadMore(false);
        refreshLayout.autoRefresh(0, 300, 1, false);
    }

    @Override
    protected BasePresenter initPresenter() {
        return new UserPhotoPresenter();
    }

    @Override
    protected void setOnRefreshListener() {
        RefreshUtil.setOnRefreshListener(this, refreshLayout, new OnRefresh() {
            @Override
            public void onRefresh(RefreshLayout refreshLayout) {
                userPhotoPresenter.getUserPhotoList(uid, albumId, UserPhotoActivity.this);
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
        ninePhotoLayout.setData(urls);
        ninePhotoLayout.setDelegate(f);
    }

    @Override
    public void onGetUserPhotoError(String msg) {
        if (refreshLayout.getState() == RefreshState.Refreshing) {
            refreshLayout.finishRefresh();
        }
        showToast(msg, ToastType.TYPE_ERROR);
    }

    private BGANinePhotoLayout.Delegate f = new BGANinePhotoLayout.Delegate(){
        @Override
        public void onClickNinePhotoItem(BGANinePhotoLayout ninePhotoLayout, View view, int position, String model, List<String> models) {
            ImageUtil.showImages(UserPhotoActivity.this, models, position);
        }

        @Override
        public void onClickExpand(BGANinePhotoLayout ninePhotoLayout, View view, int position, String model, List<String> models) {}
    };
}
