package com.scatl.uestcbbs.module.dayquestion.model;

import com.scatl.uestcbbs.entity.DayQuestionAnswerBean;
import com.scatl.uestcbbs.helper.rxhelper.Observer;
import com.scatl.uestcbbs.util.RetrofitCookieUtil;
import com.scatl.uestcbbs.util.RetrofitUtil;

import java.util.HashMap;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * author: sca_tl
 * date: 2020/5/21 13:25
 * description:
 */
public class DayQuestionModel {
    public void getDayQuestion(Observer<String> observer) {
        Observable<String> observable = RetrofitCookieUtil
                .getInstance()
                .getApiService()
                .getDayQuestion();
        observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

    public void confirmNextQuestion(String formHash, Observer<String> observer) {
        Map<String, String> map = new HashMap<>();
        map.put("formhash", formHash);
        map.put("next", "true");

        Observable<String> observable = RetrofitCookieUtil
                .getInstance()
                .getApiService()
                .confirmNextQuestion(RetrofitCookieUtil.generateRequestBody(map));
        observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

    public void submitQuestion(String formHash, String answer, Observer<String> observer) {
        Map<String, String> map = new HashMap<>();
        map.put("formhash", formHash);
        map.put("answer", answer);
        map.put("submit", "true");

        Observable<String> observable = RetrofitCookieUtil
                .getInstance()
                .getApiService()
                .submitQuestion(RetrofitCookieUtil.generateRequestBody(map));
        observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

    public void confirmFinishQuestion(String formHash, Observer<String> observer) {
        Map<String, String> map = new HashMap<>();
        map.put("formhash", formHash);
        map.put("finish", "true");

        Observable<String> observable = RetrofitCookieUtil
                .getInstance()
                .getApiService()
                .confirmFinishQuestion(RetrofitCookieUtil.generateRequestBody(map));
        observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

    public void getQuestionAnswer(String question, Observer<DayQuestionAnswerBean> observer) {
        Observable<DayQuestionAnswerBean> observable = RetrofitUtil
                .getInstance()
                .getApiService()
                .getDayQuestionAnswer(question);
        observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

    public void submitQuestionAnswer(String question, String answer, Observer<String> observer) {
        Observable<String> observable = RetrofitUtil
                .getInstance()
                .getApiService()
                .submitDayQuestionAnswer(question, answer);
        observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }
}
