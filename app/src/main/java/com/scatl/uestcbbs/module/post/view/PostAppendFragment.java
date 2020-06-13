package com.scatl.uestcbbs.module.post.view;

import android.graphics.Color;
import android.os.Bundle;

import androidx.appcompat.widget.AppCompatEditText;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.scatl.uestcbbs.R;
import com.scatl.uestcbbs.base.BaseDialogFragment;
import com.scatl.uestcbbs.base.BasePresenter;
import com.scatl.uestcbbs.module.post.presenter.PostAppendPresenter;
import com.scatl.uestcbbs.util.CommonUtil;
import com.scatl.uestcbbs.util.Constant;

import java.nio.charset.StandardCharsets;


public class PostAppendFragment extends BaseDialogFragment implements TextWatcher, PostAppendView{

    private AppCompatEditText content;
    private TextView contentLength, hint, title, dsp;
    private Button submit;
    private View layout;
    private LottieAnimationView loading;

    private int tid, pid;
    private String formHash, type;

    public static final String APPEND = "append";
    public static final String DIANPING = "dianping";

    private PostAppendPresenter postAppendPresenter;

    public static PostAppendFragment getInstance(Bundle bundle) {
        PostAppendFragment postAppendFragment = new PostAppendFragment();
        postAppendFragment.setArguments(bundle);
        return postAppendFragment;
    }

    @Override
    protected void getBundle(Bundle bundle) {
        super.getBundle(bundle);
        if (bundle != null) {
            tid = bundle.getInt(Constant.IntentKey.TOPIC_ID, Integer.MAX_VALUE);
            pid = bundle.getInt(Constant.IntentKey.POST_ID, Integer.MAX_VALUE);
            type = bundle.getString(Constant.IntentKey.TYPE);
        }
    }

    @Override
    protected int setLayoutResourceId() {
        return R.layout.fragment_post_append;
    }

    @Override
    protected void findView() {
        content = view.findViewById(R.id.post_append_fragment_content);
        contentLength = view.findViewById(R.id.post_append_fragment_content_length);
        submit = view.findViewById(R.id.post_append_fragment_submit);
        layout = view.findViewById(R.id.post_append_fragment_layout);
        hint = view.findViewById(R.id.post_append_fragment_hint);
        loading = view.findViewById(R.id.post_append_fragment_loading);
        title = view.findViewById(R.id.post_append_fragment_title);
        dsp = view.findViewById(R.id.post_append_fragment_content_dsp);
    }

    @Override
    protected void initView() {
        postAppendPresenter = (PostAppendPresenter) presenter;

        submit.setOnClickListener(this);
        content.addTextChangedListener(this);

        layout.setVisibility(View.GONE);
        loading.setVisibility(View.VISIBLE);
        hint.setText("");

        if (type.equals(APPEND)){
            title.setText("补充");
            content.setHint("请输入补充内容");
            dsp.setText("注：客户端显示补充内容会有几分钟的延迟，在此期间请从网页端查看补充内容");
            postAppendPresenter.getAppendFormHash(tid, pid);
        } else if (type.equals(DIANPING)) {
            title.setText("点评");
            content.setHint("请输入点评内容");
            postAppendPresenter.getCommentFormHash(tid, pid);
        }
    }

    @Override
    protected BasePresenter initPresenter() {
        return new PostAppendPresenter();
    }

    @Override
    protected void onClickListener(View view) {
        if (view.getId() == R.id.post_append_fragment_submit) {
            if (content.getText().toString().getBytes(StandardCharsets.UTF_8).length > 200) {
                showToast("字符数超出");
            } if (content.getText().toString().isEmpty()) {
                showToast("请输入内容");
            } else {
                submit.setText("请稍候...");
                submit.setEnabled(false);
                if (type.equals(APPEND)){
                    postAppendPresenter.postAppendSubmit(tid, pid, formHash, content.getText().toString());
                } else if (type.equals(DIANPING)) {
                    postAppendPresenter.sendDianPing(tid, pid, formHash, content.getText().toString());
                }

            }
        }
    }

    @Override
    public void onGetFormHashSuccess(String formHash) {
        this.formHash = formHash;
        layout.setVisibility(View.VISIBLE);
        loading.setVisibility(View.GONE);
        hint.setText("");
        CommonUtil.showSoftKeyboard(mActivity, content, 0);
    }

    @Override
    public void onGetFormHashError(String msg) {
        layout.setVisibility(View.GONE);
        loading.setVisibility(View.GONE);
        hint.setText(msg);
    }

    @Override
    public void onPostAppendSuccess(String msg) {
        CommonUtil.hideSoftKeyboard(mActivity, content);
        showToast(msg);
        dismiss();
    }

    @Override
    public void onPostAppendError(String msg) {
        showToast(msg);
        submit.setText("确认发表");
        submit.setEnabled(true);
    }

    @Override
    public void onSubmitDianPingSuccess(String msg) {
        CommonUtil.hideSoftKeyboard(mActivity, content);
        showToast(msg);
        dismiss();
    }

    @Override
    public void onSubmitDianPingError(String msg) {
        showToast(msg);
        submit.setText("确认发表");
        submit.setEnabled(true);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) { }

    @Override
    public void afterTextChanged(Editable s) {
        if (s.toString().getBytes(StandardCharsets.UTF_8).length <= 200) {
            contentLength.setText(new StringBuilder().append("还可输入").append(200 - s.toString().getBytes(StandardCharsets.UTF_8).length).append("个字符"));
            contentLength.setTextColor(mActivity.getColor(R.color.text_color));
        } else {
            contentLength.setText(new StringBuilder().append("超出了").append(s.toString().getBytes(StandardCharsets.UTF_8).length - 200).append("个字符"));
            contentLength.setTextColor(Color.RED);
        }

    }
}
