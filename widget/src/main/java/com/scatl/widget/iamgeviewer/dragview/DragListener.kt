package com.scatl.widget.iamgeviewer.dragview

import android.view.View

/**
 * Created by sca_tl at 2023/5/17 10:29
 */
interface DragListener {
    fun onDragging(view: View, fraction: Float) { }
    fun onRestoring(view: View, fraction: Float) { }
    fun onRelease(view: View) { }
    fun onSetViewPagerInputEnable(value: Boolean) { }
}