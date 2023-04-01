package com.scatl.util.common

import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import java.io.File
import java.text.DecimalFormat

/**
 * created by sca_tl at 2023/3/2 21:14
 */
object FileUtil {

    @JvmStatic
    fun getPath(context: Context?, uri: Uri?): String? {
        if (context == null || uri == null) {
            return null
        }
        if ("content".equals(uri.scheme, ignoreCase = true)) {
            var cursor: Cursor? = null

            try {
                val column = MediaStore.Files.FileColumns.DATA
                cursor = context.contentResolver.query(uri,
                    arrayOf(column),
                    null,
                    null,
                    null
                )
                if (cursor != null && cursor.moveToFirst()) {
                    val columnIndex = cursor.getColumnIndexOrThrow(column)
                    return cursor.getString(columnIndex)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                cursor?.close()
            }
        } else if ("file".equals(uri.scheme, ignoreCase = true)) {
            return uri.path
        }
        return null
    }

    @JvmStatic
    fun getFile(context: Context?, uri: Uri?): File {
        if (uri != null) {
            val path = getPath(context, uri)
            if (path != null && isLocal(path)) {
                return File(path)
            }
        }
        return File("")
    }

    @JvmStatic
    fun isLocal(url: String?): Boolean {
        return url != null && !url.startsWith("http://") && !url.startsWith("https://")
    }

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