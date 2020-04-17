package com.scatl.uestcbbs.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.scatl.uestcbbs.R;
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
    public static void setLogin(Context context, boolean login, AccountBean accountBean) {
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
    public static void setNightMode(Context context, boolean night) {
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
    public static void setAutoLoadMore(Context context, boolean autoLoadMore) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("settings", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(context.getString(R.string.auto_load_more), autoLoadMore);
        editor.apply();
    }

    public static boolean isAutoLoadMore(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("settings", Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(context.getString(R.string.auto_load_more), true);
    }

    /**
     * author: sca_tl
     * description: 页面大小
     */
    public static void setPageSize(Context context, int pageSize) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("settings", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(context.getString(R.string.page_size), pageSize);
        editor.apply();
    }

    public static int getPageSize(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("settings", Context.MODE_PRIVATE);
        return sharedPreferences.getInt(context.getString(R.string.page_size), 25);
    }

    /**
     * author: sca_tl
     * description: 忽略更新
     */
    public static void setIgnoreVersionCode(Context context, int versionCode) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("settings", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("ignore_version", versionCode);
        editor.apply();
    }

    public static int getIgnoreVersionCode(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("settings", Context.MODE_PRIVATE);
        return sharedPreferences.getInt("ignore_version", 100);
    }

    /**
     * author: sca_tl
     * description: 自定义板块图片
     */
    public static void setBoardImg(Context context, int boardId, String path) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("boardimg", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(String.valueOf(boardId), path);
        editor.apply();
    }

    public static String getBoardImg(Context context, int boardId) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("boardimg", Context.MODE_PRIVATE);
        return sharedPreferences.getString(String.valueOf(boardId), "file:///android_asset/board_img/" + boardId + ".jpg");
    }

    /**
     * author: sca_tl
     * description: 软件灰度
     */
    public static void setGraySaturation(Context context, float saturation) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("settings", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putFloat("graySaturation", saturation);
        editor.apply();
    }

    public static float getGraySaturation(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("settings", Context.MODE_PRIVATE);
        return sharedPreferences.getFloat("graySaturation", 1);
    }

    /**
     * author: sca_tl
     * description: 日夜主题跟随系统
     */
    public static void setUiModeFollowSystem(Context context, boolean followSystem) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("settings", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("ui_mode_follow_system", followSystem);
        editor.apply();
    }

    public static boolean getUiModeFollowSystem(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("settings", Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean("ui_mode_follow_system", false);
    }

}
