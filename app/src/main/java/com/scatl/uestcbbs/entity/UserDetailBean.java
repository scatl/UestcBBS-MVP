package com.scatl.uestcbbs.entity;

import java.util.List;

/**
 * author: sca_tl
 * description:
 * date: 2020/2/3 12:58
 */
public class UserDetailBean {

    public int rs;
    public String errcode;
    public HeadBean head;
    public BodyBean body;
    public int flag;
    public int is_black;
    public int is_follow;
    public int isFriend;
    public String icon;
    public String level_url;
    public String name;
    public String email;
    public int status;
    public int gender;
    public int score;
    public int credits;
    public int gold_num;
    public int topic_num;
    public int photo_num;
    public int reply_posts_num;
    public int essence_num;
    public int friend_num;
    public int follow_num;
    public int level;
    public String sign;
    public String userTitle;
    public String mobile;
    public List<?> verify;
    public List<?> info;

    public static class HeadBean {
        public String errCode;
        public String errInfo;
        public String version;
        public int alert;
    }

    public static class BodyBean {
        public ExternInfoBean externInfo;
        public List<?> repeatList;
        public List<ProfileListBean> profileList;
        public List<CreditListBean> creditList;
        public List<CreditShowListBean> creditShowList;

        public static class ExternInfoBean {
            public String padding;
        }

        public static class ProfileListBean {
            public String type;
            public String title;
            public String data;
        }

        public static class CreditListBean {
            public String type;
            public String title;
            public int data;
        }

        public static class CreditShowListBean {
            public String type;
            public String title;
            public int data;
        }
    }
}
