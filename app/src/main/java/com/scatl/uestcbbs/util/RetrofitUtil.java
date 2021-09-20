package com.scatl.uestcbbs.util;

import com.google.gson.GsonBuilder;
import com.scatl.uestcbbs.MyApplication;
import com.scatl.uestcbbs.api.ApiConstant;
import com.scatl.uestcbbs.api.ApiService;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class RetrofitUtil {
    private Retrofit retrofit;
    private ApiService apiService;
    private volatile static RetrofitUtil instance;

    public static RetrofitUtil getInstance() {
        if (instance == null) {
            synchronized (RetrofitUtil.class) {
                if (instance == null) {
                    instance = new RetrofitUtil();
                }
            }
        }
        return instance;
    }

    private RetrofitUtil() {
        init();
    }

    private void init() {

        //添加公共参数
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(chain -> {

                    Request request = chain.request();
                    Request.Builder requestBuilder = request.newBuilder();

                    if (request.body() instanceof FormBody) {
                        FormBody.Builder newFormBody = new FormBody.Builder();
                        FormBody oldFormBody = (FormBody) request.body();
                        for (int i = 0; i < oldFormBody.size(); i++) {
                            newFormBody.addEncoded(oldFormBody.encodedName(i), oldFormBody.encodedValue(i));
                        }
                        newFormBody.add("apphash", ForumUtil.getAppHashValue());
//                        newFormBody.add("accessToken", SharePrefUtil.getToken(MyApplication.getContext()));
//                        newFormBody.add("accessSecret", SharePrefUtil.getSecret(MyApplication.getContext()));
                        requestBuilder.method(request.method(), newFormBody.build());
                    }
                    Request newRequest = requestBuilder.build();
                    return chain.proceed(newRequest);
                })
                .connectTimeout(30, TimeUnit.SECONDS)
                .callTimeout(120, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .build();


        retrofit = new Retrofit.Builder()
                .baseUrl(ApiConstant.BBS_BASE_URL)
                .client(okHttpClient)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(new GsonBuilder().create()))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
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
