package com.scatl.uestcbbs.module.magic.presenter;

import android.util.Log;

import com.scatl.uestcbbs.api.ApiConstant;
import com.scatl.uestcbbs.base.BasePresenter;
import com.scatl.uestcbbs.entity.MagicDetailBean;
import com.scatl.uestcbbs.helper.ExceptionHelper;
import com.scatl.uestcbbs.helper.rxhelper.Observer;
import com.scatl.uestcbbs.module.magic.model.MagicModel;
import com.scatl.uestcbbs.module.magic.view.MagicDetailView;
import com.scatl.uestcbbs.util.CommonUtil;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import io.reactivex.disposables.Disposable;

public class MagicDetailPresenter extends BasePresenter<MagicDetailView> {
    private MagicModel magicModel = new MagicModel();

    public void getMagicDetail(String id) {
        magicModel.getMagicDetail(id, new Observer<String>() {
            @Override
            public void OnSuccess(String s) {

                if (s.contains("您尚未登录")) {
                    view.onGetMagicDetailError("请获取Cookies后进行此操作");
                } else {
                    try {
                        Document document = Jsoup.parse(s);
                        String formHash = document.select("form[id=scbar_form]").select("input[name=formhash]").attr("value");

                        MagicDetailBean magicDetailBean = new MagicDetailBean();

                        magicDetailBean.icon = ApiConstant.BBS_BASE_URL + document.select("dl[class=xld cl]").select("dd[class=m]").select("img").attr("src");
                        magicDetailBean.name = document.select("dl[class=xld cl]").select("dt[class=z]").select("div[class=mbm pbm bbda]").select("p").get(0).text();
                        magicDetailBean.dsp = document.select("dl[class=xld cl]").select("dt[class=z]").select("div[class=mbm pbm bbda]").select("p[class=mtn xw0 xg1]").text();
                        magicDetailBean.originalPrice = document.select("dl[class=xld cl]").select("dt[class=z]").select("div[class=mbm pbm bbda]").select("span[id=magicprice]").text();
                        magicDetailBean.discountPrice = document.select("dl[class=xld cl]").select("dt[class=z]").select("div[class=mbm pbm bbda]").select("span[id=discountprice]").text();
                        magicDetailBean.mineWaterDrop = document.select("dl[class=xld cl]").select("dt[class=z]").select("div[class=mbm pbm bbda]").select("p[class=xw0 xg1]").get(0).text();
                        magicDetailBean.weight = document.select("dl[class=xld cl]").select("dt[class=z]").select("div[class=mbm pbm bbda]").select("span[id=magicweight]").text();
                        magicDetailBean.availableWeight = document.select("dl[class=xld cl]").select("dt[class=z]").select("div[class=mbm pbm bbda]").select("p[class=xw0 xg1]").get(1).text();
                        magicDetailBean.stock = document.select("dl[class=xld cl]").select("dt[class=z]").select("div[class=xw0]").select("p[class=mtn xw0]").select("span[class=xi1 xw1 xs2]").text();
                        magicDetailBean.otherInfo = document.select("dl[class=xld cl]").select("dt[class=z]").select("div[class=xw0]").select("p[class=xi1 mtn]").text();

                        view.onGetMagicDetailSuccess(magicDetailBean, formHash);

                    } catch (Exception e) {
                        e.printStackTrace();
                        view.onGetMagicDetailError("获取道具详情失败：\n" + e.getMessage());
                    }
                }
            }

            @Override
            public void onError(ExceptionHelper.ResponseThrowable e) {
                e.printStackTrace();
                view.onGetMagicDetailError("获取道具详情失败：\n" + e.message);
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

    public void buyMagic(String formhash, String mid, int count) {
        magicModel.buyMagic(formhash, mid, count, new Observer<String>() {
            @Override
            public void OnSuccess(String s) {
                try {
                    Document document = Jsoup.parse(s);
                    String msg = document.select("div[id=messagetext]").select("p").get(0).text();

                    if (s.contains("alert_error")) {
                        view.onBuyMagicError(msg);
                    } else if (s.contains("alert_right")) {
                        view.onBuyMagicSuccess(msg);
                    }

                } catch (Exception e) {
                    view.onBuyMagicError("购买道具失败：" + e.getMessage());
                }
            }

            @Override
            public void onError(ExceptionHelper.ResponseThrowable e) {
                view.onBuyMagicError("购买道具失败：" + e.message);
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
