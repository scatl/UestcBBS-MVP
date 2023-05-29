package com.scatl.util

import android.text.TextUtils
import android.util.Base64
import java.io.File
import java.io.FileInputStream
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

    @JvmStatic
    fun getDirectorySize(directory: File?): Long {
        var size: Long = 0
        try {
            directory?.listFiles()?.let {
                for (i in it.indices) {
                    size = if (it[i].isDirectory) {
                        size + getDirectorySize(it[i])
                    } else {
                        size + it[i].length()
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return size
    }

    @JvmStatic
    fun fileToBase64(path: String?): String? {
        if (TextUtils.isEmpty(path)) {
            return null
        }
        var result: String?
        FileInputStream(path).use {
            val data = ByteArray(it.available())
            it.read(data)
            result = Base64.encodeToString(data, Base64.NO_WRAP)
        }
        return result
    }

}