package com.scatl.uestcbbs.entity;

import androidx.annotation.Nullable;

import org.litepal.crud.LitePalSupport;

/**
 * author: sca_tl
 * date: 2020/11/27 18:23
 * description: 本地黑名单数据
 */
public class BlackListBean extends LitePalSupport {
    public int id;
    public String userName;
    public int uid;
    public String avatar;

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj instanceof BlackListBean) {
            return this.uid == ((BlackListBean)obj).uid;
        }
        return false;
    }

    @Override
    public int hashCode() {
        int result = 17;
        result = result * 31 + userName.hashCode();
        result = result * 31 + avatar.hashCode();
        result = result * 31 + String.valueOf(uid).hashCode();
        return result;
    }
}
