package com.scatl.uestcbbs.entity;

import java.util.List;

/**
 * author: sca_tl
 * description:
 * date: 2020/1/25 16:29
 */
public class UploadResultBean {
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
        public List<AttachmentBean> attachment;

        public static class ExternInfoBean {
            public String padding;
        }

        public static class AttachmentBean {
            public int id;
            public String urlName;

        }
    }
}
