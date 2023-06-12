package com.scatl.uestcbbs.module.magic.view;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.google.android.material.appbar.MaterialToolbar;
import com.scatl.uestcbbs.R;
import com.scatl.uestcbbs.base.BaseActivity;
import com.scatl.uestcbbs.base.BasePresenter;
import com.scatl.uestcbbs.callback.OnRefresh;
import com.scatl.uestcbbs.entity.MagicShopBean;
import com.scatl.uestcbbs.module.magic.adapter.MagicShopAdapter;
import com.scatl.uestcbbs.module.magic.presenter.MagicShopPresenter;
import com.scatl.uestcbbs.util.Constant;
import com.scatl.uestcbbs.util.RefreshUtil;
import com.scatl.uestcbbs.util.TimeUtil;
import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.scwang.smart.refresh.layout.api.RefreshLayout;

public class MagicShopActivity extends BaseActivity implements MagicShopView{

    private SmartRefreshLayout refreshLayout;
    private RecyclerView recyclerView;
    private MagicShopAdapter magicShopAdapter;
    private TextView hint;
    private MaterialToolbar toolbar;

    private MagicShopPresenter magicShopPresenter;

    @Override
    protected int setLayoutResourceId() {
        return R.layout.activity_magic_shop;
    }

    @Override
    protected void findView() {
        refreshLayout = findViewById(R.id.magic_shop_refresh);
        recyclerView = findViewById(R.id.magic_shop_rv);
        hint = findViewById(R.id.magic_shop_hint);
        toolbar = findViewById(R.id.magic_shop_toolbar);
    }

    @Override
    protected void initView() {
        magicShopPresenter = (MagicShopPresenter) presenter;

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.mine_magic) {
                    startActivity(new Intent(MagicShopActivity.this, MineMagicActivity.class));
                }
                return true;
            }
        });

        magicShopAdapter = new MagicShopAdapter();
        recyclerView.setAdapter(magicShopAdapter);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        recyclerView.setLayoutAnimation(AnimationUtils.loadLayoutAnimation(this, R.anim.layout_animation_scale_in));

        refreshLayout.setEnableLoadMore(false);
        refreshLayout.autoRefresh(0, 300, 1 ,false);

    }

    @Override
    protected BasePresenter initPresenter() {
        return new MagicShopPresenter();
    }

    @Override
    protected void onClickListener(View v) {

    }

    @Override
    protected void setOnItemClickListener() {
        magicShopAdapter.addOnItemChildClickListener(R.id.buy_btn, new BaseQuickAdapter.OnItemChildClickListener<MagicShopBean.ItemList>() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<MagicShopBean.ItemList, ?> baseQuickAdapter, @NonNull View view, int i) {
                Bundle bundle = new Bundle();
                bundle.putString(Constant.IntentKey.MAGIC_ID, magicShopAdapter.getItems().get(i).id);
                MagicDetailFragment.getInstance(bundle).show(getSupportFragmentManager(), TimeUtil.getStringMs());
            }
        });
    }

    @Override
    protected void setOnRefreshListener() {
        RefreshUtil.setOnRefreshListener(this, refreshLayout, new OnRefresh() {
            @Override
            public void onRefresh(RefreshLayout refreshLayout) {
                magicShopPresenter.getMagicShop();
            }

            @Override
            public void onLoadMore(RefreshLayout refreshLayout) { }
        });
    }

    @Override
    public void onGetMagicShopSuccess(MagicShopBean magicShopBean) {
        hint.setText("");
        magicShopAdapter.submitList(magicShopBean.itemLists);
        recyclerView.scheduleLayoutAnimation();
        refreshLayout.finishRefresh(true);
    }

    @Override
    public void onGetMagicShopError(String msg) {
        refreshLayout.finishRefresh(false);
        hint.setText(msg);
    }

}