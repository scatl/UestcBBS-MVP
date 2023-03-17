package com.scatl.uestcbbs.http;

import androidx.annotation.NonNull;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Dns;

/**
 * created by sca_tl at 2023/3/3 22:11
 */
public class OkHttpDns implements Dns {

    @NonNull
    @Override
    public List<InetAddress> lookup(@NonNull String hostname) {
        List<InetAddress> inetAddressList = new ArrayList<>();
        try {
            InetAddress[] inetAddresses = InetAddress.getAllByName (hostname) ;
            for (InetAddress i: inetAddresses){
                if (i instanceof Inet4Address){
                    inetAddressList.add(0, i) ;
                } else {
                    inetAddressList.add(i);
                }
            }
            return inetAddressList;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return inetAddressList;
    }
}
