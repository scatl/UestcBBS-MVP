package com.scatl.widget.gallery

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.checkbox.MaterialCheckBox
import com.scatl.widget.R
//import com.scatl.widget.viewer.ImageViewer

/**
 * Created by sca_tl on 2022/8/12 13:59
 */
@SuppressLint("NotifyDataSetChanged")
class GalleryAdapter(private val mContext: Context, val onMediaClick: (MediaEntity, selected: Boolean) -> Unit):
    RecyclerView.Adapter<GalleryAdapter.GalleryViewHolder>() {

    var data: List<MediaEntity> = mutableListOf()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GalleryViewHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.item_gallery, parent, false)
        return GalleryViewHolder(view)
    }

    override fun onBindViewHolder(holder: GalleryViewHolder, position: Int) {
        holder.image.apply {
            load(data[position].uri)
            if (Gallery.INSTANCE.hasMedia(data[position])) {
                scaleX = 1.3f
                scaleY = 1.3f
            } else {
                scaleX = 1.0f
                scaleY = 1.0f
            }
        }

        holder.checkBox.apply {
            setOnCheckedChangeListener(null)
            isChecked = Gallery.INSTANCE.hasMedia(data[position])
            setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    if (Gallery.INSTANCE.isReachMax()) {
                        this.isChecked = false
                        Toast.makeText(context, "最多选择${Gallery.INSTANCE.maxSelect}张图片", Toast.LENGTH_SHORT).show()
                    } else {
                        Gallery.INSTANCE.putMedia(data[position])
                        if (holder.image.scaleX == 1.0f) {
                            ValueAnimator
                                .ofFloat(1f, 1.3f)
                                .setDuration(300)
                                .apply {
                                    addUpdateListener { animator ->
                                        holder.image.scaleX = animator.animatedValue as Float
                                        holder.image.scaleY = animator.animatedValue as Float
                                    }
                                    start()
                                }
                        }
                    }
                } else {
                    Gallery.INSTANCE.removeMedia(data[position])
                    if (holder.image.scaleX == 1.3f) {
                        ValueAnimator
                            .ofFloat(1.3f, 1f)
                            .setDuration(300)
                            .apply {
                                addUpdateListener { animator ->
                                    holder.image.scaleX = animator.animatedValue as Float
                                    holder.image.scaleY = animator.animatedValue as Float
                                }
                                start()
                            }
                    }
                }
                onMediaClick(data[position], isChecked)
            }
        }

        holder.itemView.setOnClickListener {
            val s = mutableListOf<String>()
            data.forEach {
                s.add(it.uri.toString())
            }
//            ImageViewer
//                .INSTANCE
//                .with(mContext as AppCompatActivity)
//                .setImageUrls(s)
//                .setEnterIndex(position)
//                .setEnterView(holder.image)
//                .show()
        }
    }

    override fun getItemCount() = data.size

    inner class GalleryViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val image: ImageView = view.findViewById(R.id.image)
        val checkBox: MaterialCheckBox = view.findViewById(R.id.checkbox)
    }

}

