package com.scatl.uestcbbs.entity;

/**
 * author: sca_tl
 * date: 2020/10/25 17:17
 * description:
 */
public class HouQinReportTopicBean {

    public String stuPost;
    public String accReplies;
    public PostBean post;
    public String topicPost;
    public String topicState;

    public static class PostBean {
        public String topic_date;
        public int topic_click;
        public Object photo;
        public String zone_name;
        public String acc_nickname;
        public String site_name;
        public Object extend;
        public String acc_phone;
        public String reply_person;
        public String topic_title;
        public Object topic_laud;
        public Object topic_step;
        public int topic_id;
        public String topic_text;
        public int sysAccounts_acc_id;
        public int topic_replyCount;
    }
}
