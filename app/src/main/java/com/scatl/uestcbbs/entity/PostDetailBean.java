package com.scatl.uestcbbs.entity;

import androidx.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class PostDetailBean {
    public int rs;
    public String errcode;
    public HeadBean head;
    public BodyBean body;
    public TopicBean topic;
    public int page;
    public int has_next;
    public int total_num;
    public String forumName;
    public int boardId;
    public String forumTopicUrl;
    public String img_url;
    public String icon_url;
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

    public static class TopicBean {
        public int topic_id;
        public String title;
        public String type;
        public int special;
        public int sortId;
        public int user_id;
        public String user_nick_name;
        public int replies;
        public int hits;
        public int essence;
        public int vote;
        public int hot;
        public int top;
        public int is_favor;
        public int favoriteNum;//收藏数量
        public String create_date;
        public String icon;
        public int level;
        public String userTitle;
        public String userColor;
        public int isFollow;
        public PollInfoBean poll_info;
        public Object activityInfo;
        public String location;
        public boolean delThread;
        public String mobileSign;
        public int status;
        public int reply_status;
        public int flag;
        public int gender;
        public int reply_posts_id;
        public RateListBean rateList;
        public RewardBean reward;
        public List<ZanListBean> zanList;
        public List<ContentBean> content;
        public List<?> managePanel;
        public List<ExtraPanelBean> extraPanel;
        public List<RelateItemBean> relateItem;

        public static class RateListBean {
            public HeadBeanX head;
            public TotalBean total;
            public String showAllUrl;//展示全部用户
            public List<BodyBeanX> body;

            public static class HeadBeanX {
                public String field1;
                public String field2;
                public String field3;
            }

            public static class TotalBean {
                public String field1;
                public String field2;
                public String field3;
            }

            public static class BodyBeanX {
                public String field1;
                public String field2;
                public String field3;
            }
        }

        public static class RewardBean {
            public int userNumber;
            public String showAllUrl;
            public List<ScoreBean> score;
            public List<UserListBean> userList; //具体的用户

            public static class ScoreBean {
                public String info;
                public int value;
            }

            public static class UserListBean {
                public int uid;
                public String userName;
                public String userIcon;
            }
        }

        public static class PollInfoBean {
            public String deadline;
            public int is_visible;
            public int voters;
            public int type;
            public int poll_status;
            public List<Integer> poll_id;
            public List<PollItemListBean> poll_item_list;

            public static class PollItemListBean {
                public String name;
                public int poll_item_id;
                public int total_num;
                public String percent;
            }
        }

        public static class ZanListBean {
            public String tid;
            public String recommenduid;
            public String dateline;
            public String username;
            @SerializedName("count(distinct recommenduid)")
            public String _$CountDistinctRecommenduid89; // FIXME check this code
        }

        public static class ContentBean {
            public String infor;
            public int type;
            public String originalInfo;
            public int aid;
            public String url;
            public String desc;
        }

        public static class ExtraPanelBean {
            public String action;
            public String title;
            public ExtParamsBean extParams;
            public String type;

            public static class ExtParamsBean {
                public String beforeAction;
                public int recommendAdd;
                public int isHasRecommendAdd;
            }
        }

        public static class RelateItemBean {
            public String tid;
            public String subject;
            public String image;
            public String msg;
            public String lastReplyTime;
            public String postTime;
            public String author;
        }
    }

    public static class ListBean implements Serializable {
        public int reply_id;
        public String reply_type;
        public String reply_name;
        public int reply_posts_id;
        public int poststick;
        public int position;
        public String posts_date;
        public String icon;
        public int level;
        public String userTitle;
        public String userColor;
        public String location;
        public String mobileSign;
        public int reply_status;
        public int status;
        public int role_num;
        public String title;
        public int gender;
        public int is_quote;
        public int quote_pid;
        public String quote_content;
        public String quote_user_name;
        public String quote_time;
        public String quote_content_bare;
        public boolean delThread;
        public List<ReplyContentBean> reply_content;
        public List<?> managePanel;
        public List<ExtraPanelBeanX> extraPanel;

        public boolean isLoadedRewardData;
        public String rewordInfo;
        public boolean isLoadedDianPingData;
        public boolean isLoadedDaShangData;
        public List<DianPingBean> dianPingBeanList;
        public List<DaShangBean> daShangBeanList;
        public boolean isLoadedAgainst;
        public int againstCount;
        public boolean isSupported;
        public int supportedCount;
        public boolean isHotComment;
        public List<ListBean> quote_comments;

        public static class ExtraPanelBeanX {
            public String action;
            public String title;
            public String recommendAdd;
            public ExtParamsBeanX extParams;
            public String type;

            public static class ExtParamsBeanX {
                public String beforeAction;
                public int recommendAdd;
                public int isHasRecommendAdd;
            }
        }

        public static class ReplyContentBean {
            public String infor;
            public int type;
            public String originalInfo;
            public int aid;
            public String url;
            public String desc;
        }

        public static class DianPingBean{
            public int uid;
            public String userName;
            public String userAvatar;
            public String comment;
            public String date;
        }

        public static class DaShangBean{
            public String userName;
            public int uid;
            public String time;
            public String reason;
            public String credit;
        }

        @Override
        public boolean equals(@Nullable Object obj) {
            if (obj instanceof ListBean) {
                return this.reply_posts_id == ((ListBean)obj).reply_posts_id;
            }
            return false;
        }
    }
}
