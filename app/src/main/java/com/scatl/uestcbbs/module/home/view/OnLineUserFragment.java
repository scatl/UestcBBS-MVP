package com.scatl.uestcbbs.module.home.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.scatl.uestcbbs.R;
import com.scatl.uestcbbs.base.BaseBottomFragment;
import com.scatl.uestcbbs.base.BasePresenter;
import com.scatl.uestcbbs.entity.OnLineUserBean;
import com.scatl.uestcbbs.module.home.adapter.OnLineUserAdapter;
import com.scatl.uestcbbs.module.home.presenter.OnLineUserPresenter;
import com.scatl.uestcbbs.module.user.view.UserDetailActivity;
import com.scatl.uestcbbs.util.Constant;


public class OnLineUserFragment extends BaseBottomFragment implements OnLineUserView{

    private RecyclerView recyclerView;
    private OnLineUserAdapter onLineUserAdapter;
    private TextView hint, totalUserNum, totalRegisteredNum, totalVisitorNum;
    private ProgressBar progressBar;

    private OnLineUserPresenter onLineUserPresenter;

    public static OnLineUserFragment getInstance(Bundle bundle){
        OnLineUserFragment onLineUserFragment = new OnLineUserFragment();
        onLineUserFragment.setArguments(bundle);
        return onLineUserFragment;
    }

    @Override
    protected int setLayoutResourceId() {
        return R.layout.fragment_online_user;
    }

    @Override
    protected void findView() {
        recyclerView = view.findViewById(R.id.fragment_online_user_rv);
        hint = view.findViewById(R.id.fragment_online_user_hint);
        progressBar = view.findViewById(R.id.fragment_online_user_progressbar);
        totalVisitorNum = view.findViewById(R.id.fragment_online_user_visitor_num);
        totalRegisteredNum = view.findViewById(R.id.fragment_online_user_huiyuan_num);
        totalUserNum = view.findViewById(R.id.fragment_online_user_total_user_num);
    }

    @Override
    protected void initView() {
        onLineUserPresenter = (OnLineUserPresenter) presenter;
        mBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

        onLineUserAdapter = new OnLineUserAdapter(R.layout.item_online_user);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        recyclerView.setAdapter(onLineUserAdapter);
        recyclerView.setLayoutAnimation(AnimationUtils.loadLayoutAnimation(mActivity, R.anim.layout_animation_scale_in));

        onLineUserPresenter.getHomeInfo();
    }

    @Override
    protected void setOnItemClickListener() {
        onLineUserAdapter.setOnItemClickListener((adapter, view, position) -> {
            Intent intent = new Intent(mActivity, UserDetailActivity.class);
            intent.putExtra(Constant.IntentKey.USER_ID, onLineUserAdapter.getData().get(position).uid);
            startActivity(intent);
        });
    }

    @Override
    protected BasePresenter initPresenter() {
        return new OnLineUserPresenter();
    }

    @Override
    protected double setMaxHeightMultiplier() {
        return 0.92;
    }

    @Override
    public void onGetOnLineUserSuccess(OnLineUserBean onLineUserBean) {
        progressBar.setVisibility(View.GONE);
        onLineUserAdapter.setNewData(onLineUserBean.userBeans);
        hint.setText(onLineUserAdapter.getData().size() == 0 ? "啊哦，还没有数据" : "");
        totalUserNum.setText(String.valueOf(onLineUserBean.totalUserNum));
        totalVisitorNum.setText(String.valueOf(onLineUserBean.totalVisitorNum));
        totalRegisteredNum.setText(String.valueOf(onLineUserBean.totalRegisteredUserNum));
    }

    @Override
    public void onGetOnLineUserError(String msg) {
        progressBar.setVisibility(View.GONE);
        hint.setText(msg);
    }
}