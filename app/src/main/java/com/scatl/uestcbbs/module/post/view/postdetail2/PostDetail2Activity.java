package com.scatl.uestcbbs.module.post.view.postdetail2;

import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.scatl.uestcbbs.R;
import com.scatl.uestcbbs.api.ApiConstant;
import com.scatl.uestcbbs.base.BaseActivity;
import com.scatl.uestcbbs.base.BaseIndicatorAdapter;
import com.scatl.uestcbbs.base.BasePresenter;
import com.scatl.uestcbbs.custom.MyLinearLayoutManger;
import com.scatl.uestcbbs.custom.imageview.CircleImageView;
import com.scatl.uestcbbs.custom.postview.ContentView;
import com.scatl.uestcbbs.custom.postview.MyClickableSpan;
import com.scatl.uestcbbs.entity.ContentViewBean;
import com.scatl.uestcbbs.entity.PostDetailBean;
import com.scatl.uestcbbs.entity.PostDianPingBean;
import com.scatl.uestcbbs.entity.ReportBean;
import com.scatl.uestcbbs.entity.SupportResultBean;
import com.scatl.uestcbbs.entity.VoteResultBean;
import com.scatl.uestcbbs.module.magic.view.UseRegretMagicFragment;
import com.scatl.uestcbbs.module.post.adapter.PostCommentAdapter;
import com.scatl.uestcbbs.module.post.adapter.PostDetail2ViewPagerAdapter;
import com.scatl.uestcbbs.module.post.adapter.PostDianPingAdapter;
import com.scatl.uestcbbs.module.post.presenter.postdetail2.PostDetail2Presenter;
import com.scatl.uestcbbs.module.post.view.CreateCommentFragment;
import com.scatl.uestcbbs.module.post.view.PostAppendFragment;
import com.scatl.uestcbbs.module.post.view.PostRateFragment;
import com.scatl.uestcbbs.module.post.view.ViewVoterFragment;
import com.scatl.uestcbbs.module.user.view.UserDetailActivity;
import com.scatl.uestcbbs.module.webview.view.WebViewActivity;
import com.scatl.uestcbbs.util.CommonUtil;
import com.scatl.uestcbbs.util.Constant;
import com.scatl.uestcbbs.util.ForumUtil;
import com.scatl.uestcbbs.util.JsonUtil;
import com.scatl.uestcbbs.util.SharePrefUtil;
import com.scatl.uestcbbs.util.TimeUtil;

import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.ViewPagerHelper;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 新版帖子详情样式，这里只包括帖子详情。评论、点评、点赞等以bottomsheet形式展示
 */
public class PostDetail2Activity extends BaseActivity implements PostDetail2View{

    Toolbar toolbar;
    TextView hint;
    LottieAnimationView loading;
    ContentView contentView;
    CoordinatorLayout coordinatorLayout;
    View bottomLayout, hotCommentCard, dianPingCard, viewMoreCommentBtn, viewMoreDianPingBtn;
    View favoriteLayout;
    TextView favoriteNumTextView, rewordInfoTv;
    MagicIndicator magicIndicator;
    ViewPager viewPager;
    RecyclerView hotCommentRv, dianPingRv;
    PostCommentAdapter hotCommentAdapter;
    PostDianPingAdapter postDianPingAdapter;

    PostDetail2Presenter postDetail2Presenter;

    BottomSheetBehavior bottomSheetBehavior;

    PostDetailBean postDetailBean;

    private int topicId, topicUserId;//楼主id
    private String formHash;

    @Override
    protected void getIntent(Intent intent) {
        super.getIntent(intent);
        topicId = intent.getIntExtra(Constant.IntentKey.TOPIC_ID, Integer.MAX_VALUE);
    }

    @Override
    protected int setLayoutResourceId() {
        return R.layout.activity_post_detail2;
    }

    @Override
    protected void findView() {
        toolbar = findViewById(R.id.post_detail2_toolbar);
        hint = findViewById(R.id.post_detail2_hint);
        loading = findViewById(R.id.post_detail2_loading);
        magicIndicator = findViewById(R.id.post_detail2_indicator);
        viewPager = findViewById(R.id.post_detail2_viewpager);
        contentView = findViewById(R.id.post_detail2_content);
        coordinatorLayout = findViewById(R.id.post_detail2_coor_layout);
        favoriteLayout = findViewById(R.id.post_detail2_favorite_layout);
        favoriteNumTextView = findViewById(R.id.post_detail2_favorite_num);
        rewordInfoTv = findViewById(R.id.post_detail2_reword_info);
        bottomLayout = findViewById(R.id.post_detail2_bottom_layout);
        hotCommentRv = findViewById(R.id.post_detail2_hot_comment_rv);
        hotCommentCard = findViewById(R.id.post_detail2_hot_comment_card);
        viewMoreCommentBtn = findViewById(R.id.post_detail2_view_more_comment_btn);
        dianPingCard = findViewById(R.id.post_detail2_dianping_card);
        dianPingRv = findViewById(R.id.post_detail2_dianping_rv);
        viewMoreDianPingBtn = findViewById(R.id.post_detail2_view_more_dianping_btn);
    }

    @Override
    protected void initView() {
        postDetail2Presenter = (PostDetail2Presenter) presenter;

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setTitle("帖子详情");

        ViewGroup.LayoutParams layoutParams = bottomLayout.getLayoutParams();
        layoutParams.height = (int) (getResources().getDisplayMetrics().heightPixels * .90);
        bottomLayout.setLayoutParams(layoutParams);
        bottomLayout.setVisibility(View.GONE);

        bottomSheetBehavior = BottomSheetBehavior.from(bottomLayout);

        hotCommentAdapter = new PostCommentAdapter(R.layout.item_post_comment);

        viewMoreCommentBtn.setOnClickListener(this);
        viewMoreDianPingBtn.setOnClickListener(this::onClickListener);

        postDetail2Presenter.getPostDetail(1, 0, 0, topicId, 0, this);
        postDetail2Presenter.getAllComment(topicId,  this);
    }

    @Override
    protected BasePresenter initPresenter() {
        return new PostDetail2Presenter();
    }

    @Override
    protected void onClickListener(View view) {
        if (view.getId() == R.id.post_detail2_view_more_comment_btn) {
            if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                viewPager.setCurrentItem(0);
            }
        }

        if (view.getId() == R.id.post_detail2_view_more_dianping_btn) {
            if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                viewPager.setCurrentItem(1);
            }
        }
    }

    @Override
    protected void setOnItemClickListener() {

        //投票按钮点击
        contentView.setOnPollBtnClickListener(ids -> postDetail2Presenter.vote(postDetailBean.topic.topic_id,
                postDetailBean.boardId, contentView.getVoteBean().type, ids, this));

        //查看公开投票人
        contentView.setOnViewVoterClickListener(() -> {
            Bundle bundle = new Bundle();
            bundle.putInt(Constant.IntentKey.TOPIC_ID, topicId);
            ViewVoterFragment.getInstance(bundle).show(getSupportFragmentManager(), TimeUtil.getStringMs());
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
                postDetail2Presenter.support(postDetailBean.topic.topic_id,
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
                postDetail2Presenter.moreReplyOptionsDialog(this, formHash, postDetailBean.boardId,
                        topicId, postDetailBean.topic.user_id, hotCommentAdapter.getData().get(position));
            }
        });

        hotCommentAdapter.setOnItemChildLongClickListener((adapter, view, position) -> {
            if (view.getId() == R.id.item_post_comment_root_rl) {
                postDetail2Presenter.moreReplyOptionsDialog(this, formHash, postDetailBean.boardId,
                        topicId, postDetailBean.topic.user_id, hotCommentAdapter.getData().get(position));
            }
            return false;
        });
    }

    @Override
    public void onGetPostDetailSuccess(PostDetailBean postDetailBean) {

        this.postDetailBean = postDetailBean;
        postDetail2Presenter.getPostWebDetail(topicId, 1);
        postDetail2Presenter.getDianPingList(topicId, postDetailBean.topic.reply_posts_id, 1);

        topicUserId = postDetailBean.topic.user_id;

        if (CommonUtil.contains(Constant.SECURE_BOARD_ID, postDetailBean.boardId)) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_SECURE);
        }

        hint.setText("");
        loading.setVisibility(View.GONE);
        bottomLayout.setVisibility(View.VISIBLE);

        viewPager.setOffscreenPageLimit(4);
        viewPager.setAdapter(new PostDetail2ViewPagerAdapter(getSupportFragmentManager(),
                FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT, topicId, postDetailBean.topic.reply_posts_id, formHash));
        viewPager.setCurrentItem(0);

        //int daShangUserCount = postDetailBean.topic.reward == null
        String[] titles = {"评论(" + postDetailBean.total_num + ")", "点评", "点赞", "打赏"};
        CommonNavigator commonNavigator = new CommonNavigator(this);
        commonNavigator.setAdapter(new BaseIndicatorAdapter(titles, viewPager));
        magicIndicator.setNavigator(commonNavigator);
        ViewPagerHelper.bind(magicIndicator, viewPager);

        CircleImageView userAvatar = findViewById(R.id.post_detail2_author_avatar);
        TextView postTitle = findViewById(R.id.post_detail2_title);
        TextView userName = findViewById(R.id.post_detail2_author_name);
        TextView userLevel = findViewById(R.id.post_detail2_author_level);
        TextView time = findViewById(R.id.post_detail2_time);
        TextView mobileSign = findViewById(R.id.post_detail2_mobile_sign);

        contentView.setContentData(JsonUtil.modelListA2B(postDetailBean.topic.content, ContentViewBean.class, postDetailBean.topic.content.size()));
        postTitle.setText(postDetailBean.topic.title);
        userName.setText(postDetailBean.topic.user_nick_name);
        time.setText(TimeUtil.formatTime(postDetailBean.topic.create_date, R.string.post_time1, this));
        mobileSign.setText(TextUtils.isEmpty(postDetailBean.topic.mobileSign) ? "来自网页版" : postDetailBean.topic.mobileSign);
        userLevel.setBackgroundResource(R.drawable.shape_common_textview_background_not_clickable);

        if (! this.isFinishing()) {
            Glide.with(this).load(postDetailBean.topic.icon).into(userAvatar);
        }

        if (!TextUtils.isEmpty(postDetailBean.topic.userTitle)) {
            userLevel.setVisibility(View.VISIBLE);
            Matcher matcher = Pattern.compile("(.*?)\\((Lv\\..*)\\)").matcher(postDetailBean.topic.userTitle);
            userLevel.setText(matcher.find() ? (matcher.group(2).contains("禁言") ? "禁言中" : matcher.group(2)) : postDetailBean.topic.userTitle);
            userLevel.setBackgroundTintList(ColorStateList.valueOf(ForumUtil.getLevelColor(postDetailBean.topic.userTitle)));
            userLevel.setBackgroundResource(R.drawable.shape_post_detail_user_level);
        } else {
            userLevel.setVisibility(View.GONE);
        }

        //若是投票帖
        if (postDetailBean.topic.vote == 1) {
            contentView.setVoteBean(postDetailBean.topic.poll_info);
        }

    }

    @Override
    public void onGetPostDetailError(String msg) {
        loading.setVisibility(View.GONE);
        hint.setText(msg);

        if (!TextUtils.isEmpty(msg) && msg.contains(ApiConstant.Code.RESPONSE_ERROR_500)){
            Intent intent = new Intent(this, WebViewActivity.class);
            intent.putExtra(Constant.IntentKey.URL, ApiConstant.Post.TOPIC_URL + topicId);
            startActivity(intent);
            finish();
        }
    }

    @Override
    public void onVoteSuccess(VoteResultBean voteResultBean) {
        showSnackBar(coordinatorLayout, voteResultBean.head.errInfo);
        //投票成功后更新结果
        postDetail2Presenter.getVoteData(topicId, this);
    }

    @Override
    public void onVoteError(String msg) {
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

        ImageView stamp = findViewById(R.id.post_detail2_stamp_img);
        if (originalCreate) {
            stamp.setImageDrawable(getResources().getDrawable(R.drawable.pic_original_create));
        } else if (essence) {
            stamp.setImageDrawable(getResources().getDrawable(R.drawable.pic_essence));
        }
    }

    @Override
    public void onGetAllPostSuccess(PostDetailBean postDetailBean) {
        hotCommentRv.setLayoutManager(new MyLinearLayoutManger(this));
        hotCommentRv.setAdapter(hotCommentAdapter);
        hotCommentAdapter.setAuthorId(postDetailBean.topic.user_id);

        if (postDetail2Presenter.getHotComment(postDetailBean).size() == 0) {
            hotCommentCard.setVisibility(View.GONE);
        } else {
            hotCommentCard.setVisibility(View.VISIBLE);
            hotCommentAdapter.addData(postDetail2Presenter.getHotComment(postDetailBean), true);
        }
    }

    @Override
    public void onGetAllPostError(String msg) {
        hotCommentCard.setVisibility(View.GONE);
    }

    @Override
    public void onGetPostDianPingListSuccess(List<PostDianPingBean> commentBeans, boolean hasNext) {
        postDianPingAdapter = new PostDianPingAdapter(R.layout.item_post_detail_dianping);
        dianPingRv.setLayoutManager(new MyLinearLayoutManger(this));
        dianPingRv.setAdapter(postDianPingAdapter);
        if (commentBeans == null || commentBeans.size() == 0) {
            dianPingCard.setVisibility(View.GONE);
        } else {
            dianPingCard.setVisibility(View.VISIBLE);
            postDianPingAdapter.addData(commentBeans, true);
        }
        viewMoreDianPingBtn.setVisibility(hasNext ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onGetPostDianPingListError(String msg) {
        dianPingCard.setVisibility(View.GONE);
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
    public void onPingFen(int pid) {
        Bundle bundle = new Bundle();
        bundle.putInt(Constant.IntentKey.TOPIC_ID, topicId);
        bundle.putInt(Constant.IntentKey.POST_ID, pid);
        PostRateFragment.getInstance(bundle).show(getSupportFragmentManager(), TimeUtil.getStringMs());
    }

    @Override
    public void onOnlyReplyAuthor(int uid) { }

    @Override
    public void onReportSuccess(ReportBean reportBean) {
        showSnackBar(coordinatorLayout, reportBean.head.errInfo);
    }

    @Override
    public void onReportError(String msg) {
        showSnackBar(coordinatorLayout, msg);
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
    public void onStickReplySuccess(String msg) {
        showSnackBar(coordinatorLayout, msg);
    }

    @Override
    public void onStickReplyError(String msg) {
        showSnackBar(coordinatorLayout, msg);
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
                postDetail2Presenter.showReportDialog(this, postDetailBean.topic.topic_id, "thread");
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
            if (item.getItemId() == R.id.menu_post_detail_delete) {
                onDeletePost(topicId, postDetailBean.topic.reply_posts_id);
            }
            if (item.getItemId() == R.id.menu_post_detail_against) {
                postDetail2Presenter.support(topicId,
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
    public void onBackPressed() {
        if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        } else {
            finish();
        }
    }

}