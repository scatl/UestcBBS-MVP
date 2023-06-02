package com.scatl.uestcbbs.module.collection.presenter;

import com.scatl.uestcbbs.App;
import com.scatl.uestcbbs.base.BasePresenter;
import com.scatl.uestcbbs.entity.AddToCollectionBean;
import com.scatl.uestcbbs.helper.ExceptionHelper;
import com.scatl.uestcbbs.helper.rxhelper.Observer;
import com.scatl.uestcbbs.module.collection.model.CollectionModel;
import com.scatl.uestcbbs.module.collection.view.AddToCollectionView;
import com.scatl.uestcbbs.util.SharePrefUtil;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.disposables.Disposable;

/**
 * author: sca_tl
 * date: 2021/4/24 11:55
 * description:
 */
public class AddToCollectionPresenter extends BasePresenter<AddToCollectionView> {
    private CollectionModel collectionModel = new CollectionModel();

    public void addToCollection(int tid) {
        collectionModel.addToCollection(tid, new Observer<String>() {
            @Override
            public void OnSuccess(String s) {
                try {

                    if (s.contains("请先登录")) {
                        view.onGetAddToCollectionError("请获取Cookies后使用该功能");
                    } else if (s.contains("您还没有创建淘专辑")){
                        view.onNoneCollection("您还没有创建淘专辑");
                    }else {
                        Document document = Jsoup.parse(s);
                        Elements elements = document.select("select[id=selectCollection]").select("option");
                        List<AddToCollectionBean> addToCollectionBeanList = new ArrayList<>();
                        for (int i = 0; i < elements.size(); i ++) {
                            AddToCollectionBean a = new AddToCollectionBean();
                            a.name = elements.get(i).text();
                            a.ctid = Integer.parseInt(elements.get(i).attr("value"));
                            addToCollectionBeanList.add(a);
                        }

                        int remainNum = Integer.parseInt(document.select("span[id=reamincreatenum]").text());
                        view.onGetAddToCollectionSuccess(addToCollectionBeanList, remainNum);
                    }

                } catch (Exception e) {
                    view.onGetAddToCollectionError("获取淘专辑失败：\n" + e.getMessage());
                }
            }

            @Override
            public void onError(ExceptionHelper.ResponseThrowable e) {
                view.onGetAddToCollectionError("获取淘专辑失败：\n" + e.message);
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

    public void confirmAddToCollection(String reason, int tid, int ctid) {
        collectionModel.confirmAddToCollection(SharePrefUtil.getForumHash(App.getContext()), reason, tid, ctid, new Observer<String>() {
            @Override
            public void OnSuccess(String s) {

                try {
                    Document document = Jsoup.parse(s);
                    String info = document.select("div[id=messagetext]").text();
                    if (info.contains("淘帖成功")) {
                        view.onConfirmAddToCollectionSuccess(info);
                    } else {
                        view.onConfirmAddToCollectionError(info);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    view.onConfirmAddToCollectionError("添加失败:" + e.getMessage());
                }
            }

            @Override
            public void onError(ExceptionHelper.ResponseThrowable e) {
                view.onConfirmAddToCollectionError("添加失败：" + e.message);
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

    public void createCollection(String title, String desc, String keyword) {
        collectionModel.createCollection(SharePrefUtil.getForumHash(App.getContext()), title, desc, keyword, new Observer<String>() {
            @Override
            public void OnSuccess(String s) {

                try {
                    Document document = Jsoup.parse(s);
                    String info = document.select("div[id=messagetext]").text();
                    if (info.contains("新建淘专辑成功")) {
                        view.onCreateCollectionSuccess("创建成功");
                    } else {
                        view.onCreateCollectionError(info);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    view.onConfirmAddToCollectionError("添加失败:" + e.getMessage());
                }
            }

            @Override
            public void onError(ExceptionHelper.ResponseThrowable e) {
                view.onCreateCollectionError("创建失败：" + e.message);
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
