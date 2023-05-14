package com.scatl.uestcbbs.module.board.view;


import android.os.Bundle;
import android.view.animation.AnimationUtils;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.scatl.uestcbbs.R;
import com.scatl.uestcbbs.annotation.ToastType;
import com.scatl.uestcbbs.base.BaseEvent;
import com.scatl.uestcbbs.base.BaseFragment;
import com.scatl.uestcbbs.base.BasePresenter;
import com.scatl.uestcbbs.callback.OnRefresh;
import com.scatl.uestcbbs.widget.MyLinearLayoutManger;
import com.scatl.uestcbbs.entity.ForumListBean;
import com.scatl.uestcbbs.module.board.adapter.ForumListLeftAdapter;
import com.scatl.uestcbbs.module.board.adapter.ForumListRightAdapter;
import com.scatl.uestcbbs.module.board.presenter.BoardListPresenter;
import com.scatl.uestcbbs.util.RefreshUtil;
import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.constant.RefreshState;

import org.greenrobot.eventbus.EventBus;

public class BoardListFragment extends BaseFragment implements BoardListView {

    private SmartRefreshLayout refreshLayout;
    private RecyclerView leftRv, rightRv;
    private ForumListLeftAdapter leftAdapter;
    private ForumListRightAdapter rightAdapter;

    private BoardListPresenter boardListPresenter;

    public static BoardListFragment getInstance(Bundle bundle) {
        BoardListFragment boardListFragment = new BoardListFragment();
        boardListFragment.setArguments(bundle);
        return boardListFragment;
    }

    @Override
    protected int setLayoutResourceId() {
        return R.layout.fragment_board_list;
    }

    @Override
    protected void findView() {
        refreshLayout = view.findViewById(R.id.board_list_refresh);
        leftRv = view.findViewById(R.id.board_list_left_rv);
        rightRv = view.findViewById(R.id.board_list_right_rv);
    }

    @Override
    protected void initView() {
        boardListPresenter = (BoardListPresenter) presenter;

        setRecyclerViewListener();

        leftAdapter = new ForumListLeftAdapter(R.layout.item_forum_list_left);
        leftRv.setLayoutManager(new MyLinearLayoutManger(mActivity));
        leftRv.setNestedScrollingEnabled(false);
        leftRv.setAdapter(leftAdapter);
        leftRv.setLayoutAnimation(AnimationUtils.loadLayoutAnimation(mActivity, R.anim.layout_animation_scale_in));

        rightAdapter = new ForumListRightAdapter(R.layout.item_forum_list_right);
        rightRv.setLayoutManager(new MyLinearLayoutManger(mActivity));
        rightRv.setAdapter(rightAdapter);
        rightRv.setLayoutAnimation(AnimationUtils.loadLayoutAnimation(mActivity, R.anim.layout_animation_scale_in));

        refreshLayout.setEnableLoadMore(false);
    }

    @Override
    protected void lazyLoad() {
        super.lazyLoad();
        refreshLayout.autoRefresh(10, 300, 1, false);
    }

    @Override
    protected BasePresenter initPresenter() {
        return new BoardListPresenter();
    }

    @Override
    protected void setOnItemClickListener() {
        leftAdapter.setOnItemClickListener((adapter, view, position) -> {
            leftAdapter.setSelected(position);
            rightRv.smoothScrollToPosition(position);
        });
    }

    @Override
    protected void setOnRefreshListener() {
        RefreshUtil.setOnRefreshListener(mActivity, refreshLayout, new OnRefresh() {
            @Override
            public void onRefresh(RefreshLayout refreshLayout) {
                boardListPresenter.getForumList(mActivity);
            }

            @Override
            public void onLoadMore(RefreshLayout refreshLayout) { }
        });
    }

    @Override
    public void onGetBoardListSuccess(ForumListBean forumListBean) {
        if (refreshLayout.getState() == RefreshState.Refreshing) refreshLayout.finishRefresh();

        //添加今日热门，筛选5个发帖量最多的板块
        ForumListBean.ListBean tdHot = new ForumListBean.ListBean();
        tdHot.board_category_name = "今日热门";
        forumListBean.list.add(0, tdHot);
        for (int j = 1; j < forumListBean.list.size(); j ++) {
            for (int k = 0; k < forumListBean.list.get(j).board_list.size(); k ++) {
                tdHot.board_list.add(forumListBean.list.get(j).board_list.get(k));
                if (tdHot.board_list.size() > 6) {
                    int aa = tdHot.board_list.get(0).td_posts_num;
                    int index = 0;
                    for (int m = 0; m < tdHot.board_list.size(); m ++) {
                        if (aa >= tdHot.board_list.get(m).td_posts_num) {
                            aa = tdHot.board_list.get(m).td_posts_num;
                            index = m;
                        }
                    }
                    tdHot.board_list.remove(index);
                }
            }
        }

        leftAdapter.setNewData(forumListBean.list);
        leftRv.scheduleLayoutAnimation();
        rightAdapter.setNewData(forumListBean.list);
        rightRv.scheduleLayoutAnimation();

    }

    @Override
    public void onGetBoardListError(String msg) {
        if (refreshLayout.getState() == RefreshState.Refreshing) refreshLayout.finishRefresh();
        showToast(msg, ToastType.TYPE_ERROR);
    }

    private void setRecyclerViewListener() {
        rightRv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                EventBus.getDefault().post(new BaseEvent<>(BaseEvent.EventCode.HOME_NAVIGATION_HIDE, dy > 0));
            }
        });
    }

    @Override
    protected boolean registerEventBus() {
        return true;
    }

    @Override
    public void onEventBusReceived(BaseEvent baseEvent) {
        if (baseEvent.eventCode == BaseEvent.EventCode.LOGIN_SUCCESS ||
                baseEvent.eventCode == BaseEvent.EventCode.LOGOUT_SUCCESS)
            refreshLayout.autoRefresh(10, 300, 1, false);
    }
}
