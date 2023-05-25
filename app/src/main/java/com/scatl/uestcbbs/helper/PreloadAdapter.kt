package com.scatl.uestcbbs.helper

import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.scatl.uestcbbs.entity.PostDetailBean
import com.scatl.uestcbbs.manager.BlackListManager
import kotlin.math.max

/**
 * Created by sca_tl at 2023/2/17 14:51
 */
open class PreloadAdapter<T, K : BaseViewHolder> : BaseQuickAdapter<T, K> {

    private var mOnPreload: (() -> Unit)? = null
    private var preloadItemCount = 5
    private var scrollState = RecyclerView.SCROLL_STATE_IDLE
    var isPreloading = false

    constructor(layoutResId: Int, onPreload: (() -> Unit)?): super(layoutResId) {
        mOnPreload = onPreload
    }

    constructor(data: List<T>?, onPreload: (() -> Unit)): super(data) {
        mOnPreload = onPreload
    }

    constructor(layoutResId: Int, data: List<T>?, onPreload: (() -> Unit)): super(layoutResId, data) {
        mOnPreload = onPreload
    }

    override fun convert(helper: K, item: T) {
        if (mOnPreload != null
            && !isPreloading
            && scrollState != RecyclerView.SCROLL_STATE_IDLE
            && helper.adapterPosition >= max(itemCount - 1 - preloadItemCount, 0)) {
            isPreloading = true
            mOnPreload?.invoke()
        }
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                scrollState = newState
            }
        })
    }
}