package com.scatl.widget.editor

import android.content.Context
import android.net.Uri
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.AttributeSet
import android.util.Log
import android.view.ViewGroup
import android.widget.EditText
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.imageview.ShapeableImageView
import java.io.File
import java.lang.ref.WeakReference

/**
 * Created by sca_tl on 2022/6/24 14:38
 */
class Editor @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null): RelativeLayout(context, attrs) {

    private val mAutoSaveTask by lazy { AutoSaveTask(this) }
    private val mRecyclerView by lazy { RecyclerView(getContext()).apply {
        layoutManager = LinearLayoutManager(getContext())
    } }
    private var mAutoSaveListener: AutoSaveListener? = null
    private var mAutoSaveDelay: Long = 3000
    private var mFocusedText = EditText(getContext())
    private var mFocusedPosition = 0
    private lateinit var mAdapter: EditorAdapter

    init {
        addView(mRecyclerView, LayoutParams(LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT))
        mAdapter = EditorAdapter(context)
        mAdapter.data = newInitData()
        mRecyclerView.adapter = mAdapter
//        setupAdapter()
    }

//    private fun setupAdapter() {
//        mAdapter = mRecyclerView.linear().setup {
//            addType<EditorTextEntity>(R.layout.editor_item_edittext)
//            addType<EditorImageEntity>(R.layout.editor_item_imageview)
//
//            onBind {
//                when(itemViewType) {
//                    R.layout.editor_item_edittext -> {
//                        val editText = findView<EditText>(R.id.edit)
//                        val model = getModel<EditorTextEntity>()
//
//                        if (editText.tag is TextWatcher) {
//                            editText.removeTextChangedListener(editText.tag as TextWatcher)
//                        }
//                        editText.setText(model.content)
//                        editText.hint = model.hint
//
//                        val textWatcher = object : TextWatcher {
//                            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }
//
//                            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) { }
//
//                            override fun afterTextChanged(e: Editable) {
//                                model.content = e.toString()
//                                takeIf { mFocusedText != editText }?.apply {
//                                    mFocusedText = editText
//                                    mFocusedPosition = modelPosition
//                                }
//                            }
//                        }
//                        editText.onFocusChangeListener = OnFocusChangeListener { v, hasFocus ->
//                            takeIf { hasFocus }?.apply {
//                                mFocusedText = editText
//                                mFocusedPosition = modelPosition
//                                model.requestFocus = false
//                            }
//                        }
//                        editText.addTextChangedListener(textWatcher)
//                        editText.tag = textWatcher
//                        if (model.requestFocus) {
//                            editText.requestFocus()
//                            KeyboardUtil.showSoftKeyboard(context, editText, 100)
//                        }
//                    }
//                    R.layout.editor_item_imageview -> {
//                        val model = getModel<EditorImageEntity>()
//                        val dsp = findView<EditText>(R.id.dsp)
//
//                        findView<ShapeableImageView>(R.id.image).load(model.path)
//
//                        if (dsp.tag is TextWatcher) {
//                            dsp.removeTextChangedListener(dsp.tag as TextWatcher)
//                        }
//                        dsp.setText(model.dsp)
//                        dsp.hint = model.dspHint
//
//                        val textWatcher = object : TextWatcher {
//                            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }
//
//                            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) { }
//
//                            override fun afterTextChanged(e: Editable) {
//                                model.dsp = e.toString()
//                            }
//                        }
//                        dsp.addTextChangedListener(textWatcher)
//                        dsp.tag = textWatcher
//                    }
//                }
//            }
//
//            R.id.delete.onClick {
//                val next = adapter.models?.get(modelPosition + 1)
//                val before = adapter.models?.get(modelPosition - 1)
//                if (next is EditorTextEntity && before is EditorTextEntity) {
//                    if (next.content.isEmpty()) {
//                        adapter.mutable.removeAt(modelPosition)
//                        adapter.mutable.removeAt(modelPosition)
//                        notifyItemRangeRemoved(modelPosition, 2)
//                    } else {
//                        adapter.mutable.removeAt(modelPosition)
//                        adapter.mutable.removeAt(modelPosition)
//                        notifyItemRangeRemoved(modelPosition, 2)
//                        before.content += next.content
//                        notifyItemChanged(modelPosition - 1)
//                    }
//                }
//            }
//
////            R.id.image.onClick {
////                val path = (mAdapter.models?.get(modelPosition) as ImageEntity).path
////                ImageViewer
////                    .INSTANCE
////                    .with(context as AppCompatActivity)
////                    .setImageUrls(arrayListOf(path))
////                    .setEnterIndex(0)
////                    .setEnterView(findView(R.id.image))
////                    .show()
////            }
//        }
//    }


    fun insertImage(path: String) {
        mAdapter.insertImage(path)
    }

    /**
     * 插入图片
     * @param path 图片路径
     */
//    fun insertImage(path: String) {
//        if (path.isEmpty()) {
//            Toast.makeText(context, "图片路径是空的，再试试看", Toast.LENGTH_SHORT).show()
//            return
//        }
//        if (mFocusedPosition > mAdapter.mutable.size) {
//            Toast.makeText(context, "啊，出了点问题，随便输入点什么再插入图片试试", Toast.LENGTH_SHORT).show()
//            return
//        }
//        if (mFocusedText.isFocused) {
//            when(val select = mFocusedText.selectionStart) {
//                0 -> {
//                    if (mFocusedText.length() != 0) {
//                        mAdapter.mutable.add(mFocusedPosition, EditorTextEntity())
//                        mAdapter.mutable.add(mFocusedPosition + 1, EditorImageEntity(path))
//                        mAdapter.notifyItemRangeInserted(mFocusedPosition, 2)
//                        if (mAdapter.mutable[2] is EditorTextEntity && mFocusedPosition == 0) {
//                            (mAdapter.mutable[2] as? EditorTextEntity)?.requestFocus = true
//                            mAdapter.notifyItemChanged(2)
//                        }
//                    } else {
//                        mAdapter.mutable.add(mFocusedPosition + 1, EditorImageEntity(path))
//                        mAdapter.mutable.add(mFocusedPosition + 2, EditorTextEntity(requestFocus = true))
//                        mAdapter.notifyItemRangeInserted(mFocusedPosition + 1, 2)
//                    }
//                }
//                mFocusedText.text.length -> {
//                    if (mFocusedPosition == mAdapter.mutable.size - 1 || mAdapter.models?.get(mFocusedPosition + 1) is EditorImageEntity) {
//                        mAdapter.mutable.add(mFocusedPosition + 1, EditorImageEntity(path))
//                        mAdapter.mutable.add(mFocusedPosition + 2, EditorTextEntity(requestFocus = true))
//                        mAdapter.notifyItemRangeInserted(mFocusedPosition + 1, 2)
//                    } else {
//                        mAdapter.mutable.add(mFocusedPosition + 1, EditorImageEntity(path))
//                        mAdapter.notifyItemInserted(mFocusedPosition + 1)
//                    }
//                }
//                else -> {
//                    val part1 = mFocusedText.text.subSequence(0, select)
//                    val part2 = mFocusedText.text.subSequence(select, mFocusedText.text.length)
//                    mAdapter.mutable.removeAt(mFocusedPosition)
//                    mAdapter.mutable.add(mFocusedPosition, EditorTextEntity(content = part1.toString(), requestFocus = true))
//                    mAdapter.mutable.add(mFocusedPosition + 1, EditorImageEntity(path))
//                    mAdapter.mutable.add(mFocusedPosition + 2, EditorTextEntity(part2.toString()))
//                    mAdapter.notifyItemRangeChanged(mFocusedPosition, 3)
//                }
//            }
//        }
//    }

    /**
     * 获取全部数据
     */
//    fun getData(filterEmptyText: Boolean = true): List<BaseEditorEntity> {
//        val entity = mutableListOf<BaseEditorEntity>()
//        mAdapter.models
//            ?.filterNot {
//                filterEmptyText && (it is EditorTextEntity && TextUtils.isEmpty(it.content))
//            }
//            ?.forEach {
//                entity.add(it as BaseEditorEntity)
//            }
//        return entity
//    }

//    fun isEmpty() = getData().isEmpty()

//    fun isNotEmpty() = !isEmpty()

    /**
     * 获取编辑器里的所有图片路径
     */
//    fun getImagePaths(): List<String> {
//        val images = mutableListOf<String>()
//        getData().forEach {
//            if (it is EditorImageEntity && it.type == EntityType.TYPE_IMAGE) {
//                images.add(it.path)
//            }
//        }
//        return images
//    }

    /**
     * 获取所有的图片文件
     */
//    fun getImageFiles(context: Context): List<File> {
//        val files = mutableListOf<File>()
//        getImagePaths().forEach {
//            files.add(FileUtils.getFile(context, Uri.parse(it)))
//        }
//        return files
//    }

    /**
     * 初始化
     * @param data 编辑器初始化数据。默认为[newInitData]返回的数据
     */
//    fun init(data: MutableList<BaseEditorEntity> = newInitData()) {
//        mAdapter.models = data
//        if(mAutoSaveListener != null) {
//            postDelayed(mAutoSaveTask, mAutoSaveDelay)
//        }
//    }

    private fun newInitData(): MutableList<BaseEditorEntity> {
        return mutableListOf(EditorTextEntity(hint = "写点什么吧~", requestFocus = true))
    }

    fun setAutoSaveListener(autoSaveListener: AutoSaveListener, saveDelay: Long = 3000): Editor {
        this.mAutoSaveDelay = saveDelay
        this.mAutoSaveListener = autoSaveListener
        return this
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        removeCallbacks(mAutoSaveTask)
    }

    interface AutoSaveListener {
        fun onReadySave(data: List<BaseEditorEntity>)
    }

    class AutoSaveTask(editor: Editor) : Runnable {

        private val reference by lazy { WeakReference(editor) }

        override fun run() {
            val editor: Editor? = reference.get()
            if (editor != null) {
//                editor.mAutoSaveListener?.onReadySave(editor.getData(filterEmptyText = false))
                editor.postDelayed(editor.mAutoSaveTask, editor.mAutoSaveDelay)
            }
        }
    }

}
