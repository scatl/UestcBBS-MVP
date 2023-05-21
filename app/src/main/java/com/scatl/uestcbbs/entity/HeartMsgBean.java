package com.scatl.uestcbbs.entity;

import java.util.List;

/**
 * author: sca_tl
 * description:
 * date: 2020/1/24 13:39
 */
public class HeartMsgBean {

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
        public ReplyInfoBean replyInfo;
        public AtMeInfoBean atMeInfo;
        public FriendInfoBean friendInfo;
        public SystemInfoBean systemInfo;
        public PCommentInfoBean pCommentInfoBean;
        public List<PmInfosBean> pmInfos;
        public CollectionBean collectionBean;

        public static class PCommentInfoBean {
            public int count;
            public String time;
        }

        public static class ExternInfoBean {
            public String padding;
            public String heartPeriod;
            public String pmPeriod;
        }

        public static class ReplyInfoBean {
            public int count;
            public String time;
        }

        public static class AtMeInfoBean {
            public int count;
            public String time;
        }

        public static class FriendInfoBean {
            public int count;
            public String time;
        }

        public static class PmInfosBean {
            public int fromUid;
            public int plid;
            public int pmid;
            public String time;
        }

        public static class SystemInfoBean {
            public int count;
            public String time;
        }

        public static class CollectionBean {
            public int count;
        }
    }
}
