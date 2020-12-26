package com.scatl.uestcbbs.module.task.presenter;

import android.util.Log;

import com.scatl.uestcbbs.annotation.TaskType;
import com.scatl.uestcbbs.api.ApiConstant;
import com.scatl.uestcbbs.base.BasePresenter;
import com.scatl.uestcbbs.entity.TaskBean;
import com.scatl.uestcbbs.entity.TaskDetailBean;
import com.scatl.uestcbbs.helper.ExceptionHelper;
import com.scatl.uestcbbs.helper.rxhelper.Observer;
import com.scatl.uestcbbs.module.task.model.TaskModel;
import com.scatl.uestcbbs.module.task.view.TaskDetailView;
import com.scatl.uestcbbs.util.CommonUtil;
import com.scatl.uestcbbs.util.ForumUtil;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.disposables.Disposable;

/**
 * author: sca_tl
 * date: 2020/12/26 18:23
 * description:
 */
public class TaskDetailPresenter extends BasePresenter<TaskDetailView> {
    TaskModel taskModel = new TaskModel();

    public void getTaskDetail(int id) {
        taskModel.getTaskDetail(id, new Observer<String>() {
            @Override
            public void OnSuccess(String s) {
                if (s.contains("需要先登录")) {
                    view.onGetTaskDetailError("请先到帐号管理页面高级授权后进行此操作");
                } else {
                    try {

                        Document document = Jsoup.parse(s);
                        Elements elements = document.select("div[class=ct2_a wp cl]").select("div[class=bm bw0]");

                        TaskDetailBean taskDetailBean = new TaskDetailBean();
                        taskDetailBean.otherInfo = elements.select("p[class=xg2]").text();
                        //taskDetailBean.icon = ApiConstant.BBS_BASE_URL + elements.select("table[class=tfm]")


                    } catch (Exception e) {
                        view.onGetTaskDetailError("获取任务详情失败：" + e.getMessage());
                    }
                }
            }

            @Override
            public void onError(ExceptionHelper.ResponseThrowable e) {
                view.onGetTaskDetailError("获取任务详情失败：" + e.message);
            }

            @Override
            public void OnCompleted() {

            }

            @Override
            public void OnDisposable(Disposable d) {
                disposable.add(d);
            }
        });
    }
}
