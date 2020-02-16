package com.scatl.uestcbbs.entity;

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

}
