package com.scatl.uestcbbs.module.magic.view;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.os.Bundle;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.scatl.uestcbbs.R;
import com.scatl.uestcbbs.base.BaseActivity;
import com.scatl.uestcbbs.base.BaseEvent;
import com.scatl.uestcbbs.callback.OnRefresh;
import com.scatl.uestcbbs.entity.MineMagicBean;
import com.scatl.uestcbbs.module.magic.adapter.MineMagicAdapter;
import com.scatl.uestcbbs.module.magic.presenter.MineMagicPresenter;
import com.scatl.uestcbbs.util.Constant;
import com.scatl.uestcbbs.util.RefreshUtil;
import com.scatl.uestcbbs.util.TimeUtil;
import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.scwang.smart.refresh.layout.api.RefreshLayout;

import java.util.ArrayList;

public class MineMagicActivity extends BaseActivity<MineMagicPresenter> implements MineMagicView{

    private SmartRefreshLayout refreshLayout;
    private RecyclerView recyclerView;
    private MineMagicAdapter mineMagicAdapter;
    private TextView hint;
    private Toolbar toolbar;

    @Override
    protected int setLayoutResourceId() {
        return R.layout.activity_mine_magic;
    }

    @Override
    protected void findView() {
        refreshLayout = findViewById(R.id.mine_magic_refresh);
        recyclerView = findViewById(R.id.mine_magic_rv);
        hint = findViewById(R.id.mine_magic_hint);
        toolbar = findViewById(R.id.mine_magic_toolbar);
    }

    @Override
    protected void initView() {
        super.initView();
        mineMagicAdapter = new MineMagicAdapter();
        recyclerView.setAdapter(mineMagicAdapter);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        recyclerView.setLayoutAnimation(AnimationUtils.loadLayoutAnimation(this, R.anim.layout_animation_from_top));

        refreshLayout.setEnableLoadMore(false);
        refreshLayout.autoRefresh(0, 300, 1 ,false);
    }

    @Override
    protected MineMagicPresenter initPresenter() {
        return new MineMagicPresenter();
    }

    @Override
    protected void setOnItemClickListener() {
        mineMagicAdapter.addOnItemChildClickListener(R.id.magic_use_btn, new BaseQuickAdapter.OnItemChildClickListener<MineMagicBean.ItemList>() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<MineMagicBean.ItemList, ?> baseQuickAdapter, @NonNull View view, int i) {
                Bundle bundle = new Bundle();
                bundle.putString(Constant.IntentKey.MAGIC_ID, mineMagicAdapter.getItems().get(i).magicId);
                UseMagicFragment.getInstance(bundle).show(getSupportFragmentManager(), TimeUtil.getStringMs());
            }
        });
    }

    @Override
    protected void setOnRefreshListener() {
        RefreshUtil.setOnRefreshListener(this, refreshLayout, new OnRefresh() {
            @Override
            public void onRefresh(RefreshLayout refreshLayout) {
                presenter.getMineMagic();
            }

            @Override
            public void onLoadMore(RefreshLayout refreshLayout) { }
        });
    }

    @Override
    public void onGetMineMagicSuccess(MineMagicBean mineMagicBean) {
        hint.setText("");
        mineMagicAdapter.submitList(mineMagicBean.itemLists);
        recyclerView.scheduleLayoutAnimation();
        refreshLayout.finishRefresh(true);
    }

    @Override
    public void onGetMineMagicError(String msg) {
        mineMagicAdapter.submitList(new ArrayList<>());
        refreshLayout.finishRefresh(false);
        hint.setText(msg);
    }

    @Override
    protected boolean registerEventBus() {
        return true;
    }

    @Override
    public void onEventBusReceived(BaseEvent baseEvent) {
        if (baseEvent.eventCode == BaseEvent.EventCode.USE_MAGIC_SUCCESS) {
            refreshLayout.autoRefresh(0, 300, 1 ,false);
        }
    }
}