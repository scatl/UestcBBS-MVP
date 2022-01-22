package com.scatl.uestcbbs.module.collection.presenter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatEditText;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.scatl.uestcbbs.R;
import com.scatl.uestcbbs.base.BasePresenter;
import com.scatl.uestcbbs.entity.CollectionDetailBean;
import com.scatl.uestcbbs.helper.ExceptionHelper;
import com.scatl.uestcbbs.helper.rxhelper.Observer;
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

                    String formhash = document.select("div[class=hdc]").select("div[class=wp]").select("div[class=cl]").select("form[id=scbar_form]").select("input[name=formhash]").attr("value");

                    collectionDetailBean.collectionTitle = document.getElementsByClass("xs2 z").select("a").text();
                    collectionDetailBean.subscribeCount = document.getElementsByClass("clct_flw").select("strong").text();
                    collectionDetailBean.isSubscribe = document.getElementsByClass("clct_flw").select("i").text().equals("取消订阅");
                    collectionDetailBean.collectionDsp = document.getElementsByClass("bm bml pbn").get(0).getElementsByClass("bm_c").get(0).select("div").last().ownText();
                    collectionDetailBean.collectionAuthorLink = document.getElementsByClass("bm bml pbn").get(0).getElementsByClass("bm_c").get(0).getElementsByClass("mbn cl").get(0).select("p").last().select("a").get(0).attr("href");
                    collectionDetailBean.collectionAuthorName = document.getElementsByClass("bm bml pbn").get(0).getElementsByClass("bm_c").get(0).getElementsByClass("mbn cl").get(0).select("p").last().select("a").get(0).text();
                    collectionDetailBean.collectionAuthorId = ForumUtil.getFromLinkInfo(collectionDetailBean.collectionAuthorLink).id;
                    collectionDetailBean.collectionAuthorAvatar = "https://bbs.uestc.edu.cn/uc_server/avatar.php?uid=" + collectionDetailBean.collectionAuthorId + "&size=middle";
                    collectionDetailBean.collectionTags = document.getElementsByClass("bm bml pbn").get(0).getElementsByClass("bm_c").get(0).getElementsByClass("mbn cl").get(0).select("p[class=mbn]").select("a").eachText();
                    collectionDetailBean.ratingScore = Float.parseFloat(document.select("div[class=ptn pbn xg1 cl]").attr("title"));
                    collectionDetailBean.ratingTitle = document.select("div[class=ptn pbn xg1 cl]").text();

                    Elements topics = document.getElementsByClass("tl bm").select("div[class=bm_c]").select("tr");
                    for (int i = 0; i < topics.size(); i ++) {
                        CollectionDetailBean.PostListBean postListBean = new CollectionDetailBean.PostListBean();

                        postListBean.topicTitle = topics.get(i).select("th").select("a").attr("title");
                        postListBean.topicLink = topics.get(i).select("th").select("a").attr("href");
                        postListBean.topicId = ForumUtil.getFromLinkInfo(postListBean.topicLink).id;
                        postListBean.authorLink = topics.get(i).select("td[class=by]").get(0).select("cite").select("a").attr("href");
                        postListBean.authorName = topics.get(i).select("td[class=by]").get(0).select("cite").select("a").text();
                        postListBean.authorId = ForumUtil.getFromLinkInfo(postListBean.authorLink).id;
                        postListBean.authorAvatar = "https://bbs.uestc.edu.cn/uc_server/avatar.php?uid=" + postListBean.authorId + "&size=middle";
                        postListBean.postDate = topics.get(i).select("td[class=by]").get(0).select("em[class=xi1]").text();
                        postListBean.commentCount = topics.get(i).select("td[class=num]").select("a").text();
                        postListBean.viewCount = topics.get(i).select("td[class=num]").select("em").text();

                        postListBean.lastPostAuthorLink = topics.get(i).select("td[class=by]").get(1).select("cite").select("a").attr("href");
                        postListBean.lastPostAuthorName = topics.get(i).select("td[class=by]").get(1).select("cite").select("a").text();
                        postListBean.lastPostAuthorId = ForumUtil.getFromLinkInfo(postListBean.lastPostAuthorLink).id;
                        postListBean.lastPostAuthorAvatar = "https://bbs.uestc.edu.cn/uc_server/avatar.php?uid=" + postListBean.lastPostAuthorId + "&size=middle";
                        postListBean.lastPostDate = topics.get(i).select("td[class=by]").get(1).select("em").text();

                        collectionDetailBean.postListBean.add(postListBean);
                    }

                    view.onGetCollectionSuccess(collectionDetailBean, html.contains("下一页"));
                    view.onGetFormHashSuccess(formhash);

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
            public void OnCompleted() { }

            @Override
            public void OnDisposable(Disposable d) {
                disposable.add(d);
            }
        });
    }


    public void subscribeCollection(int ctid, String op, String formash) {
        collectionModel.subscribeCollection(ctid, op, formash, new Observer<String>() {
            @Override
            public void OnSuccess(String html) {
                if (html.contains("未定义")) {
                    view.onSubscribeCollectionError("操作失败，请先高级授权");
                } else if (html.contains("成功订阅")) {
                    view.onSubscribeCollectionSuccess(true);
                } else if (html.contains("取消订阅")) {
                    view.onSubscribeCollectionSuccess(false);
                } else {
                    view.onSubscribeCollectionError("未知错误，请联系开发者");
                }

            }

            @Override
            public void onError(ExceptionHelper.ResponseThrowable e) {
                view.onSubscribeCollectionError("操作失败：" + e.message );
            }

            @Override
            public void OnCompleted() { }

            @Override
            public void OnDisposable(Disposable d) {
                disposable.add(d);
            }
        });
    }

    public void deleteCollectionPost(String formhash, int citd, int tid) {
        collectionModel.deleteCollectionPost(formhash, tid, citd, new Observer<String>() {
            @Override
            public void OnSuccess(String s) {
                try {
                    Document document = Jsoup.parse(s);
                    String info = document.select("div[id=messagetext]").text();
                    if (info.contains("删除淘专辑内主题成功")) {
                        view.onDeleteCollectionPostSuccess(info);
                    } else {
                        view.onDeleteCollectionPostError(info);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    view.onDeleteCollectionPostError("删除失败:" + e.getMessage());
                }
            }

            @Override
            public void onError(ExceptionHelper.ResponseThrowable e) {
                view.onDeleteCollectionPostError("删除失败：" + e.message);
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

    public void deleteCollection(String formhash, int ctid) {
        collectionModel.deleteCollection(formhash, ctid, new Observer<String>() {
            @Override
            public void OnSuccess(String s) {
                try {
                    Document document = Jsoup.parse(s);
                    String info = document.select("div[id=messagetext]").text();
                    if (info.contains("删除淘专辑成功")) {
                        view.onDeleteCollectionSuccess(info);
                    } else {
                        view.onDeleteCollectionError(info);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    view.onDeleteCollectionError("删除失败:" + e.getMessage());
                }
            }

            @Override
            public void onError(ExceptionHelper.ResponseThrowable e) {
                view.onDeleteCollectionError("删除失败：" + e.message);
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

    public void showDeletePostDialog(Context context, String formhash, int tid, int ctid) {
        final AlertDialog delete_dialog = new MaterialAlertDialogBuilder(context)
                .setPositiveButton("取消", null)
                .setNegativeButton("删除", null)
                .setMessage("确认将该帖子从专辑里删除吗？")
                .setTitle("删除淘帖")
                .create();
        delete_dialog.setOnShowListener(dialogInterface -> {
            Button n = delete_dialog.getButton(AlertDialog.BUTTON_NEGATIVE);
            n.setOnClickListener(view -> {
                deleteCollectionPost(formhash, ctid, tid);
                delete_dialog.dismiss();
            });
        });
        delete_dialog.show();
    }

    public void showDeleteCollectionDialog(Context context, String formhash, int ctid) {
        final AlertDialog delete_dialog = new MaterialAlertDialogBuilder(context)
                .setPositiveButton("取消", null)
                .setNegativeButton("删除", null)
                .setMessage("确认删除淘专辑吗？确认后淘专辑内帖子会被清空，并删除该专辑，操作不可撤销")
                .setTitle("删除淘专辑")
                .create();
        delete_dialog.setOnShowListener(dialogInterface -> {
            Button n = delete_dialog.getButton(AlertDialog.BUTTON_NEGATIVE);
            n.setOnClickListener(view -> {
                deleteCollection(formhash, ctid);
                delete_dialog.dismiss();
            });
        });
        delete_dialog.show();
    }

}
