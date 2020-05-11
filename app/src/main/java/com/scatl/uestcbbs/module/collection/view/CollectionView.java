package com.scatl.uestcbbs.module.collection.view;

import com.scatl.uestcbbs.entity.CollectionDetailBean;

/**
 * author: sca_tl
 * date: 2020/5/5 19:17
 * description:
 */
public interface CollectionView {
    void onGetCollectionSuccess(CollectionDetailBean collectionDetailBean, boolean hasNext);
    void onGetCollectionError(String msg);
}
