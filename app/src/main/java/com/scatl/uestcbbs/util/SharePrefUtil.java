package com.scatl.uestcbbs.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.scatl.uestcbbs.R;
import com.scatl.uestcbbs.entity.AccountBean;

import java.util.HashSet;
import java.util.Set;

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

    public static boolean isUiModeFollowSystem(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("settings", Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean("ui_mode_follow_system", false);
    }

    public static int getBoardListColumns(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("settings", Context.MODE_PRIVATE);
        return sharedPreferences.getInt(context.getString(R.string.board_list_columns), 3);
    }

    public static void setCookies(Context context, HashSet<String> cookies, String userName) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("cookies", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putStringSet("cookies-" + userName, cookies);
        editor.apply();
    }

    public static Set<String> getCookies(Context context, String userName) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("cookies", Context.MODE_PRIVATE);
        return sharedPreferences.getStringSet("cookies-" + userName, new HashSet<>());
    }

    public static void setSuperAccount(Context context, boolean isSuperAccount, String userName) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("cookies", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("superLogin-" + userName, isSuperAccount);
        editor.apply();
    }

    public static boolean isSuperLogin(Context context, String userName) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("cookies", Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean("superLogin-" + userName, false);
    }

    public static void setUploadHash(Context context, String hash, String userName) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("cookies", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("uploadHash-" + userName, hash);
        editor.apply();
    }

    public static String getUploadHash(Context context, String userName) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("cookies", Context.MODE_PRIVATE);
        return sharedPreferences.getString("uploadHash-" + userName, "");
    }

    public static boolean isShowHomeBanner(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("settings", Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean("show_home_banner", true);
    }

    public static int getHotCommentZanThreshold(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("settings", Context.MODE_PRIVATE);
        return sharedPreferences.getInt(context.getString(R.string.hot_comment_zan_threshold), 5);
    }

    public static void setNewShowOpenPicId(Context context, int id) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("settings", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("never_show_pic_id", id);
        editor.apply();
    }

    public static int getNewShowOpenPicId(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("settings", Context.MODE_PRIVATE);
        return sharedPreferences.getInt("never_show_pic_id", 0);
    }

    public static boolean isOpenLinkByInternalBrowser(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("settings", Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(context.getString(R.string.open_link_by_internal_browser), false);
    }

    public static boolean isCloseTopStickPost(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("settings", Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(context.getString(R.string.close_all_site_top_stick_post), false);
    }

    public static boolean isShowImgAtTopicList(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("settings", Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(context.getString(R.string.show_imgs_at_top_list), false);
    }

    public static void setForumHash(Context context, String forumHash) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("settings", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("forumhash", forumHash);
        editor.apply();
    }

    public static String getForumHash(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("settings", Context.MODE_PRIVATE);
        return sharedPreferences.getString("forumhash", "");
    }

    public static int getKeyBoardHeight(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("keyboard", Context.MODE_PRIVATE);
        return sharedPreferences.getInt("height", 500);
    }

    public static boolean isThemeFollowWallpaper(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("settings", Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(context.getString(R.string.theme_follow_wallpaper), true);
    }

    public static boolean isIgnoreSSLVerifier(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("settings", Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(context.getString(R.string.ignore_ssl_verifier), false);
    }

    public static boolean clearDraftAfterPostSuccess(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("settings", Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(context.getString(R.string.clear_draft_after_post_success), true);
    }

    public static boolean isOpenCollectionUpdateNotification(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("settings", Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(context.getString(R.string.collection_update_notification), false);
    }

    public static void setShowOnceDialogId(Context context, String id) {
        Set<String> ids = new HashSet<>(getShowOnceDialogId(context));
        ids.add(id);
        SharedPreferences sharedPreferences = context.getSharedPreferences("settings", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putStringSet("showOnceDialogIds", ids);
        editor.apply();
    }

    public static Set<String> getShowOnceDialogId(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("settings", Context.MODE_PRIVATE);
        return sharedPreferences.getStringSet("showOnceDialogIds", new HashSet<>());
    }

}
