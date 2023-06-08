package com.scatl.util

import android.content.ContentValues
import android.content.Context
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.text.TextUtils
import android.util.Base64
import android.widget.Toast
import java.io.ByteArrayOutputStream
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

    @JvmStatic
    fun saveImgFileToGallery(context: Context, file: File?, folder: String?) {
        if (file == null) {
            return
        }

        val fileName = System.currentTimeMillis().toString()

        val options = BitmapFactory.Options().apply {
            inJustDecodeBounds = true
        }
        BitmapFactory.decodeStream(FileInputStream(file), null, options)

        val extension = if (options.outMimeType?.startsWith("image/") == true) {
            options.outMimeType.replace("image/", "")
        } else {
            "jpg"
        }

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val savePath = if (folder.isNullOrEmpty()) {
                    Environment.DIRECTORY_PICTURES
                } else {
                    "${Environment.DIRECTORY_PICTURES}${File.separator}${folder}"
                }
                FileInputStream(file).use { inputStream ->
                    val uri = context.contentResolver.insert(
                        MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY),
                        ContentValues().apply {
                            put(MediaStore.MediaColumns.DISPLAY_NAME, fileName.plus(".").plus(extension))
                            put(MediaStore.MediaColumns.MIME_TYPE, "image/${extension}")
                            put(MediaStore.MediaColumns.RELATIVE_PATH, savePath)
                        }
                    )

                    context.contentResolver?.openOutputStream(uri!!)?.use { output ->
                        inputStream.copyTo(output)
                    }

                    Toast.makeText(context, "成功保存到相册：Pictures/${folder}", Toast.LENGTH_SHORT).show()
                }
            } else {
                val savePath = if (folder.isNullOrEmpty()) {
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).absolutePath
                } else {
                    File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).absolutePath, folder).absolutePath
                }

                val existFile = File(savePath, fileName.plus(".").plus(extension))
                if (existFile.exists() && existFile.isFile) {
                    existFile.delete()
                }

                FileInputStream(file).use { inputStream ->
                    val uri = context.contentResolver.insert(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        ContentValues().apply {
                            put(MediaStore.MediaColumns.DISPLAY_NAME, fileName.plus(".").plus(extension))
                            put(MediaStore.MediaColumns.MIME_TYPE, "image/${extension}")
                            put(MediaStore.MediaColumns.DATA, savePath)
                        }
                    )

                    context.contentResolver?.openOutputStream(uri!!)?.use { output ->
                        inputStream.copyTo(output)
                    }

                    Toast.makeText(context, "成功保存到相册：Pictures/${folder}", Toast.LENGTH_SHORT).show()
                }
            }
        } catch (e: Exception) {
            Toast.makeText(context, "保存失败:${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    @JvmStatic
    fun readAssetFileToString(context: Context?, name: String?): String? {
        var data: String? = null
        if (name == null) {
            return null
        }
        context
            ?.assets
            ?.open(name)
            ?.use {
                ByteArrayOutputStream().use { baos ->
                    it.copyTo(baos)
                    data = baos.toString()
                }
            }
        return data
    }
}