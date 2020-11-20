package com.scatl.uestcbbs.entity;

import java.util.List;

/**
 * author: sca_tl
 * date: 2020/10/23 20:44
 * description:
 */
public class HouQinReportListBean {

    public int total;
    public int pages;
    public int pageNo;
    public List<TopicBean> topic;

    public static class TopicBean {
        public String replyDept;
        public int topicId;
        public String topDate;
        public String categName;
        public String replyDate;
        public String state;
        public String title;
        public String readOrReply;
        public String account;
    }
}
