package com.scatl.uestcbbs.module.post.view.postdetail2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.scatl.uestcbbs.R;
import com.scatl.uestcbbs.base.BaseActivity;
import com.scatl.uestcbbs.base.BaseIndicatorAdapter;
import com.scatl.uestcbbs.base.BasePresenter;
import com.scatl.uestcbbs.custom.imageview.CircleImageView;
import com.scatl.uestcbbs.custom.postview.ContentView;
import com.scatl.uestcbbs.entity.ContentViewBean;
import com.scatl.uestcbbs.entity.PostDetailBean;
import com.scatl.uestcbbs.entity.PostDianPingBean;
import com.scatl.uestcbbs.entity.ReportBean;
import com.scatl.uestcbbs.entity.SupportResultBean;
import com.scatl.uestcbbs.entity.VoteResultBean;
import com.scatl.uestcbbs.module.post.adapter.PostDetail2ViewPagerAdapter;
import com.scatl.uestcbbs.module.post.presenter.postdetail2.PostDetail3Presenter;
import com.scatl.uestcbbs.util.CommonUtil;
import com.scatl.uestcbbs.util.Constant;
import com.scatl.uestcbbs.util.ForumUtil;
import com.scatl.uestcbbs.util.JsonUtil;
import com.scatl.uestcbbs.util.TimeUtil;

import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.ViewPagerHelper;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PostDetail3Activity extends BaseActivity implements PostDetail3View{

    MagicIndicator magicIndicator;
    ViewPager viewPager;
    ContentView contentView;
    CoordinatorLayout coordinatorLayout;
    Toolbar toolbar;
    PostDetail3Presenter postDetail3Presenter;

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
        return R.layout.activity_post_detail3;
    }

    @Override
    protected void findView() {
        magicIndicator = findViewById(R.id.post_detail3_indicator);
        viewPager = findViewById(R.id.post_detail3_viewpager);
        contentView = findViewById(R.id.post_detail3_content);
        coordinatorLayout = findViewById(R.id.post_detail3_coor_layout);
        toolbar = findViewById(R.id.post_detail3_toolbar);
    }

    @Override
    protected void initView() {
        postDetail3Presenter = (PostDetail3Presenter) presenter;

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setTitle("帖子详情");

        postDetail3Presenter.getPostDetail(1, 0, 0, topicId, 0, this);

    }

    @Override
    protected BasePresenter initPresenter() {
        return new PostDetail3Presenter();
    }

    @Override
    public void onGetPostDetailSuccess(PostDetailBean postDetailBean) {
        this.postDetailBean = postDetailBean;
        postDetail3Presenter.getPostWebDetail(topicId, 1);
        postDetail3Presenter.getDianPingList(topicId, postDetailBean.topic.reply_posts_id, 1);

        topicUserId = postDetailBean.topic.user_id;

        if (CommonUtil.contains(Constant.SECURE_BOARD_ID, postDetailBean.boardId)) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_SECURE);
        }

//        hint.setText("");
//        loading.setVisibility(View.GONE);
//        bottomLayout.setVisibility(View.VISIBLE);
        coordinatorLayout.setVisibility(View.VISIBLE);

        viewPager.setOffscreenPageLimit(4);
        viewPager.setAdapter(new PostDetail2ViewPagerAdapter(getSupportFragmentManager(),
                FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT, topicId, postDetailBean.topic.reply_posts_id, formHash));

        //int daShangUserCount = postDetailBean.topic.reward == null
        String[] titles = {"热评","评论(" + postDetailBean.total_num + ")", "点评", "点赞", "打赏"};
        CommonNavigator commonNavigator = new CommonNavigator(this);
        commonNavigator.setAdapter(new BaseIndicatorAdapter(titles, 16, viewPager));
        magicIndicator.setNavigator(commonNavigator);
        ViewPagerHelper.bind(magicIndicator, viewPager);
        viewPager.setCurrentItem(1);
        magicIndicator.getNavigator().onPageSelected(1);

        CircleImageView userAvatar = findViewById(R.id.post_detail3_author_avatar);
        TextView postTitle = findViewById(R.id.post_detail3_title);
        TextView userName = findViewById(R.id.post_detail3_author_name);
        TextView userLevel = findViewById(R.id.post_detail3_author_level);
        TextView time = findViewById(R.id.post_detail3_time);
        TextView mobileSign = findViewById(R.id.post_detail3_mobile_sign);

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

    }

    @Override
    public void onVoteSuccess(VoteResultBean voteResultBean) {

    }

    @Override
    public void onVoteError(String msg) {

    }

    @Override
    public void onGetNewVoteDataSuccess(PostDetailBean.TopicBean.PollInfoBean pollInfoBean) {

    }

    @Override
    public void onGetPostWebDetailSuccess(String favoriteNum, String rewardInfo, String shengYuReword, String formHash, boolean originalCreate, boolean essence) {

    }

    @Override
    public void onGetAllPostSuccess(PostDetailBean postDetailBean) {

    }

    @Override
    public void onGetAllPostError(String msg) {

    }

    @Override
    public void onGetPostDianPingListSuccess(List<PostDianPingBean> commentBeans, boolean hasNext) {

    }

    @Override
    public void onGetPostDianPingListError(String msg) {

    }

    @Override
    public void onAppendPost(int replyPostsId, int tid) {

    }

    @Override
    public void onSupportSuccess(SupportResultBean supportResultBean, String action, int position) {

    }

    @Override
    public void onSupportError(String msg) {

    }

    @Override
    public void onPingFen(int pid) {

    }

    @Override
    public void onOnlyReplyAuthor(int uid) {

    }

    @Override
    public void onReportSuccess(ReportBean reportBean) {

    }

    @Override
    public void onReportError(String msg) {

    }

    @Override
    public void onDeletePost(int tid, int pid) {

    }

    @Override
    public void onStickReplySuccess(String msg) {

    }

    @Override
    public void onStickReplyError(String msg) {

    }
}