package com.scatl.uestcbbs.entity;

import java.util.List;

public class SearchUserBean {

    public int rs;
    public String errcode;
    public HeadBean head;
    public BodyBean body;
    public int searchid;
    public int page;
    public int has_next;
    public int total_num;

    public static class HeadBean {
        public String errCode;
        public String errInfo;
        public String version;
        public int alert;
    }

    public static class BodyBean {
        public ExternInfoBean externInfo;
        public List<ListBean> list;

        public static class ExternInfoBean {
            public String padding;
        }

        public static class ListBean {
            public int uid;
            public String icon;
            public int isFriend;
            public int is_black;
            public int gender;
            public String name;
            public int status;
            public int level;
            public int credits;
            public int isFollow;
            public String dateline;
            public String signture;
            public String location;
            public String distance;
            public String userTitle;
        }
    }
}
