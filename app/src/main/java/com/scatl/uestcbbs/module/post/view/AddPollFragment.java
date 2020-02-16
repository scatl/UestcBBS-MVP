package com.scatl.uestcbbs.module.post.view;


import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;

import com.scatl.uestcbbs.R;
import com.scatl.uestcbbs.base.BaseDialogFragment;
import com.scatl.uestcbbs.base.BaseEvent;
import com.scatl.uestcbbs.base.BasePresenter;
import com.scatl.uestcbbs.custom.MyLinearLayoutManger;
import com.scatl.uestcbbs.module.post.adapter.AddPostPollAdapter;
import com.scatl.uestcbbs.util.Constant;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;


public class AddPollFragment extends BaseDialogFragment implements TextWatcher{

    private RecyclerView pollRv;

    private View optionsView;
    private Button addPollItemBtn, deletePollBtn, confirmBtn;
    private AddPostPollAdapter addPostPollAdapter;
    private EditText expiration, choices;
    private Switch visible, showVoters;

    private List<String> pollOptions = new ArrayList<String>(){{add(""); add("");}};
    private int pollExp = 1, pollChoice = 1;
    private boolean pollVisible, pollShowVoters;

    public static AddPollFragment getInstance(Bundle bundle) {
        AddPollFragment addPollFragment = new AddPollFragment();
        addPollFragment.setArguments(bundle);
        return addPollFragment;
    }

    @Override
    protected void getBundle(Bundle bundle) {
        super.getBundle(bundle);
        if (bundle != null) {
            pollOptions = bundle.getStringArrayList(Constant.IntentKey.POLL_OPTIONS);
            pollExp = bundle.getInt(Constant.IntentKey.POLL_EXPIRATION, 1);
            pollChoice = bundle.getInt(Constant.IntentKey.POLL_CHOICES, 1);
            pollVisible = bundle.getBoolean(Constant.IntentKey.POLL_VISIBLE, true);
            pollShowVoters = bundle.getBoolean(Constant.IntentKey.POLL_SHOW_VOTERS, true);
        }
    }

    @Override
    protected int setLayoutResourceId() {
        return R.layout.fragment_add_poll;
    }

    @Override
    protected void findView() {
        pollRv = view.findViewById(R.id.add_poll_rv);

        optionsView = LayoutInflater.from(mActivity).inflate(R.layout.add_poll_option_view, new LinearLayout(mActivity));
        addPollItemBtn = optionsView.findViewById(R.id.add_poll_add_item_btn);
        confirmBtn = optionsView.findViewById(R.id.add_poll_confirm_btn);
        deletePollBtn = optionsView.findViewById(R.id.add_poll_delete_poll_btn);
        expiration = optionsView.findViewById(R.id.add_poll_expiration);
        choices = optionsView.findViewById(R.id.add_poll_choices);
        visible = optionsView.findViewById(R.id.add_poll_visible);
        showVoters = optionsView.findViewById(R.id.add_poll_show_voters);
    }

    @Override
    protected void initView() {

        setCancelable(false);
        addPollItemBtn.setOnClickListener(this::onClickListener);
        confirmBtn.setOnClickListener(this::onClickListener);
        deletePollBtn.setOnClickListener(this::onClickListener);

        addPostPollAdapter = new AddPostPollAdapter(R.layout.item_add_poll);
        addPostPollAdapter.addFooterView(optionsView);
        pollRv.setLayoutManager(new MyLinearLayoutManger(mActivity));
        pollRv.setNestedScrollingEnabled(false);
        pollRv.setAdapter(addPostPollAdapter);
        addPostPollAdapter.setNewData(pollOptions);

        expiration.setText(String.valueOf(pollExp));
        choices.setText(String.valueOf(pollChoice));
        choices.addTextChangedListener(this);
        visible.setChecked(pollVisible);
        showVoters.setChecked(pollShowVoters);

    }

    @Override
    protected BasePresenter initPresenter() {
        return null;
    }

    @Override
    protected void setOnItemClickListener() {
        addPostPollAdapter.setOnItemChildClickListener((adapter, view1, position) -> {
            if (view1.getId() == R.id.item_add_poll_delete){
                if (!TextUtils.isEmpty(choices.getText().toString()) && Integer.valueOf(choices.getText().toString()) == addPostPollAdapter.getData().size()){
                    choices.setText(String.valueOf(addPostPollAdapter.getData().size() - 1));
                }
                addPostPollAdapter.getData().remove(position);
                addPostPollAdapter.notifyItemRemoved(position);
                addPostPollAdapter.notifyItemRangeChanged(position, addPostPollAdapter.getData().size() - position);
            }
        });
    }

    @Override
    protected void onClickListener(View view) {
        if (view.getId() == R.id.add_poll_add_item_btn) {
            addPostPollAdapter.addData("");
            addPostPollAdapter.notifyItemInserted(addPostPollAdapter.getData().size());
            pollRv.postDelayed(() -> pollRv.scrollToPosition(addPostPollAdapter.getData().size()), 0);
        }
        if (view.getId() == R.id.add_poll_delete_poll_btn) {
            delete();
        }
        if (view.getId() == R.id.add_poll_confirm_btn) {
            boolean emptyItem = false;
            int index = 0;
            for (int i = 0; i < addPostPollAdapter.getData().size(); i ++) {
                if (TextUtils.isEmpty(addPostPollAdapter.getData().get(i)
                        .replaceAll("\n", "")
                        .replaceAll(" ", ""))) {
                    index = i;
                    emptyItem = true;
                    break;
                }
            }
            if (emptyItem) {
                showToast("第" + (index + 1) + "个选项的描述不能为空");
            } else if (TextUtils.isEmpty(choices.getText().toString())) {
                showToast("请输入可投票数");
            } else if (TextUtils.isEmpty(expiration.getText().toString())){
                showToast("请输入有效期");
            } else if (Integer.valueOf(expiration.getText().toString()) >= 3) {
                showToast("记票天数请设置在3天以内");
            }else {
                BaseEvent.AddPoll addPoll = new BaseEvent.AddPoll();
                addPoll.pollOptions = addPostPollAdapter.getData();
                addPoll.pollChoice = Integer.valueOf(choices.getText().toString());
                addPoll.pollExp = Integer.valueOf(expiration.getText().toString());
                addPoll.pollVisible = visible.isChecked();
                addPoll.showVoters = showVoters.isChecked();
                EventBus.getDefault().post(new BaseEvent<>(BaseEvent.EventCode.ADD_POLL, addPoll));

                dismiss();
            }
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) { }

    @Override
    public void afterTextChanged(Editable s) {
        if (!TextUtils.isEmpty(s.toString()) && Integer.valueOf(s.toString()) > addPostPollAdapter.getData().size()) {
            choices.setText(String.valueOf(addPostPollAdapter.getData().size()));
        }
    }

    private void delete() {
        final AlertDialog dialog = new AlertDialog.Builder(mActivity)
                .setNegativeButton("确认", null)
                .setPositiveButton("取消", null )
                .setTitle("删除投票")
                .setMessage("确认删除该投票吗？")
                .create();
        dialog.setOnShowListener(dialogInterface -> {
            Button n = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);
            n.setOnClickListener(v -> {
                EventBus.getDefault().post(new BaseEvent<>(BaseEvent.EventCode.DELETE_POLL));
                dismiss();
                dialog.dismiss();
            });
        });
        dialog.show();
    }
}
