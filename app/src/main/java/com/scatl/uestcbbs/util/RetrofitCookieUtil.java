package com.scatl.uestcbbs.util;

import android.util.Log;
import android.webkit.WebViewClient;
import android.widget.RadioButton;
import android.widget.Toolbar;

import androidx.viewpager.widget.ViewPager;

import com.google.gson.GsonBuilder;
import com.scatl.uestcbbs.MyApplication;
import com.scatl.uestcbbs.api.ApiConstant;
import com.scatl.uestcbbs.api.ApiService;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.WebSocket;
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

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(chain -> {
                    Request.Builder builder = chain.request().newBuilder();
                    if (SharePrefUtil.isLogin(MyApplication.getContext()) &&
                            SharePrefUtil.getName(MyApplication.getContext()) != null &&
                            SharePrefUtil.isSuperLogin(MyApplication.getContext(), SharePrefUtil.getName(MyApplication.getContext()))) {
                        Set<String> preferences = SharePrefUtil.getCookies(MyApplication.getContext(), SharePrefUtil.getName(MyApplication.getContext()));
                        StringBuilder stringBuilder = new StringBuilder();
                        if (preferences != null && preferences.size() != 0) {
                            for (String cookie : preferences) {
                                stringBuilder.append(cookie).append(";");
                            }
                        }
                        builder.addHeader("Cookie", stringBuilder.toString());
                    }

                    return chain.proceed(builder.build());
                })
                .connectTimeout(30, TimeUnit.SECONDS)
                .callTimeout(120, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .build();

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
}
