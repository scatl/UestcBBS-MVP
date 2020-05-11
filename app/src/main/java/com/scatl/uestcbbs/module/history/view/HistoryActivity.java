package com.scatl.uestcbbs.module.history.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.scatl.uestcbbs.R;
import com.scatl.uestcbbs.base.BaseActivity;
import com.scatl.uestcbbs.base.BasePresenter;
import com.scatl.uestcbbs.custom.MyLinearLayoutManger;
import com.scatl.uestcbbs.entity.HistoryBean;
import com.scatl.uestcbbs.module.board.view.SingleBoardActivity;
import com.scatl.uestcbbs.module.history.adapter.HistoryAdapter;
import com.scatl.uestcbbs.module.history.presenter.HistoryPresenter;
import com.scatl.uestcbbs.module.post.view.PostDetailActivity;
import com.scatl.uestcbbs.module.user.view.UserDetailActivity;
import com.scatl.uestcbbs.util.Constant;

import org.litepal.LitePal;

import java.util.List;

public class HistoryActivity extends BaseActivity implements HistoryView{

    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private HistoryAdapter historyAdapter;
    private TextView hint;
    private TextView clearAll;

    private HistoryPresenter historyPresenter;

    @Override
    protected int setLayoutResourceId() {
        return R.layout.activity_history;
    }

    @Override
    protected void findView() {
        recyclerView = findViewById(R.id.history_rv);
        hint = findViewById(R.id.history_hint);
        clearAll = findViewById(R.id.history_clear_all);
        toolbar = findViewById(R.id.history_toolbar);
    }

    @Override
    protected void initView() {
        historyPresenter = (HistoryPresenter) presenter;

        clearAll.setOnClickListener(this);

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        historyAdapter = new HistoryAdapter(R.layout.item_history, null);
        recyclerView.setLayoutManager(new MyLinearLayoutManger(this));
        recyclerView.setAdapter(historyAdapter);
        recyclerView.setLayoutAnimation(AnimationUtils.loadLayoutAnimation(this, R.anim.layout_animation_scale_in));

        setData();
    }

    @Override
    protected BasePresenter initPresenter() {
        return new HistoryPresenter();
    }

    @Override
    protected void onClickListener(View view) {
        if (view.getId() == R.id.history_clear_all) {
            historyPresenter.showClearAllWaringDialog(this);
        }
    }

    @Override
    protected void setOnItemClickListener() {
        historyAdapter.setOnItemClickListener((adapter, view1, position) -> {
            if (view1.getId() == R.id.item_history_card_view) {
                Intent intent = new Intent(this, PostDetailActivity.class);
                intent.putExtra(Constant.IntentKey.TOPIC_ID, historyAdapter.getData().get(position).topic_id);
                startActivity(intent);
            }
        });

        historyAdapter.setOnItemChildClickListener((adapter, view1, position) -> {
            if (view1.getId() == R.id.item_history_board_name) {
                Intent intent = new Intent(this, SingleBoardActivity.class);
                intent.putExtra(Constant.IntentKey.BOARD_ID, historyAdapter.getData().get(position).board_id);
                startActivity(intent);
            }
            if (view1.getId() == R.id.item_history_avatar) {
                Intent intent = new Intent(this, UserDetailActivity.class);
                intent.putExtra(Constant.IntentKey.USER_ID, historyAdapter.getData().get(position).user_id);
                startActivity(intent);
            }
        });

        historyAdapter.setOnItemLongClickListener((adapter, view1, position) -> {
            if (view1.getId() == R.id.item_history_card_view) {
                LitePal.delete(HistoryBean.class, historyAdapter.getData().get(position).id);
                setData();
            }
            return true;
        });
    }

    @Override
    public void onClearAllSuccess() {
        showToast("清理成功");
        setData();
    }

    @Override
    public void onClearAllFail() {
        clearAll.setVisibility(View.VISIBLE);
        showToast("清理失败");
    }

    private void setData() {
        List<HistoryBean> historyBeans = LitePal.order("browserTime desc").find(HistoryBean.class);
        historyAdapter.setNewData(historyBeans);
        recyclerView.scheduleLayoutAnimation();
        if (historyBeans.size() == 0) {
            hint.setText("还没有浏览记录");
            clearAll.setVisibility(View.GONE);
        } else {
            clearAll.setVisibility(View.VISIBLE);
        }
    }
}
