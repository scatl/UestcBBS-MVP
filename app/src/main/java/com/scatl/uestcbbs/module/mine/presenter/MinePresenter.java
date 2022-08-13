package com.scatl.uestcbbs.module.mine.presenter;

import android.content.Context;
import android.widget.Button;

import androidx.appcompat.app.AlertDialog;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.scatl.uestcbbs.base.BaseEvent;
import com.scatl.uestcbbs.base.BasePresenter;
import com.scatl.uestcbbs.entity.AccountBean;
import com.scatl.uestcbbs.entity.UserGroupBean;
import com.scatl.uestcbbs.entity.UserLevel;
import com.scatl.uestcbbs.helper.ExceptionHelper;
import com.scatl.uestcbbs.helper.rxhelper.Observer;
import com.scatl.uestcbbs.module.mine.model.MineModel;
import com.scatl.uestcbbs.module.mine.view.MineView;
import com.scatl.uestcbbs.util.SharePrefUtil;

import org.greenrobot.eventbus.EventBus;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.reactivex.disposables.Disposable;

public class MinePresenter extends BasePresenter<MineView> {

    private MineModel mineModel = new MineModel();

    public void userGroup() {
        mineModel.userGroup(new Observer<String>() {
            @Override
            public void OnSuccess(String string) {

                try {
                    if (!string.contains("先登录才能")) {
                        UserGroupBean userGroupBean = new UserGroupBean();

                        Document document = Jsoup.parse(string);

                        String cc = document.select("div[class=bm bw0]").select("div[class=tdats]").select("table[class=tdat tfx]").select("th[class=alt]").select("span[class=notice]").text();
                        Matcher m = Pattern.compile(".*?([0-9]+).*?").matcher(cc);
                        userGroupBean.currentCredit = m.matches() ? Integer.parseInt(m.group(1)) : 0;

                        String nc = document.select("div[class=bm bw0]").select("div[class=tdats]").select("div[class=tscr]").select("table[class=tdat]").select("th[class=alt h]").select("span[class=notice]").text();
                        Matcher mm = Pattern.compile(".*?([0-9]+).*?").matcher(nc);
                        userGroupBean.nextCredit = mm.matches() ? Integer.parseInt(mm.group(1)) : 0;

                        String ss = document.select("div[class=bm bw0]").select("div[class=tdats]").select("table[class=tdat tfx]").select("tbody").select("tr").select("th[class=c0]").select("h4").text();
                        Matcher matcher = Pattern.compile(".*?([0-9]+).*?").matcher(ss);
                        if (matcher.matches() && !ss.contains("禁言")) {
                            userGroupBean.currentLevelNum = Integer.parseInt(matcher.group(1));
                            userGroupBean.currentLevelStr = "Lv." + userGroupBean.currentLevelNum;
                            if (userGroupBean.currentLevelNum == 12) {//下个等级是Lv.??
                                userGroupBean.nextLevelStr = "Lv.??";
                                userGroupBean.nextLevelNum = 0;
                            } else {
                                userGroupBean.nextLevelNum = userGroupBean.currentLevelNum + 1;
                                userGroupBean.nextLevelStr = "Lv." + userGroupBean.nextLevelNum;
                            }
                        } else if (ss.contains("Lv.??")){
                            userGroupBean.currentLevelStr = "Lv.??";
                            userGroupBean.topLevel = true;
                        } else {//特殊（不是Lv.1~Lv.??）
                            if (ss.contains("我的主用户组 - ")) {
                                userGroupBean.currentLevelStr = ss.replace("我的主用户组 - ", "");
                            }
                            userGroupBean.specialUser = true;
                        }

                        if (string.contains("我的主用户组 - 成电校友")) {
                            userGroupBean.isAlumna = true;
                            String ccc = document.select("div[class=bm bw0]").select("div[class=tdats]").select("table[class=tdat tfxf]").select("th[class=alt]").select("span[class=notice]").text();
                            Matcher mmm = Pattern.compile(".*?([0-9]+).*?").matcher(ccc);
                            userGroupBean.currentCredit = mmm.matches() ? Integer.parseInt(mmm.group(1)) : 0;
                            for (UserLevel userLevel: UserLevel.values()){
                                if (userGroupBean.currentCredit >= userLevel.getMinScore() &&
                                        userGroupBean.currentCredit <= userLevel.getMaxScore()) {
                                    userGroupBean.nextCredit = userLevel.getMaxScore() + 1 - userGroupBean.currentCredit;
                                    userGroupBean.currentLevelStr = "成电校友";
                                    userGroupBean.currentLevelNum = userLevel.ordinal() + 1;
                                    userGroupBean.nextLevelNum = userGroupBean.currentLevelNum + 1;
                                    userGroupBean.specialUser = false;
                                }
                            }
                        }

                        userGroupBean.totalLevelStr = ss.replace("我的主用户组 - ", "");

                        view.onGetUserGroupSuccess(userGroupBean);
                    } else {
                        view.onGetUserGroupError("未授权");
                    }
                } catch (Exception e) {
                    view.onGetUserGroupError(e.getMessage());
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(ExceptionHelper.ResponseThrowable e) {
                view.onGetUserGroupError(e.message);
            }

            @Override
            public void OnCompleted() { }

            @Override
            public void OnDisposable(Disposable d) {
                disposable.add(d);
            }
        });
    }

    public void logout(Context context) {
        final AlertDialog dialog = new MaterialAlertDialogBuilder(context)
                .setTitle("退出登录")
                .setMessage("确认要退出登录吗？")
                .setPositiveButton("确认", null)
                .setNegativeButton("取消", null)
                .create();
        dialog.setOnShowListener(dialogInterface -> {
            Button p = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            p.setOnClickListener(v -> {
                SharePrefUtil.setLogin(context, false, new AccountBean());
                EventBus.getDefault().post(new BaseEvent<>(BaseEvent.EventCode.LOGOUT_SUCCESS));
                view.onLoginOutSuccess();
                dialog.dismiss();
            });
        });
        dialog.show();
    }

}
