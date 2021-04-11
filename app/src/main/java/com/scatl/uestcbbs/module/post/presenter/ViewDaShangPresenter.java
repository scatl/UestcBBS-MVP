package com.scatl.uestcbbs.module.post.presenter;

import android.util.Log;

import com.scatl.uestcbbs.base.BasePresenter;
import com.scatl.uestcbbs.entity.RateUserBean;
import com.scatl.uestcbbs.helper.ExceptionHelper;
import com.scatl.uestcbbs.helper.rxhelper.Observer;
import com.scatl.uestcbbs.module.post.model.PostModel;
import com.scatl.uestcbbs.module.post.view.ViewDaShangView;
import com.scatl.uestcbbs.util.CommonUtil;
import com.scatl.uestcbbs.util.ForumUtil;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.disposables.Disposable;

public class ViewDaShangPresenter extends BasePresenter<ViewDaShangView> {
    private PostModel postModel = new PostModel();

    public void getRateUser(int tid, int pid) {
        postModel.getRateUser(tid, pid, new Observer<String>() {
            @Override
            public void OnSuccess(String s) {
                try {
                    Document document = Jsoup.parse(s);

                    Elements elements = document.select("div[class=c floatwrap]").select("table[class=list]").select("tbody").select("tr");

                    List<RateUserBean> rateUserBeans = new ArrayList<>();
                    for (int i = 0; i <elements.size(); i ++) {
                        RateUserBean rateUserBean = new RateUserBean();
                        rateUserBean.credit = elements.get(i).select("td").get(0).text();
                        rateUserBean.userName = elements.get(i).select("td").get(1).select("a").text();
                        rateUserBean.uid = ForumUtil.getFromLinkInfo(elements.get(i).select("td").get(1).select("a").attr("href")).id;
                        rateUserBean.time = elements.get(i).select("td").get(2).text();
                        rateUserBean.reason = elements.get(i).select("td").get(3).text();

                        rateUserBeans.add(rateUserBean);
                        Log.e("kkkkkk", CommonUtil.toString(rateUserBean));
                    }

                    view.onGetRateUserSuccess(rateUserBeans);

                } catch (Exception e) {
                    view.onGetRateUserError("获取点赞用户失败" + e.getMessage());
                }
            }

            @Override
            public void onError(ExceptionHelper.ResponseThrowable e) {
                view.onGetRateUserError("获取点赞用户失败" + e.message);
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
