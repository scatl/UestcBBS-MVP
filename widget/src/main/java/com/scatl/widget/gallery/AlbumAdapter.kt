package com.scatl.widget.gallery

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.radiobutton.MaterialRadioButton
import com.google.android.material.textview.MaterialTextView
import com.scatl.widget.R
import com.scatl.widget.iamgeviewer.ImageConstant
import com.scatl.widget.load

/**
 * Created by sca_tl on 2022/8/12 14:32
 */
@SuppressLint("NotifyDataSetChanged", "SetTextI18n")
internal class AlbumAdapter(private val mContext: Context,
                            config: Gallery,
                            val onAlbumClick: (AlbumEntity) -> Unit):
    RecyclerView.Adapter<AlbumAdapter.AlbumViewHolder>() {

    var selectedAlbum: String = ImageConstant.ALL_MEDIA_PATH

    var data: List<AlbumEntity> = mutableListOf()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlbumAdapter.AlbumViewHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.gallery_item_album, parent, false)
        return AlbumViewHolder(view)
    }

    override fun onBindViewHolder(holder: AlbumAdapter.AlbumViewHolder, position: Int) {
        val model = data[position]

        holder.cover.load(model.allMedia.getOrNull(0)?.uri)
        holder.albumName.text = model.albumRelativePath.dropLast(1)

        if (model.selectedMedia.size != 0) {
            holder.albumCount.text = "(已选${model.selectedMedia.size}个)${model.allMedia.size}个媒体"
        } else {
            holder.albumCount.text = "${model.allMedia.size}个媒体"
        }

        holder.radioButton.apply {
            isChecked = model.albumRelativePath == selectedAlbum
            setOnClickListener {
                clickAlbum(holder, position)
            }
        }

        holder.itemView.setOnClickListener {
            clickAlbum(holder, position)
        }
    }

    private fun clickAlbum(holder: AlbumViewHolder, position: Int) {
        val model = data[position]

        holder.radioButton.isChecked = true
        selectedAlbum = model.albumRelativePath
        onAlbumClick(model)
        when (position) {
            0 -> {
                notifyItemRangeChanged(1, data.size - 1)
            }
            data.size - 1 -> {
                notifyItemRangeChanged(0, data.size - 1)
            }
            else -> {
                notifyItemRangeChanged(0, position)
                notifyItemRangeChanged(position + 1, data.size - position)
            }
        }
    }

    override fun getItemCount() = data.size

    inner class AlbumViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val cover: ImageView = view.findViewById(R.id.albumCover)
        val albumName: MaterialTextView = view.findViewById(R.id.albumName)
        val albumCount: MaterialTextView = view.findViewById(R.id.albumMediaCount)
        val radioButton: MaterialRadioButton = view.findViewById(R.id.checkbox)
    }
}