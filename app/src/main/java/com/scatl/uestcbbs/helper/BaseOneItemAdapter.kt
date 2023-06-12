package com.scatl.uestcbbs.helper

import android.content.Context
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import com.chad.library.adapter.base.BaseSingleItemAdapter

/**
 * Created by sca_tl at 2023/6/12 16:04
 */
abstract class BaseOneItemAdapter<T, VB: ViewBinding>(): BaseSingleItemAdapter<T, ViewBindingHolder<VB>>() {

    override fun onBindViewHolder(holder: ViewBindingHolder<VB>, item: T?) {

    }

    override fun onCreateViewHolder(context: Context, parent: ViewGroup, viewType: Int): ViewBindingHolder<VB> {
        return ViewBindingHolder(getViewBinding(parent))
    }

    protected abstract fun getViewBinding(parent: ViewGroup): VB
}