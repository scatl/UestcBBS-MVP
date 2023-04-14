package com.scatl.widget.ninelayout

import android.view.View

/**
 * created by sca_tl at 2022/6/5 10:53
 */
abstract class NineGridAdapter {
    abstract fun getItemView(parent: NineGridLayout, position: Int): View
    abstract fun bindView(parent: NineGridLayout, view: View, position: Int)
    abstract fun getItemCount(): Int
    open fun onItemClick(view: View, position: Int) {}
}