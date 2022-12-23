package com.scatl.uestcbbs.entity;

import java.util.List;

public class HotPostBean {

    public int rs;
    public String errcode;
    public HeadBean head;
    public BodyBean body;
    public int page;
    public int has_next;
    public int total_num;
    public List<?> piclist;
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
        public int special;
        public int fid;
        public int board_id;
        public String board_name;
        public String source_type;
        public int source_id;
        public String title;
        public int user_id;
        public String last_reply_date;
        public String user_nick_name;
        public int hits;
        public String summary;
        public int replies;
        public String pic_path;
        public String ratio;
        public String redirectUrl;
        public String userAvatar;
        public int gender;
        public int recommendAdd;
        public int isHasRecommendAdd;
        public String distance;
        public String location;
        public String sourceWebUrl;
        public List<String> imageList;
        public List<?> verify;
    }
}
