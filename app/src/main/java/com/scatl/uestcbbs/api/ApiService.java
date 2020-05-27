package com.scatl.uestcbbs.api;

import com.scatl.uestcbbs.entity.AlbumListBean;
import com.scatl.uestcbbs.entity.AtMsgBean;
import com.scatl.uestcbbs.entity.AtUserListBean;
import com.scatl.uestcbbs.entity.BingPicBean;
import com.scatl.uestcbbs.entity.BlackUserBean;
import com.scatl.uestcbbs.entity.FavoritePostResultBean;
import com.scatl.uestcbbs.entity.FollowUserBean;
import com.scatl.uestcbbs.entity.ForumListBean;
import com.scatl.uestcbbs.entity.HeartMsgBean;
import com.scatl.uestcbbs.entity.HotPostBean;
import com.scatl.uestcbbs.entity.LoginBean;
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
import com.scatl.uestcbbs.entity.SimplePostListBean;
import com.scatl.uestcbbs.entity.SingleBoardBean;
import com.scatl.uestcbbs.entity.SubForumListBean;
import com.scatl.uestcbbs.entity.SupportResultBean;
import com.scatl.uestcbbs.entity.SystemMsgBean;
import com.scatl.uestcbbs.entity.UpdateBean;
import com.scatl.uestcbbs.entity.UploadResultBean;
import com.scatl.uestcbbs.entity.UserDetailBean;
import com.scatl.uestcbbs.entity.UserFriendBean;
import com.scatl.uestcbbs.entity.UserPostBean;
import com.scatl.uestcbbs.entity.VoteResultBean;

import java.util.Map;

import io.reactivex.Observable;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Response;
import retrofit2.http.Body;
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

    //更新
    @POST(ApiConstant.UPDATE_URL)
    Observable<UpdateBean> getUpdate();

    //首页通知
    @POST(ApiConstant.NOTICE_URL)
    Observable<NoticeBean> getNotice();

    //软件设置
    @POST(ApiConstant.SETTINGS_URL)
    Observable<SettingsBean> getSettings();

    //上传文件
//    @Multipart
    @POST(ApiConstant.SendMessage.UPLOAD_IMG)
    Observable<UploadResultBean> uploadImage(@Query("module") String module,
                                             @Query("type") String type,
                                             @Query("accessToken") String token,
                                             @Query("accessSecret") String secret,
                                             @Body MultipartBody multipartBody);

    @GET(ApiConstant.BING_PIC)
    Observable<BingPicBean> getBingPic();

    @Multipart
    @POST(ApiConstant.Forum.LOGIN_FOR_COOKIES)
    Observable<Response<ResponseBody>> loginForCookies(@PartMap Map<String, RequestBody> map);

    @FormUrlEncoded
    @POST(ApiConstant.User.LOGIN_URL)
    Observable<LoginBean> login(@Field("username") String username,
                                @Field("password") String password);

    @FormUrlEncoded
    @POST(ApiConstant.User.AT_USER_LIST)
    Observable<AtUserListBean> atUserList(
                                    @Field("page") int page,
                                    @Field("pageSize") int pageSize,
                                    @Field("accessToken") String token,
                                    @Field("accessSecret") String secret);

    @FormUrlEncoded
    @POST(ApiConstant.Post.SIMPLE_POST)
    Observable<SimplePostListBean> getSimplePostList(
                                @Field("page") int page,
                                @Field("pageSize") int pageSize,
                                @Field("boardId") int boardId,
                                @Field("sortby") String sortby,
                                @Field("accessToken") String token,
                                @Field("accessSecret") String secret);

    @FormUrlEncoded
    @POST(ApiConstant.Post.HOT_POST)
    Observable<HotPostBean> getHotPostList(
                                @Field("page") int page,
                                @Field("pageSize") int pageSize,
                                @Field("moduleId") int boardId,
                                @Field("accessToken") String token,
                                @Field("accessSecret") String secret);

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
    @POST(ApiConstant.Post.FORUM_TOPIC_LIST)
    Observable<SingleBoardBean> getSingleBoardPostList(
                                @Field("page") int page,
                                @Field("pageSize") int pageSize,
                                @Field("topOrder") int topOrder,
                                @Field("boardId") int boardId,
                                @Field("filterId") int filterId,
                                @Field("filterType") String filterType,
                                @Field("sortby") String sortby,
                                @Field("accessToken") String token,
                                @Field("accessSecret") String secret);

    @FormUrlEncoded
    @POST(ApiConstant.SendMessage.SEND_POST_AND_REPLY)
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
                                @Field("accessToken") String token,
                                @Field("accessSecret") String secret);

    @FormUrlEncoded
    @POST(ApiConstant.Post.FAVORITE_POST)
    Observable<FavoritePostResultBean> favorite(
                                @Field("idType") String idType,
                                @Field("action") String action,
                                @Field("id") int id,
                                @Field("accessToken") String token,
                                @Field("accessSecret") String secret);

    @FormUrlEncoded
    @POST(ApiConstant.Post.SEARCH_POST)
    Observable<SearchPostBean> searchPost(
                                @Field("page") int page,
                                @Field("pageSize") int pageSize,
                                @Field("keyword") String keyword,
                                @Field("accessToken") String token,
                                @Field("accessSecret") String secret);

    @FormUrlEncoded
    @POST(ApiConstant.User.USER_POST)
    Observable<UserPostBean> userPost(
                                @Field("page") int page,
                                @Field("pageSize") int pageSize,
                                @Field("uid") int uid,
                                @Field("type") String type,
                                @Field("accessToken") String token,
                                @Field("accessSecret") String secret);

    @FormUrlEncoded
    @POST(ApiConstant.User.USER_INFO)
    Observable<UserDetailBean> userDetail(
                                @Field("userId") int uid,
                                @Field("accessToken") String token,
                                @Field("accessSecret") String secret);

    @FormUrlEncoded
    @POST(ApiConstant.User.FOLLOW_USER)
    Observable<FollowUserBean> followUser(
                                @Field("uid") int uid,
                                @Field("type") String type,
                                @Field("accessToken") String token,
                                @Field("accessSecret") String secret);

    @FormUrlEncoded
    @POST(ApiConstant.User.BLACK_USER)
    Observable<BlackUserBean> blackUser(
                                @Field("uid") int uid,
                                @Field("type") String type,
                                @Field("accessToken") String token,
                                @Field("accessSecret") String secret);

    @FormUrlEncoded
    @POST(ApiConstant.User.FOLLOW_LIST)
    Observable<UserFriendBean> userFriend(
                                @Field("page") int page,
                                @Field("pageSize") int pageSize,
                                @Field("uid") int uid,
                                @Field("type") String type,
                                @Field("accessToken") String token,
                                @Field("accessSecret") String secret);

    @FormUrlEncoded
    @POST(ApiConstant.User.MODIFY_SIGN)
    Observable<ModifySignBean> modifySign(
                                @Field("type") String type,
                                @Field("sign") String sign,
                                @Field("accessToken") String token,
                                @Field("accessSecret") String secret);

    @FormUrlEncoded
    @POST(ApiConstant.User.MODIFY_PSW)
    Observable<ModifyPswBean> modifyPsw(
                                @Field("type") String type,
                                @Field("oldPassword") String oldPsw,
                                @Field("newPassword") String newPsw,
                                @Field("accessToken") String token,
                                @Field("accessSecret") String secret);

    @FormUrlEncoded
    @POST(ApiConstant.User.SEARCH_USER)
    Observable<SearchUserBean> searchUser(
                                @Field("page") int page,
                                @Field("pageSize") int pageSize,
                                @Field("searchid") int searchId,
                                @Field("keyword") String keyword,
                                @Field("accessToken") String token,
                                @Field("accessSecret") String secret);

    @FormUrlEncoded
    @POST(ApiConstant.User.REPORT)
    Observable<ReportBean> report(
                                @Field("idType") String idType,
                                @Field("message") String message,
                                @Field("id") int id,
                                @Field("accessToken") String token,
                                @Field("accessSecret") String secret);

    @FormUrlEncoded
    @POST(ApiConstant.User.ALBUM_LIST)
    Observable<AlbumListBean> albumList(
                                @Field("uid") int uid,
                                @Field("page") int page,
                                @Field("pageSize") int pageSize,
                                @Field("accessToken") String token,
                                @Field("accessSecret") String secret);

    @FormUrlEncoded
    @POST(ApiConstant.User.PHOTO_LIST)
    Observable<PhotoListBean> photoList(
                                @Field("uid") int uid,
                                @Field("albumId") int albumId,
                                @Field("page") int page,
                                @Field("pageSize") int pageSize,
                                @Field("accessToken") String token,
                                @Field("accessSecret") String secret);

    @FormUrlEncoded
    @POST(ApiConstant.Message.SYSTEM_MESSAGE)
    Observable<SystemMsgBean> systemMsg(
                                @Field("page") int page,
                                @Field("pageSize") int pageSize,
                                @Field("accessToken") String token,
                                @Field("accessSecret") String secret);

    @FormUrlEncoded
    @POST(ApiConstant.Message.AT_ME_MESSAGE)
    Observable<AtMsgBean> atMsg(
                                @Field("page") int page,
                                @Field("pageSize") int pageSize,
                                @Field("accessToken") String token,
                                @Field("accessSecret") String secret);

    @FormUrlEncoded
    @POST(ApiConstant.Message.REPLY_ME_MESSAGE)
    Observable<ReplyMeMsgBean> replyMeMsg(
                                @Field("page") int page,
                                @Field("pageSize") int pageSize,
                                @Field("accessToken") String token,
                                @Field("accessSecret") String secret);

    @FormUrlEncoded
    @POST(ApiConstant.Message.PRIVATE_MSG)
    Observable<PrivateMsgBean> privateMsg(
                                @Field("json") String json,
                                @Field("accessToken") String token,
                                @Field("accessSecret") String secret);

    @FormUrlEncoded
    @POST(ApiConstant.Message.PRIVATE_CHAT_MSG_LIST)
    Observable<PrivateChatBean> privateChatMsgList(
                                @Field("pmlist") String json,
                                @Field("accessToken") String token,
                                @Field("accessSecret") String secret);

    @FormUrlEncoded
    @POST(ApiConstant.SendMessage.SEND_PRIVATE_MSG)
    Observable<SendPrivateMsgResultBean> sendPrivateMsg(
                                @Field("json") String json,
                                @Field("accessToken") String token,
                                @Field("accessSecret") String secret);

    @FormUrlEncoded
    @POST(ApiConstant.Post.VOTE)
    Observable<VoteResultBean> vote(
                                @Field("tid") int tid,
                                @Field("boardId") int boardId,
                                @Field("options") String options,
                                @Field("accessToken") String token,
                                @Field("accessSecret") String secret);

    @FormUrlEncoded
    @POST(ApiConstant.Post.RATE_INFO)
    Observable<String> rateInfo(@Field("tid") int tid,
                                @Field("pid") int pid,
                                @Field("accessToken") String token,
                                @Field("accessSecret") String secret);

    @FormUrlEncoded
    @POST(ApiConstant.Post.RATE)
    Observable<String> rate(@Field("tid") int tid,
                            @Field("pid") int pid,
                            @Field("score2") int score,
                            @Field("reason") String reason,
                            @Field("sendreasonpm") String sendreasonpm,
                            @Field("accessToken") String token,
                            @Field("accessSecret") String secret);

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
    @POST(ApiConstant.Forum.FORUM_LIST)
    Observable<ForumListBean> forumList(
                                @Field("accessToken") String token,
                                @Field("accessSecret") String secret);

    @FormUrlEncoded
    @POST(ApiConstant.Forum.SUB_FORUM_LIST)
    Observable<SubForumListBean> subForumList(
                                @Field("fid") int fid,
                                @Field("accessToken") String token,
                                @Field("accessSecret") String secret);

    @FormUrlEncoded
    @POST(ApiConstant.Message.HEART_MSG)
    Observable<HeartMsgBean> getHeartMsg(
                                @Field("sdkVersion") String sdkVersion,
                                @Field("accessToken") String token,
                                @Field("accessSecret") String secret);

    @POST(ApiConstant.Forum.GRAB_SOFA)
    Observable<String> grabSofa();

    @FormUrlEncoded
    @POST(ApiConstant.Collection.TAO_TIE_COLLECTION)
    Observable<String> taoTieCollection(@Field("page") int page,
                                        @Field("op") String op);

    @FormUrlEncoded
    @POST(ApiConstant.Collection.TAO_TIE_POST_LIST)
    Observable<String> collectionDetail(@Field("ctid") int ctid,
                                        @Field("page") int page);

    @FormUrlEncoded
    @POST(ApiConstant.Collection.SUBSCRIBE_COLLECTION)
    Observable<String> subscribeCollection(@Field("ctid") int ctid,
                                            @Field("op") String op,
                                           @Field("formhash") String formhash);

    @POST(ApiConstant.Collection.MY_COLLECTION)
    Observable<String> myCollection();

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
}
