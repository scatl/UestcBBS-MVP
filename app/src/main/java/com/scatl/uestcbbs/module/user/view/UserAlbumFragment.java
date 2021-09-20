package com.scatl.uestcbbs.module.user.view;

import android.content.Intent;
import android.os.Bundle;

import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.widget.TextView;

import com.scatl.uestcbbs.R;
import com.scatl.uestcbbs.base.BaseFragment;
import com.scatl.uestcbbs.base.BasePresenter;
import com.scatl.uestcbbs.callback.OnRefresh;
import com.scatl.uestcbbs.entity.AlbumListBean;
import com.scatl.uestcbbs.module.user.adapter.AlbumListAdapter;
import com.scatl.uestcbbs.module.user.presenter.UserAlbumPresenter;
import com.scatl.uestcbbs.util.CommonUtil;
import com.scatl.uestcbbs.util.Constant;
import com.scatl.uestcbbs.util.RefreshUtil;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.RefreshState;

public class UserAlbumFragment extends BaseFragment implements UserAlbumView{

    private SmartRefreshLayout refreshLayout;
    private RecyclerView recyclerView;
    private AlbumListAdapter albumListAdapter;
    private TextView hint;

    private UserAlbumPresenter userAlbumPresenter;
    private int uid;

    public static UserAlbumFragment getInstance(Bundle bundle) {
        UserAlbumFragment userAlbumFragment = new UserAlbumFragment();
        userAlbumFragment.setArguments(bundle);
        return userAlbumFragment;
    }

    @Override
    protected void getBundle(Bundle bundle) {
        super.getBundle(bundle);
        if (bundle != null) {
            uid = bundle.getInt(Constant.IntentKey.USER_ID, Integer.MAX_VALUE);
        }
    }

    @Override
    protected int setLayoutResourceId() {
        return R.layout.fragment_user_album;
    }

    @Override
    protected void findView() {
        recyclerView = view.findViewById(R.id.user_album_fragment_rv);
        refreshLayout = view.findViewById(R.id.user_album_fragment_refresh);
        hint = view.findViewById(R.id.user_album_fragment_hint);
    }

    @Override
    protected void initView() {
        userAlbumPresenter = (UserAlbumPresenter) presenter;


        StaggeredGridLayoutManager layoutManager =
                new StaggeredGridLayoutManager(CommonUtil.screenWidth(mActivity, true) / 100,
                        StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager((layoutManager));
        albumListAdapter = new AlbumListAdapter(R.layout.item_album_list);
        recyclerView.setAdapter(albumListAdapter);

        refreshLayout.setEnableLoadMore(false);
    }

    @Override
    protected BasePresenter initPresenter() {
        return new UserAlbumPresenter();
    }

    @Override
    protected void lazyLoad() {
        super.lazyLoad();
        refreshLayout.autoRefresh(0 ,300, 1, false);
    }

    @Override
    protected void setOnItemClickListener() {
        albumListAdapter.setOnItemClickListener((adapter, view1, position) -> {
            Intent intent = new Intent(mActivity, UserPhotoActivity.class);
            intent.putExtra(Constant.IntentKey.USER_ID, uid);
            intent.putExtra(Constant.IntentKey.ALBUM_ID, albumListAdapter.getData().get(position).album_id);
            intent.putExtra(Constant.IntentKey.ALBUM_NAME, albumListAdapter.getData().get(position).title);
            startActivity(intent);
        });
    }

    @Override
    protected void setOnRefreshListener() {
        RefreshUtil.setOnRefreshListener(mActivity, refreshLayout, new OnRefresh() {
            @Override
            public void onRefresh(RefreshLayout refreshLayout) {
                userAlbumPresenter.getUserAlbumList(uid, mActivity);
            }

            @Override
            public void onLoadMore(RefreshLayout refreshLayout) { }
        });
    }

    @Override
    public void onGetAlbumListSuccess(AlbumListBean albumListBean) {
        if (refreshLayout.getState() == RefreshState.Refreshing) {
            refreshLayout.finishRefreshWithNoMoreData();
        }

        if (albumListBean.list == null || albumListBean.list.size() == 0) {
            hint.setText("相册为空，可能是用户隐私设置原因");
        } else {
            albumListAdapter.setNewData(albumListBean.list);
        }

    }

    @Override
    public void onGetAlbumListError(String msg) {
        if (refreshLayout.getState() == RefreshState.Refreshing) {
            refreshLayout.finishRefresh();
        }
        hint.setText(msg);
    }
}
