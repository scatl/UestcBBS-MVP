package com.scatl.uestcbbs.module.post.view;


import android.content.Intent;
import android.os.Bundle;

import androidx.recyclerview.widget.RecyclerView;

import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.scatl.uestcbbs.R;
import com.scatl.uestcbbs.base.BaseBottomFragment;
import com.scatl.uestcbbs.base.BasePresenter;
import com.scatl.uestcbbs.widget.MyLinearLayoutManger;
import com.scatl.uestcbbs.entity.HotPostBean;
import com.scatl.uestcbbs.module.board.view.SingleBoardActivity;
import com.scatl.uestcbbs.module.post.adapter.HotPostAdapter;
import com.scatl.uestcbbs.module.post.presenter.HotPostPresenter;
import com.scatl.uestcbbs.module.user.view.UserDetailActivity;
import com.scatl.uestcbbs.util.Constant;

public class HotPostFragment extends BaseBottomFragment implements HotPostView{

    private RecyclerView recyclerView;
    private HotPostAdapter hotPostAdapter;
    private TextView title, hint;
    private ProgressBar progressBar;

    private HotPostPresenter hotPostPresenter;

    public static HotPostFragment getInstance(Bundle bundle) {
        HotPostFragment hotPostFragment = new HotPostFragment();
        hotPostFragment.setArguments(bundle);
        return hotPostFragment;
    }

    @Override
    protected int setLayoutResourceId() {
        return R.layout.fragment_bottom_hot_post;
    }

    @Override
    protected void findView() {
        recyclerView = view.findViewById(R.id.fragment_hot_post_rv);
        title = view.findViewById(R.id.fragment_hot_post_title);
        hint = view.findViewById(R.id.fragment_hot_post_hint);
        progressBar = view.findViewById(R.id.fragment_hot_post_progressbar);
    }

    @Override
    protected void initView() {
        hotPostPresenter = (HotPostPresenter) presenter;
        mBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        hotPostAdapter = new HotPostAdapter(R.layout.item_hot_post);
        recyclerView.setLayoutManager(new MyLinearLayoutManger(mActivity));
        recyclerView.setAdapter(hotPostAdapter);
        recyclerView.setLayoutAnimation(AnimationUtils.loadLayoutAnimation(mActivity, R.anim.layout_animation_scale_in));

        hotPostPresenter.getHotPostList(1, 20, mActivity);
    }

    @Override
    protected BasePresenter initPresenter() {
        return new HotPostPresenter();
    }

    @Override
    protected void setOnItemClickListener() {
        hotPostAdapter.setOnItemClickListener((adapter, view1, position) -> {
            if (view1.getId() == R.id.item_hot_post_cardview) {
                Intent intent = new Intent(mActivity, NewPostDetailActivity.class);
                intent.putExtra(Constant.IntentKey.TOPIC_ID, hotPostAdapter.getData().get(position).source_id);
                startActivity(intent);
            }
        });

        hotPostAdapter.setOnItemChildClickListener((adapter, view1, position) -> {
            if (view1.getId() == R.id.item_hot_post_board_name) {
                Intent intent = new Intent(mActivity, SingleBoardActivity.class);
                intent.putExtra(Constant.IntentKey.BOARD_ID, hotPostAdapter.getData().get(position).board_id);
                startActivity(intent);
            }
            if (view1.getId() == R.id.item_hot_post_user_avatar) {
                Intent intent = new Intent(mActivity, UserDetailActivity.class);
                intent.putExtra(Constant.IntentKey.USER_ID, hotPostAdapter.getData().get(position).user_id);
                startActivity(intent);
            }
        });
    }

    @Override
    public void getHotPostDataSuccess(HotPostBean hotPostBean) {
        progressBar.setVisibility(View.GONE);
        hotPostAdapter.addData(hotPostBean.list, true);
        recyclerView.scheduleLayoutAnimation();
        hint.setText(hotPostAdapter.getData().size() == 0 ? "啊哦，还没有数据" : "");
    }

    @Override
    public void getHotPostDataError(String msg) {
        progressBar.setVisibility(View.GONE);
        hint.setText(msg);
    }

    @Override
    protected double setMaxHeightMultiplier() {
        return 0.9;
    }
}
