package com.scatl.uestcbbs.module.post.view;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.scatl.uestcbbs.R;
import com.scatl.uestcbbs.base.BaseDialogFragment;
import com.scatl.uestcbbs.base.BasePresenter;
import com.scatl.uestcbbs.callback.OnRefresh;
import com.scatl.uestcbbs.entity.ViewVoterBean;
import com.scatl.uestcbbs.entity.VoteOptionsBean;
import com.scatl.uestcbbs.module.post.adapter.ViewVoterAdapter;
import com.scatl.uestcbbs.module.post.presenter.ViewVoterPresenter;
import com.scatl.uestcbbs.module.user.view.UserDetailActivity;
import com.scatl.uestcbbs.util.Constant;
import com.scatl.uestcbbs.util.RefreshUtil;
import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.scwang.smart.refresh.layout.api.RefreshLayout;

import java.util.ArrayList;
import java.util.List;


public class ViewVoterFragment extends BaseDialogFragment implements ViewVoterView, AdapterView.OnItemSelectedListener {

    LottieAnimationView loading;
    TextView hint;
    AppCompatSpinner spinner;
    View layout;
    RecyclerView recyclerView;
    SmartRefreshLayout refreshLayout;
    ViewVoterAdapter viewVoterAdapter;

    ViewVoterPresenter viewVoterPresenter;

    List<VoteOptionsBean> voteOptionsBeans;

    int tid, page = 1, selectedOptionId;

    public static ViewVoterFragment getInstance(Bundle bundle) {
        ViewVoterFragment viewVoterFragment = new ViewVoterFragment();
        viewVoterFragment.setArguments(bundle);
        return viewVoterFragment;
    }

    @Override
    protected void getBundle(Bundle bundle) {
        if (bundle != null) {
            tid = bundle.getInt(Constant.IntentKey.TOPIC_ID, Integer.MAX_VALUE);
        }
    }

    @Override
    protected int setLayoutResourceId() {
        return R.layout.fragment_view_voter;
    }

    @Override
    protected void findView() {
        loading = view.findViewById(R.id.view_voter_fragment_loading);
        hint = view.findViewById(R.id.view_voter_fragment_hint);
        spinner = view.findViewById(R.id.view_voter_fragment_spinner);
        layout = view.findViewById(R.id.view_voter_fragment_layout);
        recyclerView = view.findViewById(R.id.view_voter_fragment_rv);
        refreshLayout = view.findViewById(R.id.view_voter_fragment_refresh);
    }

    @Override
    protected void initView() {
        viewVoterPresenter = (ViewVoterPresenter) presenter;

        layout.setVisibility(View.GONE);

        viewVoterAdapter = new ViewVoterAdapter();
        recyclerView.setLayoutAnimation(AnimationUtils.loadLayoutAnimation(mActivity, R.anim.layout_animation_scale_in));
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL));
        recyclerView.setAdapter(viewVoterAdapter);

        viewVoterPresenter.getVoteOptions(tid);
    }

    @Override
    protected BasePresenter initPresenter() {
        return new ViewVoterPresenter();
    }

    @Override
    protected void setOnItemClickListener() {
        viewVoterAdapter.addOnItemChildClickListener(R.id.avatar, (baseQuickAdapter, view, i) -> {
            Intent intent = new Intent(mActivity, UserDetailActivity.class);
            intent.putExtra(Constant.IntentKey.USER_ID, viewVoterAdapter.getItems().get(i).uid);
            startActivity(intent);
        });
    }

    @Override
    protected void setOnRefreshListener() {
        RefreshUtil.setOnRefreshListener(mActivity, refreshLayout, new OnRefresh() {
            @Override
            public void onRefresh(RefreshLayout refreshLayout) { }

            @Override
            public void onLoadMore(RefreshLayout refreshLayout) {
                page = page + 1;
                viewVoterPresenter.viewVoters(tid, selectedOptionId, page);
            }
        });
    }

    @Override
    public void onGetVoteOptionsSuccess(List<VoteOptionsBean> voteOptionsBeans) {
        this.voteOptionsBeans = voteOptionsBeans;
        loading.setVisibility(View.GONE);
        layout.setVisibility(View.VISIBLE);
        hint.setText("");

        String[] sItems = new String[voteOptionsBeans.size()];
        for (int i = 0; i < voteOptionsBeans.size(); i ++) {
            sItems[i] = voteOptionsBeans.get(i).optionName;
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(mActivity, android.R.layout.simple_spinner_item, sItems);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);
    }

    @Override
    public void onGetVoteOptionsError(String msg) {
        loading.setVisibility(View.GONE);
        hint.setText(msg);
    }

    @Override
    public void onGetVotersSuccess(List<ViewVoterBean> viewVoterBeans, boolean hasNext) {
        hint.setText("");
        loading.setVisibility(View.GONE);

        if (page == 1) {
            recyclerView.scheduleLayoutAnimation();
            viewVoterAdapter.submitList(viewVoterBeans);
        } else {
            viewVoterAdapter.addAll(viewVoterBeans);
        }
        if (hasNext) {
            refreshLayout.finishLoadMore(true);
        } else {
            refreshLayout.finishLoadMoreWithNoMoreData();
        }
    }

    @Override
    public void onGetVotersError(String msg) {
        loading.setVisibility(View.GONE);
        viewVoterAdapter.submitList(new ArrayList<>());
        hint.setText(msg);
        page = page - 1;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        loading.setVisibility(View.VISIBLE);
        viewVoterAdapter.submitList(new ArrayList<>());
        hint.setText("");
        page = 1;
        refreshLayout.resetNoMoreData();
        selectedOptionId = voteOptionsBeans.get(position).optionId;
        viewVoterPresenter.viewVoters(tid, selectedOptionId, page);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) { }
}