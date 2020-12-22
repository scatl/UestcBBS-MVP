package com.scatl.uestcbbs.module.dayquestion.view;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.scatl.uestcbbs.R;
import com.scatl.uestcbbs.base.BaseActivity;
import com.scatl.uestcbbs.base.BasePresenter;
import com.scatl.uestcbbs.custom.MyLinearLayoutManger;
import com.scatl.uestcbbs.entity.DayQuestionBean;
import com.scatl.uestcbbs.module.dayquestion.adapter.DayQuestionAdapter;
import com.scatl.uestcbbs.module.dayquestion.presenter.DayQuestionPresenter;

public class DayQuestionActivity extends BaseActivity implements DayQuestionView{

    private Toolbar toolbar;

    private View questionLayout;
    private TextView questionDsp, questionCheckPoint, questionTitle;
    private Button submitQuestionBtn;
    private RecyclerView questionRv;
    private DayQuestionAdapter dayQuestionAdapter;

    private View confirmLayout;
    private TextView confirmDsp;
    private Button confirmNextBtn, finishBtn;

    private View allCorrectLayout;
    private TextView allCorrectDsp;
    private Button allCorrectBtn;

    private TextView hint;

    private ProgressDialog progressDialog;

    private DayQuestionPresenter dayQuestionPresenter;

    private String formHash;


    @Override
    protected int setLayoutResourceId() {
        return R.layout.activity_day_question;
    }

    @Override
    protected void findView() {
        toolbar = findViewById(R.id.day_question_toolbar);

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

        hint = findViewById(R.id.day_question_hint);
    }

    @Override
    protected void initView() {
        dayQuestionPresenter = (DayQuestionPresenter) presenter;

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        confirmNextBtn.setOnClickListener(this);
        finishBtn.setOnClickListener(this);
        submitQuestionBtn.setOnClickListener(this::onClickListener);
        allCorrectBtn.setOnClickListener(this::onClickListener);

        dayQuestionAdapter = new DayQuestionAdapter(R.layout.item_day_question);
        questionRv.setLayoutManager(new MyLinearLayoutManger(this));
        questionRv.setAdapter(dayQuestionAdapter);
        dayQuestionAdapter.setCheckedPosition(-1);

        questionLayout.setVisibility(View.GONE);
        confirmLayout.setVisibility(View.GONE);
        allCorrectLayout.setVisibility(View.GONE);
        hint.setText("");

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("正在为您准备题目，请稍候...");
        progressDialog.show();

        dayQuestionPresenter.getDayQuestion();
    }

    @Override
    protected BasePresenter initPresenter() {
        return new DayQuestionPresenter();
    }

    @Override
    protected void onClickListener(View view) {
        if (view.getId() == R.id.day_question_confirm_next_btn) {
            progressDialog.show();
            progressDialog.setMessage("正在为您准备题目，请稍候...");
            dayQuestionPresenter.confirmNextQuestion(this.formHash);
        }
        if (view.getId() == R.id.day_question_confirm_finish) {
            progressDialog.show();
            progressDialog.setMessage("正在领取奖励，请稍候...");
            dayQuestionPresenter.confirmFinishQuestion(this.formHash);
        }
        if (view.getId() == R.id.day_question_submit_question_btn) {
            if (dayQuestionAdapter.getCheckedPosition() == -1) {
                showToast("请选择一个答案");
            } else {
                progressDialog.show();
                progressDialog.setMessage("正在提交答案，请稍候...");
                dayQuestionPresenter.submitQuestion(this.formHash, dayQuestionAdapter.getData().get(dayQuestionAdapter.getCheckedPosition()).answerValue, questionTitle.getText().toString(), dayQuestionAdapter.getData().get(dayQuestionAdapter.getCheckedPosition()).dsp);
            }
        }
        if (view.getId() == R.id.day_question_all_correct_btn) {
            progressDialog.show();
            progressDialog.setMessage("正在领取奖励，请稍候...");
            dayQuestionPresenter.confirmFinishQuestion(this.formHash);
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
        progressDialog.dismiss();
        hint.setText("");
        questionLayout.setVisibility(View.VISIBLE);
        confirmLayout.setVisibility(View.GONE);
        allCorrectLayout.setVisibility(View.GONE);

        questionDsp.setText(dayQuestionBean.description);
        questionCheckPoint.setText(dayQuestionBean.checkPoint);
        questionTitle.setText(dayQuestionBean.questionTitle);
        dayQuestionAdapter.setNewData(dayQuestionBean.options);
        dayQuestionAdapter.setCheckedPosition(-1);

        dayQuestionPresenter.getQuestionAnswer(dayQuestionBean.questionTitle);

    }

    @Override
    public void onGetDayQuestionError(String msg) {
        progressDialog.dismiss();
        confirmLayout.setVisibility(View.GONE);
        questionLayout.setVisibility(View.GONE);
        allCorrectLayout.setVisibility(View.GONE);
        hint.setText(msg);
    }

    @Override
    public void onDayQuestionFinished(String msg) {
        progressDialog.dismiss();
        questionLayout.setVisibility(View.GONE);
        confirmLayout.setVisibility(View.GONE);
        allCorrectLayout.setVisibility(View.GONE);
        hint.setText(msg);
    }

    @Override
    public void onConfirmNextSuccess() {
        progressDialog.dismiss();
        dayQuestionPresenter.getDayQuestion();
    }

    @Override
    public void onConfirmNextError(String msg) {
        progressDialog.dismiss();
        showToast(msg);
    }

    @Override
    public void onAnswerCorrect(String question, String answer) {
        progressDialog.dismiss();
        dayQuestionPresenter.getDayQuestion();
        dayQuestionPresenter.submitQuestionAnswer(question, answer);
    }

    @Override
    public void onAnswerIncorrect(String msg) {
        progressDialog.dismiss();
        hint.setText(msg);
        confirmLayout.setVisibility(View.GONE);
        questionLayout.setVisibility(View.GONE);
        allCorrectLayout.setVisibility(View.GONE);
    }

    @Override
    public void onAnswerError(String msg) {
        progressDialog.dismiss();
        showToast(msg);
    }

    @Override
    public void onGetConfirmDspSuccess(String dsp, String formHash) {
        this.formHash = formHash;
        progressDialog.dismiss();
        hint.setText("");
        questionLayout.setVisibility(View.GONE);
        confirmLayout.setVisibility(View.VISIBLE);
        allCorrectLayout.setVisibility(View.GONE);
        confirmDsp.setText(dsp);
    }

    @Override
    public void onGetConfirmDspError(String msg) {
        progressDialog.dismiss();
        showToast(msg);
    }

    @Override
    public void onConfirmFinishSuccess(String msg) {
        progressDialog.dismiss();
        hint.setText(msg);
        questionLayout.setVisibility(View.GONE);
        confirmLayout.setVisibility(View.GONE);
        allCorrectLayout.setVisibility(View.GONE);
    }

    @Override
    public void onConfirmFinishError(String msg) {
        progressDialog.dismiss();
        showToast(msg);
    }

    @Override
    public void onFinishedAllCorrect(String dsp, String formHash) {
        this.formHash = formHash;
        progressDialog.dismiss();
        allCorrectDsp.setText(dsp);
        allCorrectLayout.setVisibility(View.VISIBLE);
        questionLayout.setVisibility(View.GONE);
        confirmLayout.setVisibility(View.GONE);
    }

    @Override
    public void onGetQuestionAnswerSuccess(String answer) {
        for (int i = 0; i < dayQuestionAdapter.getData().size(); i ++) {
            if (answer.equals(dayQuestionAdapter.getData().get(i).dsp)) {
                dayQuestionAdapter.setCheckedPosition(i);
                break;
            }
        }
    }

    @Override
    public void onGetQuestionAnswerError(String msg) {
        showSnackBar(getWindow().getDecorView(), msg);
    }
}
