package com.scatl.uestcbbs.module.board.view.behavior

import android.animation.ValueAnimator
import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.OverScroller
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.ViewCompat
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.RecyclerView
import com.gyf.immersionbar.ImmersionBar
import com.scatl.uestcbbs.R
import com.scatl.util.ScreenUtil
import com.scwang.smart.refresh.layout.SmartRefreshLayout
import com.scwang.smart.refresh.layout.util.SmartUtil

/**
 * Created by sca_tl at 2023/4/27 11:03
 */
class ContentBehavior: CoordinatorLayout.Behavior<View> {

    var contentInitTranY = 0f
    var contentMaxTransY = 0f
    var topBarHeight = 0f
    private lateinit var restoreAnimator: ValueAnimator //收起内容时执行的动画
    private lateinit var contentLayout: View
    private var flingFromCollaps = false //fling是否从折叠状态发生的

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context)
    }

    private fun init(context: Context) {
        contentInitTranY = ScreenUtil.dip2pxF(context, 250f)
        contentMaxTransY = ScreenUtil.dip2pxF(context, 300f)
        topBarHeight = ScreenUtil.dip2pxF(context, 56f) + ImmersionBar.getStatusBarHeight(context)

        restoreAnimator = ValueAnimator().apply {
            addUpdateListener { animation ->
                contentLayout.translationY = animation.animatedValue as Float
            }
        }
    }

    override fun onMeasureChild(parent: CoordinatorLayout,
                                child: View,
                                parentWidthMeasureSpec: Int,
                                widthUsed: Int,
                                parentHeightMeasureSpec: Int,
                                heightUsed: Int): Boolean {
        val childLpHeight = child.layoutParams.height
        if (childLpHeight == ViewGroup.LayoutParams.MATCH_PARENT || childLpHeight == ViewGroup.LayoutParams.WRAP_CONTENT) {
            //先获取CoordinatorLayout的测量规格信息，若不指定具体高度则使用CoordinatorLayout的高度
            var availableHeight = View.MeasureSpec.getSize(parentHeightMeasureSpec)
            if (availableHeight == 0) {
                availableHeight = parent.height
            }
            //设置Content部分高度
            val height = (availableHeight - topBarHeight).toInt()
            val heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(height,
                if (childLpHeight == ViewGroup.LayoutParams.MATCH_PARENT) View.MeasureSpec.EXACTLY else View.MeasureSpec.AT_MOST
            )
            //执行指定高度的测量，并返回true表示使用Behavior来代理测量子View
            parent.onMeasureChild(child, parentWidthMeasureSpec, widthUsed, heightMeasureSpec, heightUsed)
            return true
        }
        return false
    }

    override fun onLayoutChild(parent: CoordinatorLayout, child: View, layoutDirection: Int): Boolean {
        val handleLayout = super.onLayoutChild(parent, child, layoutDirection)
        //绑定Content View
        contentLayout = child
        return handleLayout
    }

    override fun onStartNestedScroll(coordinatorLayout: CoordinatorLayout, child: View, directTargetChild: View, target: View, axes: Int): Boolean {
        return onStartNestedScroll(coordinatorLayout, child, directTargetChild, target, axes, ViewCompat.TYPE_TOUCH)
    }

    override fun onNestedScrollAccepted(coordinatorLayout: CoordinatorLayout, child: View, directTargetChild: View, target: View, axes: Int) {
        onNestedScrollAccepted(coordinatorLayout, child, directTargetChild, target, axes, ViewCompat.TYPE_TOUCH)
    }

    override fun onNestedPreScroll(coordinatorLayout: CoordinatorLayout, child: View, target: View, dx: Int, dy: Int, consumed: IntArray) {
        onNestedPreScroll(coordinatorLayout, child, target, dx, dy, consumed, ViewCompat.TYPE_TOUCH)
    }

    override fun onStopNestedScroll(coordinatorLayout: CoordinatorLayout, child: View, target: View) {
        onStopNestedScroll(coordinatorLayout, child, target, ViewCompat.TYPE_TOUCH)
    }

    override fun onNestedPreFling(coordinatorLayout: CoordinatorLayout, child: View, target: View, velocityX: Float, velocityY: Float): Boolean {
        flingFromCollaps = child.translationY <= contentInitTranY
        return false
    }

    override fun onStartNestedScroll(coordinatorLayout: CoordinatorLayout, child: View, directTargetChild: View, target: View, axes: Int, type: Int): Boolean {
        //只接受内容View的垂直滑动
        return (directTargetChild.id == R.id.content_layout && axes == ViewCompat.SCROLL_AXIS_VERTICAL)
    }

    override fun onNestedScrollAccepted(coordinatorLayout: CoordinatorLayout, child: View, directTargetChild: View, target: View, axes: Int, type: Int) {
        if (restoreAnimator.isStarted) {
            restoreAnimator.cancel()
        }
    }

    override fun onStopNestedScroll(coordinatorLayout: CoordinatorLayout, child: View, target: View, type: Int) {
        //如果是从初始状态转换到展开状态过程触发收起动画
        if (child.translationY > contentInitTranY) {
            restore()
        }
    }

    override fun onNestedPreScroll(coordinatorLayout: CoordinatorLayout, child: View, target: View, dx: Int, dy: Int,  consumed: IntArray, type: Int) {
        val transY = child.translationY - dy

        //处理上滑
        if (dy > 0) {
            if (transY >= topBarHeight) {
                translationByConsume(child, transY, consumed, dy.toFloat())
            } else {
                translationByConsume(child, topBarHeight, consumed, child.translationY - topBarHeight)
            }
        }

        var canScrollVerticallyP = false
        if (target is SmartRefreshLayout) {
            for (i in 0 until  target.childCount) {
                if (SmartUtil.isScrollableView(target.getChildAt(i))) {
                    canScrollVerticallyP = target.getChildAt(i).canScrollVertically(-1)
                    break
                }
            }
        } else {
            canScrollVerticallyP = target.canScrollVertically(-1)
        }

        if (dy < 0 && !canScrollVerticallyP) {
            //下滑时处理Fling,折叠时下滑Recycler(或NestedScrollView) Fling滚动到contentTransY停止Fling
            if (type == ViewCompat.TYPE_NON_TOUCH && transY >= contentInitTranY && flingFromCollaps) {
                flingFromCollaps = false
                translationByConsume(child, contentInitTranY, consumed, dy.toFloat())
                stopViewScroll(target)
                return
            }

            //处理下滑
            if (transY in topBarHeight .. contentMaxTransY) {
                translationByConsume(child, transY, consumed, dy.toFloat())
            } else {
                translationByConsume(child, contentMaxTransY, consumed, contentMaxTransY - child.translationY)
                stopViewScroll(target)
            }
        }
    }

    private fun stopViewScroll(target: View) {
        if (target is RecyclerView) {
            target.stopScroll()
        }
        if (target is NestedScrollView) {
            try {
                val clazz = target::class.java
                val mScroller = clazz.getDeclaredField("mScroller")
                mScroller.isAccessible = true
                val overScroller = mScroller[target] as OverScroller
                overScroller.abortAnimation()
            } catch (e: NoSuchFieldException) {
                e.printStackTrace()
            } catch (e: IllegalAccessException) {
                e.printStackTrace()
            }
        }
    }

    private fun translationByConsume(view: View, translationY: Float, consumed: IntArray, consumedDy: Float) {
        consumed[1] = consumedDy.toInt()
        view.translationY = translationY
    }

    private fun restore() {
        if (restoreAnimator.isStarted) {
            restoreAnimator.cancel()
            restoreAnimator.removeAllListeners()
        }
        restoreAnimator.setFloatValues(contentLayout.translationY, contentInitTranY)
        restoreAnimator.duration = 200
        restoreAnimator.start()
    }

    override fun onDetachedFromLayoutParams() {
        if (restoreAnimator.isStarted) {
            restoreAnimator.cancel()
            restoreAnimator.removeAllUpdateListeners()
            restoreAnimator.removeAllListeners()
        }
        super.onDetachedFromLayoutParams()
    }
}