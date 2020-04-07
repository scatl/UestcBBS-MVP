package com.scatl.uestcbbs.history.view;

import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;

import com.chad.library.adapter.base.callback.ItemDragAndSwipeCallback;
import com.chad.library.adapter.base.listener.OnItemSwipeListener;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.scatl.uestcbbs.R;
import com.scatl.uestcbbs.base.BaseBottomFragment;
import com.scatl.uestcbbs.base.BasePresenter;
import com.scatl.uestcbbs.custom.MyLinearLayoutManger;
import com.scatl.uestcbbs.entity.HistoryBean;
import com.scatl.uestcbbs.history.adapter.HistoryAdapter;
import com.scatl.uestcbbs.history.presenter.HistoryPresenter;
import com.scatl.uestcbbs.module.board.view.SingleBoardActivity;
import com.scatl.uestcbbs.module.post.view.PostDetailActivity;
import com.scatl.uestcbbs.module.user.view.UserDetailActivity;
import com.scatl.uestcbbs.util.Constant;

import org.litepal.LitePal;

import java.util.List;

public class HistoryFragment extends BaseBottomFragment implements HistoryView{

    private RecyclerView recyclerView;
    private HistoryAdapter historyAdapter;
    private TextView hint;
    private Button clearAll;

    private HistoryPresenter historyPresenter;

    public static HistoryFragment getInstance(Bundle bundle) {
        HistoryFragment fragment = new HistoryFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected int setLayoutResourceId() {
        return R.layout.fragment_bottom_history;
    }

    @Override
    protected void findView() {
        recyclerView = view.findViewById(R.id.fragment_bottom_history_rv);
        hint = view.findViewById(R.id.fragment_bottom_history_hint);
        clearAll = view.findViewById(R.id.fragment_bottom_history_clear_all);
    }

    @Override
    protected void initView() {
        historyPresenter = (HistoryPresenter) presenter;
        mBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);

        clearAll.setOnClickListener(this);

        historyAdapter = new HistoryAdapter(R.layout.item_history, null);
        recyclerView.setLayoutManager(new MyLinearLayoutManger(mActivity));
        recyclerView.setAdapter(historyAdapter);
        recyclerView.setLayoutAnimation(AnimationUtils.loadLayoutAnimation(mActivity, R.anim.layout_animation_from_top));

        setData();
    }

    @Override
    protected BasePresenter initPresenter() {
        return new HistoryPresenter();
    }

    @Override
    protected void onClickListener(View view) {
        if (view.getId() == R.id.fragment_bottom_history_clear_all) {
            historyPresenter.showClearAllWaringDialog(mActivity);
        }
    }

    @Override
    protected void setOnItemClickListener() {
        historyAdapter.setOnItemClickListener((adapter, view1, position) -> {
            if (view1.getId() == R.id.item_history_card_view) {
                Intent intent = new Intent(mActivity, PostDetailActivity.class);
                intent.putExtra(Constant.IntentKey.TOPIC_ID, historyAdapter.getData().get(position).topic_id);
                startActivity(intent);
            }
        });

        historyAdapter.setOnItemChildClickListener((adapter, view1, position) -> {
            if (view1.getId() == R.id.item_history_board_name) {
                Intent intent = new Intent(mActivity, SingleBoardActivity.class);
                intent.putExtra(Constant.IntentKey.BOARD_ID, historyAdapter.getData().get(position).board_id);
                startActivity(intent);
            }
            if (view1.getId() == R.id.item_history_avatar) {
                Intent intent = new Intent(mActivity, UserDetailActivity.class);
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
    protected double setMaxHeightMultiplier() {
        return 0.95;
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
