package com.scatl.uestcbbs.entity;

/**
 * author: sca_tl
 * date: 2020/10/25 17:18
 * description:
 */
public class HouQinReportReplyBean {

    public RepliesBean replies;
    public String repReplies;
    public String repPost;

    public static class RepliesBean {
        public String site_name;
        public Object acc_phone;
        public Object reply_step;
        public int reply_id;
        public String reply_text;
        public Object photo;
        public Object reply_laud;
        public Object acc_nickname;
        public String reply_date;
    }
}
