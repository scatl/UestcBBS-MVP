package com.scatl.uestcbbs.util;

import android.content.Context;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.scatl.uestcbbs.annotation.ToastType;

import es.dmoral.toasty.Toasty;

public class ToastUtil {
    public static void showToast(Context context, String msg, @ToastType String type) {
        if (context == null || msg == null) {
            return;
        }
        switch (type) {
            case ToastType.TYPE_ERROR:
                Toasty.error(context, msg, Toasty.LENGTH_LONG, true).show();
                break;
            case ToastType.TYPE_WARNING:
                Toasty.warning(context, msg, Toasty.LENGTH_LONG, true).show();
                break;
            case ToastType.TYPE_SUCCESS:
                Toasty.success(context, msg, Toasty.LENGTH_LONG, true).show();
                break;
            default:
                Toasty.normal(context, msg, Toasty.LENGTH_LONG).show();
                break;
        }
    }

    public static void showSnackBar(View view, String msg) {
        Snackbar.make(view, msg, Snackbar.LENGTH_SHORT).show();
    }
}
