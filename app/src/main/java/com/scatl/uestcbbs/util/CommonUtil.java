package com.scatl.uestcbbs.util;

import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.core.content.FileProvider;
import androidx.fragment.app.FragmentActivity;
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat;

import com.scatl.uestcbbs.callback.OnPermission;
import com.tbruyelle.rxpermissions2.Permission;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.functions.Consumer;

/**
 * author: sca_tl
 * description:
 * date: 2019/11/24 13:46
 */
public class CommonUtil {

    public static int getAttrColor(Context context, int resId) {
        TypedValue typedValue = new TypedValue();
        context.getTheme().resolveAttribute(resId, typedValue, true);
        return context.getColor(typedValue.resourceId);
    }

    /**
     * author: TanLei
     * description: 打开浏览器
     */
    public static void openBrowser(Context context, String url) {
        if (TextUtils.isEmpty(url)) {

            ToastUtil.showToast(context, "链接为空");

        } else {

            try{
                Intent intent= new Intent();
                intent.setAction("android.intent.action.VIEW");
                Uri content_url = Uri.parse(url);
                intent.setData(content_url);
                context.startActivity(intent);
            } catch (ActivityNotFoundException e) {
                e.getMessage();
            }

        }
    }

    /**
     * author: sca_tl
     * description: 安装软件
     */
    public static void installApk(Context context, File apkFile) {

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
            Uri apkUri = FileProvider.getUriForFile(context, "com.scatl.uestcbbs.fileprovider", apkFile);
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
            context.startActivity(intent);
        } else {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.fromFile(apkFile), "application/vnd.android.package-archive");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }
    }

    /**
     * author: sca_tl
     * description: 屏幕dp宽度
     */
    public static int screenDpWidth(Context context) {
        Resources resources = context.getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();
        return px2dip(context, dm.widthPixels);

    }

    /**
     * author: sca_tl
     * description: 屏幕dp宽度
     */
    public static int screenDpHeight(Context context) {
        Resources resources = context.getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();
        return px2dip(context, dm.heightPixels);

    }

    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    public static void hideSoftKeyboard(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (view != null && imm != null){
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public static void showSoftKeyboard(final Context context, final View view, int delayMs) {
        if (view != null) {
            view.postDelayed(() -> {
                view.requestFocus();
                InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) { imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT); }
            }, delayMs);
        }
    }

    /**
     * author: sca_tl
     * description: 获取某个int颜色更深（浅）的颜色
     * @param offSet 偏移量，负代表颜色越深，反之颜色越浅
     */
    public static int getOffsetColor(int offSet, int intColor) {
        int blue = (intColor & 0xff) + offSet;
        int green = ((intColor & 0x00ff00) >> 8) + offSet;
        int red = ((intColor & 0xff0000) >> 16) + offSet;

        return (blue <= 0 || green <= 0 || red <= 0) ? 0: Color.rgb(red, green, blue);
    }

    /**
     * author: sca_tl
     * description: 改变svg图片颜色
     */
    public static void setVectorColor(Context context, ImageView imageView, int drawable, int color) {
        VectorDrawableCompat vectorDrawableCompat = VectorDrawableCompat.create(context.getResources(), drawable, context.getTheme());
        vectorDrawableCompat.setTint(context.getColor(color));
        imageView.setImageDrawable(vectorDrawableCompat);
    }

    public static int getTranslucentColor(float f, int rgb) {
        int blue = rgb & 0xff;
        int green = (rgb & 0x00ff00) >> 8;
        int red = (rgb & 0xff0000) >> 16;
        Log.e("fffffffff", blue + "==" + green + "==" + red);

        int alpha = rgb >>> 24;
        alpha = Math.round(alpha * f);
        return Color.argb(alpha, red, green, blue);
    }

    /**
     * author: sca_tl
     * description: obj转为list，主要是避免编辑器的警告
     */
    public static <T> List<T> objCastList(Object obj, Class<T> clazz) {
        List<T> result = new ArrayList<>();
        if(obj instanceof List<?>) {
            for (Object o : (List<?>) obj) {
                result.add(clazz.cast(o));
            }
            return result;
        }
        return null;
    }

    /**
     * author: sca_tl
     * description: 获取版本号和版本名
     */
    public static int getVersionCode(Context context) {
        try {
            return context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static String getVersionName(Context context) {
        try {
            return context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * author: sca_tl
     * description: 请求权限
     */
    @SuppressLint("CheckResult")
    public static void requestPermission(final FragmentActivity fragmentActivity, final OnPermission onPermission, final String... permissions) {
        new RxPermissions(fragmentActivity)
                .requestEach(permissions)
                .subscribe(permission -> {
                    if (permission.granted) {
                        onPermission.onGranted();
                    } else if (permission.shouldShowRequestPermissionRationale) {
                        onPermission.onRefused();
                    } else {//选中不再询问
                        onPermission.onRefusedWithNoMoreRequest();
                    }
                });
    }

    /**
     * author: sca_tl
     * description: 调用系统下载管理器下载文件
     */
    public static void download(Context context, String url, String name) {
        DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, name);
        request.allowScanningByMediaScanner();
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE
                | DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        if (downloadManager != null) downloadManager.enqueue(request);
    }

    /**
     * author: sca_tl
     * description: toString后的List<String>还原成原来的list
     */
    public static List<String> toList(String toStringList){
        List<String> list = new ArrayList<>();
        if (!toStringList.contains(",")) {
            return list;
        } else {
            String[] b = toStringList.substring(1, toStringList.length() - 1).split(",");
            for (String temp : b){
                list.add(temp.replace(" ", ""));
            }
            return list;
        }
    }

}
