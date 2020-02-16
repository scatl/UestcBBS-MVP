package com.scatl.uestcbbs.module.post.view;

import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.scatl.uestcbbs.R;
import com.scatl.uestcbbs.api.ApiConstant;
import com.scatl.uestcbbs.base.BaseActivity;
import com.scatl.uestcbbs.base.BasePresenter;
import com.scatl.uestcbbs.callback.OnRefresh;
import com.scatl.uestcbbs.custom.MyLinearLayoutManger;
import com.scatl.uestcbbs.custom.imageview.CircleImageView;
import com.scatl.uestcbbs.custom.postview.ContentView;
import com.scatl.uestcbbs.entity.FavoritePostResultBean;
import com.scatl.uestcbbs.entity.PostDetailBean;
import com.scatl.uestcbbs.entity.SupportResultBean;
import com.scatl.uestcbbs.entity.VoteResultBean;
import com.scatl.uestcbbs.module.post.adapter.PostCommentAdapter;
import com.scatl.uestcbbs.module.post.presenter.PostDetailPresenter;
import com.scatl.uestcbbs.module.user.view.UserDetailActivity;
import com.scatl.uestcbbs.module.webview.view.WebViewActivity;
import com.scatl.uestcbbs.util.Constant;
import com.scatl.uestcbbs.util.RefreshUtil;
import com.scatl.uestcbbs.util.SharePrefUtil;
import com.scatl.uestcbbs.util.TimeUtil;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.RefreshState;

public class PostDetailActivity extends BaseActivity implements PostDetailView{

    private static final String TAG = "PostDetailActivity";

    private CoordinatorLayout coordinatorLayout;
    private Toolbar toolbar;
    private SmartRefreshLayout refreshLayout;
    private RecyclerView recyclerView;
    private PostCommentAdapter commentAdapter;
    private TextView hint;
    private ImageView favoriteBtn, supportBtn, upBtn, timeOrderBtn, authorOnlyBtn, shangBtn;
    private CardView optionsLl; //底部的工具栏，评论，点赞等
    private LinearLayout createCommentLl;

    private View basicView; //基本信息，头像时间等，包括帖子内容
    private CircleImageView userAvatar;
    private ContentView contentView;

    private View zanListView; //表达看法的用户（支持和发对，无法区分）

    private View rateView; //评分

    private View commentView;
    private TextView commentViewTitle;//评论标题

    private PostDetailPresenter postDetailPresenter;
    private PostDetailBean postDetailBean;

    private int topicId;
    private int page = 1, order = 0, authorId = 0;

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
        optionsLl = findViewById(R.id.post_detail_options_layout);
        createCommentLl = findViewById(R.id.post_detail_create_comment_ll);

        basicView = LayoutInflater.from(this).inflate(R.layout.post_detail_item_content_view, new LinearLayout(this));
        userAvatar = basicView.findViewById(R.id.post_detail_item_content_view_author_avatar);
        contentView = basicView.findViewById(R.id.post_detail_item_content_view_content);

        zanListView = LayoutInflater.from(this).inflate(R.layout.post_detail_item_zanlist_view, new LinearLayout(this));

        commentView = LayoutInflater.from(this).inflate(R.layout.post_detail_item_comment_view, new LinearLayout(this));
        commentViewTitle = commentView.findViewById(R.id.post_detail_item_comment_view_title);

        rateView = LayoutInflater.from(this).inflate(R.layout.post_detail_item_rate_view, new LinearLayout(this));
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
        createCommentLl.setOnClickListener(this);
        userAvatar.setOnClickListener(this);

        toolbar.setTitle("帖子详情");
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        commentAdapter = new PostCommentAdapter(R.layout.item_post_comment);
        commentAdapter.addHeaderView(basicView, 0);
        commentAdapter.addHeaderView(zanListView, 1);
        commentAdapter.addHeaderView(rateView, 2);
        commentAdapter.addHeaderView(commentView, 3);

        recyclerView.setLayoutAnimation(AnimationUtils.loadLayoutAnimation(this, R.anim.layout_animation_from_top));
        recyclerView.setLayoutManager(new MyLinearLayoutManger(this));
        recyclerView.setAdapter(commentAdapter);

        recyclerView.setVisibility(View.GONE);
        optionsLl.setVisibility(View.GONE);
        refreshLayout.autoRefresh(0, 300, 1, false);
    }

    @Override
    protected BasePresenter initPresenter() {
        return new PostDetailPresenter();
    }

    @Override
    protected void setOnItemClickListener() {

        //投票按钮点击
        contentView.setOnPollBtnClickListener(ids -> {
            postDetailPresenter.vote(postDetailBean.topic.topic_id,
                    postDetailBean.boardId, contentView.getVoteBean().type, ids, this);
        });

        //回复评论
        commentAdapter.setOnItemChildClickListener((adapter, view, position) -> {
            if (view.getId() == R.id.item_post_comment_reply_button) {
                Bundle bundle = new Bundle();
                bundle.putInt(Constant.IntentKey.BOARD_ID, postDetailBean.boardId);
                bundle.putInt(Constant.IntentKey.TOPIC_ID, postDetailBean.topic.topic_id);
                bundle.putInt(Constant.IntentKey.QUOTE_ID, commentAdapter.getData().get(position).reply_posts_id);
                bundle.putBoolean(Constant.IntentKey.IS_QUOTE, true);
                bundle.putString(Constant.IntentKey.USER_NAME, commentAdapter.getData().get(position).reply_name);
                PostCreateCommentFragment.getInstance(bundle)
                        .show(getSupportFragmentManager(), TimeUtil.getStringMs());
            }

            if (view.getId() == R.id.item_post_comment_support_button) {
                postDetailPresenter.support(postDetailBean.topic.topic_id,
                        commentAdapter.getData().get(position).reply_posts_id,
                        "post", this);
            }
            if (view.getId() == R.id.item_post_comment_author_avatar) {
                Intent intent = new Intent(this, UserDetailActivity.class);
                intent.putExtra(Constant.IntentKey.USER_ID, commentAdapter.getData().get(position).reply_id);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onClickListener(View view) {

        if (view.getId() == R.id.post_detail_create_comment_ll) {
            Bundle bundle = new Bundle();
            bundle.putInt(Constant.IntentKey.BOARD_ID, postDetailBean.boardId);
            bundle.putInt(Constant.IntentKey.TOPIC_ID, postDetailBean.topic.topic_id);
            bundle.putInt(Constant.IntentKey.QUOTE_ID, 0);
            bundle.putBoolean(Constant.IntentKey.IS_QUOTE, false);
            bundle.putString(Constant.IntentKey.USER_NAME, postDetailBean.topic.user_nick_name);
            PostCreateCommentFragment.getInstance(bundle)
                    .show(getSupportFragmentManager(), TimeUtil.getStringMs());
        }

        if (view.getId() == R.id.post_detail_favorite_btn) {
            postDetailPresenter.favorite("tid",
                    postDetailBean.topic.is_favor == 1 ? "delfavorite" : "favorite",
                    postDetailBean.topic.topic_id, this);
        }

        if (view.getId() == R.id.post_detail_support_btn) {
            postDetailPresenter.support(postDetailBean.topic.topic_id,
                    postDetailBean.topic.reply_posts_id,
                    "thread", this);
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
            Intent intent = new Intent(this, WebViewActivity.class);
            intent.putExtra(Constant.IntentKey.URL, "http://bbs.uestc.edu.cn/mobcent/app/web/index.php?r=forum/topicrate&type=view" +
                    "&tid=" + topicId +"&pid=" + postDetailBean.topic.reply_posts_id +
                    "&accessToken=" + SharePrefUtil.getToken(this) +
                    "&accessSecret=" + SharePrefUtil.getSecret(this));
            startActivity(intent);
        }
    }

    @Override
    protected void setOnRefreshListener() {
        RefreshUtil.setOnRefreshListener(this, refreshLayout, new OnRefresh() {
            @Override
            public void onRefresh(RefreshLayout refreshLayout) {
                page = 1;
                postDetailPresenter.getPostDetail(
                        page, ApiConstant.POST_COMMENT_SIZE,
                        order, topicId, authorId,
                        PostDetailActivity.this);
            }

            @Override
            public void onLoadMore(RefreshLayout refreshLayout) {
                postDetailPresenter.getPostDetail(
                        page, ApiConstant.POST_COMMENT_SIZE,
                        order, topicId, authorId,
                        PostDetailActivity.this);
            }
        });
    }


    @Override
    public void onGetPostDetailSuccess(PostDetailBean postDetailBean) {

        page = page + 1;
        hint.setText("");
        commentViewTitle.setText("•评论列表(" + postDetailBean.total_num + ")•");
        optionsLl.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.VISIBLE);

        if (refreshLayout.getState() == RefreshState.Refreshing) {
            if (postDetailBean.has_next == 1) {
                refreshLayout.finishRefresh();
            } else {
                refreshLayout.finishRefreshWithNoMoreData();
            }
        }
        if (refreshLayout.getState() == RefreshState.Loading) {
            if (postDetailBean.has_next == 1) {
                refreshLayout.finishLoadMore(true);
            } else {
                refreshLayout.finishLoadMoreWithNoMoreData();
            }
        }

        if (postDetailBean.page == 1) {
            this.postDetailBean = postDetailBean;
            recyclerView.scheduleLayoutAnimation();
            postDetailPresenter.setBasicData(this, basicView, postDetailBean);
            postDetailPresenter.setZanView(this, zanListView, postDetailBean);
            postDetailPresenter.setRateData(this, rateView, postDetailBean);
            commentAdapter.setAuthorId(postDetailBean.topic.user_id);
            commentAdapter.setNewData(postDetailBean.list);
            favoriteBtn.setImageResource(postDetailBean.topic.is_favor == 1 ? R.drawable.ic_post_detail_favorite :
                    R.drawable.ic_post_detail_not_favorite);
        } else {
            commentAdapter.addData(postDetailBean.list);
        }

        commentView.setVisibility(postDetailBean.list.size() == 0 ? View.GONE : View.VISIBLE);

    }

    @Override
    public void onGetPostDetailError(String msg) {

        if (refreshLayout.getState() == RefreshState.Refreshing) {
            refreshLayout.finishRefresh();
            hint.setText(msg);
        }
        if (refreshLayout.getState() == RefreshState.Loading) {
            refreshLayout.finishLoadMore(false);
        }

        if (!TextUtils.isEmpty(msg) && msg.contains(ApiConstant.Code.RESPONSE_ERROR_500)){
            Intent intent = new Intent(this, WebViewActivity.class);
            intent.putExtra(Constant.IntentKey.URL, ApiConstant.Post.TOPIC_URL + topicId);
            startActivity(intent);
            finish();
        }
    }

    @Override
    public void onSupportSuccess(SupportResultBean supportResultBean) {
        showSnackBar(coordinatorLayout, supportResultBean.head.errInfo);
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
    }

    @Override
    public void onVoteError(String msg) {
        showSnackBar(coordinatorLayout, msg);
    }

}
