package com.scatl.util

object NumberUtil {

    @JvmStatic
    fun parseInt(string: String?, defaultValue: Int = 0) =
        try {
            string?.toInt() ?: 0
        } catch (e: Exception) {
            defaultValue
        }

    @JvmStatic
    fun parseLong(string: String?) =
        try {
            string?.toLong() ?: 0L
        } catch (e: Exception) {
            0L
        }

}