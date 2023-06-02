package com.scatl.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import com.airbnb.lottie.LottieAnimationView

class StatusView @JvmOverloads constructor(context: Context,
                                           attrs: AttributeSet? = null,
                                           defStyleAttr: Int = 0)
    : FrameLayout(context, attrs, defStyleAttr) {

    private var mRootView: View
    private var mAnim: LottieAnimationView
    private var mText: TextView
    private var mGoneViews: Array<out View>? = null

    init {
        mRootView = LayoutInflater.from(getContext()).inflate(R.layout.layout_status_view, this)
        mAnim = mRootView.findViewById(R.id.anim)
        mText = mRootView.findViewById(R.id.text)
    }

    fun loading(vararg readyGoneViews: View) {
        mGoneViews = readyGoneViews
        mGoneViews?.forEach {
            it.visibility = GONE
        }
        mRootView.visibility = VISIBLE
        mAnim.visibility = VISIBLE
        mAnim.playAnimation()
        mText.visibility = GONE
    }

    fun error(msg: String? = null) {
        mGoneViews?.forEach {
            it.visibility = GONE
        }
        mRootView.visibility = VISIBLE
        mAnim.visibility = GONE
        mText.visibility = VISIBLE
        mText.text = msg?:"啊哦，出错了，稍后再试吧~"
    }

    fun empty(msg: String? = null) {
        mGoneViews?.forEach {
            it.visibility = GONE
        }
        mRootView.visibility = VISIBLE
        mAnim.visibility = GONE
        mText.visibility = VISIBLE
        mText.text = msg?:"啊哦，这里空空的~"
    }

    fun success() {
        mGoneViews?.forEach {
            it.visibility = VISIBLE
        }
        mRootView.visibility = GONE
    }

}