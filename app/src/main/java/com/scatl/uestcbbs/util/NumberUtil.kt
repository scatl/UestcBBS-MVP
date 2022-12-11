package com.scatl.uestcbbs.util

object NumberUtil {

    @JvmStatic
    fun parseInt(string: String?) =
        try {
            string?.toInt() ?: 0
        } catch (e: Exception) {
            0
        }

}