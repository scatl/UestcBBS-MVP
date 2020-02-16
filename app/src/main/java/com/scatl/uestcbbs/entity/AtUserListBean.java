package com.scatl.uestcbbs.entity;

import java.util.List;

public class AtUserListBean {

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
        public int uid;
        public String name;
        public int role_num;
    }
}
