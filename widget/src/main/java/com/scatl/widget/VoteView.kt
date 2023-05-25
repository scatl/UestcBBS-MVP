package com.scatl.widget

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.graphics.Shader
import android.util.AttributeSet
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator

/**
 * Created by sca_tl at 2023/4/18 10:12
 */
class VoteView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    companion object {
        const val TAG = "VoteView"
    }

    private var paint = Paint()
    private var path = Path()
    private var rectF = RectF()

    private var leftNum: Int = 0
    private var rightNum: Int = 0
    private var progress: Float = 0f
    private var gapWidth: Float = 20f
    private var cornerRadius: Float = 40f
    private var leftStartColor: Int = Color.parseColor("#FF0000")
    private var leftEndColor: Int = Color.parseColor("#FF00FF")
    private var rightStartColor: Int = Color.parseColor("#00FF00")
    private var rightEndColor: Int = Color.parseColor("#00FF00")

    private var w: Float = 0f
    private var h: Float = 0f

    init {
        if (attrs != null) {
            val typedArray = context.obtainStyledAttributes(attrs, R.styleable.VoteView)
            leftNum = typedArray.getInt(R.styleable.VoteView_leftNum, 0)
            rightNum = typedArray.getInt(R.styleable.VoteView_rightNum, 0)
            progress = typedArray.getFloat(R.styleable.VoteView_progress, 0f)
            gapWidth = typedArray.getDimension(R.styleable.VoteView_gapWidth, 0f)
            cornerRadius = typedArray.getDimension(R.styleable.VoteView_cornerRadius, 0f)
            leftStartColor = typedArray.getColor(R.styleable.VoteView_leftStartColor, Color.parseColor("#FF0000"))
            leftEndColor = typedArray.getColor(R.styleable.VoteView_leftEndColor, Color.parseColor("#FF0000"))
            rightStartColor = typedArray.getColor(R.styleable.VoteView_rightStartColor, Color.parseColor("#00FF00"))
            rightEndColor = typedArray.getColor(R.styleable.VoteView_rightEndColor, Color.parseColor("#00FF00"))
            typedArray.recycle()
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)

        val width = if (widthMode == MeasureSpec.EXACTLY) {
            widthSize
        } else {
            paddingLeft + width + paddingRight
        }

        val height = if (heightMode == MeasureSpec.EXACTLY) {
            heightSize
        } else {
            paddingTop + height + paddingBottom
        }

        setMeasuredDimension(width, height)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        w = width.toFloat()
        h = height.toFloat()

        drawCorner(canvas)
        drawCorner(canvas)
        drawLeft(canvas)
        drawRight(canvas)
    }

    private fun drawCorner(canvas: Canvas?) {
        path.reset()
        rectF.set(0f, 0f, w, h)
        val radii = floatArrayOf(
            cornerRadius, cornerRadius,
            cornerRadius, cornerRadius,
            cornerRadius, cornerRadius,
            cornerRadius, cornerRadius
        )
        path.addRoundRect(rectF, radii, Path.Direction.CW)
        canvas?.clipPath(path)
    }

    private fun drawLeft(canvas: Canvas?) {
        paint.reset()
        path.reset()

        val lNum: Int
        val rNum: Int
        if (leftNum == 0 && rightNum == 0) {
            lNum = 1
            rNum = 1
        } else {
            lNum = leftNum
            rNum = rightNum
        }

        val offset = if (rNum == 0) {
            h * progress
        } else if (lNum == 0) {
            - h * progress - (h - gapWidth) * progress
        } else {
            -(h - gapWidth) / 2f * progress
        }

        val leftLength = (lNum.toFloat() / (lNum + rNum).toFloat()) * w * progress - offset
        paint.shader = LinearGradient(0f, 0f, leftLength, h, leftStartColor, leftEndColor, Shader.TileMode.CLAMP)

        path.moveTo(0f, 0f)
        path.lineTo(leftLength, 0f)
        path.lineTo(leftLength - h, h)
        path.lineTo(0f, h)
        path.close()

        canvas?.drawPath(path, paint)
    }

    private fun drawRight(canvas: Canvas?) {
        paint.reset()
        path.reset()

        val lNum: Int
        val rNum: Int
        if (leftNum == 0 && rightNum == 0) {
            lNum = 1
            rNum = 1
        } else {
            lNum = leftNum
            rNum = rightNum
        }

        val offset = if (lNum == 0) {
            h * progress
        } else if (rNum == 0) {
            - h * progress - (h - gapWidth) * progress
        } else {
            -(h - gapWidth) / 2f * progress
        }

        val rightLength = (rNum.toFloat() / (lNum + rNum).toFloat()) * w * progress - offset
        paint.shader = LinearGradient(w - rightLength, 0f, w, h, rightStartColor, rightEndColor, Shader.TileMode.CLAMP)

        path.moveTo(w, 0f)
        path.lineTo(w - rightLength + h, 0f)
        path.lineTo(w - rightLength, h)
        path.lineTo(w, h)
        path.close()

        canvas?.drawPath(path, paint)
    }

    fun setNum(leftNum: Int?, rightNum: Int?) {
        this.leftNum = leftNum?: 0
        this.rightNum = rightNum?: 0
        invalidate()
    }

    fun plusNum(leftPlus: Int, rightPlus: Int) {
        setNum(getLeftNum().plus(leftPlus), getRightNum().plus(rightPlus))
    }

    fun getLeftNum() = this.leftNum

    fun getRightNum() = this.rightNum

    fun setColor(leftStart: Int, leftEnd:Int, rightStart: Int, rightEnd: Int) {
        this.leftStartColor = leftStart
        this.leftEndColor = leftEnd
        this.rightStartColor = rightStart
        this.rightEndColor = rightEnd
        invalidate()
    }

    fun setGapWidth(gapWidth: Float) {
        this.gapWidth = gapWidth
    }

    fun setCornerRadius(cornerRadius: Float) {
        this.cornerRadius = cornerRadius
    }

    fun setProgress(progress: Float) {
        this.progress = progress
        invalidate()
    }

    fun startAnimation(duration: Long) {
        ValueAnimator
            .ofFloat(0f, 1f)
            .setDuration(duration)
            .apply {
                interpolator = AccelerateDecelerateInterpolator()
                addUpdateListener {
                    setProgress(it.animatedValue as Float)
                }
                start()
            }
    }
}