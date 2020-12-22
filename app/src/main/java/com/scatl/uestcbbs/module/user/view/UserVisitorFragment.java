package com.scatl.uestcbbs.module.user.view;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.scatl.uestcbbs.R;
import com.scatl.uestcbbs.annotation.UserFriendType;
import com.scatl.uestcbbs.base.BaseBottomFragment;
import com.scatl.uestcbbs.base.BaseEvent;
import com.scatl.uestcbbs.base.BasePresenter;
import com.scatl.uestcbbs.custom.MyLinearLayoutManger;
import com.scatl.uestcbbs.entity.VisitorsBean;
import com.scatl.uestcbbs.module.user.adapter.UserFriendAdapter;
import com.scatl.uestcbbs.module.user.adapter.UserVisitorAdapter;
import com.scatl.uestcbbs.module.user.presenter.UserVisitorPresenter;
import com.scatl.uestcbbs.util.Constant;
import com.scatl.uestcbbs.util.SharePrefUtil;

import org.greenrobot.eventbus.EventBus;

import java.util.List;


public class UserVisitorFragment extends BaseBottomFragment implements UserVisitorView{

    TextView title, hint;
    RecyclerView recyclerView;
    UserVisitorAdapter userVisitorAdapter;
    UserVisitorPresenter userVisitorPresenter;

    int uid;
    String name;
    List<VisitorsBean> visitorsBeanList;

    public static UserVisitorFragment getInstance(Bundle bundle) {
        UserVisitorFragment userVisitorFragment = new UserVisitorFragment();
        userVisitorFragment.setArguments(bundle);
        return userVisitorFragment;
    }

    @Override
    protected void getBundle(Bundle bundle) {
        if (bundle != null) {
            uid = bundle.getInt(Constant.IntentKey.USER_ID);
            name = bundle.getString(Constant.IntentKey.USER_NAME, "");
            visitorsBeanList = (List<VisitorsBean>) bundle.getSerializable(Constant.IntentKey.DATA);
        }
    }

    @Override
    protected int setLayoutResourceId() {
        return R.layout.fragment_user_visitor;
    }

    @Override
    protected void findView() {
        title = view.findViewById(R.id.fragment_user_visitor_title);
        hint = view.findViewById(R.id.fragment_user_visitor_hint);
        recyclerView = view.findViewById(R.id.fragment_user_visitor_rv);
    }

    @Override
    protected void initView() {
        title.setText(uid == SharePrefUtil.getUid(mActivity) ? "我的访客" : name + "的访客");
        userVisitorPresenter = (UserVisitorPresenter) presenter;

        userVisitorAdapter = new UserVisitorAdapter(R.layout.item_user_visitor, SharePrefUtil.getUid(mActivity));
        recyclerView.setLayoutManager(new MyLinearLayoutManger(mActivity));
        recyclerView.setAdapter(userVisitorAdapter);
        recyclerView.setLayoutAnimation(AnimationUtils.loadLayoutAnimation(mActivity, R.anim.layout_animation_scale_in));
        if (visitorsBeanList != null && visitorsBeanList.size() != 0) {
            userVisitorAdapter.setNewData(visitorsBeanList);
        } else {
            hint.setText("没有数据，可能原因：没有高级授权、还没有访客");
        }
    }

    @Override
    protected BasePresenter initPresenter() {
        return new UserVisitorPresenter();
    }

    @Override
    protected void setOnItemClickListener() {
        userVisitorAdapter.setOnItemChildClickListener((adapter, view1, position) -> {
            if (view1.getId() == R.id.item_user_visitor_delete) {
                userVisitorPresenter.deleteVisitedHistory(uid, position);
            }
        });
    }

    @Override
    public void onDeleteVisitedHistorySuccess(String msg, int position) {
        showToast("删除成功，重新进入该用户空间会再次记录您的访问记录");
        visitorsBeanList.remove(position);
        userVisitorAdapter.notifyItemRemoved(position);
        EventBus.getDefault().post(new BaseEvent<>(BaseEvent.EventCode.DELETE_MINE_VISITOR_HISTORY_SUCCESS));
    }

    @Override
    public void onDeleteVisitedHistoryError(String msg) {
        showToast(msg);
    }
}