package com.scatl.uestcbbs.module.message.adapter

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.shape.ShapeAppearanceModel
import com.scatl.uestcbbs.R
import com.scatl.uestcbbs.databinding.ItemPrivateChatBinding
import com.scatl.uestcbbs.entity.PrivateChatBean.BodyBean.PmListBean.MsgListBean
import com.scatl.uestcbbs.helper.PreloadAdapter
import com.scatl.uestcbbs.helper.ViewBindingHolder
import com.scatl.uestcbbs.util.Constant
import com.scatl.uestcbbs.util.SharePrefUtil
import com.scatl.uestcbbs.util.TimeUtil
import com.scatl.uestcbbs.util.load
import com.scatl.util.ColorUtil
import com.scatl.util.NumberUtil
import com.scatl.util.ScreenUtil

/**
 * Created by sca_tl at 2023/3/30 9:24
 */
class PrivateChatAdapter : PreloadAdapter<MsgListBean, ItemPrivateChatBinding>() {

    override fun getViewBinding(parent: ViewGroup): ItemPrivateChatBinding {
        return ItemPrivateChatBinding.inflate(LayoutInflater.from(context), parent, false)
    }

    fun insertMsg(context: Context, content: String?, type: String) {
        val msgBean = MsgListBean().apply {
            this.type = type
            this.sender = SharePrefUtil.getUid(context)
            this.time = System.currentTimeMillis().toString()
            this.content = content
        }
        add(msgBean)
    }

    override fun onBindViewHolder(holder: ViewBindingHolder<ItemPrivateChatBinding>, position: Int, item: MsgListBean?) {
        super.onBindViewHolder(holder, position, item)
        if (item == null) {
            return
        }
        if (item.sender == SharePrefUtil.getUid(context)) {
            holder.binding.hisContentLayout.visibility = View.GONE
            holder.binding.mineContentLayout.visibility = View.VISIBLE
            setMineContent(holder, item)
        } else {
            holder.binding.hisContentLayout.visibility = View.VISIBLE
            holder.binding.mineContentLayout.visibility = View.GONE
            setHisContent(holder, item)
        }
    }

    private fun setMineContent(holder: ViewBindingHolder<ItemPrivateChatBinding>, item: MsgListBean) {
        holder.binding.mineIcon.load(Constant.USER_AVATAR_URL.plus(item.sender))
        holder.binding.mineTime.text = TimeUtil.getFormatDate(NumberUtil.parseLong(item.time), "yyyy-MM-dd HH:mm")

        val cornerR = ScreenUtil.dip2pxF(context, 15f)
        holder.binding.mineContentBg.background = GradientDrawable().apply {
            setColor(ColorUtil.getAttrColor(context, R.attr.colorPrimaryContainer))
            cornerRadii = floatArrayOf(cornerR, cornerR, 0f, 0f, cornerR, cornerR, cornerR, cornerR)
        }

        if (item.type == "text") {
            holder.binding.mineTextContent.visibility = View.VISIBLE
            holder.binding.mineImgContent.visibility = View.GONE
            holder.binding.mineTextContent.setText(item.content)
            holder.binding.mineContentBg.backgroundTintList = ColorStateList.valueOf(ColorUtil.getAttrColor(context, R.attr.colorPrimaryContainer))
        } else if (item.type == "image") {
            holder.binding.mineTextContent.visibility = View.GONE
            holder.binding.mineImgContent.visibility = View.VISIBLE
            holder.binding.mineContentBg.backgroundTintList = ColorStateList.valueOf(ColorUtil.getAttrColor(context, R.attr.colorSurface))
            holder.binding.mineImgContent.load(item.content)
            holder.binding.mineImgContent.shapeAppearanceModel = ShapeAppearanceModel.builder().apply {
                setTopLeftCornerSize(cornerR)
                setTopRightCornerSize(0f)
                setBottomLeftCornerSize(0f)
                setBottomRightCornerSize(0f)
            }.build()
        }
    }

    private fun setHisContent(holder: ViewBindingHolder<ItemPrivateChatBinding>, item: MsgListBean) {
        holder.binding.hisIcon.load(Constant.USER_AVATAR_URL.plus(item.sender))
        holder.binding.hisTime.text = TimeUtil.getFormatDate(NumberUtil.parseLong(item.time), "yyyy-MM-dd HH:mm")

        val cornerR = ScreenUtil.dip2pxF(context, 15f)
        holder.binding.hisContentBg.background = GradientDrawable().apply {
            setColor(ColorUtil.getAttrColor(context, R.attr.colorSurface))
            cornerRadii = floatArrayOf(0f, 0f, cornerR, cornerR, cornerR, cornerR, cornerR, cornerR)
        }

        if (item.type == "text") {
            holder.binding.hisTextContent.visibility = View.VISIBLE
            holder.binding.hisImgContent.visibility = View.GONE
            holder.binding.hisTextContent.setText(item.content)
        } else if (item.type == "image") {
            holder.binding.hisTextContent.visibility = View.GONE
            holder.binding.hisImgContent.visibility = View.VISIBLE
            holder.binding.hisImgContent.load(item.content)
            holder.binding.hisImgContent.shapeAppearanceModel = ShapeAppearanceModel.builder().apply {
                setTopLeftCornerSize(0f)
                setTopRightCornerSize(cornerR)
                setBottomLeftCornerSize(0f)
                setBottomRightCornerSize(0f)
            }.build()
        }
    }
}