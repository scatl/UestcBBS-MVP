package com.scatl.uestcbbs.helper

import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding

/**
 * Created by sca_tl at 2023/6/12 10:17
 */
class ViewBindingHolder<VB : ViewBinding>(val binding: VB) : RecyclerView.ViewHolder(binding.root) {

}