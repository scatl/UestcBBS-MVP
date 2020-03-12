package com.scatl.uestcbbs.module.post.view;


import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.scatl.uestcbbs.R;
import com.scatl.uestcbbs.base.BaseBottomFragment;
import com.scatl.uestcbbs.base.BaseEvent;
import com.scatl.uestcbbs.base.BasePresenter;
import com.scatl.uestcbbs.entity.ForumListBean;
import com.scatl.uestcbbs.entity.SingleBoardBean;
import com.scatl.uestcbbs.entity.SubForumListBean;
import com.scatl.uestcbbs.module.post.presenter.SelectBoardPresenter;
import com.zhy.view.flowlayout.TagFlowLayout;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

public class SelectBoardFragment extends BaseBottomFragment implements SelectBoardView{

    private TagFlowLayout tagLayout1, tagLayout2, tagLayout3, tagLayout4;
    private RelativeLayout layout1, layout2, layout3, layout4;
    private ProgressBar progressBar;
    private TextView hint;

    private SelectBoardPresenter selectBoardPresenter;

    private ForumListBean forumListBean;

    private int currentBoardId, currentFid;
    private String currentBoardName, currentFilterName;

    public static SelectBoardFragment getInstance(Bundle bundle) {
        SelectBoardFragment selectBoardFragment = new SelectBoardFragment();
        selectBoardFragment.setArguments(bundle);
        return selectBoardFragment;
    }

    @Override
    protected int setLayoutResourceId() {
        return R.layout.fragment_select_board;
    }

    @Override
    protected void findView() {
        tagLayout1 = view.findViewById(R.id.select_board_tag_layout_1);
        tagLayout2 = view.findViewById(R.id.select_board_tag_layout_2);
        tagLayout3 = view.findViewById(R.id.select_board_tag_layout_3);
        tagLayout4 = view.findViewById(R.id.select_board_tag_layout_4);
        layout1 = view.findViewById(R.id.select_board_layout_1);
        layout2 = view.findViewById(R.id.select_board_layout_2);
        layout3 = view.findViewById(R.id.select_board_layout_3);
        layout4 = view.findViewById(R.id.select_board_layout_4);
        progressBar = view.findViewById(R.id.select_board_progressbar);
        hint = view.findViewById(R.id.select_board_hint);
    }

    @Override
    protected void initView() {
        selectBoardPresenter = (SelectBoardPresenter) presenter;
        mBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        selectBoardPresenter.getForumList(mActivity);
        layout1.setVisibility(View.GONE);
        layout2.setVisibility(View.GONE);
        layout3.setVisibility(View.GONE);
        layout4.setVisibility(View.GONE);
    }

    @Override
    protected BasePresenter initPresenter() {
        return new SelectBoardPresenter();
    }

    @Override
    public void onGetBoardListSuccess(ForumListBean forumListBean) {
        this.forumListBean = forumListBean;
        layout1.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
        selectBoardPresenter.initTagLayout1(mActivity, forumListBean, tagLayout1);
    }

    @Override
    public void onGetBoardListError(String msg) {
        progressBar.setVisibility(View.GONE);
        hint.setText(msg);
    }

    @Override
    public void onGetSubBoardListSuccess(SubForumListBean subForumListBean) {
        layout3.setVisibility(View.VISIBLE);
        selectBoardPresenter.initTagLayout3(mActivity, subForumListBean, tagLayout3);
    }

    @Override
    public void onGetSubBoardListError(String msg) {
        showToast("加载子版块失败");
    }

    @Override
    public void onGetSingleBoardDataSuccess(SingleBoardBean singleBoardBean) {
        layout4.setVisibility(View.VISIBLE);
        selectBoardPresenter.initTagLayout4(mActivity, singleBoardBean, tagLayout4);
    }

    @Override
    public void onGetSingleBoardDataError(String msg) {
        showToast( "加载分类失败，请重新加载或选择不分类");
        SingleBoardBean singleBoardBean = new SingleBoardBean();
        singleBoardBean.classificationType_list = new ArrayList<>();
        SingleBoardBean.ClassificationTypeListBean sc = new SingleBoardBean.ClassificationTypeListBean();
        sc.classificationType_name = "不分类";
        sc.classificationType_id = 0;
        singleBoardBean.classificationType_list.add(0, sc);
        selectBoardPresenter.initTagLayout4(mActivity, singleBoardBean, tagLayout4);
        layout4.setVisibility(View.VISIBLE);
    }

    @Override
    public void onTagLayout1Select(int position) {
        layout3.setVisibility(View.GONE);
        layout4.setVisibility(View.GONE);
        layout2.setVisibility(View.VISIBLE);
        selectBoardPresenter.initTagLayout2(mActivity, forumListBean.list.get(position).board_list, tagLayout2);
    }

    @Override
    public void onTagLayout2Select(int boardId, String boardName) {
        layout3.setVisibility(View.GONE);
        layout4.setVisibility(View.GONE);
        selectBoardPresenter.getSubBoardList(boardId, boardName, mActivity);
    }

    @Override
    public void onTagLayout3Select(int boardId, String boardName) {
        currentBoardId = boardId;
        currentBoardName = boardName;
        selectBoardPresenter.getSingleBoardPostList(boardId, mActivity);
    }

    @Override
    public void onTagLayout4Select(int filterId, String filterName) {
        currentFid  = filterId;
        currentFilterName = filterName;

        BaseEvent.BoardSelected boardSelected = new BaseEvent.BoardSelected();
        boardSelected.boardName = currentBoardName;
        boardSelected.boardId = currentBoardId;
        boardSelected.filterName = currentFilterName;
        boardSelected.filterId = currentFid;
        EventBus.getDefault().post(new BaseEvent<>(BaseEvent.EventCode.BOARD_SELECTED, boardSelected));

        dismiss();
    }

    @Override
    protected double setMaxHeightMultiplier() {
        return 0.9;
    }
}
