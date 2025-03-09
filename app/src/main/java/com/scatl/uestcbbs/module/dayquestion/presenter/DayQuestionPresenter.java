package com.scatl.uestcbbs.module.dayquestion.presenter;

import android.text.TextUtils;

import com.scatl.uestcbbs.base.BasePresenter;
import com.scatl.uestcbbs.entity.DayQuestionBean;
import com.scatl.uestcbbs.helper.ExceptionHelper;
import com.scatl.uestcbbs.helper.rxhelper.Observer;
import com.scatl.uestcbbs.manager.DayQuestionManager;
import com.scatl.uestcbbs.module.dayquestion.model.DayQuestionModel;
import com.scatl.uestcbbs.module.dayquestion.view.DayQuestionView;
import com.scatl.util.NumberUtil;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.reactivex.disposables.Disposable;

/**
 * author: sca_tl
 * date: 2020/5/21 13:24
 * description:
 */
public class DayQuestionPresenter extends BasePresenter<DayQuestionView> {
    private DayQuestionModel dayQuestionModel = new DayQuestionModel();

    //一共有4种情况：
    //1、获取到题目
    //2、已完成答题（失败或成功）
    //3、提示是否继续答题
    //4、未登录
    public void getDayQuestion() {
        dayQuestionModel.getDayQuestion(new Observer<String>() {
            @Override
            public void OnSuccess(String s) {
                if (s.contains("答题规则")) {//获取到题目

                    try {
                        Document document = Jsoup.parse(s);

                        DayQuestionBean questionBean = new DayQuestionBean();
                        questionBean.description = document.select("div[class=bm bw0]").select("div").get(1).text();
                        questionBean.checkPoint = document.select("div[class=bm bw0]").select("form[id=myform]").select("div").select("span").select("font").eachText().get(0);
                        questionBean.questionTitle = document.select("div[class=bm bw0]").select("form[id=myform]").select("div").select("span").select("font").eachText().get(1);
                        questionBean.formHash = document.select("div[class=bm bw0]").select("form[id=myform]").select("input[name=formhash]").attr("value");

                        if (questionBean.checkPoint != null) {
                            Matcher matcher = Pattern.compile("(.*)(\\d)(.*)(\\d)(.*)").matcher(questionBean.checkPoint);
                            if (matcher.find()) {
                                questionBean.questionNum = NumberUtil.parseInt(matcher.group(2), 0);
                            }
                        }

                        questionBean.options = new ArrayList<>();
                        Elements options = document.select("div[class=bm bw0]").select("form[id=myform]").select("div[class=qs_option]");
                        for (int i = 0 ; i < options.size(); i ++) {
                            DayQuestionBean.Options o = new DayQuestionBean.Options();
                            o.answerValue = options.get(i).select("input[name=answer]").attr("value");
                            o.dsp = options.get(i).text();
                            o.answerChecked = false;
                            questionBean.options.add(o);
                        }
                        view.onGetDayQuestionSuccess(questionBean);
                    } catch (Exception e) {
                        view.onGetDayQuestionError("获取题目失败：" + e.getMessage(), false);
                    }

                } else if (s.contains("明天再来")) { //已完成答题

                    view.onDayQuestionFinished("今天已经答过题啦，明天再来吧");

                } else if (s.contains("闯关确认")) { //提示是否继续答题
                    try {
                        Document document = Jsoup.parse(s);

                        String dsp = document.select("div[class=bm bw0]").select("div").select("span").text();
                        String formHash = document.select("div[class=bm bw0]").select("form[id=myform]").select("input[name=formhash]").attr("value");
                        view.onGetConfirmDspSuccess(dsp, formHash);

                    } catch (Exception e) {
                        view.onGetConfirmDspError("加载闯关信息失败：" + e.getMessage());
                    }
                } else if (s.contains("登录后方可进入")) {

                    view.onGetDayQuestionError("该功能需要Cookies支持，请重新登录", false);

                } else if (s.contains("通关奖励")) {

                    try {
                        Document document = Jsoup.parse(s);

                        String dsp = document.select("div[class=bm bw0]").select("div").select("span").text();
                        String formHash = document.select("div[class=bm bw0]").select("form[id=myform]").select("input[name=formhash]").attr("value");
                        view.onFinishedAllCorrect(dsp, formHash);

                    } catch (Exception e) {
                        view.onGetDayQuestionError("加载通关信息失败：" + e.getMessage(), false);
                    }
                } else if (s.contains("您的积分不足以")) {
                    view.onGetDayQuestionError("您的积分不足以支付答错惩罚，无法进行答题，至少需要拥有10水滴才可以参与答题！", false);
                }
            }

            @Override
            public void onError(ExceptionHelper.ResponseThrowable e) {
                view.onGetDayQuestionError("获取题目失败：" + e.message, true);
            }

            @Override
            public void OnCompleted() { }

            @Override
            public void OnDisposable(Disposable d) {
                disposable.add(d);
            }
        });
    }

    //确认继续答题
    public void confirmNextQuestion(String formHash) {
        dayQuestionModel.confirmNextQuestion(formHash, new Observer<String>() {
            @Override
            public void OnSuccess(String s) {
                if (s.contains("正在为您准备")) {
                    view.onConfirmNextSuccess();
                } else {
                    view.onConfirmNextError("确认下一题失败");
                }
            }

            @Override
            public void onError(ExceptionHelper.ResponseThrowable e) {
                view.onConfirmNextError("确认下一题失败：" + e.message);
            }

            @Override
            public void OnCompleted() { }

            @Override
            public void OnDisposable(Disposable d) {
                disposable.add(d);
            }
        });
    }

    //提交答案
    public void submitQuestion(String formHash, String answerId, String question, String answerStr) {
        dayQuestionModel.submitQuestion(formHash, answerId, new Observer<String>() {
            @Override
            public void OnSuccess(String s) {
                if (s.contains("闯关成功")) {
                    view.onAnswerCorrect(question, answerStr);
                } else if (s.contains("闯关失败")) {
                    view.onAnswerIncorrect("答题错误，闯关失败，已扣除水滴。不要灰心，明天再来吧");
                } else {
                    view.onAnswerError("遇到了一个错误，请联系开发者");
                }
            }

            @Override
            public void onError(ExceptionHelper.ResponseThrowable e) {
                view.onAnswerError("遇到了一个错误：" + e.message);
            }

            @Override
            public void OnCompleted() { }

            @Override
            public void OnDisposable(Disposable d) {
                disposable.add(d);

            }
        });
    }

    //终止答题，领取奖励
    public void confirmFinishQuestion(String formHash) {
        dayQuestionModel.confirmFinishQuestion(formHash, new Observer<String>() {
            @Override
            public void OnSuccess(String s) {
                if (s.contains("完成了今日的")) {
                    view.onConfirmFinishSuccess("答题完成，奖励已发放，明天再来哦");
                } else {
                    view.onConfirmFinishError("领取奖励失败，请稍后再试");
                }
            }

            @Override
            public void onError(ExceptionHelper.ResponseThrowable e) {
                view.onConfirmFinishError("领取奖励失败：" + e.message);
            }

            @Override
            public void OnCompleted() { }

            @Override
            public void OnDisposable(Disposable d) {
                disposable.add(d);
            }
        });
    }

    //向数据库获取答案
    public void getQuestionAnswer(String question) {
        String answer = DayQuestionManager.getAnswer(question);
        if (TextUtils.isEmpty(answer)) {
            view.onGetQuestionAnswerError("获取答案失败，没有找到对应问题：" + question);
        } else {
            view.onGetQuestionAnswerSuccess(answer);
        }
//        dayQuestionModel.getQuestionAnswer(question, new Observer<DayQuestionAnswerBean>() {
//            @Override
//            public void OnSuccess(DayQuestionAnswerBean dayQuestionAnswerBean) {
//                if (dayQuestionAnswerBean.returnCode == 1) {
//                    view.onGetQuestionAnswerSuccess(dayQuestionAnswerBean.returnData.answer);
//                } else {
//                    view.onGetQuestionAnswerError(dayQuestionAnswerBean.returnMsg);
//                }
//            }
//
//            @Override
//            public void onError(ExceptionHelper.ResponseThrowable e) {
//                view.onGetQuestionAnswerError("获取答案失败：" + e.message);
//            }
//
//            @Override
//            public void OnCompleted() {
//
//            }
//
//            @Override
//            public void OnDisposable(Disposable d) {
//                disposable.add(d);
//            }
//        });
    }

    //向数据库记录答案
    public void submitQuestionAnswer(String question, String answer) {
//        dayQuestionModel.submitQuestionAnswer(question, answer, new Observer<String>() {
//            @Override
//            public void OnSuccess(String s) {
//
//            }
//
//            @Override
//            public void onError(ExceptionHelper.ResponseThrowable e) {
//
//            }
//
//            @Override
//            public void OnCompleted() {
//
//            }
//
//            @Override
//            public void OnDisposable(Disposable d) {
//                disposable.add(d);
//            }
//        });
    }
}
