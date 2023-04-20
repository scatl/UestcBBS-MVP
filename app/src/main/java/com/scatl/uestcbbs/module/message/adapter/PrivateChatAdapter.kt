package com.scatl.uestcbbs.module.message.adapter

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.drawable.GradientDrawable
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.shape.ShapeAppearanceModel
import com.scatl.uestcbbs.R
import com.scatl.uestcbbs.entity.PrivateChatBean.BodyBean.PmListBean.MsgListBean
import com.scatl.uestcbbs.util.Constant
import com.scatl.uestcbbs.util.SharePrefUtil
import com.scatl.uestcbbs.util.TimeUtil
import com.scatl.uestcbbs.util.load
import com.scatl.uestcbbs.widget.textview.EmojiTextView
import com.scatl.util.ColorUtil
import com.scatl.util.NumberUtil
import com.scatl.util.ScreenUtil

/**
 * Created by sca_tl at 2023/3/30 9:24
 */
class PrivateChatAdapter(layoutResId: Int) : BaseQuickAdapter<MsgListBean, BaseViewHolder>(layoutResId) {

    fun insertMsg(context: Context, content: String?, type: String) {
        val msgBean = MsgListBean().apply {
            this.type = type
            this.sender = SharePrefUtil.getUid(context)
            this.time = System.currentTimeMillis().toString()
            this.content = content
        }
        addData(msgBean)
    }

    fun deleteMsg(position: Int) {
        try {
            data.removeAt(position)
            notifyItemRemoved(position)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun convert(helper: BaseViewHolder, item: MsgListBean) {
        if (item.sender == SharePrefUtil.getUid(mContext)) {
            helper.getView<View>(R.id.his_content_layout).visibility = View.GONE
            helper.getView<View>(R.id.mine_content_layout).visibility = View.VISIBLE
            setMineContent(helper, item)
        } else {
            helper.getView<View>(R.id.his_content_layout).visibility = View.VISIBLE
            helper.getView<View>(R.id.mine_content_layout).visibility = View.GONE
            setHisContent(helper, item)
        }
    }

    private fun setMineContent(helper: BaseViewHolder, item: MsgListBean) {
        helper.addOnClickListener(R.id.mine_img_content)
        helper.addOnClickListener(R.id.mine_icon)

        val icon = helper.getView<ImageView>(R.id.mine_icon)
        val bg = helper.getView<View>(R.id.mine_content_bg)
        val textContent = helper.getView<EmojiTextView>(R.id.mine_text_content)
        val imgContent = helper.getView<ShapeableImageView>(R.id.mine_img_content)
        val time = helper.getView<TextView>(R.id.mine_time)

        icon.load(Constant.USER_AVATAR_URL.plus(item.sender))
        time.text = TimeUtil.getFormatDate(NumberUtil.parseLong(item.time), "yyyy-MM-dd HH:mm")

        val cornerR = ScreenUtil.dip2pxF(mContext, 15f)
        bg.background = GradientDrawable().apply {
            setColor(ColorUtil.getAttrColor(mContext, R.attr.colorPrimaryContainer))
            cornerRadii = floatArrayOf(cornerR, cornerR, 0f, 0f, cornerR, cornerR, cornerR, cornerR)
        }

        if (item.type == "text") {
            textContent.visibility = View.VISIBLE
            imgContent.visibility = View.GONE
            textContent.setText(item.content)
            bg.backgroundTintList = ColorStateList.valueOf(ColorUtil.getAttrColor(mContext, R.attr.colorPrimaryContainer))
        } else if (item.type == "image") {
            textContent.visibility = View.GONE
            imgContent.visibility = View.VISIBLE
            bg.backgroundTintList = ColorStateList.valueOf(ColorUtil.getAttrColor(mContext, R.attr.colorSurface))
            imgContent.load(item.content)
            imgContent.shapeAppearanceModel = ShapeAppearanceModel.builder().apply {
                setTopLeftCornerSize(cornerR)
                setTopRightCornerSize(0f)
                setBottomLeftCornerSize(0f)
                setBottomRightCornerSize(0f)
            }.build()
        }
    }

    private fun setHisContent(helper: BaseViewHolder, item: MsgListBean) {
        helper.addOnClickListener(R.id.his_img_content)
        helper.addOnClickListener(R.id.his_icon)

        val icon = helper.getView<ImageView>(R.id.his_icon)
        val bg = helper.getView<View>(R.id.his_content_bg)
        val textContent = helper.getView<EmojiTextView>(R.id.his_text_content)
        val imgContent = helper.getView<ShapeableImageView>(R.id.his_img_content)
        val time = helper.getView<TextView>(R.id.his_time)

        icon.load(Constant.USER_AVATAR_URL.plus(item.sender))
        time.text = TimeUtil.getFormatDate(NumberUtil.parseLong(item.time), "yyyy-MM-dd HH:mm")

        val cornerR = ScreenUtil.dip2pxF(mContext, 15f)
        bg.background = GradientDrawable().apply {
            setColor(ColorUtil.getAttrColor(mContext, R.attr.colorSurface))
            cornerRadii = floatArrayOf(0f, 0f, cornerR, cornerR, cornerR, cornerR, cornerR, cornerR)
        }

        if (item.type == "text") {
            textContent.visibility = View.VISIBLE
            imgContent.visibility = View.GONE
            textContent.setText(item.content)
        } else if (item.type == "image") {
            textContent.visibility = View.GONE
            imgContent.visibility = View.VISIBLE
            imgContent.load(item.content)
            imgContent.shapeAppearanceModel = ShapeAppearanceModel.builder().apply {
                setTopLeftCornerSize(0f)
                setTopRightCornerSize(cornerR)
                setBottomLeftCornerSize(0f)
                setBottomRightCornerSize(0f)
            }.build()
        }
    }
}