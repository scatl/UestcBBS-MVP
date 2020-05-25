package com.scatl.uestcbbs.module.user.view;


import android.content.Intent;
import android.os.Bundle;

import androidx.recyclerview.widget.RecyclerView;

import android.view.animation.AnimationUtils;

import com.scatl.uestcbbs.R;
import com.scatl.uestcbbs.base.BaseFragment;
import com.scatl.uestcbbs.base.BasePresenter;
import com.scatl.uestcbbs.callback.OnRefresh;
import com.scatl.uestcbbs.custom.MyLinearLayoutManger;
import com.scatl.uestcbbs.entity.AtUserListBean;
import com.scatl.uestcbbs.module.user.adapter.AtUserListAdapter;
import com.scatl.uestcbbs.module.user.presenter.AtUserListPresenter;
import com.scatl.uestcbbs.util.Constant;
import com.scatl.uestcbbs.util.RefreshUtil;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.RefreshState;

import java.util.ArrayList;
import java.util.List;


public class AtUserListFragment extends BaseFragment implements AtUserListView{

    private SmartRefreshLayout refreshLayout;
    private RecyclerView recyclerView;
    private AtUserListAdapter atUserListAdapter;

    private AtUserListPresenter atUserListPresenter;
    private int page = 1;
    private String type;

    public static final String AT_LIST_TYPE_FRIEND = "friend";
    public static final String AT_LIST_TYPE_FOLLOW = "follow";

    private static final int ROLE_NUM_FRIEND = 2;
    private static final int ROLE_NUM_FOLLOW = 6;

    public static final int AT_USER_RESULT = 20;

    public static AtUserListFragment getInstance(Bundle bundle) {
        AtUserListFragment atUserListFragment = new AtUserListFragment();
        atUserListFragment.setArguments(bundle);
        return atUserListFragment;
    }

    @Override
    protected void getBundle(Bundle bundle) {
        super.getBundle(bundle);
        if (bundle != null) {
            type = bundle.getString(Constant.IntentKey.TYPE);
        }
    }

    @Override
    protected int setLayoutResourceId() {
        return R.layout.fragment_at_user_list;
    }

    @Override
    protected void findView() {
        recyclerView = view.findViewById(R.id.at_user_list_rv);
        refreshLayout = view.findViewById(R.id.at_user_list_refresh);
    }

    @Override
    protected void initView() {
        atUserListPresenter = (AtUserListPresenter) presenter;

        atUserListAdapter = new AtUserListAdapter(R.layout.item_at_user_list);
        recyclerView.setLayoutManager(new MyLinearLayoutManger(mActivity));
        recyclerView.setLayoutAnimation(AnimationUtils.loadLayoutAnimation(mActivity, R.anim.layout_animation_scale_in));
        recyclerView.setAdapter(atUserListAdapter);

        refreshLayout.autoRefresh(0, 300, 1, false);
    }

    @Override
    protected BasePresenter initPresenter() {
        return new AtUserListPresenter();
    }

    @Override
    protected void setOnItemClickListener() {
        atUserListAdapter.setOnItemChildClickListener((adapter, view1, position) -> {
            if (view1.getId() == R.id.item_at_user_list_icon) {
                Intent intent = new Intent(mActivity, UserDetailActivity.class);
                intent.putExtra(Constant.IntentKey.USER_ID, atUserListAdapter.getData().get(position).uid);
                startActivity(intent);
            }
        });

        atUserListAdapter.setOnItemClickListener((adapter, view1, position) -> {
            Intent intent = new Intent();
            intent.putExtra(Constant.IntentKey.AT_USER, "@" + atUserListAdapter.getData().get(position).name + " ");
            mActivity.setResult(AT_USER_RESULT, intent);
            mActivity.finish();
        });
    }

    @Override
    protected void setOnRefreshListener() {
        RefreshUtil.setOnRefreshListener(mActivity, refreshLayout, new OnRefresh() {
            @Override
            public void onRefresh(RefreshLayout refreshLayout) {
                page = 1;
                atUserListPresenter.getAtUSerList(page, 1000, mActivity);
            }

            @Override
            public void onLoadMore(RefreshLayout refreshLayout) {
                atUserListPresenter.getAtUSerList(page, 1000, mActivity);
            }
        });
    }

    @Override
    public void onGetAtUserListSuccess(AtUserListBean atUserListBean) {
        page = page + 1;

        if (refreshLayout.getState() == RefreshState.Refreshing) {
            if (atUserListBean.has_next == 1) {
                refreshLayout.finishRefresh();
            } else {
                refreshLayout.finishRefreshWithNoMoreData();
            }
        }
        if (refreshLayout.getState() == RefreshState.Loading) {
            if (atUserListBean.has_next == 1) {
                refreshLayout.finishLoadMore(true);
            } else {
                refreshLayout.finishLoadMoreWithNoMoreData();
            }
        }

        List<AtUserListBean.ListBean> friend = new ArrayList<>();
        List<AtUserListBean.ListBean> follow = new ArrayList<>();

        for (int i = 0; i < atUserListBean.list.size(); i ++) {
            if (atUserListBean.list.get(i).role_num == ROLE_NUM_FRIEND) {
                friend.add(atUserListBean.list.get(i));
            }
            if (atUserListBean.list.get(i).role_num == ROLE_NUM_FOLLOW) {
                follow.add(atUserListBean.list.get(i));
            }
        }

        if (atUserListBean.page == 1) {
            recyclerView.scheduleLayoutAnimation();
            if (AT_LIST_TYPE_FRIEND.equals(type)) atUserListAdapter.setNewData(friend);
            if (AT_LIST_TYPE_FOLLOW.equals(type)) atUserListAdapter.setNewData(follow);
        } else {
            if (AT_LIST_TYPE_FRIEND.equals(type)) atUserListAdapter.addData(friend);
            if (AT_LIST_TYPE_FOLLOW.equals(type)) atUserListAdapter.addData(follow);
        }
    }

    @Override
    public void onGetAtUserListError(String msg) {
        if (refreshLayout.getState() == RefreshState.Refreshing) {
            refreshLayout.finishRefresh();
        }
        if (refreshLayout.getState() == RefreshState.Loading) {
            refreshLayout.finishLoadMore(false);
        }

        showSnackBar(getView(), msg);
    }
}
