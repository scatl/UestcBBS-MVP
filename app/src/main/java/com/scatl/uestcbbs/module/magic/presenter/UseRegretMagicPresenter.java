package com.scatl.uestcbbs.module.magic.presenter;

import android.util.Log;

import com.scatl.uestcbbs.api.ApiConstant;
import com.scatl.uestcbbs.base.BasePresenter;
import com.scatl.uestcbbs.entity.UseMagicBean;
import com.scatl.uestcbbs.entity.UseRegretMagicBean;
import com.scatl.uestcbbs.helper.ExceptionHelper;
import com.scatl.uestcbbs.helper.rxhelper.Observer;
import com.scatl.uestcbbs.module.magic.model.MagicModel;
import com.scatl.uestcbbs.module.magic.view.UseRegretMagicView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.reactivex.disposables.Disposable;

public class UseRegretMagicPresenter extends BasePresenter<UseRegretMagicView> {
    MagicModel magicModel = new MagicModel();

    public void getUseRegretMagicDetail(String id) {
        magicModel.getUseRegretMagicDetail(id, new Observer<String>() {
            @Override
            public void OnSuccess(String s) {
                if (s.contains("您尚未登录")) {
                    view.onGetMagicDetailError("请获取Cookies后进行此操作");
                } else if (s.contains("messagetext")){

                    try {
                        Document document = Jsoup.parse(s);

                        String info = document.select("div[id=messagetext]").select("p").get(0).text();
                        view.onGetMagicDetailError(info);

                    } catch (Exception e) {
                        view.onGetMagicDetailError("获取道具详情失败：\n" + e.getMessage());
                    }
                } else if (s.contains("购买道具")) {
                    view.onGetMagicDetailError("请先到道具商城购买悔悟卡");
                } else if (s.contains("使用道具")) {
                    try {
                        Document document = Jsoup.parse(s);

                        String formHash = document.select("form[id=scbar_form]").select("input[name=formhash]").attr("value");

                        UseRegretMagicBean useRegretMagicBean = new UseRegretMagicBean();
                        useRegretMagicBean.icon = ApiConstant.BBS_BASE_URL + document.select("dl[class=xld cl]").select("dd[class=m]").select("img").attr("src");
                        useRegretMagicBean.name = document.select("dl[class=xld cl]").select("dt[class=z]").get(0).ownText();
                        useRegretMagicBean.dsp = "使用本道具可以删除所选的帖子，操作不可撤销，请慎重使用";
                        useRegretMagicBean.otherInfo = document.select("dl[class=xld cl]").select("dt[class=z]").select("div[class=pns xw0 cl]").select("p[class=xi1]").text();

                        view.onGetMagicDetailSuccess(useRegretMagicBean, formHash);

                    } catch (Exception e) {
                        view.onGetMagicDetailError("获取道具详情失败：\n" + e.getMessage());
                    }
                }
            }

            @Override
            public void onError(ExceptionHelper.ResponseThrowable e) {
                view.onGetMagicDetailError("获取道具详情失败：\n" + e.getMessage());
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


    public void confirmUseRegretMagic(String formhash, int pid, int tid) {
        magicModel.confirmUseRegretMagic(formhash, pid, tid, new Observer<String>() {
            @Override
            public void OnSuccess(String s) {

                try {

                    if (s.contains("已删除")) {
                        view.onUseMagicSuccess("您操作的帖子已删除");
                    } else if (s.contains("小时内")){
                        view.onUseMagicError("抱歉，24 小时内您只能使用 1 次本道具");
                    } else {
                        view.onUseMagicError(s);
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
