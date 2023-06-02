package com.scatl.uestcbbs.module.magic.presenter;

import android.util.Log;

import com.scatl.uestcbbs.api.ApiConstant;
import com.scatl.uestcbbs.base.BasePresenter;
import com.scatl.uestcbbs.entity.MagicShopBean;
import com.scatl.uestcbbs.entity.MineMagicBean;
import com.scatl.uestcbbs.helper.ExceptionHelper;
import com.scatl.uestcbbs.helper.rxhelper.Observer;
import com.scatl.uestcbbs.module.magic.model.MagicModel;
import com.scatl.uestcbbs.module.magic.view.MagicShopView;
import com.scatl.uestcbbs.util.CommonUtil;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;

import io.reactivex.disposables.Disposable;

public class MagicShopPresenter extends BasePresenter<MagicShopView> {
    MagicModel magicModel = new MagicModel();

    public void getMagicShop() {
        magicModel.getMagicShop(new Observer<String>() {
            @Override
            public void OnSuccess(String s) {
                if (s.contains("您尚未登录")) {
                    view.onGetMagicShopError("请获取Cookies后进行此操作");
                } else {

                    try {
                        Document document = Jsoup.parse(s);

                        Elements elements = document.select("ul[class=mtm mgcl cl]").select("li");
                        MagicShopBean magicShopBean = new MagicShopBean();
                        magicShopBean.itemLists = new ArrayList<>();
                        for (int i = 0; i < elements.size(); i ++) {
                            MagicShopBean.ItemList item = new MagicShopBean.ItemList();
                            item.dsp = elements.get(i).select("div[class=tip_c]").text();
                            item.icon = ApiConstant.BBS_BASE_URL + elements.get(i).select("img").attr("src");
                            item.name = elements.get(i).select("p").get(0).text();
                            item.price = elements.get(i).select("p").get(1).text();
                            item.id = elements.get(i).select("div[class=mg_img]").attr("id").replace("magic_", "");
                            magicShopBean.itemLists.add(item);
                        }

                        view.onGetMagicShopSuccess(magicShopBean);

                    } catch (Exception e) {
                        view.onGetMagicShopError("获取道具失败：\n" + e.getMessage());
                    }
                }
            }

            @Override
            public void onError(ExceptionHelper.ResponseThrowable e) {
                view.onGetMagicShopError("获取道具失败：" + e.message);
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
