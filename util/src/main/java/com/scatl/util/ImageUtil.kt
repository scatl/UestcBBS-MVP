package com.scatl.util

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.renderscript.Allocation
import android.renderscript.Element
import android.renderscript.RenderScript
import android.renderscript.ScriptIntrinsicBlur
import android.util.Base64
import android.util.Log
import androidx.annotation.FloatRange
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileInputStream
import kotlin.math.sqrt

/**
 * created by sca_tl at 2022/12/13 17:09
 */
object ImageUtil {

    @JvmStatic
    fun saveBitmapToGallery(context: Context?, bitmap: Bitmap?, path: String? = "image"): Boolean {
        if (context == null || bitmap == null) {
            return false
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val insert = context.contentResolver.insert(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                ContentValues().apply {
                    put(MediaStore.MediaColumns.RELATIVE_PATH, "${Environment.DIRECTORY_PICTURES}/${path}")
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

    @JvmStatic
    fun blur(context: Context, bitmap: Bitmap, @FloatRange(from = 0.0, to = 25.0) radius: Float): Bitmap? {
        val result = Bitmap.createBitmap(bitmap.width, bitmap.height, Bitmap.Config.ARGB_8888)
        val renderScript = RenderScript.create(context)
        val blur = ScriptIntrinsicBlur.create(renderScript, Element.U8_4(renderScript))
        val input = Allocation.createFromBitmap(renderScript, bitmap)
        val out = Allocation.createFromBitmap(renderScript, result)
        blur.setRadius(radius)
        blur.setInput(input)
        blur.forEach(out)
        out.copyTo(result)
        renderScript.destroy()
        return result
    }

    @JvmStatic
    fun bitmap2Drawable(bitmap: Bitmap?): Drawable {
        return BitmapDrawable(bitmap)
    }

    @JvmStatic
    fun drawable2Bitmap(drawable: Drawable): Bitmap? {
        return (drawable as? BitmapDrawable?)?.bitmap
    }

    @JvmStatic
    fun bitmapToBase64(bitmap: Bitmap?): String? {
        var result: String?
        ByteArrayOutputStream().use {
            bitmap?.compress(Bitmap.CompressFormat.JPEG, 100, it)
            val bitmapBytes = it.toByteArray()
            it.flush()
            result = Base64.encodeToString(bitmapBytes, Base64.NO_WRAP)
        }
        return result
    }

    @JvmStatic
    fun getMimeType(filePath: String?): String {
        if (filePath == null) {
            return ""
        }
        try {
            val options = BitmapFactory.Options().apply {
                inJustDecodeBounds = true
            }
            BitmapFactory.decodeStream(FileInputStream(File(filePath)), null, options)
            return options.outMimeType
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return ""
    }

    @JvmStatic
    fun getBitmapFormat(filePath: String?): Bitmap.CompressFormat {
        val mimeType = getMimeType(filePath)
        return if (mimeType.startsWith("image/")) {
            when(mimeType) {
                "image/jpg", "image/jpeg" -> Bitmap.CompressFormat.JPEG
                "image/png" -> Bitmap.CompressFormat.PNG
                "image/webp" -> {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) Bitmap.CompressFormat.WEBP_LOSSLESS else Bitmap.CompressFormat.WEBP
                }
                else -> Bitmap.CompressFormat.PNG
            }
        } else {
            Bitmap.CompressFormat.PNG
        }
    }
}