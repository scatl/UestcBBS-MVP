package com.scatl.widget.textview

import android.content.Context
import android.text.Spannable
import android.text.style.ClickableSpan
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.appcompat.widget.AppCompatTextView

/**
 * Created by sca_tl on 2022/12/30 9:17
 * 长按设置了ClickableSpan的textview时，屏蔽点击事件
 */
class LongClickTextView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) : AppCompatTextView(context, attrs) {

    private var mTime: Long = 0

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if(text == null) {
            return super.onTouchEvent(event)
        }

        if (text is Spannable) {
            val clickableSpans = (text as Spannable).getSpans(0, text.length, ClickableSpan::class.java)
            if (clickableSpans.isNullOrEmpty()) {
                return super.onTouchEvent(event)
            }
            if (event?.action == MotionEvent.ACTION_DOWN) {
                mTime = System.currentTimeMillis()
            } else if (event?.action == MotionEvent.ACTION_UP) {
                if (System.currentTimeMillis() - mTime > 500) {
                    return true
                }
            }
        }
        return super.onTouchEvent(event)
    }
}