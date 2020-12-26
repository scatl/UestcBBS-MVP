package com.scatl.uestcbbs.module.task.view;

import com.scatl.uestcbbs.entity.TaskBean;

import java.util.List;

/**
 * author: sca_tl
 * date: 2020/12/26 12:11
 * description:
 */
public interface TaskView {
    void onGetNewTaskSuccess(List<TaskBean> taskBeans);
    void onGetNewTaskError(String msg);
    void onGetDoingTaskSuccess(List<TaskBean> taskBeans);
    void onGetDoingTaskError(String msg);
    void onApplyNewTaskSuccess(String msg);
    void onApplyNewTaskError(String msg);
    void onGetTaskAwardSuccess(String msg);
    void onGetTaskAwardError(String msg);
    void onDeleteDoingTaskSuccess(String msg);
    void onDeleteDoingTaskError(String msg);
}
