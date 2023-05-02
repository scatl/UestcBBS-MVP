package com.scatl.uestcbbs.module.home.presenter;

import com.scatl.uestcbbs.base.BasePresenter;
import com.scatl.uestcbbs.entity.OnLineUserBean;
import com.scatl.uestcbbs.helper.ExceptionHelper;
import com.scatl.uestcbbs.helper.rxhelper.Observer;
import com.scatl.uestcbbs.module.home.model.HomeModel;
import com.scatl.uestcbbs.module.home.view.OnLineUserView;
import com.scatl.uestcbbs.util.BBSLinkUtil;
import com.scatl.uestcbbs.util.ForumUtil;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;

import io.reactivex.disposables.Disposable;

/**
 * author: sca_tl
 * date: 2020/10/7 11:27
 * description:
 */
public class OnLineUserPresenter extends BasePresenter<OnLineUserView> {
    HomeModel homeModel = new HomeModel();

    public void getHomeInfo() {
        homeModel.getHomeInfo(new Observer<String>() {
            @Override
            public void OnSuccess(String s) {
                try {
                    Document document = Jsoup.parse(s);

                    OnLineUserBean onLineUserBean = new OnLineUserBean();
                    Elements elements1 = document.select("span[class=xs1]").select("strong");
                    onLineUserBean.totalUserNum = Integer.parseInt(elements1.get(0).text());
                    onLineUserBean.totalRegisteredUserNum = Integer.parseInt(elements1.get(1).text());
                    onLineUserBean.totalVisitorNum = Integer.parseInt(elements1.get(3).text());

                    Elements elements = document.select("dd[class=ptm pbm]").select("ul[class=cl]").select("li");

                    onLineUserBean.userBeans = new ArrayList<>();
                    for (Element e : elements) {
                        OnLineUserBean.UserBean u = new OnLineUserBean.UserBean();
                        u.time = e.attr("title").replace("时间: ", "");
                        u.userName = e.select("a").text();
                        u.uid = BBSLinkUtil.getLinkInfo(e.select("a").attr("href")).getId();
                        u.userAvatar = "https://bbs.uestc.edu.cn/uc_server/avatar.php?uid=" + u.uid + "&size=middle";
                        onLineUserBean.userBeans.add(u);
                    }

                    view.onGetOnLineUserSuccess(onLineUserBean);

                } catch (Exception e) {

                }
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
