package com.scatl.uestcbbs.util;

import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.UriPermission;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.media.AudioAttributes;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.fragment.app.FragmentActivity;

import com.scatl.uestcbbs.App;
import com.scatl.uestcbbs.annotation.ToastType;
import com.scatl.uestcbbs.callback.OnPermission;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.lang.reflect.Field;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;

/**
 * author: sca_tl
 * description:
 * date: 2019/11/24 13:46
 */
public class CommonUtil {

    /**
     * author: TanLei
     * description: 打开浏览器
     */
    public static void openBrowser(Context context, String url) {
        if (TextUtils.isEmpty(url)) {

            ToastUtil.showToast(context, "链接为空", ToastType.TYPE_ERROR);

        } else {

            try{
                Intent intent= new Intent();
                intent.setAction("android.intent.action.VIEW");
                Uri content_url = Uri.parse(url);
                intent.setData(content_url);
                context.startActivity(intent);
            } catch (ActivityNotFoundException e) {
                ToastUtil.showToast(context, "出错了：" + e.getMessage(), ToastType.TYPE_ERROR);
                e.getMessage();
            }

        }
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
     * description: 分享
     */
    public static void share(Context context, String title, String content) {
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, content);
        shareIntent = Intent.createChooser(shareIntent, title);
        context.startActivity(shareIntent);
    }

    /**
     * author: sca_tl
     * description: toString后的List<String>还原成原来的list
     */
    public static List<String> toList(String toStringList){
        List<String> list = new ArrayList<>();
        if (!toStringList.contains(",")) {
            if (toStringList.equals("[]")) return list;
            list.add(toStringList.replace("[", "").replace("]", ""));
            return list;
        } else {
            String[] b = toStringList.substring(1, toStringList.length() - 1).split(",");
            for (String temp : b){
                list.add(temp.replace(" ", ""));
            }
            return list;
        }
    }


    public static boolean contains(int[] arr, int targetValue) {
        for (int value : arr) {
            if (value == targetValue) {
                return true;
            }
        }
        return false;
    }

}
