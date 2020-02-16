package com.scatl.uestcbbs.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.scatl.uestcbbs.entity.AccountBean;
import com.scatl.uestcbbs.entity.LoginBean;

/**
 * author: sca_tl
 * description:
 * date: 2019/11/16 15:11
 */
public class SharePrefUtil {

    /**
     * author: sca_tl
     * description: 登陆，注销
     */
    public static void setLogin(Context context, boolean login, AccountBean accountBean){
        SharedPreferences sharedPreferences = context.getSharedPreferences("account", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("login", login);
        editor.putString("name", accountBean.userName);
        editor.putInt("uid", accountBean.uid);
        editor.putString("avatar", accountBean.avatar);
        editor.putString("token", accountBean.token);
        editor.putString("secret", accountBean.secret);
        editor.apply();
    }

    public static boolean isLogin(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("account", Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean("login", false);
    }

    public static int getUid(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("account", Context.MODE_PRIVATE);
        return sharedPreferences.getInt("uid", Integer.MAX_VALUE);
    }

    public static String getToken(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("account", Context.MODE_PRIVATE);
        return sharedPreferences.getString("token", "");
    }

    public static String getSecret(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("account", Context.MODE_PRIVATE);
        return sharedPreferences.getString("secret", "");
    }

    public static String getName(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("account", Context.MODE_PRIVATE);
        return sharedPreferences.getString("name", "");
    }

    public static String getAvatar(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("account", Context.MODE_PRIVATE);
        return sharedPreferences.getString("avatar", "");
    }


    /**
     * author: sca_tl
     * description: 夜间模式
     */
    public static void setNightMode(Context context, boolean night){
        SharedPreferences sharedPreferences = context.getSharedPreferences("settings", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("night_mode", night);
        editor.apply();
    }

    public static boolean isNightMode(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("settings", Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean("night_mode", false);
    }

    /**
     * author: sca_tl
     * description: 自动加载
     */
    public static void setAutoLoadMore(Context context, boolean autoLoadMore){
        SharedPreferences sharedPreferences = context.getSharedPreferences("settings", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("auto_load_more", autoLoadMore);
        editor.apply();
    }

    public static boolean isAutoLoadMore(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("settings", Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean("auto_load_more", true);
    }
}
