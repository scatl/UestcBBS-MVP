package com.scatl.uestcbbs.entity;

import java.util.List;

/**
 * author: sca_tl
 * description: 某个板块的下的帖子列表
 * date: 2020/1/30 13:28
 */
public class SingleBoardBean {

    public int rs;
    public String errcode;
    public HeadBean head;
    public BodyBean body;
    public int isOnlyTopicType;
    public ForumInfoBean forumInfo;
    public int page;
    public int has_next;
    public int total_num;
    public List<NewTopicPanelBean> newTopicPanel;
    public List<?> classificationTop_list;
    public List<ClassificationTypeListBean> classificationType_list;
    public List<?> anno_list;
    public List<TopTopicListBean> topTopicList;
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

    public static class ForumInfoBean {
        public int id;
        public String title;
        public String description;
        public String icon;
        public String td_posts_num;
        public String topic_total_num;
        public String posts_total_num;
        public int is_focus;
    }

    public static class NewTopicPanelBean {
        public String type;
        public String action;
        public String title;
    }

    public static class ClassificationTypeListBean {
        public int classificationType_id;
        public String classificationType_name;
    }

    public static class TopTopicListBean {
        public int id;
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
    }
}
