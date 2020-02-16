package com.scatl.uestcbbs.entity;

import java.util.List;

/**
 * author: sca_tl
 * description:
 * date: 2020/1/27 15:34
 */
public class SystemMsgBean {

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
            public String replied_date;
            public String mod;
            public String note;
            public String user_name;
            public int user_id;
            public String icon;
            public String is_read;
            public String type;
            public List<ActionsBean> actions;

            public static class ActionsBean {
                public String redirect;
                public String title;
                public String type;
            }
        }
    }
}
