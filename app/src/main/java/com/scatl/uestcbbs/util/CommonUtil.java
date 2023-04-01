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

    /**
     * author: sca_tl
     * description: 屏幕宽度
     */
    public static int screenWidth(Context context, boolean withDp) {
        Resources resources = context.getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();
        return withDp ? px2dip(context, dm.widthPixels) : dm.widthPixels;
    }

    /**
     * author: sca_tl
     * description: 屏幕高度
     */
    public static int screenHeight(Context context, boolean withDp) {
        Resources resources = context.getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();
        return withDp ? px2dip(context, dm.heightPixels) : dm.heightPixels;
    }

    public static int dip2px(float dpValue) {
        return dip2px(App.getContext(), dpValue);
    }

    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    public static int sp2px(Context context, float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
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
     * author: TanLei
     * description: 复制文本到剪切板
     */
    public static boolean clipToClipBoard(Context context, String s){
        ClipboardManager clipboardManager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        if (clipboardManager != null) {
            clipboardManager.setPrimaryClip(ClipData.newPlainText("1", s));
            if (clipboardManager.getPrimaryClip() != null) {
                ClipData.Item item= clipboardManager.getPrimaryClip().getItemAt(0);
                return item.getText().toString().equals(s);
            }
        }
        return false;
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

    /**
     * @author: sca_tl
     * @description:
     * @date: 2020/5/3 16:44
     * @param o
     * @return: java.lang.String
     */
    public static String toString(Object o) throws IllegalAccessException {
        if(o!=null) {//判断传过来的对象是否为空
            StringBuilder sb = new StringBuilder(o.getClass().getName() + "[");//定义一个保存数据的变量
            Class cs = o.getClass();//获取对象的类
            Field[] fields = cs.getDeclaredFields();//反射获取该对象里面的所有变量
            for (Field f : fields) {//遍历变量
                f.setAccessible(true);//强制允许访问私有变量
                Object value = f.get(o);//获取传递过来的对象 对应 f 的类型
                value = value == null ? "null" : value;//判断获取到的变量是否为空，如果为空就赋值字符串，否则下面代码会异常
                sb.append(f.getName()).append(":\"").append(value.toString()).append("\" ");// f.getName()：获取变量名；value.toString()：变量值装String
            }
            sb.append("]");
            return sb.toString();
        }else{
            return "null";
        }
    }

    public static String getAppHashValue() {
        try {
            String timeString = String.valueOf(System.currentTimeMillis());
            String authkey = "appbyme_key";
            String authString = timeString.substring(0, 5) + authkey;
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] hashkey = md.digest(authString.getBytes());
            return new BigInteger(1, hashkey).toString(16).substring(8, 16);//16进制转换字符串
        } catch (Exception e) {
            return "";
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

    public static void vibrate(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (context != null) {
                Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
                AudioAttributes audioAttributes = new AudioAttributes
                        .Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_UNKNOWN)
                        .setUsage(AudioAttributes.USAGE_NOTIFICATION_RINGTONE)
                        .build();
                if (vibrator != null && audioAttributes != null) {
                    vibrator.vibrate(VibrationEffect.createOneShot(30, 1), audioAttributes);
                }
            }
        }
    }

}
