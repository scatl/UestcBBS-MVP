package com.scatl.uestcbbs.util;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.util.Log;

import com.scatl.uestcbbs.App;

import java.util.Objects;

/**
 * author: sca_tl
 * date: 2021/9/14 15:54
 * description: 日志打印工具类
 */
public class DebugUtil {

    public static boolean isDebug(Context context) {
        try {
            ApplicationInfo info = context.getApplicationInfo();
            return (info.flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;
        } catch (Exception e) {
            return false;
        }
    }

    public static void e(String tag, String msg) {
        if (isDebug(App.getContext())) {
            Log.e(tag, msg);
        }
    }

    public static void w(String tag, String msg) {
        if (isDebug(App.getContext())) {
            Log.w(tag, msg);
        }
    }

    public static void i(String tag, String msg) {
        if (isDebug(App.getContext())) {
            Log.i(tag, msg);
        }
    }

    public static void d(String tag, Object... msgs) {
        if (isDebug(App.getContext())) {
            String m = "";
            for (Object s: msgs) {
                m = m + s.toString();
            }
            Log.d(tag, m);
        }
    }

    public static void v(String tag, String msg) {
        if (isDebug(App.getContext())) {
            Log.v(tag, msg);
        }
    }
}
