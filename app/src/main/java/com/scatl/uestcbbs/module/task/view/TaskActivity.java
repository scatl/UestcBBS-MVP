package com.scatl.uestcbbs.module.task.view;

import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.RecyclerView;

import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.scatl.uestcbbs.R;
import com.scatl.uestcbbs.annotation.TaskType;
import com.scatl.uestcbbs.annotation.ToastType;
import com.scatl.uestcbbs.base.BaseActivity;
import com.scatl.uestcbbs.base.BasePresenter;
import com.scatl.uestcbbs.callback.OnRefresh;
import com.scatl.uestcbbs.custom.MyLinearLayoutManger;
import com.scatl.uestcbbs.entity.TaskBean;
import com.scatl.uestcbbs.module.task.adapter.TaskAdapter;
import com.scatl.uestcbbs.module.task.presenter.TaskPresenter;
import com.scatl.uestcbbs.util.RefreshUtil;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;

import java.util.ArrayList;
import java.util.List;

public class TaskActivity extends BaseActivity implements TaskView{

    Toolbar toolbar;
    SmartRefreshLayout refreshLayout;
    RecyclerView recyclerView;
    TaskAdapter taskAdapter;
    TextView hint;
    CoordinatorLayout coordinatorLayout;

    TaskPresenter taskPresenter;

    List<TaskBean> newTaskBeans;
    String formhash;

    @Override
    protected int setLayoutResourceId() {
        return R.layout.activity_task;
    }

    @Override
    protected void findView() {
        toolbar = findViewById(R.id.task_toolbar);
        refreshLayout = findViewById(R.id.task_refresh);
        recyclerView = findViewById(R.id.task_rv);
        hint = findViewById(R.id.task_hint);
        coordinatorLayout = findViewById(R.id.task_coor_layout);
    }

    @Override
    protected void initView() {
        taskPresenter = (TaskPresenter) presenter;

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        taskAdapter = new TaskAdapter(R.layout.item_task);
        recyclerView.setLayoutManager(new MyLinearLayoutManger(this));
        recyclerView.setAdapter(taskAdapter);
        recyclerView.setLayoutAnimation(AnimationUtils.loadLayoutAnimation(this, R.anim.layout_animation_scale_in));

        refreshLayout.setEnableLoadMore(false);
        refreshLayout.autoRefresh(0 , 300, 1, false);
    }

    @Override
    protected BasePresenter initPresenter() {
        return new TaskPresenter();
    }

    @Override
    protected void setOnItemClickListener() {
        taskAdapter.setOnItemChildClickListener((adapter, view, position) -> {
            if (view.getId() == R.id.item_task_apply_btn) {
                if (TaskType.TYPE_DOING.equals(taskAdapter.getData().get(position).type)) {
                    taskPresenter.getTaskAward(taskAdapter.getData().get(position).id);
                } else if (TaskType.TYPE_NEW.equals(taskAdapter.getData().get(position).type)) {
                    taskPresenter.applyNewTask(taskAdapter.getData().get(position).id);
                }
            }
            if (view.getId() == R.id.item_task_delete_btn) {
                taskPresenter.showDeleteDialog(this, taskAdapter.getData().get(position).id, formhash);
            }
        });
    }

    @Override
    protected void setOnRefreshListener() {
        RefreshUtil.setOnRefreshListener(this, refreshLayout, new OnRefresh() {
            @Override
            public void onRefresh(RefreshLayout refreshLayout) {
                taskPresenter.getNewTaskList();
            }

            @Override
            public void onLoadMore(RefreshLayout refreshLayout) {

            }
        });
    }

    @Override
    public void onGetNewTaskSuccess(List<TaskBean> newTaskBeans, String formhash) {
        this.newTaskBeans = newTaskBeans;
        this.formhash = formhash;
        taskPresenter.getDoingTaskList();
    }

    @Override
    public void onGetNewTaskError(String msg) {
        hint.setText(msg);
        taskAdapter.setNewData(new ArrayList<>());
        refreshLayout.finishRefresh();
    }

    @Override
    public void onGetDoingTaskSuccess(List<TaskBean> doingTaskBeans, String formhash) {
        this.formhash = formhash;
        doingTaskBeans.addAll(newTaskBeans);
        taskAdapter.setNewData(doingTaskBeans);
        recyclerView.scheduleLayoutAnimation();
        refreshLayout.finishRefresh();
    }

    @Override
    public void onGetDoingTaskError(String msg) {
        hint.setText(msg);
        taskAdapter.setNewData(new ArrayList<>());
        refreshLayout.finishRefresh();
    }

    @Override
    public void onApplyNewTaskSuccess(String msg, int taskId) {
        refreshLayout.autoRefresh(0 , 300, 1, false);
        showToast(msg, ToastType.TYPE_SUCCESS);
        if (taskId == 3) {//新手导航任务
            taskPresenter.showFreshUserHandBookDialog(this);
        }
    }

    @Override
    public void onApplyNewTaskError(String msg) {
        showToast(msg, ToastType.TYPE_ERROR);
    }

    @Override
    public void onGetTaskAwardSuccess(String msg) {
        refreshLayout.autoRefresh(0 , 300, 1, false);
        showToast(msg, ToastType.TYPE_SUCCESS);
    }

    @Override
    public void onGetTaskAwardError(String msg) {
        showToast(msg, ToastType.TYPE_ERROR);
    }

    @Override
    public void onDeleteDoingTaskSuccess(String msg) {
        refreshLayout.autoRefresh(0 , 300, 1, false);
        showToast(msg, ToastType.TYPE_SUCCESS);
    }

    @Override
    public void onDeleteDoingTaskError(String msg) {
        showToast(msg, ToastType.TYPE_ERROR);
    }
}