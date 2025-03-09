package com.scatl.uestcbbs.api;

public class ApiConstant {

    public static final String BBS_BASE_URL = "https://bbs.uestc.edu.cn/";
    public static final String HOUQIN_BASE_URL = "https://hq.uestc.edu.cn/";

    public static final String BING_BASE_URL = "https://cn.bing.com/";
    public static final String BING_PIC = "HPImageArchive.aspx?format=js&idx=0&n=5";

    public static final String BASE_ADDITIONAL_URL = "";
    public static final String NOTICE_URL = BASE_ADDITIONAL_URL + "Uestcbbs/update/notice.json";
    public static final String SETTINGS_URL = BASE_ADDITIONAL_URL + "Uestcbbs/update/settings.json";

    public static final String OPEN_SOURCE_URL = "https://github.com/scatl/UestcBBS-MVP";


    public static class Code {
        public static final int SUCCESS_CODE = 1;
        public static final int ERROR_CODE = 0;

        //部分帖子和板块会出现此错误
        public static final String RESPONSE_ERROR_500 = "syntax error, unexpected ')'";
    }

    //
    public static class Other {
        //question
        public static final String GET_DAY_QUESTION_ANSWER = BASE_ADDITIONAL_URL + "uestcbbs/GetDayQuestionAnswer";

        //question, answer
        public static final String SUBMIT_DAY_QUESTION_ANSWER = BASE_ADDITIONAL_URL + "uestcbbs/SubmitDayQuestionAnswer";

        //获取更新
        //versionCode, isTest
        public static final String GET_UPDATE_INFO = BASE_ADDITIONAL_URL + "uestcbbs/GetUpdateInfo";

    }

    //用户相关
    public static class User {

        //登陆
        //请求参数：username, password
        public static final String LOGIN_URL = "mobcent/app/web/index.php?r=user/login";

        //关注用户
        //type=follow/unfollow  uid=关注用户的uid
        public static final String FOLLOW_USER = "mobcent/app/web/index.php?r=user/useradmin";

        //请求添加为好友
        //uid, act=add（添加）/ignore（删除）
        public static final String ADD_FRIEND = "mobcent/app/web/index.php?ruser/useradminview";

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
        //page ,pageSize,orderBy=dateline,type=followed/follow/friend,uid,
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
        public static final String REPORT = "mobcent/app/web/index.php?r=user/report";

        //获取相册列表
        //uid
        public static final String ALBUM_LIST = "mobcent/app/web/index.php?r=user/albumlist";

        //获取相册图片
        //uid, albumId, page, pageSize
        public static final String PHOTO_LIST = "mobcent/app/web/index.php?r=user/photolist";

        //获取河畔帐号黑名单
        //page
        public static final String ACCOUNT_BLACK_LIST = "home.php?mod=space&do=friend&view=blacklist";

        //获取修改头像的参数
        public static final String GET_MODIFY_AVATAR_PARA = "home.php?mod=spacecp&ac=avatar";

        //修改头像 agent，input
        //formdata:avatar1、avatar2、avatar3
        public static final String MODIFY_AVATAR = "uc_server/index.php?m=user&a=rectavatar&base64=yes&appid=1&ucapi=bbs.uestc.edu.cn%2Fuc_server&avatartype=virtual&uploadSize=2048";

        //个人空间,获取访客，勋章等数据，uid，do
        public static final String USER_SPACE = "/home.php?mod=space";

        //删除访客足迹,uid
        public static final String DELETE_VISITED_HISTORY = "/home.php?mod=space&do=index&view=admin&additional=removevlog";

        public static final String GET_ONLINE_USER = "/home.php?mod=space&do=friend&view=online&type=member";
    }

    //帖子相关
    public static class Post {

        //帖子链接
        public static final String TOPIC_URL = "https://bbs.uestc.edu.cn/forum.php?mod=viewthread&tid=";
        //板块链接
        public static final String BOARD_URL = "https://bbs.uestc.edu.cn/forum.php?mod=forumdisplay&fid=";

        //热门帖子：服务器只返回十条记录,帖子时间是发表时间
        //请求参数：page = 1，pageSize = 10，moduleId = 2
        //circle=1可以返回帖子部分回复内容
        public static final String HOT_POST = "mobcent/app/web/index.php?r=portal/newslist";

        //最新发表/回复，服务器返回的帖子列表里只有最近回复时间，并没有帖子发表时间
        //请求参数：page = 1，pageSize = 10，boardId = 0，sortby = new（最新发表）/ all（最新回复）
        //circle=1可以返回帖子部分回复内容
        public static final String HOME_TOPIC = "mobcent/app/web/index.php?r=forum/topiclist";

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

        //赞或踩
        //tid:topic_id
        //pid:reply_posts_id
        //type:thread(主题帖)，post（回复）
        //action:support(默认)，against
        public static final String SUPPORT = "mobcent/app/web/index.php?r=forum/support";

        //帖子操作，需要管理员权限。返回HTML
        //fid,  tid, pid,
        //type: topic（主题）, post（帖子回复等）
        //act:band(屏蔽主题), top置顶, marrow精华,  delete删除, close关闭, open开放, move移动
        public static final String ADMIN_VIEW = "mobcent/app/web/index.php?r=forum/topicadminview";

        //补充内容
        public static final String POST_APPEND = "forum.php?mod=misc&action=postappend";

        //查看点评，需cookies支持
        //tid, pid, page
        public static final String VIEW_COMMENT_LIST = "forum.php?mod=misc&action=commentmore&inajax=1";

        //发表点评，需cookies支持
        // tid, pid
        //表单: formhash, handlekey, message, commentsubmit
        public static final String SEND_DIANPING = "forum.php?mod=post&action=reply&comment=yes&commentsubmit=yes&infloat=yes";

        //tid, pid
        //获取formhash，需cookies支持
        public static final String GET_DIANPING_FORMHASH = "forum.php?mod=misc&action=comment";

        //tid, page
        public static final String GET_POST_WEB_DETAIL = "forum.php?mod=viewthread";

        //（取消）置顶评论
        //Form Data：formhash  fid  tid  page  handlekey=mods  topiclist[]   stickreply(1为置顶，0为取消置顶)  reason
        public static final String STICK_REPLY = "forum.php?mod=topicadmin&action=stickreply&modsubmit=yes&infloat=yes&modclick=yes&inajax=1";

        //大红楼，
        //formdata：formhash  message, subject
        public static final String BIG_RED_FLOOR = "forum.php?mod=post&action=reply&fid=25&tid=1745525&replysubmit=yes&infloat=yes&handlekey=livereplypost&inajax=1";

        //使用悔悟卡,id=帖子id:主题id
        //购买道具、messagetext、使用道具
        public static final String USE_REGRET_MAGIC = "home.php?mod=magic&mid=repent&idtype=pid";

        //确认使用悔悟卡
        //表单数据：formhash、handlekey、operation=use、magicid=20、pid=帖子id、ptid=主题id、usesubmi=yes、operation=use、magicid=20、idtype=pid、id=帖子id:主题id（例如32083305:1805557）
        public static final String CONFIRM_USE_REGRET_MAGIC = "home.php?mod=magic&action=mybox&infloat=yes&inajax=1";
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

        //点评消息
        public static final String DIANPING_MESSAGE = "mobcent/app/web/index.php?r=message/notifylistex&type=pcomment";

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

        //上传附件，需要cookies支持
        //param: fid
        //formData: uid, hash, filetype, Filename, Filedata
        public static final String UPLOAD_ATTACHMENT = "misc.php?mod=swfupload&action=swfupload&operation=upload&html5=attach";

        //获取上传所需的hash参数
        //tid
        public static final String GET_UPLOAD_HASH = BBS_BASE_URL + "forum.php?mod=viewthread";

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
        //			"isAnonymous": 0,  //1 表示匿名发帖
        //			"isOnlyAuthor": 0,  //1 表示回帖仅作者可见
        //			"isShowPostion": 0,
        //			"replyId": 0,  //引用内容的pid
        //			"isQuote": 0  //是否引用之前回复的内容。1是0否
        //          "title": "Title", // 标题。
        //          "typeId": 1234, // 分类。
        //////////投票
        //        "poll": {
        //            "expiration": 3, 记票天数
        //            "options": ["11", "22"],
        //            "maxChoices": 2,//最多选择几项
        //            "visibleAfterVote", true, //投票后结果可见
        //            "showVoters": true,  //公开投票参与人
        //        },
        //		}
        //	}
        //}
        public static final String CREATE_POST = "mobcent/app/web/index.php?r=forum/topicadmin";

        //删除全部私信
        //formdata:
        // deletepm_deluid[]=
        // custompage=1
        // deletepmsubmit_btn=true
        // deletesubmit=true
        // formhash=
        public static final String DELETE_ALL_PRIVATE_MSG = "home.php?mod=spacecp&ac=pm&op=delete&folder=";

        //删除单条私信
        //deletepm_pmid[]=4526891
        //touid=227267
        //handlekey=pmdeletehk_4526891
        //formhash=2ff26bf3
        public static final String DELETE_SINGLE_PRIVATE_MSG = "home.php?mod=spacecp&ac=pm&op=delete&deletesubmit=1&inajax=1&ajaxtarget=";

    }

    //淘帖
    public static class Collection {
        //淘帖专辑列表
        //page
        public static final String COLLECTION_LIST = "forum.php?mod=collection";

        //专辑帖子列表
        //ctid
        //page
        public static final String COLLECTION_DETAIL = "forum.php?mod=collection&action=view";

        //订阅/取消 淘专辑  需要cookies支持
        //op=follow/unfo
        //ctid
        //formhash
        public static final String SUBSCRIBE_COLLECTION = "forum.php?mod=collection&action=follow&inajax=1&ajaxtarget=undefined";

        //我的专辑，包括订阅和创建的，需要cookies支持
        public static final String MY_COLLECTION = "forum.php?mod=collection&op=my";

        //添加到淘专辑
        //tid
        public static final String ADD_TO_COLLECTION = "forum.php?mod=collection&action=edit&op=addthread";

        //确认添加
        //formdata:ctid  reason  tids[]   inajax=1   handlekey=""  fromhash   addthread=1   submitaddthread
        public static final String CONFIRM_ADD_TO_COLLECTION = "forum.php?mod=collection&action=edit&op=addthread";

        //创建淘专辑
        //formdata:title  desc  keyword  submitcollection=1  op  ctid=0  formhash  collectionsubmit=submit
        public static final String CREATE_COLLECTION = "forum.php?mod=collection&action=edit";

        //删除淘帖
        //formdata  delthread[]  ctid  formhash
        public static final String DELETE_COLLECTION_POST = "forum.php?mod=collection&action=edit&op=delthread";

        //删除淘专辑
        //ctid  formhash
        public static final String DELETE_COLLECTION = "forum.php?mod=collection&action=edit&op=remove";
    }

    //
    public static class Forum {

        //获取所有板块
        public static final String ALL_FORUM_LIST = "forum.php?mod=ajax&action=forumjump&jfid=undefined&inajax=1&ajaxtarget=fjump_menu";

        //获取所有父板块列表
        public static final String FORUM_LIST = "mobcent/app/web/index.php?r=forum/forumlist";

        //获取子版块列表
        //fid:父板块id
        public static final String SUB_FORUM_LIST = "mobcent/app/web/index.php?r=forum/forumlist";

        //清除缓存
        public static final String CLEAN_CACHE = "mobcent/app/web/index.php?r=cache/clean";

        //抢沙发RSS
        public static final String GRAB_SOFA = "forum.php?mod=guide&view=sofa&rss=1";


        //用户组，需要cookies支持
        public static final String USER_GROUP = "home.php?mod=spacecp&ac=usergroup";

        //登录获取cookies
        public static final String LOGIN_FOR_COOKIES = "member.php?mod=logging&action=login&loginsubmit=yes&inajax=1&mobile=2&handlekey=loginform";

        //每日答题
        public static final String DAY_QUESTION = "plugin.php?id=ahome_dayquestion:pop";

        //获取帖子数，在线人数等相关信息
        public static final String HOME_INFO = "forum.php?showoldetails=yes#online";

        //赞首页格言
        //gid, hash
        public static final String ZAN_GE_YAN = "plugin.php?id=vanfon_geyan:zan&action=add";

        //勋章中心
        public static final String MEDAL_CENTER = "home.php?mod=medal";

        //我的勋章
        public static final String MINE_MEDAL = "home.php?mod=medal&action=log";

        //购买勋章
        //formhash  medalid  operation  handlekey=medal  medalsubmit=true
        public static final String BUG_MEDAL = "home.php?mod=medal&action=apply&medalsubmit=yes";

        //获取实名关联
        public static final String GET_REAL_NAME_INFO = "plugin.php?id=rnreg:status";

        //道具商店
        public static final String MAGIC_SHOP = "home.php?mod=magic&action=shop";
        //道具详情,mid
        public static final String MAGIC_DETAIL = "home.php?mod=magic&action=shop&operation=buy";
        //购买道具
        //formdata: formhash,operation=buy,mid,magicnum=1,operatesubmit=true,operatesubmit=yes
        public static final String BUY_MAGIC = "home.php?mod=magic&action=shop&infloat=yes";
        //我的道具
        public static final String MINE_MAGIC = "home.php?mod=magic&action=mybox";
        //使用道具,magicid
        public static final String USE_MAGIC = "home.php?mod=magic&action=mybox&operation=use";
        //确认使用道具
        //formdata:formhash,handlekey,operation=use,magicid,usesubmit=yes,operation=use
        public static final String CONFIRM_USE_MAGIC = "home.php?mod=magic&action=mybox&infloat=yes&inajax=1";

        //积分记录，page
        //表单数据：
        //exttype=0不限  1威望  2水滴  6奖励券
        //starttime=
        //endtime=
        //income=0不限   1收入  -1支出
        //optype=
        //search=true
        //op=log
        //ac=credit
        //mod=spacecp
        public static final String CREDIT_HISTORY = "home.php?mod=spacecp&ac=credit&op=log";

        //我的财富
        public static final String MINE_CREDIT = "home.php?mod=spacecp&ac=credit&op=base";

        //转账
        //表单数据
        //formhash:
        //transfersubmit: true
        //handlekey: transfercredit
        //transferamount: 转账数目
        //to: 目标用户名
        //password:
        //transfermessage: 留言
        //transfersubmit_btn: true
        public static final String CREDIT_TRANSFER = "home.php?mod=spacecp&ac=credit&op=transfer";

        //
        public static final String GET_CREDIT_FORMHASH = "home.php?mod=spacecp&ac=credit&op=transfer";

        //获取新任务
        public static final String GET_NEW_TASK = "home.php?mod=task&item=new";

        //获取进行中任务
        public static final String GET_DOING_TASK = "home.php?mod=task&item=doing";

        public static final String GET_DONE_TASK = "home.php?mod=task&item=done";

        public static final String GET_FAILED_TASK = "home.php?mod=task&item=failed";

        //获取任务详情,id
        public static final String GET_TASK_DETAIL = "home.php?mod=task&do=view";

        //申请任务,id
        public static final String APPLY_NEW_TASK = "home.php?mod=task&do=apply";

        //领取任务奖励, id
        public static final String GET_TASK_AWARD = "home.php?mod=task&do=draw";

        //放弃任务
        public static final String DELETE_DOING_TASK = "home.php?mod=task&do=delete";

        //查看公开投票人,tid=1836978
        public static final String VIEW_VOTER = "forum.php?mod=misc&action=viewvote";

        //找回用户名
        //表单：
        //formhash:
        //student_id:
        //portal_password:
        //resetpasswordsubmit: 1
        public static final String FIND_USERNAME = "plugin.php?id=rnreg:resetpassword&forgetusername=1";

        //重置密码
        //表单：
        //formhash:
        //username:
        //student_id:
        //student_name:
        //portal_password:
        //newpassword:
        //newpassword2:
        //resetpasswordsubmit: 1
        public static final String RESET_PASSWORD = "plugin.php?id=rnreg:resetpassword";

        public static final String FORUM_DETAIL = "forum.php?mod=forumdisplay";

        //获取所有评分用户,tid, pid
        public static final String GET_ALL_RATE_USER = "forum.php?mod=misc&action=viewratings";

        public static final String DARK_ROOM = "forum.php?mod=misc&action=showdarkroom";

        //根据tid和pid获取网页帖子详情
        //formdata:pid , ptid
        public static final String FIND_POST = "forum.php?mod=redirect&goto=findpost";

        //支付水滴浏览板块
        //fid
        //formdata:formhash、loginsubmit=true
        public static final String PAY_FOR_VISITING_FORUM = "forum.php?mod=forumdisplay&action=paysubmit";

        //散水
        //表单数据：formhash，posttime（时间戳，秒）, wysiwyg=1, typeid=315, subject（标题），message(内容)，
        //price, tags, cronpublishdate, allownoticeauthor=1, addfeed=1, usesig=1, save, uploadalbum=-2, newalbum=请输入相册名称
        //replycredit_extcredits=1  每次回帖奖励1滴水滴
        //replycredit_times=10      奖励 10 次
        //replycredit_membertimes=1 每人最多可得1次（1-10）
        //replycredit_random=100    中奖率（10,20 ... 100)
        public static final String SAN_SHUI = "forum.php?mod=post&action=newthread&fid=25&extra=&topicsubmit=yes";

        //查看警告  tid  uid
        public static final String VIEW_WARNING = "forum.php?mod=misc&action=viewwarning";

        //回复时检查是否在黑名单中fid tid
        public static final String CHECK_BLACK = "forum.php?mod=post&action=reply&extra=&replysubmit=yes&infloat=yes&handlekey=fastpost&inajax=1";
    }

    public static class HouQin {
        //获取后勤投诉列表，pageNo
        public static final String GET_ALL_REPORT_POSTS = "yzs/commentSite/getAllByQueryList";

        //获取后勤投诉主题，topic_id
        public static final String GET_HOUQIN_REPORT_TOPIC = "yzs/commentSite/getTopicDetails";

        //获取后勤投诉回复，topic_id
        public static final String GET_HOUQIN_REPORT_REPLY = "yzs/commentSite/getReplyDetails";
    }



}
