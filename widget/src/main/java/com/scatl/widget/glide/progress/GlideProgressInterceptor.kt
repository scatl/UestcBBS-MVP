package com.scatl.widget.glide.progress

import android.net.Uri
import okhttp3.Interceptor
import okhttp3.Response
import java.lang.ref.WeakReference
import java.util.Collections
import java.util.HashMap

/**
 * Created by sca_tl at 2023/5/9 16:57
 */
class GlideProgressInterceptor: Interceptor {

    companion object{
        val LISTENERS: MutableMap<Uri?, ProgressListener> = Collections.synchronizedMap(HashMap<Uri?, ProgressListener>())
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
    fun onProgress(uri: Uri?, progress: Int)
}