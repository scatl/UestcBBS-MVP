package com.scatl.uestcbbs.module.board.view;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.net.Uri;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.jaeger.library.StatusBarUtil;
import com.scatl.uestcbbs.R;
import com.scatl.uestcbbs.base.BaseActivity;
import com.scatl.uestcbbs.base.BaseEvent;
import com.scatl.uestcbbs.base.BaseIndicatorAdapter;
import com.scatl.uestcbbs.base.BasePresenter;
import com.scatl.uestcbbs.custom.imageview.CircleImageView;
import com.scatl.uestcbbs.entity.SingleBoardBean;
import com.scatl.uestcbbs.entity.SubForumListBean;
import com.scatl.uestcbbs.helper.glidehelper.GlideLoader4Matisse;
import com.scatl.uestcbbs.module.board.adapter.BoardPostViewPagerAdapter;
import com.scatl.uestcbbs.module.board.presenter.BoardPresenter;
import com.scatl.uestcbbs.util.Constant;
import com.scatl.uestcbbs.util.SharePrefUtil;
import com.scatl.uestcbbs.util.TimeUtil;
import com.yalantis.ucrop.UCrop;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;

import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.ViewPagerHelper;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.glide.transformations.BlurTransformation;

public class BoardActivity extends BaseActivity implements BoardView, AppBarLayout.OnOffsetChangedListener {

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

    private BoardPresenter boardPresenter;
    private int boardId;
    private String boardName;

    private SubForumListBean subForumListBean;
    private SingleBoardBean singleBoardBean;

    private static final int ACTION_SELECT_PHOTO = 99;
    private static final int ACTION_MODIFY_PHOTO = 100;

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
    }

    @Override
    protected void initView() {

        boardPresenter = (BoardPresenter) presenter;

        boardNameTv.setText(boardName);

        appBarLayout.addOnOffsetChangedListener(this);
        boardBackground.setOnClickListener(this::onClickListener);

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        loadBoardImg();
        boardPresenter.getSubBoardList(boardId, this);
    }

    @Override
    protected BasePresenter initPresenter() {
        return new BoardPresenter();
    }

    @Override
    protected void onClickListener(View view) {
        if (view.getId() == R.id.board_background) {
            boardPresenter.requestPermission(this, ACTION_SELECT_PHOTO, Manifest.permission.READ_EXTERNAL_STORAGE);
        }
    }

    private void loadBoardImg() {
        Glide.with(this).load(SharePrefUtil.getBoardImg(this, boardId)).into(boardIcon);
        Glide.with(this)
                .load(SharePrefUtil.getBoardImg(this, boardId))
                .apply(new RequestOptions().transform(new BlurTransformation()))
                .into(boardBackground);
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

        List<Integer> ids = new ArrayList<>();
        String[] titles;
        ids.add(boardId);

        if (subForumListBean.list != null && subForumListBean.list.size() != 0) {
            titles = new String[subForumListBean.list.get(0).board_list.size() + 1];
            titles[0] = boardName;

            for (int i = 0; i < subForumListBean.list.get(0).board_list.size(); i ++) {
                titles[i + 1] = subForumListBean.list.get(0).board_list.get(i).board_name;
                ids.add(subForumListBean.list.get(0).board_list.get(i).board_id);
            }
        } else {
            titles = new String[1];
            titles[0] = boardName;
            indicator.setVisibility(View.GONE);
        }

        viewPager.setOffscreenPageLimit(ids.size());
        viewPager.setAdapter(new BoardPostViewPagerAdapter(getSupportFragmentManager(), FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT, ids));
        viewPager.setCurrentItem(0);
        CommonNavigator commonNavigator = new CommonNavigator(this);
        commonNavigator.setAdapter(new BaseIndicatorAdapter(titles, viewPager));
        indicator.setNavigator(commonNavigator);
        ViewPagerHelper.bind(indicator, viewPager);
    }

    @Override
    public void onGetSubBoardListError(String msg) {
        progressBar.setVisibility(View.GONE);
        hint.setText(msg);
    }

    @Override
    public void onPermissionGranted(int action) {
        Matisse.from(this)
                .choose(MimeType.of(MimeType.JPEG, MimeType.PNG))
                .countable(true)
                .maxSelectable(1)
                .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
                .imageEngine(new GlideLoader4Matisse())
                .forResult(action);
    }

    @Override
    public void onPermissionRefused() {
        showSnackBar(coordinatorLayout, getString(R.string.permission_request));
    }

    @Override
    public void onPermissionRefusedWithNoMoreRequest() {
        showSnackBar(coordinatorLayout, getString(R.string.permission_refuse));
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
    protected void setStatusBar() {
        super.setStatusBar();
        StatusBarUtil.setTransparent(this);
    }

    @Override
    protected int setMenuResourceId() {
        return R.menu.menu_board;
    }

    @Override
    protected void onOptionsSelected(MenuItem item) {
        super.onOptionsSelected(item);
        if (item.getItemId() == R.id.menu_board_set_background_default) {
            SharePrefUtil.setBoardImg(this, boardId, "file:///android_asset/board_img/" + boardId + ".jpg");
            loadBoardImg();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UCrop.Options options = new UCrop.Options();
        options.setStatusBarColor(getResources().getColor(R.color.colorPrimary));
        options.setToolbarColor(getResources().getColor(R.color.colorPrimary));
        options.setToolbarWidgetColor(Color.parseColor("#ffffff"));
        options.setCompressionQuality(80);

        if (requestCode == ACTION_SELECT_PHOTO && resultCode == Activity.RESULT_OK && data != null) {
            final List<Uri> uris = Matisse.obtainResult(data);
            Uri des = Uri.fromFile(new File(getExternalFilesDir(Constant.AppPath.BOARD_IMG_PATH),
                    TimeUtil.getStringMs()));

            UCrop.of(uris.get(0), des)
                    .withOptions(options)
                    .withAspectRatio(3, 2)
                    .start(this, ACTION_MODIFY_PHOTO);
        }

        if (requestCode == ACTION_MODIFY_PHOTO && resultCode == Activity.RESULT_OK && data != null) {
            final Uri resultUri = UCrop.getOutput(data);

            File file = new File(resultUri.getPath());
            SharePrefUtil.setBoardImg(this, boardId, file.getAbsolutePath());
            loadBoardImg();
        }

    }
}
