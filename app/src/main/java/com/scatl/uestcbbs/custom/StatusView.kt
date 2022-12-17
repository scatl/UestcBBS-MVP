package com.scatl.uestcbbs.custom

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import androidx.core.view.children
import com.airbnb.lottie.LottieAnimationView
import com.scatl.uestcbbs.R

class StatusView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0) : FrameLayout(context, attrs, defStyleAttr) {

    private var mRootView: View
    private var mAnim: LottieAnimationView
    private var mText: TextView

    init {
        mRootView = LayoutInflater.from(getContext()).inflate(R.layout.layout_status_view, this)
        mAnim = mRootView.findViewById(R.id.anim)
        mText = mRootView.findViewById(R.id.text)
    }

    fun loading() {
        if (parent is ViewGroup) {
            (parent as ViewGroup).children.forEach {
                if (it != this) {
                    it.visibility = GONE
                }
            }
        }
        mRootView.visibility = VISIBLE
        mAnim.visibility = VISIBLE
        mAnim.playAnimation()
        mText.visibility = GONE
    }

    fun error(msg: String?) {
        if (parent is ViewGroup) {
            (parent as ViewGroup).children.forEach {
                if (it != this) {
                    it.visibility = GONE
                }
            }
        }
        mRootView.visibility = VISIBLE
        mAnim.visibility = GONE
        mText.visibility = VISIBLE
        mText.text = msg?:"啊哦，出错了，稍后再试吧~"
    }

    fun success() {
        if (parent is ViewGroup) {
            (parent as ViewGroup).children.forEach {
                it.visibility = if (it == this) GONE else VISIBLE
            }
        }
    }

}