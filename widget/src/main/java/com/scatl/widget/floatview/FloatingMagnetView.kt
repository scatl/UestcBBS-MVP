package com.scatl.widget.floatview

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Configuration
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.ViewGroup
import android.widget.FrameLayout

/**
 * created by sca_tl at 2023/5/14 11:55
 */
open class FloatingMagnetView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : FrameLayout(context, attrs) {

    companion object {
        const val MARGIN_EDGE = 13
        const val TOUCH_TIME_THRESHOLD = 150
    }
    private var mOriginalRawX = 0f
    private var mOriginalRawY = 0f
    private var mOriginalX = 0f
    private var mOriginalY = 0f
    private var mFloatViewListener: FloatViewListener? = null
    private var mLastTouchDownTime: Long = 0
    protected var mMoveAnimator: MoveAnimator? = null
    protected var mScreenWidth = 0
    private var mScreenHeight = 0
    private var isNearestLeft = true
    private var mPortraitY = 0f

    init {
        mMoveAnimator = MoveAnimator()
        isClickable = true
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (event == null) {
            return false
        }
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                changeOriginalTouchParams(event)
                updateSize()
                mMoveAnimator?.stop()
            }

            MotionEvent.ACTION_MOVE -> {
                updateViewPosition(event)
            }

            MotionEvent.ACTION_UP -> {
                clearPortraitY()
                moveToEdge()
                if (isOnClickEvent()) {
                    dealClickEvent(event)
                }
            }
        }
        return true
    }

    protected fun dealClickEvent(event: MotionEvent?) {
        mFloatViewListener?.onClick(event)
    }

    protected fun isOnClickEvent(): Boolean {
        return System.currentTimeMillis() - mLastTouchDownTime < TOUCH_TIME_THRESHOLD
    }

    private fun updateViewPosition(event: MotionEvent) {
        x = mOriginalX + event.rawX - mOriginalRawX
        // 限制不可超出屏幕高度
        var desY = mOriginalY + event.rawY - mOriginalRawY
        if (desY < 0) {
            desY = 0f
        }
        if (desY > mScreenHeight - height) {
            desY = (mScreenHeight - height).toFloat()
        }
        y = desY
    }

    private fun changeOriginalTouchParams(event: MotionEvent) {
        mOriginalX = x
        mOriginalY = y
        mOriginalRawX = event.rawX
        mOriginalRawY = event.rawY
        mLastTouchDownTime = System.currentTimeMillis()
    }

    protected fun updateSize() {
        val viewGroup = parent as? ViewGroup?
        if (viewGroup != null) {
            mScreenWidth = viewGroup.width - width
            mScreenHeight = viewGroup.height
        }
    }

    fun moveToEdge() {
        moveToEdge(isNearestLeft(), false)
    }

    fun moveToEdge(isLeft: Boolean, isLandscape: Boolean) {
        val moveDistance = (if (isLeft) MARGIN_EDGE else mScreenWidth - MARGIN_EDGE).toFloat()
        if (!isLandscape && mPortraitY != 0f) {
            y = mPortraitY
            clearPortraitY()
        }
        mMoveAnimator?.start(
            moveDistance,
            Math.min(Math.max(0f, y), (mScreenHeight - height).toFloat())
        )
    }

    private fun clearPortraitY() {
        mPortraitY = 0f
    }

    protected fun isNearestLeft(): Boolean {
        val middle = mScreenWidth / 2
        isNearestLeft = x < middle
        return isNearestLeft
    }

    fun onRemove() {
        mFloatViewListener?.onRemove(this)
    }

    inner class MoveAnimator : Runnable {
        private val handler = Handler(Looper.getMainLooper())
        private var destinationX = 0f
        private var destinationY = 0f
        private var startingTime: Long = 0

        fun start(x: Float, y: Float) {
            destinationX = x
            destinationY = y
            startingTime = System.currentTimeMillis()
            handler.post(this)
        }

        override fun run() {
            if (rootView == null || rootView.parent == null) {
                return
            }
            val progress = Math.min(1f, (System.currentTimeMillis() - startingTime) / 400f)
            val deltaX: Float = (destinationX - getX()) * progress
            val deltaY: Float = (destinationY - getY()) * progress
            move(deltaX, deltaY)
            if (progress < 1) {
                handler.post(this)
            }
        }

        fun stop() {
            handler.removeCallbacks(this)
        }
    }

    private fun move(deltaX: Float, deltaY: Float) {
        x += deltaX
        y += deltaY
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        if (parent != null) {
            val isLandscape = newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE
            markPortraitY(isLandscape)
            (parent as? ViewGroup?)?.post {
                updateSize()
                moveToEdge(isNearestLeft, isLandscape)
            }
        }
    }

    private fun markPortraitY(isLandscape: Boolean) {
        if (isLandscape) {
            mPortraitY = y
        }
    }

    fun setFloatViewListener(floatViewListener: FloatViewListener?) {
        mFloatViewListener = floatViewListener
    }

}