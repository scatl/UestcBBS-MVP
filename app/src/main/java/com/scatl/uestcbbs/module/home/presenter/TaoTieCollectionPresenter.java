package com.scatl.uestcbbs.module.home.presenter;

import com.scatl.uestcbbs.base.BasePresenter;
import com.scatl.uestcbbs.entity.CollectionListBean;
import com.scatl.uestcbbs.helper.ExceptionHelper;
import com.scatl.uestcbbs.helper.rxhelper.Observer;
import com.scatl.uestcbbs.helper.rxhelper.SubscriptionManager;
import com.scatl.uestcbbs.module.home.model.HomeModel;
import com.scatl.uestcbbs.module.home.view.CollectionListView;
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
 * date: 2020/5/3 11:07
 * description:
 */
public class TaoTieCollectionPresenter extends BasePresenter<CollectionListView> {
    private HomeModel homeModel = new HomeModel();

    public void getTaoTieCollection(int page, String op) {
        homeModel.getTaoTieCollection(page, op, new Observer<String>() {
            @Override
            public void OnSuccess(String html) {

                List<CollectionListBean> collectionBeans = new ArrayList<>();

                try {

                    Document document = Jsoup.parse(html);
//                    Log.e("ppp", html.contains("还没有淘专辑")+"");
                    Elements elements = document.getElementsByClass("clct_list cl").get(0).getElementsByClass("xld xlda cl");

                    for (int i = 0 ; i < elements.size(); i ++) {
                        CollectionListBean collectionBean = new CollectionListBean();

                        collectionBean.collectionLink = elements.get(i).getElementsByClass("m hm").select("a").attr("href");
                        collectionBean.collectionId = ForumUtil.getIdFromLink(collectionBean.collectionLink);
                        collectionBean.postCount = elements.get(i).getElementsByClass("m hm").select("a").get(0).getElementsByClass("xi2").text();
                        collectionBean.collectionTitle = elements.get(i).getElementsByClass("xw1").select("a").get(0).getElementsByClass("xi2").text();

                        if (elements.get(i).getElementsByClass("ctag_keyword").select("a").eachText().size() != 0) {
                            collectionBean.collectionTags = elements.get(i).getElementsByClass("ctag_keyword").select("a").eachText();
                        } else {
                            collectionBean.collectionTags = new ArrayList<>();
                        }

                        collectionBean.authorLink = elements.get(i).getElementsByClass("xg1").select("a").attr("href");
                        collectionBean.authorId = ForumUtil.getIdFromLink(collectionBean.authorLink);
                        collectionBean.authorName = elements.get(i).getElementsByClass("xg1").select("a").text();
                        collectionBean.authorAvater = "http://bbs.uestc.edu.cn/uc_server/avatar.php?uid=" + collectionBean.authorId + "&size=middle";
                        collectionBean.latestUpdateDate = elements.get(i).getElementsByClass("xg1").get(0).ownText().substring(9);
                        collectionBean.collectionDsp = elements.get(i).select("p").get(0).text();

                        Matcher matcher1 = Pattern.compile("订阅 (\\d+), 评论 (\\d+)").matcher(elements.get(i).select("p").get(1).text());
                        if (matcher1.find()) {
                            collectionBean.subscribeCount = matcher1.group(1);
                            collectionBean.commentCount = matcher1.group(2);
                        }

                        collectionBean.latestPostTitle = elements.get(i).select("p").get(3).select("a").text(); //最新帖子标题
                        collectionBean.latestPostLink = elements.get(i).select("p").get(3).select("a").attr("href"); //最新帖子链接
                        collectionBean.latestPostId = ForumUtil.getIdFromLink(collectionBean.latestPostLink);

                        collectionBeans.add(collectionBean);
                    }

                    view.onGetCollectionListSuccess(collectionBeans, html.contains("下一页"));

                } catch (Exception e) {
                    view.onGetCollectionListError("数据解析失败:" + e.getMessage());
                }


            }

            @Override
            public void onError(ExceptionHelper.ResponseThrowable e) {
                view.onGetCollectionListError("获取数据失败" + e.message);
            }

            @Override
            public void OnCompleted() {

            }

            @Override
            public void OnDisposable(Disposable d) {
                SubscriptionManager.getInstance().add(d);
            }
        });
    }
}
