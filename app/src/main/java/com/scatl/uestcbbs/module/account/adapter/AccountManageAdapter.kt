package com.scatl.uestcbbs.module.account.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.scatl.uestcbbs.entity.AccountBean
import com.scatl.uestcbbs.helper.PreloadAdapter
import com.scatl.uestcbbs.databinding.ItemAccountManageBinding
import com.scatl.uestcbbs.util.SharePrefUtil
import com.scatl.uestcbbs.util.load

/**
 * Created by sca_tl at 2023/6/2 11:08
 */
class AccountManageAdapter : PreloadAdapter<AccountBean, ItemAccountManageBinding>() {

    var currentLoginUid: Int = 0

    override fun getViewBinding(parent: ViewGroup): ItemAccountManageBinding {
        return ItemAccountManageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int, item: AccountBean?) {
        super.onBindViewHolder(holder, position, item)
        if (item == null) {
            return
        }
        val binding = holder.binding as ItemAccountManageBinding

        binding.name.text = item.userName
        binding.cookiesStatus.text = if (SharePrefUtil.isSuperLogin(context, item.userName)
            && !SharePrefUtil.getCookies(context, item.userName).isNullOrEmpty()) {
            "已获取Cookies"
        } else {
            "未获取Cookies，部分功能受限"
        }
        binding.radioBtn.isChecked = currentLoginUid == item.uid
        binding.realName.visibility = if (currentLoginUid == item.uid) View.VISIBLE else View.GONE
        binding.avatar.load(item.avatar)
    }
}