package com.scatl.uestcbbs.entity;

import com.scatl.uestcbbs.http.BaseBBSResponseBean;

import java.util.List;

/**
 * author: sca_tl
 * description:
 * date: 2020/1/27 15:41
 */
public class AtMsgBean {

    public int rs;
    public String errcode;
    public HeadBean head;
    public BodyBean body;
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
        public List<DataBean> data;

        public static class ExternInfoBean {
            public String padding;
        }

        public static class DataBean {
            public String board_name;
            public int board_id;
            public int topic_id;
            public String topic_subject;
            public String topic_content;
            public String topic_url;
            public String reply_content;
            public String reply_url;
            public int reply_remind_id;
            public String user_name;
            public int user_id;
            public String icon;
            public int is_read;
            public String replied_date;
            public String type;
        }
    }
}
