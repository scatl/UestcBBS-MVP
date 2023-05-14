package com.scatl.uestcbbs.module.collection.view

import com.scatl.uestcbbs.base.BaseView
import com.scatl.uestcbbs.entity.CollectionListBean

/**
 * Created by sca_tl at 2023/5/5 11:43
 */
interface CollectionListView: BaseView {
    fun onGetCollectionListSuccess(collectionListBeans: List<CollectionListBean>, hasNext: Boolean)
    fun onGetCollectionListError(msg: String?)
}