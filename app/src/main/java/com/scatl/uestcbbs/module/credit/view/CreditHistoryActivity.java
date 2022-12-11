package com.scatl.uestcbbs.module.credit.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.scatl.uestcbbs.R;
import com.scatl.uestcbbs.annotation.ToastType;
import com.scatl.uestcbbs.base.BaseActivity;
import com.scatl.uestcbbs.base.BasePresenter;
import com.scatl.uestcbbs.callback.OnRefresh;
import com.scatl.uestcbbs.custom.MyLinearLayoutManger;
import com.scatl.uestcbbs.entity.MineCreditBean;
import com.scatl.uestcbbs.module.credit.adapter.MineCreditHistoryAdapter;
import com.scatl.uestcbbs.module.credit.presenter.CreditHistoryPresenter;
import com.scatl.uestcbbs.module.credit.presenter.MineCreditPresenter;
import com.scatl.uestcbbs.module.dayquestion.view.DayQuestionActivity;
import com.scatl.uestcbbs.module.magic.view.MagicShopActivity;
import com.scatl.uestcbbs.module.medal.view.MedalCenterActivity;
import com.scatl.uestcbbs.util.Constant;
import com.scatl.uestcbbs.util.RefreshUtil;
import com.scatl.uestcbbs.util.TimeUtil;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.RefreshState;

import java.util.List;

public class CreditHistoryActivity extends BaseActivity implements CreditHistoryView{

    Toolbar toolbar;
    CreditHistoryPresenter creditHistoryPresenter;
    MineCreditHistoryAdapter mineCreditHistoryAdapter;
    RecyclerView recyclerView;
    SmartRefreshLayout refreshLayout;

    int page = 1;

    @Override
    protected int setLayoutResourceId() {
        return R.layout.activity_credit_history;
    }

    @Override
    protected void findView() {
        toolbar = findViewById(R.id.toolbar);
        recyclerView = findViewById(R.id.credit_history_rv);
        refreshLayout = findViewById(R.id.credit_history_refresh);
    }

    @Override
    protected void initView() {
        super.initView();
        creditHistoryPresenter = (CreditHistoryPresenter) presenter;

        mineCreditHistoryAdapter = new MineCreditHistoryAdapter(R.layout.item_credit_history);
        mineCreditHistoryAdapter.setHasStableIds(true);
        recyclerView.setLayoutManager(new MyLinearLayoutManger(this));
        recyclerView.setAdapter(mineCreditHistoryAdapter);
        recyclerView.setLayoutAnimation(AnimationUtils.loadLayoutAnimation(this, R.anim.layout_animation_scale_in));

        refreshLayout.autoRefresh(0, 300, 1, false);
    }

    @Override
    protected BasePresenter initPresenter() {
        return new CreditHistoryPresenter();
    }


    @Override
    protected void setOnRefreshListener() {
        RefreshUtil.setOnRefreshListener(this, refreshLayout, new OnRefresh() {
            @Override
            public void onRefresh(RefreshLayout refreshLayout) {
                page = 1;
                creditHistoryPresenter.getCreditHistory(page);
            }

            @Override
            public void onLoadMore(RefreshLayout refreshLayout) {
                creditHistoryPresenter.getCreditHistory(page);
            }
        });
    }


    @Override
    public void onGetMineCreditHistorySuccess(List<MineCreditBean.CreditHistoryBean> creditHistoryBeans, boolean hasNext) {
        if (page == 1) {
            mineCreditHistoryAdapter.setNewData(creditHistoryBeans);
            recyclerView.scheduleLayoutAnimation();
        } else {
            mineCreditHistoryAdapter.addData(creditHistoryBeans);
        }


        if (refreshLayout.getState() == RefreshState.Refreshing) {
            page = 2;
            if (hasNext) {
                refreshLayout.finishRefresh();
            } else {
                refreshLayout.finishRefreshWithNoMoreData();
            }
        }
        if (refreshLayout.getState() == RefreshState.Loading) {
            if (hasNext) {
                page = page + 1;
                refreshLayout.finishLoadMore(true);
            } else {
                refreshLayout.finishLoadMoreWithNoMoreData();
            }
        }
    }

    @Override
    public void onGetMineCreditHistoryError(String msg) {
        refreshLayout.finishLoadMore();
        refreshLayout.finishRefresh();
        showToast(msg, ToastType.TYPE_ERROR);
    }
}