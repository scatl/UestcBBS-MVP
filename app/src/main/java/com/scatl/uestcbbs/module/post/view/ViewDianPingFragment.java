package com.scatl.uestcbbs.module.post.view;

import android.content.Intent;
import android.os.Bundle;

import androidx.recyclerview.widget.RecyclerView;

import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.scatl.uestcbbs.R;
import com.scatl.uestcbbs.annotation.PostAppendType;
import com.scatl.uestcbbs.base.BaseBottomFragment;
import com.scatl.uestcbbs.base.BaseEvent;
import com.scatl.uestcbbs.base.BasePresenter;
import com.scatl.uestcbbs.callback.OnRefresh;
import com.scatl.uestcbbs.custom.MyLinearLayoutManger;
import com.scatl.uestcbbs.entity.PostDianPingBean;
import com.scatl.uestcbbs.module.post.adapter.PostDianPingAdapter;
import com.scatl.uestcbbs.module.post.presenter.ViewDianPingPresenter;
import com.scatl.uestcbbs.module.user.view.UserDetailActivity;
import com.scatl.uestcbbs.util.Constant;
import com.scatl.uestcbbs.util.RefreshUtil;
import com.scatl.uestcbbs.util.TimeUtil;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;

import java.util.List;

import biz.laenger.android.vpbs.ViewPagerBottomSheetBehavior;


public class ViewDianPingFragment extends BaseBottomFragment implements ViewDianPingView{
    RecyclerView dianPingRv;
    SmartRefreshLayout refreshLayout;
    TextView hint, originalContent;
    LottieAnimationView loading;
    ExtendedFloatingActionButton dianpingBtn;
    PostDianPingAdapter postDianPingAdapter;

    ViewDianPingPresenter viewDianPingPresenter;

    int page = 1, tid, pid;
    boolean showOriginalContent;

    public static ViewDianPingFragment getInstance(Bundle bundle) {
        ViewDianPingFragment viewDianPingFragment = new ViewDianPingFragment();
        viewDianPingFragment.setArguments(bundle);
        return viewDianPingFragment;
    }

    @Override
    protected void getBundle(Bundle bundle) {
        if (bundle != null) {
            tid = bundle.getInt(Constant.IntentKey.TOPIC_ID, Integer.MAX_VALUE);
            pid = bundle.getInt(Constant.IntentKey.POST_ID, Integer.MAX_VALUE);
            showOriginalContent = bundle.getBoolean(Constant.IntentKey.DATA_1, false);
        }
    }

    @Override
    protected int setLayoutResourceId() {
        return R.layout.fragment_view_dian_ping;
    }

    @Override
    protected void findView() {
        refreshLayout = view.findViewById(R.id.fragment_view_dianping_refresh);
        dianPingRv = view.findViewById(R.id.fragment_view_dianping_rv);
        hint = view.findViewById(R.id.fragment_view_dianping_hint);
        dianpingBtn = view.findViewById(R.id.fragment_view_dianping_btn);
        loading = view.findViewById(R.id.fragment_view_dianping_loading);
        originalContent = view.findViewById(R.id.fragment_view_dianping_original_content);
    }

    @Override
    protected void initView() {
        viewDianPingPresenter = (ViewDianPingPresenter) presenter;
        mBehavior.setState(ViewPagerBottomSheetBehavior.STATE_EXPANDED);
        postDianPingAdapter = new PostDianPingAdapter(R.layout.item_post_detail_dianping);
        dianPingRv.setLayoutAnimation(AnimationUtils.loadLayoutAnimation(mActivity, R.anim.layout_animation_scale_in));
        dianPingRv.setLayoutManager(new MyLinearLayoutManger(mActivity));
        dianPingRv.setAdapter(postDianPingAdapter);
        dianpingBtn.setOnClickListener(this);
        refreshLayout.setEnableRefresh(false);
        refreshLayout.setEnableNestedScroll(false);

        viewDianPingPresenter.getDianPingList(tid, pid, page);
        viewDianPingPresenter.findPost(tid, pid);
    }

    @Override
    protected BasePresenter initPresenter() {
        return new ViewDianPingPresenter();
    }

    @Override
    protected void onClickListener(View view) {
        if (view.getId() == R.id.fragment_view_dianping_btn) {
            Bundle bundle = new Bundle();
            bundle.putInt(Constant.IntentKey.TOPIC_ID, tid);
            bundle.putInt(Constant.IntentKey.POST_ID, pid);
            bundle.putString(Constant.IntentKey.TYPE, PostAppendType.DIANPING);
            PostAppendFragment.getInstance(bundle).show(getChildFragmentManager(), TimeUtil.getStringMs());
        }
    }

    @Override
    protected void setOnItemClickListener() {
        postDianPingAdapter.setOnItemChildClickListener((adapter, view1, position) -> {
            if (view1.getId() == R.id.item_post_detail_dianping_avatar) {
                Intent intent = new Intent(mActivity, UserDetailActivity.class);
                intent.putExtra(Constant.IntentKey.USER_ID, postDianPingAdapter.getData().get(position).uid);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void setOnRefreshListener() {
        RefreshUtil.setOnRefreshListener(mActivity, refreshLayout, new OnRefresh() {
            @Override
            public void onRefresh(RefreshLayout refreshLayout) {
                page = 1;
                viewDianPingPresenter.getDianPingList(tid, pid, page);
            }

            @Override
            public void onLoadMore(RefreshLayout refreshLayout) {
                page = page + 1;
                viewDianPingPresenter.getDianPingList(tid, pid, page);
            }
        });
    }

    @Override
    public void onGetPostDianPingListSuccess(List<PostDianPingBean> commentBeans, boolean hasNext) {
        loading.setVisibility(View.GONE);

        if (hasNext) {
            refreshLayout.finishRefresh();
            refreshLayout.finishLoadMore(true);
        } else {
            refreshLayout.finishRefreshWithNoMoreData();
            refreshLayout.finishLoadMoreWithNoMoreData();
        }

        if (commentBeans == null || commentBeans.size() == 0) {
            hint.setText("还没有人点评，快来发表吧");
        } else {
            hint.setText("");
            if (page == 1) {
                dianPingRv.scheduleLayoutAnimation();
                postDianPingAdapter.addData(commentBeans, true);
            } else {
                postDianPingAdapter.addData(commentBeans, false);
            }
        }
    }

    @Override
    public void onGetPostDianPingListError(String msg) {
        if (page > 1)page = page - 1;
        loading.setVisibility(View.GONE);
        hint.setText(msg);
    }

    @Override
    public void onFindPostSuccess(String content) {
        if (content != null && showOriginalContent) {
            originalContent.setVisibility(View.VISIBLE);
            originalContent.setText("原帖文字内容：\n" + content);
        }
    }

    @Override
    public void onFindPostError(String msg) {

    }

    @Override
    protected boolean registerEventBus() {
        return true;
    }

    @Override
    protected void receiveEventBusMsg(BaseEvent baseEvent) {
        if (baseEvent.eventCode == BaseEvent.EventCode.DIANPING_SUCCESS) {
            viewDianPingPresenter.getDianPingList(tid, pid, 1);
        }
    }

    @Override
    protected double setMaxHeightMultiplier() {
        return 0.7;
    }
}