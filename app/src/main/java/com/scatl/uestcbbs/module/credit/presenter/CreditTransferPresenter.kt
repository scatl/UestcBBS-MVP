package com.scatl.uestcbbs.module.credit.presenter

import com.scatl.uestcbbs.base.BaseVBPresenter
import com.scatl.uestcbbs.http.Observer
import com.scatl.uestcbbs.module.credit.model.CreditModel
import com.scatl.uestcbbs.module.credit.view.CreditTransferView
import com.scatl.uestcbbs.util.SharePrefUtil
import com.scatl.uestcbbs.util.subscribeEx
import org.jsoup.Jsoup

/**
 * Created by sca_tl at 2023/6/20 14:12
 */
class CreditTransferPresenter: BaseVBPresenter<CreditTransferView>() {

    private var creditModel = CreditModel()

    fun getCreditFormHash() {
        creditModel
            .getFormHash()
            .subscribeEx(Observer<String>().observer {
                onSuccess {
                    if (it.contains("先登录才能")) {
                        mView?.onGetFormHashError("请获取Cookies后再进行本操作")
                    } else {
                        try {
                            val document = Jsoup.parse(it)
                            val formHash = document.select("form[id=scbar_form]").select("input[name=formhash]").attr("value")
                            SharePrefUtil.setForumHash(mView?.getContext(), formHash)
                            mView?.onGetFormHashSuccess(formHash)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }

                onError {
                    mView?.onGetFormHashError("出错了：${it.message}")
                }

                onSubscribe {
                    mCompositeDisposable?.add(it)
                }
            })
    }

    fun creditTransfer(formHash: String, amount: String, happyBoy: String, password: String, message: String) {
        creditModel
            .creditTransfer(formHash, amount, happyBoy, password, message)
            .subscribeEx(Observer<String>().observer {
                onSuccess {
                    if (it.contains("messagetext")) {
                        val msg = Jsoup.parse(it).select("div[id=messagetext]").text()
                        if (it.contains("积分转帐成功")) {
                            mView?.onTransferSuccess("转账成功")
                        } else {
                            mView?.onTransferError(msg)
                        }
                    } else {
                        mView?.onTransferError("出现了一个问题，请查看转账记录确认是否转账成功")
                    }
                }

                onError {
                    mView?.onTransferError("转账失败：${it.message}")
                }

                onSubscribe {
                    mCompositeDisposable?.add(it)
                }
            })
    }

}