package com.scatl.widget.iamgeviewer.dragview

import android.content.Context
import android.graphics.PointF
import android.util.AttributeSet
import android.view.HapticFeedbackConstants
import android.view.MotionEvent
import android.view.ViewConfiguration
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

class DragSubsamplingScaleImageView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) : SubsamplingScaleImageView(context, attrs) {

    private var initCenter: PointF? = null
    private var changedCenter: PointF? = null
    private var initScale: Float? = null
    private val scaledTouchSlop by lazy { ViewConfiguration.get(context).scaledTouchSlop * 4f }
    private val dismissEdge by lazy { height * 0.12 }
    private var imageLoaded = false
    private var singleTouch = true
    private var fakeDragOffset = 0f
    private var lastX = 0f
    private var lastY = 0f
    private var hapticFeedback = true
    private var mDragListener: DragListener? = null

    var mDragEnable = true

    init {
        setOnStateChangedListener(object : OnStateChangedListener {
            override fun onScaleChanged(newScale: Float, origin: Int) = Unit
            override fun onCenterChanged(newCenter: PointF?, origin: Int) {
                changedCenter = newCenter
            }
        })
        setOnImageEventListener(object : DefaultOnImageEventListener() {
            override fun onImageLoaded() {
                imageLoaded = true
            }
        })
    }

    fun setDragListener(dragListener: DragListener?) {
        this.mDragListener = dragListener
    }

    override fun dispatchTouchEvent(event: MotionEvent?): Boolean {
        if (mDragEnable) {
            handleDispatchTouchEvent(event)
        }
        return super.dispatchTouchEvent(event)
    }

    private fun handleDispatchTouchEvent(event: MotionEvent?) {
        if (!imageLoaded) {
            return
        }
        if (initScale == null) {
            initScale = scale
            initCenter = center
            changedCenter = center
        }
        when (event?.actionMasked) {
            MotionEvent.ACTION_POINTER_DOWN -> {
                setSingleTouch(false)
                animate()
                        .translationX(0f)
                        .translationY(0f)
                        .scaleX(1f)
                        .scaleY(1f)
                        .setDuration(200)
                        .start()
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> up()
            MotionEvent.ACTION_MOVE -> {
                if (singleTouch && scale == initScale && (changedCenter?.y ?: initCenter?.y) == (initCenter?.y ?: 0f)) {
                    if (lastX == 0f) lastX = event.rawX
                    if (lastY == 0f) lastY = event.rawY
                    val offsetX = event.rawX - lastX
                    val offsetY = event.rawY - lastY
                    fakeDrag(offsetX, offsetY)
                }
            }
        }
    }

    private fun fakeDrag(offsetX: Float, offsetY: Float) {
        if (fakeDragOffset == 0f) {
            if (offsetY > scaledTouchSlop) fakeDragOffset = scaledTouchSlop
            else if (offsetY < -scaledTouchSlop) fakeDragOffset = -scaledTouchSlop
        }
        if (fakeDragOffset != 0f) {
            val fixedOffsetY = offsetY - fakeDragOffset
            parent?.requestDisallowInterceptTouchEvent(true)
            val fraction = abs(max(-1f, min(1f, fixedOffsetY / height)))
            val fakeScale = 1 - min(0.4f, fraction)
            scaleX = fakeScale
            scaleY = fakeScale
            translationY = fixedOffsetY
            translationX = offsetX / 2
            mDragListener?.onDragging(this, fraction)

            performFeedback()
        }
    }

    private fun performFeedback() {
        if (canRelease()) {
            if (hapticFeedback) {
                hapticFeedback = false
                performHapticFeedback(HapticFeedbackConstants.CONTEXT_CLICK)
            }
        } else {
            hapticFeedback = true
        }
    }

    private fun canRelease() = abs(translationY) > dismissEdge

    private fun up() {
        parent?.requestDisallowInterceptTouchEvent(false)
        setSingleTouch(true)
        fakeDragOffset = 0f
        lastX = 0f
        lastY = 0f

        if (canRelease()) {
            mDragListener?.onRelease(this)
        } else {
            animate()
                    .translationX(0f)
                    .translationY(0f)
                    .scaleX(1f)
                    .scaleY(1f)
                    .setDuration(200)
                    .setUpdateListener {
                        val offsetY = translationY
                        val fraction = min(1f, offsetY / height)
                        mDragListener?.onRestoring(this, fraction)
                    }
                    .start()
        }
    }

    private fun setSingleTouch(value: Boolean) {
        singleTouch = value
        mDragListener?.onSetViewPagerInputEnable(value)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        animate().cancel()
    }
}
