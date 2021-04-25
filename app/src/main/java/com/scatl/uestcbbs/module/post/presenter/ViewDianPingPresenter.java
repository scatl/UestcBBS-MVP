package com.scatl.uestcbbs.module.post.presenter;

import android.util.Log;

import com.scatl.uestcbbs.base.BasePresenter;
import com.scatl.uestcbbs.entity.PostDianPingBean;
import com.scatl.uestcbbs.helper.ExceptionHelper;
import com.scatl.uestcbbs.helper.rxhelper.Observer;
import com.scatl.uestcbbs.module.post.model.PostModel;
import com.scatl.uestcbbs.module.post.view.ViewDianPingView;
import com.scatl.uestcbbs.util.Constant;
import com.scatl.uestcbbs.util.ForumUtil;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.disposables.Disposable;

/**
 * author: sca_tl
 * date: 2021/4/6 20:11
 * description:
 */
public class ViewDianPingPresenter extends BasePresenter<ViewDianPingView> {
    private PostModel postModel = new PostModel();

    public void getDianPingList(int tid, int pid, int page) {
        postModel.getCommentList(tid, pid, page, new Observer<String>() {
            @Override
            public void OnSuccess(String s) {
                String html = s.replace("<?xml version=\"1.0\" encoding=\"utf-8\"?>", "")
                        .replace("<root><![CDATA[", "").replace("]]></root>", "");

                try {
                    List<PostDianPingBean> postDianPingBeans = new ArrayList<>();

                    Document document = Jsoup.parse(html);
                    Elements elements = document.select("div[class=pstl]");
                    for (int i = 0; i < elements.size(); i ++) {
                        PostDianPingBean postDianPingBean = new PostDianPingBean();
                        postDianPingBean.userName = elements.get(i).select("div[class=psti]").select("a[class=xi2 xw1]").text();
                        postDianPingBean.comment = elements.get(i).getElementsByClass("psti").get(0).text().replace(elements.get(i).select("div[class=psti]").select("span[class=xg1]").text(), "").replace(postDianPingBean.userName + " ", "");
                        postDianPingBean.date = elements.get(i).select("div[class=psti]").select("span[class=xg1]").text().replace("发表于 ", "");
                        postDianPingBean.uid = ForumUtil.getFromLinkInfo(elements.get(i).select("div[class=psti]").select("a[class=xi2 xw1]").attr("href")).id;
                        postDianPingBean.userAvatar = Constant.USER_AVATAR_URL + postDianPingBean.uid;

                        postDianPingBeans.add(postDianPingBean);
                    }

                    view.onGetPostDianPingListSuccess(postDianPingBeans, s.contains("下一页"));


                } catch (Exception e) {
                    view.onGetPostDianPingListError("获取点评失败：" + e.getMessage());
                }


            }

            @Override
            public void onError(ExceptionHelper.ResponseThrowable e) {
                view.onGetPostDianPingListError("获取点评失败：" + e.message);
            }

            @Override
            public void OnCompleted() { }

            @Override
            public void OnDisposable(Disposable d) {
                disposable.add(d);
            }
        });
    }


    public void findPost(int ptid, int pid) {
        postModel.findPost(ptid, pid, new Observer<String>() {
            @Override
            public void OnSuccess(String s) {

                try {

                    //Log.e("gggg", s);

                    Document document = Jsoup.parse(s);
                    Elements elements = document.select("td[id=postmessage_" + pid + "]");

                    //Log.e("fffff", ptid + "===="+pid + elements.text()+"9999");

                    view.onFindPostSuccess(elements.text());


                } catch (Exception e) {
                    e.printStackTrace();
                    view.onFindPostError("获取评论失败：" + e.getMessage());
                }


            }

            @Override
            public void onError(ExceptionHelper.ResponseThrowable e) {
                e.printStackTrace();
                view.onFindPostError("获取评论失败：" + e.message);
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
