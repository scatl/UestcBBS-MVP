package com.scatl.uestcbbs.module.home.view;

import com.scatl.uestcbbs.entity.CollectionListBean;

import java.util.List;

/**
 * author: sca_tl
 * date: 2020/5/3 11:08
 * description:
 */
public interface CollectionListView {
    void onGetCollectionListSuccess(List<CollectionListBean> collectionListBeans, boolean hasNext);
    void onGetCollectionListError(String msg);
}
