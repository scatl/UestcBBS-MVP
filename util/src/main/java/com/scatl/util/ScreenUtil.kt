package com.scatl.util

import android.content.Context

/**
 * created by sca_tl at 2023/3/19 19:45
 */
object ScreenUtil {

    @JvmStatic
    fun dip2pxF(context: Context, dpValue: Float): Float {
        val scale = context.resources.displayMetrics.density
        return dpValue * scale + 0.5f
    }

    @JvmStatic
    fun dip2px(context: Context, dpValue: Float): Int {
        val scale = context.resources.displayMetrics.density
        return (dpValue * scale + 0.5f).toInt()
    }

    @JvmStatic
    fun px2dip(context: Context, pxValue: Float): Float {
        val scale = context.resources.displayMetrics.density
        return pxValue / scale + 0.5f
    }

    @JvmStatic
    fun sp2px(context: Context, spValue: Float): Float {
        val fontScale = context.resources.displayMetrics.scaledDensity
        return spValue * fontScale + 0.5f
    }

    @JvmStatic
    fun getScreenWidth(context: Context?, withDp: Boolean = false): Int {
        if (context == null) {
            return -1
        }
        val resources = context.resources
        val dm = resources.displayMetrics
        return if (withDp) px2dip(context, dm.widthPixels.toFloat()).toInt() else dm.widthPixels
    }

    @JvmStatic
    fun getScreenHeight(context: Context?, withDp: Boolean = false): Int {
        if (context == null) {
            return -1
        }
        val resources = context.resources
        val dm = resources.displayMetrics
        return if (withDp) px2dip(context, dm.heightPixels.toFloat()).toInt() else dm.heightPixels
    }

}