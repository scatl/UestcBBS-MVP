package com.scatl.uestcbbs.module.collection.presenter;

import com.scatl.uestcbbs.base.BasePresenter;
import com.scatl.uestcbbs.entity.CollectionDetailBean;
import com.scatl.uestcbbs.helper.ExceptionHelper;
import com.scatl.uestcbbs.helper.rxhelper.Observer;
import com.scatl.uestcbbs.helper.rxhelper.SubscriptionManager;
import com.scatl.uestcbbs.module.collection.view.CollectionView;
import com.scatl.uestcbbs.module.collection.model.CollectionModel;
import com.scatl.uestcbbs.util.ForumUtil;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.ArrayList;

import io.reactivex.disposables.Disposable;

/**
 * author: sca_tl
 * date: 2020/5/5 19:16
 * description:
 */
public class CollectionPresenter extends BasePresenter<CollectionView> {

    private CollectionModel collectionModel = new CollectionModel();

    public void getCollectionDetail(int ctid, int page) {
        collectionModel.getCollectionDetail(ctid, page, new Observer<String>() {
            @Override
            public void OnSuccess(String html) {

                try {

                    CollectionDetailBean collectionDetailBean = new CollectionDetailBean();
                    collectionDetailBean.subscriberBean = new ArrayList<>();
                    collectionDetailBean.authorOtherCollection = new ArrayList<>();
                    collectionDetailBean.postListBean = new ArrayList<>();

                    Document document = Jsoup.parse(html);

                    collectionDetailBean.collectionTitle = document.getElementsByClass("xs2 z").select("a").text();
                    collectionDetailBean.subscribeCount = document.getElementsByClass("clct_flw").select("strong").text();
                    collectionDetailBean.isSubscribe = document.getElementsByClass("clct_flw").select("i").text().equals("取消订阅");
                    collectionDetailBean.collectionDsp = document.getElementsByClass("bm bml pbn").get(0).getElementsByClass("bm_c").get(0).select("div").last().ownText();
                    collectionDetailBean.collectionAuthorLink = document.getElementsByClass("bm bml pbn").get(0).getElementsByClass("bm_c").get(0).getElementsByClass("mbn cl").get(0).select("p").last().select("a").get(0).attr("href");
                    collectionDetailBean.collectionAuthorName = document.getElementsByClass("bm bml pbn").get(0).getElementsByClass("bm_c").get(0).getElementsByClass("mbn cl").get(0).select("p").last().select("a").get(0).text();
                    collectionDetailBean.collectionAuthorId = ForumUtil.getIdFromLink(collectionDetailBean.collectionAuthorLink) + "";
                    collectionDetailBean.collectionAuthorAvatar = "http://bbs.uestc.edu.cn/uc_server/avatar.php?uid=" + collectionDetailBean.collectionAuthorId + "&size=middle";
                    collectionDetailBean.collectionTags = document.getElementsByClass("bm bml pbn").get(0).getElementsByClass("bm_c").get(0).getElementsByClass("mbn cl").get(0).select("p[class=mbn]").select("a").eachText();
                    collectionDetailBean.ratingScore = Float.parseFloat(document.select("div[class=ptn pbn xg1 cl]").attr("title"));
                    collectionDetailBean.ratingTitle = document.select("div[class=ptn pbn xg1 cl]").text();

                    Elements topics = document.getElementsByClass("tl bm").select("div[class=bm_c]").select("tr");
                    for (int i = 0; i < topics.size(); i ++) {
                        CollectionDetailBean.PostListBean postListBean = new CollectionDetailBean.PostListBean();

                        postListBean.topicTitle = topics.get(i).select("th").select("a").attr("title");
                        postListBean.topicLink = topics.get(i).select("th").select("a").attr("href");
                        postListBean.topicId = ForumUtil.getIdFromLink(postListBean.topicLink);
                        postListBean.authorLink = topics.get(i).select("td[class=by]").get(0).select("cite").select("a").attr("href");
                        postListBean.authorName = topics.get(i).select("td[class=by]").get(0).select("cite").select("a").text();
                        postListBean.authorId = ForumUtil.getIdFromLink(postListBean.authorLink);
                        postListBean.authorAvatar = "http://bbs.uestc.edu.cn/uc_server/avatar.php?uid=" + postListBean.authorId + "&size=middle";
                        postListBean.postDate = topics.get(i).select("td[class=by]").get(0).select("em[class=xi1]").text();
                        postListBean.commentCount = topics.get(i).select("td[class=num]").select("a").text();
                        postListBean.viewCount = topics.get(i).select("td[class=num]").select("em").text();

                        postListBean.lastPostAuthorLink = topics.get(i).select("td[class=by]").get(1).select("cite").select("a").attr("href");
                        postListBean.lastPostAuthorName = topics.get(i).select("td[class=by]").get(1).select("cite").select("a").text();
                        postListBean.lastPostAuthorId = ForumUtil.getIdFromLink(postListBean.lastPostAuthorLink);
                        postListBean.lastPostAuthorAvatar = "http://bbs.uestc.edu.cn/uc_server/avatar.php?uid=" + postListBean.lastPostAuthorId + "&size=middle";
                        postListBean.lastPostDate = topics.get(i).select("td[class=by]").get(1).select("em").text();

                        collectionDetailBean.postListBean.add(postListBean);
                    }

                    view.onGetCollectionSuccess(collectionDetailBean, html.contains("下一页"));

                } catch (Exception e) {
                    view.onGetCollectionError("数据解析失败:" + e.getMessage());
                    e.printStackTrace();
                }


            }

            @Override
            public void onError(ExceptionHelper.ResponseThrowable e) {
                view.onGetCollectionError("获取数据失败" + e.message);
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
