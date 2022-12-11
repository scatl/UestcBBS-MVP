package com.scatl.uestcbbs.module.board.view;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.jaeger.library.StatusBarUtil;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.scatl.uestcbbs.R;
import com.scatl.uestcbbs.annotation.ToastType;
import com.scatl.uestcbbs.base.BaseActivity;
import com.scatl.uestcbbs.base.BasePresenter;
import com.scatl.uestcbbs.entity.ForumDetailBean;
import com.scatl.uestcbbs.entity.SingleBoardBean;
import com.scatl.uestcbbs.entity.SubForumListBean;
import com.scatl.uestcbbs.helper.glidehelper.GlideEngineForPictureSelector;
import com.scatl.uestcbbs.module.board.adapter.BoardPostViewPagerAdapter;
import com.scatl.uestcbbs.module.board.presenter.BoardPresenter;
import com.scatl.uestcbbs.util.ExtensionKt;
import com.scatl.uestcbbs.util.Constant;
import com.scatl.uestcbbs.util.FileUtil;
import com.scatl.uestcbbs.util.ImageUtil;
import com.scatl.uestcbbs.util.SharePrefUtil;

import java.util.ArrayList;
import java.util.List;

public class BoardActivity extends BaseActivity implements BoardView, AppBarLayout.OnOffsetChangedListener {

    private AppBarLayout appBarLayout;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private Toolbar toolbar;
    private CoordinatorLayout coordinatorLayout;
    private View boardInfoLayout;
    private ProgressBar progressBar;
    private TextView hint, postsNumTv;
    private ImageView boardIcon;
    private ImageView boardBackground;
    private TabLayout mTabLayout;
    private ViewPager2 viewPager;
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
        toolbar = findViewById(R.id.toolbar);
        boardInfoLayout = findViewById(R.id.board_info_layout);
        boardIcon = findViewById(R.id.board_icon);
        boardBackground = findViewById(R.id.board_background);
        mTabLayout = findViewById(R.id.board_indicator);
        viewPager = findViewById(R.id.board_viewpager);
        boardNameTv = findViewById(R.id.board_name);
        postsNumTv = findViewById(R.id.board_posts_num);
    }

    @Override
    protected void initView() {
        super.initView();
        boardPresenter = (BoardPresenter) presenter;

        ExtensionKt.desensitize(viewPager);
        boardNameTv.setText(boardName);

        appBarLayout.addOnOffsetChangedListener(this);
        boardBackground.setOnClickListener(this::onClickListener);

        setSupportActionBar(toolbar);

        loadBoardImg();
        boardPresenter.getForumDetail(this, boardId);
        if(boardId == Constant.DEPARTMENT_BOARD_ID) {
            String data = FileUtil.readAssetFile(this, "department.json");
            if (JSON.isValidObject(data)) {
                try {
                    JSONObject jsonObject = JSONObject.parseObject(data);
                    SubForumListBean subForumListBean = JSON.toJavaObject(jsonObject, SubForumListBean.class);
                    onGetSubBoardListSuccess(subForumListBean);
                } catch (Exception e) {
                    showToast("出错了，请联系开发者", ToastType.TYPE_ERROR);
                }
            }
        } else {
            boardPresenter.getSubBoardList(boardId, this);
        }
    }

    @Override
    protected BasePresenter initPresenter() {
        return new BoardPresenter();
    }

    @Override
    protected void onClickListener(View view) {
        if (view.getId() == R.id.board_background) {
            PictureSelector.create(this)
                    .openGallery(PictureMimeType.ofImage())
                    .isCamera(false)
                    .isGif(false)
                    .showCropFrame(true)
                    .hideBottomControls(false)
                    .theme(R.style.picture_WeChat_style)
                    .maxSelectNum(1)
                    .isEnableCrop(true)
                    .withAspectRatio(3, 2)
                    .imageEngine(GlideEngineForPictureSelector.createGlideEngine())
                    .forResult(PictureConfig.CHOOSE_REQUEST);
        }
    }

    private void loadBoardImg() {
        Glide
                .with(this)
                .load(SharePrefUtil.getBoardImg(this, boardId))
                .into(boardIcon);

        try {
            Glide
                    .with(this)
                    .load(SharePrefUtil.getBoardImg(this, boardId))
                    .into(new SimpleTarget<Drawable>() {
                        @Override
                        public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                            Bitmap bitmap = resource instanceof GifDrawable ? ((GifDrawable) resource).getFirstFrame() : ImageUtil.drawable2Bitmap(resource);
                            boardBackground.setImageBitmap(ImageUtil.blurPhoto(BoardActivity.this, bitmap, 25));
                        }
                    });
        } catch (Exception e) {
//            Glide.with(this)
//                    .load(SharePrefUtil.getBoardImg(this, boardId))
//                    .apply(new RequestOptions().transform(new BlurTransformation()))
//                    .into(boardBackground);
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

        List<Integer> ids = new ArrayList<>();
        String[] titles;
        ids.add(boardId);

        if (subForumListBean.list != null && subForumListBean.list.size() != 0) {

            postsNumTv.setText("今日：" + subForumListBean.list.get(0).td_posts_num + " | " +
                    "主题：" + subForumListBean.list.get(0).topic_total_num + " | " +
                    "帖子：" + subForumListBean.list.get(0).posts_total_num);
            boardNameTv.setText(subForumListBean.list.get(0).board_category_name);

            titles = new String[subForumListBean.list.get(0).board_list.size() + 1];
            titles[0] = boardName;

            for (int i = 0; i < subForumListBean.list.get(0).board_list.size(); i ++) {
                titles[i + 1] = subForumListBean.list.get(0).board_list.get(i).board_name;
                ids.add(subForumListBean.list.get(0).board_list.get(i).board_id);
            }
        } else {
            titles = new String[1];
            titles[0] = boardName;
            mTabLayout.setVisibility(View.GONE);
        }

        viewPager.setOffscreenPageLimit(titles.length);
        viewPager.setAdapter(new BoardPostViewPagerAdapter(this, ids));
        viewPager.setCurrentItem(0);
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                BoardActivity.this.onPageSelected(position);
            }
        });

        new TabLayoutMediator(mTabLayout, viewPager, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                tab.setText(titles[position]);
            }
        }).attach();

    }

    @Override
    public void onGetSubBoardListError(String msg) {
        progressBar.setVisibility(View.GONE);
        hint.setText(msg);
    }


    @Override
    public void onPermissionRefusedWithNoMoreRequest() {
        showToast(getString(R.string.permission_refuse), ToastType.TYPE_ERROR);
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
    protected void onOptionsSelected(MenuItem item) {
        super.onOptionsSelected(item);
        if (item.getItemId() == R.id.menu_board_set_background_default) {
            SharePrefUtil.setBoardImg(this, boardId, "file:///android_asset/board_img/" + boardId + ".jpg");
            loadBoardImg();
        }
    }

    @Override
    public void onGetForumDetailSuccess(ForumDetailBean forumDetailBean) {

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == PictureConfig.CHOOSE_REQUEST) {
            List<LocalMedia> selectList = PictureSelector.obtainMultipleResult(data);
            if (selectList.size() != 0 && selectList.get(0).isCut()) {
                SharePrefUtil.setBoardImg(this, boardId, selectList.get(0).getCutPath());
                loadBoardImg();
            }
        }
    }

    private void onPageSelected(int position) {
        if (subForumListBean != null && ExtensionKt.isNotNullAndEmpty(subForumListBean.list)) {
            SubForumListBean.ListBean listBean = subForumListBean.list.get(0);
            if (ExtensionKt.isNotNullAndEmpty(listBean.board_list)) {
                if (position != 0) {
                    postsNumTv.setText("今日：" + listBean.board_list.get(position - 1).td_posts_num + " | " +
                            "主题：" + listBean.board_list.get(position - 1).topic_total_num + " | " +
                            "帖子：" + listBean.board_list.get(position - 1).posts_total_num);
                    boardNameTv.setText(listBean.board_list.get(position - 1).board_name);
                } else {
                    postsNumTv.setText("今日：" + listBean.td_posts_num + " | " +
                            "主题：" + listBean.topic_total_num + " | " +
                            "帖子：" + listBean.posts_total_num);
                    boardNameTv.setText(listBean.board_category_name);
                }
            }
        }
    }

}
