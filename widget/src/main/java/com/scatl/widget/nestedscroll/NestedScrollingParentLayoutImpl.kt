package com.scatl.widget.nestedscroll

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.RecyclerView

/**
 * Created by sca_tl at 2023/6/7 15:19
 * 处理 Recyclerview 嵌套 tab + viewpager
 */
class NestedScrollingParentLayoutImpl @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : NestedScrollingParentLayout(context, attrs, defStyleAttr) {

    var mLastItemView: View? = null
    var mChildRecyclerView: RecyclerView? = null
    private var mParentRecyclerView: RecyclerView? = null

    init {
        orientation = VERTICAL
    }

    override fun onStartNestedScroll(child: View, target: View, axes: Int, type: Int): Boolean {
        return nestedScrollAxes == ViewCompat.SCROLL_AXIS_VERTICAL
    }

    override fun onNestedPreScroll(target: View?, dx: Int, dy: Int, consumed: IntArray) {
        if (mLastItemView == null || mChildRecyclerView == null || mParentRecyclerView == null) {
            return
        }

        if (target == mParentRecyclerView) {
            handleParentRecyclerViewScroll(mLastItemView!!.top, dy, consumed)
        }
    }

    private fun handleParentRecyclerViewScroll(lastItemTop: Int, dy: Int, consumed: IntArray) {
        if (lastItemTop != 0) {
            if (dy > 0) {
                if (lastItemTop > dy) {
                    //tab的top>想要滑动的dy,就让外部RecyclerView自行处理
                } else {
                    //tab的top<=想要滑动的dy,先滑外部RecyclerView，滑距离为lastItemTop，刚好到顶；剩下的就滑内层了。
                    consumed[1] = dy
                    mParentRecyclerView!!.scrollBy(0, lastItemTop)
                    mChildRecyclerView!!.scrollBy(0, dy - lastItemTop)
                }
            } else {
                if (mChildRecyclerView!!.canScrollVertically(-1)) {
                    consumed[1] = dy
                    mChildRecyclerView!!.scrollBy(0, dy)
                }
            }
        } else {
            //tab上边到顶了
            if (dy > 0) {
                //向上，内层直接消费掉
                mChildRecyclerView!!.scrollBy(0, dy)
                consumed[1] = dy
            } else {
                val childScrolledY = mChildRecyclerView!!.computeVerticalScrollOffset()
                if (childScrolledY > Math.abs(dy)) {
                    //内层已滚动的距离，大于想要滚动的距离，内层直接消费掉
                    mChildRecyclerView!!.scrollBy(0, dy)
                    consumed[1] = dy
                } else {
                    //内层已滚动的距离，小于想要滚动的距离，那么内层消费一部分，到顶后，剩的还给外层自行滑动
                    mChildRecyclerView!!.scrollBy(0, childScrolledY)
                    consumed[1] = -childScrolledY
                }
            }
        }
        if (!mParentRecyclerView!!.canScrollVertically(-1) && !mChildRecyclerView!!.canScrollVertically(-1)
            || !mChildRecyclerView!!.canScrollVertically(1) && !mParentRecyclerView!!.canScrollVertically(1)) {
            ViewCompat.stopNestedScroll(mParentRecyclerView!!, ViewCompat.TYPE_NON_TOUCH)
        }
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        mParentRecyclerView = getParentRecyclerView()
    }

    private fun getParentRecyclerView(): RecyclerView? {
        for (i in 0 until childCount) {
            val child = getChildAt(i)
            if (child is RecyclerView) {
                if (mParentRecyclerView == null) {
                    return child
                }
            }
        }
        return null
    }
}