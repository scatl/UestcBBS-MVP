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
 * Created by sca_tl at 2023/5/4 13:53
 * 该部分紧贴着content
 */
class TitleBehavior: CoordinatorLayout.Behavior<View> {

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

    override fun onDependentViewChanged(parent: CoordinatorLayout, child: View, dependency: View): Boolean {
        adjustPosition(parent, child, dependency)
        val start = (contentInitTranY + topBarHeight) / 2f
        val upPro = (contentInitTranY - MathUtils.clamp(dependency.translationY, start, contentInitTranY)) / (contentInitTranY - start)
        child.alpha = 1 - upPro
        return true
    }

    override fun onLayoutChild(parent: CoordinatorLayout, child: View, layoutDirection: Int): Boolean {
        val dependencies = parent.getDependencies(child)
        var dependency: View? = null
        for (view in dependencies) {
            if (view.id == R.id.content_layout) {
                dependency = view
                break
            }
        }

        return if (dependency != null) {
            adjustPosition(parent, child, dependency)
            true
        } else {
            false
        }
    }

    /**
     * 调整位置，紧贴Content顶部上面
     */
    private fun adjustPosition(parent: CoordinatorLayout, child: View, dependency: View) {
        val lp = child.layoutParams as CoordinatorLayout.LayoutParams
        val left = parent.paddingLeft + lp.leftMargin
        val top = (dependency.y - child.measuredHeight + lp.topMargin).toInt()
        val right = child.measuredWidth + left - parent.paddingRight - lp.rightMargin
        val bottom = (dependency.y - lp.bottomMargin).toInt()
        child.layout(left, top, right, bottom)
    }

}