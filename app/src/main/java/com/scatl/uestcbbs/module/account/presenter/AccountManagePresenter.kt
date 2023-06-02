package com.scatl.uestcbbs.module.account.presenter

import com.alibaba.fastjson.JSONObject
import com.scatl.uestcbbs.base.BaseVBPresenter
import com.scatl.uestcbbs.helper.ExceptionHelper.ResponseThrowable
import com.scatl.uestcbbs.helper.rxhelper.Observer
import com.scatl.uestcbbs.module.account.model.AccountModel
import com.scatl.uestcbbs.module.account.view.AccountManageView
import com.scatl.uestcbbs.util.SharePrefUtil
import io.reactivex.disposables.Disposable
import org.jsoup.Jsoup
import java.util.regex.Pattern

/**
 * Created by sca_tl at 2023/6/2 9:43
 */
class AccountManagePresenter: BaseVBPresenter<AccountManageView>() {

    private var accountModel = AccountModel()

    fun getRealNameInfo() {
        accountModel.getRealNameInfo(object : Observer<String>() {
            override fun OnSuccess(s: String) {
                if (s.contains("您必须")) {
                    mView?.onGetRealNameInfoError("需要获取Cookies查看实名关联信息，请重新登录")
                } else {
                    try {
                        val document = Jsoup.parse(s)
                        val info = document.select("div[id=messagetext]").select("p")[0].text()
                        mView?.onGetRealNameInfoSuccess(info)
                    } catch (e: Exception) {
                        mView?.onGetRealNameInfoError("查询实名关联信息失败：" + e.message)
                    }
                }
            }

            override fun onError(e: ResponseThrowable) {
                mView?.onGetRealNameInfoError("查询实名关联信息失败：" + e.message)
            }

            override fun OnCompleted() {

            }

            override fun OnDisposable(d: Disposable) {
                mCompositeDisposable?.add(d)
            }
        })
    }

    fun getUploadHash(tid: Int, toast: Boolean) {
        accountModel.getUploadHash(tid, object : Observer<String>() {
            override fun OnSuccess(s: String) {
                try {
                    val document = Jsoup.parse(s)
                    val ss = document.select("div[class=upfl hasfsl]").select("script")
                        .last().html().replace("[\\r\\t\\n\\a]".toRegex(), "")
                    val matcher = Pattern.compile("var upload = new SWFUpload(.*?)post_params: (.*?),file_size_limit ").matcher(ss)
                    if (matcher.find()) {
                        val hash = JSONObject.parseObject(matcher.group(2)).getString("hash")
                        if (hash?.length == 32) {
                            SharePrefUtil.setUploadHash(mView?.getContext(), hash, SharePrefUtil.getName(mView?.getContext()))
                            mView?.onGetUploadHashSuccess(hash, "获取成功！现在你可以上传附件了", toast)
                        } else {
                            mView?.onGetUploadHashError("获取失败：参数值为空或长度不匹配", toast)
                        }
                    } else {
                        mView?.onGetUploadHashError("获取失败，你可以尝试重新获取", toast)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    mView?.onGetUploadHashError("获取失败：${e.message}", toast)
                }
            }

            override fun onError(e: ResponseThrowable) {
                e.printStackTrace()
                mView?.onGetUploadHashError("取hash参数值失败，你可以尝试重新获取：" + e.message, toast)
            }

            override fun OnCompleted() {

            }

            override fun OnDisposable(d: Disposable) {
                mCompositeDisposable?.add(d)
            }
        })
    }
}