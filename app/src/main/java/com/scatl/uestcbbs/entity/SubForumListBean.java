package com.scatl.uestcbbs.entity;

import java.util.List;

public class SubForumListBean {

    public int rs;
    public String errcode;
    public HeadBean head;
    public BodyBean body;
    public int online_user_num;
    public int td_visitors;
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

    public static class ListBean {
        public int board_category_id;
        public String board_category_name;
        public int board_category_type;
        public String description;
        public String icon;
        public String td_posts_num;
        public String topic_total_num;
        public String posts_total_num;
        public int is_focus;
        public List<BoardListBean> board_list;

        public static class BoardListBean {
            public int board_id;
            public String board_name;
            public String description;
            public int board_child;
            public String board_img;
            public String last_posts_date;
            public int board_content;
            public String forumRedirect;
            public int favNum;
            public int td_posts_num;
            public int topic_total_num;
            public int posts_total_num;
            public int is_focus;
        }
    }
}
