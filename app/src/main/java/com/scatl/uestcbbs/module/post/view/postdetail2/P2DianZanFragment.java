package com.scatl.uestcbbs.module.post.view.postdetail2;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.scatl.uestcbbs.R;
import com.scatl.uestcbbs.base.BaseBottomFragment;
import com.scatl.uestcbbs.base.BaseDialogFragment;
import com.scatl.uestcbbs.base.BaseFragment;
import com.scatl.uestcbbs.base.BasePresenter;
import com.scatl.uestcbbs.callback.OnRefresh;
import com.scatl.uestcbbs.entity.PostDetailBean;
import com.scatl.uestcbbs.module.post.adapter.P2DianZanAdapter;
import com.scatl.uestcbbs.module.post.adapter.ViewVoterAdapter;
import com.scatl.uestcbbs.module.post.presenter.postdetail2.P2DianZanPresenter;
import com.scatl.uestcbbs.module.user.view.UserDetailActivity;
import com.scatl.uestcbbs.util.Constant;
import com.scatl.uestcbbs.util.RefreshUtil;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;

import java.util.ArrayList;

public class P2DianZanFragment extends BaseFragment implements P2DianZanView{

    P2DianZanPresenter p2DianZanPresenter;
    P2DianZanAdapter p2DianZanAdapter;
    RecyclerView recyclerView;
    SmartRefreshLayout refreshLayout;
    LottieAnimationView loading;
    TextView hint;

    private int topicId;

    public static P2DianZanFragment getInstance(Bundle bundle) {
        P2DianZanFragment p2DianZanFragment = new P2DianZanFragment();
        p2DianZanFragment.setArguments(bundle);
        return p2DianZanFragment;
    }

    @Override
    protected void getBundle(Bundle bundle) {
        if (bundle != null) {
            topicId = bundle.getInt(Constant.IntentKey.TOPIC_ID, Integer.MAX_VALUE);
            //pid = bundle.getInt(Constant.IntentKey.POST_ID, Integer.MAX_VALUE);
        }
    }

    @Override
    protected int setLayoutResourceId() {
        return R.layout.fragment_p2_dian_zan;
    }

    @Override
    protected void findView() {
        recyclerView = view.findViewById(R.id.p2_dianzan_fragment_rv);
        refreshLayout = view.findViewById(R.id.p2_dianzan_fragment_refresh);
        loading = view.findViewById(R.id.p2_dianzan_fragment_loading);
        hint = view.findViewById(R.id.p2_dianzan_fragment_hint);
    }

    @Override
    protected void initView() {
        p2DianZanPresenter = (P2DianZanPresenter) presenter;

        p2DianZanAdapter = new P2DianZanAdapter(R.layout.item_view_voter);
        recyclerView.setLayoutAnimation(AnimationUtils.loadLayoutAnimation(mActivity, R.anim.layout_animation_scale_in));
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL));
        recyclerView.setAdapter(p2DianZanAdapter);

        refreshLayout.setEnableRefresh(false);
        refreshLayout.setEnableNestedScroll(false);
    }

    @Override
    protected BasePresenter initPresenter() {
        return new P2DianZanPresenter();
    }

    @Override
    protected void lazyLoad() {
        p2DianZanPresenter.getPostDetail(1, 0, 0, topicId, 0, mActivity);
    }

    @Override
    protected void setOnItemClickListener() {
        p2DianZanAdapter.setOnItemChildClickListener((adapter, view1, position) -> {
            if (view1.getId() == R.id.item_view_voter_avatar) {
                Intent intent = new Intent(mActivity, UserDetailActivity.class);
                intent.putExtra(Constant.IntentKey.USER_ID, Integer.parseInt(p2DianZanAdapter.getData().get(position).recommenduid));
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
    public void onGetPostDetailSuccess(PostDetailBean postDetailBean) {
        loading.setVisibility(View.GONE);
        try {
            if (postDetailBean.topic.zanList.size() != 0) {
                recyclerView.scheduleLayoutAnimation();
                refreshLayout.finishLoadMoreWithNoMoreData();
                p2DianZanAdapter.setNewData(postDetailBean.topic.zanList);
            } else {
                p2DianZanAdapter.setNewData(new ArrayList<>());
                hint.setText("还没有用户参与投票");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onGetPostDetailError(String msg) {
        loading.setVisibility(View.GONE);
        p2DianZanAdapter.setNewData(new ArrayList<>());
        hint.setText(msg);
    }


}