package com.scatl.uestcbbs.module.user.view;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.scatl.uestcbbs.R;
import com.scatl.uestcbbs.annotation.UserFriendType;
import com.scatl.uestcbbs.base.BaseBottomFragment;
import com.scatl.uestcbbs.base.BasePresenter;
import com.scatl.uestcbbs.widget.MyLinearLayoutManger;
import com.scatl.uestcbbs.entity.UserFriendBean;
import com.scatl.uestcbbs.module.user.adapter.UserFriendAdapter;
import com.scatl.uestcbbs.module.user.presenter.UserFriendPresenter;
import com.scatl.uestcbbs.util.Constant;
import com.scatl.uestcbbs.util.SharePrefUtil;
import com.scatl.widget.bottomsheet.ViewPagerBottomSheetBehavior;


public class UserFriendFragment extends BaseBottomFragment implements UserFriendView{

    private RecyclerView recyclerView;
    private UserFriendAdapter userFriendAdapter;
    private TextView title, hint;
    private ProgressBar progressBar;

    private UserFriendPresenter userFriendPresenter;

    private int uid;
    private String type, name;

    public static UserFriendFragment getInstance(Bundle bundle) {
        UserFriendFragment userFriendFragment = new UserFriendFragment();
        userFriendFragment.setArguments(bundle);
        return userFriendFragment;
    }

    @Override
    protected void getBundle(Bundle bundle) {
        super.getBundle(bundle);
        if (bundle != null) {
            uid = bundle.getInt(Constant.IntentKey.USER_ID, Integer.MAX_VALUE);
            name = bundle.getString(Constant.IntentKey.USER_NAME);
            type = bundle.getString(Constant.IntentKey.TYPE);
        }
    }

    @Override
    protected int setLayoutResourceId() {
        return R.layout.fragment_bottom_user_friend;
    }

    @Override
    protected void findView() {
        recyclerView = view.findViewById(R.id.fragment_bottom_user_friend_rv);
        title = view.findViewById(R.id.fragment_bottom_user_friend_title);
        hint = view.findViewById(R.id.fragment_bottom_user_friend_hint);
        progressBar = view.findViewById(R.id.fragment_bottom_user_friend_progressbar);
    }

    @Override
    protected void initView() {
        userFriendPresenter = (UserFriendPresenter) presenter;
        mBehavior.setState(ViewPagerBottomSheetBehavior.STATE_COLLAPSED);

        if (uid == SharePrefUtil.getUid(mActivity)) {
            if (UserFriendType.TYPE_FOLLOW.equals(type)) {
                title.setText("我关注的");
            } else if (UserFriendType.TYPE_FOLLOWED.equals(type)){
                title.setText("我的粉丝");
            } else if (UserFriendType.TYPE_FRIEND.equals(type)) {
                title.setText("我的好友");
            }
        } else {
            if (UserFriendType.TYPE_FOLLOW.equals(type)) {
                title.setText(name + "关注的");
            } else if (UserFriendType.TYPE_FOLLOWED.equals(type)){
                title.setText(name + "的粉丝");
            } else if (UserFriendType.TYPE_FRIEND.equals(type)) {
                title.setText(name + "的好友");
            }
        }
//        title.setText(uid == SharePrefUtil.getUid(mActivity) ? UserFriendType.TYPE_FOLLOW.equals(type) ? "我关注的" : "我的粉丝"
//                : UserFriendType.TYPE_FOLLOW.equals(type) ? name + "关注的" : name + "的粉丝");

        userFriendAdapter = new UserFriendAdapter(R.layout.item_user_friend);
        recyclerView.setLayoutManager(new MyLinearLayoutManger(mActivity));
        recyclerView.setAdapter(userFriendAdapter);
        recyclerView.setLayoutAnimation(AnimationUtils.loadLayoutAnimation(mActivity, R.anim.layout_animation_scale_in));

        userFriendPresenter.getUserFriend(uid, type, mActivity);
    }

    @Override
    protected BasePresenter initPresenter() {
        return new UserFriendPresenter();
    }

    @Override
    protected void setOnItemClickListener() {
        userFriendAdapter.setOnItemClickListener((adapter, view1, position) -> {
            if (view1.getId() == R.id.item_user_friend_root_layout) {
                Intent intent = new Intent(mActivity, UserDetailActivity.class);
                intent.putExtra(Constant.IntentKey.USER_ID, userFriendAdapter.getData().get(position).uid);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onGetUserFriendSuccess(UserFriendBean userFriendBean) {
        progressBar.setVisibility(View.GONE);
        userFriendAdapter.setNewData(userFriendBean.list);
        hint.setText(userFriendAdapter.getData().size() == 0 ? "啊哦，还没有数据" : "");
    }

    @Override
    public void onGetUserFriendError(String msg) {
        progressBar.setVisibility(View.GONE);
        hint.setText(msg);
    }

    @Override
    protected double setMaxHeightMultiplier() {
        return 0.92;
    }
}
