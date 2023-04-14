package com.scatl.widget.editor

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.card.MaterialCardView
import com.google.android.material.imageview.ShapeableImageView
import com.scatl.widget.R
import java.util.regex.Pattern

/**
 * Created by sca_tl at 2023/3/31 16:09
 */
@SuppressLint("NotifyDataSetChanged")
class EditorAdapter(val mContext: Context): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var mFocusedText = EditText(mContext)
    private var mFocusedPosition = 0

    var data: MutableList<BaseEditorEntity> = mutableListOf()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when(viewType) {
            EntityType.TYPE_TEXT -> {
                TextViewHolder(LayoutInflater.from(mContext).inflate(R.layout.editor_item_edittext, parent, false))
            }
            EntityType.TYPE_IMAGE -> {
                ImageViewHolder(LayoutInflater.from(mContext).inflate(R.layout.editor_item_imageview, parent, false))
            }
            else -> {
                TextViewHolder(LayoutInflater.from(mContext).inflate(R.layout.editor_item_edittext, parent, false))
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(getItemViewType(position)) {
            EntityType.TYPE_TEXT -> {
                setText(holder as TextViewHolder, position)
            }
            EntityType.TYPE_IMAGE -> {
                setImage(holder as ImageViewHolder, position)
            }
        }
    }

    override fun getItemCount() = data.size

    override fun getItemViewType(position: Int) = data[position].type

    private fun setImage(holder: ImageViewHolder, position: Int) {
        val model = data[position] as EditorImageEntity

        Glide.with(mContext).load(model.path).into(holder.image)

        holder.delete.setOnClickListener {
            val next = data.get(position + 1)
            val before = data.get(position - 1)
            if (next is EditorTextEntity && before is EditorTextEntity) {
                if (next.content.isEmpty()) {
                    data.removeAt(position)
                    data.removeAt(position)
                    notifyItemRangeRemoved(position, 2)
                } else {
                    data.removeAt(position)
                    data.removeAt(position)
                    notifyItemRangeRemoved(position, 2)
                    before.content += next.content
                    notifyItemChanged(position - 1)
                }
            }
        }
    }

    private fun setText(holder: TextViewHolder, position: Int) {
        val editText = holder.editText
        val model = data[position] as EditorTextEntity

        if (editText.tag is TextWatcher) {
            editText.removeTextChangedListener(editText.tag as TextWatcher)
        }
        editText.setText(model.content)
        editText.hint = model.hint

        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) { }

            override fun afterTextChanged(e: Editable) {
                model.content = e.toString()
                takeIf { mFocusedText != editText }?.apply {
                    mFocusedText = editText
                    mFocusedPosition = position
                }
            }
        }
        editText.onFocusChangeListener = View.OnFocusChangeListener { v, hasFocus ->
            takeIf { hasFocus }?.apply {
                mFocusedText = editText
                mFocusedPosition = position
                model.requestFocus = false
            }
        }
        editText.addTextChangedListener(textWatcher)
        editText.tag = textWatcher
        if (model.requestFocus) {
            editText.requestFocus()
//            KeyboardUtil.showSoftKeyboard(context, editText, 100)
        }
    }

    /**
     * 插入图片
     * @param path 图片路径
     */
    fun insertImage(path: String) {
        if (path.isEmpty()) {
            Toast.makeText(mContext, "图片路径是空的，再试试看", Toast.LENGTH_SHORT).show()
            return
        }
        if (mFocusedPosition > data.size) {
            Toast.makeText(mContext, "啊，出了点问题，随便输入点什么再插入图片试试", Toast.LENGTH_SHORT).show()
            return
        }
        if (mFocusedText.isFocused) {
            when(val select = mFocusedText.selectionStart) {
                0 -> {
                    if (mFocusedText.length() != 0) {
                        data.add(mFocusedPosition, EditorTextEntity())
                        data.add(mFocusedPosition + 1, EditorImageEntity(path))
                        notifyItemRangeInserted(mFocusedPosition, 2)
                        if (data[2] is EditorTextEntity && mFocusedPosition == 0) {
                            (data[2] as? EditorTextEntity)?.requestFocus = true
                            notifyItemChanged(2)
                        }
                    } else {
                        data.add(mFocusedPosition + 1, EditorImageEntity(path))
                        data.add(mFocusedPosition + 2, EditorTextEntity(requestFocus = true))
                        notifyItemRangeInserted(mFocusedPosition + 1, 2)
                    }
                }
                mFocusedText.text.length -> {
                    if (mFocusedPosition == data.size - 1 || data.get(mFocusedPosition + 1) is EditorImageEntity) {
                        data.add(mFocusedPosition + 1, EditorImageEntity(path))
                        data.add(mFocusedPosition + 2, EditorTextEntity(requestFocus = true))
                        notifyItemRangeInserted(mFocusedPosition + 1, 2)
                    } else {
                        data.add(mFocusedPosition + 1, EditorImageEntity(path))
                        notifyItemInserted(mFocusedPosition + 1)
                    }
                }
                else -> {
                    val part1 = mFocusedText.text.subSequence(0, select)
                    val part2 = mFocusedText.text.subSequence(select, mFocusedText.text.length)
                    data.removeAt(mFocusedPosition)
                    data.add(mFocusedPosition, EditorTextEntity(content = part1.toString(), requestFocus = true))
                    data.add(mFocusedPosition + 1, EditorImageEntity(path))
                    data.add(mFocusedPosition + 2, EditorTextEntity(part2.toString()))
                    notifyItemRangeChanged(mFocusedPosition, 3)
                }
            }
        }
    }

    class TextViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val editText: EditText = itemView.findViewById(R.id.edit_text)
    }

    class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val image: ShapeableImageView = itemView.findViewById(R.id.image)
        val delete: ImageView = itemView.findViewById(R.id.delete)
    }


}