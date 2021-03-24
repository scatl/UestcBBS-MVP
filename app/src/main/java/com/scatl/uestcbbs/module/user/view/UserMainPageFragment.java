package com.scatl.uestcbbs.module.user.view;

import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.scatl.uestcbbs.R;
import com.scatl.uestcbbs.base.BaseEvent;
import com.scatl.uestcbbs.base.BaseFragment;
import com.scatl.uestcbbs.base.BasePresenter;
import com.scatl.uestcbbs.module.user.presenter.UserMainPagePresenter;
import com.scatl.uestcbbs.util.Constant;
import com.scatl.uestcbbs.util.TimeUtil;

import org.greenrobot.eventbus.EventBus;


public class UserMainPageFragment extends BaseFragment implements UserMainPageView{

    TextView isOnlineTv, onLineTimeTv, LastLoginTv, ageTv;
    View moreInfoLayout;

    UserMainPagePresenter userMainPagePresenter;

    int uid;

    public static UserMainPageFragment getInstance(Bundle bundle) {
        UserMainPageFragment userMainPageFragment = new UserMainPageFragment();
        userMainPageFragment.setArguments(bundle);
        return userMainPageFragment;
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
        return R.layout.fragment_user_main_page;
    }

    @Override
    protected void findView() {
        isOnlineTv = view.findViewById(R.id.user_main_page_online_status);
        onLineTimeTv = view.findViewById(R.id.user_main_page_online_time);
        LastLoginTv = view.findViewById(R.id.user_main_page_last_login_time);
        ageTv = view.findViewById(R.id.user_main_page_register_age);
        moreInfoLayout = view.findViewById(R.id.user_main_page_view_more_info);
    }

    @Override
    protected void initView() {
        userMainPagePresenter = (UserMainPagePresenter) presenter;

        moreInfoLayout.setOnClickListener(this);

        userMainPagePresenter.getUserSpace(uid, mActivity);
    }

    @Override
    protected BasePresenter initPresenter() {
        return new UserMainPagePresenter();
    }

    @Override
    protected void onClickListener(View v) {
        if (v.getId() == R.id.user_main_page_view_more_info) {
            EventBus.getDefault().post(new BaseEvent<>(BaseEvent.EventCode.VIEW_USER_MORE_INFO));
        }
    }

    @Override
    public void onGetUserSpaceSuccess(boolean isOnline, String onLineTime, String registerTime, String lastLoginTime, String ipLocation) {
        isOnlineTv.setText(isOnline ? "在线" : "离线");
        isOnlineTv.setTextColor(isOnline ? Color.parseColor("#00B4E7") : Color.parseColor("#BC6363"));
        onLineTimeTv.setText(onLineTime);

        LastLoginTv.setText(TimeUtil.formatTime(TimeUtil.getMilliSecond(lastLoginTime, "yyyy-MM-dd HH:mm") + "", R.string.post_time1, mActivity));
        ageTv.setText(TimeUtil.caclDays(registerTime, "yyyy-MM-dd HH:mm") + "天（" + registerTime + "注册）");
    }

    @Override
    public void onGetUserSpaceError(String msg) {

    }
}