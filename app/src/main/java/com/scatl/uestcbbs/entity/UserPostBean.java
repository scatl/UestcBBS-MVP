package com.scatl.uestcbbs.entity;

import java.util.List;

/**
 * author: sca_tl
 * description:
 * date: 2020/1/27 12:34
 */
public class UserPostBean {

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
        public String pic_path;
        public int board_id;
        public String board_name;
        public int topic_id;
        public int type_id;
        public int sort_id;
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
        public String userAvatar;
        public int special;
    }
}
