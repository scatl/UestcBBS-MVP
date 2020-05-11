package com.scatl.uestcbbs.module.post.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.scatl.uestcbbs.R;
import com.scatl.uestcbbs.base.BaseActivity;
import com.scatl.uestcbbs.base.BaseEvent;
import com.scatl.uestcbbs.base.BasePresenter;
import com.scatl.uestcbbs.custom.MyLinearLayoutManger;
import com.scatl.uestcbbs.module.post.adapter.AddPostPollAdapter;
import com.scatl.uestcbbs.util.Constant;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

public class AddPollActivity extends BaseActivity implements TextWatcher {

    private Toolbar toolbar;
    private RecyclerView pollRv;
    private LinearLayout addPollItemBtn;
    private TextView confirmBtn, deleteBtn;
    private AddPostPollAdapter addPostPollAdapter;
    private EditText expiration, choices;
    private Switch visible, showVoters;

    private View addItem;

    private List<String> pollOptions = new ArrayList<String>(){{add(""); add("");}};
    private int pollExp = 1, pollChoice = 1;
    private boolean pollVisible, pollShowVoters;

    @Override
    protected void getIntent(Intent intent) {
        super.getIntent(intent);
        if (intent != null) {
            pollOptions = intent.getStringArrayListExtra(Constant.IntentKey.POLL_OPTIONS) == null ? pollOptions : intent.getStringArrayListExtra(Constant.IntentKey.POLL_OPTIONS);
            pollExp = intent.getIntExtra(Constant.IntentKey.POLL_EXPIRATION, 1);
            pollChoice = intent.getIntExtra(Constant.IntentKey.POLL_CHOICES, 1);
            pollVisible = intent.getBooleanExtra(Constant.IntentKey.POLL_VISIBLE, true);
            pollShowVoters = intent.getBooleanExtra(Constant.IntentKey.POLL_SHOW_VOTERS, true);
        }
    }


    @Override
    protected int setLayoutResourceId() {
        return R.layout.activity_add_poll;
    }

    @Override
    protected void findView() {
        pollRv = findViewById(R.id.add_poll_rv);
        toolbar = findViewById(R.id.add_poll_toolbar);
        confirmBtn = findViewById(R.id.add_poll_confirm_btn);
        deleteBtn = findViewById(R.id.add_poll_delete_poll_btn);
        expiration = findViewById(R.id.add_poll_expiration);
        choices = findViewById(R.id.add_poll_choices);
        visible = findViewById(R.id.add_poll_visible);
        showVoters = findViewById(R.id.add_poll_show_voters);

        addItem = LayoutInflater.from(this).inflate(R.layout.view_add_poll_item, new LinearLayout(this));
        addPollItemBtn = addItem.findViewById(R.id.add_poll_add_item_btn);

    }

    @Override
    protected void initView() {

        addPollItemBtn.setOnClickListener(this::onClickListener);
        confirmBtn.setOnClickListener(this::onClickListener);
        deleteBtn.setOnClickListener(this::onClickListener);

//        setSupportActionBar(toolbar);
//        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        addPostPollAdapter = new AddPostPollAdapter(R.layout.item_add_poll);
        addPostPollAdapter.addFooterView(addItem);
        pollRv.setLayoutManager(new MyLinearLayoutManger(this));
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
                if (!TextUtils.isEmpty(choices.getText().toString()) && Integer.parseInt(choices.getText().toString()) == addPostPollAdapter.getData().size()){
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
            save();
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) { }

    @Override
    public void afterTextChanged(Editable s) {
        if (!TextUtils.isEmpty(s.toString()) && Integer.parseInt(s.toString()) > addPostPollAdapter.getData().size()) {
            showToast("1 + 1 = ？");
            choices.setText(String.valueOf(addPostPollAdapter.getData().size()));
        } else if ("0".equals(s.toString())) {
            showToast("你还让不让人家投票啦？");
            choices.setText("1");
        }
    }

    private void save() {
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
        } else if ("0".equals(expiration.getText().toString())) {
            showToast("刚生产就过期啦？");
        } else if (Integer.parseInt(expiration.getText().toString()) > 3) {
            showToast("记票天数请设置在3天以内");
        }else {
            BaseEvent.AddPoll addPoll = new BaseEvent.AddPoll();
            addPoll.pollOptions = addPostPollAdapter.getData();
            addPoll.pollChoice = Integer.parseInt(choices.getText().toString());
            addPoll.pollExp = Integer.parseInt(expiration.getText().toString());
            addPoll.pollVisible = visible.isChecked();
            addPoll.showVoters = showVoters.isChecked();
            EventBus.getDefault().post(new BaseEvent<>(BaseEvent.EventCode.ADD_POLL, addPoll));
            finish();
        }
    }

    private void delete() {
        final AlertDialog dialog = new AlertDialog.Builder(this)
                .setNegativeButton("确认", null)
                .setPositiveButton("取消", null )
                .setTitle("删除投票")
                .setMessage("确认删除该投票吗？")
                .create();
        dialog.setOnShowListener(dialogInterface -> {
            Button n = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);

            new CountDownTimer(3300, 1000) {
                @Override
                public void onTick(long l) {
                    n.setClickable(false);
                    n.setText(String.valueOf("请稍候(" + l/1000 + ")"));
                }

                @Override
                public void onFinish() {
                    n.setClickable(true);
                    n.setText("确认");
                }
            }.start();

            n.setOnClickListener(v -> {
                EventBus.getDefault().post(new BaseEvent<>(BaseEvent.EventCode.DELETE_POLL));
                dialog.dismiss();
                finish();
            });
        });
        dialog.show();
    }

    @Override
    public void onBackPressed() {
        save();
    }

}
