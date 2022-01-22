package com.scatl.uestcbbs.module.post.view.postdetail2;

import android.content.Intent;
import android.os.Bundle;

import androidx.recyclerview.widget.RecyclerView;

import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.scatl.uestcbbs.R;
import com.scatl.uestcbbs.annotation.PostAppendType;
import com.scatl.uestcbbs.annotation.ToastType;
import com.scatl.uestcbbs.base.BaseFragment;
import com.scatl.uestcbbs.base.BasePresenter;
import com.scatl.uestcbbs.callback.OnRefresh;
import com.scatl.uestcbbs.custom.MyLinearLayoutManger;
import com.scatl.uestcbbs.entity.PostDetailBean;
import com.scatl.uestcbbs.entity.ReportBean;
import com.scatl.uestcbbs.entity.SupportResultBean;
import com.scatl.uestcbbs.module.magic.view.UseRegretMagicFragment;
import com.scatl.uestcbbs.module.post.adapter.PostCommentAdapter;
import com.scatl.uestcbbs.module.post.presenter.postdetail2.P2CommentPresenter;
import com.scatl.uestcbbs.module.post.view.CreateCommentActivity;
import com.scatl.uestcbbs.module.post.view.PostAppendFragment;
import com.scatl.uestcbbs.module.post.view.ViewDaShangFragment;
import com.scatl.uestcbbs.module.post.view.ViewDianPingFragment;
import com.scatl.uestcbbs.module.user.view.UserDetailActivity;
import com.scatl.uestcbbs.util.Constant;
import com.scatl.uestcbbs.util.RefreshUtil;
import com.scatl.uestcbbs.util.SharePrefUtil;
import com.scatl.uestcbbs.util.TimeUtil;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;

public class P2CommentFragment extends BaseFragment implements P2CommentView{

    SmartRefreshLayout refreshLayout;
    RecyclerView recyclerView;
    PostCommentAdapter commentAdapter;
    TextView hint;
    LottieAnimationView loading;

    P2CommentPresenter p2CommentPresenter;

    PostDetailBean postDetailBean;

    int page = 1, topicId, order = 0;
    int topicUserId;//楼主id
    int authorId = 0;//只看authorid帖子
    String formHash;


    public static P2CommentFragment getInstance(Bundle bundle) {
        P2CommentFragment p2CommentFragment = new P2CommentFragment();
        p2CommentFragment.setArguments(bundle);
        return p2CommentFragment;
    }

    @Override
    protected void getBundle(Bundle bundle) {
        if (bundle != null) {
            topicId = bundle.getInt(Constant.IntentKey.TOPIC_ID, Integer.MAX_VALUE);
            formHash = bundle.getString(Constant.IntentKey.FORM_HASH, "");
        }
    }

    @Override
    protected int setLayoutResourceId() {
        return R.layout.fragment_p2_comment;
    }

    @Override
    protected void findView() {
        refreshLayout = view.findViewById(R.id.p2_comment_fragment_refresh);
        recyclerView = view.findViewById(R.id.p2_comment_fragment_rv);
        hint = view.findViewById(R.id.p2_comment_fragment_hint);
        loading = view.findViewById(R.id.p2_comment_fragment_loading);
    }

    @Override
    protected void initView() {
        p2CommentPresenter = (P2CommentPresenter) presenter;

        commentAdapter = new PostCommentAdapter(R.layout.item_post_comment);
        recyclerView.setLayoutAnimation(AnimationUtils.loadLayoutAnimation(mActivity, R.anim.layout_animation_scale_in));
        recyclerView.setLayoutManager(new MyLinearLayoutManger(mActivity));
        recyclerView.setAdapter(commentAdapter);

        refreshLayout.setEnableRefresh(false);
        refreshLayout.setEnableNestedScroll(false);

    }

    @Override
    protected void lazyLoad() {
        p2CommentPresenter.getPostComment(page, SharePrefUtil.getPageSize(mActivity), order, topicId, authorId, mActivity);
        //refreshLayout.autoRefresh(0, 300, 1, false);
    }

    @Override
    protected BasePresenter initPresenter() {
        return new P2CommentPresenter();
    }

    @Override
    protected void setOnItemClickListener() {
        commentAdapter.setOnItemChildClickListener((adapter, view, position) -> {
            if (view.getId() == R.id.item_post_comment_reply_button ||
                    view.getId() == R.id.item_post_comment_root_rl) {
//                Bundle bundle = new Bundle();
//                bundle.putInt(Constant.IntentKey.BOARD_ID, postDetailBean.boardId);
//                bundle.putInt(Constant.IntentKey.TOPIC_ID, postDetailBean.topic.topic_id);
//                bundle.putInt(Constant.IntentKey.QUOTE_ID, commentAdapter.getData().get(position).reply_posts_id);
//                bundle.putBoolean(Constant.IntentKey.IS_QUOTE, true);
//                bundle.putString(Constant.IntentKey.USER_NAME, commentAdapter.getData().get(position).reply_name);
//                CreateCommentFragment.getInstance(bundle)
//                        .show(getChildFragmentManager(), TimeUtil.getStringMs());

                Intent intent = new Intent(mActivity, CreateCommentActivity.class);
                intent.putExtra(Constant.IntentKey.BOARD_ID, postDetailBean.boardId);
                intent.putExtra(Constant.IntentKey.TOPIC_ID, postDetailBean.topic.topic_id);
                intent.putExtra(Constant.IntentKey.QUOTE_ID, commentAdapter.getData().get(position).reply_posts_id);
                intent.putExtra(Constant.IntentKey.IS_QUOTE, true);
                intent.putExtra(Constant.IntentKey.USER_NAME, commentAdapter.getData().get(position).reply_name);
                startActivity(intent);
            }

            if (view.getId() == R.id.item_post_comment_support_button) {
                p2CommentPresenter.support(postDetailBean.topic.topic_id,
                        commentAdapter.getData().get(position).reply_posts_id,
                        "post", "support", position, mActivity);
            }

            if (view.getId() == R.id.item_post_comment_author_avatar) {
                Intent intent = new Intent(mActivity, UserDetailActivity.class);
                intent.putExtra(Constant.IntentKey.USER_ID, commentAdapter.getData().get(position).reply_id);
                startActivity(intent);
            }
            if (view.getId() == R.id.item_post_comment_buchong_button) {
                onAppendPost(commentAdapter.getData().get(position).reply_posts_id, topicId);
            }
            if (view.getId() == R.id.item_post_comment_more_button) {
                p2CommentPresenter.moreReplyOptionsDialog(mActivity, formHash, postDetailBean.boardId,
                        topicId,  postDetailBean.topic.user_id, commentAdapter.getData().get(position));
            }
        });

        commentAdapter.setOnItemChildLongClickListener((adapter, view, position) -> {
            if (view.getId() == R.id.item_post_comment_root_rl) {
                p2CommentPresenter.moreReplyOptionsDialog(mActivity, formHash, postDetailBean.boardId,
                        topicId,  postDetailBean.topic.user_id, commentAdapter.getData().get(position));
            }
            return false;
        });
    }

    @Override
    protected void setOnRefreshListener() {
        RefreshUtil.setOnRefreshListener(mActivity, refreshLayout, new OnRefresh() {
            @Override
            public void onRefresh(RefreshLayout refreshLayout) {
                page = 1;
                p2CommentPresenter.getPostComment(page, SharePrefUtil.getPageSize(mActivity), order, topicId, authorId, mActivity);
            }

            @Override
            public void onLoadMore(RefreshLayout refreshLayout) {
                p2CommentPresenter.getPostComment(page, SharePrefUtil.getPageSize(mActivity), order, topicId, authorId, mActivity);
            }
        });
    }

    @Override
    public void onGetPostCommentSuccess(PostDetailBean postDetailBean) {
        page = page + 1;
        hint.setText("");
        loading.setVisibility(View.GONE);

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
            commentAdapter.setAuthorId(postDetailBean.topic.user_id);
            commentAdapter.setNewData(postDetailBean.list);
        } else {
            commentAdapter.addData(postDetailBean.list);
        }

        if (postDetailBean.list == null || postDetailBean.list.size() == 0) {
            hint.setText("还没有评论");
        }
    }

    @Override
    public void onGetPostCommentError(String msg, int code) {

    }

    @Override
    public void onAppendPost(int replyPostsId, int tid) {
        Bundle bundle = new Bundle();
        bundle.putInt(Constant.IntentKey.POST_ID, replyPostsId);
        bundle.putInt(Constant.IntentKey.TOPIC_ID, tid);
        bundle.putString(Constant.IntentKey.TYPE, PostAppendType.APPEND);
        PostAppendFragment.getInstance(bundle).show(getChildFragmentManager(), TimeUtil.getStringMs());
    }

    @Override
    public void onSupportSuccess(SupportResultBean supportResultBean, String action, int position) {
        if (action.equals("support")) {
            showToast( supportResultBean.head.errInfo, ToastType.TYPE_SUCCESS);
        } else {
            showToast("赞-1", ToastType.TYPE_SUCCESS);
        }
    }

    @Override
    public void onSupportError(String msg) {
        showToast(msg, ToastType.TYPE_ERROR);
    }

    @Override
    public void onPingFen(int pid) {
        Bundle bundle = new Bundle();
        bundle.putInt(Constant.IntentKey.TOPIC_ID, topicId);
        bundle.putInt(Constant.IntentKey.POST_ID, pid);
        ViewDaShangFragment.getInstance(bundle).show(getChildFragmentManager(), TimeUtil.getStringMs());
    }

    @Override
    public void onDianPing(int pid) {
        Bundle bundle = new Bundle();
        bundle.putInt(Constant.IntentKey.TOPIC_ID, topicId);
        bundle.putInt(Constant.IntentKey.POST_ID, pid);
        bundle.putString(Constant.IntentKey.TYPE, PostAppendType.DIANPING);
        ViewDianPingFragment.getInstance(bundle).show(getChildFragmentManager(), TimeUtil.getStringMs());
    }

    @Override
    public void onOnlyReplyAuthor(int uid) { }

    @Override
    public void onReportSuccess(ReportBean reportBean) {
        showToast(reportBean.head.errInfo, ToastType.TYPE_SUCCESS);
    }

    @Override
    public void onReportError(String msg) {
        showToast(msg, ToastType.TYPE_ERROR);
    }

    @Override
    public void onDeletePost(int tid, int pid) {
        Bundle bundle = new Bundle();
        bundle.putInt(Constant.IntentKey.POST_ID, pid);
        bundle.putInt(Constant.IntentKey.TOPIC_ID, tid);
        UseRegretMagicFragment.getInstance(bundle).show(getChildFragmentManager(), TimeUtil.getStringMs());
    }

    @Override
    public void onStickReplySuccess(String msg) {
        showToast(msg, ToastType.TYPE_SUCCESS);
        recyclerView.scrollToPosition(0);
        refreshLayout.autoRefresh(0 , 300, 1, false);
    }

    @Override
    public void onStickReplyError(String msg) {
        showToast(msg, ToastType.TYPE_ERROR);
    }
}