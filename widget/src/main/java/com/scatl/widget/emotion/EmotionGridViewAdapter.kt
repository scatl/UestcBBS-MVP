package com.scatl.widget.emotion

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.RelativeLayout
import com.scatl.widget.R
import com.scatl.util.ScreenUtil
import com.scatl.widget.load

/**
 * Created by sca_tl at 2023/5/15 9:28
 */
class EmotionGridViewAdapter(val context: Context,
                             val colums: Int,
                             val path: List<String>,
                             val onEmotionClick: (path: String?) -> Unit): BaseAdapter() {

    var rows: Int = 0

    init {
        rows = path.size / colums
    }

    override fun getCount() = path.size

    override fun getItem(position: Int) = path[position]

    override fun getItemId(position: Int) = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {
        val holder: ViewHolder
        var convertV = convertView

        if (convertV == null) {
            convertV = LayoutInflater.from(context).inflate(R.layout.item_emotion_gridview, RelativeLayout(context))
            holder = ViewHolder().apply {
                imageView = convertV.findViewById(R.id.img)
                rootView = convertV.findViewById(R.id.root_view)
            }
            convertV.tag = holder
        } else {
            holder = convertV.tag as ViewHolder
        }

        if (position / colums == rows) {
            holder.rootView?.layoutParams = (holder.rootView?.layoutParams as? RelativeLayout.LayoutParams?)?.apply {
                bottomMargin = ScreenUtil.dip2px(context, 30f)
            }
        } else {
            holder.rootView?.layoutParams = (holder.rootView?.layoutParams as? RelativeLayout.LayoutParams?)?.apply {
                bottomMargin = ScreenUtil.dip2px(context, 0f)
            }
        }

        holder.imageView?.setOnClickListener { _ ->
            onEmotionClick(path[position])
        }
        holder.imageView?.load(path[position])

        return convertV
    }

    class ViewHolder {
        var imageView: ImageView? = null
        var rootView: RelativeLayout? = null
    }
}