package com.scatl.uestcbbs.module.account.adapter

import android.view.View
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.TextView
import com.chad.library.adapter.base.BaseViewHolder
import com.scatl.uestcbbs.entity.AccountBean
import com.scatl.uestcbbs.helper.PreloadAdapter
import com.scatl.uestcbbs.util.SharePrefUtil
import com.scatl.uestcbbs.R
import com.scatl.uestcbbs.util.isNullOrEmpty
import com.scatl.uestcbbs.util.load

/**
 * Created by sca_tl at 2023/6/2 11:08
 */
class AccountManageAdapter(layoutResId: Int, onPreload: (() -> Unit)? = null) :
    PreloadAdapter<AccountBean, BaseViewHolder>(layoutResId, onPreload) {

    var currentLoginUid: Int = 0

    override fun convert(helper: BaseViewHolder, item: AccountBean) {
        helper
            .addOnClickListener(R.id.delete_btn)
            .addOnClickListener(R.id.real_name)

        val radioButton = helper.getView<RadioButton>(R.id.radio_btn)
        val name = helper.getView<TextView>(R.id.name)
        val cookieStatus = helper.getView<TextView>(R.id.cookies_status)
        val realName = helper.getView<ImageView>(R.id.real_name)
        val avatar = helper.getView<ImageView>(R.id.avatar)

        name.text = item.userName
        cookieStatus.text = if (SharePrefUtil.isSuperLogin(mContext, item.userName)
            && !SharePrefUtil.getCookies(mContext, item.userName).isNullOrEmpty()) {
            "已获取Cookies"
        } else {
            "未获取Cookies，部分功能受限"
        }
        radioButton.isChecked = currentLoginUid == item.uid
        realName.visibility = if (currentLoginUid == item.uid) View.VISIBLE else View.GONE
        avatar.load(item.avatar)
    }
}