package com.scatl.uestcbbs.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.Paint
import android.util.AttributeSet
import android.widget.FrameLayout
import com.scatl.uestcbbs.util.SharePrefUtil

/**
 * created by sca_tl at 2022/12/30 20:26
 */
class GrayFrameLayout @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) : FrameLayout(context, attrs) {

    private val mPaint = Paint()

    constructor(saturation: Float, context: Context, attrs: AttributeSet? = null): this(context, attrs) {
        val cm = ColorMatrix()
        cm.setSaturation(saturation)
        mPaint.colorFilter = ColorMatrixColorFilter(cm)
    }

    override fun dispatchDraw(canvas: Canvas) {
        canvas.saveLayer(null, mPaint, Canvas.ALL_SAVE_FLAG)
        super.dispatchDraw(canvas)
        canvas.restore()
    }

    override fun draw(canvas: Canvas) {
        canvas.saveLayer(null, mPaint, Canvas.ALL_SAVE_FLAG)
        super.draw(canvas)
        canvas.restore()
    }

}