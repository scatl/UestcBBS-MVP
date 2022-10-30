package com.scatl.uestcbbs.util

object NumberUtil {

    @JvmStatic
    fun parseInt(string: String?) =
        try {
            string?.toInt()
        } catch (e: Exception) {
            0
        }

}