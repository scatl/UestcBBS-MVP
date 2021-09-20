package com.scatl.uestcbbs.annotation;

import static com.scatl.uestcbbs.annotation.ToastType.TYPE_ERROR;
import static com.scatl.uestcbbs.annotation.ToastType.TYPE_NORMAL;
import static com.scatl.uestcbbs.annotation.ToastType.TYPE_SUCCESS;
import static com.scatl.uestcbbs.annotation.ToastType.TYPE_WARNING;

import androidx.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * author: sca_tl
 * date: 2021/9/19 19:48
 * description:
 */
@StringDef({TYPE_ERROR, TYPE_NORMAL, TYPE_WARNING, TYPE_SUCCESS})
@Retention(RetentionPolicy.SOURCE)
public @interface ToastType {
    String TYPE_ERROR = "error";
    String TYPE_NORMAL = "normal";
    String TYPE_WARNING = "warning";
    String TYPE_SUCCESS = "success";
}
