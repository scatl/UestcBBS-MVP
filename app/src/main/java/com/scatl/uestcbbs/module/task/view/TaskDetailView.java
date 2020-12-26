package com.scatl.uestcbbs.module.task.view;

import com.scatl.uestcbbs.entity.TaskDetailBean;

/**
 * author: sca_tl
 * date: 2020/12/26 18:21
 * description:
 */
public interface TaskDetailView {
    void onGetTaskDetailSuccess(TaskDetailBean taskDetailBean);
    void onGetTaskDetailError(String msg);
}
