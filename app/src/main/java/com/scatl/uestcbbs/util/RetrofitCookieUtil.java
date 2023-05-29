package com.scatl.uestcbbs.util;

import com.google.gson.GsonBuilder;
import com.scatl.uestcbbs.App;
import com.scatl.uestcbbs.api.ApiConstant;
import com.scatl.uestcbbs.api.ApiService;
import com.scatl.util.OkHttpDns;
import com.scatl.util.SSLUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/**
 * author: sca_tl
 * date: 2020/5/17 12:04
 * description:
 */
public class RetrofitCookieUtil {
    private Retrofit retrofit;
    private ApiService apiService;
    private volatile static RetrofitCookieUtil instance;

    public static RetrofitCookieUtil getInstance() {
        if (instance == null) {
            synchronized (RetrofitCookieUtil.class) {
                if (instance == null) {
                    instance = new RetrofitCookieUtil();
                }
            }
        }
        return instance;
    }

    private RetrofitCookieUtil() {
        init();
    }

    private void init() {

        OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder()
                .dns(new OkHttpDns())
                .addInterceptor(chain -> {
                    Request.Builder builder = chain.request().newBuilder();
                    builder.addHeader("Cookie", getCookies());

//                    builder.removeHeader("User-Agent");
//                    builder.addHeader("User-Agent", getUserAgent());

                    return chain.proceed(builder.build());
                })
                .connectTimeout(30, TimeUnit.SECONDS)
                .callTimeout(120, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS);

        if (SharePrefUtil.isIgnoreSSLVerifier(App.getContext())
                && SSLUtil.getSSLSocketFactory() != null) {
            clientBuilder.sslSocketFactory(SSLUtil.getSSLSocketFactory(), SSLUtil.getTrustManager())
                    .hostnameVerifier(SSLUtil.getHostNameVerifier());
        }

        OkHttpClient okHttpClient = clientBuilder.build();

        retrofit = new Retrofit.Builder()
                .baseUrl(ApiConstant.BBS_BASE_URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(new GsonBuilder().create()))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(okHttpClient)
                .build();
        apiService = retrofit.create(ApiService.class);
    }

    public ApiService getApiService(){
        return apiService;
    }


    public static Map<String, RequestBody> generateRequestBody(Map<String, String> requestDataMap) {
        Map<String, RequestBody> requestBodyMap = new HashMap<>();
        for (String key : requestDataMap.keySet()) {
            RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), requestDataMap.get(key) == null ? "" : requestDataMap.get(key));
            requestBodyMap.put(key, requestBody);
        }
        return requestBodyMap;
    }

    public static String getCookies() {
        if (SharePrefUtil.isLogin(App.getContext()) &&
                SharePrefUtil.getName(App.getContext()) != null &&
                SharePrefUtil.isSuperLogin(App.getContext(), SharePrefUtil.getName(App.getContext()))) {
            Set<String> preferences = SharePrefUtil.getCookies(App.getContext(), SharePrefUtil.getName(App.getContext()));
            StringBuilder stringBuilder = new StringBuilder();
            if (preferences != null && preferences.size() != 0) {
                for (String cookie : preferences) {
                    stringBuilder.append(cookie).append(";");
                }
            }
            return stringBuilder.toString();
        }
        return "";
    }
}
