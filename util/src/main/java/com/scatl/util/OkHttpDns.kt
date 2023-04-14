package com.scatl.util

import okhttp3.Dns
import java.net.Inet4Address
import java.net.InetAddress

/**
 * created by sca_tl at 2023/3/19 17:27
 */
class OkHttpDns: Dns {
    override fun lookup(hostname: String): List<InetAddress> {
        val inetAddressList: MutableList<InetAddress> = ArrayList()
        try {
            val inetAddresses = InetAddress.getAllByName(hostname)
            for (i in inetAddresses) {
                if (i is Inet4Address) {
                    inetAddressList.add(0, i)
                } else {
                    inetAddressList.add(i)
                }
            }
            return inetAddressList
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return inetAddressList
    }
}