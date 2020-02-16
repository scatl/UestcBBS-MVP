package com.scatl.uestcbbs.entity;

import java.util.List;

/**
 * author: sca_tl
 * description:
 * date: 2020/1/29 19:12
 */
public class PrivateChatBean {

    public int rs;
    public String errcode;
    public HeadBean head;
    public BodyBean body;

    public static class HeadBean {
        public String errCode;
        public String errInfo;
        public String version;
        public int alert;
    }

    public static class BodyBean {
        public ExternInfoBean externInfo;
        public UserInfoBean userInfo;
        public List<PmListBean> pmList;

        public static class ExternInfoBean {
            public String padding;
        }

        public static class UserInfoBean {
            public int uid;
            public String name;
            public String avatar;
        }

        public static class PmListBean {
            public int fromUid;
            public String name;
            public String avatar;
            public int plid;
            public int hasPrev;
            public List<MsgListBean> msgList;

            public static class MsgListBean {
                public int sender;
                public int mid;
                public String content;
                public String type;
                public String time;
            }
        }
    }
}
