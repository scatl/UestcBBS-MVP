package com.scatl.util

import java.security.SecureRandom
import java.security.cert.X509Certificate
import javax.net.ssl.*

/**
 * created by sca_tl at 2022/9/12 9:18
 */
object SSLUtil {

    @JvmStatic
    fun getSSLSocketFactory(): SSLSocketFactory {
        return SSLContext.getInstance("SSL").run {
            init(null, arrayOf(getTrustManager()), SecureRandom())
            socketFactory
        }
    }

    @JvmStatic
    fun getTrustManager(): X509TrustManager {
        return object : X509TrustManager {
            override fun checkClientTrusted(chain: Array<X509Certificate?>?, authType: String?) {}
            override fun checkServerTrusted(chain: Array<X509Certificate?>?, authType: String?) {}
            override fun getAcceptedIssuers(): Array<X509Certificate> = arrayOf()
        }
    }

    @JvmStatic
    fun getHostNameVerifier() = HostnameVerifier { hostname, session -> true }

}