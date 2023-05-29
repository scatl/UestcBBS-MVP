package com.scatl.widget.imageview

import android.content.Context
import android.graphics.Canvas
import android.graphics.Path
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView
import com.scatl.widget.R

/**
 * Created by sca_tl at 2023/5/26 15:04
 */
class ArcImageView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) : AppCompatImageView(context, attrs) {

    private var path: Path? = null

    var arcHeight = 50F
        set(value) {
            field = value
            invalidate()
        }

    init {
        if (attrs != null) {
            val typedArray = context.obtainStyledAttributes(attrs, R.styleable.ArcImageView)
            arcHeight = typedArray.getDimension(R.styleable.ArcImageView_arcHeight, 50F)
            typedArray.recycle()
        }
        path = Path()
    }

    override fun onDraw(canvas: Canvas) {
        path?.moveTo(0f, height - arcHeight)
        path?.quadTo(width / 2f, height.toFloat(), width.toFloat(), height - arcHeight)
        path?.lineTo(width.toFloat(), height - arcHeight)
        path?.lineTo(width.toFloat(), 0f)
        path?.lineTo(0f, 0f)
        path?.close()
        canvas.clipPath(path!!)
        super.onDraw(canvas)
    }
}