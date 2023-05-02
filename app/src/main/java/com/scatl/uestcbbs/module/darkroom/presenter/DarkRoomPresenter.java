package com.scatl.uestcbbs.module.darkroom.presenter;

import android.util.Log;

import com.scatl.uestcbbs.base.BasePresenter;
import com.scatl.uestcbbs.entity.DarkRoomBean;
import com.scatl.uestcbbs.helper.ExceptionHelper;
import com.scatl.uestcbbs.helper.rxhelper.Observer;
import com.scatl.uestcbbs.module.darkroom.model.DarkRoomModel;
import com.scatl.uestcbbs.module.darkroom.view.DarkRoomView;
import com.scatl.uestcbbs.util.BBSLinkUtil;
import com.scatl.uestcbbs.util.CommonUtil;
import com.scatl.uestcbbs.util.ForumUtil;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.disposables.Disposable;

/**
 * author: sca_tl
 * date: 2021/4/10 12:08
 * description:
 */
public class DarkRoomPresenter extends BasePresenter<DarkRoomView> {
    private DarkRoomModel darkRoomModel = new DarkRoomModel();

    public void getDarkRoomList() {
        darkRoomModel.getDarkRoomList(new Observer<String>() {
            @Override
            public void OnSuccess(String s) {
                try {
                    Document document = Jsoup.parse(s);
                    Elements elements = document.select("table[id=darkroomtable]").select("tbody").select("tr");

                    List<DarkRoomBean> darkRoomBeanList = new ArrayList<>();
                    for (int i = 1; i <elements.size(); i ++) {
                        DarkRoomBean darkRoomBean = new DarkRoomBean();
                        darkRoomBean.username = elements.get(i).select("td").get(0).select("a").text();
                        darkRoomBean.uid = BBSLinkUtil.getLinkInfo(elements.get(i).select("td").get(0).select("a").attr("href")).getId();
                        darkRoomBean.action = elements.get(i).select("td").get(1).text();
                        darkRoomBean.dateline = elements.get(i).select("td").get(2).text();
                        darkRoomBean.actionTime = elements.get(i).select("td").get(3).text();
                        darkRoomBean.reason = elements.get(i).select("td").get(4).text();
                        darkRoomBeanList.add(darkRoomBean);
                    }
                    view.onGetDarkRoomDataSuccess(darkRoomBeanList);

                } catch (Exception e){
                    view.onGetDarkRoomDataError("获取小黑屋数据失败：" + e.getMessage());
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(ExceptionHelper.ResponseThrowable e) {
                view.onGetDarkRoomDataError("获取小黑屋数据失败：" + e.message);
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
