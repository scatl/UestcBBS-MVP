package com.scatl.uestcbbs.entity;

import java.util.List;

/**
 * author: sca_tl
 * date: 2021/1/2 19:25
 * description:
 */
class DarkRoomBean {

    public MessageBean message;
    public List<DataBean> data;

    public static class MessageBean {
        public String dataexist;
        public String cid;
    }

    public static class DataBean {
        public String cid;
        public String uid;
        public String operatorid;
        public String operator;
        public String action;
        public String reason;
        public String dateline;
        public String username;
        public String groupexpiry;
    }
}
