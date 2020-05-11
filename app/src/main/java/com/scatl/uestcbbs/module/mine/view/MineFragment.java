package com.scatl.uestcbbs.module.mine.view;


import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;

import androidx.cardview.widget.CardView;

import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.scatl.uestcbbs.R;
import com.scatl.uestcbbs.base.BaseEvent;
import com.scatl.uestcbbs.base.BaseFragment;
import com.scatl.uestcbbs.base.BasePresenter;
import com.scatl.uestcbbs.module.history.view.HistoryActivity;
import com.scatl.uestcbbs.module.account.view.AccountManagerActivity;
import com.scatl.uestcbbs.module.mine.presenter.MinePresenter;
import com.scatl.uestcbbs.module.post.view.PostDraftActivity;
import com.scatl.uestcbbs.module.post.view.SelfPostActivity;
import com.scatl.uestcbbs.module.setting.view.SettingsActivity;
import com.scatl.uestcbbs.module.user.view.UserDetailActivity;
import com.scatl.uestcbbs.util.Constant;
import com.scatl.uestcbbs.util.SharePrefUtil;

import org.greenrobot.eventbus.EventBus;

public class MineFragment extends BaseFragment implements MineView {

    private CardView mineCardView1;
    private ImageView userIcon;
    private TextView userName;
    private RelativeLayout mineFavoriteRl, minePostRl, mineReplyRl, mineDraftRl,
            settingsRl, exitRl, accountMangerRl, historyRl, attachmentRl;
    private Switch nightModeSwitch;

    private MinePresenter minePresenter;

    public static MineFragment getInstance(Bundle bundle) {
        MineFragment mineFragment = new MineFragment();
        mineFragment.setArguments(bundle);
        return mineFragment;
    }

    @Override
    protected int setLayoutResourceId() {
        return R.layout.fragment_mine;
    }

    @Override
    protected void findView() {

        minePresenter = (MinePresenter) presenter;

        mineCardView1 = view.findViewById(R.id.mine_cardview1);
        userIcon = view.findViewById(R.id.mine_user_icon);
        userName = view.findViewById(R.id.mine_user_name);
        settingsRl = view.findViewById(R.id.mine_settings_rl);
        exitRl = view.findViewById(R.id.mine_exit_rl);
        mineFavoriteRl = view.findViewById(R.id.mine_favorite_rl);
        minePostRl = view.findViewById(R.id.mine_post_rl);
        mineReplyRl = view.findViewById(R.id.mine_reply_rl);
        mineDraftRl = view.findViewById(R.id.mine_draft_rl);
        accountMangerRl = view.findViewById(R.id.mine_account_manager_rl);
        historyRl = view.findViewById(R.id.mine_history_rl);
        attachmentRl = view.findViewById(R.id.mine_file_manage_rl);
        nightModeSwitch = view.findViewById(R.id.mine_night_mode_switch);

    }

    @Override
    protected void initView() {
        mineCardView1.setOnClickListener(this);
        settingsRl.setOnClickListener(this);
        exitRl.setOnClickListener(this);
        minePostRl.setOnClickListener(this);
        mineReplyRl.setOnClickListener(this);
        mineDraftRl.setOnClickListener(this);
        mineFavoriteRl.setOnClickListener(this);
        accountMangerRl.setOnClickListener(this);
        historyRl.setOnClickListener(this::onClickListener);
        attachmentRl.setOnClickListener(this::onClickListener);

        initUserInfo();
        initNightMode();
    }

    @Override
    protected BasePresenter initPresenter() {
        return new MinePresenter();
    }

    /**
     * author: sca_tl
     * description: 初始化用户信息
     */
    private void initUserInfo() {
        if (SharePrefUtil.isLogin(mActivity)) {
            String icon = SharePrefUtil.getAvatar(mActivity);
            String name = SharePrefUtil.getName(mActivity);
            userName.setText(name);
            Glide.with(mActivity).load(icon).into(userIcon);
            exitRl.setVisibility(View.VISIBLE);
        } else {
            userName.setText("请登录");
            Glide.with(mActivity).load(R.drawable.ic_default_avatar).into(userIcon);
            exitRl.setVisibility(View.GONE);
        }
    }

    /**
     * author: sca_tl
     * description: 初始化夜间模式
     */
    private void initNightMode() {
//        if (SharePrefUtil.getUiModeFollowSystem(mActivity)) {
//            nightModeSwitch.setEnabled(false);
//        } else {
//            nightModeSwitch.setEnabled(true);
//            nightModeSwitch.setChecked(SharePrefUtil.isNightMode(mActivity));
//        }
        nightModeSwitch.setChecked(SharePrefUtil.isNightMode(mActivity));
        nightModeSwitch.setOnCheckedChangeListener((compoundButton, b) -> {
            if (! compoundButton.isPressed()) return;
            int mode = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
            if (mode == Configuration.UI_MODE_NIGHT_YES) {
                SharePrefUtil.setNightMode(mActivity, false);
                EventBus.getDefault().post(new BaseEvent<>(BaseEvent.EventCode.NIGHT_MODE_NO));
            } else {
                SharePrefUtil.setNightMode(mActivity, true);
                EventBus.getDefault().post(new BaseEvent<>(BaseEvent.EventCode.NIGHT_MODE_YES));
            }
        });

//        nightModeSwitch.setOnClickListener(v -> {
//            if (SharePrefUtil.getUiModeFollowSystem(mActivity)) {
//                showToast("您已设置跟随系统");
//            }
//        });

    }

    @Override
    protected void onClickListener(View v) {
        if (v.getId() == R.id.mine_cardview1) {
            if (SharePrefUtil.isLogin(mActivity)) {
                Intent intent = new Intent(mActivity, UserDetailActivity.class);
                intent.putExtra(Constant.IntentKey.USER_ID, SharePrefUtil.getUid(mActivity));
                startActivity(intent);
            } else {
                startActivity(new Intent(mActivity, AccountManagerActivity.class));
            }
        }

        if (v.getId() == R.id.mine_favorite_rl) {
            Intent intent = new Intent(mActivity, SelfPostActivity.class);
            intent.putExtra(Constant.IntentKey.TYPE, SelfPostActivity.TYPE_USER_FAVORITE);
            startActivity(intent);
        }

        if (v.getId() == R.id.mine_post_rl) {
            Intent intent = new Intent(mActivity, SelfPostActivity.class);
            intent.putExtra(Constant.IntentKey.TYPE, SelfPostActivity.TYPE_USER_POST);
            startActivity(intent);
        }

        if (v.getId() == R.id.mine_reply_rl) {
            Intent intent = new Intent(mActivity, SelfPostActivity.class);
            intent.putExtra(Constant.IntentKey.TYPE, SelfPostActivity.TYPE_USER_REPLY);
            startActivity(intent);
        }

        if (v.getId() == R.id.mine_draft_rl) {
            Intent intent = new Intent(mActivity, PostDraftActivity.class);
            startActivity(intent);
        }

        if (v.getId() == R.id.mine_settings_rl) {
            Intent intent3 = new Intent(mActivity, SettingsActivity.class);
            startActivity(intent3);
        }

        if (v.getId() == R.id.mine_exit_rl) {
            minePresenter.logout(mActivity);
        }

        if (v.getId() == R.id.mine_account_manager_rl) {
            startActivity(new Intent(mActivity, AccountManagerActivity.class));
        }

        if (v.getId() == R.id.mine_history_rl) {
            startActivity(new Intent(mActivity, HistoryActivity.class));
        }
        if (v.getId() == R.id.mine_file_manage_rl) {
        }
    }

    @Override
    public void onLoginOutSuccess() {
        initUserInfo();
    }

    @Override
    protected boolean registerEventBus() {
        return true;
    }

    @Override
    protected void receiveEventBusMsg(BaseEvent baseEvent) {
        if (baseEvent.eventCode == BaseEvent.EventCode.LOGIN_SUCCESS ||
            baseEvent.eventCode == BaseEvent.EventCode.LOGOUT_SUCCESS)
            initUserInfo();
        if (baseEvent.eventCode == BaseEvent.EventCode.UI_MODE_FOLLOW_SYSTEM) {

        }
    }
}
