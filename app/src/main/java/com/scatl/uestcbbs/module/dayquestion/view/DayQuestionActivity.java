package com.scatl.uestcbbs.module.dayquestion.view;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.scatl.uestcbbs.R;
import com.scatl.uestcbbs.annotation.ToastType;
import com.scatl.uestcbbs.base.BaseActivity;
import com.scatl.uestcbbs.widget.MyLinearLayoutManger;
import com.scatl.uestcbbs.entity.DayQuestionBean;
import com.scatl.uestcbbs.module.dayquestion.adapter.DayQuestionAdapter;
import com.scatl.uestcbbs.module.dayquestion.presenter.DayQuestionPresenter;
import com.scatl.uestcbbs.module.post.view.NewPostDetailActivity;
import com.scatl.uestcbbs.util.Constant;
import com.scatl.uestcbbs.util.SharePrefUtil;

public class DayQuestionActivity extends BaseActivity<DayQuestionPresenter> implements DayQuestionView{

    private static final String TAG = "DayQuestionActivity";

    private Toolbar toolbar;

    private View questionLayout;
    private TextView questionDsp, questionCheckPoint, questionTitle, autoAnswerHint;
    private Button submitQuestionBtn;
    private RecyclerView questionRv;
    private DayQuestionAdapter dayQuestionAdapter;

    private View confirmLayout;
    private TextView confirmDsp;
    private Button confirmNextBtn, finishBtn, getMoreBtn;

    private View allCorrectLayout;
    private TextView allCorrectDsp;
    private Button allCorrectBtn;

    private View makeChoiceLayout;
    private Button oneKeyAnswerButton, manualAnswerBtn;

    private TextView hint;

    private ProgressDialog manualAnswerProgressDialog;
    private AlertDialog oneKeyAnswerProgressDialog;

    private String formHash;
    private TextView oneKeyTextView;
    private ScrollView scrollView;
    private StringBuilder oneKeyStr;

    private boolean enableOneKeyAnswer = false;

    private int currentQuestionIndex = 1;


    @Override
    protected int setLayoutResourceId() {
        return R.layout.activity_day_question;
    }

    @Override
    protected void findView() {
        toolbar = findViewById(R.id.toolbar);

        questionLayout = findViewById(R.id.day_question_question_layout);
        questionDsp = findViewById(R.id.day_question_question_dsp);
        questionCheckPoint = findViewById(R.id.day_question_question_checkpoint);
        questionTitle = findViewById(R.id.day_question_question_title);
        questionRv = findViewById(R.id.day_question_question_rv);
        submitQuestionBtn = findViewById(R.id.day_question_submit_question_btn);

        confirmLayout = findViewById(R.id.day_question_confirm_layout);
        confirmDsp = findViewById(R.id.day_question_confirm_dsp);
        confirmNextBtn = findViewById(R.id.day_question_confirm_next_btn);
        finishBtn = findViewById(R.id.day_question_confirm_finish);

        allCorrectLayout = findViewById(R.id.day_question_all_correct_layout);
        allCorrectDsp = findViewById(R.id.day_question_all_correct_dsp);
        allCorrectBtn = findViewById(R.id.day_question_all_correct_btn);

        makeChoiceLayout = findViewById(R.id.day_question_choice_layout);
        oneKeyAnswerButton = findViewById(R.id.day_question_one_key_answer_btn);
        manualAnswerBtn = findViewById(R.id.day_question_manual_answer_btn);

        hint = findViewById(R.id.day_question_hint);
        autoAnswerHint = findViewById(R.id.day_question_auto_hint);
        getMoreBtn = findViewById(R.id.day_question_get_more_btn);
    }

    @Override
    protected void initView() {
        super.initView();

        confirmNextBtn.setOnClickListener(this);
        finishBtn.setOnClickListener(this);
        submitQuestionBtn.setOnClickListener(this::onClickListener);
        allCorrectBtn.setOnClickListener(this::onClickListener);
        autoAnswerHint.setOnClickListener(this::onClickListener);
        oneKeyAnswerButton.setOnClickListener(this::onClickListener);
        manualAnswerBtn.setOnClickListener(this::onClickListener);
        getMoreBtn.setOnClickListener(this::onClickListener);

        dayQuestionAdapter = new DayQuestionAdapter(R.layout.item_day_question);
        questionRv.setLayoutManager(new MyLinearLayoutManger(this));
        questionRv.setAdapter(dayQuestionAdapter);
        dayQuestionAdapter.setCheckedPosition(-1);

        questionLayout.setVisibility(View.GONE);
        confirmLayout.setVisibility(View.GONE);
        allCorrectLayout.setVisibility(View.GONE);
        makeChoiceLayout.setVisibility(View.VISIBLE);
        hint.setText("");

        View oneKeyView = LayoutInflater.from(this).inflate(R.layout.dialog_one_key_answer, new LinearLayout(this));
        oneKeyTextView = oneKeyView.findViewById(R.id.dialog_one_key_answer_text);
        scrollView = oneKeyView.findViewById(R.id.dialog_one_key_answer_scroll_view);
        oneKeyAnswerProgressDialog = new MaterialAlertDialogBuilder(this)
                .setView(oneKeyView)
                .setCancelable(false)
                .create();
        oneKeyStr = new StringBuilder();

        manualAnswerProgressDialog = new ProgressDialog(this);
        manualAnswerProgressDialog.setCancelable(false);
    }

    @Override
    protected DayQuestionPresenter initPresenter() {
        return new DayQuestionPresenter();
    }

    @Override
    protected void onClickListener(View view) {
        if (view.getId() == R.id.day_question_confirm_next_btn) {
            if (!enableOneKeyAnswer) {
                manualAnswerProgressDialog.show();
                manualAnswerProgressDialog.setMessage("正在为您准备题目，请稍候...");
            } else {
                oneKeyAnswerProgressDialog.show();
            }
            presenter.confirmNextQuestion(this.formHash);
        }
        if (view.getId() == R.id.day_question_confirm_finish) {
            if (!enableOneKeyAnswer) {
                manualAnswerProgressDialog.show();
                manualAnswerProgressDialog.setMessage("正在领取奖励，请稍候...");
            } else {
                oneKeyAnswerProgressDialog.show();
                oneKeyStr.append("\n>>正在领取奖励");
                oneKeyTextView.setText(oneKeyStr.toString());
                scrollView.fullScroll(View.FOCUS_DOWN);
            }
            presenter.confirmFinishQuestion(this.formHash);
        }
        if (view.getId() == R.id.day_question_submit_question_btn) {
            if (dayQuestionAdapter.getCheckedPosition() == -1) {
                showToast("请选择一个答案", ToastType.TYPE_WARNING);
            } else {
                if (!enableOneKeyAnswer) {
                    manualAnswerProgressDialog.show();
                    manualAnswerProgressDialog.setMessage("正在提交答案，请稍候...");
                } else {
                    oneKeyAnswerProgressDialog.show();
                }
                presenter.submitQuestion(this.formHash, dayQuestionAdapter.getData().get(dayQuestionAdapter.getCheckedPosition()).answerValue, questionTitle.getText().toString(), dayQuestionAdapter.getData().get(dayQuestionAdapter.getCheckedPosition()).dsp);
            }
        }
        if (view.getId() == R.id.day_question_all_correct_btn) {
            if (!enableOneKeyAnswer) {
                manualAnswerProgressDialog.show();
                manualAnswerProgressDialog.setMessage("正在领取奖励，请稍候...");
            } else {
                oneKeyAnswerProgressDialog.show();
            }
            presenter.confirmFinishQuestion(this.formHash);
        }
        //一键答题
        if (view.getId() == R.id.day_question_one_key_answer_btn) {
            enableOneKeyAnswer = true;
            oneKeyStr.append("\n" + ">>正在获取第").append(currentQuestionIndex).append("题题目");
            oneKeyTextView.setText(oneKeyStr.toString());
            scrollView.fullScroll(View.FOCUS_DOWN);
            oneKeyAnswerProgressDialog.show();
            presenter.getDayQuestion();
        }
        //手动答题
        if (view.getId() == R.id.day_question_manual_answer_btn) {
            enableOneKeyAnswer = false;
            manualAnswerProgressDialog.setMessage("正在为您准备题目，请稍候...");
            manualAnswerProgressDialog.show();
            presenter.getDayQuestion();
        }
        if (view.getId() == R.id.day_question_get_more_btn) {
            Intent intent = new Intent(this, NewPostDetailActivity.class);
            intent.putExtra(Constant.IntentKey.TOPIC_ID, 1879902);
            startActivity(intent);
        }
    }

    @Override
    protected void setOnItemClickListener() {
        dayQuestionAdapter.setOnItemChildClickListener((adapter, view, position) -> {
            if (view.getId() == R.id.item_day_question_radio_btn) {
                dayQuestionAdapter.setCheckedPosition(position);
            }
        });
    }

    @Override
    public void onGetDayQuestionSuccess(DayQuestionBean dayQuestionBean) {
        this.formHash = dayQuestionBean.formHash;
        if (!enableOneKeyAnswer) {
            manualAnswerProgressDialog.hide();
        }
        hint.setText("");
        questionLayout.setVisibility(View.VISIBLE);
        confirmLayout.setVisibility(View.GONE);
        allCorrectLayout.setVisibility(View.GONE);
        makeChoiceLayout.setVisibility(View.GONE);

        questionDsp.setText(dayQuestionBean.description);
        questionCheckPoint.setText(dayQuestionBean.checkPoint);
        questionTitle.setText(dayQuestionBean.questionTitle);
        dayQuestionAdapter.setNewData(dayQuestionBean.options);
        dayQuestionAdapter.setCheckedPosition(-1);

        //手动答题并且自动获取答案
        if (!enableOneKeyAnswer)
            presenter.getQuestionAnswer(dayQuestionBean.questionTitle);//自动获取题目答案

        //一键答题
        if (enableOneKeyAnswer) {
            presenter.getQuestionAnswer(dayQuestionBean.questionTitle);//自动获取题目答案
            oneKeyAnswerProgressDialog.show();
        }

    }

    @Override
    public void onGetDayQuestionError(String msg, boolean netError) {
        if (!enableOneKeyAnswer) {
            manualAnswerProgressDialog.hide();
        } else {
            oneKeyAnswerProgressDialog.hide();
        }
        onError(msg);
    }

    //已经完成了今日的答题
    @Override
    public void onDayQuestionFinished(String msg) {
        if (!enableOneKeyAnswer) {
            manualAnswerProgressDialog.hide();
        } else {
            oneKeyAnswerProgressDialog.hide();
        }
        onError(msg);
    }

    @Override
    public void onConfirmNextSuccess() {
        if (!enableOneKeyAnswer) {
            manualAnswerProgressDialog.hide();
        }
        if (enableOneKeyAnswer) {
            oneKeyAnswerProgressDialog.show();
        }
        presenter.getDayQuestion();
    }

    @Override
    public void onConfirmNextError(String msg) {
        if (!enableOneKeyAnswer) {
            manualAnswerProgressDialog.hide();
        } else {
            oneKeyAnswerProgressDialog.hide();
        }
        onError(msg);
    }

    @Override
    public void onAnswerCorrect(String question, String answer) {
        if (!enableOneKeyAnswer) manualAnswerProgressDialog.hide();
        presenter.submitQuestionAnswer(question, answer);
        if (enableOneKeyAnswer) {
            oneKeyAnswerProgressDialog.show();
            oneKeyStr.append("\n" + ">>答题正确");
            oneKeyTextView.setText(oneKeyStr.toString());
            scrollView.fullScroll(View.FOCUS_DOWN);
            currentQuestionIndex = currentQuestionIndex + 1;
        }
        presenter.getDayQuestion();
    }

    @Override
    public void onAnswerIncorrect(String msg) {
        if (!enableOneKeyAnswer) {
            manualAnswerProgressDialog.hide();
        } else {
            oneKeyAnswerProgressDialog.hide();
        }
        onError(msg);
    }

    @Override
    public void onAnswerError(String msg) {
        if (!enableOneKeyAnswer) {
            manualAnswerProgressDialog.hide();
        } else {
            oneKeyAnswerProgressDialog.hide();
        }
        onError(msg);
    }

    @Override
    public void onGetConfirmDspSuccess(String dsp, String formHash) {
        this.formHash = formHash;
        if (!enableOneKeyAnswer) manualAnswerProgressDialog.hide();
        hint.setText("");
        questionLayout.setVisibility(View.GONE);
        confirmLayout.setVisibility(View.VISIBLE);
        allCorrectLayout.setVisibility(View.GONE);
        makeChoiceLayout.setVisibility(View.GONE);
        confirmDsp.setText(dsp);
        if (enableOneKeyAnswer) {
            oneKeyAnswerProgressDialog.show();
            oneKeyStr.append("\n" + ">>正在获取第").append(currentQuestionIndex).append("题题目");
            oneKeyTextView.setText(oneKeyStr.toString());
            scrollView.fullScroll(View.FOCUS_DOWN);

            new Handler().postDelayed(() -> confirmNextBtn.performClick(), 300);
        }
    }

    @Override
    public void onGetConfirmDspError(String msg) {
        if (!enableOneKeyAnswer) {
            manualAnswerProgressDialog.hide();
        } else {
            oneKeyAnswerProgressDialog.hide();
        }
        onError(msg);
    }

    @Override
    public void onConfirmFinishSuccess(String msg) {
        if (!enableOneKeyAnswer) {
            manualAnswerProgressDialog.hide();
        } else {
            oneKeyAnswerProgressDialog.hide();
        }
        onError(msg);
    }

    @Override
    public void onConfirmFinishError(String msg) {
        if (!enableOneKeyAnswer) {
            manualAnswerProgressDialog.hide();
        } else {
            oneKeyAnswerProgressDialog.hide();
        }
        onError(msg);
    }


    //答题全部正确
    @Override
    public void onFinishedAllCorrect(String dsp, String formHash) {
        this.formHash = formHash;
        if (!enableOneKeyAnswer) manualAnswerProgressDialog.hide();
        allCorrectDsp.setText(dsp);
        allCorrectLayout.setVisibility(View.VISIBLE);
        questionLayout.setVisibility(View.GONE);
        confirmLayout.setVisibility(View.GONE);
        makeChoiceLayout.setVisibility(View.GONE);
        if (enableOneKeyAnswer) {
            oneKeyAnswerProgressDialog.show();
            oneKeyStr.append("\n" + ">>恭喜，全部回答正确，正在领取奖励");
            oneKeyTextView.setText(oneKeyStr.toString());
            scrollView.fullScroll(View.FOCUS_DOWN);

            new Handler().postDelayed(() -> allCorrectBtn.performClick(), 300);
        }
    }

    @Override
    public void onGetQuestionAnswerSuccess(String answer) {
        boolean getAnswerSuccess = false;
        for (int i = 0; i < dayQuestionAdapter.getData().size(); i ++) {
            if (answer.equals(dayQuestionAdapter.getData().get(i).dsp)) {
                dayQuestionAdapter.setCheckedPosition(i);
                getAnswerSuccess = true;
                break;
            }
        }
        if (getAnswerSuccess && enableOneKeyAnswer) {
            oneKeyAnswerProgressDialog.show();
            new Handler().postDelayed(() -> {
                submitQuestionBtn.performClick();//获取答案后自动点击提交答案按钮
            }, 300);
        } else {
            enableOneKeyAnswer = false;//获取答案失败，变为手动答题
        }
    }

    @Override
    public void onGetQuestionAnswerError(String msg) {
        showToast(msg, ToastType.TYPE_ERROR);
        enableOneKeyAnswer = false;//获取答案失败，变为手动答题
        oneKeyAnswerProgressDialog.hide();
    }

    private void onError(String msg) {
        hint.setText(msg);
        questionLayout.setVisibility(View.GONE);
        confirmLayout.setVisibility(View.GONE);
        allCorrectLayout.setVisibility(View.GONE);
        makeChoiceLayout.setVisibility(View.GONE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (oneKeyAnswerProgressDialog != null) {
            oneKeyAnswerProgressDialog.dismiss();
        }
        if (manualAnswerProgressDialog != null) {
            manualAnswerProgressDialog.dismiss();
        }
    }
}
