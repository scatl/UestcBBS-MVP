package com.scatl.widget.floatview

import android.content.Context
import android.content.res.ColorStateList
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import com.scatl.util.ColorUtil

/**
 * created by sca_tl at 2023/5/14 12:19
 */
class CustomFloatingView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : FloatingMagnetView(context, attrs) {

    private var mInflate: View? = null

    constructor(context: Context, resource: Int, layoutParam: ViewGroup.LayoutParams?) : this(context, null) {
        mInflate = inflate(context, resource, this)
        layoutParams = layoutParam ?: getParams()
    }

    private fun getParams(): LayoutParams {
        val params = LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT)
        params.gravity = Gravity.BOTTOM or Gravity.START
        params.setMargins(100, params.topMargin, params.rightMargin, 100)
        return params
    }
}