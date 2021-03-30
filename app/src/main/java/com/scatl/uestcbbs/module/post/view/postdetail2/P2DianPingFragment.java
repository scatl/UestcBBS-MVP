package com.scatl.uestcbbs.module.post.view.postdetail2;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.scatl.uestcbbs.R;
import com.scatl.uestcbbs.base.BaseFragment;
import com.scatl.uestcbbs.base.BasePresenter;
import com.scatl.uestcbbs.callback.OnRefresh;
import com.scatl.uestcbbs.custom.MyLinearLayoutManger;
import com.scatl.uestcbbs.entity.PostDianPingBean;
import com.scatl.uestcbbs.module.post.adapter.PostDianPingAdapter;
import com.scatl.uestcbbs.module.post.presenter.postdetail2.P2DianPingPresenter;
import com.scatl.uestcbbs.util.Constant;
import com.scatl.uestcbbs.util.RefreshUtil;
import com.scatl.uestcbbs.util.SharePrefUtil;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;

import java.util.List;

public class P2DianPingFragment extends BaseFragment implements P2DianPingView{

    RecyclerView dianPingRv;
    SmartRefreshLayout refreshLayout;
    TextView hint;
    LottieAnimationView loading;
    PostDianPingAdapter postDianPingAdapter;

    P2DianPingPresenter p2DianPingPresenter;

    int page = 1, tid, pid;

    public static P2DianPingFragment getInstance(Bundle bundle) {
        P2DianPingFragment p2DianPingFragment = new P2DianPingFragment();
        p2DianPingFragment.setArguments(bundle);
        return p2DianPingFragment;
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
        return R.layout.fragment_p2_dian_ping;
    }

    @Override
    protected void findView() {
        refreshLayout = view.findViewById(R.id.p2_dianping_fragment_refresh);
        dianPingRv = view.findViewById(R.id.p2_dianping_fragment_rv);
        hint = view.findViewById(R.id.p2_dianping_fragment_hint);
        loading = view.findViewById(R.id.p2_dianping_fragment_loading);
    }

    @Override
    protected void initView() {
        p2DianPingPresenter = (P2DianPingPresenter) presenter;

        postDianPingAdapter = new PostDianPingAdapter(R.layout.item_post_detail_dianping);
        dianPingRv.setLayoutAnimation(AnimationUtils.loadLayoutAnimation(mActivity, R.anim.layout_animation_scale_in));
        dianPingRv.setLayoutManager(new MyLinearLayoutManger(mActivity));
        dianPingRv.setAdapter(postDianPingAdapter);

//        refreshLayout.setEnableRefresh(false);
//        refreshLayout.setEnableNestedScroll(false);
    }

    @Override
    protected BasePresenter initPresenter() {
        return new P2DianPingPresenter();
    }

    @Override
    protected void lazyLoad() {
        p2DianPingPresenter.getDianPingList(tid, pid, page);
    }

    @Override
    protected void setOnRefreshListener() {
        RefreshUtil.setOnRefreshListener(mActivity, refreshLayout, new OnRefresh() {
            @Override
            public void onRefresh(RefreshLayout refreshLayout) {
                page = 1;
                p2DianPingPresenter.getDianPingList(tid, pid, page);
            }

            @Override
            public void onLoadMore(RefreshLayout refreshLayout) {
                page = page + 1;
                p2DianPingPresenter.getDianPingList(tid, pid, page);
            }
        });
    }

    @Override
    public void onGetPostDianPingListSuccess(List<PostDianPingBean> commentBeans, boolean hasNext) {
        loading.setVisibility(View.GONE);
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
}