package com.scatl.uestcbbs.module.collection.view;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.material.appbar.AppBarLayout;
import com.jaeger.library.StatusBarUtil;
import com.scatl.uestcbbs.R;
import com.scatl.uestcbbs.base.BaseActivity;
import com.scatl.uestcbbs.base.BasePresenter;
import com.scatl.uestcbbs.callback.OnRefresh;
import com.scatl.uestcbbs.custom.MyLinearLayoutManger;
import com.scatl.uestcbbs.entity.CollectionDetailBean;
import com.scatl.uestcbbs.helper.glidehelper.GlideLoader4Common;
import com.scatl.uestcbbs.module.collection.adapter.CollectionAdapter;
import com.scatl.uestcbbs.module.collection.presenter.CollectionPresenter;
import com.scatl.uestcbbs.module.post.view.PostDetailActivity;
import com.scatl.uestcbbs.module.post.view.postdetail2.PostDetail2Activity;
import com.scatl.uestcbbs.module.user.view.UserDetailActivity;
import com.scatl.uestcbbs.util.Constant;
import com.scatl.uestcbbs.util.ImageUtil;
import com.scatl.uestcbbs.util.RefreshUtil;
import com.scatl.uestcbbs.util.SharePrefUtil;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.RefreshState;
import com.zhy.view.flowlayout.FlowLayout;
import com.zhy.view.flowlayout.TagAdapter;
import com.zhy.view.flowlayout.TagFlowLayout;

import java.util.Random;

import me.zhanghai.android.materialratingbar.MaterialRatingBar;

public class CollectionActivity extends BaseActivity implements CollectionView, AppBarLayout.OnOffsetChangedListener {

    private CoordinatorLayout coordinatorLayout;
    private ImageView background, avatar;
    private TextView collectionTitle, dsp, ratingTitle, subscribeCount;
    private Button subscribeBtn;
    private AppBarLayout appBarLayout;
    private Toolbar toolbar;
    private RelativeLayout collectionRl;
    private MaterialRatingBar ratingBar;
    private TagFlowLayout tagFlowLayout;
    private SmartRefreshLayout refreshLayout;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private TextView hint;
    private CollectionAdapter collectionAdapter;

    private int ctid, page = 1;
    private String formHash;
    private CollectionDetailBean collectionDetailBean;

    private CollectionPresenter collectionPresenter;

    @Override
    protected void getIntent(Intent intent) {
        super.getIntent(intent);
        ctid = intent.getIntExtra(Constant.IntentKey.COLLECTION_ID, Integer.MAX_VALUE);
    }

    @Override
    protected int setLayoutResourceId() {
        return R.layout.activity_collection;
    }

    @Override
    protected void findView() {
        coordinatorLayout = findViewById(R.id.collection_coor_layout);
        background = findViewById(R.id.collection_background);
        avatar = findViewById(R.id.collection_user_avatar);
        collectionTitle = findViewById(R.id.collection_title);
        dsp = findViewById(R.id.collection_dsp);
        subscribeBtn = findViewById(R.id.collection_subscribe_btn);
        tagFlowLayout = findViewById(R.id.collection_tags);
        refreshLayout = findViewById(R.id.collection_refresh);
        recyclerView = findViewById(R.id.collection_rv);
        ratingTitle = findViewById(R.id.collection_rating_title);
        ratingBar = findViewById(R.id.collection_ratingbar);
        toolbar = findViewById(R.id.collection_toolbar);
        appBarLayout = findViewById(R.id.collection_app_bar);
        collectionRl = findViewById(R.id.collection_rl);
        progressBar = findViewById(R.id.collection_progressbar);
        hint = findViewById(R.id.collection_hint);
        subscribeCount = findViewById(R.id.collection_subscribe_count);
    }

    @Override
    protected void initView() {
        collectionPresenter = (CollectionPresenter) presenter;

        progressBar.setVisibility(View.VISIBLE);
        subscribeBtn.setOnClickListener(this);
        appBarLayout.addOnOffsetChangedListener(this);

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        collectionAdapter = new CollectionAdapter(R.layout.item_collection);
        recyclerView.setLayoutManager(new MyLinearLayoutManger(this));
        recyclerView.setLayoutAnimation(AnimationUtils.loadLayoutAnimation(this, R.anim.layout_animation_scale_in));
        recyclerView.setAdapter(collectionAdapter);

        collectionPresenter.getCollectionDetail(ctid, 1);
    }

    @Override
    protected BasePresenter initPresenter() {
        return new CollectionPresenter();
    }

    @Override
    protected void setOnItemClickListener() {
        collectionAdapter.setOnItemClickListener((adapter, view, position) -> {
            if (view.getId() == R.id.item_collection_card_view) {
                Intent intent = new Intent(this, SharePrefUtil.isPostDetailNewStyle(this) ? PostDetail2Activity.class : PostDetailActivity.class);
                intent.putExtra(Constant.IntentKey.TOPIC_ID, collectionAdapter.getData().get(position).topicId);
                startActivity(intent);
            }
        });

        collectionAdapter.setOnItemChildClickListener((adapter, view, position) -> {
            if (view.getId() == R.id.item_collection_user_avatar) {
                Intent intent = new Intent(this, UserDetailActivity.class);
                intent.putExtra(Constant.IntentKey.USER_ID, collectionAdapter.getData().get(position).authorId);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onClickListener(View view) {
        if (view.getId() == R.id.collection_subscribe_btn) {
            collectionPresenter.subscribeCollection(ctid, collectionDetailBean.isSubscribe ? "unfo" : "follow", formHash);
        }
    }

    @Override
    protected void setOnRefreshListener() {
        RefreshUtil.setOnRefreshListener(this, refreshLayout, new OnRefresh() {
            @Override
            public void onRefresh(RefreshLayout refreshLayout) {
                page = 1;
                collectionPresenter.getCollectionDetail(ctid, page);
            }

            @Override
            public void onLoadMore(RefreshLayout refreshLayout) {
                collectionPresenter.getCollectionDetail(ctid, page);
            }
        });
    }

    @Override
    public void onGetFormHashSuccess(String formHash) {
        this.formHash = formHash;
    }

    @Override
    public void onGetCollectionSuccess(CollectionDetailBean collectionDetailBean, boolean hasNext) {
        this.collectionDetailBean = collectionDetailBean;

        progressBar.setVisibility(View.GONE);
        coordinatorLayout.setVisibility(View.VISIBLE);

        collectionTitle.setText(collectionDetailBean.collectionTitle);
        subscribeCount.setText(collectionDetailBean.subscribeCount + "人订阅");
        subscribeBtn.setText(collectionDetailBean.isSubscribe ? "取消订阅" : "订阅");
        dsp.setText(collectionDetailBean.collectionDsp);
        dsp.setVisibility(TextUtils.isEmpty(collectionDetailBean.collectionDsp) ? View.GONE : View.VISIBLE);
        ratingBar.setRating(collectionDetailBean.ratingScore);
        ratingTitle.setText(collectionDetailBean.ratingTitle);
        if (! this.isFinishing()) {
            GlideLoader4Common.simpleLoad(this, collectionDetailBean.collectionAuthorAvatar, avatar);
            Glide.with(this).load(collectionDetailBean.collectionAuthorAvatar).into(new SimpleTarget<Drawable>() {
                @Override
                public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                    background.setImageBitmap(ImageUtil.blurPhoto(CollectionActivity.this,
                            resource instanceof GifDrawable ?  ((GifDrawable) resource).getFirstFrame() : ImageUtil.drawable2Bitmap(resource), 25));
                }
            });
        }

        tagFlowLayout.setAdapter(new TagAdapter<String>(collectionDetailBean.collectionTags) {
            @Override
            public View getView(FlowLayout parent, int position, String o) {
                TextView textView = new TextView(CollectionActivity.this);
                textView.setClickable(true);
                textView.setFocusable(true);
                textView.setTextSize(12);
                textView.setText(o);
                textView.setAlpha(0.9f);
                textView.setTextColor(Color.WHITE);
                textView.setBackgroundResource(R.drawable.shape_collection_tag);
                textView.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(Constant.TAG_COLOR[new Random().nextInt(Constant.TAG_COLOR.length)])));
                return textView;
            }
        });

        if (page == 1) {
            collectionAdapter.addData(collectionDetailBean.postListBean, true);
            //collectionAdapter.setNewData(collectionDetailBean.postListBean);
            recyclerView.scheduleLayoutAnimation();
        } else {
            collectionAdapter.addData(collectionDetailBean.postListBean, false);
            //collectionAdapter.addData(collectionDetailBean.postListBean);
        }

        if (refreshLayout.getState() == RefreshState.Refreshing) {
            if (hasNext) {
                refreshLayout.finishRefresh();
            } else {
                refreshLayout.finishRefreshWithNoMoreData();
            }
        }
        if (refreshLayout.getState() == RefreshState.Loading) {
            if (hasNext) {
                refreshLayout.finishLoadMore(true);
            } else {
                refreshLayout.finishLoadMoreWithNoMoreData();
            }
        }

        page = page + 1;
    }

    @Override
    public void onGetCollectionError(String msg) {
        if (refreshLayout.getState() == RefreshState.Refreshing) {
            refreshLayout.finishRefresh();
        }
        if (refreshLayout.getState() == RefreshState.Loading) {
            refreshLayout.finishLoadMore(false);
        }
        hint.setText(msg);
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onSubscribeCollectionSuccess(boolean subscribe) {
        collectionDetailBean.isSubscribe = subscribe;
        subscribeBtn.setText(collectionDetailBean.isSubscribe ? "取消订阅" : "订阅");
        showSnackBar(coordinatorLayout, subscribe ? "订阅成功" : "取消订阅成功");
    }

    @Override
    public void onSubscribeCollectionError(String msg) {
        showSnackBar(coordinatorLayout, msg);
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int i) {
        int scrollRange = appBarLayout.getTotalScrollRange();
        float alpha = 1 - (1.0f * (- i)) / scrollRange;
        collectionRl.setAlpha(alpha);
        toolbar.setTitle(collectionDetailBean.collectionTitle);
        toolbar.setAlpha(1-alpha);
    }

    @Override
    protected void setStatusBar() {
        super.setStatusBar();
        StatusBarUtil.setTransparent(this);
    }


}
