package com.scatl.util

/**
 * created by sca_tl at 2023/5/21 11:34
 */
object TimeUtil {

    /**
     * 毫秒数转换成分秒格式：100000ms -> 01:40
     * 7%60 =
     */
    @JvmStatic
    fun formatMsToMinutes(ms: Long): String {
        val minutes = (ms / 60000).toInt()

        val formatMinutes = if (minutes < 10) {
            "0${minutes}"
        } else {
            minutes.toString()
        }

        val seconds = (ms - minutes * 60000) / 1000
        val formatSeconds = if (seconds < 10) {
            "0${seconds}"
        } else {
            seconds.toString()
        }

        return formatMinutes.plus(":").plus(formatSeconds)
    }

}