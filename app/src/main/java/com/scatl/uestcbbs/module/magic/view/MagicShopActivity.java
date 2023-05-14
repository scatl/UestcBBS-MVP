package com.scatl.uestcbbs.module.magic.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.appbar.MaterialToolbar;
import com.scatl.uestcbbs.R;
import com.scatl.uestcbbs.base.BaseActivity;
import com.scatl.uestcbbs.base.BasePresenter;
import com.scatl.uestcbbs.callback.OnRefresh;
import com.scatl.uestcbbs.entity.MagicShopBean;
import com.scatl.uestcbbs.entity.MedalBean;
import com.scatl.uestcbbs.entity.MineMagicBean;
import com.scatl.uestcbbs.module.magic.adapter.MagicShopAdapter;
import com.scatl.uestcbbs.module.magic.adapter.MineMagicAdapter;
import com.scatl.uestcbbs.module.magic.presenter.MagicShopPresenter;
import com.scatl.uestcbbs.module.medal.adapter.MedalCenterAdapter;
import com.scatl.uestcbbs.module.medal.presenter.MedalCenterPresenter;
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

        magicShopAdapter = new MagicShopAdapter(R.layout.item_magic_shop);
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
        magicShopAdapter.setOnItemChildClickListener((adapter, view, position) -> {
            Bundle bundle = new Bundle();
            bundle.putString(Constant.IntentKey.MAGIC_ID, magicShopAdapter.getData().get(position).id);
            MagicDetailFragment.getInstance(bundle).show(getSupportFragmentManager(), TimeUtil.getStringMs());
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
        magicShopAdapter.setNewData(magicShopBean.itemLists);
        recyclerView.scheduleLayoutAnimation();
        refreshLayout.finishRefresh(true);
    }

    @Override
    public void onGetMagicShopError(String msg) {
        refreshLayout.finishRefresh(false);
        hint.setText(msg);
    }

}