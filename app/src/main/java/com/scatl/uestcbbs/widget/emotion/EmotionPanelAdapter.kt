package com.scatl.uestcbbs.widget.emotion

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.scatl.uestcbbs.databinding.ItemEmotionPanelBinding
import com.scatl.uestcbbs.util.CommonUtil

/**
 * Created by sca_tl on 2023/1/6 10:38
 */
class EmotionPanelAdapter(val mContext: Context,
                          val mData: ArrayList<ArrayList<String>>): RecyclerView.Adapter<EmotionPanelAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemEmotionPanelBinding.inflate(LayoutInflater.from(mContext), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.mBinding.gridView.apply {
            numColumns = CommonUtil.screenWidth(mContext, true) / 60
            adapter = EmotionGridViewAdapter(
                mContext,
                mData[position]
            )
        }
    }

    override fun getItemCount() = mData.size

    class ViewHolder(binding: ItemEmotionPanelBinding) : RecyclerView.ViewHolder(binding.root) {
        var mBinding = binding
    }
}