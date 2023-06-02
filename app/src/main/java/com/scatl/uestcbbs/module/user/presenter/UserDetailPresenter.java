package com.scatl.uestcbbs.module.user.presenter;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.appcompat.app.AlertDialog;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.scatl.uestcbbs.R;
import com.scatl.uestcbbs.api.ApiConstant;
import com.scatl.uestcbbs.base.BasePresenter;
import com.scatl.uestcbbs.entity.BlackUserBean;
import com.scatl.uestcbbs.entity.FollowUserBean;
import com.scatl.uestcbbs.entity.ModifyPswBean;
import com.scatl.uestcbbs.entity.ModifySignBean;
import com.scatl.uestcbbs.entity.UserDetailBean;
import com.scatl.uestcbbs.entity.UserFriendBean;
import com.scatl.uestcbbs.entity.VisitorsBean;
import com.scatl.uestcbbs.helper.ExceptionHelper;
import com.scatl.uestcbbs.helper.rxhelper.Observer;
import com.scatl.uestcbbs.module.user.model.UserModel;
import com.scatl.uestcbbs.module.user.view.ModifyAvatarActivity;
import com.scatl.uestcbbs.module.user.view.UserDetailView;
import com.scatl.uestcbbs.module.webview.view.WebViewActivity;
import com.scatl.uestcbbs.util.BBSLinkUtil;
import com.scatl.uestcbbs.util.ClipBoardUtil;
import com.scatl.uestcbbs.util.CommonUtil;
import com.scatl.uestcbbs.util.Constant;
import com.scatl.uestcbbs.util.SharePrefUtil;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.disposables.Disposable;


/**
 * author: sca_tl
 * description:
 * date: 2020/2/3 12:54
 */
public class UserDetailPresenter extends BasePresenter<UserDetailView> {

    private UserModel userModel = new UserModel();

    public void getUidByName(String name) {
        userModel.getUserSpaceByName(name, new Observer<String>() {
            @Override
            public void OnSuccess(String s) {
                try {
                    Document document = Jsoup.parse(s);
                    String url = document.select("div[class=wp cl]").select("div[id=nv]")
                            .select("ul").select("li").get(0).select("a").attr("href");
                    int uid = BBSLinkUtil.getLinkInfo(url).getId();
                    view.onGetSpaceByNameSuccess(uid);
                } catch (Exception e) {
                    view.onGetSpaceByNameError(e.getMessage());
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(ExceptionHelper.ResponseThrowable e) {
                view.onGetSpaceByNameError(e.message);
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

    public void getUserDetail(int uid, Context context) {
        userModel.getUserDetail(uid, new Observer<UserDetailBean>() {
            @Override
            public void OnSuccess(UserDetailBean userDetailBean) {
                if (userDetailBean.rs == ApiConstant.Code.SUCCESS_CODE) {
                    view.onGetUserDetailSuccess(userDetailBean);
                }
                if (userDetailBean.rs == ApiConstant.Code.ERROR_CODE) {
                    view.onGetUserDetailError(userDetailBean.head.errInfo);
                }
            }

            @Override
            public void onError(ExceptionHelper.ResponseThrowable e) {
                view.onGetUserDetailError(e.message);
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

    public void followUser(int uid, String type, Context context) {
        userModel.followUser(uid, type, new Observer<FollowUserBean>() {
            @Override
            public void OnSuccess(FollowUserBean followUserBean) {
                if (followUserBean.rs == ApiConstant.Code.SUCCESS_CODE) {
                    view.onFollowUserSuccess(followUserBean);
                }
                if (followUserBean.rs == ApiConstant.Code.ERROR_CODE) {
                    view.onFollowUserError(followUserBean.head.errInfo);
                }
            }

            @Override
            public void onError(ExceptionHelper.ResponseThrowable e) {
                view.onFollowUserError(e.message);
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

    public void blackUser(int uid, String type, Context context) {
        userModel.blackUser(uid, type, new Observer<BlackUserBean>() {
            @Override
            public void OnSuccess(BlackUserBean blackUserBean) {
                if (blackUserBean.rs == ApiConstant.Code.SUCCESS_CODE) {
                    view.onBlackUserSuccess(blackUserBean);
                }
                if (blackUserBean.rs == ApiConstant.Code.ERROR_CODE) {
                    view.onBlackUserError(blackUserBean.head.errInfo);
                }
            }

            @Override
            public void onError(ExceptionHelper.ResponseThrowable e) {
                view.onBlackUserError(e.message);
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

    public void modifySign(String sign, Context context) {
        userModel.modifySign("info", sign, new Observer<ModifySignBean>() {
            @Override
            public void OnSuccess(ModifySignBean modifySignBean) {
                if (modifySignBean.rs == ApiConstant.Code.SUCCESS_CODE) {
                    view.onModifySignSuccess(modifySignBean, sign);
                }
                if (modifySignBean.rs == ApiConstant.Code.ERROR_CODE) {
                    view.onModifySignError(modifySignBean.head.errInfo);
                }
            }

            @Override
            public void onError(ExceptionHelper.ResponseThrowable e) {
                view.onModifySignError(e.message);
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

    public void modifyPsw(String oldPsw, String newPsw, Context context) {
        userModel.modifyPsw("password", oldPsw, newPsw, new Observer<ModifyPswBean>() {
            @Override
            public void OnSuccess(ModifyPswBean modifyPswBean) {
                if (modifyPswBean.rs == ApiConstant.Code.SUCCESS_CODE) {
                    view.onModifyPswSuccess(modifyPswBean);
                }
                if (modifyPswBean.rs == ApiConstant.Code.ERROR_CODE) {
                    view.onModifyPswError(modifyPswBean.head.errInfo);
                }
            }

            @Override
            public void onError(ExceptionHelper.ResponseThrowable e) {
                view.onModifyPswError(e.message);
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

    public void getUserSpace(int uid, Context context) {
        userModel.getUserSpace(uid, "", new Observer<String>() {
            @Override
            public void OnSuccess(String s) {
                try {

                    Document document = Jsoup.parse(s);
                    Elements visitor_elements = document.select("div[id=visitor]").select("ul[class=ml mls cl]").select("li");

                    List<VisitorsBean> visitorsBeans = new ArrayList<>();
                    for (int i = 0; i < visitor_elements.size(); i ++) {
                        VisitorsBean visitorsBean = new VisitorsBean();
                        visitorsBean.visitedTime = visitor_elements.get(i).select("span[class=xg2]").text();
                        visitorsBean.visitorName = visitor_elements.get(i).select("p").select("a").text();
                        visitorsBean.visitorUid = BBSLinkUtil.getLinkInfo(visitor_elements.get(i).select("p").select("a").attr("href")).getId();
                        visitorsBean.visitorAvatar = Constant.USER_AVATAR_URL + visitorsBean.visitorUid;
                        visitorsBeans.add(visitorsBean);
                    }

                    List<String> medalImages = new ArrayList<>();
                    Elements medal_elements = document.select("p[class=md_ctrl]").select("a").select("img");
                    for (int i = 0; i < medal_elements.size(); i ++) {
                        medalImages.add(ApiConstant.BBS_BASE_URL + medal_elements.get(i).attr("src"));
                    }
                    view.onGetUserSpaceSuccess(visitorsBeans, medalImages);
                } catch (Exception e) {
                    view.onGetUserSpaceError(e.getMessage());
                }
            }

            @Override
            public void onError(ExceptionHelper.ResponseThrowable e) {
                view.onGetUserSpaceError(e.message);
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

    public void getUserFriend(int uid, String type, Context context) {
        userModel.getUserFriend(1, 1000, uid, type, new Observer<UserFriendBean>() {
            @Override
            public void OnSuccess(UserFriendBean userFriendBean) {
                if (userFriendBean.rs == ApiConstant.Code.SUCCESS_CODE) {
                    view.onGetUserFriendSuccess(userFriendBean);
                }

                if (userFriendBean.rs == ApiConstant.Code.ERROR_CODE) {
                    view.onGetUserFriendError(userFriendBean.head.errInfo);
                }
            }

            @Override
            public void onError(ExceptionHelper.ResponseThrowable e) {
                view.onGetUserFriendError(e.message);
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

    public void showModifyInfoDialog(Context context) {
        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_modify_user_info, new LinearLayout(context));
        LinearLayout modifyPsw = dialogView.findViewById(R.id.dialog_modify_user_info_modify_psw_layout);
        LinearLayout modifySign = dialogView.findViewById(R.id.dialog_modify_user_info_modify_sign_layout);
        LinearLayout modifyOther = dialogView.findViewById(R.id.dialog_modify_user_info_modify_other_layout);
        LinearLayout modifyAvatar = dialogView.findViewById(R.id.dialog_modify_user_info_modify_avatar_layout);
        AlertDialog dialog = new MaterialAlertDialogBuilder(context)
                .setView(dialogView)
                .create();
        dialog.show();
        modifyAvatar.setOnClickListener(v -> {
            dialog.dismiss();
            context.startActivity(new Intent(context, ModifyAvatarActivity.class));
        });
        modifySign.setOnClickListener(v -> {
            dialog.dismiss();
            showModifySignDialog("", context);
        });
        modifyPsw.setOnClickListener(v -> {
            dialog.dismiss();
            showModifyPswDialog(context);
        });
        modifyOther.setOnClickListener(v -> {
            dialog.dismiss();
            Intent intent = new Intent(context, WebViewActivity.class);
            intent.putExtra(Constant.IntentKey.URL, "http://bbs.uestc.edu.cn/mobcent/app/web/index.php?r=user/userinfoadminview" +
                    "&accessToken=" + SharePrefUtil.getToken(context) +
                    "&accessSecret=" + SharePrefUtil.getSecret(context) +
                    "&act=info");
            context.startActivity(intent);
        });
    }

    public void showModifyPswDialog(Context context) {
        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_modify_psw, new LinearLayout(context));
        EditText oldPsw = dialogView.findViewById(R.id.dialog_modify_psw_old_psw);
        EditText newPsw = dialogView.findViewById(R.id.dialog_modify_psw_new_psw);
        EditText confirmPsw = dialogView.findViewById(R.id.dialog_modify_psw_confirm_psw);

        CommonUtil.showSoftKeyboard(context, oldPsw, 1);
        AlertDialog dialog = new MaterialAlertDialogBuilder(context)
                .setPositiveButton("确认", null)
                .setNegativeButton("取消", null)
                .setView(dialogView)
                .setTitle("修改密码")
                .create();
        dialog.setOnShowListener(d -> {
            Button p = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            p.setOnClickListener(v -> {
                if (TextUtils.isEmpty(oldPsw.getText().toString())) {
                    view.onModifyPswError("请输入旧密码");
                } else if (TextUtils.isEmpty(newPsw.getText().toString())){
                    view.onModifyPswError("请输入新密码");
                } else if (TextUtils.isEmpty(confirmPsw.getText().toString())) {
                    view.onModifyPswError("请确认密码");
                } else if (!newPsw.getText().toString().equals(confirmPsw.getText().toString())){
                    view.onModifyPswError("新密码不一致");
                } else {
                    modifyPsw(oldPsw.getText().toString(), newPsw.getText().toString(), context);
                    dialog.dismiss();
                }
            });
        });
        dialog.show();
    }


    public void showModifySignDialog(String sign, Context context) {
        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_modify_sign, new LinearLayout(context));
        EditText content = dialogView.findViewById(R.id.dialog_modify_sign_content);
        CommonUtil.showSoftKeyboard(context, content, 1);
        content.setText(sign);
        AlertDialog dialog = new MaterialAlertDialogBuilder(context)
                .setPositiveButton("确认", null)
                .setNegativeButton("取消", null)
                .setView(dialogView)
                .setTitle("修改签名")
                .create();
        dialog.setOnShowListener(d -> {
            Button p = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            p.setOnClickListener(v -> {
                if (TextUtils.isEmpty(content.getText().toString())) {
                    view.onModifySignError("请输入签名内容");
                } else {
                    modifySign(content.getText().toString(), context);
                    dialog.dismiss();
                }
            });
        });
        dialog.show();
    }

    public void showUserSignDialog(String sign, Context context) {
        AlertDialog dialog = new MaterialAlertDialogBuilder(context)
                .setTitle("查看签名")
                .setPositiveButton("复制", null)
                .setMessage(sign)
                .create();
        dialog.setOnShowListener(d -> {
            Button p = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            p.setOnClickListener(v -> {
                ClipBoardUtil.copyToClipBoard(context, sign);
                dialog.dismiss();
            });
        });
        dialog.show();
    }

    /**
     * author: sca_tl
     * description: 展示用户资料
     */
    public void showUserInfo(UserDetailBean userDetailBean, boolean property, Context context) {
        AlertDialog user_info_dialog = new MaterialAlertDialogBuilder(context)
//                .setPositiveButton("确认", null)
//                .setNegativeButton("取消", null)
                .setTitle(property ? "财富信息" : "其它资料")
                .create();
        if (!property) {
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < userDetailBean.body.profileList.size(); i ++) {
                String title = userDetailBean.body.profileList.get(i).title;
                String data = userDetailBean.body.profileList.get(i).data + "";
                builder.append(title).append("：").append(data).append("\n");
            }
            user_info_dialog.setMessage(builder);

        } else {
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < userDetailBean.body.creditList.size(); i ++) {
                String title = userDetailBean.body.creditList.get(i).title;
                String data = userDetailBean.body.creditList.get(i).data + "";
                builder.append(title).append("：").append(data).append("\n");
            }
            user_info_dialog.setMessage(builder);
        }

        user_info_dialog.show();

    }

    public void showBlackConfirmDialog(Context context, int uid) {
        AlertDialog dialog = new MaterialAlertDialogBuilder(context)
                .setPositiveButton("确认", null)
                .setNegativeButton("取消", null)
                .setTitle("加入黑名单")
                .setMessage(context.getString(R.string.black_list_desp))
                .create();
        dialog.setOnShowListener(d -> {
            Button p = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            p.setOnClickListener(v -> {
                blackUser(uid, "black", context);
                dialog.dismiss();
            });
        });
        dialog.show();
    }

}
