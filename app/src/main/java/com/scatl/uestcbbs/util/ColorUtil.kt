package com.scatl.uestcbbs.util

import android.content.Context
import androidx.annotation.AttrRes
import com.google.android.material.color.MaterialColors
import com.scatl.uestcbbs.R

/**
 * created by sca_tl at 2022/6/1 18:16
 */
object ColorUtil {

    @JvmStatic
    fun getAttrColor(context: Context, @AttrRes color: Int) =
        MaterialColors.getColor(context, color, context.getColor(R.color.colorPrimary))
}