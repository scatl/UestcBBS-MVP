package com.scatl.util

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Paint
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import java.io.File
import java.io.FileOutputStream
import kotlin.math.sqrt

/**
 * created by sca_tl at 2022/12/13 17:09
 */
object BitmapUtil {

    @JvmStatic
    fun saveBitmapToGallery(context: Context, bitmap: Bitmap): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val insert = context.contentResolver.insert(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                ContentValues().apply {
                    put(MediaStore.MediaColumns.RELATIVE_PATH, "${Environment.DIRECTORY_PICTURES}/uestcbbs")
                }
            ) ?: return false

            context.contentResolver.openOutputStream(insert).use {
                it ?: return false
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it)
            }
            return true
        } else {
            MediaStore.Images.Media.insertImage(context.contentResolver, bitmap, System.currentTimeMillis().toString(), "")
            return true
        }
    }

    @JvmStatic
    fun saveBitmap(name: String?, path: String?, bitmap: Bitmap?, mContext: Context?) {
        if (bitmap == null || mContext == null) {
            return
        }
        val realName = name?:"image.jpg"

        val saveFile = File(path, realName)
        try {
            if (saveFile.exists()) {
                saveFile.delete()
            }
            FileOutputStream(saveFile).use {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 50, it)
                it.flush()
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    @JvmStatic
    fun setWaterMark(text: String, originBitmap: Bitmap): Bitmap {
        val bitmap = originBitmap.copy(originBitmap.config, true)
        val canvas = Canvas(bitmap)
        canvas.rotate(-25f)

        val width = originBitmap.width
        val height  = originBitmap.height
        val diagonal = sqrt((width * width + height * height).toDouble()).toInt()

        val paint = Paint()
        paint.color = Color.parseColor("#01ff0000")
        paint.textSize = 50f
        paint.isAntiAlias = true
        val textWidth: Float = paint.measureText(text)

        var index = 0
        var fromX: Float

        var positionY = diagonal / 20
        while (positionY <= diagonal) {
            fromX = -width + index++ % 2 * textWidth
            var positionX = fromX
            while (positionX < width) {
                canvas.drawText(text, positionX, positionY.toFloat(), paint)
                positionX += (textWidth * 1.2).toFloat()
            }
            positionY += diagonal / 20
        }

        return bitmap
    }

    @JvmStatic
    fun compressBitmap(bitmap: Bitmap): Bitmap {
        var res = bitmap
        val width: Int = bitmap.width
        val height: Int = bitmap.height
        var inSampleSize = 1
        val max = 100 * 1024 * 1024
        while (width * height * 4 / inSampleSize > max) {
            inSampleSize *= 2
        }
        if (inSampleSize > 1) {
            val m = Matrix().apply {
                setScale(1 / inSampleSize.toFloat(), 1 / inSampleSize.toFloat())
            }
            res = Bitmap.createBitmap(bitmap, 0, 0, width, height, m, false)
        }
        return res
    }

}