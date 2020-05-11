package com.scatl.uestcbbs.module.home.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.scatl.uestcbbs.R;
import com.scatl.uestcbbs.base.BaseFragment;
import com.scatl.uestcbbs.base.BasePresenter;
import com.scatl.uestcbbs.callback.OnRefresh;
import com.scatl.uestcbbs.custom.MyLinearLayoutManger;
import com.scatl.uestcbbs.entity.CollectionListBean;
import com.scatl.uestcbbs.module.collection.view.CollectionActivity;
import com.scatl.uestcbbs.module.home.adapter.CollectionListAdapter;
import com.scatl.uestcbbs.module.home.presenter.TaoTieCollectionPresenter;
import com.scatl.uestcbbs.module.post.view.PostDetailActivity;
import com.scatl.uestcbbs.module.user.view.UserDetailActivity;
import com.scatl.uestcbbs.util.Constant;
import com.scatl.uestcbbs.util.RefreshUtil;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.RefreshState;

import java.util.List;


public class CollectionListFragment extends BaseFragment implements CollectionListView {

    private SmartRefreshLayout refreshLayout;
    private RecyclerView recyclerView;
    private CollectionListAdapter collectionListAdapter;
    private ProgressBar progressBar;
    private TextView hint;

    private TaoTieCollectionPresenter taoTieCollectionPresenter;

    private int page = 1;

    public static CollectionListFragment getInstance(Bundle bundle) {
        CollectionListFragment collectionListFragment = new CollectionListFragment();
        collectionListFragment.setArguments(bundle);
        return collectionListFragment;
    }

    @Override
    protected int setLayoutResourceId() {
        return R.layout.fragment_collection_list;
    }

    @Override
    protected void findView() {
        refreshLayout = view.findViewById(R.id.tao_tie_collection_refresh);
        recyclerView = view.findViewById(R.id.tao_tie_collection_rv);
        progressBar = view.findViewById(R.id.tao_tie_progressbar);
        hint = view.findViewById(R.id.tao_tie_hint);
    }

    @Override
    protected void initView() {

        taoTieCollectionPresenter = (TaoTieCollectionPresenter) presenter;

        collectionListAdapter = new CollectionListAdapter(R.layout.item_collection_list);
        recyclerView.setLayoutManager(new MyLinearLayoutManger(mActivity));
        recyclerView.setLayoutAnimation(AnimationUtils.loadLayoutAnimation(mActivity, R.anim.layout_animation_scale_in));
        recyclerView.setAdapter(collectionListAdapter);
    }

    @Override
    protected void lazyLoad() {
        refreshLayout.autoRefresh(0, 300, 1, false);
    }

    @Override
    protected BasePresenter initPresenter() {
        return new TaoTieCollectionPresenter();
    }

    @Override
    protected void setOnItemClickListener() {
        collectionListAdapter.setOnItemChildClickListener((adapter, view1, position) -> {
            if (view1.getId() == R.id.item_tao_tie_collection_latest_post) {
                Intent intent = new Intent(mActivity, PostDetailActivity.class);
                intent.putExtra(Constant.IntentKey.TOPIC_ID, collectionListAdapter.getData().get(position).latestPostId);
                startActivity(intent);
            }

            if (view1.getId() == R.id.item_tao_tie_collection_user_avatar) {
                Intent intent = new Intent(mActivity, UserDetailActivity.class);
                intent.putExtra(Constant.IntentKey.USER_ID, collectionListAdapter.getData().get(position).authorId);
                startActivity(intent);
            }
        });

        collectionListAdapter.setOnItemClickListener((adapter, view1, position) -> {
            if (view1.getId() == R.id.item_tao_tie_collection_card_view) {
                Intent intent = new Intent(mActivity, CollectionActivity.class);
                intent.putExtra(Constant.IntentKey.COLLECTION_ID, collectionListAdapter.getData().get(position).collectionId);
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
                taoTieCollectionPresenter.getTaoTieCollection(page, "all");//todo
            }

            @Override
            public void onLoadMore(RefreshLayout refreshLayout) {
                taoTieCollectionPresenter.getTaoTieCollection(page,"all");//todo
            }
        });
    }

    @Override
    public void onGetCollectionListSuccess(List<CollectionListBean> collectionBeans, boolean hasNext) {
        progressBar.setVisibility(View.GONE);
        hint.setText("");

        if (page == 1) {
            collectionListAdapter.setNewData(collectionBeans);
            recyclerView.scheduleLayoutAnimation();
        }
        if (page != 1) collectionListAdapter.addData(collectionBeans);


        if (refreshLayout.getState() == RefreshState.Refreshing) {
            page = 2;
            if (hasNext) {
                refreshLayout.finishRefresh();
            } else {
                refreshLayout.finishRefreshWithNoMoreData();
            }
        }
        if (refreshLayout.getState() == RefreshState.Loading) {
            if (hasNext) {
                page = page + 1;
                refreshLayout.finishLoadMore(true);
            } else {
                refreshLayout.finishLoadMoreWithNoMoreData();
            }
        }
    }

    @Override
    public void onGetCollectionListError(String msg) {
        if (refreshLayout.getState() == RefreshState.Refreshing) {
            refreshLayout.finishRefresh();
        }
        if (refreshLayout.getState() == RefreshState.Loading) {
            refreshLayout.finishLoadMore(false);
        }
        hint.setText(msg);
    }
}
