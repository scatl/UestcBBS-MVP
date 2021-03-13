package com.scatl.uestcbbs.module.post.view;

import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.FileObserver;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.scatl.uestcbbs.R;
import com.scatl.uestcbbs.api.ApiConstant;
import com.scatl.uestcbbs.base.BaseActivity;
import com.scatl.uestcbbs.base.BaseEvent;
import com.scatl.uestcbbs.base.BasePresenter;
import com.scatl.uestcbbs.callback.OnRefresh;
import com.scatl.uestcbbs.custom.MyLinearLayoutManger;
import com.scatl.uestcbbs.custom.imageview.CircleImageView;
import com.scatl.uestcbbs.custom.postview.ContentView;
import com.scatl.uestcbbs.custom.postview.MyClickableSpan;
import com.scatl.uestcbbs.entity.FavoritePostResultBean;
import com.scatl.uestcbbs.entity.PostDianPingBean;
import com.scatl.uestcbbs.entity.PostDetailBean;
import com.scatl.uestcbbs.entity.ReportBean;
import com.scatl.uestcbbs.entity.SupportResultBean;
import com.scatl.uestcbbs.entity.VoteResultBean;
import com.scatl.uestcbbs.module.magic.view.UseRegretMagicFragment;
import com.scatl.uestcbbs.module.post.adapter.PostCommentAdapter;
import com.scatl.uestcbbs.module.post.adapter.PostDianPingAdapter;
import com.scatl.uestcbbs.module.post.presenter.PostDetailPresenter;
import com.scatl.uestcbbs.module.user.view.UserDetailActivity;
import com.scatl.uestcbbs.module.webview.view.WebViewActivity;
import com.scatl.uestcbbs.util.CommonUtil;
import com.scatl.uestcbbs.util.Constant;
import com.scatl.uestcbbs.util.RefreshUtil;
import com.scatl.uestcbbs.util.SharePrefUtil;
import com.scatl.uestcbbs.util.TimeUtil;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;

import java.io.File;
import java.util.List;

public class PostDetailActivity extends BaseActivity implements PostDetailView{

    private static final String TAG = "PostDetailActivity";

    private CoordinatorLayout coordinatorLayout;
    private Toolbar toolbar;
    private SmartRefreshLayout refreshLayout;
    private RecyclerView recyclerView;
    private PostCommentAdapter commentAdapter;
    private TextView hint;
    private ImageView favoriteBtn, supportBtn, upBtn, timeOrderBtn, authorOnlyBtn, shangBtn, buchongBtn;
    private CardView optionsLl; //底部的工具栏，评论，点赞等
    private LinearLayout createCommentLl, createDianPingLayout;
    private LottieAnimationView loading;

    private View basicView; //基本信息，头像时间等，包括帖子内容
    private CircleImageView userAvatar;
    private ContentView contentView;
    private View favoriteLayout;
    private TextView favoriteNumTextView, rewordInfoTv;

    private View zanListView; //表达看法的用户（支持和发对，无法区分）

    private View rateView; //评分

    private View dianPingView;//点评
    private LottieAnimationView dianPingLoading;
    private TextView dianPingHint, dianPingLastPage, dianPingNextPage;
    private RecyclerView dianPingRv;
    private PostDianPingAdapter postDianPingAdapter;

    private View commentView;
    private TextView commentViewTitle;//评论标题

    private View hotCommentView;
    private RecyclerView hotCommentRv;
    private PostCommentAdapter hotCommentAdapter;

    private PostDetailPresenter postDetailPresenter;
    private PostDetailBean postDetailBean;

    private int topicId;
    private int page = 1, order = 0, authorId = 0, dianPingPage = 1, topicUserId;
    private String formHash;

    @Override
    protected void getIntent(Intent intent) {
        super.getIntent(intent);
        topicId = intent.getIntExtra(Constant.IntentKey.TOPIC_ID, Integer.MAX_VALUE);
    }

    @Override
    protected int setLayoutResourceId() {
        return R.layout.activity_post_detail;
    }

    @Override
    protected void findView() {
        coordinatorLayout = findViewById(R.id.post_detail_coor_layout);
        toolbar = findViewById(R.id.post_detail_toolbar);
        recyclerView = findViewById(R.id.post_detail_rv);
        refreshLayout = findViewById(R.id.post_detail_refresh);
        hint = findViewById(R.id.post_detail_hint);
        favoriteBtn = findViewById(R.id.post_detail_favorite_btn);
        supportBtn = findViewById(R.id.post_detail_support_btn);
        upBtn = findViewById(R.id.post_detail_up_btn);
        timeOrderBtn = findViewById(R.id.post_detail_time_order_btn);
        authorOnlyBtn = findViewById(R.id.post_detail_watch_author_only_btn);
        shangBtn = findViewById(R.id.post_detail_shang_btn);
        buchongBtn = findViewById(R.id.post_detail_buchong_btn);
        optionsLl = findViewById(R.id.post_detail_options_layout);
        createCommentLl = findViewById(R.id.post_detail_create_comment_layout);
        createDianPingLayout = findViewById(R.id.post_detail_create_dianping_layout);
        loading = findViewById(R.id.post_detail_loading);

        basicView = LayoutInflater.from(this).inflate(R.layout.post_detail_item_content_view, new LinearLayout(this));
        userAvatar = basicView.findViewById(R.id.post_detail_item_content_view_author_avatar);
        contentView = basicView.findViewById(R.id.post_detail_item_content_view_content);
        favoriteLayout = basicView.findViewById(R.id.post_detail_item_content_view_favorite_layout);
        favoriteNumTextView = basicView.findViewById(R.id.post_detail_item_content_view_favorite_num);
        rewordInfoTv = basicView.findViewById(R.id.post_detail_item_content_view_reword_info);

        zanListView = LayoutInflater.from(this).inflate(R.layout.post_detail_item_zanlist_view, new LinearLayout(this));

        commentView = LayoutInflater.from(this).inflate(R.layout.post_detail_item_comment_view, new LinearLayout(this));
        commentViewTitle = commentView.findViewById(R.id.post_detail_item_comment_view_title);

        rateView = LayoutInflater.from(this).inflate(R.layout.post_detail_item_rate_view, new LinearLayout(this));

        dianPingView = LayoutInflater.from(this).inflate(R.layout.post_detail_item_dianping_view, new RelativeLayout(this));
        dianPingHint = dianPingView.findViewById(R.id.post_detail_item_dianping_view_hint);
        dianPingLoading = dianPingView.findViewById(R.id.post_detail_item_dianping_view_loading);
        dianPingRv = dianPingView.findViewById(R.id.post_detail_item_dianping_view_rv);
        dianPingLastPage = dianPingView.findViewById(R.id.post_detail_item_dianping_view_last_page);
        dianPingNextPage = dianPingView.findViewById(R.id.post_detail_item_dianping_view_next_page);

        hotCommentView = LayoutInflater.from(this).inflate(R.layout.post_detail_item_hot_comment_view, new LinearLayout(this));
        hotCommentRv = hotCommentView.findViewById(R.id.post_detail_item_hot_comment_rv);
    }

    @Override
    protected void initView() {

        postDetailPresenter = (PostDetailPresenter) presenter;
        favoriteBtn.setOnClickListener(this);
        supportBtn.setOnClickListener(this);
        upBtn.setOnClickListener(this);
        timeOrderBtn.setOnClickListener(this);
        authorOnlyBtn.setOnClickListener(this);
        shangBtn.setOnClickListener(this);
        buchongBtn.setOnClickListener(this);
        createCommentLl.setOnClickListener(this);
        createDianPingLayout.setOnClickListener(this);
        userAvatar.setOnClickListener(this);
        dianPingLastPage.setOnClickListener(this);
        dianPingNextPage.setOnClickListener(this);

        toolbar.setTitle("帖子详情");
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //点评
        postDianPingAdapter = new PostDianPingAdapter(R.layout.item_post_detail_dianping);
        dianPingRv.setLayoutManager(new MyLinearLayoutManger(this));
        dianPingRv.setAdapter(postDianPingAdapter);

        hotCommentAdapter = new PostCommentAdapter(R.layout.item_post_comment);

        //评论
        commentAdapter = new PostCommentAdapter(R.layout.item_post_comment);
        commentAdapter.addHeaderView(basicView, 0);
        commentAdapter.addHeaderView(hotCommentView, 1);
        commentAdapter.addHeaderView(zanListView, 2);
        commentAdapter.addHeaderView(rateView, 3);
        commentAdapter.addHeaderView(dianPingView, 4);
        commentAdapter.addHeaderView(commentView, 5);

        recyclerView.setLayoutAnimation(AnimationUtils.loadLayoutAnimation(this, R.anim.layout_animation_scale_in));
        recyclerView.setLayoutManager(new MyLinearLayoutManger(this));
        recyclerView.setAdapter(commentAdapter);

        recyclerView.setVisibility(View.GONE);
        optionsLl.setVisibility(View.GONE);
        refreshLayout.setEnableRefresh(false);
        postDetailPresenter.getPostDetail(page, SharePrefUtil.getPageSize(this), order, topicId, authorId, this);
        postDetailPresenter.getAllComment(order, topicId, authorId, this);

    }

    @Override
    protected BasePresenter initPresenter() {
        return new PostDetailPresenter();
    }

    @Override
    protected void setOnItemClickListener() {

        //投票按钮点击
        contentView.setOnPollBtnClickListener(ids -> postDetailPresenter.vote(postDetailBean.topic.topic_id,
                postDetailBean.boardId, contentView.getVoteBean().type, ids, this));

        //查看公开投票人
        contentView.setOnViewVoterClickListener(() -> {
            Bundle bundle = new Bundle();
            bundle.putInt(Constant.IntentKey.TOPIC_ID, topicId);
            ViewVoterFragment.getInstance(bundle).show(getSupportFragmentManager(), TimeUtil.getStringMs());
        });

        //回复评论
        commentAdapter.setOnItemChildClickListener((adapter, view, position) -> {
            if (view.getId() == R.id.item_post_comment_reply_button ||
                    view.getId() == R.id.item_post_comment_root_rl) {
                Bundle bundle = new Bundle();
                bundle.putInt(Constant.IntentKey.BOARD_ID, postDetailBean.boardId);
                bundle.putInt(Constant.IntentKey.TOPIC_ID, postDetailBean.topic.topic_id);
                bundle.putInt(Constant.IntentKey.QUOTE_ID, commentAdapter.getData().get(position).reply_posts_id);
                bundle.putBoolean(Constant.IntentKey.IS_QUOTE, true);
                bundle.putString(Constant.IntentKey.USER_NAME, commentAdapter.getData().get(position).reply_name);
                CreateCommentFragment.getInstance(bundle)
                        .show(getSupportFragmentManager(), TimeUtil.getStringMs());
            }

            if (view.getId() == R.id.item_post_comment_support_button) {
                postDetailPresenter.support(postDetailBean.topic.topic_id,
                        commentAdapter.getData().get(position).reply_posts_id,
                        "post", "support", position, this);
            }

            if (view.getId() == R.id.item_post_comment_author_avatar) {
                Intent intent = new Intent(this, UserDetailActivity.class);
                intent.putExtra(Constant.IntentKey.USER_ID, commentAdapter.getData().get(position).reply_id);
                startActivity(intent);
            }
            if (view.getId() == R.id.item_post_comment_buchong_button) {
                onAppendPost(commentAdapter.getData().get(position).reply_posts_id, topicId);
            }
            if (view.getId() == R.id.item_post_comment_more_button) {
                postDetailPresenter.moreReplyOptionsDialog(this, formHash, postDetailBean.boardId,
                        topicId,  postDetailBean.topic.user_id, commentAdapter.getData().get(position));
            }
        });

        commentAdapter.setOnItemChildLongClickListener((adapter, view, position) -> {
            if (view.getId() == R.id.item_post_comment_root_rl) {
                postDetailPresenter.moreReplyOptionsDialog(this, formHash, postDetailBean.boardId,
                        topicId,  postDetailBean.topic.user_id, commentAdapter.getData().get(position));
            }
            return false;
        });

        //回复评论
        hotCommentAdapter.setOnItemChildClickListener((adapter, view, position) -> {
            if (view.getId() == R.id.item_post_comment_reply_button ||
                    view.getId() == R.id.item_post_comment_root_rl) {
                Bundle bundle = new Bundle();
                bundle.putInt(Constant.IntentKey.BOARD_ID, postDetailBean.boardId);
                bundle.putInt(Constant.IntentKey.TOPIC_ID, postDetailBean.topic.topic_id);
                bundle.putInt(Constant.IntentKey.QUOTE_ID, hotCommentAdapter.getData().get(position).reply_posts_id);
                bundle.putBoolean(Constant.IntentKey.IS_QUOTE, true);
                bundle.putString(Constant.IntentKey.USER_NAME, hotCommentAdapter.getData().get(position).reply_name);
                CreateCommentFragment.getInstance(bundle)
                        .show(getSupportFragmentManager(), TimeUtil.getStringMs());
            }

            if (view.getId() == R.id.item_post_comment_support_button) {
                postDetailPresenter.support(postDetailBean.topic.topic_id,
                        hotCommentAdapter.getData().get(position).reply_posts_id,
                        "post", "support", position, this);
            }

            if (view.getId() == R.id.item_post_comment_author_avatar) {
                Intent intent = new Intent(this, UserDetailActivity.class);
                intent.putExtra(Constant.IntentKey.USER_ID, hotCommentAdapter.getData().get(position).reply_id);
                startActivity(intent);
            }
            if (view.getId() == R.id.item_post_comment_buchong_button) {
                onAppendPost(hotCommentAdapter.getData().get(position).reply_posts_id, topicId);
            }
            if (view.getId() == R.id.item_post_comment_more_button) {
                postDetailPresenter.moreReplyOptionsDialog(this, formHash, postDetailBean.boardId,
                        topicId, postDetailBean.topic.user_id, hotCommentAdapter.getData().get(position));
            }
        });

        hotCommentAdapter.setOnItemChildLongClickListener((adapter, view, position) -> {
            if (view.getId() == R.id.item_post_comment_root_rl) {
                postDetailPresenter.moreReplyOptionsDialog(this, formHash, postDetailBean.boardId,
                        topicId, postDetailBean.topic.user_id, hotCommentAdapter.getData().get(position));
            }
            return false;
        });
    }

    @Override
    protected void onClickListener(View view) {

        if (view.getId() == R.id.post_detail_create_comment_layout) {
            Bundle bundle = new Bundle();
            bundle.putInt(Constant.IntentKey.BOARD_ID, postDetailBean.boardId);
            bundle.putInt(Constant.IntentKey.TOPIC_ID, postDetailBean.topic.topic_id);
            bundle.putInt(Constant.IntentKey.QUOTE_ID, 0);
            bundle.putBoolean(Constant.IntentKey.IS_QUOTE, false);
            bundle.putString(Constant.IntentKey.USER_NAME, postDetailBean.topic.user_nick_name);
            CreateCommentFragment.getInstance(bundle).show(getSupportFragmentManager(), TimeUtil.getStringMs());
        }

        if (view.getId() == R.id.post_detail_create_dianping_layout){
            Bundle bundle = new Bundle();
            bundle.putInt(Constant.IntentKey.POST_ID, postDetailBean.topic.reply_posts_id);
            bundle.putInt(Constant.IntentKey.TOPIC_ID, topicId);
            bundle.putString(Constant.IntentKey.TYPE, PostAppendFragment.DIANPING);
            PostAppendFragment.getInstance(bundle).show(getSupportFragmentManager(), TimeUtil.getStringMs());
        }

        if (view.getId() == R.id.post_detail_favorite_btn) {
            showSnackBar(coordinatorLayout, "操作中，请稍候...");
            postDetailPresenter.favorite("tid", postDetailBean.topic.is_favor == 1 ? "delfavorite" : "favorite", postDetailBean.topic.topic_id, this);
        }

        if (view.getId() == R.id.post_detail_support_btn) {
            postDetailPresenter.support(postDetailBean.topic.topic_id, postDetailBean.topic.reply_posts_id, "thread", "support", 0, this);
        }

        if (view.getId() == R.id.post_detail_time_order_btn) {
            recyclerView.scrollToPosition(0);
            order = order == 1 ? 0 : 1;
            refreshLayout.autoRefresh(0 , 300, 1, false);
            showSnackBar(coordinatorLayout, order == 1 ? "按时间倒序浏览" : "按时间正序浏览");
        }

        if (view.getId() == R.id.post_detail_watch_author_only_btn) {
            recyclerView.scrollToPosition(0);
            authorId = authorId == 0 ? postDetailBean.topic.user_id : 0;
            refreshLayout.autoRefresh(0 , 300, 1, false);
            authorOnlyBtn.setImageResource(authorId == 0 ? R.drawable.ic_person : R.drawable.ic_person_fill);
            showSnackBar(coordinatorLayout, authorId == 0 ? "全部评论" : "只看楼主");
        }

        if (view.getId() == R.id.post_detail_up_btn) {
            recyclerView.scrollToPosition(0);
        }

        if (view.getId() == R.id.post_detail_item_content_view_author_avatar) {
            Intent intent = new Intent(this, UserDetailActivity.class);
            intent.putExtra(Constant.IntentKey.USER_ID, postDetailBean.topic.user_id);
            startActivity(intent);
        }

        if (view.getId() == R.id.post_detail_shang_btn) {
            onPingFen(postDetailBean.topic.reply_posts_id);
        }

        if (view.getId() == R.id.post_detail_buchong_btn) {
            Bundle bundle = new Bundle();
            bundle.putInt(Constant.IntentKey.POST_ID, postDetailBean.topic.reply_posts_id);
            bundle.putInt(Constant.IntentKey.TOPIC_ID, topicId);
            bundle.putString(Constant.IntentKey.TYPE, PostAppendFragment.APPEND);
            PostAppendFragment.getInstance(bundle).show(getSupportFragmentManager(), TimeUtil.getStringMs());
        }

        if (view.getId() == R.id.post_detail_item_dianping_view_last_page) {
            dianPingPage = dianPingPage - 1;
            postDetailPresenter.getDianPingList(topicId, postDetailBean.topic.reply_posts_id, dianPingPage);
        }

        if (view.getId() == R.id.post_detail_item_dianping_view_next_page) {
            dianPingPage = dianPingPage + 1;
            postDetailPresenter.getDianPingList(topicId, postDetailBean.topic.reply_posts_id, dianPingPage
            );
        }
    }

    @Override
    protected void setOnRefreshListener() {
        RefreshUtil.setOnRefreshListener(this, refreshLayout, new OnRefresh() {
            @Override
            public void onRefresh(RefreshLayout refreshLayout) {
                page = 1;
                postDetailPresenter.getAllComment(order, topicId, authorId, PostDetailActivity.this);
                postDetailPresenter.getPostDetail(page, SharePrefUtil.getPageSize(PostDetailActivity.this), order, topicId, authorId, PostDetailActivity.this);
            }

            @Override
            public void onLoadMore(RefreshLayout refreshLayout) {
                postDetailPresenter.getPostDetail(page, SharePrefUtil.getPageSize(PostDetailActivity.this), order, topicId, authorId, PostDetailActivity.this);
            }
        });
    }

    @Override
    public void onGetPostDetailSuccess(PostDetailBean postDetailBean) {

        if (CommonUtil.contains(Constant.SECURE_BOARD_ID, postDetailBean.boardId)) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_SECURE);
        }

        page = page + 1;
        hint.setText("");
        commentViewTitle.setText(new StringBuilder().append("•评论列表(").append(postDetailBean.total_num).append(")•"));
        optionsLl.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.VISIBLE);
        loading.setVisibility(View.GONE);
        refreshLayout.setEnableRefresh(true);

        if (postDetailBean.has_next == 1) {
            refreshLayout.finishRefresh();
            refreshLayout.finishLoadMore(true);
        } else {
            refreshLayout.finishRefreshWithNoMoreData();
            refreshLayout.finishLoadMoreWithNoMoreData();
        }

        if (postDetailBean.page == 1) {
            this.postDetailBean = postDetailBean;
            topicUserId = postDetailBean.topic.user_id;
            recyclerView.scheduleLayoutAnimation();
            postDetailPresenter.setBasicData(this, basicView, postDetailBean);
            postDetailPresenter.setZanView(this, zanListView, postDetailBean);
            postDetailPresenter.setRateData(this, rateView, postDetailBean);
            postDetailPresenter.saveHistory(postDetailBean);
            postDetailPresenter.getPostWebDetail(topicId, 1);
            commentAdapter.setAuthorId(postDetailBean.topic.user_id);
            commentAdapter.addData(postDetailBean.list, true);
            postDetailPresenter.getDianPingList(topicId, postDetailBean.topic.reply_posts_id, dianPingPage);
            favoriteBtn.setImageResource(postDetailBean.topic.is_favor == 1 ? R.drawable.ic_post_detail_favorite : R.drawable.ic_post_detail_not_favorite);
            shangBtn.setVisibility(postDetailBean.topic.user_id == SharePrefUtil.getUid(this) ? View.GONE : View.VISIBLE);
            buchongBtn.setVisibility(postDetailBean.topic.user_id == SharePrefUtil.getUid(this) ? View.VISIBLE : View.GONE);
            if (postDetailBean.topic != null && postDetailBean.topic.essence == 1) {
                ((ImageView)basicView.findViewById(R.id.post_detail_item_content_view_stamp_img)).setImageDrawable(getResources().getDrawable(R.drawable.pic_essence));
            }
        } else {
            commentAdapter.addData(postDetailBean.list, false);
        }

        commentView.setVisibility(postDetailBean.list.size() == 0 ? View.GONE : View.VISIBLE);
    }

    @Override
    public void onGetPostDetailError(String msg, int code) {
        loading.setVisibility(View.GONE);

        refreshLayout.finishRefresh();
        refreshLayout.finishLoadMore(false);
        hint.setText(msg);

        if (!TextUtils.isEmpty(msg) && msg.contains(ApiConstant.Code.RESPONSE_ERROR_500)){
            Intent intent = new Intent(this, WebViewActivity.class);
            intent.putExtra(Constant.IntentKey.URL, ApiConstant.Post.TOPIC_URL + topicId);
            startActivity(intent);
            finish();
        }
    }

    @Override
    public void onSupportSuccess(SupportResultBean supportResultBean, String action, int position) {
        if (action.equals("support")) {
            showSnackBar(coordinatorLayout, supportResultBean.head.errInfo);
        } else {
            showSnackBar(coordinatorLayout, "赞-1");
        }
    }

    @Override
    public void onSupportError(String msg) {
        showSnackBar(coordinatorLayout, msg);
    }

    @Override
    public void onFavoritePostSuccess(FavoritePostResultBean favoritePostResultBean) {
        if (postDetailBean.topic.is_favor == 1) {
            showSnackBar(coordinatorLayout, "取消收藏成功");
            favoriteBtn.setImageResource(R.drawable.ic_post_detail_not_favorite);
            postDetailBean.topic.is_favor = 0;
        } else {
            showSnackBar(coordinatorLayout,"收藏成功" );
            favoriteBtn.setImageResource(R.drawable.ic_post_detail_favorite);
            postDetailBean.topic.is_favor = 1;
        }

    }

    @Override
    public void onFavoritePostError(String msg) {
        showSnackBar(coordinatorLayout, msg);
    }

    @Override
    public void onVoteSuccess(VoteResultBean voteResultBean) {
        showSnackBar(coordinatorLayout, voteResultBean.head.errInfo);
        //投票成功后更新结果
        postDetailPresenter.getVoteData(topicId, this);
    }

    @Override
    public void onVoteError(String msg) {
        showSnackBar(coordinatorLayout, msg);
    }


    @Override
    public void onReportSuccess(ReportBean reportBean) {
        showSnackBar(coordinatorLayout, reportBean.head.errInfo);
    }

    @Override
    public void onReportError(String msg) {
        showSnackBar(coordinatorLayout, msg);
    }

    @Override
    public void onGetNewVoteDataSuccess(PostDetailBean.TopicBean.PollInfoBean pollInfoBean) {
        if (pollInfoBean != null) {
            contentView.setVoteBean(pollInfoBean);
            contentView.insertPollView(true);
        }
    }

    @Override
    public void onGetPostDianPingListSuccess(List<PostDianPingBean> commentBeans, boolean hasNext) {
        if (commentBeans == null || commentBeans.size() == 0) {
            dianPingView.setVisibility(View.GONE);
        } else {
            dianPingView.setVisibility(View.VISIBLE);
            hint.setVisibility(View.GONE);
            dianPingLoading.setVisibility(View.GONE);
            postDianPingAdapter.addData(commentBeans, true);
            dianPingLastPage.setVisibility(dianPingPage == 1 ? View.GONE : View.VISIBLE);
            dianPingNextPage.setVisibility(hasNext ? View.VISIBLE : View.GONE);
        }
    }

    @Override
    public void onGetPostDianPingListError(String msg) {
        dianPingLoading.setVisibility(View.GONE);
        dianPingHint.setVisibility(View.VISIBLE);
        dianPingHint.setText(msg);
    }

    @Override
    public void onStickReplySuccess(String msg) {
        showSnackBar(coordinatorLayout, msg);
        recyclerView.scrollToPosition(0);
        refreshLayout.autoRefresh(0 , 300, 1, false);
    }

    @Override
    public void onStickReplyError(String msg) {
        showSnackBar(coordinatorLayout, msg);
    }

    @Override
    public void onGetPostWebDetailSuccess(String favoriteNum, String rewordInfo, String shengYuReword,String formHash, boolean originalCreate, boolean essence) {
        if (formHash != null) this.formHash = formHash;
        if (!TextUtils.isEmpty(favoriteNum) && !"0".endsWith(favoriteNum)) {
            favoriteLayout.setVisibility(View.VISIBLE);
            favoriteNumTextView.setText(String.format("收藏%s", favoriteNum));
        }
        if (rewordInfo != null && rewordInfo.length() != 0 && shengYuReword != null) {
            rewordInfoTv.setVisibility(View.VISIBLE);
            if (shengYuReword.contains("水滴")) {
                rewordInfoTv.setText(String.format("[剩余%s]%s", shengYuReword, rewordInfo));
            } else {
                rewordInfoTv.setText(rewordInfo);
            }

            SpannableString spannableString = new SpannableString("查看中奖记录");
            MyClickableSpan clickableSpan = new MyClickableSpan(this, Constant.CREDIT_HISTORY_LINK);
            spannableString.setSpan(clickableSpan, 0, spannableString.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            rewordInfoTv.setMovementMethod(LinkMovementMethod.getInstance());
            rewordInfoTv.append(spannableString);
        } else {
            rewordInfoTv.setVisibility(View.GONE);
        }

        ImageView stamp = basicView.findViewById(R.id.post_detail_item_content_view_stamp_img);
        if (originalCreate) {
            stamp.setImageDrawable(getResources().getDrawable(R.drawable.pic_original_create));
        } else if (essence) {
            stamp.setImageDrawable(getResources().getDrawable(R.drawable.pic_essence));
        }

    }

    @Override
    public void onPingFen(int pid) {
        Bundle bundle = new Bundle();
        bundle.putInt(Constant.IntentKey.TOPIC_ID, topicId);
        bundle.putInt(Constant.IntentKey.POST_ID, pid);
        PostRateFragment.getInstance(bundle).show(getSupportFragmentManager(), TimeUtil.getStringMs());
    }

    @Override
    public void onOnlyReplyAuthor(int uid) {
//        recyclerView.scrollToPosition(0);
//        authorId = uid;
//        refreshLayout.autoRefresh(0 , 300, 1, false);
//        showSnackBar(coordinatorLayout, "只看Ta");
    }

    @Override
    public void onAppendPost(int replyPostsId, int tid) {
        Bundle bundle = new Bundle();
        bundle.putInt(Constant.IntentKey.POST_ID, replyPostsId);
        bundle.putInt(Constant.IntentKey.TOPIC_ID, tid);
        bundle.putString(Constant.IntentKey.TYPE, PostAppendFragment.APPEND);
        PostAppendFragment.getInstance(bundle).show(getSupportFragmentManager(), TimeUtil.getStringMs());
    }

    @Override
    public void onDeletePost(int tid, int pid) {
        Bundle bundle = new Bundle();
        bundle.putInt(Constant.IntentKey.POST_ID, pid);
        bundle.putInt(Constant.IntentKey.TOPIC_ID, tid);
        bundle.putString(Constant.IntentKey.TYPE, PostAppendFragment.APPEND);
        UseRegretMagicFragment.getInstance(bundle).show(getSupportFragmentManager(), TimeUtil.getStringMs());
    }

    @Override
    public void onGetAllPostSuccess(PostDetailBean postDetailBean) {
        hotCommentRv.setLayoutManager(new MyLinearLayoutManger(this));
        hotCommentRv.setAdapter(hotCommentAdapter);
        hotCommentAdapter.setAuthorId(postDetailBean.topic.user_id);

        if (postDetailPresenter.getHotComment(postDetailBean).size() == 0) {
            hotCommentView.setVisibility(View.GONE);
        } else {
            hotCommentView.setVisibility(View.VISIBLE);
            hotCommentAdapter.addData(postDetailPresenter.getHotComment(postDetailBean), true);
        }
    }

    @Override
    public void onGetAllPostError(String msg) {
        hotCommentView.setVisibility(View.GONE);
    }

    @Override
    protected int setMenuResourceId() {
        return R.menu.menu_post_detail;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        menu.findItem(R.id.menu_post_detail_delete).setVisible(topicUserId == SharePrefUtil.getUid(this));
        menu.findItem(R.id.menu_post_detail_modify_post).setVisible(topicUserId == SharePrefUtil.getUid(this));
        menu.findItem(R.id.menu_post_detail_report_thread).setVisible(topicUserId != SharePrefUtil.getUid(this));
        menu.findItem(R.id.menu_post_detail_against).setVisible(topicUserId != SharePrefUtil.getUid(this));

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    protected void onOptionsSelected(MenuItem item) {
        super.onOptionsSelected(item);
        if (postDetailBean != null) {
            if (item.getItemId() == R.id.menu_post_detail_report_thread) {
                postDetailPresenter.showReportDialog(this, postDetailBean.topic.topic_id, "thread");
            }
            if (item.getItemId() == R.id.menu_post_detail_share_post) {
                String title = getResources().getString(R.string.share_title, postDetailBean.topic.title);
                String content = getResources().getString(R.string.share_content,
                        postDetailBean.topic.title, postDetailBean.forumTopicUrl);
                CommonUtil.share(this, title, content);
            }
            if (item.getItemId() == R.id.menu_post_detail_copy_link) {
                showSnackBar(coordinatorLayout, CommonUtil.clipToClipBoard(this, postDetailBean.forumTopicUrl) ? "复制链接成功" : "复制链接失败，请检查是否拥有剪切板权限");
            }
//            if (item.getItemId() == R.id.menu_post_detail_admin_action) {
//                postDetailPresenter.showAdminDialog(this, postDetailBean.boardId, postDetailBean.topic.topic_id, postDetailBean.topic.reply_posts_id);
//            }
            if (item.getItemId() == R.id.menu_post_detail_delete) {
                onDeletePost(topicId, postDetailBean.topic.reply_posts_id);
            }
            if (item.getItemId() == R.id.menu_post_detail_against) {
                postDetailPresenter.support(topicId,
                        postDetailBean.topic.reply_posts_id,
                        "thread", "against", 0, this);
            }
            if (item.getItemId() == R.id.menu_post_detail_modify_post) {
                Intent intent = new Intent(this, WebViewActivity.class);
                intent.putExtra(Constant.IntentKey.URL, "https://bbs.uestc.edu.cn/forum.php?mod=post&action=edit&tid=" + topicId + "&pid=" + postDetailBean.topic.reply_posts_id);
                startActivity(intent);
            }
        }
        if (item.getItemId() == R.id.menu_post_detail_open_link) {
            CommonUtil.openBrowser(this, "http://bbs.uestc.edu.cn/forum.php?mod=viewthread&tid=" + topicId);
        }

    }

    @Override
    protected boolean registerEventBus() {
        return true;
    }

    @Override
    protected void receiveEventBusMsg(BaseEvent baseEvent) {
        if (baseEvent.eventCode == BaseEvent.EventCode.SEND_COMMENT_SUCCESS) {//发表评论成功
            recyclerView.scrollToPosition(0);
            order = 1;
            refreshLayout.autoRefresh(0 , 300, 1, false);
        }
        if (baseEvent.eventCode == BaseEvent.EventCode.USE_MAGIC_SUCCESS) {
            recyclerView.scrollToPosition(0);
            refreshLayout.autoRefresh(0 , 300, 1, false);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
