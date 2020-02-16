package com.scatl.uestcbbs.entity;

/**
 * author: sca_tl
 * description:
 * date: 2020/1/29 19:28
 */
public class SendPrivateMsgResultBean {

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
        public int plid;
        public int pmid;
        public String sendTime;

        public static class ExternInfoBean {
            public String padding;
        }
    }
}
