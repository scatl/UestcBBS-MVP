package com.scatl.widget.dialog

import android.content.Context
import android.content.DialogInterface
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup.MarginLayoutParams
import androidx.appcompat.app.AlertDialog
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.scatl.util.ScreenUtil
import com.scatl.widget.R

/**
 * Created by sca_tl at 2023/4/13 15:43
 */
class InputAlertDialogBuilder: BlurAlertDialogBuilder {

    private var mHint: CharSequence? = "请输入内容"
    private var mMessage: CharSequence? = ""
    private var mInputType: Int = InputType.TYPE_CLASS_TEXT
    private var mPositiveListener: OnPositiveListener? = null

    constructor(context: Context) : super(context)

    constructor(context: Context, overrideThemeResId: Int) : super(context, overrideThemeResId)

    override fun create(): AlertDialog {
        setView(null)
        return super.create()
    }

    override fun setView(view: View?) = apply {
        val rootLayout: View = LayoutInflater.from(context).inflate(R.layout.dialog_input, null, false)
        val inputLayout: TextInputLayout = rootLayout.findViewById(R.id.input_layout)
        val edittext: TextInputEditText = rootLayout.findViewById(R.id.edittext)
        edittext.apply {
            hint = mHint
            inputType = mInputType
        }

        if (mMessage.isNullOrEmpty()) {
            inputLayout.layoutParams = (inputLayout.layoutParams as MarginLayoutParams).apply {
                topMargin = ScreenUtil.dip2px(context, 15f)
            }
        }

        super.setView(rootLayout)
    }

    override fun show(): AlertDialog? {
        val dialog = super.show()
        dialog?.getButton(AlertDialog.BUTTON_POSITIVE)?.setOnClickListener {
            val edittext = dialog.findViewById<TextInputEditText>(R.id.edittext)
            if (edittext != null) {
                mPositiveListener?.onClick(dialog, edittext.text?.toString())
            }
        }
        return dialog
    }

    override fun setMessage(message: CharSequence?) = apply {
        mMessage = message
        super.setMessage(message)
    }

    override fun setMessage(messageId: Int) = apply {
        mMessage = context.getString(messageId)
        super.setMessage(messageId)
    }

    fun setHint(hint: String?) = apply {
        if (hint != null) {
            mHint = hint
        }
    }

    fun setInputType(type: Int?) = apply {
        if (type != null) {
            mInputType = type
        }
    }

    fun setPositiveButton(text: String?, listener: OnPositiveListener) = apply {
        super.setPositiveButton(text, null)
        mPositiveListener = listener
    }

    fun interface OnPositiveListener {
        fun onClick(dialog: DialogInterface?, inputText: String?)
    }

}