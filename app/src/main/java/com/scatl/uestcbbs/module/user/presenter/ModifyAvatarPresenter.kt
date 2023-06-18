package com.scatl.uestcbbs.module.user.presenter

import com.scatl.uestcbbs.base.BaseVBPresenter
import com.scatl.uestcbbs.helper.ExceptionHelper.ResponseThrowable
import com.scatl.uestcbbs.helper.rxhelper.Observer
import com.scatl.uestcbbs.module.user.model.UserModel
import com.scatl.uestcbbs.module.user.view.ModifyAvatarView
import io.reactivex.disposables.Disposable
import org.jsoup.Jsoup
import java.net.URLDecoder
import java.util.regex.Pattern

/**
 * Created by sca_tl at 2023/6/14 17:05
 */
class ModifyAvatarPresenter: BaseVBPresenter<ModifyAvatarView>() {

    private val userModel = UserModel()

    fun getParams() {
        userModel.getModifyAvatarParams(object : Observer<String>() {
            override fun OnSuccess(s: String) {
                if (s.contains("需要先登录")) {
                    mView?.onGetParamsError("请先获取Cookies后进行本操作")
                } else {
                    try {
                        val document = Jsoup.parse(s)
                        val elements = document.select("table[class=tfm]")[1].select("tbody").select("tr").select("td").select("script")
                        for (i in elements.indices) {
                            if (elements[i].html().contains("agent")) {
                                val matcher = Pattern.compile("(.*?)&input=(.*?)&agent=(.*?)&ucapi=(.*?)").matcher(elements[i].html())
                                if (matcher.find()) {
                                    //此处需要先解码，防止自动转码导致字符串请求和获取的不一致
                                    val agent = URLDecoder.decode(matcher.group(3), "GBK")
                                    val input = URLDecoder.decode(matcher.group(2), "GBK")
                                    mView?.onGetParamsSuccess(agent, input)
                                } else {
                                    mView?.onGetParamsError("出现了错误，请联系开发者进行反馈")
                                }
                            }
                        }
                    } catch (e: Exception) {
                        mView?.onGetParamsError("获取参数失败：" + e.message)
                    }
                }
            }

            override fun onError(e: ResponseThrowable) {
                mView?.onGetParamsError("获取参数失败：" + e.message)
            }

            override fun OnCompleted() {}

            override fun OnDisposable(d: Disposable) {
                mCompositeDisposable?.add(d)
            }
        })
    }

    fun modifyAvatar(agent: String?, input: String?, avatar1: String?, avatar2: String?, avatar3: String?) {
        userModel.modifyAvatar(agent, input, avatar1, avatar2, avatar3,
            object : Observer<String>() {
                override fun OnSuccess(s: String) {
                    if (s.contains("success")) {
                        mView?.onUploadSuccess("更改头像成功！")
                    } else {
                        mView?.onUploadError("更改头像失败：$s")
                    }
                }

                override fun onError(e: ResponseThrowable) {
                    mView?.onUploadError("更改头像失败：" + e.message)
                }

                override fun OnCompleted() {}

                override fun OnDisposable(d: Disposable) {
                    mCompositeDisposable?.add(d)
                }
            })
    }

}