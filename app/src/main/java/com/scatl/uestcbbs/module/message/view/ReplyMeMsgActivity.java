package com.scatl.uestcbbs.module.message.view;

import android.content.Intent;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;

import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.scatl.uestcbbs.R;
import com.scatl.uestcbbs.annotation.ToastType;
import com.scatl.uestcbbs.base.BaseActivity;
import com.scatl.uestcbbs.base.BaseEvent;
import com.scatl.uestcbbs.base.BasePresenter;
import com.scatl.uestcbbs.callback.OnRefresh;
import com.scatl.uestcbbs.custom.MyLinearLayoutManger;
import com.scatl.uestcbbs.entity.ReplyMeMsgBean;
import com.scatl.uestcbbs.module.board.view.SingleBoardActivity;
import com.scatl.uestcbbs.module.message.adapter.ReplyMeMsgAdapter;
import com.scatl.uestcbbs.module.message.presenter.ReplyMeMsgPresenter;
import com.scatl.uestcbbs.module.post.view.CreateCommentActivity;
import com.scatl.uestcbbs.module.post.view.PostDetailActivity;
import com.scatl.uestcbbs.module.post.view.postdetail2.PostDetail2Activity;
import com.scatl.uestcbbs.module.user.view.UserDetailActivity;
import com.scatl.uestcbbs.util.Constant;
import com.scatl.uestcbbs.util.RefreshUtil;
import com.scatl.uestcbbs.util.SharePrefUtil;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.RefreshState;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

public class ReplyMeMsgActivity extends BaseActivity<ReplyMeMsgPresenter> implements ReplyMeMsgView{

    private Toolbar toolbar;
    private CoordinatorLayout coordinatorLayout;
    private SmartRefreshLayout refreshLayout;
    private RecyclerView recyclerView;
    private ReplyMeMsgAdapter replyMeMsgAdapter;

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
        super.initView();
        replyMeMsgAdapter = new ReplyMeMsgAdapter(R.layout.item_reply_me_msg);
        recyclerView.setLayoutManager(new MyLinearLayoutManger(this));
        recyclerView.setAdapter(replyMeMsgAdapter);
        LayoutAnimationController layoutAnimationController = AnimationUtils.loadLayoutAnimation(this, R.anim.layout_animation_from_top);
        recyclerView.setLayoutAnimation(layoutAnimationController);

        refreshLayout.autoRefresh(0, 300, 1, false);
    }

    @Override
    protected Toolbar getToolbar() {
        return toolbar;
    }

    @Override
    protected ReplyMeMsgPresenter initPresenter() {
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
//                Bundle bundle = new Bundle();
//                bundle.putInt(Constant.IntentKey.BOARD_ID, replyMeMsgAdapter.getData().get(position).board_id);
//                bundle.putInt(Constant.IntentKey.TOPIC_ID, replyMeMsgAdapter.getData().get(position).topic_id);
//                bundle.putInt(Constant.IntentKey.QUOTE_ID, replyMeMsgAdapter.getData().get(position).reply_remind_id);
//                bundle.putBoolean(Constant.IntentKey.IS_QUOTE, true);
//                bundle.putString(Constant.IntentKey.USER_NAME, replyMeMsgAdapter.getData().get(position).user_name);
//                CreateCommentFragment.getInstance(bundle)
//                        .show(getSupportFragmentManager(), TimeUtil.getStringMs());

                Intent intent = new Intent(this, CreateCommentActivity.class);
                intent.putExtra(Constant.IntentKey.BOARD_ID, replyMeMsgAdapter.getData().get(position).board_id);
                intent.putExtra(Constant.IntentKey.TOPIC_ID, replyMeMsgAdapter.getData().get(position).topic_id);
                intent.putExtra(Constant.IntentKey.QUOTE_ID, replyMeMsgAdapter.getData().get(position).reply_remind_id);
                intent.putExtra(Constant.IntentKey.IS_QUOTE, true);
                intent.putExtra(Constant.IntentKey.USER_NAME, replyMeMsgAdapter.getData().get(position).user_name);
                startActivity(intent);
            }

            if (view.getId() == R.id.item_reply_me_user_icon) {
                Intent intent = new Intent(this, UserDetailActivity.class);
                intent.putExtra(Constant.IntentKey.USER_ID, replyMeMsgAdapter.getData().get(position).user_id);
                startActivity(intent);
            }

            if (view.getId() == R.id.item_reply_me_quote_rl) {
                Intent intent = new Intent(this, SharePrefUtil.isPostDetailNewStyle(this) ? PostDetail2Activity.class : PostDetailActivity.class);
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
                presenter.getReplyMeMsg(page, SharePrefUtil.getPageSize(ReplyMeMsgActivity.this), ReplyMeMsgActivity.this);
            }

            @Override
            public void onLoadMore(RefreshLayout refreshLayout) {
                page = page + 1;
                presenter.getReplyMeMsg(page, SharePrefUtil.getPageSize(ReplyMeMsgActivity.this), ReplyMeMsgActivity.this);
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

        //屏蔽已匿名，但是显示未匿名的用户
        List<ReplyMeMsgBean.BodyBean.DataBean> newList = new ArrayList<>();
        for (int i = 0; i < replyMeMsgBean.body.data.size(); i ++) {
            if ((replyMeMsgBean.body.data.get(i).user_name == null ||
                    replyMeMsgBean.body.data.get(i).user_name.length() == 0) && SharePrefUtil.isHideAnonymousPost(this)) {
                replyMeMsgBean.body.data.get(i).user_name = Constant.ANONYMOUS_NAME;
                replyMeMsgBean.body.data.get(i).icon = Constant.DEFAULT_AVATAR;
                replyMeMsgBean.body.data.get(i).user_id = 0;
            }
            newList.add(replyMeMsgBean.body.data.get(i));
        }
        if (replyMeMsgBean.page == 1) {
            replyMeMsgAdapter.setNewData(newList);
            recyclerView.scheduleLayoutAnimation();
        } else replyMeMsgAdapter.addData(newList);

        EventBus.getDefault().post(new BaseEvent<>(BaseEvent.EventCode.SET_NEW_REPLY_COUNT_ZERO));
    }

    @Override
    public void onGetReplyMeMsgError(String msg) {
        if (refreshLayout.getState() == RefreshState.Refreshing) {
            refreshLayout.finishRefresh();
        }
        if (refreshLayout.getState() == RefreshState.Loading) {
            refreshLayout.finishLoadMore(false);
        }
        showToast(msg, ToastType.TYPE_ERROR);
    }
}
