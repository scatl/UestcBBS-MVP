package com.scatl.uestcbbs.module.user.view;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.material.appbar.AppBarLayout;
import com.jaeger.library.StatusBarUtil;
import com.scatl.uestcbbs.R;
import com.scatl.uestcbbs.annotation.ToastType;
import com.scatl.uestcbbs.annotation.UserFriendType;
import com.scatl.uestcbbs.base.BaseActivity;
import com.scatl.uestcbbs.base.BaseEvent;
import com.scatl.uestcbbs.base.BaseIndicatorAdapter;
import com.scatl.uestcbbs.base.BasePresenter;
import com.scatl.uestcbbs.custom.MyLinearLayoutManger;
import com.scatl.uestcbbs.entity.BlackListBean;
import com.scatl.uestcbbs.entity.BlackUserBean;
import com.scatl.uestcbbs.entity.FollowUserBean;
import com.scatl.uestcbbs.entity.ModifyPswBean;
import com.scatl.uestcbbs.entity.ModifySignBean;
import com.scatl.uestcbbs.entity.SearchUserBean;
import com.scatl.uestcbbs.entity.UserDetailBean;
import com.scatl.uestcbbs.entity.UserFriendBean;
import com.scatl.uestcbbs.entity.VisitorsBean;
import com.scatl.uestcbbs.module.credit.view.CreditTransferFragment;
import com.scatl.uestcbbs.module.message.view.PrivateChatActivity;
import com.scatl.uestcbbs.module.user.adapter.UserPostViewPagerAdapter;
import com.scatl.uestcbbs.module.user.adapter.UserSpaceMedalAdapter;
import com.scatl.uestcbbs.module.user.presenter.UserDetailPresenter;
import com.scatl.uestcbbs.util.Constant;
import com.scatl.uestcbbs.util.ForumUtil;
import com.scatl.uestcbbs.util.ImageUtil;
import com.scatl.uestcbbs.util.SharePrefUtil;
import com.scatl.uestcbbs.util.TimeUtil;

import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.ViewPagerHelper;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;

import org.greenrobot.eventbus.EventBus;
import org.litepal.LitePal;

import java.io.Serializable;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UserDetailActivity extends BaseActivity<UserDetailPresenter> implements UserDetailView, AppBarLayout.OnOffsetChangedListener{

    private RelativeLayout userInfoRl;
    private CoordinatorLayout coordinatorLayout;
    private AppBarLayout appBarLayout;
    private Toolbar toolbar;
    private LottieAnimationView loading;
    private ImageView background, chatBtn, blackBtn;
    private ImageView avatar;
    private TextView userName, userSign, userFollowed, userFollow, friendNum, visitorNum, userLevel, userGender, hint;
    private TextView shuidiNum, jifenNum, favoriteBtn, favoriteToolbarBtn;
    private LinearLayout shuidiLayout, jifenLayout;
    private Button blackedBtn;
    private MagicIndicator indicator;
    private ViewPager viewPager;
    private RecyclerView userMedalRv;
    private View actionLayout;
    private UserSpaceMedalAdapter userSpaceMedalAdapter;

    private UserDetailBean userDetailBean;
    private List<VisitorsBean> visitorsBeans;

    private int userId;

    @Override
    protected void getIntent(Intent intent) {
        super.getIntent(intent);
        userId = intent.getIntExtra(Constant.IntentKey.USER_ID, Integer.MAX_VALUE);
    }

    @Override
    protected int setLayoutResourceId() {
        return R.layout.activity_user_detail;
    }

    @Override
    protected void findView() {
        appBarLayout = findViewById(R.id.user_detail_app_bar);
        actionLayout = findViewById(R.id.user_detail_action_layout);
        toolbar = findViewById(R.id.user_detail_toolbar);
        coordinatorLayout = findViewById(R.id.user_detail_coor_layout);
        userInfoRl = findViewById(R.id.user_detail_info_rl);
        background = findViewById(R.id.user_detail_user_background);
        avatar = findViewById(R.id.user_detail_user_icon);
        userName = findViewById(R.id.user_detail_user_name);
        userSign = findViewById(R.id.user_detail_user_sign);
        userFollowed = findViewById(R.id.user_detail_followed_num);
        userFollow = findViewById(R.id.user_detail_follow_num);
        visitorNum = findViewById(R.id.user_detail_visitor_num);
        userLevel = findViewById(R.id.user_detail_user_level);
        userGender = findViewById(R.id.user_detail_user_gender);
        favoriteBtn = findViewById(R.id.user_detail_favorite_btn);
        chatBtn = findViewById(R.id.user_detail_chat_btn);
        blackBtn = findViewById(R.id.user_detail_black_btn);
        hint = findViewById(R.id.user_detail_hint);
        shuidiNum = findViewById(R.id.user_detail_shuidi_num);
        jifenNum = findViewById(R.id.user_detail_jifen_num);
        shuidiLayout = findViewById(R.id.user_detail_shuidi_layout);
        jifenLayout = findViewById(R.id.user_detail_jifen_layout);
        indicator = findViewById(R.id.user_detail_indicator);
        viewPager = findViewById(R.id.user_detail_viewpager);
        loading = findViewById(R.id.user_detail_loading);
        userMedalRv = findViewById(R.id.user_detail_user_medal_rv);
        friendNum = findViewById(R.id.user_detail_friend_num);
        blackedBtn = findViewById(R.id.user_detail_blacked_btn);
        favoriteToolbarBtn = findViewById(R.id.user_detail_favorite_toolbar_btn);
    }

    @Override
    protected void initView() {
        super.initView();

        favoriteBtn.setOnClickListener(this);
        favoriteToolbarBtn.setOnClickListener(this::onClickListener);
        chatBtn.setOnClickListener(this);
        blackBtn.setOnClickListener(this);
        shuidiLayout.setOnClickListener(this);
        jifenLayout.setOnClickListener(this);
        userLevel.setOnClickListener(this);
        userGender.setOnClickListener(this);
        userFollowed.setOnClickListener(this);
        visitorNum.setOnClickListener(this::onClickListener);
        friendNum.setOnClickListener(this::onClickListener);
        userFollow.setOnClickListener(this);
        appBarLayout.addOnOffsetChangedListener(this);
        avatar.setOnClickListener(this::onClickListener);
        userSign.setOnClickListener(this);
        blackedBtn.setOnClickListener(this::onClickListener);

        if (userId == SharePrefUtil.getUid(this)) {
            actionLayout.setVisibility(View.GONE);
        }

        viewPager.setOffscreenPageLimit(4);
        viewPager.setAdapter(new UserPostViewPagerAdapter(getSupportFragmentManager(),
                FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT, userId));
        viewPager.setCurrentItem(0);

        presenter.getUserDetail(userId, this);
        presenter.getUserSpace(userId, this);
        presenter.getUserFriend(userId, UserFriendType.TYPE_FRIEND, this);
    }

    @Override
    protected Toolbar getToolbar() {
        return toolbar;
    }

    @Override
    protected UserDetailPresenter initPresenter() {
        return new UserDetailPresenter();
    }

    @Override
    protected void onClickListener(View view) {
        if (view.getId() == R.id.user_detail_favorite_btn || view.getId() == R.id.user_detail_favorite_toolbar_btn) {
            presenter.followUser(userId, userDetailBean.is_follow == 1 ? "unfollow" : "follow", this);
        }
        if (view.getId() == R.id.user_detail_chat_btn) {
            Intent intent = new Intent(this, PrivateChatActivity.class);
            intent.putExtra(Constant.IntentKey.USER_ID, userId);
            intent.putExtra(Constant.IntentKey.USER_NAME, userDetailBean.name);
            startActivity(intent);
        }
        if (view.getId() == R.id.user_detail_black_btn || view.getId() == R.id.user_detail_blacked_btn) {
            if (userDetailBean.is_black == 0) {
                presenter.showBlackConfirmDialog(this, userId);
            } else {
                presenter.blackUser(userId, "delblack", this);
            }
        }
        if (view.getId() == R.id.user_detail_shuidi_layout || view.getId() == R.id.user_detail_jifen_layout) {
            presenter.showUserInfo(userDetailBean, true, this);
        }
        if (view.getId() == R.id.user_detail_user_gender || view.getId() == R.id.user_detail_user_level) {
            presenter.showUserInfo(userDetailBean, false, this);
        }
        if (view.getId() == R.id.user_detail_user_sign) {
            if (userId == SharePrefUtil.getUid(this)) {
                presenter.showModifySignDialog(userDetailBean.sign, this);
            } else {
                presenter.showUserSignDialog(userDetailBean.sign, this);
            }
        }
        if (view.getId() == R.id.user_detail_followed_num) {
            Bundle bundle = new Bundle();
            bundle.putInt(Constant.IntentKey.USER_ID, userId);
            bundle.putString(Constant.IntentKey.USER_NAME, userDetailBean.name);
            bundle.putString(Constant.IntentKey.TYPE, UserFriendType.TYPE_FOLLOWED);
            UserFriendFragment.getInstance(bundle)
                    .show(getSupportFragmentManager(), TimeUtil.getStringMs());
        }
        if (view.getId() == R.id.user_detail_follow_num) {
            Bundle bundle = new Bundle();
            bundle.putInt(Constant.IntentKey.USER_ID, userId);
            bundle.putString(Constant.IntentKey.USER_NAME, userDetailBean.name);
            bundle.putString(Constant.IntentKey.TYPE, UserFriendType.TYPE_FOLLOW);
            UserFriendFragment.getInstance(bundle)
                    .show(getSupportFragmentManager(), TimeUtil.getStringMs());
        }
        if (view.getId() == R.id.user_detail_visitor_num) {
            Bundle bundle = new Bundle();
            bundle.putString(Constant.IntentKey.USER_NAME, userDetailBean.name);
            bundle.putInt(Constant.IntentKey.USER_ID, userId);
            bundle.putSerializable(Constant.IntentKey.DATA_1, (Serializable) visitorsBeans);
            UserVisitorFragment.getInstance(bundle).show(getSupportFragmentManager(), TimeUtil.getStringMs());
        }
        if (view.getId() == R.id.user_detail_friend_num) {
            Bundle bundle = new Bundle();
            bundle.putInt(Constant.IntentKey.USER_ID, userId);
            bundle.putString(Constant.IntentKey.USER_NAME, userDetailBean.name);
            bundle.putString(Constant.IntentKey.TYPE, UserFriendType.TYPE_FRIEND);
            UserFriendFragment.getInstance(bundle)
                    .show(getSupportFragmentManager(), TimeUtil.getStringMs());
        }
        if (view.getId() == R.id.user_detail_user_icon) {
            if (userId == SharePrefUtil.getUid(this)) {
                startActivity(new Intent(this, ModifyAvatarActivity.class));
            }
        }
    }


    @Override
    public void onGetUserDetailSuccess(UserDetailBean userDetailBean) {

        this.userDetailBean = userDetailBean;

        presenter.searchUser(userDetailBean.name, this);

        final String[] titles = {"主页", "发表(" + userDetailBean.topic_num + ")", "回复(" + userDetailBean.reply_posts_num + ")", "收藏", "相册"};

        CommonNavigator commonNavigator = new CommonNavigator(this);
        commonNavigator.setAdapter(new BaseIndicatorAdapter(titles, 16, viewPager));
        indicator.setNavigator(commonNavigator);
        ViewPagerHelper.bind(indicator, viewPager);

        AlphaAnimation alphaAnimation = new AlphaAnimation(0, 1);
        alphaAnimation.setDuration(600);
        coordinatorLayout.startAnimation(alphaAnimation);
        coordinatorLayout.setVisibility(View.VISIBLE);
        loading.setVisibility(View.GONE);

        userName.setText(userDetailBean.name);
        userFollow.setText(String.valueOf("关注：" + userDetailBean.friend_num));
        userFollowed.setText(String.valueOf("粉丝：" + userDetailBean.follow_num));
        toolbar.setTitle(userDetailBean.name);

        if (userId != SharePrefUtil.getUid(this)) setBlackStatus();


        if (userDetailBean.body.creditList.size() >= 3) {
            shuidiNum.setText(String.valueOf(userDetailBean.body.creditList.get(2).data));
            jifenNum.setText(String.valueOf(userDetailBean.body.creditList.get(0).data));
        }

        if (userDetailBean.gender == 0) {  //保密
            userGender.setText("保密");
            userGender.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#83C6C2")));
            userSign.setText(TextUtils.isEmpty(userDetailBean.sign) ? "Ta还未设置签名" : userDetailBean.sign);

        } else if (userDetailBean.gender == 1) {  //男
            userGender.setText("♂");
            userGender.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#5A93D6")));
            userSign.setText(TextUtils.isEmpty(userDetailBean.sign) ? "他还未设置签名" : userDetailBean.sign);

        } else if (userDetailBean.gender == 2) { //女
            userGender.setText("♀");
            userGender.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#9D54AA")));
            userSign.setText(TextUtils.isEmpty(userDetailBean.sign) ? "她还未设置签名" : userDetailBean.sign);

        }

        if (!TextUtils.isEmpty(userDetailBean.userTitle)) {
            Matcher matcher = Pattern.compile("(.*?)\\((Lv\\..*)\\)").matcher(userDetailBean.userTitle);
            userLevel.setText(matcher.find() ? matcher.group(2) : userDetailBean.userTitle);
            userLevel.setBackgroundTintList(ColorStateList.valueOf(ForumUtil.getLevelColor(userDetailBean.userTitle)));
        }

        Glide.with(this).load(userDetailBean.icon).into(avatar);
        Glide.with(this).load(userDetailBean.icon).into(new SimpleTarget<Drawable>() {
            @Override
            public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                background.setImageBitmap(ImageUtil.blurPhoto(UserDetailActivity.this,
                        resource instanceof GifDrawable ?  ((GifDrawable) resource).getFirstFrame() : ImageUtil.drawable2Bitmap(resource), 5));
            }
        });

    }

    @Override
    public void onGetUserDetailError(String msg) {
        hint.setText(msg);
        loading.setVisibility(View.GONE);
    }

    private void setBlackStatus() {
        if (userDetailBean.is_black == 1) {
            blackedBtn.setVisibility(View.VISIBLE);
            favoriteBtn.setVisibility(View.GONE);
            favoriteToolbarBtn.setVisibility(View.GONE);
            chatBtn.setVisibility(View.GONE);
            blackBtn.setVisibility(View.GONE);

            blackedBtn.setText("解除黑名单");
            blackedBtn.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FF3C3C")));
        } else {
            blackedBtn.setVisibility(View.GONE);
            favoriteBtn.setVisibility(View.VISIBLE);
            favoriteToolbarBtn.setVisibility(View.VISIBLE);
            chatBtn.setVisibility(View.VISIBLE);
            blackBtn.setVisibility(View.VISIBLE);

            favoriteBtn.setText(userDetailBean.is_follow == 1 ? "已关注" : "+ 关注");
            favoriteToolbarBtn.setText(userDetailBean.is_follow == 1 ? "已关注" : "+ 关注");
            blackBtn.setImageTintList(ColorStateList.valueOf(Color.parseColor(userDetailBean.is_black == 1 ? "#FF3C3C" : "#bbbbbb")));
        }
    }

    @Override
    public void onFollowUserSuccess(FollowUserBean followUserBean) {
        showToast(followUserBean.head.errInfo, ToastType.TYPE_SUCCESS);
        userDetailBean.is_follow = userDetailBean.is_follow == 1 ? 0 : 1;
        favoriteBtn.setText(userDetailBean.is_follow == 1 ? "已关注" : "+ 关注");
        favoriteToolbarBtn.setText(userDetailBean.is_follow == 1 ? "已关注" : "+ 关注");
    }

    @Override
    public void onFollowUserError(String msg) {
        showToast(msg, ToastType.TYPE_ERROR);
    }

    @Override
    public void onBlackUserSuccess(BlackUserBean blackUserBean) {
        showToast(blackUserBean.head.errInfo, ToastType.TYPE_SUCCESS);
        //blackBtn.setImageResource(userDetailBean.is_black == 0 ? R.drawable.ic_black_list : R.drawable.ic_white_list);
        blackBtn.setImageTintList(ColorStateList.valueOf(Color.parseColor(userDetailBean.is_black == 0 ? "#FF3C3C" : "#bbbbbb")));
        userDetailBean.is_black = userDetailBean.is_black == 1 ? 0 : 1;

        //将该用户数据从本地删除或写入
        if (userDetailBean.is_black == 1) {//拉黑
            BlackListBean blackListBean = new BlackListBean();
            blackListBean.uid = userId;
            blackListBean.userName = userDetailBean.name;
            blackListBean.avatar = userDetailBean.icon;
            blackListBean.blackTime = TimeUtil.getLongMs();
            blackListBean.save();
        } else {//取消拉黑
            LitePal.deleteAll(BlackListBean.class, "uid = " + userId);
        }

        setBlackStatus();
        EventBus.getDefault().post(new BaseEvent<>(BaseEvent.EventCode.BLACK_LIST_CHANGE));

    }

    @Override
    public void onBlackUserError(String msg) {
        showToast(msg, ToastType.TYPE_ERROR);
    }

    @Override
    public void onModifySignSuccess(ModifySignBean modifySignBean, String sign) {
        userSign.setText(sign);
        userDetailBean.sign = sign;
        showToast(modifySignBean.head.errInfo, ToastType.TYPE_SUCCESS);
    }

    @Override
    public void onModifySignError(String msg) {
        showToast(msg, ToastType.TYPE_ERROR);
    }

    @Override
    public void onModifyPswSuccess(ModifyPswBean modifyPswBean) {
        showToast(modifyPswBean.head.errInfo, ToastType.TYPE_SUCCESS);
    }

    @Override
    public void onModifyPswError(String msg) {
        showToast(msg, ToastType.TYPE_ERROR);
    }

    @Override
    public void onGetUserSpaceSuccess(List<VisitorsBean> visitorsBeans, List<String> medalImages) {
        this.visitorsBeans = visitorsBeans;

        visitorNum.setText("访客：" + visitorsBeans.size());

        if (medalImages == null || medalImages.size() == 0) {
            userMedalRv.setVisibility(View.GONE);
        } else {
            userMedalRv.setVisibility(View.VISIBLE);
            userSpaceMedalAdapter = new UserSpaceMedalAdapter(R.layout.item_user_space_medal);
            MyLinearLayoutManger myLinearLayoutManger = new MyLinearLayoutManger(this);
            myLinearLayoutManger.setOrientation(LinearLayoutManager.HORIZONTAL);
            userMedalRv.setLayoutManager(myLinearLayoutManger);
            userMedalRv.setAdapter(userSpaceMedalAdapter);
            userSpaceMedalAdapter.setNewData(medalImages);
        }

    }

    @Override
    public void onGetUserSpaceError(String msg) {

    }

    @Override
    public void onGetUserFriendSuccess(UserFriendBean userFriendBean) {
        if (userFriendBean != null && userFriendBean.list != null) {
            friendNum.setText("好友：" + userFriendBean.list.size());
        }
    }

    @Override
    public void onGetUserFriendError(String msg) {

    }

    @Override
    public void onSearchUserSuccess(SearchUserBean searchUserBean, String searchName) {
        if (searchUserBean.body != null && searchUserBean.body.list != null) {
            for (int i = 0; i < searchUserBean.body.list.size(); i ++) {
                if (searchUserBean.body.list.get(i).name.equals(searchName)) {
                    String lastLoginTime = searchUserBean.body.list.get(i).dateline;
                    //lastLoginTv.setText(TimeUtil.formatTime(lastLoginTime, R.string.last_login_time, this));
                    break;
                }
            }
        }
    }

    @Override
    public void onSearchUserError(String msg) {

    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int i) {
        int scrollRange = appBarLayout.getTotalScrollRange();
        float alpha = 1 - (1.0f * (- i)) / scrollRange;
        userInfoRl.setAlpha(alpha);
        toolbar.setAlpha(1-alpha);
        favoriteToolbarBtn.setVisibility(alpha == 0 ? SharePrefUtil.getUid(this) == userId ? View.GONE : View.VISIBLE : View.GONE);
    }

    @Override
    protected int setMenuResourceId() {
        return R.menu.menu_user_detail;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        menu.findItem(R.id.menu_user_detail_modify_profile).setVisible(userId == SharePrefUtil.getUid(this));
        menu.findItem(R.id.menu_user_detail_report).setVisible(userId != SharePrefUtil.getUid(this));
        menu.findItem(R.id.menu_user_detail_transfer_credit).setVisible(userId != SharePrefUtil.getUid(this));

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    protected void onOptionsSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_user_detail_modify_profile) {
            presenter.showModifyInfoDialog(this);
        }

        if (userDetailBean != null) {
            if (item.getItemId() == R.id.menu_user_detail_transfer_credit) {
                Bundle bundle = new Bundle();
                bundle.putString(Constant.IntentKey.USER_NAME, userDetailBean.name);
                CreditTransferFragment.getInstance(bundle).show(getSupportFragmentManager(), TimeUtil.getStringMs());
            }
        }

    }

    @Override
    protected void setStatusBar() {
        super.setStatusBar();
        StatusBarUtil.setTransparent(this);
    }

    @Override
    protected boolean registerEventBus() {
        return true;
    }

    @Override
    protected void receiveEventBusMsg(BaseEvent baseEvent) {
        if (baseEvent.eventCode == BaseEvent.EventCode.DELETE_MINE_VISITOR_HISTORY_SUCCESS) {
            for (int i = 0; i < visitorsBeans.size(); i ++) {
                if (visitorsBeans.get(i).visitorUid == SharePrefUtil.getUid(this)) {
                    visitorsBeans.remove(i);
                    break;
                }
            }
        }
        if (baseEvent.eventCode == BaseEvent.EventCode.VIEW_USER_MORE_INFO) {
            presenter.showUserInfo(userDetailBean, false, this);
        }
    }
}
