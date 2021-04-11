package com.scatl.uestcbbs.annotation;

import androidx.annotation.StringDef;

import static com.scatl.uestcbbs.annotation.PostAppendType.APPEND;
import static com.scatl.uestcbbs.annotation.PostAppendType.DIANPING;

/**
 * author: sca_tl
 * date: 2021/4/6 20:22
 * description:
 */
@StringDef({APPEND, DIANPING})
public @interface PostAppendType {
    String APPEND = "append";
    String DIANPING = "dianping";
}
