package com.scatl.uestcbbs.module.post.presenter;

import android.content.Context;

import com.scatl.uestcbbs.base.BasePresenter;
import com.scatl.uestcbbs.entity.RateInfoBean;
import com.scatl.uestcbbs.helper.ExceptionHelper;
import com.scatl.uestcbbs.helper.rxhelper.Observer;
import com.scatl.uestcbbs.module.post.model.PostModel;
import com.scatl.uestcbbs.module.post.view.PostRateView;
import com.scatl.uestcbbs.util.SharePrefUtil;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.reactivex.disposables.Disposable;

/**
 * author: sca_tl
 * date: 2020/6/21 19:54
 * description:
 */
public class PostRatePresenter extends BasePresenter<PostRateView> {

    PostModel postModel = new PostModel();

    public void getRateInfo(int tid, int pid, Context context) {
        postModel.getRateInfo(tid, pid,
                new Observer<String>() {
                    @Override
                    public void OnSuccess(String html) {

                        RateInfoBean rateInfoBean = new RateInfoBean();

                        try {

                            Document doc = Jsoup.parse(html);

                            Elements s = doc.select("script");
                            Pattern pattern = Pattern.compile("alert\\(\"([\\s\\S]*)\"\\)");
                            for (Element element: s) {
                                Matcher matcher = pattern.matcher(element.data());
                                if (matcher.find()) {
                                    view.onGetRateInfoError( matcher.group(1) == null ? "未知错误" : matcher.group(1));
                                    break;
                                } else {

                                    Element element1 = doc.select("#rateform").first();
                                    rateInfoBean.rateUrl = element1.attr("action");

                                    Elements trs = doc.select("tr");
                                    Elements tds = trs.get(1).select("td");

                                    String range = tds.get(2).text().replace(" ", "");
                                    String total = tds.get(3).text().trim();

                                    rateInfoBean.success = true;
                                    rateInfoBean.errorReason = "";
                                    rateInfoBean.todayTotal = Integer.parseInt(total);

                                    int index = range.indexOf("~");
                                    rateInfoBean.minScore = Integer.parseInt(range.substring(0, index));
                                    rateInfoBean.maxScore = Integer.parseInt(range.substring(index + 1));

                                    view.onGetRateInfoSuccess(rateInfoBean);
                                }
                            }

                        } catch (Exception ex) {
                            view.onGetRateInfoError("发生了错误:" + ex.getMessage());
                        }
                    }

                    @Override
                    public void onError(ExceptionHelper.ResponseThrowable e) {
                        view.onGetRateInfoError(e.message);
                    }

                    @Override
                    public void OnCompleted() { }

                    @Override
                    public void OnDisposable(Disposable d) {
                        disposable.add(d);
                    }
                });
    }

    public void rate(int tid, int pid, int score, String reason, String sendreasonpm, Context context) {
        postModel.rate(tid, pid, score, reason, sendreasonpm,
                new Observer<String>() {
                    @Override
                    public void OnSuccess(String html) {

                        try {
                            Document doc = Jsoup.parse(html);

                            if (doc.text().equals("redirect to mobile view")) {

                                view.onRateSuccess("评分成功");

                            } else {

                                Pattern pattern = Pattern.compile("alert\\(\"([\\s\\S]*)\"\\)");
                                Pattern pattern1 = Pattern.compile("errorMsg = \'([\\s\\S]*)\'");

                                Elements trs = doc.select("script");

                                for (Element element: trs) {

                                    Matcher matcher = pattern.matcher(element.data());
                                    Matcher matcher1 = pattern1.matcher(element.data());

                                    if (matcher.find()) {
                                        view.onRateError(matcher.group(1));
                                        break;
                                    }

                                    if (matcher1.find()) {
                                        view.onRateError(matcher1.group(1));
                                        break;
                                    }
                                }

                            }


                        } catch (Exception e) {
                            view.onRateError("评分失败：" + e.getMessage());
                        }


                    }

                    @Override
                    public void onError(ExceptionHelper.ResponseThrowable e) {
                        view.onRateError(e.message);
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
