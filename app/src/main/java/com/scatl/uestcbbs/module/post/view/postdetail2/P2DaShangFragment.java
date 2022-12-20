package com.scatl.uestcbbs.module.post.view.postdetail2;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.scatl.uestcbbs.R;
import com.scatl.uestcbbs.base.BaseBottomFragment;
import com.scatl.uestcbbs.base.BaseDialogFragment;
import com.scatl.uestcbbs.base.BaseEvent;
import com.scatl.uestcbbs.base.BaseFragment;
import com.scatl.uestcbbs.base.BasePresenter;
import com.scatl.uestcbbs.callback.OnRefresh;
import com.scatl.uestcbbs.custom.MyLinearLayoutManger;
import com.scatl.uestcbbs.entity.PostDianPingBean;
import com.scatl.uestcbbs.entity.RateUserBean;
import com.scatl.uestcbbs.module.post.adapter.P2DaShangAdapter;
import com.scatl.uestcbbs.module.post.adapter.PostDianPingAdapter;
import com.scatl.uestcbbs.module.post.presenter.postdetail2.P2DaShangPresenter;
import com.scatl.uestcbbs.module.post.presenter.postdetail2.P2DianPingPresenter;
import com.scatl.uestcbbs.module.post.view.PostRateFragment;
import com.scatl.uestcbbs.module.user.view.UserDetailActivity;
import com.scatl.uestcbbs.util.Constant;
import com.scatl.uestcbbs.util.RefreshUtil;
import com.scatl.uestcbbs.util.TimeUtil;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;

import java.util.List;

public class P2DaShangFragment extends BaseFragment<P2DaShangPresenter> implements P2DaShangView{
    RecyclerView dianPingRv;
    SmartRefreshLayout refreshLayout;
    TextView hint;
    LottieAnimationView loading;
    ExtendedFloatingActionButton daShangBtn;
    P2DaShangAdapter p2DaShangAdapter;
    int tid, pid;

    public static P2DaShangFragment getInstance(Bundle bundle) {
        P2DaShangFragment p2DaShangFragment = new P2DaShangFragment();
        p2DaShangFragment.setArguments(bundle);
        return p2DaShangFragment;
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
        return R.layout.fragment_p2_da_shang;
    }

    @Override
    protected void findView() {
        refreshLayout = view.findViewById(R.id.p2_dashang_fragment_refresh);
        dianPingRv = view.findViewById(R.id.p2_dashang_fragment_rv);
        daShangBtn = view.findViewById(R.id.p2_dashang_fragment_dashang_btn);
        hint = view.findViewById(R.id.p2_dashang_fragment_hint);
        loading = view.findViewById(R.id.p2_dashang_fragment_loading);
    }

    @Override
    protected void initView() {
        p2DaShangAdapter = new P2DaShangAdapter(R.layout.item_p2_dashang);
        dianPingRv.setLayoutAnimation(AnimationUtils.loadLayoutAnimation(mActivity, R.anim.layout_animation_scale_in));
        dianPingRv.setLayoutManager(new MyLinearLayoutManger(mActivity));
        dianPingRv.setAdapter(p2DaShangAdapter);

        daShangBtn.setOnClickListener(this);

        refreshLayout.setEnableRefresh(false);
        refreshLayout.setEnableNestedScroll(false);
    }

    @Override
    protected P2DaShangPresenter initPresenter() {
        return new P2DaShangPresenter();
    }

    @Override
    protected void lazyLoad() {
        presenter.getRateUser(tid, pid);
    }

    @Override
    protected void onClickListener(View view) {
        if (view.getId() == R.id.p2_dashang_fragment_dashang_btn) {
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
            dianPingRv.scheduleLayoutAnimation();
            p2DaShangAdapter.setNewData(rateUserBeans);
        }
    }

    @Override
    public void onGetRateUserError(String msg) {
        loading.setVisibility(View.GONE);
        hint.setText(msg);
    }

    @Override
    protected boolean registerEventBus() {
        return true;
    }

    @Override
    protected void receiveEventBusMsg(BaseEvent baseEvent) {
        if (baseEvent.eventCode == BaseEvent.EventCode.RATE_SUCCESS) {
            presenter.getRateUser(tid, pid);
        }
    }
}