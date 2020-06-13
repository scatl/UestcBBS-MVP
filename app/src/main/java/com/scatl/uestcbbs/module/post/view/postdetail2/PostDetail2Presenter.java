package com.scatl.uestcbbs.module.post.view.postdetail2;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.scatl.uestcbbs.R;
import com.scatl.uestcbbs.api.ApiConstant;
import com.scatl.uestcbbs.base.BasePreferenceFragment;
import com.scatl.uestcbbs.base.BasePresenter;
import com.scatl.uestcbbs.custom.imageview.CircleImageView;
import com.scatl.uestcbbs.custom.postview.ContentView;
import com.scatl.uestcbbs.entity.ContentViewBean;
import com.scatl.uestcbbs.entity.PostDetailBean;
import com.scatl.uestcbbs.helper.ExceptionHelper;
import com.scatl.uestcbbs.helper.rxhelper.Observer;
import com.scatl.uestcbbs.module.post.model.PostModel;
import com.scatl.uestcbbs.util.JsonUtil;
import com.scatl.uestcbbs.util.SharePrefUtil;
import com.scatl.uestcbbs.util.TimeUtil;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.reactivex.disposables.Disposable;

/**
 * author: sca_tl
 * date: 2020/6/6 19:30
 * description:
 */
public class PostDetail2Presenter extends BasePresenter<PostDetail2View> {
    private PostModel postModel = new PostModel();

    public void getPostDetail(int page,
                              int pageSize,
                              int order,
                              int topicId,
                              int authorId,
                              Context context) {
        postModel.getPostDetail(page, pageSize, order, topicId, authorId,
                SharePrefUtil.getToken(context),
                SharePrefUtil.getSecret(context),
                new Observer<PostDetailBean>() {
                    @Override
                    public void OnSuccess(PostDetailBean postDetailBean) {
                        if (postDetailBean.rs == ApiConstant.Code.SUCCESS_CODE) {
                            view.onGetPostDetailSuccess(postDetailBean);
                        }

                        if (postDetailBean.rs == ApiConstant.Code.ERROR_CODE) {
                            view.onGetPostDetailError(postDetailBean.head.errInfo);
                        }
                    }

                    @Override
                    public void onError(ExceptionHelper.ResponseThrowable e) {
                        view.onGetPostDetailError(e.message);
                    }

                    @Override
                    public void OnCompleted() {

                    }

                    @Override
                    public void OnDisposable(Disposable d) {
                        disposable.add(d);
                    }
                });
    }

    /**
     * author: sca_tl
     * description: 展示帖子基本信息（除去评论）
     */
    public void setBasicData(Activity activity, View basicView, PostDetailBean postDetailBean) {
        CircleImageView userAvatar = basicView.findViewById(R.id.post_detail_item_content_view_author_avatar);
        TextView postTitle = basicView.findViewById(R.id.post_detail_item_content_view_title);
        TextView userName = basicView.findViewById(R.id.post_detail_item_content_view_author_name);
        TextView userLevel = basicView.findViewById(R.id.post_detail_item_content_view_author_level);
        TextView time = basicView.findViewById(R.id.post_detail_item_content_view_time);
        TextView mobileSign = basicView.findViewById(R.id.post_detail_item_content_view_mobile_sign);
        ContentView contentView = basicView.findViewById(R.id.post_detail_item_content_view_content);

        //若是投票帖
        if (postDetailBean.topic.vote == 1) {
            contentView.setVoteBean(postDetailBean.topic.poll_info);
        }
        contentView.setContentData(JsonUtil.modelListA2B(postDetailBean.topic.content, ContentViewBean.class, postDetailBean.topic.content.size()));

        postTitle.setText(postDetailBean.topic.title);
        userName.setText(postDetailBean.topic.user_nick_name);
        time.setText(TimeUtil.formatTime(postDetailBean.topic.create_date, R.string.post_time1, activity));
        mobileSign.setText(TextUtils.isEmpty(postDetailBean.topic.mobileSign) ? "来自网页版" : postDetailBean.topic.mobileSign);

        if (! activity.isFinishing()) {
            Glide.with(activity).load(postDetailBean.topic.icon).into(userAvatar);
        }

        if (!TextUtils.isEmpty(postDetailBean.topic.userTitle)) {
            Matcher matcher = Pattern.compile("(.*?)\\((Lv\\..*)\\)").matcher(postDetailBean.topic.userTitle);
            if (matcher.find()) {
                String level = matcher.group(2);
                userLevel.setText(level);

            } else {
                userLevel.setText(postDetailBean.topic.userTitle);
            }
            userLevel.setBackgroundResource(R.drawable.shape_common_textview_background_not_clickable);
        } else {
            userLevel.setText(postDetailBean.topic.user_nick_name);
            userLevel.setBackgroundResource(R.drawable.shape_common_textview_background_not_clickable);
        }

    }
}
