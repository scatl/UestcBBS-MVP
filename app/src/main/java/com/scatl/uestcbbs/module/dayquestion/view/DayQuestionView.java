package com.scatl.uestcbbs.module.dayquestion.view;

import com.scatl.uestcbbs.entity.DayQuestionBean;
import com.scwang.smart.refresh.layout.SmartRefreshLayout;

/**
 * author: sca_tl
 * date: 2020/5/21 13:23
 * description:
 */
public interface DayQuestionView {
    void onGetDayQuestionSuccess(DayQuestionBean dayQuestionBean);
    void onGetDayQuestionError(String msg, boolean netError);

    void onDayQuestionFinished(String msg);

    void onConfirmFinishSuccess(String msg);
    void onConfirmFinishError(String msg);

    void onGetConfirmDspSuccess(String dsp, String formHash);
    void onGetConfirmDspError(String msg);

    void onConfirmNextSuccess();
    void onConfirmNextError(String msg);

    void onAnswerCorrect(String question, String answer);
    void onAnswerIncorrect(String msg);
    void onAnswerError(String msg);

    void onFinishedAllCorrect(String msg, String formHash);

    void onGetQuestionAnswerSuccess(String answer);
    void onGetQuestionAnswerError(String msg);

}
