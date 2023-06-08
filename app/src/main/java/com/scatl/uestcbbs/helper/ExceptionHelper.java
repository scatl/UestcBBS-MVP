package com.scatl.uestcbbs.helper;

import android.net.ParseException;

import com.google.gson.JsonParseException;

import org.json.JSONException;

import java.io.IOException;
import java.net.ConnectException;
import java.util.Objects;

import okhttp3.ResponseBody;
import retrofit2.HttpException;

/**
 * author: sca_tl
 * description:
 * date: 2019/11/17 12:59
 */
public class ExceptionHelper {
    private static final int UNAUTHORIZED = 401;
    private static final int FORBIDDEN = 403;
    private static final int NOT_FOUND = 404;
    private static final int REQUEST_TIMEOUT = 408;
    private static final int INTERNAL_SERVER_ERROR = 500;
    private static final int BAD_GATEWAY = 502;
    private static final int SERVICE_UNAVAILABLE = 503;
    private static final int GATEWAY_TIMEOUT = 504;
    private static final int FAIL_QUEST = 406;//无法使用请求的内容特性来响应请求的网页
    private static final int BAD_REQUEST = 400;
    private static ResponseBody body;

    public static ResponseThrowable handleException(Throwable e) {
        ResponseThrowable ex;
        if (e instanceof HttpException) {
            HttpException httpException = (HttpException) e;
            ex = new ResponseThrowable(e, ERROR.HTTP_ERROR);
            switch (httpException.code()) {
                case UNAUTHORIZED:
                    ex.message = "401 Error:" + httpException.getMessage();
                    break;
                case FORBIDDEN:
                    ex.message = "403 FORBIDDEN！" + httpException.getMessage();
                    break;
                case NOT_FOUND:
                    ex.message = "404 NOT FOUND:" + httpException.getMessage();
                    break;
                case REQUEST_TIMEOUT:
                    ex.message = "408 Request Timeout";
                    break;
                case GATEWAY_TIMEOUT:
                    ex.message = "504 Error:" + httpException.getMessage();
                case INTERNAL_SERVER_ERROR:

                    try {
                        ex.message = "服务器遇到了一个错误，无法完成对请求的处理：" +
                                Objects.requireNonNull(Objects.requireNonNull(httpException.response()).errorBody()).string();
                    } catch (IOException exc) {
                        exc.printStackTrace();
                    }

                    break;
                case BAD_REQUEST:
                    ex.message = "400 Error:" + httpException.getMessage();
                    break;
                case BAD_GATEWAY:
                    ex.message = "502 Error:" + httpException.getMessage();
                case SERVICE_UNAVAILABLE:
                    ex.message = "503 Error:" + httpException.getMessage();
                case FAIL_QUEST:
                    ex.message = httpException.getMessage();
                    break;
                default:
                    ex.message = e.getMessage();
                    break;
            }
            return ex;
        } else if (e instanceof ServerException) {
            ServerException resultException = (ServerException) e;
            ex = new ResponseThrowable(resultException, resultException.code);
            ex.message = resultException.message;
            return ex;
        } else if (e instanceof JsonParseException || e instanceof JSONException || e instanceof ParseException) {
            ex = new ResponseThrowable(e, ERROR.PARSE_ERROR);
            ex.message = "解析错误："+e.getMessage();
            return ex;
        } else if (e instanceof ConnectException) {
            ex = new ResponseThrowable(e, ERROR.NETWORK_ERROR);
            ex.message = "网络错误:" + e.getMessage();
            return ex;
        } else if (e instanceof javax.net.ssl.SSLHandshakeException) {
            ex = new ResponseThrowable(e, ERROR.SSL_ERROR);
            ex.message = "证书验证失败：" + e.getMessage();
            return ex;
        } else if (e instanceof java.net.SocketTimeoutException) {
            ex = new ResponseThrowable(e, ERROR.TIMEOUT_ERROR);
            ex.message = "连接超时，请稍后再试！";
            e.printStackTrace();
            return ex;
        } else if (e instanceof java.net.UnknownHostException) {
            ex = new ResponseThrowable(e, ERROR.UNKNOWN);
            ex.message = "网络错误，请检查网络连接:"+e.getMessage();
            return ex;
        } else if (e instanceof javax.net.ssl.SSLException) {
            ex = new ResponseThrowable(e, ERROR.SSL_ERROR);
            ex.message = "证书错误！";
            return ex;
        } else if (e instanceof java.io.EOFException) {
            ex = new ResponseThrowable(e, ERROR.PARSE_EmptyERROR);
            ex.message = "No content:" + e.getMessage();
            return ex;
        } else if (e instanceof NullPointerException) {
            ex = new ResponseThrowable(e, ERROR.PARSE_EmptyERROR);
            ex.message = "NullPointerException:" + e.getMessage();
            return ex;
        } else {
            ex = new ResponseThrowable(e, ERROR.UNKNOWN);
            ex.message = e.getMessage();
            return ex;
        }
    }


    /**
     * 约定异常
     */
    public static class ERROR {
        /**
         * 未知错误
         */
        public static final int UNKNOWN = 1000;
        /**
         * 解析错误
         */
        public static final int PARSE_ERROR = 1001;
        /**
         * 解析no content错误
         */
        public static final int PARSE_EmptyERROR = 1007;
        /**
         * 网络错误
         */
        public static final int NETWORK_ERROR = 1002;
        /**
         * 协议出错
         */
        public static final int HTTP_ERROR = 1003;

        /**
         * 证书出错
         */
        public static final int SSL_ERROR = 1005;

        /**
         * 连接超时
         */
        public static final int TIMEOUT_ERROR = 1006;


    }

    public static class ResponseThrowable extends Exception {
        public int code;
        public String message;

        public ResponseThrowable(Throwable throwable, int code) {
            super(throwable);
            this.code = code;
        }

        public ResponseThrowable(String message, int code) {
            this.code = code;
            this.message = message + "";
        }
    }

    public static class ServerException extends RuntimeException {
        public int code;
        public String message;

        public ServerException(int code, String message) {
            this.code = code;
            this.message = message + "";
        }
    }
}
