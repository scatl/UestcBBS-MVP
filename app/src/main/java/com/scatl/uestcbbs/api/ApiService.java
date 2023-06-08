package com.scatl.uestcbbs.api;

import com.scatl.uestcbbs.entity.AlbumListBean;
import com.scatl.uestcbbs.entity.AtMsgBean;
import com.scatl.uestcbbs.entity.AtUserListBean;
import com.scatl.uestcbbs.entity.BingPicBean;
import com.scatl.uestcbbs.entity.BlackUserBean;
import com.scatl.uestcbbs.entity.CommonPostBean;
import com.scatl.uestcbbs.entity.DayQuestionAnswerBean;
import com.scatl.uestcbbs.entity.DianPingMsgBean;
import com.scatl.uestcbbs.entity.FavoritePostResultBean;
import com.scatl.uestcbbs.entity.FollowUserBean;
import com.scatl.uestcbbs.entity.ForumListBean;
import com.scatl.uestcbbs.entity.HeartMsgBean;
import com.scatl.uestcbbs.entity.HouQinReportReplyBean;
import com.scatl.uestcbbs.entity.HouQinReportTopicBean;
import com.scatl.uestcbbs.entity.HouQinReportListBean;
import com.scatl.uestcbbs.entity.ModifyPswBean;
import com.scatl.uestcbbs.entity.ModifySignBean;
import com.scatl.uestcbbs.entity.NoticeBean;
import com.scatl.uestcbbs.entity.PhotoListBean;
import com.scatl.uestcbbs.entity.PostDetailBean;
import com.scatl.uestcbbs.entity.PrivateChatBean;
import com.scatl.uestcbbs.entity.PrivateMsgBean;
import com.scatl.uestcbbs.entity.ReplyMeMsgBean;
import com.scatl.uestcbbs.entity.ReportBean;
import com.scatl.uestcbbs.entity.SearchPostBean;
import com.scatl.uestcbbs.entity.SearchUserBean;
import com.scatl.uestcbbs.entity.SendPostBean;
import com.scatl.uestcbbs.entity.SendPrivateMsgResultBean;
import com.scatl.uestcbbs.entity.SettingsBean;
import com.scatl.uestcbbs.entity.SubForumListBean;
import com.scatl.uestcbbs.entity.SupportResultBean;
import com.scatl.uestcbbs.entity.SystemMsgBean;
import com.scatl.uestcbbs.entity.UpdateBean;
import com.scatl.uestcbbs.entity.UploadResultBean;
import com.scatl.uestcbbs.entity.UserDetailBean;
import com.scatl.uestcbbs.entity.UserFriendBean;
import com.scatl.uestcbbs.entity.VoteResultBean;
import com.scatl.uestcbbs.http.BaseBBSResponseBean;

import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.Query;
import retrofit2.http.Streaming;
import retrofit2.http.Url;

public interface ApiService {

    @Streaming
    @GET
    Observable<ResponseBody> downloadFile(@Url String url);

    //首页通知
    @POST(ApiConstant.NOTICE_URL)
    Observable<NoticeBean> getNotice();

    //软件设置
    @POST(ApiConstant.SETTINGS_URL)
    Observable<SettingsBean> getSettings();

    @Multipart
    @POST(ApiConstant.Message.UPLOAD_IMG)
    Observable<UploadResultBean> uploadImage(@PartMap Map<String,RequestBody> params,
                                             @Part List<MultipartBody.Part> files);

    //上传附件
    @Multipart
    @POST(ApiConstant.Message.UPLOAD_ATTACHMENT)
    Observable<String> uploadAttachment(@Query("fid") int tid,
                                        @PartMap Map<String, RequestBody> map);

    @GET(ApiConstant.BING_PIC)
    Observable<BingPicBean> getBingPic();

    @Multipart
    @POST(ApiConstant.Forum.LOGIN_FOR_COOKIES)
    Observable<Response<ResponseBody>> loginForCookies(@PartMap Map<String, RequestBody> map);

    @FormUrlEncoded
    @POST(ApiConstant.User.LOGIN_URL)
    Observable<Response<ResponseBody>> login(@Field("username") String username,
                                             @Field("password") String password);

    @FormUrlEncoded
    @POST(ApiConstant.User.AT_USER_LIST)
    Observable<AtUserListBean> atUserList(
            @Field("page") int page,
            @Field("pageSize") int pageSize);

    @FormUrlEncoded
    @POST(ApiConstant.Post.HOME_TOPIC)
    Observable<CommonPostBean> getHomeTopicList(
            @Field("page") int page,
            @Field("pageSize") int pageSize,
            @Field("boardId") int boardId,
            @Field("sortby") String sortby);

    @FormUrlEncoded
    @POST(ApiConstant.Post.HOT_POST)
    Observable<CommonPostBean> getHotPostList(
            @Field("page") int page,
            @Field("pageSize") int pageSize,
            @Field("moduleId") int boardId);

    @FormUrlEncoded
    @POST(ApiConstant.Post.POST_DETAIL)
    Observable<PostDetailBean> getPostDetailList(
            @Field("page") int page,
            @Field("pageSize") int pageSize,
            @Field("order") int order,
            @Field("topicId") int topicId,
            @Field("authorId") int authorId,
            @Field("accessToken") String token,
            @Field("accessSecret") String secret);

    @FormUrlEncoded
    @POST(ApiConstant.Post.POST_DETAIL)
    Call<PostDetailBean> getPostContent(
            @Field("page") int page,
            @Field("pageSize") int pageSize,
            @Field("order") int order,
            @Field("topicId") int topicId,
            @Field("authorId") int authorId);

    @FormUrlEncoded
    @POST(ApiConstant.Post.FORUM_TOPIC_LIST)
    Observable<CommonPostBean> getSingleBoardPostList(
            @Field("page") int page,
            @Field("pageSize") int pageSize,
            @Field("topOrder") int topOrder,
            @Field("boardId") int boardId,
            @Field("filterId") int filterId,
            @Field("filterType") String filterType,
            @Field("sortby") String sortby);

    @FormUrlEncoded
    @POST(ApiConstant.Message.CREATE_POST)
    Observable<SendPostBean> sendPost(
            @Field("act") String act,
            @Field("json") String json,
            @Field("accessToken") String token,
            @Field("accessSecret") String secret);

    @FormUrlEncoded
    @POST(ApiConstant.Post.SUPPORT)
    Observable<SupportResultBean> support(
            @Field("tid") int tid,
            @Field("pid") int pid,
            @Field("type") String type,
            @Field("action") String action);

    @FormUrlEncoded
    @POST(ApiConstant.Post.FAVORITE_POST)
    Observable<FavoritePostResultBean> favorite(
            @Field("idType") String idType,
            @Field("action") String action,
            @Field("id") int id);

    @FormUrlEncoded
    @POST(ApiConstant.Post.SEARCH_POST)
    Observable<SearchPostBean> searchPost(
            @Field("page") int page,
            @Field("pageSize") int pageSize,
            @Field("keyword") String keyword);

    @FormUrlEncoded
    @POST(ApiConstant.User.USER_POST)
    Observable<CommonPostBean> userPost(
            @Field("page") int page,
            @Field("pageSize") int pageSize,
            @Field("uid") int uid,
            @Field("type") String type);

    @FormUrlEncoded
    @POST(ApiConstant.User.USER_INFO)
    Observable<UserDetailBean> userDetail(
            @Field("userId") int uid);

    @FormUrlEncoded
    @POST(ApiConstant.User.FOLLOW_USER)
    Observable<FollowUserBean> followUser(
            @Field("uid") int uid,
            @Field("type") String type);

    @FormUrlEncoded
    @POST(ApiConstant.User.BLACK_USER)
    Observable<BlackUserBean> blackUser(
            @Field("uid") int uid,
            @Field("type") String type);

    @FormUrlEncoded
    @POST(ApiConstant.User.FOLLOW_LIST)
    Observable<UserFriendBean> userFriend(
            @Field("page") int page,
            @Field("pageSize") int pageSize,
            @Field("uid") int uid,
            @Field("type") String type);

    @FormUrlEncoded
    @POST(ApiConstant.User.MODIFY_SIGN)
    Observable<ModifySignBean> modifySign(
            @Field("type") String type,
            @Field("sign") String sign);

    @FormUrlEncoded
    @POST(ApiConstant.User.MODIFY_PSW)
    Observable<ModifyPswBean> modifyPsw(
            @Field("type") String type,
            @Field("oldPassword") String oldPsw,
            @Field("newPassword") String newPsw);

    @FormUrlEncoded
    @POST(ApiConstant.User.SEARCH_USER)
    Observable<SearchUserBean> searchUser(
            @Field("page") int page,
            @Field("pageSize") int pageSize,
            @Field("searchid") int searchId,
            @Field("keyword") String keyword);

    @FormUrlEncoded
    @POST(ApiConstant.User.REPORT)
    Observable<ReportBean> report(
            @Field("idType") String idType,
            @Field("message") String message,
            @Field("id") int id);

    @FormUrlEncoded
    @POST(ApiConstant.User.ALBUM_LIST)
    Observable<AlbumListBean> albumList(
            @Field("uid") int uid,
            @Field("page") int page,
            @Field("pageSize") int pageSize);

    @FormUrlEncoded
    @POST(ApiConstant.User.PHOTO_LIST)
    Observable<PhotoListBean> photoList(
            @Field("uid") int uid,
            @Field("albumId") int albumId,
            @Field("page") int page,
            @Field("pageSize") int pageSize);

    @POST(ApiConstant.User.GET_MODIFY_AVATAR_PARA)
    Observable<String> getModifyAvatarPara();

    @Multipart
    @POST(ApiConstant.User.MODIFY_AVATAR)
    Observable<String> modifyAvatar(@Query("agent") String agent,
                                    @Query("input") String input,
                                    @PartMap Map<String, RequestBody> map);

    @FormUrlEncoded
    @POST(ApiConstant.User.USER_SPACE)
    Observable<String> userSpace(@Field("uid") int uid,
                                 @Field("do") String doo);

    @FormUrlEncoded
    @POST(ApiConstant.User.USER_SPACE)
    Observable<String> getUserSpaceByName(@Field("username") String name);

    @GET(ApiConstant.User.DELETE_VISITED_HISTORY)
    Observable<String> deleteVisitedHistory(@Query("uid") int uid);

    @FormUrlEncoded
    @POST(ApiConstant.Message.SYSTEM_MESSAGE)
    Observable<SystemMsgBean> systemMsg(
            @Field("page") int page,
            @Field("pageSize") int pageSize);

    @FormUrlEncoded
    @POST(ApiConstant.Message.AT_ME_MESSAGE)
    Observable<AtMsgBean> atMsg(
            @Field("page") int page,
            @Field("pageSize") int pageSize);

    @FormUrlEncoded
    @POST(ApiConstant.Message.REPLY_ME_MESSAGE)
    Observable<ReplyMeMsgBean> replyMeMsg(
            @Field("page") int page,
            @Field("pageSize") int pageSize);

    @FormUrlEncoded
    @POST(ApiConstant.Message.DIANPING_MESSAGE)
    Observable<DianPingMsgBean> dianPingMsg(
            @Field("page") int page,
            @Field("pageSize") int pageSize);

    @FormUrlEncoded
    @POST(ApiConstant.Message.PRIVATE_MSG)
    Observable<PrivateMsgBean> privateMsg(
            @Field("apphash") String apphash,
            @Field("json") String json);

    @FormUrlEncoded
    @POST(ApiConstant.Message.PRIVATE_CHAT_MSG_LIST)
    Observable<PrivateChatBean> privateChatMsgList(@Field("pmlist") String json);

    @FormUrlEncoded
    @POST(ApiConstant.Message.SEND_PRIVATE_MSG)
    Observable<SendPrivateMsgResultBean> sendPrivateMsg(@Field("json") String json);

    @FormUrlEncoded
    @POST(ApiConstant.Post.VOTE)
    Observable<VoteResultBean> vote(
            @Field("tid") int tid,
            @Field("boardId") int boardId,
            @Field("options") String options);

    @FormUrlEncoded
    @POST(ApiConstant.Post.RATE_INFO)
    Observable<String> rateInfo(@Field("tid") int tid,
                                @Field("pid") int pid);

    @FormUrlEncoded
    @POST(ApiConstant.Post.RATE)
    Observable<String> rate(@Field("tid") int tid,
                            @Field("pid") int pid,
                            @Field("score2") int score,
                            @Field("reason") String reason,
                            @Field("sendreasonpm") String sendreasonpm);

    @FormUrlEncoded
    @POST(ApiConstant.Post.POST_APPEND)
    Observable<String> postAppendHash(@Field("tid") int tid,
                                      @Field("pid") int pid);

    @Multipart
    @POST(ApiConstant.Post.POST_APPEND)
    Observable<String> postAppendSubmit(@Query("tid") int tid,
                                        @Query("pid") int pid,
                                        @Query("postappendsubmit") String postappendsubmit,
                                        @PartMap Map<String, RequestBody> map);
    @FormUrlEncoded
    @POST(ApiConstant.Post.VIEW_COMMENT_LIST)
    Observable<String> getCommentList(@Field("tid") int tid,
                                      @Field("pid") int pid,
                                      @Field("page") int page);

    @FormUrlEncoded
    @POST(ApiConstant.Post.VIEW_COMMENT_LIST)
    Call<String> getCommentList1(@Field("tid") int tid,
                                 @Field("pid") int pid);

    @FormUrlEncoded
    @POST(ApiConstant.Post.GET_DIANPING_FORMHASH)
    Observable<String> getDianPingFormHash(@Field("tid") int tid,
                                           @Field("pid") int pid);

    @Multipart
    @POST(ApiConstant.Post.SEND_DIANPING)
    Observable<String> dianPingSubmit(@Query("tid") int tid,
                                      @Query("pid") int pid,
                                      @PartMap Map<String, RequestBody> map);

    @FormUrlEncoded
    @POST(ApiConstant.Post.GET_POST_WEB_DETAIL)
    Observable<String> getPostWebDetail(@Field("tid") int tid,
                                        @Field("page") int page);

    @Multipart
    @POST(ApiConstant.Post.STICK_REPLY)
    Observable<String> stickReply(@PartMap Map<String, RequestBody> map);

    @POST(ApiConstant.Forum.FORUM_LIST)
    Observable<ForumListBean> forumList();

    @POST(ApiConstant.Forum.ALL_FORUM_LIST)
    Observable<String> allForumList();

    @FormUrlEncoded
    @POST(ApiConstant.Forum.SUB_FORUM_LIST)
    Observable<SubForumListBean> subForumList(@Field("fid") int fid);

    @FormUrlEncoded
    @POST(ApiConstant.Message.HEART_MSG)
    Observable<HeartMsgBean> getHeartMsg(@Field("sdkVersion") String sdkVersion);

    @POST(ApiConstant.Forum.GRAB_SOFA)
    Observable<String> grabSofa();

    @FormUrlEncoded
    @POST(ApiConstant.Collection.COLLECTION_LIST)
    Observable<String> getCollectionList(@Field("page") int page,
                                         @Field("op") String op,
                                         @Field("order") String order);

    @FormUrlEncoded
    @POST(ApiConstant.Collection.COLLECTION_DETAIL)
    Observable<String> collectionDetail(@Field("ctid") int ctid,
                                        @Field("page") int page);

    @FormUrlEncoded
    @POST(ApiConstant.Collection.SUBSCRIBE_COLLECTION)
    Observable<String> subscribeCollection(@Field("ctid") int ctid,
                                           @Field("op") String op,
                                           @Field("formhash") String formhash);

    @POST(ApiConstant.Collection.MY_COLLECTION)
    Observable<String> myCollection();

    @FormUrlEncoded
    @POST(ApiConstant.Collection.ADD_TO_COLLECTION)
    Observable<String> addToCollection(@Field("tid") int tid);

    @Multipart
    @POST(ApiConstant.Collection.CONFIRM_ADD_TO_COLLECTION)
    Observable<String> confirmAddToCollection(@PartMap Map<String, RequestBody> map);

    @Multipart
    @POST(ApiConstant.Collection.CREATE_COLLECTION)
    Observable<String> createCollection(@PartMap Map<String, RequestBody> map);

    @Multipart
    @POST(ApiConstant.Collection.DELETE_COLLECTION_POST)
    Observable<String> deleteCollectionPost(@PartMap Map<String, RequestBody> map);

    @FormUrlEncoded
    @POST(ApiConstant.Collection.DELETE_COLLECTION)
    Observable<String> deleteCollection(@Field("ctid") int ctid,
                                        @Field("formhash") String formhash);

    @POST(ApiConstant.Forum.USER_GROUP)
    Observable<String> userGroup();

    @POST(ApiConstant.Forum.DAY_QUESTION)
    Observable<String> getDayQuestion();

    @Multipart
    @POST(ApiConstant.Forum.DAY_QUESTION)
    Observable<String> confirmNextQuestion(@PartMap Map<String, RequestBody> map);

    @Multipart
    @POST(ApiConstant.Forum.DAY_QUESTION)
    Observable<String> submitQuestion(@PartMap Map<String, RequestBody> map);

    @Multipart
    @POST(ApiConstant.Forum.DAY_QUESTION)
    Observable<String> confirmFinishQuestion(@PartMap Map<String, RequestBody> map);

    @POST(ApiConstant.Forum.HOME_INFO)
    Observable<String> getHomeInfo();

    @FormUrlEncoded
    @POST(ApiConstant.Forum.ZAN_GE_YAN)
    Observable<String> zanGeYan(@Field("gid") int gid,
                                @Field("hash") String hash);

    @POST(ApiConstant.Forum.MEDAL_CENTER)
    Observable<String> getMedal();

    @POST(ApiConstant.Forum.MINE_MEDAL)
    Observable<String> getMineMedal();

    @POST(ApiConstant.Forum.GET_REAL_NAME_INFO)
    Observable<String> getRealNameInfo();

    @POST(ApiConstant.Forum.DARK_ROOM)
    Observable<String> getDarkRoomList();

    @Multipart
    @POST(ApiConstant.Forum.SAN_SHUI)
    Observable<Response<ResponseBody>> sanShui(@PartMap Map<String, RequestBody> map);

    @FormUrlEncoded
    @POST(ApiConstant.Message.GET_UPLOAD_HASH)
    Observable<String> getUploadHash(@Field("tid") int tid);

    @FormUrlEncoded
    @POST(ApiConstant.HouQin.GET_ALL_REPORT_POSTS)
    Observable<HouQinReportListBean> getAllReportPosts(@Field("pageNo") int pageNo);

    @FormUrlEncoded
    @POST(ApiConstant.HouQin.GET_HOUQIN_REPORT_TOPIC)
    Observable<HouQinReportTopicBean> getHouQinReportTopic(@Field("topic_id") int topic_id);

    @FormUrlEncoded
    @POST(ApiConstant.HouQin.GET_HOUQIN_REPORT_REPLY)
    Observable<HouQinReportReplyBean> getHouQinReportReply(@Field("topic_id") int topic_id);

    @POST(ApiConstant.Forum.MAGIC_SHOP)
    Observable<String> getMagicShop();

    @POST(ApiConstant.Forum.MINE_MAGIC)
    Observable<String> getMineMagic();

    @FormUrlEncoded
    @POST(ApiConstant.Forum.MAGIC_DETAIL)
    Observable<String> getMagicDetail(@Field("mid") String mid);

    @FormUrlEncoded
    @POST(ApiConstant.Forum.USE_MAGIC)
    Observable<String> getUseMagicDetail(@Field("magicid") String magicid);

    @FormUrlEncoded
    @POST(ApiConstant.Forum.FIND_POST)
    Observable<String> findPost(@Field("ptid") int ptid,
                                @Field("pid") int pid);

    @Multipart
    @POST(ApiConstant.Forum.BUY_MAGIC)
    Observable<String> buyMagic(@PartMap Map<String, RequestBody> map);

    @Multipart
    @POST(ApiConstant.Forum.CONFIRM_USE_MAGIC)
    Observable<String> confirmUseMagic(@PartMap Map<String, RequestBody> map);

    @Multipart
    @POST(ApiConstant.Forum.CREDIT_HISTORY)
    Observable<String> getCreditHistory(@Query("page") int page,
                                        @PartMap Map<String, RequestBody> map);

    @POST(ApiConstant.Forum.MINE_CREDIT)
    Observable<String> getMineCredit();

    @Multipart
    @POST(ApiConstant.Forum.CREDIT_TRANSFER)
    Observable<String> creditTransfer(@PartMap Map<String, RequestBody> map);

    @POST(ApiConstant.Forum.GET_CREDIT_FORMHASH)
    Observable<String> getCreditFormHash();

    @POST(ApiConstant.Forum.GET_NEW_TASK)
    Observable<String> getNewTask();

    @POST(ApiConstant.Forum.GET_DOING_TASK)
    Observable<String> getDoingTask();

    @POST(ApiConstant.Forum.GET_DONE_TASK)
    Observable<String> getDoneTask();

    @POST(ApiConstant.Forum.GET_FAILED_TASK)
    Observable<String> getFailedTask();

    @POST(ApiConstant.User.GET_ONLINE_USER)
    Observable<String> getOnlineUser();

    @Multipart
    @POST(ApiConstant.Message.DELETE_ALL_PRIVATE_MSG)
    Observable<String> deletePrivateMsg(@PartMap Map<String, RequestBody> map);

    @FormUrlEncoded
    @POST(ApiConstant.Message.DELETE_SINGLE_PRIVATE_MSG)
    Observable<String> deleteSinglePrivateMsg(@Field("formhash") String formhash,
                                              @Field("handlekey") String handlekey,
                                              @Field("touid") int touid,
                                              @Field("deletepm_pmid[]") int pmid);

    @FormUrlEncoded
    @POST(ApiConstant.Forum.GET_TASK_DETAIL)
    Observable<String> getTaskDetail(@Field("id") int id);

    @FormUrlEncoded
    @POST(ApiConstant.Forum.APPLY_NEW_TASK)
    Observable<String> applyNewTask(@Field("id") int id);

    @FormUrlEncoded
    @POST(ApiConstant.Forum.GET_TASK_AWARD)
    Observable<String> getTaskAward(@Field("id") int id);

    @FormUrlEncoded
    @POST(ApiConstant.Forum.DELETE_DOING_TASK)
    Observable<String> deleteDoingTask(@Field("id") int id,
                                       @Field("formhash") String formhash);

    @FormUrlEncoded
    @POST(ApiConstant.Forum.VIEW_VOTER)
    Observable<String> getVoteOptions(@Field("tid") int tid);

    @FormUrlEncoded
    @POST(ApiConstant.Forum.VIEW_VOTER)
    Observable<String> viewVoter(@Field("tid") int tid,
                                 @Field("polloptionid") int polloptionid,
                                 @Field("page") int page);

    @FormUrlEncoded
    @POST(ApiConstant.Forum.GET_ALL_RATE_USER)
    Observable<String> getAllRateUser(@Field("tid") int tid,
                                      @Field("pid") int pid);

    @FormUrlEncoded
    @POST(ApiConstant.Forum.GET_ALL_RATE_USER)
    Call<String> getAllRateUser1(@Field("tid") int tid,
                                 @Field("pid") int pid);

    @FormUrlEncoded
    @POST(ApiConstant.Forum.FORUM_DETAIL)
    Observable<String> getForumDetail(@Field("fid") int fid);

    @FormUrlEncoded
    @POST(ApiConstant.Forum.VIEW_WARNING)
    Observable<String> viewWarning(@Field("tid") int tid,
                                   @Field("uid") int uid);

    @FormUrlEncoded
    @POST(ApiConstant.Forum.CHECK_BLACK)
    Observable<String> checkBlack(@Field("tid") int tid,
                                  @Field("fid") int fid,
                                  @Field("repquote") int quoteid);

    @Multipart
    @POST(ApiConstant.Forum.FIND_USERNAME)
    Observable<String> findUserName(@PartMap Map<String, RequestBody> map);

    @Multipart
    @POST(ApiConstant.Forum.RESET_PASSWORD)
    Observable<String> resetPassword(@PartMap Map<String, RequestBody> map);

    @Multipart
    @POST(ApiConstant.Forum.PAY_FOR_VISITING_FORUM)
    Observable<String> payForVisitingForum(@Query("fid") int tid,
                                           @PartMap Map<String, RequestBody> map);

    @FormUrlEncoded
    @POST(ApiConstant.Post.USE_REGRET_MAGIC)
    Observable<String> getUseRegretMagicDetail(@Field("id") String id);

    @Multipart
    @POST(ApiConstant.Post.CONFIRM_USE_REGRET_MAGIC)
    Observable<String> confirmUseRegretMagic(@PartMap Map<String, RequestBody> map);

    @FormUrlEncoded
    @POST(ApiConstant.User.ACCOUNT_BLACK_LIST)
    Observable<String> getAccountBlackList(@Field("page") int page);

    @GET(ApiConstant.Other.GET_DAY_QUESTION_ANSWER)
    Observable<DayQuestionAnswerBean> getDayQuestionAnswer(@Query("question") String question);

    @GET(ApiConstant.Other.SUBMIT_DAY_QUESTION_ANSWER)
    Observable<String> submitDayQuestionAnswer(@Query("question") String question,
                                               @Query("answer") String answer);

    @GET(ApiConstant.Other.GET_UPDATE_INFO)
    Observable<UpdateBean> getUpdateInfo(@Query("oldVersionCode") int oldVersionCode,
                                         @Query("isTest") boolean isTest);

}
