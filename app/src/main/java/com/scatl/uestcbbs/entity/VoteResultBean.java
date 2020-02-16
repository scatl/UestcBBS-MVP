package com.scatl.uestcbbs.entity;

import java.util.List;

/**
 * author: sca_tl
 * description:
 * date: 2020/1/27 19:56
 */
public class VoteResultBean {

    public int rs;
    public String errcode;
    public HeadBean head;
    public BodyBean body;
    public List<VoteRsBean> vote_rs;

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

    public static class VoteRsBean {
        public String name;
        public int pollItemId;
        public int totalNum;
    }
}
