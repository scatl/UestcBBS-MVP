package com.scatl.uestcbbs.custom

import android.content.Context
import android.util.AttributeSet
import androidx.recyclerview.widget.RecyclerView
import com.scwang.smartrefresh.layout.SmartRefreshLayout

/**
 * Created by sca_tl on 2022/8/15 10:24
 */
class FixRefreshLayout : SmartRefreshLayout {

    constructor(context: Context): super(context)

    constructor(context: Context, attributes: AttributeSet): super(context, attributes)

    override fun onFinishInflate() {
        super.onFinishInflate()
        for (i in 0 until childCount) {
            val child = getChildAt(i)
            if (child is RecyclerView) {
                child.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                        if (child.canScrollVertically(-1)) {
                            setEnableRefresh(false)
                        } else {
                            setEnableRefresh(true)
                        }
                    }
                })
                break
            }
        }
    }
}