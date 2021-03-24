package com.scatl.uestcbbs.module.user.presenter;

import android.content.Context;
import android.util.Log;

import com.scatl.uestcbbs.R;
import com.scatl.uestcbbs.api.ApiConstant;
import com.scatl.uestcbbs.base.BasePresenter;
import com.scatl.uestcbbs.entity.VisitorsBean;
import com.scatl.uestcbbs.helper.ExceptionHelper;
import com.scatl.uestcbbs.helper.rxhelper.Observer;
import com.scatl.uestcbbs.module.user.model.UserModel;
import com.scatl.uestcbbs.module.user.view.UserMainPageView;
import com.scatl.uestcbbs.util.ForumUtil;

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
 * date: 2021/3/17 13:02
 * description:
 */
public class UserMainPagePresenter extends BasePresenter<UserMainPageView> {
    private UserModel userModel = new UserModel();

    public void getUserSpace(int uid, Context context) {
        userModel.getUserSpace(uid, "profile", new Observer<String>() {
            @Override
            public void OnSuccess(String s) {
                try {

                    Document document = Jsoup.parse(s);
                    Elements elements = document.select("div[class=bm_c u_profile]").select("div[class=pbm mbm bbda cl]");

                    boolean isOnline = elements.get(0).select("h2[class=mbn]").html().contains("在线");

                    String onLineTime = "";
                    String registerTime = "";
                    String lastLoginTime = "";
                    String ipLocation = "";

                    for (int i = 0; i < elements.size(); i ++) {
                        if (elements.get(i).html().contains("活跃概况")) {
                            onLineTime = elements.get(i).select("ul[class=pf_l]").select("li").get(0).ownText();
                            registerTime = elements.get(i).select("ul[class=pf_l]").select("li").get(1).ownText();
                            lastLoginTime = elements.get(i).select("ul[class=pf_l]").select("li").get(2).ownText();

                            String ipAddress = elements.get(i).select("ul[class=pf_l]").select("li").get(4).ownText();
                            Matcher matcher = Pattern.compile("(.*?)( - - )(.*?)").matcher(ipAddress);
                            if (matcher.matches()) {
                                ipLocation = matcher.group(3);
                            }

                            break;
                        }
                    }

                    view.onGetUserSpaceSuccess(isOnline, onLineTime, registerTime, lastLoginTime, ipLocation);
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
}
