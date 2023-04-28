package com.scatl.uestcbbs.module.account.presenter;

import com.scatl.uestcbbs.base.BasePresenter;
import com.scatl.uestcbbs.helper.ExceptionHelper;
import com.scatl.uestcbbs.helper.rxhelper.Observer;
import com.scatl.uestcbbs.module.account.model.AccountModel;
import com.scatl.uestcbbs.module.account.view.ResetPasswordView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import io.reactivex.disposables.Disposable;

public class ResetPasswordPresenter extends BasePresenter<ResetPasswordView> {
    AccountModel accountModel = new AccountModel();

    public void findUserName(String formhash, String student_id, String portal_password) {
        accountModel.findUserName(formhash, student_id, portal_password, new Observer<String>() {
            @Override
            public void OnSuccess(String s) {
                try {
                    Document document = Jsoup.parse(s);
                    String msg = document.select("div[id=messagetext]").text();

                    if (msg.contains("该学号")) {
                        view.onFindUserNameSuccess(msg);
                    } else {
                        view.onFindUserNameError(msg);
                    }

                } catch (Exception e) {
                    view.onFindUserNameError("找回用户名失败：" + e.getMessage());
                }
            }

            @Override
            public void onError(ExceptionHelper.ResponseThrowable e) {
                view.onFindUserNameError("找回用户名失败：" + e.message);
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

    public void resetPassword(String formhash, String username, String student_name,
                              String newpassword, String newpassword2 ,String student_id, String portal_password) {
        accountModel.resetPassword(formhash, username, student_name, newpassword, newpassword2, student_id, portal_password, new Observer<String>() {
            @Override
            public void OnSuccess(String s) {
                try {
                    Document document = Jsoup.parse(s);
                    String msg = document.select("div[id=messagetext]").text();

                    if (msg.contains("密码已重置")) {
                        view.onResetPswSuccess(msg);
                    } else {
                        view.onResetPswError(msg);
                    }

                } catch (Exception e) {
                    view.onResetPswError("密码重置失败：" + e.getMessage());
                }
            }

            @Override
            public void onError(ExceptionHelper.ResponseThrowable e) {
                view.onResetPswError("重置密码失败：" + e.message);
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

    public void getFormHash() {
        accountModel.getFormHash(new Observer<String>() {
            @Override
            public void OnSuccess(String s) {
                try {
                    Document document = Jsoup.parse(s);
                    String formHash = document.select("form[id=scbar_form]").select("input[name=formhash]").attr("value");
                    view.onGetFormHashSuccess(formHash);
                } catch (Exception e) {
                    view.onGetFormHashError("获取相关数据失败，请重试：" + e.getMessage());
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(ExceptionHelper.ResponseThrowable e) {
                view.onGetFormHashError("获取FormHash失败，请重试：" + e.message);
            }

            @Override
            public void OnCompleted() { }

            @Override
            public void OnDisposable(Disposable d) {
                disposable.add(d);
            }
        });
    }

}
