package com.scatl.uestcbbs.widget.textview

import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.Rect
import android.text.Spannable
import android.text.SpannableString
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText
import com.scatl.uestcbbs.manager.EmotionManager
import com.scatl.widget.sapn.CenterImageSpan
import com.scatl.util.ImageUtil
import java.util.regex.Pattern

/**
 * Created by sca_tl at 2023/5/11 14:09
 */
class EmojiEditText @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : AppCompatEditText(context, attrs) {

    fun setText(text: String?) {
        val spannableString = SpannableString(text)
        val emotionMatcher = Pattern.compile("(\\[([as]):\\d*])").matcher(text.toString())
        if (emotionMatcher.find()) {
            do {
                val name = emotionMatcher.group(0)
                if (name != null) {
                    val realName = name.replace(":", "_")
                    val emotionItem = EmotionManager.INSTANCE.getEmotionByName(realName)

                    val start = emotionMatcher.start()
                    val end = emotionMatcher.end()

                    if (emotionItem != null) {
                        context.resources.assets.open(emotionItem.rPath).use {
                            val bitmap = BitmapFactory.decodeStream(it)
                            val drawable = ImageUtil.bitmap2Drawable(bitmap)
                            val radio = drawable.intrinsicWidth.toFloat() / drawable.intrinsicHeight.toFloat()
                            drawable.bounds = Rect(0, 0, (textSize * radio * 1.5f).toInt(), (textSize * 1.5f).toInt())
                            val imageSpan = CenterImageSpan(drawable)
                            spannableString.setSpan(imageSpan, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                        }
                    }
                }
            } while (emotionMatcher.find())
        }

        super.setText(spannableString)
    }

}