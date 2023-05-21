package com.scatl.uestcbbs.entity;

import java.util.List;

/**
 * Created by sca_tl at 2023/5/19 9:15
 */
public class DianPingMsgBean {

    public Integer rs;
    public String errcode;
    public HeadBean head;
    public BodyBean body;
    public Integer page;
    public Integer has_next;
    public Integer total_num;

    public static class HeadBean {
        public String errCode;
        public String errInfo;
        public String version;
        public Integer alert;
    }

    public static class BodyBean {
        public ExternInfoBean externInfo;
        public List<DataBean> data;

        public static class ExternInfoBean {
            public String padding;
        }

        public static class DataBean {
            public String board_name;
            public Integer board_id;
            public Integer topic_id;
            public String topic_subject;
            public String topic_content;
            public String topic_url;
            public String reply_content;
            public String reply_url;
            public Integer reply_remind_id;
            public String user_name;
            public Integer user_id;
            public String icon;
            public Integer is_read;
            public String replied_date;
            public String comment_user_id;
            public String comment_user_name;
            public String type;
        }
    }
}
