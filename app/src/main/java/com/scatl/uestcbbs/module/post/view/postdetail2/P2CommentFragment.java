package com.scatl.uestcbbs.module.post.view.postdetail2;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.view.HapticFeedbackConstants;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.material.chip.ChipGroup;
import com.scatl.uestcbbs.R;
import com.scatl.uestcbbs.annotation.PostAppendType;
import com.scatl.uestcbbs.annotation.ToastType;
import com.scatl.uestcbbs.base.BaseEvent;
import com.scatl.uestcbbs.base.BaseFragment;
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
import com.scatl.uestcbbs.module.post.view.ViewOriginCommentFragment;
import com.scatl.uestcbbs.module.user.view.UserDetailActivity;
import com.scatl.uestcbbs.util.Constant;
import com.scatl.uestcbbs.util.RefreshUtil;
import com.scatl.uestcbbs.util.SharePrefUtil;
import com.scatl.uestcbbs.util.TimeUtil;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

public class P2CommentFragment extends BaseFragment<P2CommentPresenter> implements P2CommentView{

    SmartRefreshLayout refreshLayout;
    RecyclerView recyclerView;
    PostCommentAdapter commentAdapter;
    TextView hint;
    LottieAnimationView loading;
    PostDetailBean postDetailBean;
    ChipGroup chipGroup;
    List<PostDetailBean.ListBean> totalCommentData;

    int page = 1, topicId, order = 0;
    int topicUserId;//楼主id
    int authorId = 0;//只看authorid帖子
    private SORT currentSort;

    public static final int PAGE_SIZE = 10000;

    private enum SORT {
        DEFAULT, NEW, AUTHOR, FLOOR_IN_FLOOR
    }

    public static P2CommentFragment getInstance(Bundle bundle) {
        P2CommentFragment p2CommentFragment = new P2CommentFragment();
        p2CommentFragment.setArguments(bundle);
        return p2CommentFragment;
    }

    @Override
    protected void getBundle(Bundle bundle) {
        if (bundle != null) {
            topicId = bundle.getInt(Constant.IntentKey.TOPIC_ID, Integer.MAX_VALUE);
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
        chipGroup = view.findViewById(R.id.chip_group);
    }

    @Override
    protected void initView() {
        commentAdapter = new PostCommentAdapter(R.layout.item_post_comment);
        recyclerView.setLayoutAnimation(AnimationUtils.loadLayoutAnimation(mActivity, R.anim.layout_animation_scale_in));
        recyclerView.setLayoutManager(new MyLinearLayoutManger(mActivity));
        recyclerView.setAdapter(commentAdapter);

        refreshLayout.setEnableRefresh(false);
        refreshLayout.setEnableNestedScroll(false);
        chipGroup.check(R.id.default_sort_btn);
        currentSort = SORT.DEFAULT;

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                EventBus.getDefault().post(new BaseEvent<>(BaseEvent.EventCode.COMMENT_FRAGMENT_SCROLL, dy));
            }
        });
    }

    @Override
    protected void lazyLoad() {
        presenter.getPostComment(page, PAGE_SIZE, order, topicId, authorId, mActivity);
    }

    @Override
    protected P2CommentPresenter initPresenter() {
        return new P2CommentPresenter();
    }

    @Override
    protected void setOnItemClickListener() {
        commentAdapter.setOnItemChildClickListener((adapter, view, position) -> {
            if (view.getId() == R.id.item_post_comment_reply_button ||
                    view.getId() == R.id.item_post_comment_root_rl) {
                Intent intent = new Intent(mActivity, CreateCommentActivity.class);
                intent.putExtra(Constant.IntentKey.BOARD_ID, postDetailBean.boardId);
                intent.putExtra(Constant.IntentKey.TOPIC_ID, postDetailBean.topic.topic_id);
                intent.putExtra(Constant.IntentKey.QUOTE_ID, commentAdapter.getData().get(position).reply_posts_id);
                intent.putExtra(Constant.IntentKey.IS_QUOTE, true);
                intent.putExtra(Constant.IntentKey.USER_NAME, commentAdapter.getData().get(position).reply_name);
                intent.putExtra(Constant.IntentKey.POSITION, position);
                startActivity(intent);
            }

            if (view.getId() == R.id.item_post_comment_support_button) {
                view.performHapticFeedback(HapticFeedbackConstants.CONTEXT_CLICK);
                presenter.support(postDetailBean.topic.topic_id,
                        commentAdapter.getData().get(position).reply_posts_id,
                        "post", "support", position, mActivity);
            }

            if (view.getId() == R.id.item_post_comment_author_avatar) {
                Intent intent = new Intent(mActivity, UserDetailActivity.class);
                intent.putExtra(Constant.IntentKey.USER_ID, commentAdapter.getData().get(position).reply_id);
                startActivity(intent);
            }

            if (view.getId() == R.id.item_post_comment_more_button) {
                presenter.moreReplyOptionsDialog(mActivity, postDetailBean.boardId,
                        topicId,  postDetailBean.topic.user_id, commentAdapter.getData().get(position));
            }

            if (view.getId() == R.id.quote_layout) {
                int pid = commentAdapter.getData().get(position).quote_pid;
                PostDetailBean.ListBean data = presenter.findCommentByPid(totalCommentData, pid);
                if (data != null) {
                    Bundle bundle = new Bundle();
                    bundle.putInt(Constant.IntentKey.TOPIC_ID, topicId);
                    bundle.putSerializable(Constant.IntentKey.DATA_1, data);
                    if (mActivity instanceof FragmentActivity) {
                        ViewOriginCommentFragment
                                .Companion
                                .getInstance(bundle)
                                .show(((FragmentActivity) mActivity).getSupportFragmentManager(), TimeUtil.getStringMs());
                    }
                }
            }
        });

        commentAdapter.setOnItemChildLongClickListener((adapter, view, position) -> {
            if (view.getId() == R.id.item_post_comment_root_rl) {
                presenter.moreReplyOptionsDialog(mActivity, postDetailBean.boardId,
                        topicId, postDetailBean.topic.user_id, commentAdapter.getData().get(position));
            }
            return false;
        });

        chipGroup.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (checkedIds.size() != 0) {
                page = 1;
                Integer id = checkedIds.get(0);
                if (id == R.id.default_sort_btn) {
                    currentSort = SORT.DEFAULT;
                    order = 0;
                    authorId = 0;
                    commentAdapter.setNewData(new ArrayList<>());
                    presenter.getPostComment(page, PAGE_SIZE, order, topicId, authorId, mActivity);
                } else if (id == R.id.new_sort_btn) {
                    currentSort = SORT.NEW;
                    order = 1;
                    authorId = 0;
                    commentAdapter.setNewData(new ArrayList<>());
                    presenter.getPostComment(page, PAGE_SIZE, order, topicId, authorId, mActivity);
                } else if (id == R.id.author_sort_btn) {
                    currentSort = SORT.AUTHOR;
                    authorId = postDetailBean.topic.user_id;
                    commentAdapter.setNewData(new ArrayList<>());
                    presenter.getPostComment(page, PAGE_SIZE, order, topicId, authorId, mActivity);
                } else if (id == R.id.floor_in_floor_sort_btn) {
                    currentSort = SORT.FLOOR_IN_FLOOR;
                    order = 0;
                    authorId = 0;
                    presenter.getPostComment(page, PAGE_SIZE, order, topicId, authorId, mActivity);
                }
                hint.setText("");
                loading.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    protected void setOnRefreshListener() {
        RefreshUtil.setOnRefreshListener(mActivity, refreshLayout, new OnRefresh() {
            @Override
            public void onRefresh(RefreshLayout refreshLayout) { }

            @Override
            public void onLoadMore(RefreshLayout refreshLayout) {
                presenter.getPostComment(page, PAGE_SIZE, order, topicId, authorId, mActivity);
            }
        });
    }

    @Override
    public void onGetPostCommentSuccess(PostDetailBean postDetailBean) {
        page = page + 1;
        hint.setText("");
        loading.setVisibility(View.GONE);

        if (postDetailBean.has_next == 1 && currentSort != SORT.FLOOR_IN_FLOOR) {
            refreshLayout.finishRefresh();
            refreshLayout.finishLoadMore(true);
        } else {
            refreshLayout.finishRefreshWithNoMoreData();
            refreshLayout.finishLoadMoreWithNoMoreData();
        }

        commentAdapter.setAuthorId(postDetailBean.topic.user_id);

        if (postDetailBean.page == 1) {
            this.postDetailBean = postDetailBean;
            topicUserId = postDetailBean.topic.user_id;
            recyclerView.scheduleLayoutAnimation();
            if (currentSort == SORT.DEFAULT) {
                totalCommentData = postDetailBean.list;
                commentAdapter.setTotalCommentData(totalCommentData);
                commentAdapter.setNewData(presenter.resortComment(postDetailBean));
            } else if (currentSort == SORT.FLOOR_IN_FLOOR) {
                presenter.getFloorInFloorCommentData(postDetailBean);
            } else {
                commentAdapter.setNewData(postDetailBean.list);
            }
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
            showToast(supportResultBean.head.errInfo, ToastType.TYPE_SUCCESS);
            commentAdapter.refreshNotifyItemChanged(position, PostCommentAdapter.Payload.UPDATE_SUPPORT);
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

    @Override
    public void onGetReplyDataSuccess(PostDetailBean postDetailBean, final int replyPosition) {
        if (postDetailBean.list != null) {
            for (PostDetailBean.ListBean data : postDetailBean.list) {
                if (data.reply_id == SharePrefUtil.getUid(mActivity) && commentAdapter != null) {
                    try {
                        commentAdapter.getData().add(replyPosition + 1, data);
                        commentAdapter.notifyItemInserted(replyPosition + 1);
                        if (commentAdapter.getData().size() - 1 == replyPosition) {
                            recyclerView.scrollToPosition(replyPosition + 1);
                        } else {
                            recyclerView.scrollToPosition(replyPosition + 2);
                        }
                        new Handler().postDelayed(() ->
                                commentAdapter.refreshNotifyItemChanged(replyPosition + 1,
                                        PostCommentAdapter.Payload.BLING), 100
                        );
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                }
            }
        }
    }

    @Override
    protected boolean registerEventBus() {
        return true;
    }

    @Override
    protected void receiveEventBusMsg(BaseEvent baseEvent) {
        if (baseEvent.eventCode == BaseEvent.EventCode.SEND_COMMENT_SUCCESS) {
            presenter.getReplyData(topicId, (int)baseEvent.eventData, mActivity);
        }
    }
}