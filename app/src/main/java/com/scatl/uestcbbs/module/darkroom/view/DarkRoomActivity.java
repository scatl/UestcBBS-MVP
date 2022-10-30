package com.scatl.uestcbbs.module.darkroom.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.jaeger.library.StatusBarUtil;
import com.scatl.uestcbbs.R;
import com.scatl.uestcbbs.annotation.PostSortByType;
import com.scatl.uestcbbs.base.BaseActivity;
import com.scatl.uestcbbs.base.BasePresenter;
import com.scatl.uestcbbs.callback.OnRefresh;
import com.scatl.uestcbbs.custom.MyLinearLayoutManger;
import com.scatl.uestcbbs.entity.DarkRoomBean;
import com.scatl.uestcbbs.module.darkroom.adapter.DarkRoomAdapter;
import com.scatl.uestcbbs.module.darkroom.presenter.DarkRoomPresenter;
import com.scatl.uestcbbs.module.post.adapter.HotPostAdapter;
import com.scatl.uestcbbs.module.user.view.UserDetailActivity;
import com.scatl.uestcbbs.util.Constant;
import com.scatl.uestcbbs.util.RefreshUtil;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;

import java.util.List;

public class DarkRoomActivity extends BaseActivity<DarkRoomPresenter> implements DarkRoomView{

    Toolbar toolbar;
    SmartRefreshLayout refreshLayout;
    RecyclerView recyclerView;
    DarkRoomAdapter darkRoomAdapter;
    TextView hint;

    @Override
    protected int setLayoutResourceId() {
        return R.layout.activity_dark_room;
    }

    @Override
    protected void findView() {
        toolbar = findViewById(R.id.dark_room_toolbar);
        refreshLayout = findViewById(R.id.dark_room_refresh);
        recyclerView = findViewById(R.id.dark_room_rv);
        hint = findViewById(R.id.dark_room_hint);
    }

    @Override
    protected void initView() {
        super.initView();

        refreshLayout.setEnableLoadMore(false);

        darkRoomAdapter = new DarkRoomAdapter(R.layout.item_dark_room);
        recyclerView.setLayoutManager(new MyLinearLayoutManger(this));
        recyclerView.setLayoutAnimation(AnimationUtils.loadLayoutAnimation(this, R.anim.layout_animation_scale_in));
        recyclerView.setAdapter(darkRoomAdapter);
        recyclerView.scheduleLayoutAnimation();

        refreshLayout.autoRefresh(0, 300, 1, false);
    }

    @Override
    protected Toolbar getToolbar() {
        return toolbar;
    }

    @Override
    protected DarkRoomPresenter initPresenter() {
        return new DarkRoomPresenter();
    }

    @Override
    protected void setOnItemClickListener() {
        darkRoomAdapter.setOnItemChildClickListener((adapter, view, position) -> {
            if (view.getId() == R.id.item_dark_room_user_name) {
                Intent intent = new Intent(this, UserDetailActivity.class);
                intent.putExtra(Constant.IntentKey.USER_ID, darkRoomAdapter.getData().get(position).uid);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void setOnRefreshListener() {
        RefreshUtil.setOnRefreshListener(this, refreshLayout, new OnRefresh() {
            @Override
            public void onRefresh(RefreshLayout refreshLayout) {
                presenter.getDarkRoomList();
            }

            @Override
            public void onLoadMore(RefreshLayout refreshLayout) { }
        });
    }

    @Override
    public void onGetDarkRoomDataSuccess(List<DarkRoomBean> darkRoomBeanList) {
        refreshLayout.finishRefresh();
        recyclerView.scheduleLayoutAnimation();
        darkRoomAdapter.setNewData(darkRoomBeanList);
    }

    @Override
    public void onGetDarkRoomDataError(String msg) {
        hint.setText(msg);
    }

    @Override
    protected void setStatusBar() {
        StatusBarUtil.setColor(this, Color.parseColor("#303030"), 0);
    }
}