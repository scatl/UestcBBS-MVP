package com.scatl.uestcbbs.module.post.view;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.scatl.uestcbbs.R;
import com.scatl.uestcbbs.annotation.ToastType;
import com.scatl.uestcbbs.base.BaseBottomFragment;
import com.scatl.uestcbbs.base.BaseDialogFragment;
import com.scatl.uestcbbs.base.BaseEvent;
import com.scatl.uestcbbs.base.BasePresenter;
import com.scatl.uestcbbs.entity.RateInfoBean;
import com.scatl.uestcbbs.module.post.presenter.PostRatePresenter;
import com.scatl.uestcbbs.util.Constant;

import org.greenrobot.eventbus.EventBus;


public class PostRateFragment extends BaseDialogFragment implements PostRateView{

    private TextView total, hint;
    private Spinner shuidiSpinner;
    private Spinner reasonSpinner;
    private EditText reason;
    private CheckBox notify;
    private Button submit;
    private View rateLayout;
    private LottieAnimationView loading;

    private String[] spinnerItem;

    private int tid, pid;

    private PostRatePresenter postRatePresenter;

    @Override
    protected void getBundle(Bundle bundle) {
        super.getBundle(bundle);
        if (bundle != null) {
            tid = bundle.getInt(Constant.IntentKey.TOPIC_ID, Integer.MAX_VALUE);
            pid = bundle.getInt(Constant.IntentKey.POST_ID, Integer.MAX_VALUE);
        }
    }

    public static PostRateFragment getInstance(Bundle bundle) {
        PostRateFragment postRateFragment = new PostRateFragment();
        postRateFragment.setArguments(bundle);
        return postRateFragment;
    }

    @Override
    protected int setLayoutResourceId() {
        return R.layout.fragment_post_rate;
    }

    @Override
    protected void findView() {
        total = view.findViewById(R.id.post_rate_fragment_shuidi);
        shuidiSpinner = view.findViewById(R.id.post_rate_fragment_shuidi_spinner);
        reasonSpinner = view.findViewById(R.id.post_rate_fragment_reason_spinner);
        reason = view.findViewById(R.id.post_rate_fragment_reason);
        notify = view.findViewById(R.id.post_rate_fragment_notify);
        loading = view.findViewById(R.id.post_rate_fragment_loading);
        submit = view.findViewById(R.id.post_rate_fragment_submit);
        hint = view.findViewById(R.id.post_rate_fragment_hint);
        rateLayout = view.findViewById(R.id.post_rate_fragment_rate_layout);
    }

    @Override
    protected void initView() {
        postRatePresenter = (PostRatePresenter) presenter;

        loading.setVisibility(View.VISIBLE);
        rateLayout.setVisibility(View.GONE);

        submit.setOnClickListener(this);

        postRatePresenter.getRateInfo(tid, pid, mActivity);
    }

    @Override
    protected BasePresenter initPresenter() {
        return new PostRatePresenter();
    }

    @Override
    protected void onClickListener(View v) {
        if (v.getId() == R.id.post_rate_fragment_submit) {
            submit.setText("请稍候");
            submit.setEnabled(false);
            postRatePresenter.rate(tid, pid, Integer.parseInt(spinnerItem[shuidiSpinner.getSelectedItemPosition()].replace("水滴", "")),
                    reason.getText().toString(), notify.isChecked() ? "on" : "", mActivity);
        }
    }

    @Override
    public void onGetRateInfoSuccess(RateInfoBean rateInfoBean) {
        loading.setVisibility(View.GONE);
        rateLayout.setVisibility(View.VISIBLE);

        reasonSpinner.setOnItemSelectedListener(new Spinner.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                reason.setText(reasonSpinner.getSelectedItem().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });

        total.setText(new StringBuilder().append("水滴（今日还剩").append(rateInfoBean.todayTotal).append("水滴可评分）："));

        spinnerItem = new String[rateInfoBean.maxScore - rateInfoBean.minScore + 1];
        for (int i = 0; i <= rateInfoBean.maxScore - rateInfoBean.minScore; i ++){
            spinnerItem[i] = (rateInfoBean.minScore + i) + "水滴";
        }

        ArrayAdapter<String> spinner_adapter = new ArrayAdapter<>(mActivity, android.R.layout.simple_spinner_item, spinnerItem);
        spinner_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        shuidiSpinner.setAdapter(spinner_adapter);
        shuidiSpinner.setSelection(6);
    }

    @Override
    public void onGetRateInfoError(String msg) {
        loading.setVisibility(View.GONE);
        hint.setText(msg);
    }

    @Override
    public void onRateSuccess(String msg) {
        EventBus.getDefault().post(new BaseEvent<>(BaseEvent.EventCode.RATE_SUCCESS));
        showToast(msg, ToastType.TYPE_SUCCESS);
        dismiss();
    }

    @Override
    public void onRateError(String msg) {
        submit.setText("确认评分");
        submit.setEnabled(true);
        showToast(msg, ToastType.TYPE_ERROR);
    }


}