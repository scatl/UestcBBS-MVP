package com.scatl.uestcbbs.entity;

import java.util.List;

/**
 * author: sca_tl
 * description:
 * date: 2020/2/5 16:40
 */
public class UserFriendBean {

    public int rs;
    public String errcode;
    public HeadBean head;
    public BodyBean body;
    public int page;
    public int has_next;
    public int total_num;
    public List<ListBean> list;

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

    public static class ListBean {
        public String distance;
        public String location;
        public int is_friend;
        public int isFriend;
        public int isFollow;
        public int uid;
        public String name;
        public int status;
        public int is_black;
        public int gender;
        public String icon;
        public int level;
        public String userTitle;
        public String lastLogin;
        public String dateline;
        public String signature;
        public int credits;
        public List<?> verify;
    }
}
