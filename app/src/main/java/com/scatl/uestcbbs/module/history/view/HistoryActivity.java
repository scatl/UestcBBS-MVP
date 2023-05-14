package com.scatl.uestcbbs.module.history.view;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.view.MenuItem;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.scatl.uestcbbs.R;
import com.scatl.uestcbbs.annotation.ToastType;
import com.scatl.uestcbbs.base.BaseActivity;
import com.scatl.uestcbbs.base.BasePresenter;
import com.scatl.uestcbbs.manager.ForumListManager;
import com.scatl.uestcbbs.module.board.view.BoardActivity;
import com.scatl.uestcbbs.widget.MyLinearLayoutManger;
import com.scatl.uestcbbs.entity.HistoryBean;
import com.scatl.uestcbbs.module.history.adapter.HistoryAdapter;
import com.scatl.uestcbbs.module.history.presenter.HistoryPresenter;
import com.scatl.uestcbbs.module.post.view.NewPostDetailActivity;
import com.scatl.uestcbbs.module.user.view.UserDetailActivity;
import com.scatl.uestcbbs.util.Constant;

import org.litepal.LitePal;

import java.util.List;

public class HistoryActivity extends BaseActivity implements HistoryView{

    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private HistoryAdapter historyAdapter;
    private TextView hint;

    private HistoryPresenter historyPresenter;

    @Override
    protected int setLayoutResourceId() {
        return R.layout.activity_history;
    }

    @Override
    protected void findView() {
        recyclerView = findViewById(R.id.history_rv);
        hint = findViewById(R.id.history_hint);
        toolbar = findViewById(R.id.toolbar);
    }

    @Override
    protected void initView() {
        super.initView();
        historyPresenter = (HistoryPresenter) presenter;
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
    protected void setOnItemClickListener() {
        historyAdapter.setOnItemClickListener((adapter, view1, position) -> {
            if (view1.getId() == R.id.item_history_card_view) {
                Intent intent = new Intent(this, NewPostDetailActivity.class);
                intent.putExtra(Constant.IntentKey.TOPIC_ID, historyAdapter.getData().get(position).topic_id);
                startActivity(intent);
            }
        });

        historyAdapter.setOnItemChildClickListener((adapter, view1, position) -> {
            if (view1.getId() == R.id.item_history_board_name) {
                Intent intent = new Intent(this, BoardActivity.class);
                intent.putExtra(Constant.IntentKey.BOARD_ID, ForumListManager.Companion.getINSTANCE().getParentForum(historyAdapter.getData().get(position).board_id).getId());
                intent.putExtra(Constant.IntentKey.LOCATE_BOARD_ID, historyAdapter.getData().get(position).board_id);
                intent.putExtra(Constant.IntentKey.BOARD_NAME, historyAdapter.getData().get(position).board_name);
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
                historyAdapter.getData().remove(position);
                historyAdapter.notifyItemRemoved(position);
                historyAdapter.notifyItemRangeChanged(position, historyAdapter.getData().size());
            }
            return true;
        });
    }

    @Override
    public void onClearAllSuccess() {
        showToast("清理成功", ToastType.TYPE_SUCCESS);
        setData();
    }

    @Override
    public void onClearAllFail() {
        showToast("清理失败", ToastType.TYPE_ERROR);
    }

    @Override
    protected void onOptionsSelected(MenuItem item) {
        if (item.getItemId() == R.id.delete_all) {
            historyPresenter.showClearAllWaringDialog(this);
        }
    }

    private void setData() {
        List<HistoryBean> historyBeans = LitePal.order("browserTime desc").find(HistoryBean.class);
        historyAdapter.setNewData(historyBeans);
        if (historyBeans.size() == 0) {
            hint.setText("还没有浏览记录");
        }
    }
}
