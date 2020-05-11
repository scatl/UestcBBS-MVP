package com.scatl.uestcbbs.module.post.presenter;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.google.android.material.textfield.TextInputEditText;
import com.scatl.uestcbbs.R;
import com.scatl.uestcbbs.api.ApiConstant;
import com.scatl.uestcbbs.base.BasePresenter;
import com.scatl.uestcbbs.custom.MyLinearLayoutManger;
import com.scatl.uestcbbs.custom.imageview.CircleImageView;
import com.scatl.uestcbbs.custom.postview.ContentView;
import com.scatl.uestcbbs.custom.postview.adapter.PostContentMultiAdapter;
import com.scatl.uestcbbs.entity.ContentViewBean;
import com.scatl.uestcbbs.entity.FavoritePostResultBean;
import com.scatl.uestcbbs.entity.HistoryBean;
import com.scatl.uestcbbs.entity.PostDetailBean;
import com.scatl.uestcbbs.entity.ReportBean;
import com.scatl.uestcbbs.entity.SimplePostListBean;
import com.scatl.uestcbbs.entity.SupportResultBean;
import com.scatl.uestcbbs.entity.VoteResultBean;
import com.scatl.uestcbbs.helper.ExceptionHelper;
import com.scatl.uestcbbs.helper.glidehelper.GlideLoader4Common;
import com.scatl.uestcbbs.helper.rxhelper.Observer;
import com.scatl.uestcbbs.helper.rxhelper.SubscriptionManager;
import com.scatl.uestcbbs.module.post.model.PostModel;
import com.scatl.uestcbbs.module.post.model.Rate;
import com.scatl.uestcbbs.module.post.model.RateInfo;
import com.scatl.uestcbbs.module.post.view.PostDetailActivity;
import com.scatl.uestcbbs.module.post.view.PostDetailView;
import com.scatl.uestcbbs.module.user.view.UserDetailActivity;
import com.scatl.uestcbbs.module.webview.view.WebViewActivity;
import com.scatl.uestcbbs.util.CommonUtil;
import com.scatl.uestcbbs.util.Constant;
import com.scatl.uestcbbs.util.JsonUtil;
import com.scatl.uestcbbs.util.SharePrefUtil;
import com.scatl.uestcbbs.util.TimeUtil;
import com.scatl.uestcbbs.util.ToastUtil;
import com.zhy.view.flowlayout.FlowLayout;
import com.zhy.view.flowlayout.TagAdapter;
import com.zhy.view.flowlayout.TagFlowLayout;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.reactivex.disposables.Disposable;
import okhttp3.Call;

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
                        SubscriptionManager.getInstance().add(d);
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
                        SubscriptionManager.getInstance().add(d);
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
                        SubscriptionManager.getInstance().add(d);
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
                            SubscriptionManager.getInstance().add(d);
                        }
                    });

        }


    }

    public void getRateInfo(int tid,
                            int pid,
                            Context context) {
        postModel.getRateInfo(tid, pid,
                SharePrefUtil.getToken(context),
                SharePrefUtil.getSecret(context),
                new Observer<String>() {
                    @Override
                    public void OnSuccess(String html) {
                        view.onGetRateInfoSuccess(html);
                    }

                    @Override
                    public void onError(ExceptionHelper.ResponseThrowable e) {
                        view.onGetRateInfoError(e.message);
                    }

                    @Override
                    public void OnCompleted() {

                    }

                    @Override
                    public void OnDisposable(Disposable d) {
                        SubscriptionManager.getInstance().add(d);
                    }
                });
    }

    public void rate(int tid,
                     int pid,
                     int score,
                     String reason,
                     String sendreasonpm,
                     Context context) {
        postModel.rate(tid, pid, score, reason, sendreasonpm,
                SharePrefUtil.getToken(context),
                SharePrefUtil.getSecret(context),
                new Observer<String>() {
                    @Override
                    public void OnSuccess(String html) {
                        Rate rate = Rate.rate(html);
                        if (rate.successful) {
                            view.onRateSuccess(rate.info);
                        } else {
                            view.onRateError(rate.info);
                        }
                    }

                    @Override
                    public void onError(ExceptionHelper.ResponseThrowable e) {
                        view.onRateError(e.message);
                    }

                    @Override
                    public void OnCompleted() {

                    }

                    @Override
                    public void OnDisposable(Disposable d) {
                        SubscriptionManager.getInstance().add(d);
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
                        SubscriptionManager.getInstance().add(d);
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
                        SubscriptionManager.getInstance().add(d);
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
                            (postDetailBean.topic.zanList.size()-postDetailBean.topic.extraPanel.get(i).extParams.recommendAdd)+"人反对）"));
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
     * description: 评分
     */
    public void showRateDialog(int tid, int pid,  RateInfo rateInfo, Context context){
        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_rate, new LinearLayout(context));
        TextView total = dialogView.findViewById(R.id.dialog_rate_total_shuidi);
        Spinner shuidiSpinner = dialogView.findViewById(R.id.dialog_rate_spinner);
        Spinner reasonSpinner = dialogView.findViewById(R.id.dialog_rate_default_reason_spinner);
        EditText reason = dialogView.findViewById(R.id.dialog_rate_reason);
        CheckBox checkBox = dialogView.findViewById(R.id.dialog_rate_notify);

        reasonSpinner.setOnItemSelectedListener(new Spinner.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                reason.setText(reasonSpinner.getSelectedItem().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });

        total.setText("水滴（今日还剩"+rateInfo.todayTotal+"水滴）：");

        String[] spinnerItem = new String[rateInfo.maxScore - rateInfo.minScore + 1];
        for (int i = 0; i <= rateInfo.maxScore - rateInfo.minScore; i ++){
            spinnerItem[i] = String.valueOf((rateInfo.minScore + i) + "水滴");
        }

        ArrayAdapter<String> spinner_adapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, spinnerItem);
        spinner_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        shuidiSpinner.setAdapter(spinner_adapter);
        shuidiSpinner.setSelection(6);

        AlertDialog dialog = new AlertDialog.Builder(context)
                .setPositiveButton("确认", null)
                .setNegativeButton("取消", null)
                .setView(dialogView)
                .setTitle("评分")
                .create();
        dialog.setOnShowListener(d -> {
            Button p = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            p.setOnClickListener(v -> {
                rate(tid, pid, Integer.valueOf(spinnerItem[shuidiSpinner.getSelectedItemPosition()].replace("水滴", "")),
                        reason.getText().toString(), checkBox.isChecked() ? "on" : "", context
                        );
                dialog.dismiss();
            });
        });
        dialog.show();
    }

    /**
     * author: sca_tl
     * description: 举报
     */
    public void showReportDialog(Context context, int id) {
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
                report("thread", msg, id, context);
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
