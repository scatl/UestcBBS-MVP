package com.scatl.uestcbbs.util;

import com.google.gson.GsonBuilder;
import com.scatl.uestcbbs.App;
import com.scatl.uestcbbs.api.ApiConstant;
import com.scatl.uestcbbs.api.ApiService;
import com.scatl.util.common.SSLUtil;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.charset.UnsupportedCharsetException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;
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
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .addInterceptor(chain -> {
                    Request.Builder newBuilder = chain.request().newBuilder();
                    newBuilder.addHeader("Cookie", getCookies());
                    return chain.proceed(newBuilder.build());
                })
//                .addInterceptor(chain -> {
//                    Request request = chain.request();
//                    Response response = chain.proceed(request);
//
//                    if (request.url().toString().contains("topiclist"))
//                    String str = new String(response.body().bytes());
//                    Response.Builder newBuilder = response.newBuilder();
//                    newBuilder.body(ResponseBody.create(response.body().contentType(), str));
//
//                    return newBuilder.build();
//                })
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

                        if (request.url().toString().contains("r=forum/topiclist")) {
                            if (SharePrefUtil.isShowImgAtTopicList(App.getContext())) {
                                newFormBody.add("isImageList", "1");
                            }
                            newFormBody.add("circle", "1");
                        }
                        if (request.url().toString().contains("r=portal/newslist")) {
                            newFormBody.add("circle", "1");
                        }
                        requestBuilder.method(request.method(), newFormBody.build());
                    }
                    Request newRequest = requestBuilder.build();
                    return chain.proceed(newRequest);
                })
                .connectTimeout(30, TimeUnit.SECONDS)
                .callTimeout(120, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS);

        if (SharePrefUtil.isIgnoreSSLVerifier(App.getContext())
                && SSLUtil.getSSLSocketFactory() != null) {
            builder.sslSocketFactory(SSLUtil.getSSLSocketFactory(), SSLUtil.getTrustManager())
                    .hostnameVerifier(SSLUtil.getHostNameVerifier());
        }

        OkHttpClient okHttpClient = builder.build();

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
