package com.scatl.uestcbbs.module.post.presenter;

import android.util.Log;

import com.scatl.uestcbbs.base.BasePresenter;
import com.scatl.uestcbbs.helper.ExceptionHelper;
import com.scatl.uestcbbs.helper.rxhelper.Observer;
import com.scatl.uestcbbs.module.post.model.PostModel;
import com.scatl.uestcbbs.module.post.view.PostAppendFragment;
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

    public void getFormHash(int tid, int pid) {
        postModel.postAppendFormHash(tid, pid, new Observer<String>() {
            @Override
            public void OnSuccess(String s) {
                if (s.contains("只能在自己的")) {
                    view.onGetFormHashError("该功能需要cookies支持，请到帐号管理页面授权后使用");
                } else {
                    try {
                        Document document = Jsoup.parse(s);
                        String formHash = document.select("form[id=postappendform]").select("input[id=formhash]").attr("value");
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

}
