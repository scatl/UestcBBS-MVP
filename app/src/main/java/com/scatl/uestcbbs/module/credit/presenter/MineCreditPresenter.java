package com.scatl.uestcbbs.module.credit.presenter;

import com.scatl.uestcbbs.base.BasePresenter;
import com.scatl.uestcbbs.entity.HistoryBean;
import com.scatl.uestcbbs.entity.MineCreditBean;
import com.scatl.uestcbbs.helper.ExceptionHelper;
import com.scatl.uestcbbs.helper.rxhelper.Observer;
import com.scatl.uestcbbs.module.credit.model.CreditModel;
import com.scatl.uestcbbs.module.credit.view.MineCreditView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.reactivex.disposables.Disposable;

public class MineCreditPresenter extends BasePresenter<MineCreditView> {
    private CreditModel creditModel = new CreditModel();

    public void getMineCredit() {
        creditModel.getMineCredit(new Observer<String>() {
            @Override
            public void OnSuccess(String s) {
                try {
                    Document document = Jsoup.parse(s);
                    Elements elements = document.select("ul[class=creditl mtm bbda cl]").select("li");
                    String formHash = document.select("form[id=scbar_form]").select("input[name=formhash]").attr("value");

                    MineCreditBean mineCreditBean = new MineCreditBean();

                    Matcher m1 = Pattern.compile("(.*?)(\\d+)(.*)").matcher(elements.get(0).text());
                    if (m1.find()) { mineCreditBean.shuiDiNum = m1.group(2); }

                    Matcher m2 = Pattern.compile("(.*?)(\\d+)(.*)").matcher(elements.get(1).text());
                    if (m2.find()) { mineCreditBean.weiWangNum = m2.group(2); }

                    Matcher m3 = Pattern.compile("(.*?)(\\d+)(.*)").matcher(elements.get(2).text());
                    if (m3.find()) { mineCreditBean.jiangLiQuanNum = m3.group(2); }

                    Matcher m4 = Pattern.compile("(.*?)(\\d+)(.*)").matcher(elements.get(3).text().replace(elements.get(3).select("span[class=xg1]").text(), ""));
                    if (m4.find()) { mineCreditBean.jiFenNum = m4.group(2); }

                    Elements history_elements = document.select("table[summary=转账与兑换]").select("tbody").select("tr");
                    mineCreditBean.historyBeans = new ArrayList<>();
                    for (int i = 1; i < history_elements.size(); i ++) {
                        MineCreditBean.CreditHistoryBean historyBean = new MineCreditBean.CreditHistoryBean();

                        historyBean.action = history_elements.get(i).select("td").get(0).text();
                        historyBean.change = history_elements.get(i).select("td").get(1).text();
                        historyBean.detail = history_elements.get(i).select("td").get(2).text();
                        historyBean.time = history_elements.get(i).select("td").get(3).text();
                        historyBean.increase = historyBean.change.contains("+");

                        mineCreditBean.historyBeans.add(historyBean);
                    }

                    view.onGetMineCreditSuccess(mineCreditBean, formHash);

                } catch (Exception e) {
                    e.printStackTrace();
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
