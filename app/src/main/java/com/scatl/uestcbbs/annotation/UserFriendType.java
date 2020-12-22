package com.scatl.uestcbbs.annotation;

import androidx.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import static com.scatl.uestcbbs.annotation.UserFriendType.TYPE_FOLLOWED;
import static com.scatl.uestcbbs.annotation.UserFriendType.TYPE_FOLLOW;

@StringDef({TYPE_FOLLOW, TYPE_FOLLOWED})
@Retention(RetentionPolicy.SOURCE)
public @interface UserFriendType {
    String TYPE_FOLLOW = "follow";
    String TYPE_FOLLOWED = "followed";
}
