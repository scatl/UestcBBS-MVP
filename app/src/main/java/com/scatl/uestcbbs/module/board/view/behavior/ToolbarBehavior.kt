package com.scatl.uestcbbs.module.board.view.behavior

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.math.MathUtils
import com.gyf.immersionbar.ImmersionBar
import com.scatl.uestcbbs.R
import com.scatl.util.ScreenUtil

/**
 * Created by sca_tl at 2023/4/27 13:35
 */
class ToolbarBehavior: CoordinatorLayout.Behavior<View> {

    var contentInitTranY = 0f
    var topBarHeight = 0f

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context)
    }

    private fun init(context: Context) {
        contentInitTranY = ScreenUtil.dip2pxF(context, 250f)
        topBarHeight = ScreenUtil.dip2pxF(context, 56f) + ImmersionBar.getStatusBarHeight(context)
    }

    override fun layoutDependsOn(parent: CoordinatorLayout, child: View, dependency: View): Boolean {
        return dependency.id == R.id.content_layout
    }

    override fun onLayoutChild(parent: CoordinatorLayout, child: View, layoutDirection: Int): Boolean {
        child.layoutParams = (child.layoutParams as CoordinatorLayout.LayoutParams).apply {
            topMargin = ImmersionBar.getStatusBarHeight(parent.context)
        }
        return super.onLayoutChild(parent, child, layoutDirection)
    }

    override fun onDependentViewChanged(parent: CoordinatorLayout, child: View, dependency: View): Boolean {
        val upPro: Float = (contentInitTranY - MathUtils.clamp(dependency.translationY, topBarHeight, contentInitTranY)) / (contentInitTranY - topBarHeight)
        child.alpha = upPro
        return true
    }
}