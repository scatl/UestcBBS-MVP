package com.scatl.uestcbbs.module.task.presenter;

import android.content.Context;
import android.content.Intent;
import android.widget.Button;

import androidx.appcompat.app.AlertDialog;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.scatl.uestcbbs.annotation.TaskType;
import com.scatl.uestcbbs.api.ApiConstant;
import com.scatl.uestcbbs.base.BasePresenter;
import com.scatl.uestcbbs.entity.TaskBean;
import com.scatl.uestcbbs.helper.ExceptionHelper;
import com.scatl.uestcbbs.helper.rxhelper.Observer;
import com.scatl.uestcbbs.module.post.view.PostDetailActivity;
import com.scatl.uestcbbs.module.task.model.TaskModel;
import com.scatl.uestcbbs.module.task.view.TaskView;
import com.scatl.uestcbbs.util.Constant;
import com.scatl.uestcbbs.util.ForumUtil;
import com.scatl.uestcbbs.util.SharePrefUtil;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.disposables.Disposable;

/**
 * author: sca_tl
 * date: 2020/12/26 12:12
 * description:
 */
public class TaskPresenter extends BasePresenter<TaskView> {
    TaskModel taskModel = new TaskModel();

    public void getNewTaskList() {
        taskModel.getNewTask(new Observer<String>() {
            @Override
            public void OnSuccess(String s) {
                if (s.contains("需要先登录")) {
                    view.onGetNewTaskError("请先到帐号管理页面高级授权后进行此操作");
                } else {
                    try {

                        Document document = Jsoup.parse(s);
                        Elements elements = document.select("div[class=ct2_a wp cl]").select("div[class=ptm]").select("table").select("tbody").select("tr");

                        String formhash = document.select("form[id=scbar_form]").select("input[name=formhash]").attr("value");

                        List<TaskBean> taskBeans = new ArrayList<>();
                        for (int i = 0; i < elements.size(); i ++) {
                            TaskBean taskBean = new TaskBean();
                            taskBean.type = TaskType.TYPE_NEW;
                            taskBean.name = elements.get(i).select("td[class=bbda ptm pbm]").select("h3").select("a").get(0).text();
                            taskBean.id = ForumUtil.getFromLinkInfo(elements.get(i).select("td[class=bbda ptm pbm]").select("h3").select("a").get(0).attr("href")).id;
                            taskBean.popularNum = Integer.parseInt(elements.get(i).select("td[class=bbda ptm pbm]").select("h3").select("span[class=xs1 xg2 xw0]").select("a").text());
                            taskBean.dsp = elements.get(i).select("td[class=bbda ptm pbm]").select("p[class=xg2]").text();
                            taskBean.award = elements.get(i).select("td[class=xi1 bbda hm]").text();
                            taskBean.icon = ApiConstant.BBS_BASE_URL + elements.get(i).select("td").get(0).select("img").attr("src");

                            taskBeans.add(taskBean);
                        }

                        view.onGetNewTaskSuccess(taskBeans, formhash);

                    } catch (Exception e) {
                        view.onGetNewTaskError("获取任务失败：" + e.getMessage());
                    }
                }
            }

            @Override
            public void onError(ExceptionHelper.ResponseThrowable e) {
                view.onGetNewTaskError("获取任务失败：" + e.message);
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

    public void getDoingTaskList() {
        taskModel.getDoingTask(new Observer<String>() {
            @Override
            public void OnSuccess(String s) {
                if (s.contains("需要先登录")) {
                    view.onGetNewTaskError("请先到帐号管理页面高级授权后进行此操作");
                } else {
                    try {

                        Document document = Jsoup.parse(s);
                        Elements elements = document.select("div[class=ct2_a wp cl]").select("div[class=ptm]").select("table").select("tbody").select("tr");

                        String formhash = document.select("form[id=scbar_form]").select("input[name=formhash]").attr("value");

                        List<TaskBean> taskBeans = new ArrayList<>();
                        for (int i = 0; i < elements.size(); i ++) {
                            TaskBean taskBean = new TaskBean();
                            taskBean.type = TaskType.TYPE_DOING;
                            taskBean.name = elements.get(i).select("td[class=bbda ptm pbm]").select("h3").select("a").get(0).text();
                            taskBean.id = ForumUtil.getFromLinkInfo(elements.get(i).select("td[class=bbda ptm pbm]").select("h3").select("a").get(0).attr("href")).id;
                            taskBean.popularNum = Integer.parseInt(elements.get(i).select("td[class=bbda ptm pbm]").select("h3").select("span[class=xs1 xg2 xw0]").select("a").text());
                            taskBean.dsp = elements.get(i).select("td[class=bbda ptm pbm]").select("p[class=xg2]").text();
                            taskBean.award = elements.get(i).select("td[class=xi1 bbda hm]").text();
                            taskBean.progress = Double.parseDouble(elements.get(i).select("td[class=bbda ptm pbm]").select("div[class=xs0]").text().replace("已完成 ", "").replace("%", ""));
                            taskBean.icon = ApiConstant.BBS_BASE_URL + elements.get(i).select("td").get(0).select("img").attr("src");

                            taskBeans.add(taskBean);
                        }

                        view.onGetDoingTaskSuccess(taskBeans, formhash);

                    } catch (Exception e) {
                        e.printStackTrace();
                        view.onGetDoingTaskError("获取任务失败：" + e.getMessage());
                    }
                }
            }

            @Override
            public void onError(ExceptionHelper.ResponseThrowable e) {
                view.onGetDoingTaskError("获取任务失败：" + e.message);
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

    public void applyNewTask(int id) {
        taskModel.applyNewTask(id, new Observer<String>() {
            @Override
            public void OnSuccess(String s) {
                try {
                    Document document = Jsoup.parse(s);
                    String msg = document.select("div[id=messagetext]").text();
                    view.onApplyNewTaskSuccess(msg, id);
                } catch (Exception e) {
                    view.onApplyNewTaskError("申请任务失败：" + e.getMessage());
                }
            }

            @Override
            public void onError(ExceptionHelper.ResponseThrowable e) {
                view.onApplyNewTaskError("申请任务失败：" + e.message);
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

    public void getTaskAward(int id) {
        taskModel.getTaskAward(id, new Observer<String>() {
            @Override
            public void OnSuccess(String s) {
                try {
                    Document document = Jsoup.parse(s);
                    String msg = document.select("div[id=messagetext]").text();
                    view.onGetTaskAwardSuccess(msg);
                } catch (Exception e) {
                    view.onGetTaskAwardError("领取奖励失败：" + e.getMessage());
                }
            }

            @Override
            public void onError(ExceptionHelper.ResponseThrowable e) {
                view.onGetTaskAwardError("领取奖励失败：" + e.message);
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

    public void deleteDoingTask(int id, String formhash) {
        taskModel.deleteDoingTask(id, formhash, new Observer<String>() {
            @Override
            public void OnSuccess(String s) {
                try {
                    Document document = Jsoup.parse(s);
                    String msg = document.select("div[id=messagetext]").text();
                    view.onDeleteDoingTaskSuccess(msg);
                } catch (Exception e) {
                    view.onDeleteDoingTaskError("放弃任务失败：" + e.getMessage());
                }
            }

            @Override
            public void onError(ExceptionHelper.ResponseThrowable e) {
                view.onDeleteDoingTaskError("放弃任务失败：" + e.message);
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

    public void showDeleteDialog(Context context, int id, String formhash) {
        final AlertDialog dialog = new MaterialAlertDialogBuilder(context)
                .setPositiveButton("取消", null)
                .setNegativeButton("确认", null)
                .setMessage("放弃该任务后，进度会重置。确认放弃吗？")
                .setTitle("放弃任务")
                .create();
        dialog.setOnShowListener(dialogInterface -> {
            Button n = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);
            n.setOnClickListener(view -> {
                deleteDoingTask(id, formhash);
                dialog.dismiss();
            });
        });
        dialog.show();
    }

    public void showFreshUserHandBookDialog(Context context) {
        final AlertDialog dialog = new MaterialAlertDialogBuilder(context)
                .setPositiveButton("确认", null)
                .setNegativeButton("取消", null)
                .setMessage("在新手导航主题帖回复有水滴奖励，是否跳转以便回复帖子？回复后，请返回任务列表领取奖励")
                .setTitle("跳转")
                .create();
        dialog.setOnShowListener(dialogInterface -> {
            Button p = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            p.setOnClickListener(view -> {
                Intent intent3 = new Intent(context,  PostDetailActivity.class);
                intent3.putExtra(Constant.IntentKey.TOPIC_ID, 1821753);
                context.startActivity(intent3);
                dialog.dismiss();
            });
        });
        dialog.show();
    }
}
