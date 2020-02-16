package com.scatl.uestcbbs.entity;

/**
 * author: sca_tl
 * description:
 * date: 2020/2/5 19:24
 */
public class ModifySignBean {

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

        public static class ExternInfoBean {
            public String padding;
        }
    }
}
