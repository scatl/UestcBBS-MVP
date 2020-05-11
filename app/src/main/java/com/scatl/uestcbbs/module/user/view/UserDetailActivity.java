package com.scatl.uestcbbs.module.user.view;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.material.appbar.AppBarLayout;
import com.jaeger.library.StatusBarUtil;
import com.scatl.uestcbbs.R;
import com.scatl.uestcbbs.base.BaseActivity;
import com.scatl.uestcbbs.base.BaseIndicatorAdapter;
import com.scatl.uestcbbs.base.BasePresenter;
import com.scatl.uestcbbs.custom.imageview.CircleImageView;
import com.scatl.uestcbbs.entity.BlackUserBean;
import com.scatl.uestcbbs.entity.FollowUserBean;
import com.scatl.uestcbbs.entity.ModifyPswBean;
import com.scatl.uestcbbs.entity.ModifySignBean;
import com.scatl.uestcbbs.entity.UserDetailBean;
import com.scatl.uestcbbs.module.message.view.PrivateChatActivity;
import com.scatl.uestcbbs.module.user.adapter.UserPostViewPagerAdapter;
import com.scatl.uestcbbs.module.user.presenter.UserDetailPresenter;
import com.scatl.uestcbbs.util.Constant;
import com.scatl.uestcbbs.util.ImageUtil;
import com.scatl.uestcbbs.util.SharePrefUtil;
import com.scatl.uestcbbs.util.TimeUtil;

import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.ViewPagerHelper;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UserDetailActivity extends BaseActivity implements UserDetailView, AppBarLayout.OnOffsetChangedListener{

    private RelativeLayout userInfoRl;
    private CoordinatorLayout coordinatorLayout;
    private AppBarLayout appBarLayout;
    private Toolbar toolbar;
    private ProgressBar progressBar;
    private ImageView background;
    private CircleImageView avatar;
    private TextView userName, userSign, userFollowed, userFollow, userLevel, userGender, hint;
    private TextView shuidiNum, jifenNum;
    private LinearLayout shuidiLayout, jifenLayout;
    private Button favoriteBtn, modifyBtn;
    private ImageButton chatBtn, blackBtn;
    private MagicIndicator indicator;
    private ViewPager viewPager;

    private UserDetailPresenter userDetailPresenter;
    private UserDetailBean userDetailBean;

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
        toolbar = findViewById(R.id.user_detail_toolbar);
        coordinatorLayout = findViewById(R.id.user_detail_coor_layout);
        userInfoRl = findViewById(R.id.user_detail_info_rl);
        progressBar = findViewById(R.id.user_detail_progressbar);
        background = findViewById(R.id.user_detail_user_background);
        avatar = findViewById(R.id.user_detail_user_icon);
        userName = findViewById(R.id.user_detail_user_name);
        userSign = findViewById(R.id.user_detail_user_sign);
        userFollowed = findViewById(R.id.user_detail_followed_num);
        userFollow = findViewById(R.id.user_detail_follow_num);
        userLevel = findViewById(R.id.user_detail_user_level);
        userGender = findViewById(R.id.user_detail_user_gender);
        favoriteBtn = findViewById(R.id.user_detail_favorite_btn);
        chatBtn = findViewById(R.id.user_detail_chat_btn);
        blackBtn = findViewById(R.id.user_detail_black_btn);
        modifyBtn = findViewById(R.id.user_detail_modify_btn);
        hint = findViewById(R.id.user_detail_hint);
        shuidiNum = findViewById(R.id.user_detail_shuidi_num);
        jifenNum = findViewById(R.id.user_detail_jifen_num);
        shuidiLayout = findViewById(R.id.user_detail_shuidi_layout);
        jifenLayout = findViewById(R.id.user_detail_jifen_layout);
        indicator = findViewById(R.id.user_detail_indicator);
        viewPager = findViewById(R.id.user_detail_viewpager);
    }

    @Override
    protected void initView() {
        userDetailPresenter = (UserDetailPresenter) presenter;

        progressBar.setVisibility(View.VISIBLE);

        favoriteBtn.setOnClickListener(this);
        chatBtn.setOnClickListener(this);
        blackBtn.setOnClickListener(this);
        modifyBtn.setOnClickListener(this);
        shuidiLayout.setOnClickListener(this);
        jifenLayout.setOnClickListener(this);
        userLevel.setOnClickListener(this);
        userGender.setOnClickListener(this);
        userFollowed.setOnClickListener(this);
        userFollow.setOnClickListener(this);
        appBarLayout.addOnOffsetChangedListener(this);

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (userId == SharePrefUtil.getUid(this)) {
            modifyBtn.setVisibility(View.VISIBLE);
            favoriteBtn.setVisibility(View.GONE);
            chatBtn.setVisibility(View.GONE);
            blackBtn.setVisibility(View.GONE);
            userSign.setOnClickListener(this);
        }

        viewPager.setOffscreenPageLimit(4);
        viewPager.setAdapter(new UserPostViewPagerAdapter(getSupportFragmentManager(),
                FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT, userId));
        viewPager.setCurrentItem(0);

        userDetailPresenter.getUserDetail(userId, this);
    }

    @Override
    protected BasePresenter initPresenter() {
        return new UserDetailPresenter();
    }

    @Override
    protected void onClickListener(View view) {
        if (view.getId() == R.id.user_detail_favorite_btn) {
            userDetailPresenter.followUser(userId, userDetailBean.is_follow == 1 ? "unfollow" : "follow", this);
        }
        if (view.getId() == R.id.user_detail_chat_btn) {
            Intent intent = new Intent(this, PrivateChatActivity.class);
            intent.putExtra(Constant.IntentKey.USER_ID, userId);
            intent.putExtra(Constant.IntentKey.USER_NAME, userDetailBean.name);
            startActivity(intent);
        }
        if (view.getId() == R.id.user_detail_black_btn) {
            if (userDetailBean.is_black == 0) {
                userDetailPresenter.showBlackConfirmDialog(this, userId);
            } else {
                userDetailPresenter.blackUser(userId, "delblack", this);
            }
        }
        if (view.getId() == R.id.user_detail_modify_btn) {
            userDetailPresenter.showModifyInfoDialog(this);
        }
        if (view.getId() == R.id.user_detail_shuidi_layout || view.getId() == R.id.user_detail_jifen_layout) {
            userDetailPresenter.showUserInfo(userDetailBean, true, this);
        }
        if (view.getId() == R.id.user_detail_user_gender || view.getId() == R.id.user_detail_user_level) {
            userDetailPresenter.showUserInfo(userDetailBean, false, this);
        }
        if (view.getId() == R.id.user_detail_user_sign) {
            userDetailPresenter.showModifySignDialog(this);
        }
        if (view.getId() == R.id.user_detail_followed_num) {
            Bundle bundle = new Bundle();
            bundle.putInt(Constant.IntentKey.USER_ID, userId);
            bundle.putString(Constant.IntentKey.USER_NAME, userDetailBean.name);
            bundle.putString(Constant.IntentKey.TYPE, UserFriendBottomFragment.TYPE_FOLLOWED);
            UserFriendBottomFragment.getInstance(bundle)
                    .show(getSupportFragmentManager(), TimeUtil.getStringMs());
        }
        if (view.getId() == R.id.user_detail_follow_num) {
            Bundle bundle = new Bundle();
            bundle.putInt(Constant.IntentKey.USER_ID, userId);
            bundle.putString(Constant.IntentKey.USER_NAME, userDetailBean.name);
            bundle.putString(Constant.IntentKey.TYPE, UserFriendBottomFragment.TYPE_FOLLOW);
            UserFriendBottomFragment.getInstance(bundle)
                    .show(getSupportFragmentManager(), TimeUtil.getStringMs());
        }
    }


    @Override
    public void onGetUserDetailSuccess(UserDetailBean userDetailBean) {

        this.userDetailBean = userDetailBean;

        final String[] titles = {"发表(" + userDetailBean.topic_num + ")", "回复(" + userDetailBean.reply_posts_num + ")", "收藏", "相册"};

        CommonNavigator commonNavigator = new CommonNavigator(this);
        commonNavigator.setAdapter(new BaseIndicatorAdapter(titles, viewPager));
        indicator.setNavigator(commonNavigator);
        ViewPagerHelper.bind(indicator, viewPager);

        AlphaAnimation alphaAnimation = new AlphaAnimation(0, 1);
        alphaAnimation.setDuration(600);
        coordinatorLayout.startAnimation(alphaAnimation);
        coordinatorLayout.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);

        userName.setText(userDetailBean.name);
        userFollow.setText(String.valueOf("关注：" + userDetailBean.friend_num));
        userFollowed.setText(String.valueOf("粉丝：" + userDetailBean.follow_num));
        favoriteBtn.setText(userDetailBean.is_follow == 1 ? "已关注" : "关注");

        blackBtn.setImageResource(userDetailBean.is_black == 1 ? R.drawable.ic_black_list : R.drawable.ic_white_list);
        blackBtn.setImageTintList(ColorStateList.valueOf(Color.parseColor(userDetailBean.is_black == 1 ? "#FF3C3C" : "#ffffff")));

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
            userLevel.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.colorPrimary)));
        }

        Glide.with(this).load(userDetailBean.icon).into(avatar);
        Glide.with(this).load(userDetailBean.icon).into(new SimpleTarget<Drawable>() {
            @Override
            public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                background.setImageBitmap(ImageUtil.blurPhoto(UserDetailActivity.this,
                        resource instanceof GifDrawable ?  ((GifDrawable) resource).getFirstFrame() : ImageUtil.drawable2Bitmap(resource), 25));
            }
        });

    }

    @Override
    public void onGetUserDetailError(String msg) {
        hint.setText(msg);
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onFollowUserSuccess(FollowUserBean followUserBean) {
        showSnackBar(getWindow().getDecorView(), followUserBean.head.errInfo);
        favoriteBtn.setText(userDetailBean.is_follow == 1 ? "关注" : "已关注");
        userDetailBean.is_follow = userDetailBean.is_follow == 1 ? 0 : 1;
    }

    @Override
    public void onFollowUserError(String msg) {
        showSnackBar(getWindow().getDecorView(), msg);
    }

    @Override
    public void onBlackUserSuccess(BlackUserBean blackUserBean) {
        showSnackBar(getWindow().getDecorView(), blackUserBean.head.errInfo);
        blackBtn.setImageResource(userDetailBean.is_black == 0 ? R.drawable.ic_black_list : R.drawable.ic_white_list);
        blackBtn.setImageTintList(ColorStateList.valueOf(Color.parseColor(userDetailBean.is_black == 0 ? "#FF3C3C" : "#ffffff")));
        userDetailBean.is_black = userDetailBean.is_black == 1 ? 0 : 1;
    }

    @Override
    public void onBlackUserError(String msg) {
        showSnackBar(getWindow().getDecorView(), msg);
    }

    @Override
    public void onModifySignSuccess(ModifySignBean modifySignBean, String sign) {
        userSign.setText(sign);
        userDetailBean.sign = sign;
        showSnackBar(coordinatorLayout, modifySignBean.head.errInfo);
    }

    @Override
    public void onModifySignError(String msg) {
        //showSnackBar(coordinatorLayout, msg);
        showToast(msg);
    }

    @Override
    public void onModifyPswSuccess(ModifyPswBean modifyPswBean) {
        showSnackBar(coordinatorLayout, modifyPswBean.head.errInfo);
    }

    @Override
    public void onModifyPswError(String msg) {
        showToast(msg);
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int i) {
        int scrollRange = appBarLayout.getTotalScrollRange();
        float alpha = 1 - (1.0f * (- i)) / scrollRange;
        userInfoRl.setAlpha(alpha);
        toolbar.setTitle(userDetailBean.name);
        toolbar.setAlpha(1-alpha);
//        toolbar.setTitle(scrollRange == (-i) ? userDetailBean.name : " ");
    }

    @Override
    protected void setStatusBar() {
        super.setStatusBar();
        StatusBarUtil.setTransparent(this);
    }
}
