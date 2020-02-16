package com.scatl.uestcbbs.entity;

import java.util.List;

/**
 * author: sca_tl
 * description:
 * date: 2020/1/29 16:27
 */
public class PrivateMsgBean {

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
        public int hasNext;
        public int count;
        public List<ListBean> list;

        public static class ExternInfoBean {
            public String padding;
        }

        public static class ListBean {
            public int plid;
            public int pmid;
            public int lastUserId;
            public String lastUserName;
            public String lastSummary;
            public String lastDateline;
            public int toUserId;
            public String toUserAvatar;
            public String toUserName;
            public int toUserIsBlack;
            public int isNew;
        }
    }
}
