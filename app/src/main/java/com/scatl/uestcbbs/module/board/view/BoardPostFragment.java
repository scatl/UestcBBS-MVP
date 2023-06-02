package com.scatl.uestcbbs.module.board.view;


import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.scatl.uestcbbs.R;
import com.scatl.uestcbbs.annotation.ToastType;
import com.scatl.uestcbbs.api.ApiConstant;
import com.scatl.uestcbbs.base.BaseFragment;
import com.scatl.uestcbbs.base.BasePresenter;
import com.scatl.uestcbbs.callback.HapticClickListener;
import com.scatl.uestcbbs.callback.OnRefresh;
import com.scatl.uestcbbs.entity.CommonPostBean;
import com.scatl.uestcbbs.module.post.adapter.CommonPostAdapter;
import com.scatl.uestcbbs.module.webview.view.WebViewActivity;
import com.scatl.uestcbbs.widget.MyLinearLayoutManger;

import com.scatl.uestcbbs.module.board.presenter.BoardPostPresenter;
import com.scatl.uestcbbs.module.post.view.NewPostDetailActivity;
import com.scatl.uestcbbs.module.user.view.UserDetailActivity;
import com.scatl.uestcbbs.util.Constant;
import com.scatl.uestcbbs.util.RefreshUtil;
import com.scatl.uestcbbs.util.SharePrefUtil;
import com.scatl.util.ScreenUtil;
import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.constant.RefreshState;

public class BoardPostFragment extends BaseFragment implements BoardPostView{

    private SmartRefreshLayout refreshLayout;
    private RecyclerView recyclerView;
    private CommonPostAdapter boardPostAdapter;
    private TextView hint, errorText;
    private LinearLayout error500Layout;
    private Button openBrowserBtn;
    private View filterView;
    private ChipGroup mChipGroup;

    private int boardId, mFid, page = 1;
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
            mFid = bundle.getInt(Constant.IntentKey.FILTER_ID, Integer.MAX_VALUE);
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
        errorText = view.findViewById(R.id.fragment_board_error_text);

        filterView = LayoutInflater.from(mActivity).inflate(R.layout.view_board_post_filter, null, false);
        mChipGroup = filterView.findViewById(R.id.chip_group);
    }

    @Override
    protected void initView() {
        boardPostPresenter = (BoardPostPresenter) presenter;

        openBrowserBtn.setOnClickListener(this);

        boardPostAdapter = new CommonPostAdapter(R.layout.item_common_post, "", null);
        boardPostAdapter.addHeaderView(filterView, 0);
        recyclerView.setLayoutManager(new MyLinearLayoutManger(mActivity));
        recyclerView.setAdapter(boardPostAdapter);
        recyclerView.setLayoutAnimation(AnimationUtils.loadLayoutAnimation(mActivity, R.anim.layout_animation_scale_in));

    }

    @Override
    protected void lazyLoad() {
        super.lazyLoad();
        refreshLayout.autoRefresh(10, 300, 1, false);
    }

    @Override
    protected BasePresenter initPresenter() {
        return new BoardPostPresenter();
    }

    @Override
    protected void onClickListener(View v) {
        if (v.getId() == R.id.fragment_board_open_browser_btn) {
            if (openBrowserBtn.getTag().equals(ErrorStatus.STATUS_500)) {
                Intent intent = new Intent(mActivity, WebViewActivity.class);
                intent.putExtra(Constant.IntentKey.URL, ApiConstant.Post.BOARD_URL + boardId);
                startActivity(intent);
            } else if (openBrowserBtn.getTag().equals(ErrorStatus.STATUS_NEED_PAY)) {
                boardPostPresenter.payForVisiting(boardId, SharePrefUtil.getForumHash(mActivity));
            }
        }
    }

    @Override
    protected void setOnItemClickListener() {
        boardPostAdapter.setOnItemChildClickListener((adapter, view, position) -> {
            if (view.getId() == R.id.avatar) {
                Intent intent = new Intent(mActivity, UserDetailActivity.class);
                intent.putExtra(Constant.IntentKey.USER_ID, boardPostAdapter.getData().get(position).user_id);
                startActivity(intent);
            }
            if (view.getId() == R.id.content_layout) {
                Intent intent = new Intent(mActivity, NewPostDetailActivity.class);
                intent.putExtra(Constant.IntentKey.TOPIC_ID, boardPostAdapter.getData().get(position).topic_id);
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
                boardPostPresenter.getBoardPostList(page,
                        SharePrefUtil.getPageSize(mActivity), 1,
                        boardId, mFid, "typeid", sortBy,
                        mActivity);
            }

            @Override
            public void onLoadMore(RefreshLayout refreshLayout) {
                boardPostPresenter.getBoardPostList(page,
                        SharePrefUtil.getPageSize(mActivity), 1,
                        boardId, mFid, "typeid", sortBy,
                        mActivity);
            }
        });
    }

    @Override
    public void onGetBoardPostSuccess(CommonPostBean singleBoardBean) {
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
            setFilterView(singleBoardBean);
            recyclerView.scheduleLayoutAnimation();
        } else {
            boardPostAdapter.addData(singleBoardBean.list, false);
        }

        hint.setText(boardPostAdapter.getData().size() == 0 ? "啊哦，这里空空的" : "");
    }

    private void setFilterView(CommonPostBean singleBoardBean) {
        if (singleBoardBean.classificationType_list != null && singleBoardBean.classificationType_list.size() > 0) {
            mChipGroup.removeAllViews();
            mChipGroup.addView(getChip("全部", 0));
            for (int i = 0; i < singleBoardBean.classificationType_list.size(); i ++) {
                Chip chip = getChip(singleBoardBean.classificationType_list.get(i).classificationType_name,
                        singleBoardBean.classificationType_list.get(i).classificationType_id);
                mChipGroup.addView(chip);
            }
            filterView.setVisibility(View.VISIBLE);
        } else {
            filterView.setVisibility(View.GONE);
        }
    }

    private Chip getChip(String text, final int filterId) {
        Chip chip = new Chip(new ContextThemeWrapper(mActivity, R.style.Widget_Material3_Chip_Filter));
        chip.setText(text);
        chip.setCheckable(true);
        chip.setChipStrokeWidth(0);
        chip.setChipCornerRadius(ScreenUtil.dip2px(mActivity, 25));
        chip.setChipStrokeColor(ColorStateList.valueOf(Color.parseColor("#00000000")));
        chip.setChecked(filterId == mFid);
        chip.setOnClickListener(new HapticClickListener() {
            @Override
            public void onViewClick(@NonNull View v) {
                Chip c = (Chip) v;
                if (c.isChecked()) {
                    mFid = filterId;
                    recyclerView.scrollToPosition(0);
                    refreshLayout.autoRefresh(10, 300, 1, false);
                } else {
                    c.setChecked(true);
                }
            }
        });
        return chip;
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
            errorText.setText(getString(R.string.board_error_500));
            openBrowserBtn.setText("打开Web页面");
            openBrowserBtn.setTag(ErrorStatus.STATUS_500);
            error500Layout.setVisibility(View.VISIBLE);
        } else if (msg.contains("您需要支付")){
            errorText.setText(msg);
            openBrowserBtn.setTag(ErrorStatus.STATUS_NEED_PAY);
            openBrowserBtn.setText("立即支付");
            error500Layout.setVisibility(View.VISIBLE);
        } else {
            error500Layout.setVisibility(View.GONE);
            hint.setText(msg);
        }
    }

    @Override
    public void onPaySuccess(String msg) {
        error500Layout.setVisibility(View.GONE);
        showToast(msg, ToastType.TYPE_SUCCESS);
    }

    @Override
    public void onPayError(String msg) {
        showToast(msg, ToastType.TYPE_ERROR);
    }

    private enum ErrorStatus {
        STATUS_NEED_PAY,
        STATUS_500
    }
}
