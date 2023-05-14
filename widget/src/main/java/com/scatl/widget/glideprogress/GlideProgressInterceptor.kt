package com.scatl.widget.glideprogress

import android.net.Uri
import okhttp3.Interceptor
import okhttp3.Response
import java.lang.ref.WeakReference

/**
 * Created by sca_tl at 2023/5/9 16:57
 */
class GlideProgressInterceptor: Interceptor {

    companion object{
        val LISTENERS = mutableMapOf<Uri?, WeakReference<ProgressListener>>()
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val response = chain.proceed(request)
        val url = request.url.toString()
        val body = response.body
        return if (body != null) {
            response.newBuilder().body(GlideProgressResponseBody(Uri.parse(url), body)).build()
        } else {
            response
        }
    }
}

interface ProgressListener {
    fun onProgress(progress: Int)
}