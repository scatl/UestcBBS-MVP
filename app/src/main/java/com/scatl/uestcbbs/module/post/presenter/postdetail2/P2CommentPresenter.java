package com.scatl.uestcbbs.module.post.presenter.postdetail2;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.fragment.app.FragmentActivity;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.scatl.uestcbbs.App;
import com.scatl.uestcbbs.R;
import com.scatl.uestcbbs.api.ApiConstant;
import com.scatl.uestcbbs.base.BasePresenter;
import com.scatl.uestcbbs.entity.PostDetailBean;
import com.scatl.uestcbbs.entity.ReportBean;
import com.scatl.uestcbbs.entity.SupportResultBean;
import com.scatl.uestcbbs.helper.ExceptionHelper;
import com.scatl.uestcbbs.helper.rxhelper.Observer;
import com.scatl.uestcbbs.module.post.model.PostModel;
import com.scatl.uestcbbs.module.post.view.postdetail2.P2CommentView;
import com.scatl.uestcbbs.module.report.ReportFragment;
import com.scatl.uestcbbs.module.webview.view.WebViewActivity;
import com.scatl.uestcbbs.util.Constant;
import com.scatl.uestcbbs.util.DebugUtil;
import com.scatl.uestcbbs.util.ForumUtil;
import com.scatl.uestcbbs.util.SharePrefUtil;
import com.scatl.uestcbbs.util.TimeUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.reactivex.disposables.Disposable;

public class P2CommentPresenter extends BasePresenter<P2CommentView> {
    private PostModel postModel = new PostModel();

    /**
     * 获取刚刚发送的评论数据
     */
    public void getReplyData(int topicId,
                             int replyPosition,
                             Context context) {
        postModel.getPostDetail(1, 20, 1, topicId, 0,
                SharePrefUtil.getToken(context),
                SharePrefUtil.getSecret(context),
                new Observer<PostDetailBean>() {
                    @Override
                    public void OnSuccess(PostDetailBean postDetailBean) {
                        if (postDetailBean.rs == ApiConstant.Code.SUCCESS_CODE) {
                            view.onGetReplyDataSuccess(postDetailBean, replyPosition);
                        }
                    }

                    @Override
                    public void onError(ExceptionHelper.ResponseThrowable e) {

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

    public void getPostComment(int page,
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
                            view.onGetPostCommentSuccess(postDetailBean);
                        }

                        if (postDetailBean.rs == ApiConstant.Code.ERROR_CODE) {
                            view.onGetPostCommentError(postDetailBean.head.errInfo, ApiConstant.Code.ERROR_CODE);
                        }
                    }

                    @Override
                    public void onError(ExceptionHelper.ResponseThrowable e) {
                        view.onGetPostCommentError(e.message, e.code);
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

    public void support(int tid,
                        int pid,
                        String type,
                        String action,
                        int position,
                        Context context) {
        postModel.support(tid, pid, type, action,
                SharePrefUtil.getToken(context),
                SharePrefUtil.getSecret(context),
                new Observer<SupportResultBean>() {
                    @Override
                    public void OnSuccess(SupportResultBean supportResultBean) {
                        if (supportResultBean.rs == ApiConstant.Code.SUCCESS_CODE) {
                            view.onSupportSuccess(supportResultBean, action, position);
                        }

                        if (supportResultBean.rs == ApiConstant.Code.ERROR_CODE) {
                            view.onSupportError(supportResultBean.head.errInfo);
                        }
                    }

                    @Override
                    public void onError(ExceptionHelper.ResponseThrowable e) {
                        view.onSupportError(e.message);
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

    public void stickReply(String formHash, int fid, int tid,
                           boolean stick, int replyId) {
        postModel.stickReply(formHash, fid, tid, stick, replyId, new Observer<String>() {
            @Override
            public void OnSuccess(String s) {
                if (s.contains("管理操作成功")) {
                    view.onStickReplySuccess(stick ? "评论置顶成功" : "评论已取消置顶");
                } else if (s.contains("没有权限")) {
                    view.onStickReplyError("您没有权限进行此操作，只能操作自己帖子里的评论哦");
                }
            }

            @Override
            public void onError(ExceptionHelper.ResponseThrowable e) {
                view.onStickReplyError("操作错误：" + e.message);
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

    public void moreReplyOptionsDialog(Context context, int fid, int tid, int authorId,
                                       PostDetailBean.ListBean listBean) {
        final View options_view = LayoutInflater.from(context).inflate(R.layout.dialog_post_reply_options, new LinearLayout(context));
        View stick = options_view.findViewById(R.id.options_post_reply_stick);
        View rate = options_view.findViewById(R.id.options_post_reply_rate);
        View report = options_view.findViewById(R.id.options_post_reply_report);
        View onlyAuthor = options_view.findViewById(R.id.options_post_reply_only_author);
        View buchong = options_view.findViewById(R.id.options_post_reply_buchong);
        View delete = options_view.findViewById(R.id.options_post_reply_delete);
        View against = options_view.findViewById(R.id.options_post_reply_against);
        View modify = options_view.findViewById(R.id.options_post_reply_modify);
        View dianping = options_view.findViewById(R.id.options_post_reply_dianping);
        TextView stickText = options_view.findViewById(R.id.options_post_reply_stick_text);

        stickText.setText(listBean.poststick == 0 ? "置顶" : "取消置顶");
        buchong.setVisibility(listBean.reply_id == SharePrefUtil.getUid(context) ? View.VISIBLE : View.GONE);
        rate.setVisibility(listBean.reply_id == SharePrefUtil.getUid(context) ? View.GONE : View.VISIBLE);
        delete.setVisibility(listBean.reply_id == SharePrefUtil.getUid(context) ? View.VISIBLE : View.GONE);
        stick.setVisibility(authorId == SharePrefUtil.getUid(context) ? View.VISIBLE : View.GONE);
        modify.setVisibility(listBean.reply_id == SharePrefUtil.getUid(context) ? View.VISIBLE : View.GONE);
        against.setVisibility(listBean.reply_id == SharePrefUtil.getUid(context) ? View.GONE : View.VISIBLE);
        report.setVisibility(listBean.reply_id == SharePrefUtil.getUid(context) ? View.GONE : View.VISIBLE);


        final AlertDialog options_dialog = new MaterialAlertDialogBuilder(context)
                .setView(options_view)
                .create();

        options_dialog.show();

        stick.setOnClickListener(v -> {
            stickReply(SharePrefUtil.getForumHash(context), fid, tid, listBean.poststick == 0, listBean.reply_posts_id);
            options_dialog.dismiss();
        });
        rate.setOnClickListener(v -> {
            view.onPingFen(listBean.reply_posts_id);
            options_dialog.dismiss();
        });
        onlyAuthor.setOnClickListener(v -> {
            view.onOnlyReplyAuthor(listBean.reply_id);
            options_dialog.dismiss();
        });
        report.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putString(Constant.IntentKey.TYPE, "post");
            bundle.putInt(Constant.IntentKey.ID, listBean.reply_posts_id);
            if (context instanceof FragmentActivity) {
                ReportFragment.Companion.getInstance(bundle).show(((FragmentActivity) context).getSupportFragmentManager(), TimeUtil.getStringMs());
            }
            options_dialog.dismiss();
        });
        buchong.setOnClickListener(v -> {
            view.onAppendPost(listBean.reply_posts_id, tid);
            options_dialog.dismiss();
        });
        delete.setOnClickListener(v -> {
            view.onDeletePost(tid, listBean.reply_posts_id);
            options_dialog.dismiss();
        });
        against.setOnClickListener(v -> {
            support(tid, listBean.reply_posts_id, "post", "against", 0, context);
            options_dialog.dismiss();
        });
        dianping.setOnClickListener(v -> {
            view.onDianPing(listBean.reply_posts_id);
            options_dialog.dismiss();
        });
        modify.setOnClickListener(v -> {
            Intent intent = new Intent(context, WebViewActivity.class);
            intent.putExtra(Constant.IntentKey.URL, "https://bbs.uestc.edu.cn/forum.php?mod=post&action=edit&tid=" + tid + "&pid=" + listBean.reply_posts_id);
            context.startActivity(intent);
        });
    }

    //获取点赞数大于等于*的所有评论
    public List<PostDetailBean.ListBean> getHotComment(PostDetailBean postDetailBean) {
        List<PostDetailBean.ListBean> hot = new ArrayList<>();

        for (int i = 0; i < postDetailBean.list.size(); i ++) {
            PostDetailBean.ListBean item = postDetailBean.list.get(i);
            if ("support".equals(item.extraPanel.get(0).type)
                    && item.extraPanel.get(0).extParams.recommendAdd >=
                    SharePrefUtil.getHotCommentZanThreshold(App.getContext())) {
                item.isHotComment = true;
                if (!ForumUtil.isInBlackList(item.reply_id)) {
                    hot.add(item);
                }
            }
        }
        Collections.sort(hot, (o1, o2) -> o2.extraPanel.get(0).extParams.recommendAdd - o1.extraPanel.get(0).extParams.recommendAdd);
        return hot;
    }

    /**
     * 将热门评论排在前面
     */
    public List<PostDetailBean.ListBean> resortComment(PostDetailBean postDetailBean) {
        try {
            List<PostDetailBean.ListBean> hot = getHotComment(postDetailBean);
            List<PostDetailBean.ListBean> filter = new ArrayList<>();

            for (int i = 0; i < postDetailBean.list.size(); i ++) {
                PostDetailBean.ListBean item = postDetailBean.list.get(i);
                if (!hot.contains(item)) {
                    if (!ForumUtil.isInBlackList(item.reply_id)) {
                        filter.add(item);
                    }
                }
            }

            List<PostDetailBean.ListBean> result = new ArrayList<>(hot);
            result.addAll(filter);
            return result;
        } catch (Exception e) {
            return postDetailBean.list;
        }
    }

    public List<PostDetailBean.ListBean> getFloorInFloorCommentData(PostDetailBean postDetailBean) {
        List<PostDetailBean.ListBean> res = new ArrayList<>();

        List<PostDetailBean.ListBean> a = new ArrayList<>();
        for (int i = 0; i < postDetailBean.list.size(); i ++) {
            PostDetailBean.ListBean listBean = postDetailBean.list.get(i);
            if (listBean.is_quote == 0) {
                res.add(listBean);
            } else if (listBean.is_quote == 1) {
                a.add(listBean);
            }
//            else if (listBean.is_quote == 1) {
//                PostDetailBean.ListBean quoteComment = findCommentByPid(postDetailBean, listBean.quote_pid);
//                if (quoteComment != null) {
//                   listBean.quote_comment = quoteComment;
//
//                    PostDetailBean.ListBean quoteComment1 = findCommentByPid(postDetailBean, quoteComment.quote_pid);
//                   if (quoteComment1 != null) {
//
//                   }
//                }
//            }
        }

        DebugUtil.e("fffff", a.size()+"");
        int j = 0;
        for (int i = 0; i < a.size(); i ++) {
            int qpid = a.get(i).quote_pid;
            for (PostDetailBean.ListBean listBean : res) {
                if (listBean.reply_posts_id == qpid) {
                    if (listBean.quote_comments == null) {
                        listBean.quote_comments = new ArrayList<>();
                    }
                    j ++;
                    listBean.quote_comments.add(a.get(i));
                }
            }
        }
        DebugUtil.e("fffff", j+"");

        do {
            for (int i = 0; i < a.size(); i ++) {
                int qpid = a.get(i).quote_pid;
                for (PostDetailBean.ListBean listBean : res) {
                    if (listBean.reply_posts_id == qpid) {
                        if (listBean.quote_comments == null) {
                            listBean.quote_comments = new ArrayList<>();
                        }
                        j ++;
                        listBean.quote_comments.add(a.get(i));
                    }
                }
            }
        } while (j < a.size());

        return res;
    }

    public PostDetailBean.ListBean findCommentByPid(List<PostDetailBean.ListBean> listBean, int pid) {
        for (int i = 0; i < listBean.size(); i ++) {
            PostDetailBean.ListBean bean = listBean.get(i);
            if (pid == bean.reply_posts_id) {
                return bean;
            }
        }
        return null;
    }
}
