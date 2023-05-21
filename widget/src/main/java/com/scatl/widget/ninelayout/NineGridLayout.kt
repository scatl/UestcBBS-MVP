package com.scatl.widget.ninelayout

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import com.scatl.widget.R
import kotlin.math.roundToInt

/**
 * created by sca_tl at 2022/6/5 11:22
 */
class NineGridLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ViewGroup(context, attrs, defStyleAttr) {

    private val TAG = "NineGridLayout"

    private var mWidth = 0
    private var mHeight = 0

    //间距大小
    private val mGridSpace = 10

    //适配器
    private var mNineGridAdapter: NineGridAdapter? = null

    private var mCenterOneChild = false

    init {
        if (attrs != null) {
            val typedArray = context.obtainStyledAttributes(attrs, R.styleable.NineGridLayout)
            mCenterOneChild = typedArray.getBoolean(R.styleable.NineGridLayout_centerOneChild, false)
            typedArray.recycle()
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        mWidth = MeasureSpec.getSize(widthMeasureSpec)
        measureChildrenSize()
        measureChildren(widthMeasureSpec, heightMeasureSpec)
        mHeight = calHeight()
        setMeasuredDimension(mWidth, mHeight)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        when (childCount) {
            1 -> layout1Children()
            2 -> layout2Children()
            3 -> layout3Children()
            4 -> layout4Children()
            5 -> layout5Children()
            in 6..9 -> layout6PlusChildren()
        }
    }

    /**
     * 设置子View的大小
     */
    private fun measureChildrenSize() {
        when (childCount) {
            1 -> measure1Children()
            2 -> measure2Children()
            3 -> measure3Children()
            4 -> measure4Children()
            5 -> measure5Children()
            else -> measure6PlusChildren()
        }
    }

    /**
     * 测量一个child
     */
    private fun measure1Children() {
        val child = getChildAt(0)
        child.layoutParams = child.layoutParams.apply {
            width = LayoutParams.WRAP_CONTENT
            height = LayoutParams.WRAP_CONTENT
        }
    }

    /**
     * 测量2个child
     */
    private fun measure2Children() {
        for (i in 0 until childCount) {
            val child = getChildAt(i)
            child.layoutParams = child.layoutParams.apply {
                width = (mWidth - mGridSpace) / 2
                height = width
            }
        }
    }

    /**
     * 测量3个child
     */
    private fun measure3Children() {
        for (i in 1 until childCount) {
            val child = getChildAt(i)
            child.layoutParams = child.layoutParams.apply {
                width = (mWidth - mGridSpace) / 2
                height = (width / 1.5).roundToInt()
            }
        }

        val child1 = getChildAt(0)
        child1.layoutParams = child1.layoutParams.apply {
            width = (mWidth - mGridSpace) / 2
            height = getChildAt(1).layoutParams.height * 2 + mGridSpace
        }
    }

    /**
     * 测量4个child
     */
    private fun measure4Children() {
        for (i in 0 until childCount) {
            val child = getChildAt(i)
            child.layoutParams = child.layoutParams.apply {
                width = (mWidth - mGridSpace) / 2
                height = (width / 1.5).roundToInt()
            }
        }
    }

    /**
     * 测量5个child
     */
    private fun measure5Children() {
        val singleWidth = (mWidth - 2 * mGridSpace) / 3

        for (i in 0..1) {
            val child = getChildAt(i)
            child.layoutParams = child.layoutParams.apply {
                width = singleWidth * (i + 1) + mGridSpace * i
                height = (singleWidth * 1.5).roundToInt()
            }
        }

        for (i in 2 until childCount) {
            val child = getChildAt(i)
            child.layoutParams = child.layoutParams.apply {
                width = singleWidth
                height = width
            }
        }
    }

    /**
     * 测量6、7、8、9个child
     */
    private fun measure6PlusChildren() {
        val count = if (childCount > 9) 9 else childCount
        for (i in 0 until count) {
            val child = getChildAt(i)
            child.layoutParams = child.layoutParams.apply {
                width = (mWidth - 2 * mGridSpace) / 3
                height = width
            }
        }
    }

    /**
     * 计算整个布局高度
     */
    private fun calHeight(): Int {
        return when (childCount) {
            0   -> 0
            1   -> getChildAt(0).measuredHeight
            2,3 -> getChildAt(0).layoutParams.height
            4   -> getChildAt(0).layoutParams.height * 2 + mGridSpace
            5   -> getChildAt(0).layoutParams.height + getChildAt(2).layoutParams.height + mGridSpace
            6   -> getChildAt(0).layoutParams.height * 2 + mGridSpace
            else -> getChildAt(0).layoutParams.height * 3 + mGridSpace * 2
        }
    }

    private fun layout1Children() {
        if (mCenterOneChild) {
            getChildAt(0).layout(
                (mWidth - getChildAt(0).measuredWidth) / 2,
                0,
                getChildAt(0).measuredWidth + (mWidth - getChildAt(0).measuredWidth) / 2,
                getChildAt(0).measuredHeight
            )
        } else {
            getChildAt(0).layout(0, 0, getChildAt(0).measuredWidth, getChildAt(0).measuredHeight)
        }
    }

    private fun layout2Children() {
        val singleWidth = (mWidth - mGridSpace) / 2
        for (i in 0 until childCount) {
            val l: Int = i * (singleWidth + mGridSpace)
            val t = 0
            val r: Int = l + singleWidth
            val b: Int = t + singleWidth
            getChildAt(i).layout(l, t, r, b)
        }
    }

    private fun layout3Children() {
        val singleWidth = (mWidth - mGridSpace) / 2

        getChildAt(0).layout(0, 0, singleWidth, getChildAt(0).layoutParams.height)
        getChildAt(1).layout(singleWidth + mGridSpace, 0, mWidth, ((getChildAt(0).layoutParams.height - mGridSpace) / 2))
        getChildAt(2).layout(singleWidth + mGridSpace, mGridSpace + ((getChildAt(0).layoutParams.height - mGridSpace) / 2), mWidth, getChildAt(0).layoutParams.height)
    }

    private fun layout4Children() {
        val singleWidth = (mWidth - mGridSpace) / 2
        val singleHeight = getChildAt(0).layoutParams.height
        for (i in 0 until childCount) {
            val l = if (i == 0 || i == 1) {
                i * (singleWidth + mGridSpace)
            } else {
                //换行
                (i - 2) * (singleWidth + mGridSpace)
            }
            val t = if (i == 0 || i == 1) {
                0
            } else {
                singleHeight + mGridSpace
            }
            val r: Int = l + singleWidth
            val b: Int = t + singleHeight
            getChildAt(i).layout(l, t, r, b)
        }
    }

    private fun layout5Children() {
        val singleWidth = (mWidth - 2 * mGridSpace) / 3
        getChildAt(0).apply { layout(0, 0, singleWidth, layoutParams.height) }
        getChildAt(1).apply { layout(singleWidth + mGridSpace, 0, mWidth, layoutParams.height) }

        for (i in 2 until childCount) {
            val l = (singleWidth + mGridSpace) * (i - 2)
            val t = getChildAt(0).layoutParams.height + mGridSpace
            val r = singleWidth * (i - 1) + mGridSpace * (i - 2)
            val b = mHeight
            getChildAt(i).layout(l, t, r, b)
        }
    }

    private fun layout6PlusChildren() {
        val singleWidth = (mWidth - 2 * mGridSpace) / 3

        val count = if (childCount > 9) 9 else childCount

        for (i in 0 until count) {
            val l = when (i) {
                0, 1, 2 -> { i * (singleWidth + mGridSpace) }
                3, 4, 5 -> { (i - 3) * (singleWidth + mGridSpace) }
                else -> { (i - 6) * (singleWidth + mGridSpace) }
            }

            val t = when (i) {
                0, 1, 2 -> { 0 }
                3, 4, 5 -> { singleWidth + mGridSpace }
                else -> { singleWidth* 2 + mGridSpace * 2 }
            }

            val r = when (i) {
                0, 1, 2 -> { mWidth - (2 - i) * (singleWidth + mGridSpace) }
                3, 4, 5 -> { mWidth - (5 - i) * (singleWidth + mGridSpace) }
                else -> { mWidth - (8 - i) * (singleWidth + mGridSpace) }
            }

            val b = when (i) {
                0, 1, 2 -> { singleWidth }
                3, 4, 5 -> { singleWidth * 2 + mGridSpace }
                else -> { singleWidth * 3 + mGridSpace * 2 }
            }

            getChildAt(i).layout(l, t, r, b)
        }
    }

    fun setNineGridAdapter(nineGridAdapter: NineGridAdapter) {
        mNineGridAdapter = nineGridAdapter
        removeAllViews()
        val childCount = childCount
        val count = Math.min(mNineGridAdapter!!.getItemCount(), 9)
        if (childCount > count) {
            removeViews(count, childCount - count)
        } else if (childCount < count) {
            for (i in childCount until count) {
                val view: View = getItemView(i)
                addViewInLayout(view, i, view.layoutParams)
            }
        }
        for (i in 0 until if (count > 9) 9 else count) {
            mNineGridAdapter!!.bindView(this, getChildAt(i), i)
        }
    }

    /**
     * @param position 位置
     */
    private fun getItemView(position: Int): View {
        if (mNineGridAdapter == null) {
            throw Exception("no NineGridAdapter")
        }
        val view = mNineGridAdapter!!.getItemView(this, position)
        view.setOnClickListener { v -> mNineGridAdapter!!.onItemClick(v, position) }
        return view
    }

}