package com.scatl.util

import android.content.Context
import android.graphics.Color
import androidx.annotation.AttrRes
import com.google.android.material.color.MaterialColors

/**
 * created by sca_tl at 2022/6/1 18:16
 */
object ColorUtil {

    @JvmStatic
    fun getAttrColor(context: Context?, @AttrRes color: Int): Int {
        if (context == null) {
            return Color.BLUE
        }
        return MaterialColors.getColor(context, color, 0)
    }

    @JvmStatic
    fun getAlphaColor(alpha: Float, baseColor: Int): Int {
        val a = Math.min(255, Math.max(0, (alpha * 255).toInt())) shl 24
        val rgb = 0x00ffffff and baseColor
        return a + rgb
    }

}