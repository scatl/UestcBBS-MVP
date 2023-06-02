package com.scatl.uestcbbs.module.magic.presenter;

import android.util.Log;

import com.scatl.uestcbbs.api.ApiConstant;
import com.scatl.uestcbbs.base.BasePresenter;
import com.scatl.uestcbbs.entity.MagicShopBean;
import com.scatl.uestcbbs.entity.UseMagicBean;
import com.scatl.uestcbbs.helper.ExceptionHelper;
import com.scatl.uestcbbs.helper.rxhelper.Observer;
import com.scatl.uestcbbs.module.magic.model.MagicModel;
import com.scatl.uestcbbs.module.magic.view.UseMagicView;
import com.scatl.uestcbbs.util.CommonUtil;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.reactivex.disposables.Disposable;

public class UseMagicPresenter extends BasePresenter<UseMagicView> {

    private MagicModel magicModel = new MagicModel();

    public void getUseMagicDetail(String magicId) {
        magicModel.getUseMagicDetail(magicId, new Observer<String>() {
            @Override
            public void OnSuccess(String s) {
                if (s.contains("您尚未登录")) {
                    view.onGetUseMagicDetailError("请获取Cookies后进行此操作");
                } else {

                    try {
                        Document document = Jsoup.parse(s);

                        String formHash = document.select("form[id=scbar_form]").select("input[name=formhash]").attr("value");

                        UseMagicBean useMagicBean = new UseMagicBean();
                        useMagicBean.icon = ApiConstant.BBS_BASE_URL + document.select("dl[class=xld cl]").select("dd[class=m]").select("img").attr("src");
                        useMagicBean.name = document.select("dl[class=xld cl]").select("dt[class=z]").get(0).ownText();
                        useMagicBean.dsp = document.select("dl[class=xld cl]").select("dt[class=z]").select("div[class=pns xw0 cl]").select("p").get(0).text();
                        useMagicBean.otherInfo = document.select("dl[class=xld cl]").select("dt[class=z]").select("div[class=pns xw0 cl]").select("p[class=xi1]").text();

                        view.onGetUseMagicDetailSuccess(useMagicBean, formHash);

                    } catch (Exception e) {
                        e.printStackTrace();
                        view.onGetUseMagicDetailError("获取道具详情失败：\n" + e.getMessage());
                    }
                }
            }

            @Override
            public void onError(ExceptionHelper.ResponseThrowable e) {
                view.onGetUseMagicDetailError("获取道具详情失败：" + e.message);
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

    public void confirmUseMagic(String formhash, String magicId) {
        magicModel.confirmUseMagic(formhash, magicId, new Observer<String>() {
            @Override
            public void OnSuccess(String s) {
                try {

                    Matcher matcher = Pattern.compile("(.*?)CDATA\\[(.*?)<script(.*?)").matcher(s);
                    if (matcher.find()) {
                        if (s.contains("恭喜")) {
                            view.onUseMagicSuccess(matcher.group(2));
                        } else {
                            view.onUseMagicError(matcher.group(2));
                        }
                    } else {
                        view.onUseMagicError("使用道具失败，请重试");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    view.onUseMagicError("使用道具失败：" + e.getMessage());
                }
            }

            @Override
            public void onError(ExceptionHelper.ResponseThrowable e) {
                e.printStackTrace();
                view.onUseMagicError("使用道具失败：" + e.message);
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
