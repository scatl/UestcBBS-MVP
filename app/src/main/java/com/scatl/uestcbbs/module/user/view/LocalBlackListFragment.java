package com.scatl.uestcbbs.module.user.view;

import android.os.Bundle;

import androidx.recyclerview.widget.RecyclerView;

import android.view.animation.AnimationUtils;

import com.scatl.uestcbbs.R;
import com.scatl.uestcbbs.base.BaseBottomFragment;
import com.scatl.uestcbbs.base.BaseEvent;
import com.scatl.uestcbbs.base.BasePresenter;
import com.scatl.uestcbbs.widget.MyLinearLayoutManger;
import com.scatl.uestcbbs.entity.BlackListBean;
import com.scatl.uestcbbs.annotation.BlackListType;
import com.scatl.uestcbbs.module.user.adapter.BlackListAdapter;

import org.greenrobot.eventbus.EventBus;
import org.litepal.LitePal;

public class LocalBlackListFragment extends BaseBottomFragment {

    private RecyclerView recyclerView;
    private BlackListAdapter blackListAdapter;

    public static LocalBlackListFragment getInstance(Bundle bundle) {
        LocalBlackListFragment localBlackListFragment = new LocalBlackListFragment();
        localBlackListFragment.setArguments(bundle);
        return localBlackListFragment;
    }

    @Override
    protected int setLayoutResourceId() {
        return R.layout.fragment_local_black_list;
    }

    @Override
    protected void findView() {
        recyclerView = view.findViewById(R.id.fragment_local_black_list_rv);
    }

    @Override
    protected void initView() {
        blackListAdapter = new BlackListAdapter(R.layout.item_black_list, BlackListType.TYPE_LOCAL);
        recyclerView.setAdapter(blackListAdapter);
        recyclerView.setLayoutManager(new MyLinearLayoutManger(mActivity));
        recyclerView.setLayoutAnimation(AnimationUtils.loadLayoutAnimation(mActivity, R.anim.layout_animation_scale_in));

        blackListAdapter.setNewData(LitePal.findAll(BlackListBean.class));
    }

    @Override
    protected BasePresenter initPresenter() {
        return null;
    }

    @Override
    protected void setOnItemClickListener() {
        blackListAdapter.setOnItemChildClickListener((adapter, view1, position) -> {
            if (view1.getId() == R.id.item_black_list_delete) {
                LitePal.deleteAll(BlackListBean.class, "uid=" + blackListAdapter.getData().get(position).uid);
                blackListAdapter.setNewData(LitePal.findAll(BlackListBean.class));
                EventBus.getDefault().post(new BaseEvent<>(BaseEvent.EventCode.BLACK_LIST_CHANGE));
            }
        });
    }
}