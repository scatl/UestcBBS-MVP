package com.scatl.uestcbbs.widget.textview

import android.content.Context
import android.text.Html
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ImageSpan
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import com.scatl.widget.sapn.CenterImageSpan
import com.scatl.widget.sapn.EmojiImageGetter
import java.util.regex.Pattern

/**
 * Created by sca_tl on 2022/12/29 19:54
 */
class EmojiTextView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null) : AppCompatTextView(context, attrs) {

    fun setText(text: String?) {
        var realText = text?: ""
        val emotionMatcher = Pattern.compile("(\\[mobcent_phiz=(.*?)])").matcher(text.toString())
        if (emotionMatcher.find()) {
            do {
                val whole = emotionMatcher.group(0)
                val url = emotionMatcher.group(2)
                if (whole != null && url != null) {
                    realText = realText.replaceFirst(whole, "<img src = $url >")
                }
            } while (emotionMatcher.find())
        }

        val spanned = Html.fromHtml(realText, EmojiImageGetter(context, this), null)

        if (spanned is SpannableStringBuilder) {
            val imageSpans = spanned.getSpans(0, realText.length, ImageSpan::class.java)
            imageSpans?.forEach {
                try {
                    spanned.setSpan(
                        CenterImageSpan(it.drawable),
                        spanned.getSpanStart(it),
                        spanned.getSpanEnd(it),
                        Spannable.SPAN_INCLUSIVE_INCLUSIVE
                    )
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }

        super.setText(spanned)
    }
}