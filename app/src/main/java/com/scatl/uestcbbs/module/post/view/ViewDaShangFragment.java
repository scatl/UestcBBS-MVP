package com.scatl.uestcbbs.module.post.view;

import android.content.Intent;
import android.os.Bundle;

import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.scatl.uestcbbs.R;
import com.scatl.uestcbbs.base.BaseBottomFragment;
import com.scatl.uestcbbs.base.BaseDialogFragment;
import com.scatl.uestcbbs.base.BaseEvent;
import com.scatl.uestcbbs.base.BasePresenter;
import com.scatl.uestcbbs.callback.OnRefresh;
import com.scatl.uestcbbs.custom.MyLinearLayoutManger;
import com.scatl.uestcbbs.entity.RateUserBean;
import com.scatl.uestcbbs.module.post.adapter.P2DaShangAdapter;
import com.scatl.uestcbbs.module.post.presenter.ViewDaShangPresenter;
import com.scatl.uestcbbs.module.user.view.UserDetailActivity;
import com.scatl.uestcbbs.util.Constant;
import com.scatl.uestcbbs.util.RefreshUtil;
import com.scatl.uestcbbs.util.TimeUtil;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;

import java.util.List;

public class ViewDaShangFragment extends BaseBottomFragment implements ViewDaShangView{
    RecyclerView daShangRv;
    SmartRefreshLayout refreshLayout;
    TextView hint;
    LottieAnimationView loading;
    ExtendedFloatingActionButton rateBtn;
    P2DaShangAdapter p2DaShangAdapter;

    ViewDaShangPresenter viewDaShangPresenter;

    int tid, pid;

    public static ViewDaShangFragment getInstance(Bundle bundle) {
        ViewDaShangFragment viewDaShangFragment = new ViewDaShangFragment();
        viewDaShangFragment.setArguments(bundle);
        return viewDaShangFragment;
    }

    @Override
    protected void getBundle(Bundle bundle) {
        if (bundle != null) {
            tid = bundle.getInt(Constant.IntentKey.TOPIC_ID, Integer.MAX_VALUE);
            pid = bundle.getInt(Constant.IntentKey.POST_ID, Integer.MAX_VALUE);
        }
    }

    @Override
    protected int setLayoutResourceId() {
        return R.layout.fragment_view_da_shang;
    }

    @Override
    protected void findView() {
        refreshLayout = view.findViewById(R.id.fragment_view_da_shang_refresh);
        daShangRv = view.findViewById(R.id.fragment_view_da_shang_rv);
        hint = view.findViewById(R.id.fragment_view_da_shang_hint);
        loading = view.findViewById(R.id.fragment_view_da_shang_loading);
        rateBtn = view.findViewById(R.id.fragment_view_da_shang_rate_btn);
    }

    @Override
    protected void initView() {
        viewDaShangPresenter = (ViewDaShangPresenter) presenter;

        p2DaShangAdapter = new P2DaShangAdapter(R.layout.item_p2_dashang);
        daShangRv.setLayoutAnimation(AnimationUtils.loadLayoutAnimation(mActivity, R.anim.layout_animation_scale_in));
        daShangRv.setLayoutManager(new MyLinearLayoutManger(mActivity));
        daShangRv.setAdapter(p2DaShangAdapter);

        rateBtn.setOnClickListener(this);
        refreshLayout.setEnableRefresh(false);
        refreshLayout.setEnableNestedScroll(false);

        viewDaShangPresenter.getRateUser(tid, pid);
    }

    @Override
    protected BasePresenter initPresenter() {
        return new ViewDaShangPresenter();
    }

    @Override
    protected void onClickListener(View view) {
        if (view.getId() == R.id.fragment_view_da_shang_rate_btn) {
            Bundle bundle = new Bundle();
            bundle.putInt(Constant.IntentKey.TOPIC_ID, tid);
            bundle.putInt(Constant.IntentKey.POST_ID, pid);
            PostRateFragment.getInstance(bundle).show(getChildFragmentManager(), TimeUtil.getStringMs());
        }
    }

    @Override
    protected void setOnItemClickListener() {
        p2DaShangAdapter.setOnItemChildClickListener((adapter, view1, position) -> {
            if (view1.getId() == R.id.item_p2_dashang_root_layout) {
                Intent intent = new Intent(mActivity, UserDetailActivity.class);
                intent.putExtra(Constant.IntentKey.USER_ID, p2DaShangAdapter.getData().get(position).uid);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void setOnRefreshListener() {
        RefreshUtil.setOnRefreshListener(mActivity, refreshLayout, new OnRefresh() {
            @Override
            public void onRefresh(RefreshLayout refreshLayout) { }

            @Override
            public void onLoadMore(RefreshLayout refreshLayout) { }
        });
    }

    @Override
    public void onGetRateUserSuccess(List<RateUserBean> rateUserBeans) {
        loading.setVisibility(View.GONE);

        refreshLayout.finishLoadMoreWithNoMoreData();
        if (rateUserBeans == null || rateUserBeans.size() == 0) {
            hint.setText("还没有人打赏");
        } else {
            hint.setText("");
            daShangRv.scheduleLayoutAnimation();
            p2DaShangAdapter.setNewData(rateUserBeans);
        }
    }

    @Override
    public void onGetRateUserError(String msg) {
        loading.setVisibility(View.GONE);
        hint.setText(msg);
    }

    @Override
    protected double setMaxHeightMultiplier() {
        return 0.7;
    }

    @Override
    protected boolean registerEventBus() {
        return true;
    }

    @Override
    protected void receiveEventBusMsg(BaseEvent baseEvent) {
        if (baseEvent.eventCode == BaseEvent.EventCode.RATE_SUCCESS) {
            viewDaShangPresenter.getRateUser(tid, pid);
        }
    }


}