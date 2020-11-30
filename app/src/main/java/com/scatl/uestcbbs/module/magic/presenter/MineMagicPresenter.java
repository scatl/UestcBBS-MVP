package com.scatl.uestcbbs.module.magic.presenter;

import android.util.Log;

import com.scatl.uestcbbs.api.ApiConstant;
import com.scatl.uestcbbs.base.BasePresenter;
import com.scatl.uestcbbs.entity.MagicShopBean;
import com.scatl.uestcbbs.entity.MineMagicBean;
import com.scatl.uestcbbs.helper.ExceptionHelper;
import com.scatl.uestcbbs.helper.rxhelper.Observer;
import com.scatl.uestcbbs.module.magic.model.MagicModel;
import com.scatl.uestcbbs.module.magic.view.MineMagicView;
import com.scatl.uestcbbs.util.CommonUtil;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.ArrayList;

import io.reactivex.disposables.Disposable;

public class MineMagicPresenter extends BasePresenter<MineMagicView> {
    MagicModel magicModel = new MagicModel();

    public void getMineMagic() {
        magicModel.getMineMagic(new Observer<String>() {
            @Override
            public void OnSuccess(String s) {
                try {
                    Document document = Jsoup.parse(s);

                    Elements elements = document.select("ul[class=mtm mgcl cl]").select("li");
                    MineMagicBean mineMagicBean = new MineMagicBean();
                    mineMagicBean.itemLists = new ArrayList<>();
                    for (int i = 0; i < elements.size(); i ++) {
                        MineMagicBean.ItemList item = new MineMagicBean.ItemList();
                        item.dsp = elements.get(i).select("div[class=tip_c]").text();
                        item.icon = ApiConstant.BBS_BASE_URL + elements.get(i).select("img").attr("src");
                        item.name = elements.get(i).select("p").get(0).text();
                        item.totalCount = elements.get(i).select("p").get(1).select("font[class=xi1 xw1]").text();
                        item.totalWeight = elements.get(i).select("p").get(1).select("font[class=xi1]").text();
                        item.magicId = elements.get(i).select("p[class=mtn]").select("a").get(0).attr("href").replace("https://bbs.uestc.edu.cn/home.php?mod=magic&action=mybox&operation=use&magicid=", "");
                        item.showUseBtn = elements.get(i).select("p[class=mtn]").select("a").get(0).text().contains("使用");
                        mineMagicBean.itemLists.add(item);
                    }

                    if (mineMagicBean.itemLists.size() == 0) {
                        view.onGetMineMagicError("您还没有道具");
                    } else {
                        view.onGetMineMagicSuccess(mineMagicBean);
                    }

                } catch (Exception e) {
                    view.onGetMineMagicError("获取道具失败：\n" + e.getMessage());
                }
            }

            @Override
            public void onError(ExceptionHelper.ResponseThrowable e) {
                view.onGetMineMagicError("获取我的道具失败：" + e.message);
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
