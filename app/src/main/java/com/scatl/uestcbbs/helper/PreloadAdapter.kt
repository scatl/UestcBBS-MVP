package com.scatl.uestcbbs.helper

import android.content.Context
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.chad.library.adapter.base.BaseQuickAdapter
import kotlin.math.max

/**
 * Created by sca_tl at 2023/2/17 14:51
 */
abstract class PreloadAdapter<T, VB: ViewBinding>() : BaseQuickAdapter<T, PreloadAdapter.ViewHolder>() {

    private var mOnPreload: (() -> Unit)? = null
    private var preloadItemCount = 5
    private var scrollState = RecyclerView.SCROLL_STATE_IDLE
    var isPreloading = false

    constructor(onPreload: (() -> Unit)?): this() {
        mOnPreload = onPreload
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int, item: T?) {
        if (mOnPreload != null
            && !isPreloading
            && scrollState != RecyclerView.SCROLL_STATE_IDLE
            && holder.adapterPosition >= max(itemCount - 1 - preloadItemCount, 0)) {
            isPreloading = true
            mOnPreload?.invoke()
        }
    }

    override fun onCreateViewHolder(context: Context, parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(getViewBinding(parent))
    }

    protected abstract fun getViewBinding(parent: ViewGroup): VB

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                scrollState = newState
            }
        })
    }

    class ViewHolder(val binding: ViewBinding): RecyclerView.ViewHolder(binding.root) {

    }
}