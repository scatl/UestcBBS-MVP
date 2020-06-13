package com.scatl.uestcbbs.module.post.view.postdetail2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.jaeger.library.StatusBarUtil;
import com.scatl.uestcbbs.R;
import com.scatl.uestcbbs.base.BaseActivity;
import com.scatl.uestcbbs.base.BasePresenter;
import com.scatl.uestcbbs.custom.imageview.CircleImageView;
import com.scatl.uestcbbs.custom.postview.ContentView;
import com.scatl.uestcbbs.entity.ContentViewBean;
import com.scatl.uestcbbs.entity.PostDetailBean;
import com.scatl.uestcbbs.module.post.presenter.PostDetailPresenter;
import com.scatl.uestcbbs.module.post.view.PostDetailActivity;
import com.scatl.uestcbbs.util.Constant;
import com.scatl.uestcbbs.util.JsonUtil;
import com.scatl.uestcbbs.util.SharePrefUtil;
import com.scatl.uestcbbs.util.TimeUtil;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PostDetail2Activity extends BaseActivity implements PostDetail2View{

    private Toolbar toolbar;
    private PostDetail2Presenter postDetail2Presenter;

    private int topicId;

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
    }

    @Override
    protected void initView() {
        postDetail2Presenter = (PostDetail2Presenter) presenter;

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setTitle("帖子详情");

        postDetail2Presenter.getPostDetail(1, 0, 0, topicId, 0, this);
    }

    @Override
    protected BasePresenter initPresenter() {
        return new PostDetail2Presenter();
    }

    @Override
    public void onGetPostDetailSuccess(PostDetailBean postDetailBean) {
       // postDetail2Presenter.setBasicData(this, contentView, postDetailBean);
        CircleImageView userAvatar = findViewById(R.id.post_detail2_author_avatar);
        TextView postTitle = findViewById(R.id.post_detail2_title);
        TextView userName = findViewById(R.id.post_detail2_author_name);
        TextView userLevel = findViewById(R.id.post_detail2_author_level);
        TextView time = findViewById(R.id.post_detail2_time);
        TextView mobileSign = findViewById(R.id.post_detail2_mobile_sign);
        ContentView contentView = findViewById(R.id.post_detail2_content);

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
            Matcher matcher = Pattern.compile("(.*?)\\((Lv\\..*)\\)").matcher(postDetailBean.topic.userTitle);
            userLevel.setText(matcher.find() ? matcher.group(2) : postDetailBean.topic.userTitle);
        } else {
            userLevel.setText(postDetailBean.topic.user_nick_name);
        }

        //若是投票帖
        if (postDetailBean.topic.vote == 1) {
            contentView.setVoteBean(postDetailBean.topic.poll_info);
        }
    }

    @Override
    public void onGetPostDetailError(String msg) {

    }

}