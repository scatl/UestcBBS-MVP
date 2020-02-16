package com.scatl.uestcbbs.entity;

import java.util.List;

public class LoginBean {

    public int rs;
    public String errcode;
    public HeadBean head;
    public BodyBean body;
    public int isValidation;
    public String token;
    public String secret;
    public int score;
    public int uid;
    public String userName;
    public String avatar;
    public int gender;
    public String userTitle;
    public String mobile;
    public int groupid;
    public List<?> repeatList;
    public List<?> verify;
    public List<CreditShowListBean> creditShowList;

    public static class HeadBean {

        public String errCode;
        public String errInfo;
        public String version;
        public int alert;
    }

    public static class BodyBean {

        public ExternInfoBean externInfo;

        public static class ExternInfoBean {

            public String padding;

        }
    }

    public static class CreditShowListBean {

        public String type;
        public String title;
        public int data;

    }
}
