package com.scatl.uestcbbs.module.collection.view;

import com.scatl.uestcbbs.entity.AddToCollectionBean;

import java.util.List;

/**
 * author: sca_tl
 * date: 2021/4/24 11:55
 * description:
 */
public interface AddToCollectionView {
    void onGetAddToCollectionSuccess(List<AddToCollectionBean> addToCollectionBeanList, int remainNum);
    void onGetAddToCollectionError(String msg);
    void onNoneCollection(String msg);
    void onConfirmAddToCollectionSuccess(String msg);
    void onConfirmAddToCollectionError(String msg);
    void onCreateCollectionSuccess(String msg);
    void onCreateCollectionError(String msg);
}
