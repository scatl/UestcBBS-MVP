package com.scatl.uestcbbs.module.board.view.behavior

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.math.MathUtils
import com.gyf.immersionbar.ImmersionBar
import com.scatl.uestcbbs.R
import com.scatl.util.ScreenUtil

/**
 * Created by sca_tl at 2023/4/27 10:39
 */
class CoverBehavior: CoordinatorLayout.Behavior<View> {

    var contentInitTranY = 0f
    var contentMaxTransY = 0f
    var coverTransY = 0f
    var topBarHeight = 0f

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context)
    }

    private fun init(context: Context) {
        contentInitTranY = ScreenUtil.dip2pxF(context, 250f)
        contentMaxTransY = ScreenUtil.dip2pxF(context, 300f)
        coverTransY = ScreenUtil.dip2pxF(context, -20f)
        topBarHeight = ScreenUtil.dip2pxF(context, 56f) + ImmersionBar.getStatusBarHeight(context)
    }

    override fun layoutDependsOn(parent: CoordinatorLayout, child: View, dependency: View): Boolean {
        return dependency.id == R.id.content_layout
    }

    override fun onDependentViewChanged(parent: CoordinatorLayout, child: View, dependency: View): Boolean {
        val upPro: Float = (contentInitTranY - MathUtils.clamp(dependency.translationY, topBarHeight, contentInitTranY)) / (contentInitTranY - topBarHeight)
        val downPro: Float = (contentMaxTransY - MathUtils.clamp(dependency.translationY, contentInitTranY, contentMaxTransY)) / (contentMaxTransY - contentInitTranY)

        if (dependency.translationY >= contentInitTranY) {
            child.translationY = downPro * coverTransY
        } else {
            child.translationY = coverTransY + 4 * upPro * coverTransY
        }

        (parent.context as? OnCoverViewChanged)?.onCoverChanged(upPro, downPro)

        return true
    }

    fun interface OnCoverViewChanged {
        fun onCoverChanged(upPercent: Float, downPercent: Float)
    }
}