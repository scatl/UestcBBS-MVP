package com.scatl.uestcbbs.annotation;

import androidx.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import static com.scatl.uestcbbs.annotation.ResetPswType.TYPE_RESET;
import static com.scatl.uestcbbs.annotation.ResetPswType.TYPE_FIND;

@StringDef({TYPE_RESET, TYPE_FIND})
@Retention(RetentionPolicy.SOURCE)
public @interface ResetPswType {
    String TYPE_RESET = "type_reset";
    String TYPE_FIND = "type_find";
}
