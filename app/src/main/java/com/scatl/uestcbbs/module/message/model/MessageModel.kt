package com.scatl.uestcbbs.module.message.model

import com.scatl.uestcbbs.entity.AtMsgBean
import com.scatl.uestcbbs.entity.DianPingMsgBean
import com.scatl.uestcbbs.entity.PrivateChatBean
import com.scatl.uestcbbs.entity.PrivateMsgBean
import com.scatl.uestcbbs.entity.ReplyMeMsgBean
import com.scatl.uestcbbs.entity.SendPrivateMsgResultBean
import com.scatl.uestcbbs.entity.SystemMsgBean
import com.scatl.uestcbbs.entity.UploadResultBean
import com.scatl.uestcbbs.http.BaseBBSResponseBean
import com.scatl.uestcbbs.util.ForumUtil
import com.scatl.uestcbbs.util.RetrofitCookieUtil
import com.scatl.uestcbbs.util.RetrofitUtil
import io.reactivex.Observable
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

/**
 * Created by sca_tl at 2023/6/8 16:28
 */
class MessageModel {
    fun getSystemMsg(page: Int, pageSize: Int): Observable<SystemMsgBean> {
        return RetrofitUtil
            .getInstance()
            .apiService
            .systemMsg(page, pageSize)
    }

    fun getAtMeMsg(page: Int, pageSize: Int): Observable<AtMsgBean> {
        return RetrofitUtil
            .getInstance()
            .apiService
            .atMsg(page, pageSize)
    }

    fun getReplyMsg(page: Int, pageSize: Int): Observable<ReplyMeMsgBean> {
        return RetrofitUtil
            .getInstance()
            .apiService
            .replyMeMsg(page, pageSize)
    }

    fun getPrivateMsg(json: String?): Observable<PrivateMsgBean> {
        return RetrofitUtil
            .getInstance()
            .apiService
            .privateMsg(ForumUtil.getAppHashValue(), json)
    }

    fun getPrivateChatMsgList(json: String?): Observable<PrivateChatBean> {
        return RetrofitUtil
            .getInstance()
            .apiService
            .privateChatMsgList(json)
    }

    fun sendPrivateMsg(json: String?): Observable<SendPrivateMsgResultBean> {
        return RetrofitUtil
            .getInstance()
            .apiService
            .sendPrivateMsg(json)
    }

    fun deleteAllPrivateMsg(uid: Int, formHash: String): Observable<String> {
        val map: MutableMap<String, String> = HashMap()
        map["deletepm_deluid[]"] = uid.toString() + ""
        map["custompage"] = "1"
        map["deletepmsubmit_btn"] = "true"
        map["deletesubmit"] = "true"
        map["formhash"] = formHash

        return RetrofitCookieUtil
            .getInstance()
            .apiService
            .deletePrivateMsg(RetrofitCookieUtil.generateRequestBody(map))
    }

    fun deleteSinglePrivateMsg(pmid: Int, touid: Int, formHash: String?): Observable<String> {
        return RetrofitCookieUtil
            .getInstance()
            .apiService
            .deleteSinglePrivateMsg(formHash, "pmdeletehk_$pmid", touid, pmid)
    }

    fun uploadImages(files: List<File>, module: String, type: String): Observable<UploadResultBean> {
        val params: MutableMap<String, RequestBody> = HashMap()
        params["module"] = module.toRequestBody("".toMediaTypeOrNull())
        params["type"] = type.toRequestBody("".toMediaTypeOrNull())

        val parts: MutableList<MultipartBody.Part> = ArrayList(files.size)
        for (file in files) {
            val requestBody = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
            val part = MultipartBody.Part.createFormData("uploadFile[]", "ImageFile.png", requestBody)
            parts.add(part)
        }

        return RetrofitUtil
            .getInstance()
            .apiService
            .uploadImage(params, parts)
    }

    fun getUserSpace(uid: Int, doo: String?): Observable<String> {
        return RetrofitUtil
            .getInstance()
            .apiService
            .userSpace(uid, doo)
    }

    fun getDianPingMsg(page: Int, pageSize: Int): Observable<DianPingMsgBean> {
        return RetrofitUtil
            .getInstance()
            .apiService
            .dianPingMsg(page, pageSize)
    }
}