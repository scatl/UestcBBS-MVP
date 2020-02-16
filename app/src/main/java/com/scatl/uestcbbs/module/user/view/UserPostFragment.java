package com.scatl.uestcbbs.module.user.view;


import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.TextView;

import com.scatl.uestcbbs.R;
import com.scatl.uestcbbs.api.ApiConstant;
import com.scatl.uestcbbs.base.BaseFragment;
import com.scatl.uestcbbs.base.BasePresenter;
import com.scatl.uestcbbs.callback.OnRefresh;
import com.scatl.uestcbbs.custom.MyLinearLayoutManger;
import com.scatl.uestcbbs.entity.UserPostBean;
import com.scatl.uestcbbs.module.board.view.SingleBoardActivity;
import com.scatl.uestcbbs.module.post.adapter.UserPostAdapter;
import com.scatl.uestcbbs.module.post.view.PostDetailActivity;
import com.scatl.uestcbbs.module.post.view.SelfPostActivity;
import com.scatl.uestcbbs.module.user.presenter.UserPostPresenter;
import com.scatl.uestcbbs.util.Constant;
import com.scatl.uestcbbs.util.RefreshUtil;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.RefreshState;

public class UserPostFragment extends BaseFragment implements UserPostView{

    private SmartRefreshLayout refreshLayout;
    private RecyclerView recyclerView;
    private UserPostAdapter userPostAdapter;
    private TextView hint;

    public static final String TYPE_USER_POST = "topic";
    public static final String TYPE_USER_REPLY = "reply";
    public static final String TYPE_USER_FAVORITE = "favorite";

    private int userId, page = 1;
    private String type;

    private UserPostPresenter userPostPresenter;

    public static UserPostFragment getInstance(Bundle bundle) {
        UserPostFragment userPostFragment = new UserPostFragment();
        userPostFragment.setArguments(bundle);
        return userPostFragment;
    }

    @Override
    protected void getBundle(Bundle bundle) {
        super.getBundle(bundle);
        if (bundle != null) {
            userId = bundle.getInt(Constant.IntentKey.USER_ID, Integer.MAX_VALUE);
            type = bundle.getString(Constant.IntentKey.TYPE);
        }
    }

    @Override
    protected int setLayoutResourceId() {
        return R.layout.fragment_user_post;
    }

    @Override
    protected void findView() {
        recyclerView = view.findViewById(R.id.user_post_fragment_rv);
        refreshLayout = view.findViewById(R.id.user_post_fragment_refresh);
        hint = view.findViewById(R.id.user_post_fragment_hint);
    }

    @Override
    protected void initView() {
        userPostPresenter = (UserPostPresenter) presenter;

        userPostAdapter = new UserPostAdapter(R.layout.item_simple_post);
        recyclerView.setLayoutManager(new MyLinearLayoutManger(mActivity));
        recyclerView.setAdapter(userPostAdapter);
        LayoutAnimationController layoutAnimationController = AnimationUtils.loadLayoutAnimation(mActivity, R.anim.layout_animation_from_top);
        recyclerView.setLayoutAnimation(layoutAnimationController);

        refreshLayout.autoRefresh(0, 300, 1, false);
    }

    @Override
    protected BasePresenter initPresenter() {
        return new UserPostPresenter();
    }

    @Override
    protected void setOnItemClickListener() {
        userPostAdapter.setOnItemClickListener((adapter, view, position) -> {
            if (view.getId() == R.id.item_simple_post_card_view) {
                Intent intent = new Intent(mActivity, PostDetailActivity.class);
                intent.putExtra(Constant.IntentKey.TOPIC_ID, userPostAdapter.getData().get(position).topic_id);
                startActivity(intent);
            }
        });

        userPostAdapter.setOnItemChildClickListener((adapter, view, position) -> {
            if (view.getId() == R.id.item_simple_post_board_name) {
                Intent intent = new Intent(mActivity, SingleBoardActivity.class);
                intent.putExtra(Constant.IntentKey.BOARD_ID, userPostAdapter.getData().get(position).board_id);
                startActivity(intent);
            }
            if (view.getId() == R.id.item_simple_post_user_avatar) {
                Intent intent = new Intent(mActivity, UserDetailActivity.class);
                intent.putExtra(Constant.IntentKey.USER_ID, userPostAdapter.getData().get(position).user_id);
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
                userPostPresenter.userPost(page,
                        ApiConstant.SIMPLE_POST_LIST_SIZE, userId,
                        type, mActivity);
            }

            @Override
            public void onLoadMore(RefreshLayout refreshLayout) {
                userPostPresenter.userPost(page,
                        ApiConstant.SIMPLE_POST_LIST_SIZE, userId,
                        type, mActivity);
            }
        });
    }

    @Override
    public void onGetUserPostSuccess(UserPostBean userPostBean) {

        page = page + 1;

        if (refreshLayout.getState() == RefreshState.Refreshing) {
            if (userPostBean.has_next == 1) {
                refreshLayout.finishRefresh();
            } else {
                refreshLayout.finishRefreshWithNoMoreData();
            }
        }
        if (refreshLayout.getState() == RefreshState.Loading) {
            if (userPostBean.has_next == 1) {
                refreshLayout.finishLoadMore(true);
            } else {
                refreshLayout.finishLoadMoreWithNoMoreData();
            }
        }

        if (userPostBean.page == 1) {
            userPostAdapter.setNewData(userPostBean.list);
            recyclerView.scheduleLayoutAnimation();
        } else userPostAdapter.addData(userPostBean.list);

        hint.setText(userPostAdapter.getData().size() == 0 ? "啊哦，这里空空的" : "");
    }

    @Override
    public void onGetUserPostError(String msg) {
        if (refreshLayout.getState() == RefreshState.Refreshing) {
            refreshLayout.finishRefresh();
        }
        if (refreshLayout.getState() == RefreshState.Loading) {
            refreshLayout.finishLoadMore(false);
        }

        showSnackBar(getView(), msg);
    }
}
