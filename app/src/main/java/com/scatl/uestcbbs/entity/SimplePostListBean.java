package com.scatl.uestcbbs.entity;

import java.util.List;

public class SimplePostListBean {

    public int rs;
    public String errcode;
    public HeadBean head;
    public BodyBean body;
    public int isOnlyTopicType;
    public int page;
    public int has_next;
    public int total_num;
    public List<NewTopicPanelBean> newTopicPanel;
    public List<?> classificationTop_list;
    public List<?> classificationType_list;
    public List<?> anno_list;
    public List<ListBean> list;

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

    public static class NewTopicPanelBean {
        public String type;
        public String action;
        public String title;
    }

    public static class ListBean {
        public int board_id;
        public String board_name;
        public int topic_id;
        public String type;
        public String title;
        public int user_id;
        public String user_nick_name;
        public String userAvatar;
        public String last_reply_date;
        public int vote;
        public int hot;
        public int hits;
        public int replies;
        public int essence;
        public int top;
        public int status;
        public String subject;
        public String pic_path;
        public String ratio;
        public int gender;
        public String userTitle;
        public int recommendAdd;
        public int special;
        public int isHasRecommendAdd;
        public String sourceWebUrl;
        public List<String> imageList;
        public List<?> verify;
        public List<ReplyBean> reply;

        public static class ReplyBean {
            public String uid;
            public String username;
            public String reply_id;
            public String text;
            public QuoteBean quote;

            public static class QuoteBean {
                public String uid;
                public String username;
            }
        }
    }
}
