package com.scatl.uestcbbs.entity;

import androidx.annotation.Nullable;

import org.litepal.crud.LitePalSupport;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class AccountBean extends LitePalSupport implements Serializable {

    public int id;
    public boolean isLogin;
    public String token;
    public String secret;
    public int uid;
    public String userName;
    public String avatar;

    @Override
    public int hashCode() {
        int result = 17;
        result = result * 31 + userName.hashCode();
        result = result * 31 + avatar.hashCode();
        result = result * 31 + String.valueOf(uid).hashCode();
        return result;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj instanceof AccountBean) {
            return this.uid == ((AccountBean)obj).uid;
        }
        return false;
    }
}
