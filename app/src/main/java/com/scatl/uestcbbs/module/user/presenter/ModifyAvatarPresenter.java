package com.scatl.uestcbbs.module.user.presenter;

import android.util.Log;

import com.scatl.uestcbbs.base.BasePresenter;
import com.scatl.uestcbbs.helper.ExceptionHelper;
import com.scatl.uestcbbs.helper.rxhelper.Observer;
import com.scatl.uestcbbs.module.user.model.UserModel;
import com.scatl.uestcbbs.module.user.view.ModifyAvatarView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.reactivex.disposables.Disposable;

public class ModifyAvatarPresenter extends BasePresenter<ModifyAvatarView> {
    UserModel userModel = new UserModel();

    public void getParams() {
        userModel.getModifyAvatarParams(new Observer<String>() {
            @Override
            public void OnSuccess(String s) {

                if (s.contains("需要先登录")) {
                    view.onGetParaError("请先获取Cookies后进行本操作");
                } else {
                    try {
                        Document document = Jsoup.parse(s);
                        Elements elements = document.select("table[class=tfm]").get(1).select("tbody").select("tr").select("td").select("script");
                        for (int i = 0; i < elements.size(); i ++) {
                            if (elements.get(i).html().contains("agent")) {
                                Matcher matcher = Pattern.compile("(.*?)&input=(.*?)&agent=(.*?)&ucapi=(.*?)").matcher(elements.get(i).html());
                                if (matcher.find()) {
                                    Log.e("agent", matcher.group(3));
                                    Log.e("input", matcher.group(2));
                                    view.onGetParaSuccess(matcher.group(3), matcher.group(2));
                                } else {
                                    view.onGetParaError("出现了错误，请联系开发者进行反馈");
                                }
                            }

                        }

                    } catch (Exception e) {
                        view.onGetParaError("获取参数失败：" + e.getMessage());
                    }
                }
            }

            @Override
            public void onError(ExceptionHelper.ResponseThrowable e) {
                view.onGetParaError("获取参数失败：" + e.message);
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

    public void modifyAvatar(String agent, String input, String avatar1, String avatar2, String avatar3) {
        userModel.modifyAvatar(agent, input, avatar1, avatar2, avatar3, new Observer<String>() {
            @Override
            public void OnSuccess(String s) {
                if (s.contains("success")) {
                    view.onUploadSuccess("更改头像成功！");
                } else {
                    view.onUploadError("更改头像失败：" + s);
                }
            }

            @Override
            public void onError(ExceptionHelper.ResponseThrowable e) {
                view.onUploadError("更改头像失败：" + e.message);
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
