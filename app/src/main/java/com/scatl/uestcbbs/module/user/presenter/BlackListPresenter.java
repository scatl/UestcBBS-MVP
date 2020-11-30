package com.scatl.uestcbbs.module.user.presenter;

import com.scatl.uestcbbs.base.BasePresenter;
import com.scatl.uestcbbs.entity.BlackListBean;
import com.scatl.uestcbbs.helper.ExceptionHelper;
import com.scatl.uestcbbs.helper.rxhelper.Observer;
import com.scatl.uestcbbs.module.user.model.UserModel;
import com.scatl.uestcbbs.module.user.view.BlackListView;
import com.scatl.uestcbbs.util.ForumUtil;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.disposables.Disposable;

/**
 * author: sca_tl
 * date: 2020/11/28 13:08
 * description:
 */
public class BlackListPresenter extends BasePresenter<BlackListView> {
    UserModel userModel = new UserModel();

    public void getBlackList(int page) {
        userModel.getAccountBlackList(page, new Observer<String>() {
            @Override
            public void OnSuccess(String s) {
                if (s.contains("先登录后才能继续")) {
                    view.onGetBlackListError("请先到帐号管理页面进行授权");
                } else {
                    try {
                        Document document = Jsoup.parse(s);
                        Elements elements = document.select("ul[class=buddy cl]").select("li");
                        List<BlackListBean> list = new ArrayList<>();
                        for (int i = 0; i < elements.size(); i ++) {
                            BlackListBean blackListBean = new BlackListBean();
                            blackListBean.userName = elements.get(i).select("h4").select("a").get(1).text();
                            blackListBean.uid = ForumUtil.getFromLinkInfo(elements.get(i).select("h4").select("a").get(1).attr("href")).id;
                            blackListBean.avatar = "https://bbs.uestc.edu.cn/uc_server/avatar.php?uid=" + blackListBean.uid + "&size=middle";
                            list.add(blackListBean);
                        }
                        if (list.size() != 0){
                            view.onGetBlackListSuccess(list, s.contains("下一页"));
                        } else {
                            view.onGetBlackListError("啊哦，这里空空的");
                        }
                    } catch (Exception e) {
                        view.onGetBlackListError("获取黑名单列表失败：\n" + e.getMessage());
                    }
                }
            }

            @Override
            public void onError(ExceptionHelper.ResponseThrowable e) {
                view.onGetBlackListError("获取黑名单列表失败：\n" + e.message);
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
