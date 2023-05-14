package com.scatl.uestcbbs.module.houqin.view;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.scatl.uestcbbs.R;
import com.scatl.uestcbbs.base.BaseActivity;
import com.scatl.uestcbbs.callback.OnRefresh;
import com.scatl.uestcbbs.widget.MyLinearLayoutManger;
import com.scatl.uestcbbs.entity.HouQinReportListBean;
import com.scatl.uestcbbs.module.houqin.adapter.HouQinReportListAdapter;
import com.scatl.uestcbbs.module.houqin.presenter.HouQinReportListPresenter;
import com.scatl.uestcbbs.util.Constant;
import com.scatl.uestcbbs.util.RefreshUtil;
import com.scatl.uestcbbs.util.TimeUtil;
import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.scwang.smart.refresh.layout.api.RefreshLayout;

public class HouQinReportListActivity extends BaseActivity<HouQinReportListPresenter> implements HouQinReportListView{

    private SmartRefreshLayout refreshLayout;
    private RecyclerView recyclerView;
    private TextView hint;
    private HouQinReportListAdapter houQinReportListAdapter;
    private Toolbar toolbar;

    private int pageNo = 1;

    @Override
    protected int setLayoutResourceId() {
        return R.layout.activity_hou_qin_report_list;
    }

    @Override
    protected void findView() {
        refreshLayout = findViewById(R.id.houqin_report_list_refresh);
        recyclerView = findViewById(R.id.houqin_report_list_rv);
        hint = findViewById(R.id.houqin_report_list_hint);
        toolbar = findViewById(R.id.toolbar);
    }

    @Override
    protected void initView() {
        super.initView();
        houQinReportListAdapter = new HouQinReportListAdapter(R.layout.item_houqin_report_list);
        recyclerView.setLayoutAnimation(AnimationUtils.loadLayoutAnimation(this, R.anim.layout_animation_scale_in));
        recyclerView.setLayoutManager(new MyLinearLayoutManger(this));
        recyclerView.setAdapter(houQinReportListAdapter);

        refreshLayout.autoRefresh(0 , 300, 1, false);
    }

    @Override
    protected void setOnItemClickListener() {
        houQinReportListAdapter.setOnItemClickListener((adapter, view, position) -> {
            Bundle bundle = new Bundle();
            bundle.putInt(Constant.IntentKey.TOPIC_ID, houQinReportListAdapter.getData().get(position).topicId);
            HouQinReportDetailFragment.getInstance(bundle).show(getSupportFragmentManager(), TimeUtil.getStringMs());
        });
    }

    @Override
    protected void setOnRefreshListener() {
        RefreshUtil.setOnRefreshListener(this, refreshLayout, new OnRefresh() {
            @Override
            public void onRefresh(RefreshLayout refreshLayout) {
                pageNo = 1;
                presenter.getAllReportList(pageNo);
            }

            @Override
            public void onLoadMore(RefreshLayout refreshLayout) {
                presenter.getAllReportList(pageNo);
            }
        });
    }

    @Override
    protected HouQinReportListPresenter initPresenter() {
        return new HouQinReportListPresenter();
    }

    @Override
    public void onGetReportListSuccess(HouQinReportListBean houQinReportListBean) {
        hint.setText("");
        if (pageNo == 1) {
            houQinReportListAdapter.setNewData(houQinReportListBean.topic);
            recyclerView.scheduleLayoutAnimation();
        } else {
            houQinReportListAdapter.addData(houQinReportListBean.topic);
        }

        if (pageNo != houQinReportListBean.pages) {
            refreshLayout.finishRefresh();
            refreshLayout.finishLoadMore(true);
        } else {
            refreshLayout.finishRefreshWithNoMoreData();
            refreshLayout.finishLoadMoreWithNoMoreData();
        }

        pageNo ++;
    }

    @Override
    public void onGetReportListError(String msg) {
        hint.setText(msg);
        refreshLayout.finishRefresh();
        refreshLayout.finishLoadMore();
    }
}