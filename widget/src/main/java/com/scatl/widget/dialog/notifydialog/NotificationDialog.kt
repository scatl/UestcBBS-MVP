package com.scatl.widget.dialog.notifydialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.TextView
import com.scatl.widget.R
import com.scatl.widget.databinding.LayoutNotificationDialogBinding
import kotlin.math.abs

/**
 * Created by sca_tl at 2023/5/18 9:33
 */
class NotificationDialog(context: Context, var title: String, var content: String) : Dialog(context, R.style.dialog_notifacation_top) {

    private var mListener: OnNotificationClick? = null
    private var mStartY: Float = 0F
    private var mView: View? = null
    private var mHeight: Int? = 0
    private var mBinding = LayoutNotificationDialogBinding.inflate(LayoutInflater.from(getContext()))

    init {
        mView = LayoutInflater.from(context).inflate(R.layout.layout_notification_dialog, null)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(mView!!)

        window?.apply {
            setGravity(Gravity.TOP)
            setWindowAnimations(R.style.dialog_animation)
        }
        window?.attributes = window?.attributes?.apply {
            width = ViewGroup.LayoutParams.MATCH_PARENT
            height = ViewGroup.LayoutParams.WRAP_CONTENT
            flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
        }
        setCanceledOnTouchOutside(false)
        initData()
    }

    private fun initData() {
        val tvTitle = findViewById<TextView>(R.id.tv_title)
        val tvContent = findViewById<TextView>(R.id.tv_content)
        tvTitle.text = title
        tvContent.text = content
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                if (isOutOfBounds(event)) {
                    mStartY = event.y
                }
            }

            MotionEvent.ACTION_UP -> {
                if (mStartY > 0 && isOutOfBounds(event)) {
                    val moveY = event.y
                    if (abs(mStartY - moveY) < 20) {
                        mListener?.onClick()
                    }
                    dismiss()
                }
            }
        }
        return false
    }

    /**
     * 点击是否在范围外
     */
    private fun isOutOfBounds(event: MotionEvent): Boolean {
        val yValue = event.y
        if (yValue > 0 && yValue <= (mHeight ?: (0 + 40))) {
            return true
        }
        return false
    }

    private fun setDialogSize() {
        mBinding.root.addOnLayoutChangeListener { v, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom ->
            mHeight = v?.height
        }
    }

    /**
     * 显示Dialog但是不会自动退出
     */
    fun showDialog() {
        if (!isShowing) {
            show()
            setDialogSize()
        }
    }

    /**
     * 显示Dialog,3000毫秒后自动退出
     */
    fun showDialogAutoDismiss() {
        if (!isShowing) {
            show()
            setDialogSize()
            //延迟3000毫秒后自动消失
            Handler(Looper.getMainLooper()).postDelayed({
                if (isShowing) {
                    dismiss()
                }
            }, 3000L)
        }
    }

    fun setOnNotificationClickListener(listener: OnNotificationClick) {
        mListener = listener
    }

    interface OnNotificationClick {
        fun onClick()
    }
}
