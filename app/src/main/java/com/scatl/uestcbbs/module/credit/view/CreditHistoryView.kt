package com.scatl.uestcbbs.module.credit.view

import com.scatl.uestcbbs.base.BaseView
import com.scatl.uestcbbs.entity.MineCreditBean.CreditHistoryBean

interface CreditHistoryView: BaseView {
    fun onGetMineCreditHistorySuccess(creditHistoryBeans: List<CreditHistoryBean>, hasNext: Boolean)
    fun onGetMineCreditHistoryError(msg: String?)
}