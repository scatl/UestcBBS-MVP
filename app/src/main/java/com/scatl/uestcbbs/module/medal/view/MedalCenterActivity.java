package com.scatl.uestcbbs.module.medal.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.os.Bundle;
import android.util.Log;
import android.view.animation.AnimationUtils;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.scatl.uestcbbs.R;
import com.scatl.uestcbbs.annotation.ToastType;
import com.scatl.uestcbbs.base.BaseActivity;
import com.scatl.uestcbbs.base.BasePresenter;
import com.scatl.uestcbbs.callback.OnRefresh;
import com.scatl.uestcbbs.entity.MedalBean;
import com.scatl.uestcbbs.module.home.adapter.OnLineUserAdapter;
import com.scatl.uestcbbs.module.medal.adapter.MedalCenterAdapter;
import com.scatl.uestcbbs.module.medal.presenter.MedalCenterPresenter;
import com.scatl.uestcbbs.util.RefreshUtil;
import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.scwang.smart.refresh.layout.api.RefreshLayout;

import java.util.Collections;

public class MedalCenterActivity extends BaseActivity<MedalCenterPresenter> implements MedalCenterView{

    private SmartRefreshLayout refreshLayout;
    private RecyclerView recyclerView;
    private MedalCenterAdapter medalCenterAdapter;
    private TextView hint;
    private Toolbar toolbar;

    @Override
    protected int setLayoutResourceId() {
        return R.layout.activity_medal_center;
    }

    @Override
    protected void findView() {
        refreshLayout = findViewById(R.id.medal_center_refresh);
        recyclerView = findViewById(R.id.medal_center_rv);
        hint = findViewById(R.id.medal_center_hint);
        toolbar = findViewById(R.id.toolbar);
    }

    @Override
    protected void initView() {
        super.initView();
        medalCenterAdapter = new MedalCenterAdapter(R.layout.item_medal_center);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        recyclerView.setAdapter(medalCenterAdapter);

        refreshLayout.setEnableLoadMore(false);
        refreshLayout.autoRefresh(0, 300, 1 ,false);

        showToast("目前仅供查看，不支持交易", ToastType.TYPE_NORMAL);
    }

    @Override
    protected MedalCenterPresenter initPresenter() {
        return new MedalCenterPresenter();
    }

    @Override
    protected void setOnRefreshListener() {
        RefreshUtil.setOnRefreshListener(this, refreshLayout, new OnRefresh() {
            @Override
            public void onRefresh(RefreshLayout refreshLayout) {
                presenter.getMedalCenter();
            }

            @Override
            public void onLoadMore(RefreshLayout refreshLayout) {

            }
        });
    }

    @Override
    public void onGetMedalCenterDataSuccess(MedalBean medalBean) {
        hint.setText("");
        medalCenterAdapter.setNewData(medalBean.medalCenterBeans);
        recyclerView.setLayoutAnimation(AnimationUtils.loadLayoutAnimation(this, R.anim.layout_animation_scale_in));
        refreshLayout.finishRefresh(true);
    }

    @Override
    public void onGetMedalCenterDataError(String msg) {
        refreshLayout.finishRefresh(false);
        hint.setText(msg);
    }
}