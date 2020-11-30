package com.scatl.uestcbbs.module.home.view;

import android.content.Intent;
import android.os.Bundle;

import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.view.animation.AnimationUtils;

import com.scatl.uestcbbs.R;
import com.scatl.uestcbbs.base.BaseFragment;
import com.scatl.uestcbbs.base.BasePresenter;
import com.scatl.uestcbbs.callback.OnRefresh;
import com.scatl.uestcbbs.entity.GrabSofaBean;
import com.scatl.uestcbbs.module.home.adapter.GrabSofaAdapter;
import com.scatl.uestcbbs.module.home.presenter.GrabSofaPresenter;
import com.scatl.uestcbbs.module.post.view.PostDetailActivity;
import com.scatl.uestcbbs.util.Constant;
import com.scatl.uestcbbs.util.ForumUtil;
import com.scatl.uestcbbs.util.RefreshUtil;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;


public class GrabSofaFragment extends BaseFragment implements GrabSofaView{

    private SmartRefreshLayout refreshLayout;
    private RecyclerView recyclerView;
    private GrabSofaAdapter grabSofaAdapter;

    private GrabSofaPresenter grabSofaPresenter;

    public static GrabSofaFragment getInstance(Bundle bundle) {
        GrabSofaFragment grabSofaFragment = new GrabSofaFragment();
        grabSofaFragment.setArguments(bundle);
        return grabSofaFragment;
    }

    @Override
    protected int setLayoutResourceId() {
        return R.layout.fragment_grab_sofa;
    }

    @Override
    protected void findView() {
        recyclerView = view.findViewById(R.id.grab_sofa_rv);
        refreshLayout = view.findViewById(R.id.grab_sofa_refresh);
    }

    @Override
    protected void initView() {
        grabSofaPresenter = (GrabSofaPresenter) presenter;

        grabSofaAdapter = new GrabSofaAdapter(R.layout.item_grab_sofa);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        recyclerView.setLayoutAnimation(AnimationUtils.loadLayoutAnimation(mActivity, R.anim.layout_animation_scale_in));
        recyclerView.setAdapter(grabSofaAdapter);

        refreshLayout.setEnableLoadMore(false);
    }

    @Override
    protected void lazyLoad() {
        refreshLayout.autoRefresh(0, 300, 1, false);
    }

    @Override
    protected BasePresenter initPresenter() {
        return new GrabSofaPresenter();
    }

    @Override
    protected void setOnItemClickListener() {
        grabSofaAdapter.setOnItemClickListener((adapter, view1, position) -> {
            if (view1.getId() == R.id.item_grab_sofa_card_view) {
                Intent intent = new Intent(mActivity, PostDetailActivity.class);
                intent.putExtra(Constant.IntentKey.TOPIC_ID, ForumUtil.getFromLinkInfo(grabSofaAdapter.getData().get(position).link).id);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void setOnRefreshListener() {
        RefreshUtil.setOnRefreshListener(mActivity, refreshLayout, new OnRefresh() {
            @Override
            public void onRefresh(RefreshLayout refreshLayout) {
                grabSofaPresenter.getGrabSofaData();
            }

            @Override
            public void onLoadMore(RefreshLayout refreshLayout) { }
        });
    }

    @Override
    public void onGrabSofaDataSuccess(GrabSofaBean grabSofaBean) {
        grabSofaAdapter.addData(grabSofaBean.channel.itemBeans, true);
        recyclerView.scheduleLayoutAnimation();
        refreshLayout.finishRefresh();
    }

    @Override
    public void onGrabSofaDataError(String msg) {

    }
}
