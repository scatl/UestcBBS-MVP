package com.scatl.uestcbbs.module.account.presenter

import android.content.Context
import com.alibaba.fastjson.JSONObject
import com.scatl.uestcbbs.api.ApiConstant
import com.scatl.uestcbbs.base.BaseVBPresenter
import com.scatl.uestcbbs.entity.LoginBean
import com.scatl.uestcbbs.helper.ExceptionHelper.ResponseThrowable
import com.scatl.uestcbbs.helper.rxhelper.Observer
import com.scatl.uestcbbs.module.account.model.AccountModel
import com.scatl.uestcbbs.module.account.view.LoginView
import com.scatl.uestcbbs.util.SharePrefUtil
import com.scatl.uestcbbs.util.isNullOrEmpty
import io.reactivex.disposables.Disposable
import okhttp3.ResponseBody
import retrofit2.Response
import java.io.IOException

/**
 * Created by sca_tl at 2023/6/2 14:11
 */
class LoginPresenter: BaseVBPresenter<LoginView>() {

    private val accountModel = AccountModel()

    fun login(context: Context?, userName: String?, userPsw: String?) {
        accountModel.login(userName, userPsw, object : Observer<Response<ResponseBody?>?>() {
            override fun OnSuccess(response: Response<ResponseBody?>?) {
                try {
                    if (response?.body() != null) {
                        val res = response.body()?.string()
                        val loginBean = JSONObject.parseObject(res, LoginBean::class.java)
                        if (loginBean.rs == ApiConstant.Code.SUCCESS_CODE) {
                            mView?.onLoginSuccess(loginBean)
                        } else {
                            mView?.onLoginError("登录失败：${loginBean.head.errInfo}")
                        }
                    }
                    val cookies = response?.headers()?.values("Set-Cookie")?.let { HashSet(it) }
                    if (!cookies.isNullOrEmpty()) {
                        SharePrefUtil.setCookies(context, cookies, userName)
                        SharePrefUtil.setSuperAccount(context, true, userName)
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                    mView?.onLoginError("登录失败：${e.message}")
                }
            }

            override fun onError(e: ResponseThrowable) {
                mView?.onLoginError("登录失败：${e.message}")
            }

            override fun OnCompleted() {

            }

            override fun OnDisposable(d: Disposable) {
                mCompositeDisposable?.add(d)
            }
        })
    }

}