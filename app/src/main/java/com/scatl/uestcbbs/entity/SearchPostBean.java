package com.scatl.uestcbbs.entity;

import java.util.List;

public class SearchPostBean {

    public int rs;
    public String errcode;
    public HeadBean head;
    public BodyBean body;
    public int page;
    public int has_next;
    public int total_num;
    public int searchid;
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
        public int board_id;
        public int topic_id;
        public int type_id;
        public int sort_id;
        public int vote;
        public String title;
        public String subject;
        public int user_id;
        public String last_reply_date;
        public String user_nick_name;
        public int hits;
        public int replies;
        public int top;
        public int status;
        public int essence;
        public int hot;
        public String pic_path;
        public String avatar;
    }
}
