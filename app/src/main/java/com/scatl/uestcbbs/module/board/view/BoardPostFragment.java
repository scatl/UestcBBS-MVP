package com.scatl.uestcbbs.module.board.view;


import android.content.Intent;
import android.os.Bundle;

import androidx.recyclerview.widget.RecyclerView;

import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.scatl.uestcbbs.R;
import com.scatl.uestcbbs.api.ApiConstant;
import com.scatl.uestcbbs.base.BaseEvent;
import com.scatl.uestcbbs.base.BaseFragment;
import com.scatl.uestcbbs.base.BasePresenter;
import com.scatl.uestcbbs.callback.OnRefresh;
import com.scatl.uestcbbs.custom.MyLinearLayoutManger;
import com.scatl.uestcbbs.entity.SingleBoardBean;
import com.scatl.uestcbbs.module.board.adapter.BoardPostAdapter;
import com.scatl.uestcbbs.module.board.presenter.BoardPostPresenter;
import com.scatl.uestcbbs.module.post.view.PostDetailActivity;
import com.scatl.uestcbbs.module.post.view.postdetail2.PostDetail2Activity;
import com.scatl.uestcbbs.module.user.view.UserDetailActivity;
import com.scatl.uestcbbs.module.webview.view.WebViewActivity;
import com.scatl.uestcbbs.util.Constant;
import com.scatl.uestcbbs.util.ImageUtil;
import com.scatl.uestcbbs.util.RefreshUtil;
import com.scatl.uestcbbs.util.SharePrefUtil;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.RefreshState;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

public class BoardPostFragment extends BaseFragment implements BoardPostView{

    private SmartRefreshLayout refreshLayout;
    private RecyclerView recyclerView;
    private BoardPostAdapter boardPostAdapter;
    private TextView hint;
    private LinearLayout error500Layout;
    private Button openBrowserBtn;

    public static final String TYPE_NEW = "new";
    public static final String TYPE_ALL = "all";
    public static final String TYPE_ESSENCE = "essence";

    private int boardId, fid, page = 1;
    private String sortBy;

    private BoardPostPresenter boardPostPresenter;

    public static BoardPostFragment getInstance(Bundle bundle) {
        BoardPostFragment boardPostFragment = new BoardPostFragment();
        boardPostFragment.setArguments(bundle);
        return boardPostFragment;
    }

    @Override
    protected void getBundle(Bundle bundle) {
        super.getBundle(bundle);
        if (bundle != null) {
            boardId = bundle.getInt(Constant.IntentKey.BOARD_ID, Integer.MAX_VALUE);
            fid = bundle.getInt(Constant.IntentKey.FILTER_ID, Integer.MAX_VALUE);
            sortBy = bundle.getString(Constant.IntentKey.TYPE);
        }
    }

    @Override
    protected int setLayoutResourceId() {
        return R.layout.fragment_board_post;
    }

    @Override
    protected void findView() {
        recyclerView = view.findViewById(R.id.fragment_board_post_rv);
        refreshLayout = view.findViewById(R.id.fragment_board_post_refresh);
        hint = view.findViewById(R.id.fragment_board_post_hint);
        error500Layout = view.findViewById(R.id.fragment_board_error_500_layout);
        openBrowserBtn = view.findViewById(R.id.fragment_board_open_browser_btn);
    }

    @Override
    protected void initView() {
        boardPostPresenter = (BoardPostPresenter) presenter;

        openBrowserBtn.setOnClickListener(this);

        boardPostAdapter = new BoardPostAdapter(R.layout.item_simple_post);
        recyclerView.setLayoutManager(new MyLinearLayoutManger(mActivity));
        recyclerView.setAdapter(boardPostAdapter);
        recyclerView.setLayoutAnimation(AnimationUtils.loadLayoutAnimation(mActivity, R.anim.layout_animation_scale_in));

    }

    @Override
    protected void lazyLoad() {
        super.lazyLoad();
        refreshLayout.autoRefresh(0, 300, 1, false);
    }

    @Override
    protected BasePresenter initPresenter() {
        return new BoardPostPresenter();
    }

    @Override
    protected void onClickListener(View v) {
        if (v.getId() == R.id.fragment_board_open_browser_btn) {
            Intent intent = new Intent(mActivity, WebViewActivity.class);
            intent.putExtra(Constant.IntentKey.URL, ApiConstant.Post.BOARD_URL + boardId);
            startActivity(intent);
        }
    }

    @Override
    protected void setOnItemClickListener() {
        boardPostAdapter.setOnItemClickListener((adapter, view, position) -> {
            if (view.getId() == R.id.item_simple_post_card_view) {
                Intent intent = new Intent(mActivity, SharePrefUtil.isPostDetailNewStyle(mActivity) ? PostDetail2Activity.class : PostDetailActivity.class);
                intent.putExtra(Constant.IntentKey.TOPIC_ID, boardPostAdapter.getData().get(position).topic_id);
                startActivity(intent);
            }
        });

        boardPostAdapter.setOnItemChildClickListener((adapter, view, position) -> {
            if (view.getId() == R.id.item_simple_post_board_name) {
                Intent intent = new Intent(mActivity, SingleBoardActivity.class);
                intent.putExtra(Constant.IntentKey.BOARD_ID, boardPostAdapter.getData().get(position).board_id);
                startActivity(intent);
            }
            if (view.getId() == R.id.item_simple_post_user_avatar) {
                Intent intent = new Intent(mActivity, UserDetailActivity.class);
                intent.putExtra(Constant.IntentKey.USER_ID, boardPostAdapter.getData().get(position).user_id);
                startActivity(intent);
            }
        });

        boardPostAdapter.setOnImgClickListener((imgUrls, selected) -> ImageUtil.showImages(mActivity, imgUrls, selected));
    }

    @Override
    protected void setOnRefreshListener() {
        RefreshUtil.setOnRefreshListener(mActivity, refreshLayout, new OnRefresh() {
            @Override
            public void onRefresh(RefreshLayout refreshLayout) {
                page = 1;
                boardPostPresenter.getBoardPostList(page,
                        SharePrefUtil.getPageSize(mActivity), 1,
                        boardId, fid, "typeid", sortBy,
                        mActivity);
            }

            @Override
            public void onLoadMore(RefreshLayout refreshLayout) {
                boardPostPresenter.getBoardPostList(page,
                        SharePrefUtil.getPageSize(mActivity), 1,
                        boardId, fid, "typeid", sortBy,
                        mActivity);
            }
        });
    }

    @Override
    public void onGetBoardPostSuccess(SingleBoardBean singleBoardBean) {
        page = page + 1;

        error500Layout.setVisibility(View.GONE);

        if (refreshLayout.getState() == RefreshState.Refreshing) {
            if (singleBoardBean.has_next == 1) {
                refreshLayout.finishRefresh();
            } else {
                refreshLayout.finishRefreshWithNoMoreData();
            }
        }
        if (refreshLayout.getState() == RefreshState.Loading) {
            if (singleBoardBean.has_next == 1) {
                refreshLayout.finishLoadMore(true);
            } else {
                refreshLayout.finishLoadMoreWithNoMoreData();
            }
        }

        if (singleBoardBean.page == 1) {
            boardPostAdapter.addData(singleBoardBean.list, true);
            //boardPostAdapter.setNewData(singleBoardBean.list);
            recyclerView.scheduleLayoutAnimation();
        } else {
            boardPostAdapter.addData(singleBoardBean.list, false);
            //boardPostAdapter.addData(singleBoardBean.list);
        }

        hint.setText(boardPostAdapter.getData().size() == 0 ? "啊哦，这里空空的" : "");
    }

    @Override
    public void onGetBoardPostError(String msg) {
        if (refreshLayout.getState() == RefreshState.Refreshing) {
            refreshLayout.finishRefresh();
        }
        if (refreshLayout.getState() == RefreshState.Loading) {
            refreshLayout.finishLoadMore(false);
        }

        if (msg.contains(ApiConstant.Code.RESPONSE_ERROR_500)) {
            error500Layout.setVisibility(View.VISIBLE);
        } else {
            error500Layout.setVisibility(View.GONE);
            hint.setText(msg);
        }

    }
}
