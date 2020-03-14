package com.scatl.uestcbbs.entity;

import java.util.List;

/**
 * author: sca_tl
 * description:
 * date: 2020/3/14 11:27
 */
public class PhotoListBean {

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
        public int pic_id;
        public String title;
        public int user_id;
        public String release_date;
        public String last_update_date;
        public String user_nick_name;
        public int hot;
        public int replies;
        public String thumb_pic;
        public String origin_pic;
    }
}
