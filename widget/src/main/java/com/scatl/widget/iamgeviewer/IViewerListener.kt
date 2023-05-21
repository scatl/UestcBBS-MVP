package com.scatl.widget.iamgeviewer

/**
 * Created by sca_tl at 2023/5/16 10:56
 */
interface IViewerListener {
    fun onEnter(animation: Boolean)
    fun onExit(animation: Boolean)
    fun onLoadFailed()
    fun onLoadSuccess()
    fun onFinish()
    fun onDragging(fraction: Float)
    fun onDragRestoring(fraction: Float)
    fun onSetViewPagerInputEnable(value: Boolean) { }
}