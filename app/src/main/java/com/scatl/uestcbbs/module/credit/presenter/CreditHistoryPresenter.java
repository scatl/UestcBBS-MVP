package com.scatl.uestcbbs.module.credit.presenter;

import com.scatl.uestcbbs.base.BasePresenter;
import com.scatl.uestcbbs.entity.MineCreditBean;
import com.scatl.uestcbbs.helper.ExceptionHelper;
import com.scatl.uestcbbs.helper.rxhelper.Observer;
import com.scatl.uestcbbs.module.credit.model.CreditModel;
import com.scatl.uestcbbs.module.credit.view.CreditHistoryView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.disposables.Disposable;

/**
 * author: sca_tl
 * date: 2020/12/12 19:03
 * description:
 */
public class CreditHistoryPresenter extends BasePresenter<CreditHistoryView> {
    CreditModel creditModel = new CreditModel();

    public void getCreditHistory(int page) {
        creditModel.getCreditHistory(page, new Observer<String>() {
            @Override
            public void OnSuccess(String s) {
                if (s.contains("先登录才能")) {
                    view.onGetMineCreditHistoryError("请先高级授权后再进行本操作");
                } else {
                    try {
                        Document document = Jsoup.parse(s);
                        Elements elements = document.select("table[summary=主题付费]").select("tbody").select("tr");

                        List<MineCreditBean.CreditHistoryBean> creditHistoryBeans = new ArrayList<>();
                        for (int i = 1; i < elements.size(); i ++) {
                            MineCreditBean.CreditHistoryBean historyBean = new MineCreditBean.CreditHistoryBean();

                            historyBean.action = elements.get(i).select("td").get(0).text();
                            historyBean.change = elements.get(i).select("td").get(1).text();
                            historyBean.detail = elements.get(i).select("td").get(2).text();
                            historyBean.time = elements.get(i).select("td").get(3).text();
                            historyBean.increase = historyBean.change.contains("+");

                            creditHistoryBeans.add(historyBean);
                        }
                        view.onGetMineCreditHistorySuccess(creditHistoryBeans, s.contains("下一页"));
                    } catch (Exception e) {
                        view.onGetMineCreditHistoryError("获取历史记录失败");
                        e.printStackTrace();
                    }
                }

            }

            @Override
            public void onError(ExceptionHelper.ResponseThrowable e) {
                view.onGetMineCreditHistoryError("获取历史记录失败");
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
