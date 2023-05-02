package com.scatl.uestcbbs.module.post.presenter;

import com.scatl.uestcbbs.base.BasePresenter;
import com.scatl.uestcbbs.helper.ExceptionHelper;
import com.scatl.uestcbbs.helper.rxhelper.Observer;
import com.scatl.uestcbbs.module.post.model.PostModel;
import com.scatl.uestcbbs.module.post.view.PostAppendView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import io.reactivex.disposables.Disposable;

/**
 * author: sca_tl
 * date: 2020/5/27 14:11
 * description:
 */
public class PostAppendPresenter extends BasePresenter<PostAppendView> {

    private PostModel postModel = new PostModel();

    public void getAppendFormHash(int tid, int pid) {
        postModel.postAppendFormHash(tid, pid, new Observer<String>() {
            @Override
            public void OnSuccess(String s) {
                if (s.contains("只能在自己的")) {
                    view.onGetFormHashError("该功能需要cookies支持，请到帐号管理页面授权后使用");
                } else {
                    try {
                        Document document = Jsoup.parse(s);
                        String formHash = document.select("form[id=postappendform]").select("input[name=formhash]").attr("value");
                        view.onGetFormHashSuccess(formHash);
                    } catch (Exception e) {
                        view.onGetFormHashError("获取相关数据失败，请重试：" + e.getMessage());
                        e.printStackTrace();
                    }

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

    public void postAppendSubmit(int tid, int pid, String formHash, String content) {
        postModel.postAppendSubmit(tid, pid, formHash, content, new Observer<String>() {
            @Override
            public void OnSuccess(String s) {
                if (s.contains("添加成功")) {
                    view.onPostAppendSuccess("内容补充成功，请稍等几分钟后查看");
                } else {
                    view.onPostAppendError("内容补充失败，未知错误");
                }
            }

            @Override
            public void onError(ExceptionHelper.ResponseThrowable e) {
                view.onPostAppendError("内容补充失败：" + e.message);
            }

            @Override
            public void OnCompleted() { }

            @Override
            public void OnDisposable(Disposable d) {
                disposable.add(d);
            }
        });
    }

    public void getDianPingFormHash(int tid, int pid) {
        postModel.getDianPingFormHash(tid, pid, new Observer<String>() {
            @Override
            public void OnSuccess(String s) {
                if (s.contains("您不能点评")) {
                    view.onGetFormHashError("无法点评，可能的原因：\n1、该功能需要cookies支持，请到帐号管理页面授权后使用，若您已经授权，则\n2、该帖不能点评");
                } else {
                    try {
                        Document document = Jsoup.parse(s);
                        String formHash = document.select("form[id=scbar_form]").select("input[name=formhash]").attr("value");
                        view.onGetFormHashSuccess(formHash);
                    } catch (Exception e) {
                        view.onGetFormHashError("获取相关数据失败，请重试：" + e.getMessage());
                        e.printStackTrace();
                    }

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

    public void sendDianPing(int tid, int pid, String formHash, String content) {
        postModel.sendDianPing(tid, pid, formHash, content, new Observer<String>() {
            @Override
            public void OnSuccess(String s) {
                if (s.contains("点评成功")) {
                    view.onSubmitDianPingSuccess("点评成功");
                } else {
                    view.onSubmitDianPingError("点评失败");
                }
            }

            @Override
            public void onError(ExceptionHelper.ResponseThrowable e) {
                view.onSubmitDianPingError("点评失败：" + e.message);
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
