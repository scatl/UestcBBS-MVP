package com.scatl.uestcbbs.api;

public class ApiConstant {

    public static final String BBS_BASE_URL = "http://bbs.uestc.edu.cn/";

    public static final String BING_BASE_URL = "https://cn.bing.com/";
    public static final String BING_PIC = "HPImageArchive.aspx?format=js&idx=0&n=5";

    public static final String BASE_ADDITIONAL_URL = "http://47.101.218.117:8080/";
    public static final String UPDATE_URL = BASE_ADDITIONAL_URL + "Uestcbbs/update/update_version_1.json";
    public static final String OPEN_SOURCE_URL = "https://github.com/scatl/UestcBBS-MVP";

    public static final int SIMPLE_POST_LIST_SIZE = 50;
    public static final int SIMPLE_POST_LIST_SIZE_1 = 25;
    public static final int POST_COMMENT_SIZE = 50;

    public static class Code {
        public static final int SUCCESS_CODE = 1;
        public static final int ERROR_CODE = 0;

        //部分帖子和板块会出现此错误
        public static final String RESPONSE_ERROR_500 = "syntax error, unexpected ')'";
    }

    //用户相关
    public static class User {

        public static final String REGISTER_URL = "http://bbs.uestc.edu.cn/member.php?mod=register";

        //登陆
        //请求参数：username, password
        public static final String LOGIN_URL = "mobcent/app/web/index.php?r=user/login";

        //关注用户
        //type=follow/unfollow  uid=关注用户的uid
        public static final String FOLLOW_USER = "mobcent/app/web/index.php?r=user/useradmin";

        //拉黑用户
        //type=black/delblack,  uid
        public static final String BLACK_USER = "mobcent/app/web/index.php?r=user/useradmin";

        //用户详情
        //userId=用户id
        public static final String USER_INFO = "mobcent/app/web/index.php?r=user/userinfo";

        //用户帖子
        //uid=用户id, type=reply/topic/favorite
        public static final String USER_POST = "mobcent/app/web/index.php?r=user/topiclist";

        //获取可以艾特的用户列表
        //返回值中role_num=6关注的用户，role_num=2好友
        public static final String AT_USER_LIST = "mobcent/app/web/index.php?r=forum/atuserlist";

        //获取粉丝和关注列表
        //page ,pageSize,orderBy=dateline,type=followed/follow,uid,
        public static final String FOLLOW_LIST = "mobcent/app/web/index.php?r=user/userlist";

        //修改密码
        //type = password, oldPassword, newPassword
        public static final String MODIFY_PSW = "mobcent/app/web/index.php?r=user/updateuserinfo";

        //修改签名
        //type = info, sign =
        public static final String MODIFY_SIGN = "mobcent/app/web/index.php?r=user/updateuserinfo";

        //搜索用户
        //keyword=用户名, page,pageSize,searchid=0
        public static final String SEARCH_USER = "mobcent/app/web/index.php?r=user/searchuser";

        //举报用户
        //idType=user举报用户/post举报用户的回复/thread举报用户发的帖子, message, id=相关id
//        public static final String REPORT_TYPE_USER = "user";
//        public static final String REPORT_TYPE_POST = "post";
//        public static final String REPORT_TYPE_THREAD = "thread";
        public static final String REPORT = "mobcent/app/web/index.php?r=user/report";
    }

    //帖子相关
    public static class Post {

        //帖子链接
        public static final String TOPIC_URL = "http://bbs.uestc.edu.cn/forum.php?mod=viewthread&tid=";
        //板块链接
        public static final String BOARD_URL = "http://bbs.uestc.edu.cn/forum.php?mod=forumdisplay&fid=";

        //热门帖子：服务器只返回十条记录,帖子时间是发表时间
        //请求参数：page = 1，pageSize = 10，moduleId = 2
        //circle=1可以返回帖子部分回复内容
        public static final String HOT_POST = "mobcent/app/web/index.php?r=portal/newslist";

        //最新发表/回复，服务器返回的帖子列表里只有最近回复时间，并没有帖子发表时间
        //请求参数：page = 1，pageSize = 10，boardId = 0，sortby = new（最新发表）/ all（最新回复）
        //circle=1可以返回帖子部分回复内容
        public static final String SIMPLE_POST = "mobcent/app/web/index.php?r=forum/topiclist";

        //获取某一版块的主题列表
        //boardId ,page, pageSize, sortby=new最新，essence精华，all全部,
        //filterType=typeid   filterId 分类 ID，只返回指定分类的主题，每个子版块下都有不同的分类
        //topOrder 0（不返回置顶帖，默认）, 1（返回本版置顶帖）, 2（返回分类置顶帖）, 3（返回全局置顶帖）。置顶帖包含在 topTopicList 字段中。
        public static final String FORUM_TOPIC_LIST = "mobcent/app/web/index.php?r=forum/topiclist";

        //帖子详情
        //topicId,authorId 只返回指定作者的回复，默认为 0 返回所有回复。
        //order 0 或 1（回帖倒序排列）page,pageSize
        public static final String POST_DETAIL = "mobcent/app/web/index.php?r=forum/postlist";


        //板块列表
        //fid 板块ID（可选，加上就是返回该板块下的子版块，否则返回全部板块信息）
        public static final String FORUM_LIST = "mobcent/app/web/index.php?r=forum/forumlist";

        //搜索帖子
        //keyword, page, pageSize
        public static final String SEARCH_POST = "mobcent/app/web/index.php?r=forum/search";

        //收藏帖子或板块
        //idType=tid/fid, action=favorite/delfavorite  id=帖子或板块id
        public static final String FAVORITE_POST = "mobcent/app/web/index.php?r=user/userfavorite";


        //投票
        //tid=帖子id,options=xxx,xxx,xxx(xxx为标题id),boardId,
        public static final String VOTE = "mobcent/app/web/index.php?r=forum/vote";

        //获取评分信息，返回HTML
        //tid=帖子id,pid,
        public static final String RATE_INFO = "mobcent/app/web/index.php?r=forum/topicrate&&type=view";

        //评分，返回HTML
        //tid=帖子id,pid,score2,reason,sendreasonpm=on或空,modsubmit="确定"
        public static final String RATE = "mobcent/app/web/index.php?r=forum/topicrate&modsubmit=确定";

        //赞
        //tid:topic_id
        //pid:reply_posts_id
        //type:thread(主题帖)，post（回复）
        public static final String SUPPORT = "mobcent/app/web/index.php?r=forum/support";
    }

    //消息相关
    public static class Message {

        //获取短消息会话列表
        //json={page:xx,pageSize:xx}
        public static final String PRIVATE_MSG = "mobcent/app/web/index.php?r=message/pmsessionlist";

        //获取系统通知列表
        //page, pageSize, type=system
        public static final String SYSTEM_MESSAGE = "mobcent/app/web/index.php?r=message/notifylistex&type=system";

        //获取回复我的内容
        //page,pageSize,type=post
        public static final String REPLY_ME_MESSAGE = "mobcent/app/web/index.php?r=message/notifylistex&type=post";

        //获取at我的消息
        //page,pageSize,type=at
        public static final String AT_ME_MESSAGE = "mobcent/app/web/index.php?r=message/notifylistex&type=at";


        //pmlist={
        //  "body": {
        //    "externInfo": {
        //      "onlyFromUid": 0, // 只返回收到的消息（不包括自己发出去的消息）。
        //    },
        //    "pmInfos": [{
        //      "startTime": , // 开始时间（以毫秒为单位）。startTime 和 stopTime 均为 0 表示获取最新（未读）消息，如果要获取历史消息指定一个较早的时间。
        //      "stopTime": , // 结束时间（以毫秒为单位），为零表示获取晚于 startTime 的所有消息。
        //      "cacheCount": 1,
        //      "fromUid": 123, // UID，必须指定。
        //      "pmLimit": 10, // 最多返回几条结果，默认为 15。
        //    }]
        //  }
        //} 获取与用户的私信内容
        public static final String PRIVATE_CHAT_MSG_LIST = "mobcent/app/web/index.php?r=message/pmlist";

        //发送私信
        //json={
        //	"action": "send",
        //	"msg": {
        //		"content": "nnn",
        //		"type": "text"
        //	},
        //	"plid": 0,
        //	"pmid": 0,
        //	"toUid": xxxxxx
        //}
        public static final String SEND_PRIVATE_MSG = "mobcent/app/web/index.php?r=message/pmadmin";


        //获取消息提醒
        //        "body": {
        //            "externInfo": {
        //                "padding": "",
        //                        "heartPeriod": "120000",
        //                        "pmPeriod": "20000"
        //            },
        //            "replyInfo": {  回复
        //                "count": 0,
        //                 "time": "0"
        //            },
        //            "atMeInfo": {   at我的
        //                "count": 0,
        //                 "time": "0"
        //            },
        //            "pmInfos": [  私信
        //            {
        //                "fromUid": 227267,
        //                "plid": 4036801,
        //                "pmid": 4036801,
        //                "time": "1565006051000"
        //            }
        //           ],
        //            "friendInfo": {
        //                "count": 0,
        //                "time": "0"
        //            }
        //        }
        public static final String HEART_MSG = "mobcent/app/web/index.php?r=message/heart";

    }

    //发帖相关
    public static class SendMessage {
        //上传图片
        //type=image/audio,module=pm(私信图片)/forum(帖子图片)/album
        public static final String UPLOAD_IMG = "mobcent/app/web/index.php?r=forum/sendattachmentex";

        //发送帖子/回复
        //act=new发帖/reply回复（回复他人，回复作者）/其他字符串（编辑）
        //{
        //	"body": {
        //		"json": {
        //			"fid": xx, // 发帖时的版块。
        //			"tid": xxxxxx,  // 回复时的帖子ID。
        //			"location": "",
        //			"aid": "1950445,1950446", //附件id
        //			"content": "[{\"type\":0,\"infor\":\"1100\"},{\"type\":1,\"infor\":\"http:\\\/\\\/bbs.uestc.edu.cn\\\/data\\\/attachment\\\/\\\/forum\\\/201908\\\/17\\\/175651c59732z7jwi78zz3.jpg\"},{\"type\":1,\"infor\":\"http:\\\/\\\/bbs.uestc.edu.cn\\\/data\\\/attachment\\\/\\\/forum\\\/201908\\\/17\\\/175651n67n3jqkm5oljl3o.jpg\"}]",
        //			"longitude": "103.93878173828125", //可选
        //			"latitude": "30.76161003112793",  //可选
        //			"isHidden": 0,
        //			"isAnonymous": 0,  //1 表示匿名发帖。貌似不可用
        //			"isOnlyAuthor": 0,  //1 表示回帖仅作者可见。貌似不可用
        //			"isShowPostion": 0,
        //			"replyId": 0,  //引用内容的pid
        //			"isQuote": 0  //是否引用之前回复的内容。1是0否
        //          "title": "Title", // 标题。
        //          "typeId": 1234, // 分类。
        //
        //////////投票
        //        "poll": {
        //            "expiration": 3, 记票天数
//                    "options": ["11", "22"],
//                    "maxChoices": 2,//最多选择几项
//                    "visibleAfterVote", true, //投票后结果可见
//                    "showVoters": true,  //公开投票参与人
        //        },
        //		}
        //	}
        //}
        public static final String SEND_POST_AND_REPLY = "mobcent/app/web/index.php?r=forum/topicadmin";

        //发送私信
        //json={
        //	"action": "send",
        //	"msg": {
        //		"content": "nnn",
        //		"type": "text"
        //	},
        //	"plid": 0,
        //	"pmid": 0,
        //	"toUid": xxxxxx
        //}
        public static final String SEND_PRIVATE_MSG = "mobcent/app/web/index.php?r=message/pmadmin";
    }

    public static class Forum {

        //获取所有父板块列表
        public static final String FORUM_LIST = "mobcent/app/web/index.php?r=forum/forumlist";

        //获取子版块列表
        //fid:父板块id
        public static final String SUB_FORUM_LIST = "mobcent/app/web/index.php?r=forum/forumlist";


    }

}
