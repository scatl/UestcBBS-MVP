package com.scatl.uestcbbs.module.home.view;

import android.content.Intent;
import android.os.Bundle;

import androidx.recyclerview.widget.RecyclerView;

import android.view.animation.AnimationUtils;

import com.alibaba.fastjson.JSON;
import com.scatl.uestcbbs.R;
import com.scatl.uestcbbs.annotation.PostSortByType;
import com.scatl.uestcbbs.base.BaseEvent;
import com.scatl.uestcbbs.base.BaseFragment;
import com.scatl.uestcbbs.base.BasePresenter;
import com.scatl.uestcbbs.callback.OnRefresh;
import com.scatl.uestcbbs.custom.MyLinearLayoutManger;
import com.scatl.uestcbbs.entity.HotPostBean;
import com.scatl.uestcbbs.entity.SimplePostListBean;
import com.scatl.uestcbbs.module.board.view.SingleBoardActivity;
import com.scatl.uestcbbs.module.home.adapter.HomeAdapter;
import com.scatl.uestcbbs.module.home.presenter.PostListPresenter;
import com.scatl.uestcbbs.module.post.adapter.HotPostAdapter;
import com.scatl.uestcbbs.module.post.view.PostDetailActivity;
import com.scatl.uestcbbs.module.user.view.UserDetailActivity;
import com.scatl.uestcbbs.util.Constant;
import com.scatl.uestcbbs.util.FileUtil;
import com.scatl.uestcbbs.util.RefreshUtil;
import com.scatl.uestcbbs.util.SharePrefUtil;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.RefreshState;

import java.io.File;

/**
 * author: sca_tl
 * description: 首页最新回复
 */
public class PostListFragment extends BaseFragment implements PostListView{

    private SmartRefreshLayout refreshLayout;
    private RecyclerView recyclerView;

    private HotPostAdapter hotPostAdapter;
    private HomeAdapter simplePostAdapter;

    private String type;
    private int page;

    private PostListPresenter postListPresenter;

    public static PostListFragment getInstance(Bundle bundle) {
        PostListFragment postListFragment = new PostListFragment();
        postListFragment.setArguments(bundle);
        return postListFragment;
    }

    @Override
    protected void getBundle(Bundle bundle) {
        super.getBundle(bundle);
        if (bundle != null) {
            type = bundle.getString(Constant.IntentKey.TYPE, "");
        }
    }

    @Override
    protected int setLayoutResourceId() {
        return R.layout.fragment_post_list;
    }

    @Override
    protected void findView() {
        refreshLayout = view.findViewById(R.id.post_list_refresh);
        recyclerView = view.findViewById(R.id.post_list_rv);
    }

    @Override
    protected void initView() {

        postListPresenter = (PostListPresenter) presenter;

        simplePostAdapter = new HomeAdapter(R.layout.item_simple_post);
        hotPostAdapter = new HotPostAdapter(R.layout.item_hot_post);

        recyclerView.setLayoutManager(new MyLinearLayoutManger(mActivity));
        recyclerView.setLayoutAnimation(AnimationUtils.loadLayoutAnimation(mActivity, R.anim.layout_animation_scale_in));
        recyclerView.setAdapter(type.equals(PostSortByType.TYPE_HOT) ? hotPostAdapter : simplePostAdapter);
        recyclerView.scheduleLayoutAnimation();

//        postListPresenter.initSavedData(mActivity, type);
//        refreshLayout.autoRefresh(0, 300, 1, false);
    }

    @Override
    protected void lazyLoad() {
        super.lazyLoad();
        refreshLayout.autoRefresh(0, 300, 1, false);

    }

    @Override
    protected BasePresenter initPresenter() {
        return new PostListPresenter();
    }

    @Override
    protected void setOnItemClickListener() {
        simplePostAdapter.setOnItemClickListener((adapter, view1, position) -> {
            if (view1.getId() == R.id.item_simple_post_card_view) {
                Intent intent = new Intent(mActivity, PostDetailActivity.class);
                intent.putExtra(Constant.IntentKey.TOPIC_ID, simplePostAdapter.getData().get(position).topic_id);
                startActivity(intent);
            }
        });

        simplePostAdapter.setOnItemChildClickListener((adapter, view1, position) -> {
            if (view1.getId() == R.id.item_simple_post_board_name) {
                Intent intent = new Intent(mActivity, SingleBoardActivity.class);
                intent.putExtra(Constant.IntentKey.BOARD_ID, simplePostAdapter.getData().get(position).board_id);
                startActivity(intent);
            }
            if (view1.getId() == R.id.item_simple_post_user_avatar) {
                Intent intent = new Intent(mActivity, UserDetailActivity.class);
                intent.putExtra(Constant.IntentKey.USER_ID, simplePostAdapter.getData().get(position).user_id);
                startActivity(intent);
            }
        });

        hotPostAdapter.setOnItemClickListener((adapter, view1, position) -> {
            if (view1.getId() == R.id.item_hot_post_cardview) {
                Intent intent = new Intent(mActivity, PostDetailActivity.class);
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
    protected void setOnRefreshListener() {
        RefreshUtil.setOnRefreshListener(mActivity, refreshLayout, new OnRefresh() {
            @Override
            public void onRefresh(RefreshLayout refreshLayout) {
                page = 1;
                if (type.equals(PostSortByType.TYPE_ALL) || type.equals(PostSortByType.TYPE_NEW) || type.equals(PostSortByType.TYPE_ESSENCE)) {
                    postListPresenter.getSimplePostList(page, SharePrefUtil.getPageSize(mActivity), type, mActivity);
                }

                if (type.equals(PostSortByType.TYPE_HOT)) {
                    postListPresenter.getHotPostList(page, SharePrefUtil.getPageSize(mActivity), mActivity);
                }
            }

            @Override
            public void onLoadMore(RefreshLayout refreshLayout) {
                if (type.equals(PostSortByType.TYPE_ALL) || type.equals(PostSortByType.TYPE_NEW) || type.equals(PostSortByType.TYPE_ESSENCE)) {
                    postListPresenter.getSimplePostList(page, SharePrefUtil.getPageSize(mActivity), type, mActivity);
                }

                if (type.equals(PostSortByType.TYPE_HOT)) {
                    postListPresenter.getHotPostList(page, SharePrefUtil.getPageSize(mActivity), mActivity);
                }
            }
        });
    }

    @Override
    public void onGetHotPostSuccess(HotPostBean hotPostBean) {
        page = page + 1;

        if (refreshLayout.getState() == RefreshState.Refreshing) {
            if (hotPostBean.has_next == 1) {
                refreshLayout.finishRefresh();
            } else {
                refreshLayout.finishRefreshWithNoMoreData();
            }
        }
        if (refreshLayout.getState() == RefreshState.Loading) {
            if (hotPostBean.has_next == 1) {
                refreshLayout.finishLoadMore(true);
            } else {
                refreshLayout.finishLoadMoreWithNoMoreData();
            }
        }

        if (hotPostBean.page == 1) {
            //保存为json文件
            if (type.equals(PostSortByType.TYPE_HOT)) {
                FileUtil.saveStringToFile(JSON.toJSONString(hotPostBean),
                        new File(mActivity.getExternalFilesDir(Constant.AppPath.JSON_PATH),
                                Constant.FileName.HOME1_HOT_POST_JSON));
            }
            recyclerView.scheduleLayoutAnimation();
            hotPostAdapter.addData(hotPostBean.list, true);
        } else {
            hotPostAdapter.addData(hotPostBean.list, false);
        }
    }

    @Override
    public void onGetHotPostError(String msg) {
        if (refreshLayout.getState() == RefreshState.Refreshing) {
            refreshLayout.finishRefresh();
        }
        if (refreshLayout.getState() == RefreshState.Loading) {
            refreshLayout.finishLoadMoreWithNoMoreData();
        }
        showSnackBar(mActivity.getWindow().getDecorView(), msg);
    }

    @Override
    public void onGetSimplePostSuccess(SimplePostListBean simplePostListBean) {

        page = page + 1;

        if (refreshLayout.getState() == RefreshState.Refreshing) {
            if (simplePostListBean.has_next == 1) {
                refreshLayout.finishRefresh();
            } else {
                refreshLayout.finishRefreshWithNoMoreData();
            }
        }
        if (refreshLayout.getState() == RefreshState.Loading) {
            if (simplePostListBean.has_next == 1) {
                refreshLayout.finishLoadMore(true);
            } else {
                refreshLayout.finishLoadMoreWithNoMoreData();
            }
        }

        if (simplePostListBean.page == 1) {
            //保存为json文件
            if (type.equals(PostSortByType.TYPE_ALL)) {
                FileUtil.saveStringToFile(JSON.toJSONString(simplePostListBean),
                        new File(mActivity.getExternalFilesDir(Constant.AppPath.JSON_PATH),
                                Constant.FileName.HOME1_ALL_POST_JSON));
            }
            if (type.equals(PostSortByType.TYPE_NEW)) {
                FileUtil.saveStringToFile(JSON.toJSONString(simplePostListBean),
                        new File(mActivity.getExternalFilesDir(Constant.AppPath.JSON_PATH),
                                Constant.FileName.HOME1_NEW_POST_JSON));
            }
            if (type.equals(PostSortByType.TYPE_ESSENCE)) {
                FileUtil.saveStringToFile(JSON.toJSONString(simplePostListBean),
                        new File(mActivity.getExternalFilesDir(Constant.AppPath.JSON_PATH),
                                Constant.FileName.HOME1_ESSENCE_POST_JSON));
            }
            recyclerView.scheduleLayoutAnimation();
            simplePostAdapter.addData(simplePostListBean.list, true);
        } else {
            simplePostAdapter.addData(simplePostListBean.list, false);
        }

    }

    @Override
    public void onGetSimplePostError(String msg) {
        if (refreshLayout.getState() == RefreshState.Refreshing) {
            refreshLayout.finishRefresh();
        }
        if (refreshLayout.getState() == RefreshState.Loading) {
            refreshLayout.finishLoadMoreWithNoMoreData();
        }
        showSnackBar(mActivity.getWindow().getDecorView(), msg);
    }

    @Override
    protected boolean registerEventBus() {
        return true;
    }

    @Override
    public void onEventBusReceived(BaseEvent baseEvent) {
        if (baseEvent.eventCode == BaseEvent.EventCode.HOME1_REFRESH) {
            if (((int)baseEvent.eventData == 0 && type.equals(PostSortByType.TYPE_NEW)) ||
                ((int)baseEvent.eventData == 1 && type.equals(PostSortByType.TYPE_ALL)) ||
                ((int)baseEvent.eventData == 2 && type.equals(PostSortByType.TYPE_HOT))) {
                recyclerView.scrollToPosition(0);
                refreshLayout.autoRefresh(0, 300, 1, false);
            }

        }
    }
}
