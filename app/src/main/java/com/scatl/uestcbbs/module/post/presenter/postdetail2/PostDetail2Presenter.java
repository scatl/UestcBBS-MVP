package com.scatl.uestcbbs.module.post.presenter.postdetail2;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatEditText;

import com.bumptech.glide.Glide;
import com.scatl.uestcbbs.MyApplication;
import com.scatl.uestcbbs.R;
import com.scatl.uestcbbs.api.ApiConstant;
import com.scatl.uestcbbs.base.BasePreferenceFragment;
import com.scatl.uestcbbs.base.BasePresenter;
import com.scatl.uestcbbs.custom.imageview.CircleImageView;
import com.scatl.uestcbbs.custom.postview.ContentView;
import com.scatl.uestcbbs.entity.ContentViewBean;
import com.scatl.uestcbbs.entity.PostDetailBean;
import com.scatl.uestcbbs.entity.PostDianPingBean;
import com.scatl.uestcbbs.entity.PostWebBean;
import com.scatl.uestcbbs.entity.ReportBean;
import com.scatl.uestcbbs.entity.SupportResultBean;
import com.scatl.uestcbbs.entity.VoteResultBean;
import com.scatl.uestcbbs.helper.ExceptionHelper;
import com.scatl.uestcbbs.helper.rxhelper.Observer;
import com.scatl.uestcbbs.module.post.model.PostModel;
import com.scatl.uestcbbs.module.post.view.postdetail2.PostDetail2View;
import com.scatl.uestcbbs.module.webview.view.WebViewActivity;
import com.scatl.uestcbbs.util.CommonUtil;
import com.scatl.uestcbbs.util.Constant;
import com.scatl.uestcbbs.util.ForumUtil;
import com.scatl.uestcbbs.util.JsonUtil;
import com.scatl.uestcbbs.util.SharePrefUtil;
import com.scatl.uestcbbs.util.TimeUtil;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
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

    public void vote(int tid,
                     int boardId,
                     int max,
                     List<Integer> options,
                     Context context) {

        if (options.size() == 0) {
            view.onVoteError("至少选择1项");
        } else if (options.size() > max) {
            view.onVoteError("至多选择" + max + "项");
        } else {
            postModel.vote(tid, boardId, options.toString().replace("[", "").replace("]", ""),
                    SharePrefUtil.getToken(context),
                    SharePrefUtil.getSecret(context),
                    new Observer<VoteResultBean>() {
                        @Override
                        public void OnSuccess(VoteResultBean voteResultBean) {
                            if (voteResultBean.rs == ApiConstant.Code.SUCCESS_CODE) {
                                view.onVoteSuccess(voteResultBean);
                            }

                            if (voteResultBean.rs == ApiConstant.Code.ERROR_CODE) {
                                view.onVoteError(voteResultBean.head.errInfo);
                            }
                        }

                        @Override
                        public void onError(ExceptionHelper.ResponseThrowable e) {
                            view.onVoteError(e.message);
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
    }

    public void getVoteData(int topicId,
                            Context context) {
        postModel.getPostDetail(1, 0, 1, topicId, 0,
                SharePrefUtil.getToken(context),
                SharePrefUtil.getSecret(context),
                new Observer<PostDetailBean>() {
                    @Override
                    public void OnSuccess(PostDetailBean postDetailBean) {
                        if (postDetailBean.rs == ApiConstant.Code.SUCCESS_CODE) {
                            view.onGetNewVoteDataSuccess(postDetailBean.topic.poll_info);
                        }
                    }

                    @Override
                    public void onError(ExceptionHelper.ResponseThrowable e) { }

                    @Override
                    public void OnCompleted() { }

                    @Override
                    public void OnDisposable(Disposable d) {
                        disposable.add(d);
                    }
                });
    }

    public void getPostWebDetail(int tid, int page) {
        postModel.getPostWebDetail(tid, page, new Observer<String>() {
            @Override
            public void OnSuccess(String s) {

                if (s.contains("立即注册") && s.contains("找回密码") && s.contains("自动登录")) {

                } else {
                    try {

                        Document document = Jsoup.parse(s);

                        PostWebBean postWebBean = new PostWebBean();

                        postWebBean.favoriteNum = document.select("span[id=favoritenumber]").text();
                        postWebBean.formHash = document.select("form[id=scbar_form]").select("input[name=formhash]").attr("value");
                        postWebBean.rewardInfo = document.select("td[class=plc ptm pbm xi1]").text();
                        postWebBean.shengYuReword = document.select("td[class=pls vm ptm]").text();

                        postWebBean.originalCreate = document.select("div[id=threadstamp]").html().contains("原创");
                        postWebBean.essence = document.select("div[id=threadstamp]").html().contains("精华");

                        postWebBean.supportCount = Integer.parseInt(document.select("em[id=recommendv_add_digg]").text());
                        postWebBean.againstCount = Integer.parseInt(document.select("em[id=recommendv_sub_digg]").text());

                        view.onGetPostWebDetailSuccess(postWebBean);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
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

    public void getAllComment(int topicId,
                              Context context) {
        postModel.getPostDetail(1, 1000, 0, topicId, 0,
                SharePrefUtil.getToken(context),
                SharePrefUtil.getSecret(context),
                new Observer<PostDetailBean>() {
                    @Override
                    public void OnSuccess(PostDetailBean postDetailBean) {
                        if (postDetailBean.rs == ApiConstant.Code.SUCCESS_CODE) {
                            view.onGetAllPostSuccess(postDetailBean);
                        }

                        if (postDetailBean.rs == ApiConstant.Code.ERROR_CODE) {
                            view.onGetAllPostError(postDetailBean.head.errInfo);
                        }
                    }

                    @Override
                    public void onError(ExceptionHelper.ResponseThrowable e) {
                        view.onGetAllPostError(e.message);
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

    public void getDianPingList(int tid, int pid, int page) {
        postModel.getCommentList(tid, pid, page, new Observer<String>() {
            @Override
            public void OnSuccess(String s) {
                String html = s.replace("<?xml version=\"1.0\" encoding=\"utf-8\"?>", "")
                        .replace("<root><![CDATA[", "").replace("]]></root>", "");

                try {
                    List<PostDianPingBean> postDianPingBeans = new ArrayList<>();

                    Document document = Jsoup.parse(html);
                    Elements elements = document.select("div[class=pstl]");
                    for (int i = 0; i < elements.size(); i ++) {
                        PostDianPingBean postDianPingBean = new PostDianPingBean();
                        postDianPingBean.userName = elements.get(i).select("div[class=psti]").select("a[class=xi2 xw1]").text();
                        postDianPingBean.comment = elements.get(i).getElementsByClass("psti").get(0).text().replace(elements.get(i).select("div[class=psti]").select("span[class=xg1]").text(), "").replace(postDianPingBean.userName + " ", "");
                        postDianPingBean.date = elements.get(i).select("div[class=psti]").select("span[class=xg1]").text().replace("发表于 ", "");
                        postDianPingBean.uid = ForumUtil.getFromLinkInfo(elements.get(i).select("div[class=psti]").select("a[class=xi2 xw1]").attr("href")).id;
                        postDianPingBean.userAvatar = Constant.USER_AVATAR_URL + postDianPingBean.uid;

                        postDianPingBeans.add(postDianPingBean);
                    }

                    view.onGetPostDianPingListSuccess(postDianPingBeans, s.contains("下一页"));


                } catch (Exception e) {
                    view.onGetPostDianPingListError("获取点评失败：" + e.getMessage());
                }


            }

            @Override
            public void onError(ExceptionHelper.ResponseThrowable e) {
                view.onGetPostDianPingListError("获取点评失败：" + e.message);
            }

            @Override
            public void OnCompleted() { }

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

    public void report(String idType,
                       String message,
                       int id,
                       Context context) {
        postModel.report(idType, message, id,
                SharePrefUtil.getToken(context),
                SharePrefUtil.getSecret(context),
                new Observer<ReportBean>() {
                    @Override
                    public void OnSuccess(ReportBean reportBean) {
                        if (reportBean.rs == ApiConstant.Code.SUCCESS_CODE) {
                            view.onReportSuccess(reportBean);
                        }

                        if (reportBean.rs == ApiConstant.Code.ERROR_CODE) {
                            view.onReportError(reportBean.head.errInfo);
                        }
                    }

                    @Override
                    public void onError(ExceptionHelper.ResponseThrowable e) {
                        view.onReportError(e.message);
                    }

                    @Override
                    public void OnCompleted() {

                    }

                    @Override
                    public void OnDisposable(Disposable d) {
                        disposable.add(d);
//                        SubscriptionManager.getInstance().add(d);
                    }
                });
    }

    /**
     * @author: sca_tl
     * @description: 对于评论的更多操作
     * @date: 2020/10/5 21:45
     * @param context 上下文
     * @param formHash 论坛hash
     * @param fid 板块id
     * @param tid 帖子id
     * @param authorId 楼主id
     * @param listBean 评论数据
     * @return: void
     */
    public void moreReplyOptionsDialog(Context context, String formHash, int fid, int tid, int authorId,
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
        TextView stickText = options_view.findViewById(R.id.options_post_reply_stick_text);

        stickText.setText(listBean.poststick == 0 ? "置顶" : "取消置顶");
        buchong.setVisibility(listBean.reply_id == SharePrefUtil.getUid(context) ? View.VISIBLE : View.GONE);
        rate.setVisibility(listBean.reply_id == SharePrefUtil.getUid(context) ? View.GONE : View.VISIBLE);
        delete.setVisibility(listBean.reply_id == SharePrefUtil.getUid(context) ? View.VISIBLE : View.GONE);
        stick.setVisibility(authorId == SharePrefUtil.getUid(context) ? View.VISIBLE : View.GONE);
        modify.setVisibility(listBean.reply_id == SharePrefUtil.getUid(context) ? View.VISIBLE : View.GONE);
        against.setVisibility(listBean.reply_id == SharePrefUtil.getUid(context) ? View.GONE : View.VISIBLE);
        report.setVisibility(listBean.reply_id == SharePrefUtil.getUid(context) ? View.GONE : View.VISIBLE);


        final AlertDialog options_dialog = new AlertDialog.Builder(context)
                .setView(options_view)
                .create();

        options_dialog.show();

        stick.setOnClickListener(v -> {
            stickReply(formHash, fid, tid, listBean.poststick == 0, listBean.reply_posts_id);
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
            showReportDialog(context, listBean.reply_posts_id, "post");
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
        modify.setOnClickListener(v -> {
            Intent intent = new Intent(context, WebViewActivity.class);
            intent.putExtra(Constant.IntentKey.URL, "https://bbs.uestc.edu.cn/forum.php?mod=post&action=edit&tid=" + tid + "&pid=" + listBean.reply_posts_id);
            context.startActivity(intent);
        });
    }

    /**
     * author: sca_tl
     * description: 举报
     */
    public void showReportDialog(Context context, int id, String type) {
        final View report_view = LayoutInflater.from(context).inflate(R.layout.dialog_report, new RelativeLayout(context));
        final AppCompatEditText editText = report_view.findViewById(R.id.dialog_report_text);
        final RadioGroup radioGroup = report_view.findViewById(R.id.dialog_report_radio_group);

        final AlertDialog report_dialog = new AlertDialog.Builder(context)
                .setPositiveButton("确认举报", null)
                .setNegativeButton("取消", null)
                .setView(report_view)
                .setTitle("举报")
                .create();
        report_dialog.setOnShowListener(dialogInterface -> {
            Button p = report_dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            p.setOnClickListener(view -> {
                RadioButton radioButton = report_view.findViewById(radioGroup.getCheckedRadioButtonId());
                String s = radioButton.getText().toString();
                String msg = "[" + s + "]" + editText.getText().toString();
                report(type, msg, id, context);
                report_dialog.dismiss();
            });
        });
        report_dialog.show();
    }

    //获取点赞数大于等于指定值的所有评论
    public List<PostDetailBean.ListBean> getHotComment(PostDetailBean postDetailBean) {
        List<PostDetailBean.ListBean> hot = new ArrayList<>();

        for (int i = 0; i < postDetailBean.list.size(); i ++) {
            //不包含引用内容的回复，因为引用的文字显示不完整，容易让别人看着摸不着头脑，应该只是回复楼主的评论
            //但是有的评论本身就是引用了回复，但是is_quote为0，暂且不管
            if (postDetailBean.list.get(i).is_quote == 0 && "support".equals(postDetailBean.list.get(i).extraPanel.get(0).type) && postDetailBean.list.get(i).extraPanel.get(0).extParams.recommendAdd >= SharePrefUtil.getHotCommentZanThreshold(MyApplication.getContext())) {
                hot.add(postDetailBean.list.get(i));
            }
        }

        Collections.sort(hot, (o1, o2) -> o2.extraPanel.get(0).extParams.recommendAdd - o1.extraPanel.get(0).extParams.recommendAdd);

        return hot;
    }

}
