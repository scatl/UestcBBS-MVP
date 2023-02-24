package com.scatl.uestcbbs.util

import android.content.Context
import androidx.annotation.AttrRes
import com.google.android.material.color.MaterialColors
import com.scatl.uestcbbs.App
import com.scatl.uestcbbs.R

/**
 * created by sca_tl at 2022/6/1 18:16
 */
object ColorUtil {

    @JvmStatic
    fun getAttrColor(context: Context?, @AttrRes color: Int): Int {
        if (context == null) {
            return App.getContext().getColor(R.color.md_theme_primary)
        }
        return MaterialColors.getColor(context, color, context.getColor(R.color.md_theme_primary))
    }

}