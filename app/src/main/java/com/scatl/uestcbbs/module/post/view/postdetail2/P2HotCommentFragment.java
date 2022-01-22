package com.scatl.uestcbbs.module.post.view.postdetail2;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.scatl.uestcbbs.R;
import com.scatl.uestcbbs.annotation.PostAppendType;
import com.scatl.uestcbbs.annotation.ToastType;
import com.scatl.uestcbbs.base.BaseDialogFragment;
import com.scatl.uestcbbs.base.BasePresenter;
import com.scatl.uestcbbs.callback.OnRefresh;
import com.scatl.uestcbbs.custom.MyLinearLayoutManger;
import com.scatl.uestcbbs.entity.PostDetailBean;
import com.scatl.uestcbbs.entity.ReportBean;
import com.scatl.uestcbbs.entity.SupportResultBean;
import com.scatl.uestcbbs.module.magic.view.UseRegretMagicFragment;
import com.scatl.uestcbbs.module.post.adapter.PostCommentAdapter;
import com.scatl.uestcbbs.module.post.adapter.PostDianPingAdapter;
import com.scatl.uestcbbs.module.post.presenter.postdetail2.P2DianPingPresenter;
import com.scatl.uestcbbs.module.post.presenter.postdetail2.P2HotCommentPresenter;
import com.scatl.uestcbbs.module.post.view.CreateCommentFragment;
import com.scatl.uestcbbs.module.post.view.PostAppendFragment;
import com.scatl.uestcbbs.module.post.view.PostRateFragment;
import com.scatl.uestcbbs.module.user.view.UserDetailActivity;
import com.scatl.uestcbbs.util.Constant;
import com.scatl.uestcbbs.util.RefreshUtil;
import com.scatl.uestcbbs.util.TimeUtil;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;

import java.util.List;


public class P2HotCommentFragment extends BaseDialogFragment implements P2HotCommentView{

    RecyclerView recyclerView;
    SmartRefreshLayout refreshLayout;
    TextView hint;
    LottieAnimationView loading;
    PostCommentAdapter postCommentAdapter;

    P2HotCommentPresenter p2HotCommentPresenter;
    int tid;
    String formHash;
    PostDetailBean postDetailBean;

    public static P2HotCommentFragment getInstance(Bundle bundle) {
        P2HotCommentFragment p2HotCommentFragment = new P2HotCommentFragment();
        p2HotCommentFragment.setArguments(bundle);
        return p2HotCommentFragment;
    }

    @Override
    protected void getBundle(Bundle bundle) {
        if (bundle != null) {
            tid = bundle.getInt(Constant.IntentKey.TOPIC_ID, Integer.MAX_VALUE);
            formHash = bundle.getString(Constant.IntentKey.FORM_HASH, "");
        }
    }

    @Override
    protected int setLayoutResourceId() {
        return R.layout.fragment_p2_hot_comment;
    }

    @Override
    protected void findView() {
        refreshLayout = view.findViewById(R.id.p2_hotcomment_fragment_refresh);
        recyclerView = view.findViewById(R.id.p2_hotcomment_fragment_rv);
        hint = view.findViewById(R.id.p2_hotcomment_fragment_hint);
        loading = view.findViewById(R.id.p2_hotcomment_fragment_loading);
    }

    @Override
    protected void initView() {
        p2HotCommentPresenter = (P2HotCommentPresenter) presenter;


        postCommentAdapter = new PostCommentAdapter(R.layout.item_post_comment);
        recyclerView.setLayoutAnimation(AnimationUtils.loadLayoutAnimation(mActivity, R.anim.layout_animation_scale_in));
        recyclerView.setLayoutManager(new MyLinearLayoutManger(mActivity));
        recyclerView.setAdapter(postCommentAdapter);

        refreshLayout.setEnableRefresh(false);
        refreshLayout.setEnableNestedScroll(false);
    }

    @Override
    protected BasePresenter initPresenter() {
        return new P2HotCommentPresenter();
    }

    @Override
    protected void lazyLoad() {
        p2HotCommentPresenter.getAllComment(tid, mActivity);
    }

    @Override
    protected void setOnItemClickListener() {
        //回复评论
        postCommentAdapter.setOnItemChildClickListener((adapter, view, position) -> {
            if (view.getId() == R.id.item_post_comment_reply_button ||
                    view.getId() == R.id.item_post_comment_root_rl) {
                Bundle bundle = new Bundle();
                bundle.putInt(Constant.IntentKey.BOARD_ID, postDetailBean.boardId);
                bundle.putInt(Constant.IntentKey.TOPIC_ID, postDetailBean.topic.topic_id);
                bundle.putInt(Constant.IntentKey.QUOTE_ID, postCommentAdapter.getData().get(position).reply_posts_id);
                bundle.putBoolean(Constant.IntentKey.IS_QUOTE, true);
                bundle.putString(Constant.IntentKey.USER_NAME, postCommentAdapter.getData().get(position).reply_name);
                CreateCommentFragment.getInstance(bundle)
                        .show(getChildFragmentManager(), TimeUtil.getStringMs());
            }

            if (view.getId() == R.id.item_post_comment_support_button) {
                p2HotCommentPresenter.support(postDetailBean.topic.topic_id,
                        postCommentAdapter.getData().get(position).reply_posts_id,
                        "post", "support", position, mActivity);
            }

            if (view.getId() == R.id.item_post_comment_author_avatar) {
                Intent intent = new Intent(mActivity, UserDetailActivity.class);
                intent.putExtra(Constant.IntentKey.USER_ID, postCommentAdapter.getData().get(position).reply_id);
                startActivity(intent);
            }
            if (view.getId() == R.id.item_post_comment_buchong_button) {
                onAppendPost(postCommentAdapter.getData().get(position).reply_posts_id, tid);
            }
            if (view.getId() == R.id.item_post_comment_more_button) {
                p2HotCommentPresenter.moreReplyOptionsDialog(mActivity, formHash, postDetailBean.boardId,
                        tid, postDetailBean.topic.user_id, postCommentAdapter.getData().get(position));
            }
        });

        postCommentAdapter.setOnItemChildLongClickListener((adapter, view, position) -> {
            if (view.getId() == R.id.item_post_comment_root_rl) {
                p2HotCommentPresenter.moreReplyOptionsDialog(mActivity, formHash, postDetailBean.boardId,
                        tid, postDetailBean.topic.user_id, postCommentAdapter.getData().get(position));
            }
            return false;
        });
    }

    @Override
    protected void setOnRefreshListener() {
        RefreshUtil.setOnRefreshListener(mActivity, refreshLayout, new OnRefresh() {
            @Override
            public void onRefresh(RefreshLayout refreshLayout) { }

            @Override
            public void onLoadMore(RefreshLayout refreshLayout) { }
        });
    }

    @Override
    public void onGetAllPostSuccess(PostDetailBean postDetailBean) {
        this.postDetailBean = postDetailBean;
        loading.setVisibility(View.GONE);
        hint.setText("");

        List<PostDetailBean.ListBean> hots = p2HotCommentPresenter.getHotComment(postDetailBean);

        if (hots.size() == 0) {
            hint.setText("还没有精彩评论");
        } else {
            recyclerView.scheduleLayoutAnimation();
            postCommentAdapter.setNewData(hots);
            refreshLayout.finishLoadMoreWithNoMoreData();
        }
    }

    @Override
    public void onGetAllPostError(String msg) {
        loading.setVisibility(View.GONE);
        hint.setText(msg);
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
            showToast("赞-1",ToastType.TYPE_SUCCESS);
        }
    }

    @Override
    public void onSupportError(String msg) {
        showToast(msg, ToastType.TYPE_ERROR);
    }

    @Override
    public void onPingFen(int pid) {
        Bundle bundle = new Bundle();
        bundle.putInt(Constant.IntentKey.TOPIC_ID, tid);
        bundle.putInt(Constant.IntentKey.POST_ID, pid);
        PostRateFragment.getInstance(bundle).show(getChildFragmentManager(), TimeUtil.getStringMs());
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