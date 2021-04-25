package com.scatl.uestcbbs.module.message.presenter;

import android.util.Log;

import com.scatl.uestcbbs.base.BasePresenter;
import com.scatl.uestcbbs.entity.DianPingMessageBean;
import com.scatl.uestcbbs.helper.ExceptionHelper;
import com.scatl.uestcbbs.helper.rxhelper.Observer;
import com.scatl.uestcbbs.module.message.model.MessageModel;
import com.scatl.uestcbbs.module.message.view.DianPingMessageView;
import com.scatl.uestcbbs.util.CommonUtil;
import com.scatl.uestcbbs.util.Constant;
import com.scatl.uestcbbs.util.ForumUtil;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.reactivex.disposables.Disposable;

/**
 * author: sca_tl
 * date: 2021/4/18 17:52
 * description:
 */
public class DianPingMsgPresenter extends BasePresenter<DianPingMessageView> {
    private MessageModel messageModel = new MessageModel();

    public void getDianPingMsg(int page) {
        messageModel.getDianPingMsg(page, new Observer<String>() {
            @Override
            public void OnSuccess(String s) {
                try {
                    Document document = Jsoup.parse(s);
                    Elements elements = document.select("div[class=ct2_a wp cl]").select("div[class=xld xlda]").select("div[class=nts]").select("dl");

                    List<DianPingMessageBean> dianPingMessageBeans = new ArrayList<>();
                    for (int i = 0; i < elements.size(); i ++) {
                        DianPingMessageBean d = new DianPingMessageBean();
                        d.time = elements.get(i).select("span[class=xg1 xw0]").text();
                        d.userName = elements.get(i).select("dd[class=ntc_body]").select("a").get(0).text();
                        d.uid = ForumUtil.getFromLinkInfo(elements.get(i).select("dd[class=ntc_body]").select("a").get(0).attr("href")).id;
                        d.userAvatar = Constant.USER_AVATAR_URL + d.uid;
                        d.tid = ForumUtil.getFromLinkInfo(elements.get(i).select("dd[class=ntc_body]").select("a").get(1).attr("href")).id;
                        d.topicTitle = elements.get(i).select("dd[class=ntc_body]").select("a").get(1).text();
                        d.pid = ForumUtil.getFromLinkInfo(elements.get(i).select("dd[class=ntc_body]").select("a").get(2).attr("href")).id;

                        dianPingMessageBeans.add(d);
                    }

                    view.onGetDianPingMessageSuccess(dianPingMessageBeans, s.contains("下一页"));

                } catch (Exception e) {
                    view.onGetDianPingMessageError("获取点评消息失败：" + e.getMessage());
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(ExceptionHelper.ResponseThrowable e) {
                view.onGetDianPingMessageError("获取点评消息失败：" + e.message);
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
