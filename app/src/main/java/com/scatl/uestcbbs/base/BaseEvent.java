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

    public static class BoardSelected {
        public String boardName;
        public int boardId;
        public String filterName;
        public int filterId;
    }

    public static class AddPoll {
        public List<String> pollOptions;
        public int pollExp;
        public int pollChoice;
        public boolean pollVisible;
        public boolean showVoters;
    }

    public static class EventCode {

        public static final String NEW_REPLY_MSG = "newReplyMsg";   //新回复消息
        public static final String NEW_AT_MSG = "newAtMsg";      //新at消息
        public static final String NEW_PRIVATE_MSG = "newPrivateMsg"; //新私信消息
        public static final String NEW_SYSTEM_MSG = "newSystemMsg";
        public static final String NEW_DAINPING_MSG = "newDianPingMsg";
        /**
         *打开下载完成的文件
         */
        public static final String OPEN_DOWNLOADED_FILE= "open_file";

        public static final int NIGHT_MODE_YES = 1;    //夜间模式
        public static final int NIGHT_MODE_NO = 2;     //日间模式
        public static final int LOGIN_SUCCESS = 3;     //登录成功
        public static final int LOGOUT_SUCCESS = 4;    //登出成功

        public static final int SET_MSG_COUNT = 5;     //消息数目
        public static final int CLEAR_SYSTEM_MSG_COUNT = 6;
        public static final int CLEAR_REPLY_MSG_COUNT = 7;  //新回复消息数目置零
        public static final int CLEAR_AT_MSG_COUNT = 8;     //新at消息数目置零
        public static final int SET_NEW_PM_COUNT_SUBTRACT = 9;  //新私信消息数目减1
        public static final int READ_PRIVATE_CHAT_MSG = 10;    //读取了私信内容
        public static final int CLEAR_DIANPING_MSG_COUNT = 11;

        public static final int INSERT_EMOTION = 16;  //插入表情
        public static final int AT_USER = 17;   //艾特用户
        public static final int ADD_ACCOUNT_SUCCESS = 18;//添加帐号成功
        public static final int BOARD_ID_CHANGE = 19;//板块id变化
        public static final int FILTER_ID_CHANGE = 20;//分类id变化
        public static final int FILTER_DATA = 22;
        public static final int BOARD_SELECTED = 23; //发表帖子时选择了板块
        public static final int HOME_REFRESH = 24;
        public static final int DELETE_POLL = 25;
        public static final int ADD_POLL = 26;
        public static final int HOME1_REFRESH = 27;
        public static final int SEND_COMMENT_SUCCESS = 28; //发表评论成功
        public static final int SWITCH_TO_MESSAGE = 29;
        public static final int UI_MODE_FOLLOW_SYSTEM = 30;
        public static final int HOME_NAVIGATION_HIDE = 31;
        public static final int SUPER_LOGIN_SUCCESS = 32;
        public static final int POST_APPEND_SUCCESS = 33;
        public static final int HOME_BANNER_VISIBILITY_CHANGE = 34;
        public static final int USE_MAGIC_SUCCESS = 35;
        public static final int BLACK_LIST_CHANGE = 36;
        public static final int DELETE_MINE_VISITOR_HISTORY_SUCCESS = 37;
        public static final int VIEW_USER_MORE_INFO = 38;
        public static final int ALL_SITE_TOP_STICK_VISIBILITY_CHANGE = 39;
        public static final int VIEW_PAGER_TITLE_CLICK = 40;
        public static final int RATE_SUCCESS = 41;
        public static final int DIANPING_SUCCESS = 42;
        public static final int EXIT_CREATE_POST = 43;
        /**
         * 下载文件完成
         */
        public static final int DOWNLOAD_FILE_COMPLETED = 44;

        /**
         * CommentFragment滑动
         */
        public static final int COMMENT_FRAGMENT_SCROLL = 45;

        public static final int SCROLL_POST_DETAIL_TAB_TO_TOP = 46;

        public static final int APPLY_NEW_TASK_SUCCESS = 47;
        public static final int DELETE_TASK_SUCCESS = 48;

    }

}
