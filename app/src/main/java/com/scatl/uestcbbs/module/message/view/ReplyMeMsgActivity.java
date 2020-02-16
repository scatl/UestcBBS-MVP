package com.scatl.uestcbbs.module.message.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.ImageView;

import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.scatl.uestcbbs.R;
import com.scatl.uestcbbs.api.ApiConstant;
import com.scatl.uestcbbs.base.BaseActivity;
import com.scatl.uestcbbs.base.BasePresenter;
import com.scatl.uestcbbs.callback.OnRefresh;
import com.scatl.uestcbbs.custom.MyLinearLayoutManger;
import com.scatl.uestcbbs.entity.ReplyMeMsgBean;
import com.scatl.uestcbbs.module.board.view.SingleBoardActivity;
import com.scatl.uestcbbs.module.message.adapter.AtMeMsgAdapter;
import com.scatl.uestcbbs.module.message.adapter.ReplyMeMsgAdapter;
import com.scatl.uestcbbs.module.message.presenter.AtMeMsgPresenter;
import com.scatl.uestcbbs.module.message.presenter.ReplyMeMsgPresenter;
import com.scatl.uestcbbs.module.post.view.PostCreateCommentFragment;
import com.scatl.uestcbbs.module.post.view.PostDetailActivity;
import com.scatl.uestcbbs.module.user.view.UserDetailActivity;
import com.scatl.uestcbbs.util.Constant;
import com.scatl.uestcbbs.util.RefreshUtil;
import com.scatl.uestcbbs.util.TimeUtil;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.RefreshState;

public class ReplyMeMsgActivity extends BaseActivity implements ReplyMeMsgView{

    private Toolbar toolbar;
    private CoordinatorLayout coordinatorLayout;
    private SmartRefreshLayout refreshLayout;
    private RecyclerView recyclerView;
    private ReplyMeMsgAdapter replyMeMsgAdapter;

    private ReplyMeMsgPresenter replyMeMsgPresenter;

    private int page = 1;

    @Override
    protected int setLayoutResourceId() {
        return R.layout.activity_reply_me_msg;
    }

    @Override
    protected void findView() {
        toolbar = findViewById(R.id.reply_me_msg_toolbar);
        coordinatorLayout = findViewById(R.id.reply_me_msg_coor_layout);
        refreshLayout = findViewById(R.id.reply_me_msg_refresh);
        recyclerView = findViewById(R.id.reply_me_msg_rv);
    }

    @Override
    protected void initView() {
        replyMeMsgPresenter = (ReplyMeMsgPresenter) presenter;

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        replyMeMsgAdapter = new ReplyMeMsgAdapter(R.layout.item_reply_me_msg);
        recyclerView.setLayoutManager(new MyLinearLayoutManger(this));
        recyclerView.setAdapter(replyMeMsgAdapter);
        LayoutAnimationController layoutAnimationController = AnimationUtils.loadLayoutAnimation(this, R.anim.layout_animation_from_top);
        recyclerView.setLayoutAnimation(layoutAnimationController);

        refreshLayout.autoRefresh(0, 300, 1, false);
    }

    @Override
    protected BasePresenter initPresenter() {
        return new ReplyMeMsgPresenter();
    }

    @Override
    protected void setOnItemClickListener() {
        replyMeMsgAdapter.setOnItemClickListener((adapter, view, position) -> {
//            if (view.getId() == R.id.item_at_me_cardview) {
//                Intent intent = new Intent(this, PostDetailActivity.class);
//                intent.putExtra(Constant.IntentKey.TOPIC_ID, atMeMsgAdapter.getData().get(position).topic_id);
//                startActivity(intent);
//            }
        });

        replyMeMsgAdapter.setOnItemChildClickListener((adapter, view, position) -> {
            if (view.getId() == R.id.item_reply_me_reply_btn) {
                Bundle bundle = new Bundle();
                bundle.putInt(Constant.IntentKey.BOARD_ID, replyMeMsgAdapter.getData().get(position).board_id);
                bundle.putInt(Constant.IntentKey.TOPIC_ID, replyMeMsgAdapter.getData().get(position).topic_id);
                bundle.putInt(Constant.IntentKey.QUOTE_ID, replyMeMsgAdapter.getData().get(position).reply_remind_id);
                bundle.putBoolean(Constant.IntentKey.IS_QUOTE, true);
                bundle.putString(Constant.IntentKey.USER_NAME, replyMeMsgAdapter.getData().get(position).user_name);
                PostCreateCommentFragment.getInstance(bundle)
                        .show(getSupportFragmentManager(), TimeUtil.getStringMs());
            }

            if (view.getId() == R.id.item_reply_me_user_icon) {
                Intent intent = new Intent(this, UserDetailActivity.class);
                intent.putExtra(Constant.IntentKey.USER_ID, replyMeMsgAdapter.getData().get(position).user_id);
                startActivity(intent);
            }

            if (view.getId() == R.id.item_reply_me_quote_rl) {
                Intent intent = new Intent(this, PostDetailActivity.class);
                intent.putExtra(Constant.IntentKey.TOPIC_ID, replyMeMsgAdapter.getData().get(position).topic_id);
                startActivity(intent);
            }

            if (view.getId() == R.id.item_reply_me_board_name) {
                Intent intent = new Intent(this, SingleBoardActivity.class);
                intent.putExtra(Constant.IntentKey.BOARD_ID, replyMeMsgAdapter.getData().get(position).board_id);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void setOnRefreshListener() {
        RefreshUtil.setOnRefreshListener(this, refreshLayout, new OnRefresh() {
            @Override
            public void onRefresh(RefreshLayout refreshLayout) {
                page = 1;
                replyMeMsgPresenter.getReplyMeMsg(page, ApiConstant.SIMPLE_POST_LIST_SIZE, ReplyMeMsgActivity.this);
            }

            @Override
            public void onLoadMore(RefreshLayout refreshLayout) {
                page = page + 1;
                replyMeMsgPresenter.getReplyMeMsg(page, ApiConstant.SIMPLE_POST_LIST_SIZE, ReplyMeMsgActivity.this);
            }
        });
    }

    @Override
    public void onGetReplyMeMsgSuccess(ReplyMeMsgBean replyMeMsgBean) {
        if (refreshLayout.getState() == RefreshState.Refreshing) {
            if (replyMeMsgBean.has_next == 1) {
                refreshLayout.finishRefresh();
            } else {
                refreshLayout.finishRefreshWithNoMoreData();
            }
        }
        if (refreshLayout.getState() == RefreshState.Loading) {
            if (replyMeMsgBean.has_next == 1) {
                refreshLayout.finishLoadMore(true);
            } else {
                refreshLayout.finishLoadMoreWithNoMoreData();
            }
        }

        if (replyMeMsgBean.page == 1) {
            recyclerView.scheduleLayoutAnimation();
            replyMeMsgAdapter.setNewData(replyMeMsgBean.body.data);
        } else {
            replyMeMsgAdapter.addData(replyMeMsgBean.body.data);
        }
    }

    @Override
    public void onGetReplyMeMsgError(String msg) {
        if (refreshLayout.getState() == RefreshState.Refreshing) {
            refreshLayout.finishRefresh();
        }
        if (refreshLayout.getState() == RefreshState.Loading) {
            refreshLayout.finishLoadMore(false);
        }
        showSnackBar(coordinatorLayout, msg);
    }
}
