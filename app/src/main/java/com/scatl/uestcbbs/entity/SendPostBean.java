package com.scatl.uestcbbs.entity;

/**
 * author: sca_tl
 * description:
 * date: 2020/1/25 20:43
 */
public class SendPostBean {

    public int rs;
    public String errcode;
    public HeadBean head;
    public BodyBean body;

    public static class HeadBean {
        public String errCode;
        public String errInfo;
        public String version;
        public String alert;
    }

    public static class BodyBean {
        public ExternInfoBean externInfo;

        public static class ExternInfoBean {
            public String padding;
        }
    }

}
