package com.scatl.util

import java.text.DecimalFormat

/**
 * created by sca_tl at 2023/3/2 21:14
 */
object FileUtil {

    @JvmStatic
    fun formatFileSize(size: Long): String {
        var sizeStr = ""
        val df = DecimalFormat("#0.00")

        when(size) {
            in Long.MIN_VALUE .. 1024 -> {
                sizeStr = df.format(size.toDouble()) + "B"
            }
            in 1024 .. 1048576 -> {
                sizeStr = df.format(size.toDouble() / 1024) + "KB"
            }
            in 1048576 .. 1073741824 -> {
                sizeStr = df.format(size.toDouble() / 1048576) + "MB"
            }
        }

        return sizeStr
    }

}