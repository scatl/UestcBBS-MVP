package com.scatl.uestcbbs.module.medal.presenter;

import android.util.Log;

import com.scatl.uestcbbs.api.ApiConstant;
import com.scatl.uestcbbs.base.BasePresenter;
import com.scatl.uestcbbs.entity.MedalBean;
import com.scatl.uestcbbs.helper.ExceptionHelper;
import com.scatl.uestcbbs.helper.rxhelper.Observer;
import com.scatl.uestcbbs.module.medal.model.MedalModel;
import com.scatl.uestcbbs.module.medal.view.MedalCenterView;
import com.scatl.uestcbbs.util.CommonUtil;
import com.scatl.uestcbbs.util.FileUtil;
import com.scatl.uestcbbs.util.JsonUtil;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.Collections;

import io.reactivex.disposables.Disposable;

/**
 * author: sca_tl
 * date: 2020/10/7 18:59
 * description:
 */
public class MedalCenterPresenter extends BasePresenter<MedalCenterView> {
    private MedalModel medalModel = new MedalModel();

    public void getMedalCenter() {
        medalModel.getMedalCenter(new Observer<String>() {
            @Override
            public void OnSuccess(String s) {
                try {

                    Document document = Jsoup.parse(s);
                    Elements elements = document.select("ul[class=mtm mgcl cl]").select("li");
                    String formHash = document.select("form[id=scbar_form]").select("input[name=formhash]").attr("value");

                    MedalBean medalBean = new MedalBean();
                    medalBean.medalCenterBeans = new ArrayList<>();
                    for (Element e : elements) {
                        MedalBean.MedalCenterBean medalCenterBean = new MedalBean.MedalCenterBean();
                        medalCenterBean.medalDsp = e.select("div[class=tip_c]").text();
                        medalCenterBean.medalIcon = ApiConstant.BBS_BASE_URL + e.select("img").attr("src");
                        medalCenterBean.medalName = e.select("p[class=xw1]").text();
                        medalCenterBean.medalId = Integer.parseInt(e.select("div[class=mg_img]").attr("id").replace("medal_", ""));
                        medalCenterBean.buyDsp = e.select("a[class=xi2]").text();
                        medalBean.medalCenterBeans.add(medalCenterBean);
                    }
                    Collections.reverse(medalBean.medalCenterBeans);
                    view.onGetMedalCenterDataSuccess(medalBean);

                } catch (Exception e) {
                    view.onGetMedalCenterDataError("获取勋章信息失败：" + e.getMessage());
                }
            }

            @Override
            public void onError(ExceptionHelper.ResponseThrowable e) {
                view.onGetMedalCenterDataError("获取勋章信息失败：" + e.getMessage());
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

    public void getMineMedal() {
        medalModel.getMineMedal(new Observer<String>() {
            @Override
            public void OnSuccess(String s) {

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
}
