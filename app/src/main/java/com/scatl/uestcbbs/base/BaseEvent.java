package com.scatl.uestcbbs.base;

import java.util.List;

/**
 * author: sca_tl
 * description:
 * date: 2019/8/26 16:47
 */
public class BaseEvent<T> {
    public int eventCode;
    public T eventData;

    public BaseEvent(int code, T data) {
        this.eventCode = code;
        this.eventData = data;
    }

    public BaseEvent(int code) {
        this.eventCode = code;
    }

    public static class AddPoll {
        public List<String> pollOptions;
        public int pollExp;
        public int pollChoice;
        public boolean pollVisible;
        public boolean showVoters;
    }

    public static class EventCode {
        public static final int NIGHT_MODE = 1;    //夜间模式/日间模式
        public static final int LOGIN_SUCCESS = 3;     //登录成功
        public static final int LOGOUT_SUCCESS = 4;    //登出成功
        public static final int SET_MSG_COUNT = 5;     //消息数目
        public static final int AT_USER = 17;   //艾特用户
        public static final int ADD_ACCOUNT_SUCCESS = 18;//添加帐号成功
        public static final int BOARD_SELECTED = 23; //发表帖子时选择了板块
        public static final int HOME_REFRESH = 24;
        public static final int DELETE_POLL = 25;
        public static final int ADD_POLL = 26;
        public static final int SEND_COMMENT_SUCCESS = 28; //发表评论成功
        public static final int SWITCH_TO_MESSAGE = 29;
        public static final int HOME_NAVIGATION_HIDE = 31;
        public static final int SUPER_LOGIN_SUCCESS = 32;
        public static final int HOME_BANNER_VISIBILITY_CHANGE = 34;
        public static final int USE_MAGIC_SUCCESS = 35;
        public static final int DELETE_MINE_VISITOR_HISTORY_SUCCESS = 37;
        public static final int VIEW_USER_MORE_INFO = 38;
        public static final int ALL_SITE_TOP_STICK_VISIBILITY_CHANGE = 39;
        public static final int RATE_SUCCESS = 41;
        public static final int DIANPING_SUCCESS = 42;
        public static final int EXIT_CREATE_POST = 43;
        public static final int COMMENT_FRAGMENT_SCROLL = 45; //CommentFragment滑动
        public static final int SCROLL_POST_DETAIL_TAB_TO_TOP = 46;
        public static final int APPLY_NEW_TASK_SUCCESS = 47;
        public static final int DELETE_TASK_SUCCESS = 48;
        public static final int COMMENT_SORT_CHANGE = 49;
        public static final int LOCATE_COMMENT = 50;
        public static final int COMMENT_REFRESHED = 51;
        public static final int BLACK_LIST_DATA_CHANGED = 52;
    }

}
