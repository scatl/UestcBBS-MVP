package com.scatl.uestcbbs.module.collection.presenter

import com.scatl.uestcbbs.base.BaseVBPresenter
import com.scatl.uestcbbs.entity.CollectionListBean
import com.scatl.uestcbbs.helper.ExceptionHelper.ResponseThrowable
import com.scatl.uestcbbs.helper.rxhelper.Observer
import com.scatl.uestcbbs.module.collection.model.CollectionModel
import com.scatl.uestcbbs.module.collection.view.CollectionListView
import com.scatl.uestcbbs.util.BBSLinkUtil
import com.scatl.uestcbbs.util.Constant
import com.scatl.uestcbbs.util.JsoupParseUtil
import io.reactivex.disposables.Disposable
import org.jsoup.Jsoup
import java.util.regex.Pattern

/**
 * Created by sca_tl at 2023/5/5 11:43
 */
class CollectionListPresenter: BaseVBPresenter<CollectionListView>() {

    private val collectionModel = CollectionModel()

    fun getCollectionList(page: Int, op: String, order: String) {
        collectionModel.getCollectionList(page, op, order, object : Observer<String>() {
            override fun OnSuccess(html: String) {
                val collectionBeans = JsoupParseUtil.parseCollectionList(html)
                mView?.onGetCollectionListSuccess(collectionBeans, html.contains("下一页"))
            }

            override fun onError(e: ResponseThrowable) {
                mView?.onGetCollectionListError("获取数据失败" + e.message)
            }

            override fun OnCompleted() {

            }

            override fun OnDisposable(d: Disposable) {
                mCompositeDisposable?.add(d)
            }
        })
    }

}