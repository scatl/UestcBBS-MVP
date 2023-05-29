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

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
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
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .dns(new OkHttpDns())
                .addInterceptor(chain -> {
                    Request request = chain.request();
                    if (!request.url().toString().contains("r=user/login")) {
                        Request.Builder newBuilder = chain.request().newBuilder();
                        newBuilder.addHeader("Cookie", getCookies());
                        return chain.proceed(newBuilder.build());
                    }
                    return chain.proceed(request);
                })
                .addInterceptor(chain -> {

                    Request request = chain.request();

                    if (request.url().toString().contains(ApiConstant.BBS_BASE_URL)) {
                        HashMap<String, String> addParams = new HashMap<>();

                        addParams.put("apphash", ForumUtil.getAppHashValue());

                        if (request.url().toString().contains("r=forum/topiclist")
                                || request.url().toString().contains("r=portal/newslist")) {
                            if (SharePrefUtil.isShowImgAtTopicList(App.getContext())) {
                                addParams.put("isImageList", "1");
                            }
                            addParams.put("circle", "1");
                        }

                        if (!request.url().toString().contains("r=forum/topicadmin")
                                && !request.url().toString().contains("r=forum/postlist")) {
                            addParams.put("accessToken", getToken());
                            addParams.put("accessSecret", getSecret());
                        }

                        if (request.url().toString().contains("r=forum/postlist")) {
                            addParams.put("bbcode", "1");
                        }

                        Request.Builder requestBuilder = request.newBuilder();

                        if (request.body() instanceof FormBody) {
                            FormBody.Builder newFormBody = new FormBody.Builder();
                            FormBody oldFormBody = (FormBody) request.body();
                            for (int i = 0; i < oldFormBody.size(); i++) {
                                newFormBody.addEncoded(oldFormBody.encodedName(i), oldFormBody.encodedValue(i));
                            }
                            for (Map.Entry<String, String> entry: addParams.entrySet()) {
                                newFormBody.add(entry.getKey(), entry.getValue());
                            }
                            requestBuilder.method(request.method(), newFormBody.build());
                        } else if (request.body() instanceof MultipartBody) {
                            MultipartBody.Builder newMultipartBody = new MultipartBody.Builder();
                            MultipartBody oldFormBody = (MultipartBody) request.body();
                            for (int i = 0; i < oldFormBody.size(); i++) {
                                newMultipartBody.addPart(oldFormBody.part(i));
                            }
                            for (Map.Entry<String, String> entry: addParams.entrySet()) {
                                newMultipartBody.addFormDataPart(entry.getKey(), entry.getValue());
                            }
                            newMultipartBody.setType(oldFormBody.type());
                            requestBuilder.method(request.method(), newMultipartBody.build());
                        } else if ("POST".equalsIgnoreCase(request.method())) {
                            FormBody.Builder newFormBody = new FormBody.Builder();
                            for (Map.Entry<String, String> entry: addParams.entrySet()) {
                                newFormBody.add(entry.getKey(), entry.getValue());
                            }
                            requestBuilder.method(request.method(), newFormBody.build());
                        } else if ("GET".equalsIgnoreCase(request.method())) {
                            String url = request.url().toString();
                            for (Map.Entry<String, String> entry: addParams.entrySet()) {
                                url += ("&" + entry.getKey() + "=" + entry.getValue());
                            }
                            requestBuilder.url(url);
                        }

//                        requestBuilder.removeHeader("User-Agent");
//                        requestBuilder.addHeader("User-Agent", getUserAgent());

                        Request newRequest = requestBuilder.build();
                        return chain.proceed(newRequest);
                    } else {
                        return chain.proceed(request);
                    }
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

    public static String getSecret() {
        return SharePrefUtil.getSecret(App.getContext());
    }

    public static String getToken() {
        return SharePrefUtil.getToken(App.getContext());
    }
}
