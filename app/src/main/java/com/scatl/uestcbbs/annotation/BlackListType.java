package com.scatl.uestcbbs.annotation;

import androidx.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import static com.scatl.uestcbbs.annotation.BlackListType.TYPE_CLOUD;
import static com.scatl.uestcbbs.annotation.BlackListType.TYPE_LOCAL;

/**
 * author: sca_tl
 * date: 2020/11/28 19:43
 * description: 黑名单列表类型，本地或者云端
 */
@StringDef({TYPE_CLOUD, TYPE_LOCAL})
@Retention(RetentionPolicy.SOURCE)
public @interface BlackListType {
    String TYPE_CLOUD = "type_cloud";
    String TYPE_LOCAL = "type_local";
}

