package com.scatl.uestcbbs.module.post.view.postdetail2;

import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.os.Handler;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.Glide;
import com.scatl.uestcbbs.R;
import com.scatl.uestcbbs.annotation.PostAppendType;
import com.scatl.uestcbbs.api.ApiConstant;
import com.scatl.uestcbbs.base.BaseActivity;
import com.scatl.uestcbbs.base.BaseEvent;
import com.scatl.uestcbbs.base.BaseIndicatorAdapter;
import com.scatl.uestcbbs.base.BasePresenter;
import com.scatl.uestcbbs.custom.MyLinearLayoutManger;
import com.scatl.uestcbbs.custom.VoteView;
import com.scatl.uestcbbs.custom.imageview.CircleImageView;
import com.scatl.uestcbbs.custom.postview.ContentView;
import com.scatl.uestcbbs.custom.postview.MyClickableSpan;
import com.scatl.uestcbbs.entity.ContentViewBean;
import com.scatl.uestcbbs.entity.PostDetailBean;
import com.scatl.uestcbbs.entity.PostDianPingBean;
import com.scatl.uestcbbs.entity.PostWebBean;
import com.scatl.uestcbbs.entity.ReportBean;
import com.scatl.uestcbbs.entity.SupportResultBean;
import com.scatl.uestcbbs.entity.VoteResultBean;
import com.scatl.uestcbbs.module.magic.view.UseRegretMagicFragment;
import com.scatl.uestcbbs.module.post.adapter.PostCommentAdapter;
import com.scatl.uestcbbs.module.post.adapter.PostDetail2ViewPagerAdapter;
import com.scatl.uestcbbs.module.post.adapter.PostDianPingAdapter;
import com.scatl.uestcbbs.module.post.adapter.PostRateAdapter;
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

import biz.laenger.android.vpbs.BottomSheetUtils;
import biz.laenger.android.vpbs.ViewPagerBottomSheetBehavior;


/**
 * 新版帖子详情样式，这里只包括帖子详情。评论、点评、点赞等以bottomsheet形式展示
 */
public class PostDetail2Activity extends BaseActivity implements PostDetail2View, View.OnScrollChangeListener{

    Toolbar toolbar;
    TextView hint;
    LottieAnimationView loading;
    ContentView contentView;
    CoordinatorLayout coordinatorLayout;
    View bottomLayout, hotCommentCard, dianPingCard, commentCard, viewMoreHotCommentBtn, viewMoreDianPingBtn, viewMoreCommentBtn;
    View favoriteLayout, optionsLayout;
    TextView favoriteNumTextView, rewordInfoTv;
    MagicIndicator magicIndicator;
    NestedScrollView nestedScrollView;
    ViewPager viewPager;
    RecyclerView hotCommentRv, dianPingRv, commentRv;
    PostCommentAdapter hotCommentAdapter, commentAdapter;
    PostDianPingAdapter postDianPingAdapter;
    CircleImageView userAvatar;
    View supportBtn, againstBtn, viewDianZanDetailBtn;
    TextView supportCount, againstCount;
    VoteView voteView;

    PostDetail2Presenter postDetail2Presenter;

    ViewPagerBottomSheetBehavior bottomSheetBehavior;

    PostDetailBean postDetailBean;

    private int topicId, topicUserId;//楼主id
    private String formHash;

    private static String[] titles;

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
        viewMoreHotCommentBtn = findViewById(R.id.post_detail2_view_more_hot_comment_btn);
        dianPingCard = findViewById(R.id.post_detail2_dianping_card);
        dianPingRv = findViewById(R.id.post_detail2_dianping_rv);
        nestedScrollView = findViewById(R.id.post_detail2_scrollview);
        userAvatar = findViewById(R.id.post_detail2_author_avatar);
        viewMoreDianPingBtn = findViewById(R.id.post_detail2_view_more_dianping_btn);
        optionsLayout = findViewById(R.id.post_detail2_options_layout);
        supportBtn = findViewById(R.id.post_detail2_zan_support_btn);
        againstBtn = findViewById(R.id.post_detail2_zan_against_btn);
        viewDianZanDetailBtn = findViewById(R.id.post_detail2_view_dianzan_btn);
        supportCount = findViewById(R.id.post_detail2_zan_support_count);
        againstCount = findViewById(R.id.post_detail2_zan_against_count);
        voteView = findViewById(R.id.post_detail2_zan_vote_view);
        commentRv = findViewById(R.id.post_detail2_comment_rv);
        viewMoreCommentBtn = findViewById(R.id.post_detail2_view_more_comment_btn);
        commentCard = findViewById(R.id.post_detail2_comment_card);
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

        bottomSheetBehavior = ViewPagerBottomSheetBehavior.from(bottomLayout);
        bottomSheetBehavior.setState(ViewPagerBottomSheetBehavior.STATE_HIDDEN);

        hotCommentAdapter = new PostCommentAdapter(R.layout.item_post_comment);
        commentAdapter = new PostCommentAdapter(R.layout.item_post_comment);

        viewMoreHotCommentBtn.setOnClickListener(this);
        viewMoreDianPingBtn.setOnClickListener(this);
        viewMoreCommentBtn.setOnClickListener(this);
        supportBtn.setOnClickListener(this);
        againstBtn.setOnClickListener(this);
        viewDianZanDetailBtn.setOnClickListener(this);
        userAvatar.setOnClickListener(this);
        nestedScrollView.setOnScrollChangeListener(this);

        BottomSheetUtils.setupViewPager(viewPager);

        postDetail2Presenter.getPostDetail(1, 5, 0, topicId, 0, this);
        postDetail2Presenter.getAllComment(topicId,  this);
    }

    @Override
    protected BasePresenter initPresenter() {
        return new PostDetail2Presenter();
    }

    @Override
    protected void onClickListener(View view) {
        if (view.getId() == R.id.post_detail2_view_more_hot_comment_btn) {
            showBottomSheet(0);
        }

        if (view.getId() == R.id.post_detail2_view_more_comment_btn) {
            showBottomSheet(1);
        }

        if (view.getId() == R.id.post_detail2_view_more_dianping_btn) {
            showBottomSheet(2);
        }

        if (view.getId() == R.id.post_detail2_author_avatar) {
            Intent intent = new Intent(this, UserDetailActivity.class);
            intent.putExtra(Constant.IntentKey.USER_ID, postDetailBean.topic.user_id);
            startActivity(intent);
        }

        if (view.getId() == R.id.post_detail2_zan_support_btn) {
            postDetail2Presenter.support(topicId, postDetailBean.topic.reply_posts_id, "thread", "support", 0, this);
        }
        if (view.getId() == R.id.post_detail2_zan_against_btn) {
            postDetail2Presenter.support(topicId, postDetailBean.topic.reply_posts_id, "thread", "against", 0, this);
        }
        if (view.getId() == R.id.post_detail2_view_dianzan_btn) {
            showBottomSheet(3);
        }
    }

    private void showBottomSheet(int position) {
        if (bottomSheetBehavior.getState() == ViewPagerBottomSheetBehavior.STATE_HIDDEN) {
            bottomSheetBehavior.setState(ViewPagerBottomSheetBehavior.STATE_EXPANDED);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    viewPager.setCurrentItem(position, false);
                    magicIndicator.getNavigator().onPageSelected(position);
                }
            }, 100);
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
                postDetail2Presenter.moreReplyOptionsDialog(this, formHash, postDetailBean.boardId, topicId, postDetailBean.topic.user_id, hotCommentAdapter.getData().get(position));
            }
            return false;
        });

        commentAdapter.setOnItemChildClickListener((adapter, view, position) -> {
            if (view.getId() == R.id.item_post_comment_reply_button ||
                    view.getId() == R.id.item_post_comment_root_rl) {
                Bundle bundle = new Bundle();
                bundle.putInt(Constant.IntentKey.BOARD_ID, postDetailBean.boardId);
                bundle.putInt(Constant.IntentKey.TOPIC_ID, postDetailBean.topic.topic_id);
                bundle.putInt(Constant.IntentKey.QUOTE_ID, commentAdapter.getData().get(position).reply_posts_id);
                bundle.putBoolean(Constant.IntentKey.IS_QUOTE, true);
                bundle.putString(Constant.IntentKey.USER_NAME, commentAdapter.getData().get(position).reply_name);
                CreateCommentFragment.getInstance(bundle).show(getSupportFragmentManager(), TimeUtil.getStringMs());
            }

            if (view.getId() == R.id.item_post_comment_support_button) {
                postDetail2Presenter.support(postDetailBean.topic.topic_id, commentAdapter.getData().get(position).reply_posts_id, "post", "support", position, this);
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
                postDetail2Presenter.moreReplyOptionsDialog(this, formHash, postDetailBean.boardId, topicId, postDetailBean.topic.user_id, commentAdapter.getData().get(position));
            }
        });

        commentAdapter.setOnItemChildLongClickListener((adapter, view, position) -> {
            if (view.getId() == R.id.item_post_comment_root_rl) {
                postDetail2Presenter.moreReplyOptionsDialog(this, formHash, postDetailBean.boardId, topicId, postDetailBean.topic.user_id, commentAdapter.getData().get(position));
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

        initViewPager(postDetailBean);
        initBasicData(postDetailBean);
        setRateData(postDetailBean);
        setCommentData(postDetailBean);

        new Handler().postDelayed(() -> {
            hint.setText("");
            loading.setVisibility(View.GONE);
            bottomLayout.setVisibility(View.VISIBLE);
            nestedScrollView.setVisibility(View.VISIBLE);
            optionsLayout.setVisibility(View.VISIBLE);
        }, 50);

    }

    private void initViewPager(PostDetailBean postDetailBean) {
        viewPager.setOffscreenPageLimit(5);
        viewPager.setAdapter(new PostDetail2ViewPagerAdapter(getSupportFragmentManager(),
                FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT, topicId, postDetailBean.topic.reply_posts_id, formHash));
        viewPager.setCurrentItem(0);

        int dianZanCount = postDetailBean.topic.zanList != null ?
                postDetailBean.topic.zanList.size() : 0;
        int daShangCount = (postDetailBean.topic.reward != null && postDetailBean.topic.reward.userList != null)?
                postDetailBean.topic.reward.userList.size() : 0;
        titles = new String[]{"热评", "评论(" + postDetailBean.total_num + ")", "点评", "评价" + (dianZanCount == 0 ? "" : "("+dianZanCount+")"), "评分" + (daShangCount == 0 ? "" : "("+daShangCount+")")};
        CommonNavigator commonNavigator = new CommonNavigator(this);
        commonNavigator.setAdapter(new BaseIndicatorAdapter(titles, 13, viewPager));
        magicIndicator.setNavigator(commonNavigator);
        ViewPagerHelper.bind(magicIndicator, viewPager);
    }

    private void initBasicData(PostDetailBean postDetailBean) {
        userAvatar = findViewById(R.id.post_detail2_author_avatar);
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

    public void setRateData(PostDetailBean postDetailBean) {
        View layout = findViewById(R.id.post_detail2_dashang_card);
        TextView shuidiNum = findViewById(R.id.post_detail2_dashang_shuidi_num);
        LinearLayout shuidiLayout = findViewById(R.id.post_detail2_dashang_shuidi_layout);
        TextView weiwangNum = findViewById(R.id.post_detail2_dashang_weiwang_num);
        LinearLayout weiwangLayout = findViewById(R.id.post_detail2_dashang_weiwang_layout);
        LinearLayout more = findViewById(R.id.post_detail2_dashang_view_more);
        RecyclerView recyclerView = findViewById(R.id.post_detail2_dashang_rv);

        PostRateAdapter postRateAdapter = new PostRateAdapter(R.layout.item_post_rate_user);
        MyLinearLayoutManger myLinearLayoutManger = new MyLinearLayoutManger(this);
        myLinearLayoutManger.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView.setLayoutManager(myLinearLayoutManger);
        recyclerView.setAdapter(postRateAdapter);

        try {
            postRateAdapter.setNewData(postDetailBean.topic.reward.userList);
        } catch (Exception e) {
            e.printStackTrace();
        }


        if (postDetailBean.topic.reward == null) {
            layout.setVisibility(View.GONE);
        } else {
            layout.setVisibility(View.VISIBLE);
            for (int i = 0; i < postDetailBean.topic.reward.score.size(); i ++) {

                if (postDetailBean.topic.reward.score.get(i).info.equals("水滴")) {
                    shuidiLayout.setVisibility(View.VISIBLE);
                    shuidiNum.setText(postDetailBean.topic.reward.score.get(i).value >= 0 ?
                            " +" + postDetailBean.topic.reward.score.get(i).value : " "+postDetailBean.topic.reward.score.get(i).value);
                }
                if (postDetailBean.topic.reward.score.get(i).info.equals("威望")) {
                    weiwangLayout.setVisibility(View.VISIBLE);
                    weiwangNum.setText(postDetailBean.topic.reward.score.get(i).value >= 0 ?
                            " +" + postDetailBean.topic.reward.score.get(i).value : " "+postDetailBean.topic.reward.score.get(i).value);
                }
            }

            more.setOnClickListener(v -> {
                showBottomSheet(4);
            });
        }
    }

    private void setCommentData(PostDetailBean postDetailBean) {
        commentRv.setLayoutManager(new MyLinearLayoutManger(this));
        commentRv.setNestedScrollingEnabled(false);
        commentRv.setAdapter(commentAdapter);
        commentAdapter.setAuthorId(postDetailBean.topic.user_id);

        if (postDetailBean.list.size() == 0) {
            commentCard.setVisibility(View.GONE);
        } else {
            try{
                ((TextView)findViewById(R.id.post_detail2_view_comment_btn_text)).setText("查看全部" + postDetailBean.topic.replies + "条评论>");
                commentCard.setVisibility(View.VISIBLE);
                viewMoreCommentBtn.setVisibility(postDetailBean.list.size() > 3 ? View.VISIBLE : View.GONE);
                commentAdapter.addData(postDetailBean.list.subList(0, Math.min(postDetailBean.list.size(), 3)), true);
                commentRv.setLayoutAnimation(AnimationUtils.loadLayoutAnimation(this, R.anim.layout_animation_scale_in));
                commentRv.scheduleLayoutAnimation();
            } catch (Exception e) {
                e.printStackTrace();
            }
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
    public void onGetPostWebDetailSuccess(PostWebBean postWebBean) {
        if (postWebBean.formHash != null) this.formHash = postWebBean.formHash;
        if (!TextUtils.isEmpty(postWebBean.favoriteNum) && !"0".endsWith(postWebBean.favoriteNum)) {
            favoriteLayout.setVisibility(View.VISIBLE);
            favoriteNumTextView.setText(String.format("收藏%s", postWebBean.favoriteNum));
        }

        voteView.setNum(postWebBean.supportCount, postWebBean.againstCount);
        supportCount.setText(postWebBean.supportCount + " 人");
        againstCount.setText(postWebBean.againstCount + " 人");

        if (postWebBean.rewardInfo != null && postWebBean.rewardInfo.length() != 0 && postWebBean.shengYuReword != null) {
            rewordInfoTv.setVisibility(View.VISIBLE);
            if (postWebBean.shengYuReword.contains("水滴")) {
                rewordInfoTv.setText(String.format("[剩余%s]%s", postWebBean.shengYuReword, postWebBean.rewardInfo));
            } else {
                rewordInfoTv.setText(postWebBean.rewardInfo);
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
        if (postWebBean.originalCreate) {
            stamp.setImageDrawable(getResources().getDrawable(R.drawable.pic_original_create));
        } else if (postWebBean.essence) {
            stamp.setImageDrawable(getResources().getDrawable(R.drawable.pic_essence));
        } else if (postWebBean.topStick) {
            stamp.setImageDrawable(getResources().getDrawable(R.drawable.pic_topstick));
        }
    }

    @Override
    public void onGetAllPostSuccess(PostDetailBean postDetailBean) {
        hotCommentRv.setLayoutManager(new MyLinearLayoutManger(this));
        hotCommentRv.setNestedScrollingEnabled(false);
        hotCommentRv.setAdapter(hotCommentAdapter);
        hotCommentAdapter.setAuthorId(postDetailBean.topic.user_id);

        List<PostDetailBean.ListBean> hots = postDetail2Presenter.getHotComment(postDetailBean);

        if (hots.size() == 0) {
            hotCommentCard.setVisibility(View.GONE);
        } else {
            try{
                titles[0] = "热评(" + hots.size() + ")";
                magicIndicator.getNavigator().notifyDataSetChanged();
                hotCommentCard.setVisibility(View.VISIBLE);

                viewMoreHotCommentBtn.setVisibility(hots.size() > 3 ? View.VISIBLE : View.GONE);
                hotCommentAdapter.addData(hots.subList(0, Math.min(hots.size(), 3)), true);
                hotCommentRv.setLayoutAnimation(AnimationUtils.loadLayoutAnimation(this, R.anim.layout_animation_scale_in));
                hotCommentRv.scheduleLayoutAnimation();
            } catch (Exception e) {

            }
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
        dianPingRv.setNestedScrollingEnabled(false);
        if (commentBeans == null || commentBeans.size() == 0) {
            dianPingCard.setVisibility(View.GONE);
        } else {
            viewMoreDianPingBtn.setVisibility(hasNext ? View.VISIBLE : View.GONE);
            titles[2] = "点评(" + commentBeans.size() + (hasNext ? "+" : "")+ ")";
            magicIndicator.getNavigator().notifyDataSetChanged();
            dianPingCard.setVisibility(View.VISIBLE);
            dianPingRv.setLayoutAnimation(AnimationUtils.loadLayoutAnimation(this, R.anim.layout_animation_scale_in));
            dianPingRv.scheduleLayoutAnimation();
            postDianPingAdapter.addData(commentBeans, true);
        }
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
        bundle.putString(Constant.IntentKey.TYPE, PostAppendType.APPEND);
        PostAppendFragment.getInstance(bundle).show(getSupportFragmentManager(), TimeUtil.getStringMs());
    }

    @Override
    public void onSupportSuccess(SupportResultBean supportResultBean, String action, int position) {
        if (action.equals("support")) {
            showSnackBar(coordinatorLayout, supportResultBean.head.errInfo);
            supportCount.setText((voteView.getLeftNum() + 1) + " 人");
            voteView.setNum(voteView.getLeftNum() + 1, voteView.getRightNum());
        } else {
            showSnackBar(coordinatorLayout, "赞-1");
            againstCount.setText((voteView.getRightNum() - 1) + " 人");
            voteView.setNum(voteView.getLeftNum(), voteView.getRightNum() - 1);
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

            if (item.getItemId() == R.id.menu_post_detail_show_bottom_sheet) {
                if (bottomSheetBehavior.getState() == ViewPagerBottomSheetBehavior.STATE_HIDDEN) {
                    bottomSheetBehavior.setState(ViewPagerBottomSheetBehavior.STATE_EXPANDED);
                } else if (bottomSheetBehavior.getState() == ViewPagerBottomSheetBehavior.STATE_EXPANDED) {
                    bottomSheetBehavior.setState(ViewPagerBottomSheetBehavior.STATE_HIDDEN);
                }
            }
        }
        if (item.getItemId() == R.id.menu_post_detail_open_link) {
            CommonUtil.openBrowser(this, "http://bbs.uestc.edu.cn/forum.php?mod=viewthread&tid=" + topicId);
        }

    }

    @Override
    public void onBackPressed() {
        if (bottomSheetBehavior.getState() == ViewPagerBottomSheetBehavior.STATE_EXPANDED) {
            bottomSheetBehavior.setState(ViewPagerBottomSheetBehavior.STATE_HIDDEN);
        } else {
            finish();
        }
    }

    @Override
    protected boolean registerEventBus() {
        return true;
    }

    @Override
    protected void receiveEventBusMsg(BaseEvent baseEvent) {
//        if (baseEvent.eventCode == BaseEvent.EventCode.VIEW_PAGER_TITLE_CLICK) {
//            if (bottomSheetBehavior.getState() == ViewPagerBottomSheetBehavior.STATE_COLLAPSED) {
//                bottomSheetBehavior.setState(ViewPagerBottomSheetBehavior.STATE_EXPANDED);
//                viewPager.setCurrentItem((int)baseEvent.eventData);
//            }
//        }
    }

    @Override
    public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
        if (scrollY > oldScrollY) {
            if (optionsLayout.getVisibility() == View.VISIBLE){
                optionsLayout.setVisibility(View.GONE);
                optionsLayout.startAnimation(AnimationUtils.loadAnimation(this,R.anim.view_dismiss_y0_y1_no_alpha));
            }
        }
        if (scrollY < oldScrollY){
            if (optionsLayout.getVisibility() == View.GONE){
                optionsLayout.setVisibility(View.VISIBLE);
                optionsLayout.startAnimation(AnimationUtils.loadAnimation(this,R.anim.view_appear_y1_y0_no_alpha));
            }
        }
        if (scrollY == 0) { }
        if (scrollY == ((NestedScrollView)v).getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight()) { }
    }
}