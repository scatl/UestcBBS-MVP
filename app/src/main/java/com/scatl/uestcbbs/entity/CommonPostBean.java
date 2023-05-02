package com.scatl.uestcbbs.entity;

import androidx.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by sca_tl at 2023/4/25 17:14
 */
public class CommonPostBean {
    public int rs;
    public String errcode;
    public HeadBean head;
    public BodyBean body;
    public int isOnlyTopicType;
    public ForumInfoBean forumInfo;
    public int page;
    public int has_next;
    public int total_num;
    public int searchid;
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
        public int fid;
        public int board_id;
        public String board_name;
        @SerializedName(value = "topic_id", alternate = {"source_id"})
        public int topic_id;
        public String type;
        public String title;
        public int user_id;
        public int type_id;
        public int sort_id;
        public String user_nick_name;
        @SerializedName(value = "userAvatar", alternate = {"avatar"})
        public String userAvatar;
        public String last_reply_date;
        public int vote;
        public int hot;
        public int hits;
        public int replies;
        public int essence;
        public int top;
        public int status;
        @SerializedName(value = "subject", alternate = {"summary"})
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
        public String distance;
        public String location;
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

        @Override
        public int hashCode() {
            int result = 17;
            result = result * 31 + user_nick_name.hashCode();
            result = result * 31 + userAvatar.hashCode();
            result = result * 31 + String.valueOf(topic_id).hashCode();
            return result;
        }

        @Override
        public boolean equals(@Nullable Object obj) {
            if (obj instanceof ListBean) {
                return this.topic_id == ((ListBean)obj).topic_id;
            }
            return false;
        }
    }
}
