package com.scatl.widget.floatview

import android.view.MotionEvent

/**
 * created by sca_tl at 2023/5/14 12:11
 */
interface FloatViewListener {
    fun onRemove(magnetView: FloatingMagnetView?) {}
    fun onClick(event: MotionEvent?) {}
}