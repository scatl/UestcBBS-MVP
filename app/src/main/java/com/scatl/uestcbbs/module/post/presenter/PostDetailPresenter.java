package com.scatl.uestcbbs.module.post.presenter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
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
import com.scatl.uestcbbs.R;
import com.scatl.uestcbbs.api.ApiConstant;
import com.scatl.uestcbbs.base.BasePresenter;
import com.scatl.uestcbbs.custom.imageview.CircleImageView;
import com.scatl.uestcbbs.custom.postview.ContentView;
import com.scatl.uestcbbs.entity.ContentViewBean;
import com.scatl.uestcbbs.entity.FavoritePostResultBean;
import com.scatl.uestcbbs.entity.HistoryBean;
import com.scatl.uestcbbs.entity.PostDetailBean;
import com.scatl.uestcbbs.entity.PostDianPingBean;
import com.scatl.uestcbbs.entity.ReportBean;
import com.scatl.uestcbbs.entity.SupportResultBean;
import com.scatl.uestcbbs.entity.VoteResultBean;
import com.scatl.uestcbbs.helper.ExceptionHelper;
import com.scatl.uestcbbs.helper.glidehelper.GlideLoader4Common;
import com.scatl.uestcbbs.helper.rxhelper.Observer;
import com.scatl.uestcbbs.module.post.model.PostModel;
import com.scatl.uestcbbs.module.post.view.PostAppendFragment;
import com.scatl.uestcbbs.module.post.view.PostDetailView;
import com.scatl.uestcbbs.module.user.view.UserDetailActivity;
import com.scatl.uestcbbs.module.webview.view.WebViewActivity;
import com.scatl.uestcbbs.util.Constant;
import com.scatl.uestcbbs.util.ForumUtil;
import com.scatl.uestcbbs.util.JsonUtil;
import com.scatl.uestcbbs.util.SharePrefUtil;
import com.scatl.uestcbbs.util.TimeUtil;
import com.zhy.view.flowlayout.FlowLayout;
import com.zhy.view.flowlayout.TagAdapter;
import com.zhy.view.flowlayout.TagFlowLayout;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.reactivex.disposables.Disposable;

/**
 * author: sca_tl
 * description:
 * date: 2020/1/24 14:33
 */
public class PostDetailPresenter extends BasePresenter<PostDetailView> {

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

    public void favorite(String idType,
                         String action,
                         int id,
                         Context context) {
        postModel.favorite(idType, action, id,
                SharePrefUtil.getToken(context),
                SharePrefUtil.getSecret(context),
                new Observer<FavoritePostResultBean>() {
                    @Override
                    public void OnSuccess(FavoritePostResultBean favoritePostResultBean) {
                        if (favoritePostResultBean.rs == ApiConstant.Code.SUCCESS_CODE) {
                            view.onFavoritePostSuccess(favoritePostResultBean);
                        }

                        if (favoritePostResultBean.rs == ApiConstant.Code.ERROR_CODE) {
                            view.onFavoritePostError(favoritePostResultBean.head.errInfo);
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


    public void support(int tid,
                        int pid,
                        String type,
                        int position,
                        Context context) {
        postModel.support(tid, pid, type,
                SharePrefUtil.getToken(context),
                SharePrefUtil.getSecret(context),
                new Observer<SupportResultBean>() {
                    @Override
                    public void OnSuccess(SupportResultBean supportResultBean) {
                        if (supportResultBean.rs == ApiConstant.Code.SUCCESS_CODE) {
                            view.onSupportSuccess(supportResultBean, type, position);
                        }

                        if (supportResultBean.rs == ApiConstant.Code.ERROR_CODE) {
                            view.onSupportError(supportResultBean.head.errInfo);
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
//                        SubscriptionManager.getInstance().add(d);
                    }
                });
    }

    public void getDianPingList(int tid, int pid, int page) {
        postModel.getCommentList(tid, pid, page, new Observer<String>() {
            @Override
            public void OnSuccess(String s) {
//                Log.e("ppppp", s);
                String html = s.replace("<?xml version=\"1.0\" encoding=\"utf-8\"?>", "")
                        .replace("<root><![CDATA[", "").replace("]]></root>", "");

                try {
                    List<PostDianPingBean> postDianPingBeans = new ArrayList<>();

                    Document document = Jsoup.parse(html);
                    Elements elements = document.select("div[class=pstl]");
                    for (int i = 0; i < elements.size(); i ++) {
                        PostDianPingBean postDianPingBean = new PostDianPingBean();
                        postDianPingBean.userAvatar = new StringBuilder().append("http://").append(elements.get(i).select("div[class=psta]").select("img").attr("src").replace("//", "").replace("small", "middle")).toString();
                        postDianPingBean.userName = elements.get(i).select("div[class=psti]").select("a[class=xi2 xw1]").text();
                        postDianPingBean.comment = elements.get(i).getElementsByClass("psti").get(0).ownText();
                        postDianPingBean.date = elements.get(i).select("div[class=psti]").select("span[class=xg1]").text().replace("发表于 ", "");
                        postDianPingBean.uid = ForumUtil.getFromLinkInfo(elements.get(i).select("div[class=psti]").select("a[class=xi2 xw1]").attr("href")).id;

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

    public void getPostWebDetail(int tid, int page) {
        postModel.getPostWebDetail(tid, page, new Observer<String>() {
            @Override
            public void OnSuccess(String s) {

                if (s.contains("立即注册") && s.contains("找回密码") && s.contains("自动登录")) {

                } else {
                    try {

                        Document document = Jsoup.parse(s);
                        String favoriteNum = document.select("span[id=favoritenumber]").text();
                        String formHash = document.select("form[id=scbar_form]").select("input[name=formhash]").attr("value");
                        view.onGetPostWebDetailSuccess(favoriteNum, formHash);

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


//        RecyclerView recyclerView = basicView.findViewById(R.id.post_detail_item_content_view_rv);
//        recyclerView.setLayoutManager(new MyLinearLayoutManger(activity));
//        recyclerView.setAdapter(new PostContentMultiAdapter(JsonUtil.modelListA2B(postDetailBean.topic.content, ContentViewBean.class, postDetailBean.topic.content.size())));
    }

    /**
     * author: sca_tl
     * description: 展示表达意见的用户
     */
    public void setZanView(Context context, View zanView, PostDetailBean postDetailBean) {
        TagFlowLayout zanFlowLayout = zanView.findViewById(R.id.post_detail_item_zanlist_view_taglayout);
        TextView zanViewTitle = zanView.findViewById(R.id.post_detail_item_zanlist_view_title);
        TextView subTitle = zanView.findViewById(R.id.post_detail_item_zanlist_view_subtitle);
        if (postDetailBean.topic.zanList == null || postDetailBean.topic.zanList.size() == 0) {
            zanView.setVisibility(View.GONE);
        } else {
            zanView.setVisibility(View.VISIBLE);
            zanViewTitle.setText("•支持或反对•");
            for (int i = 0; i < postDetailBean.topic.extraPanel.size(); i ++) {
                if (postDetailBean.topic.extraPanel.get(i).type.equals("support")) {
                    subTitle.setText(String.valueOf("（" + postDetailBean.topic.extraPanel.get(i).extParams.recommendAdd + "人支持，" +
                            (postDetailBean.topic.zanList.size()-postDetailBean.topic.extraPanel.get(i).extParams.recommendAdd < 0 ? "0" :
                                    postDetailBean.topic.zanList.size()-postDetailBean.topic.extraPanel.get(i).extParams.recommendAdd)+"人反对）"));
                }
            }

            zanFlowLayout.setAdapter(new TagAdapter<PostDetailBean.TopicBean.ZanListBean>(postDetailBean.topic.zanList) {
                @Override
                public View getView(FlowLayout parent, int position, PostDetailBean.TopicBean.ZanListBean o) {
                    View view = LayoutInflater.from(context)
                            .inflate(R.layout.item_post_detail_item_zanlist_view, new LinearLayout(context));
                    CircleImageView imageView = view.findViewById(R.id.item_post_detail_item_zanlist_view_avatar);
                    TextView textView = view.findViewById(R.id.item_post_detail_item_zanlist_view_name);
                    GlideLoader4Common.simpleLoad(context, context.getString(R.string.icon_url, Integer.valueOf(o.recommenduid)), imageView);
                    textView.setText(o.username);
                    return view;
                }
            });
            zanFlowLayout.setOnTagClickListener((view, position, parent) -> {
                Intent intent = new Intent(context, UserDetailActivity.class);
                intent.putExtra(Constant.IntentKey.USER_ID, Integer.valueOf(postDetailBean.topic.zanList.get(position).recommenduid));
                context.startActivity(intent);
                return true;
            });

        }
    }

    /**
     * author: sca_tl
     * description: 展示评分用户
     */
    public void setRateData(Context context, View rateView, PostDetailBean postDetailBean) {
        TextView rateViewTitle = rateView.findViewById(R.id.post_detail_item_rate_view_title);
        TextView shuidiNum = rateView.findViewById(R.id.post_detail_item_rate_view_shuidi_num);
        LinearLayout shuidiLayout = rateView.findViewById(R.id.post_detail_item_rate_view_shuidi_layout);
        TextView weiwangNum = rateView.findViewById(R.id.post_detail_item_rate_view_weiwang_num);
        LinearLayout weiwangLayout = rateView.findViewById(R.id.post_detail_item_rate_view_weiwang_layout);
        LinearLayout more = rateView.findViewById(R.id.post_detail_rate_view_more);
        TagFlowLayout zanFlowLayout = rateView.findViewById(R.id.post_detail_item_rate_view_taglayout);

        if (postDetailBean.topic.reward == null) {
            rateView.setVisibility(View.GONE);
        } else {
            rateView.setVisibility(View.VISIBLE);
            rateViewTitle.setText("•评分(" + postDetailBean.topic.reward.userNumber + ")•");

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
                Intent intent = new Intent(context, WebViewActivity.class);
                intent.putExtra(Constant.IntentKey.URL, postDetailBean.topic.reward.showAllUrl);
                context.startActivity(intent);
            });

            zanFlowLayout.setAdapter(new TagAdapter<PostDetailBean.TopicBean.RewardBean.UserListBean>(postDetailBean.topic.reward.userList) {
                @Override
                public View getView(FlowLayout parent, int position, PostDetailBean.TopicBean.RewardBean.UserListBean o) {
                    View view = LayoutInflater.from(context)
                            .inflate(R.layout.item_post_detail_item_zanlist_view, new LinearLayout(context));
                    CircleImageView imageView = view.findViewById(R.id.item_post_detail_item_zanlist_view_avatar);
                    TextView textView = view.findViewById(R.id.item_post_detail_item_zanlist_view_name);
                    GlideLoader4Common.simpleLoad(context, o.userIcon, imageView);
                    textView.setText(o.userName);
                    return view;
                }
            });
            zanFlowLayout.setOnTagClickListener((view, position, parent) -> {
                Intent intent = new Intent(context, UserDetailActivity.class);
                intent.putExtra(Constant.IntentKey.USER_ID, postDetailBean.topic.reward.userList.get(position).uid);
                context.startActivity(intent);
                return true;
            });

        }
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

    /**
     * author: sca_tl
     * description: 管理员操作
     */
    public void showAdminDialog(Context context, int fid, int tid, int pid) {
        final View admin_view = LayoutInflater.from(context).inflate(R.layout.dialog_post_admin_action, new LinearLayout(context));

        LinearLayout band = admin_view.findViewById(R.id.dialog_post_admin_action_band);
        LinearLayout top = admin_view.findViewById(R.id.dialog_post_admin_action_top);
        LinearLayout marrow = admin_view.findViewById(R.id.dialog_post_admin_action_marrow);
        LinearLayout open = admin_view.findViewById(R.id.dialog_post_admin_action_open_or_close);
        LinearLayout move = admin_view.findViewById(R.id.dialog_post_admin_action_move);
        LinearLayout delete = admin_view.findViewById(R.id.dialog_post_admin_action_delete);

        final AlertDialog admin_dialog = new AlertDialog.Builder(context)
                .setView(admin_view)
                .create();
        admin_dialog.show();

        String url = ApiConstant.BBS_BASE_URL + ApiConstant.Post.ADMIN_VIEW +
                "&accessToken=" + SharePrefUtil.getToken(context) +
                "&accessSecret=" + SharePrefUtil.getSecret(context) +
                "&fid=" + fid + "&tid=" + tid + "&pid=" + pid + "&type=topic&act=";

        band.setOnClickListener(v -> {
            Intent intent = new Intent(context, WebViewActivity.class);
            intent.putExtra(Constant.IntentKey.URL, url + "band");
            context.startActivity(intent);
            admin_dialog.dismiss();
        });

        top.setOnClickListener(v -> {
            Intent intent = new Intent(context, WebViewActivity.class);
            intent.putExtra(Constant.IntentKey.URL, url + "top");
            context.startActivity(intent);
            admin_dialog.dismiss();
        });

        marrow.setOnClickListener(v -> {
            Intent intent = new Intent(context, WebViewActivity.class);
            intent.putExtra(Constant.IntentKey.URL, url + "marrow");
            context.startActivity(intent);
            admin_dialog.dismiss();
        });

        open.setOnClickListener(v -> {
            Intent intent = new Intent(context, WebViewActivity.class);
            intent.putExtra(Constant.IntentKey.URL, url + "open");
            context.startActivity(intent);
            admin_dialog.dismiss();
        });

        move.setOnClickListener(v -> {
            Intent intent = new Intent(context, WebViewActivity.class);
            intent.putExtra(Constant.IntentKey.URL, url + "move");
            context.startActivity(intent);
            admin_dialog.dismiss();
        });

        delete.setOnClickListener(v -> {
            Intent intent = new Intent(context, WebViewActivity.class);
            intent.putExtra(Constant.IntentKey.URL, url + "delete");
            context.startActivity(intent);
            admin_dialog.dismiss();
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
     * @param listBean 评论数据
     * @return: void
     */
    public void moreReplyOptionsDialog(Context context, String formHash, int fid, int tid,
                                       PostDetailBean.ListBean listBean) {
        final View options_view = LayoutInflater.from(context).inflate(R.layout.dialog_post_reply_options, new LinearLayout(context));
        View stick = options_view.findViewById(R.id.options_post_reply_stick);
        View rate = options_view.findViewById(R.id.options_post_reply_rate);
        View report = options_view.findViewById(R.id.options_post_reply_report);
        View onlyAuthor = options_view.findViewById(R.id.options_post_reply_only_author);
        View buchong = options_view.findViewById(R.id.options_post_reply_buchong);
        TextView stickText = options_view.findViewById(R.id.options_post_reply_stick_text);

        stickText.setText(listBean.poststick == 0 ? "置顶" : "取消置顶");
        buchong.setVisibility(listBean.reply_id == SharePrefUtil.getUid(context) ? View.VISIBLE : View.GONE);
        rate.setVisibility(listBean.reply_id == SharePrefUtil.getUid(context) ? View.GONE : View.VISIBLE);

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
    }

    /**
     * author: sca_tl
     * description: 保存浏览历史
     */
    public void saveHistory(PostDetailBean postDetailBean) {
        HistoryBean historyBean = new HistoryBean();
        historyBean.browserTime = TimeUtil.getLongMs();
        historyBean.topic_id =  postDetailBean.topic.topic_id;
        historyBean.title = postDetailBean.topic.title;
        historyBean.userAvatar = postDetailBean.topic.icon;
        historyBean.user_nick_name = postDetailBean.topic.user_nick_name;
        historyBean.user_id = postDetailBean.topic.user_id;
        historyBean.board_id = postDetailBean.boardId;
        historyBean.board_name = postDetailBean.forumName;
        historyBean.hits = postDetailBean.topic.hits;
        historyBean.replies = postDetailBean.topic.replies;
        historyBean.last_reply_date = postDetailBean.topic.create_date;

        for (int i = 0; i < postDetailBean.topic.content.size(); i ++) {
            if (postDetailBean.topic.content.get(i).type == 0) {
                historyBean.subject = postDetailBean.topic.content.get(i).infor;
                break;
            }
        }

        historyBean.saveOrUpdate("topic_id = ?", String.valueOf(postDetailBean.topic.topic_id));
    }

}
