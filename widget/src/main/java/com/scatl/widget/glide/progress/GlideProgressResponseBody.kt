package com.scatl.widget.glide.progress

import android.net.Uri
import okhttp3.ResponseBody
import okio.Buffer
import okio.BufferedSource
import okio.ForwardingSource
import okio.Source
import okio.buffer
import java.io.IOException
import java.lang.ref.WeakReference


/**
 * Created by sca_tl at 2023/5/9 17:02
 */
class GlideProgressResponseBody(val uri: Uri, val responseBody: ResponseBody): ResponseBody() {

    private var listener: ProgressListener? = null
    private var bufferedSource: BufferedSource? = null

    init {
        listener = GlideProgressInterceptor.LISTENERS[uri]
    }

    override fun contentLength() = responseBody.contentLength()

    override fun contentType() = responseBody.contentType()

    override fun source(): BufferedSource {
        if (bufferedSource == null) {
            bufferedSource = ProgressSource(responseBody.source()).buffer()
        }
        return bufferedSource!!
    }

    private inner class ProgressSource constructor(source: Source) : ForwardingSource(source) {
        var totalBytesRead: Long = 0
        var currentProgress = 0

        @Throws(IOException::class)
        override fun read(sink: Buffer, byteCount: Long): Long {
            val bytesRead = super.read(sink, byteCount)
            val fullLength = responseBody.contentLength()
            if (bytesRead == -1L) {
                totalBytesRead = fullLength
            } else {
                totalBytesRead += bytesRead
            }
            val progress = (100f * totalBytesRead / fullLength).toInt()
            if (listener != null && progress != currentProgress) {
                listener?.onProgress(uri, progress)
            }
            if (listener != null && totalBytesRead == fullLength) {
                listener = null
            }
            currentProgress = progress
            return bytesRead
        }
    }

}
