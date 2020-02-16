package com.scatl.uestcbbs.module.board.view;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.jaeger.library.StatusBarUtil;
import com.scatl.uestcbbs.R;
import com.scatl.uestcbbs.base.BaseActivity;
import com.scatl.uestcbbs.base.BaseEvent;
import com.scatl.uestcbbs.base.BasePresenter;
import com.scatl.uestcbbs.custom.imageview.CircleImageView;
import com.scatl.uestcbbs.entity.SingleBoardBean;
import com.scatl.uestcbbs.entity.SubForumListBean;
import com.scatl.uestcbbs.module.board.adapter.BoardPostIndicatorAdapter;
import com.scatl.uestcbbs.module.board.adapter.BoardPostViewPagerAdapter;
import com.scatl.uestcbbs.module.board.presenter.BoardPresenter;
import com.scatl.uestcbbs.util.Constant;
import com.scatl.uestcbbs.util.ImageUtil;

import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.ViewPagerHelper;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;

import org.greenrobot.eventbus.EventBus;

public class BoardActivity extends BaseActivity implements BoardView, AppBarLayout.OnOffsetChangedListener{

    private AppBarLayout appBarLayout;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private Toolbar toolbar;
    private CoordinatorLayout coordinatorLayout;
    private LinearLayout boardInfoLayout;
    private ProgressBar progressBar;
    private TextView hint;
    private CircleImageView boardIcon;
    private ImageView boardBackground;
    private MagicIndicator indicator;
    private ViewPager viewPager;
    private TextView boardNameTv;
    private ImageView subBoardIcon;
    private LinearLayout filterLayout;
    private TextView filterName;

    private BoardPresenter boardPresenter;
    private int boardId;
    private String boardName;

    private SubForumListBean subForumListBean;
    private SingleBoardBean singleBoardBean;

    @Override
    protected void getIntent(Intent intent) {
        super.getIntent(intent);
        boardId = intent.getIntExtra(Constant.IntentKey.BOARD_ID, Integer.MAX_VALUE);
        boardName = intent.getStringExtra(Constant.IntentKey.BOARD_NAME);
    }

    @Override
    protected int setLayoutResourceId() {
        return R.layout.activity_board;
    }

    @Override
    protected void findView() {
        collapsingToolbarLayout = findViewById(R.id.board_toolbar_layout);
        progressBar = findViewById(R.id.board_progressbar);
        hint = findViewById(R.id.board_hint);
        appBarLayout = findViewById(R.id.board_app_bar);
        coordinatorLayout = findViewById(R.id.board_coor_layout);
        toolbar = findViewById(R.id.board_toolbar);
        boardInfoLayout = findViewById(R.id.board_info_layout);
        boardIcon = findViewById(R.id.board_icon);
        boardBackground = findViewById(R.id.board_background);
        indicator = findViewById(R.id.board_indicator);
        viewPager = findViewById(R.id.board_viewpager);
        boardNameTv = findViewById(R.id.board_name);
        subBoardIcon = findViewById(R.id.board_subboard_icon);
        filterLayout = findViewById(R.id.board_filter_layout);
        filterName = findViewById(R.id.board_filter_name);
    }

    @Override
    protected void initView() {

        boardPresenter = (BoardPresenter) presenter;

        boardNameTv.setText(boardName);
        Glide.with(this).load("file:///android_asset/board_img/" + boardId + ".jpg").into(boardIcon);
        Glide.with(this).load("file:///android_asset/board_img/" + boardId + ".jpg").into(new SimpleTarget<Drawable>() {
            @Override
            public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                boardBackground.setImageBitmap(ImageUtil.blurPhoto(BoardActivity.this,
                        resource instanceof GifDrawable ?  ((GifDrawable) resource).getFirstFrame() : ImageUtil.drawable2Bitmap(resource), 15));
            }
        });

        appBarLayout.addOnOffsetChangedListener(this);
        subBoardIcon.setOnClickListener(this::onClickListener);
        filterLayout.setOnClickListener(this::onClickListener);

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        viewPager.setOffscreenPageLimit(3);
        viewPager.setAdapter(new BoardPostViewPagerAdapter(getSupportFragmentManager(),
                FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT, boardId));
        viewPager.setCurrentItem(0);
        CommonNavigator commonNavigator = new CommonNavigator(this);
        commonNavigator.setAdapter(new BoardPostIndicatorAdapter(viewPager));
        indicator.setNavigator(commonNavigator);
        ViewPagerHelper.bind(indicator, viewPager);

        boardPresenter.getSubBoardList(boardId, this);
    }

    @Override
    protected BasePresenter initPresenter() {
        return new BoardPresenter();
    }

    @Override
    protected void onClickListener(View view) {
        if (view.getId() == R.id.board_subboard_icon) {
            boardPresenter.showSubBoardDialog(this, subForumListBean);
        }
        if (view.getId() == R.id.board_filter_layout) {
            if (singleBoardBean != null && singleBoardBean.classificationType_list.size() > 0)
                boardPresenter.showFilterDialog(this, singleBoardBean);
        }
    }

    @Override
    public void onGetSubBoardListSuccess(SubForumListBean subForumListBean) {
        this.subForumListBean = subForumListBean;
        AlphaAnimation alphaAnimation = new AlphaAnimation(0, 1);
        alphaAnimation.setDuration(600);
        coordinatorLayout.startAnimation(alphaAnimation);
        coordinatorLayout.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
        hint.setVisibility(View.GONE);

        if (subForumListBean.list == null || subForumListBean.list.size() == 0) {
            subBoardIcon.setVisibility(View.GONE);
        }
    }

    @Override
    public void onGetSubBoardListError(String msg) {
        progressBar.setVisibility(View.GONE);
        hint.setText(msg);
    }

    @Override
    public void onSubBoardSelect(int position) {
        filterName.setText("全部");
        boardNameTv.setText(subForumListBean.list.get(0).board_list.get(position).board_name);
        EventBus.getDefault().post(new BaseEvent<>(BaseEvent.EventCode.BOARD_ID_CHANGE, subForumListBean.list.get(0).board_list.get(position).board_id));
    }

    @Override
    public void onFilterSelect(int fid, String name, int position) {
        filterName.setText(name);
        EventBus.getDefault().post(new BaseEvent<>(BaseEvent.EventCode.FILTER_ID_CHANGE, fid));
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
        int scrollRange = appBarLayout.getTotalScrollRange();
        float alpha = 1 - (1.0f * (- verticalOffset)) / scrollRange;
        boardInfoLayout.setAlpha(alpha);
        toolbar.setTitle(boardName);
        toolbar.setAlpha(1-alpha);
    }

    @Override
    protected boolean registerEventBus() {
        return true;
    }

    @Override
    public void onEventBusReceived(BaseEvent baseEvent) {
        if (baseEvent.eventCode == BaseEvent.EventCode.FILTER_DATA) {
            this.singleBoardBean = (SingleBoardBean) baseEvent.eventData;
        }

    }

    @Override
    protected void setStatusBar() {
        super.setStatusBar();
        StatusBarUtil.setTransparent(this);
    }
}
