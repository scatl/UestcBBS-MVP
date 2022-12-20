package com.scatl.uestcbbs.util

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import com.scatl.uestcbbs.App
import kotlin.math.sqrt

/**
 * created by sca_tl at 2022/12/13 17:09
 */
object BitmapUtil {

    @JvmStatic
    fun saveBitmap(context: Context, bitmap: Bitmap): Boolean {
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
    fun setWaterMark(originBitmap: Bitmap): Bitmap {
        val bitmap = originBitmap.copy(originBitmap.config, true)
        val canvas = Canvas(bitmap)
        canvas.rotate(0f)

        val width = originBitmap.width
        val height  = originBitmap.height
        val diagonal = sqrt((width * width + height * height).toDouble()).toInt()

        val paint = Paint()
        paint.color = Color.parseColor("#aeaeae")
        paint.textSize = CommonUtil.sp2px(App.getContext(), 16f).toFloat()
        paint.isAntiAlias = true
        val textWidth: Float = paint.measureText("test")

        var index = 0
        var fromX: Float

        var positionY = diagonal / 20
        while (positionY <= diagonal) {
            fromX = -width + index++ % 2 * textWidth
            var positionX = fromX
            while (positionX < width) {
                canvas.drawText("test", positionX, positionY.toFloat(), paint)
                positionX += (textWidth * 1.2).toFloat()
            }
            positionY += diagonal / 20
        }

        return bitmap
    }
}