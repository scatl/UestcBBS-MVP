package com.scatl.uestcbbs.module.user.view;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.scatl.uestcbbs.R;
import com.scatl.uestcbbs.base.BaseActivity;
import com.scatl.uestcbbs.base.BaseEvent;
import com.scatl.uestcbbs.base.BasePresenter;
import com.scatl.uestcbbs.callback.OnRefresh;
import com.scatl.uestcbbs.custom.MyLinearLayoutManger;
import com.scatl.uestcbbs.entity.BlackListBean;
import com.scatl.uestcbbs.annotation.BlackListType;
import com.scatl.uestcbbs.module.user.adapter.BlackListAdapter;
import com.scatl.uestcbbs.module.user.presenter.BlackListPresenter;
import com.scatl.uestcbbs.util.Constant;
import com.scatl.uestcbbs.util.RefreshUtil;
import com.scatl.uestcbbs.util.TimeUtil;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.RefreshState;

import java.util.ArrayList;
import java.util.List;

/**
 * created by sca_tl at 2020/11/27 20:59
 */
 public class BlackListActivity extends BaseActivity<BlackListPresenter> implements BlackListView{

    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private SmartRefreshLayout refreshLayout;
    private BlackListAdapter blackListAdapter;
    private ImageView localBlackList;
    private TextView hint;

    private int page = 1;

    @Override
    protected int setLayoutResourceId() {
        return R.layout.activity_black_list;
    }

    @Override
    protected void findView() {
        toolbar = findViewById(R.id.black_list_toolbar);
        recyclerView = findViewById(R.id.black_list_rv);
        refreshLayout = findViewById(R.id.black_list_refresh);
        hint = findViewById(R.id.black_list_hint);
        localBlackList = findViewById(R.id.black_list_local_blacklist);
    }

    @Override
    protected void initView() {
        super.initView();
        localBlackList.setOnClickListener(this);

        blackListAdapter = new BlackListAdapter(R.layout.item_black_list, BlackListType.TYPE_CLOUD);
        recyclerView.setAdapter(blackListAdapter);
        recyclerView.setLayoutManager(new MyLinearLayoutManger(this));
        recyclerView.setLayoutAnimation(AnimationUtils.loadLayoutAnimation(this, R.anim.layout_animation_scale_in));

        refreshLayout.autoRefresh(0, 300, 1, false);
    }

    @Override
    protected Toolbar getToolbar() {
        return toolbar;
    }

    @Override
    protected BlackListPresenter initPresenter() {
        return new BlackListPresenter();
    }

    @Override
    protected void onClickListener(View view) {
        if (view.getId() == R.id.black_list_local_blacklist) {
            LocalBlackListFragment.getInstance(null).show(getSupportFragmentManager(), TimeUtil.getStringMs());
        }
    }

    @Override
    protected void setOnItemClickListener() {
        blackListAdapter.setOnItemClickListener((adapter, view, position) -> {
            Intent intent = new Intent(this, UserDetailActivity.class);
            intent.putExtra(Constant.IntentKey.USER_ID, blackListAdapter.getData().get(position).uid);
            startActivity(intent);
        });
    }

    @Override
    protected void setOnRefreshListener() {
        RefreshUtil.setOnRefreshListener(this, refreshLayout, new OnRefresh() {
            @Override
            public void onRefresh(RefreshLayout refreshLayout) {
                page = 1;
                presenter.getBlackList(page);
            }

            @Override
            public void onLoadMore(RefreshLayout refreshLayout) {
                presenter.getBlackList(page);
            }
        });
    }

    @Override
    public void onGetBlackListSuccess(List<BlackListBean> blackListBeans, boolean hasNext) {
        hint.setText("");

        if (page == 1) {
            blackListAdapter.setNewData(blackListBeans);
            recyclerView.scheduleLayoutAnimation();
        } else {
            blackListAdapter.addData(blackListBeans);
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
    public void onGetBlackListError(String msg) {
        blackListAdapter.setNewData(new ArrayList<>());
        if (refreshLayout.getState() == RefreshState.Refreshing) {
            refreshLayout.finishRefresh();
        }
        if (refreshLayout.getState() == RefreshState.Loading) {
            refreshLayout.finishLoadMore(false);
        }
        hint.setText(msg);
    }

    @Override
    protected boolean registerEventBus() {
        return true;
    }

    @Override
    public void onEventBusReceived(BaseEvent baseEvent) {
        if (baseEvent.eventCode == BaseEvent.EventCode.BLACK_LIST_CHANGE) {
            refreshLayout.autoRefresh(0, 300, 1, false);
        }
    }


}