package com.scatl.uestcbbs.module.post.adapter;

import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.scatl.uestcbbs.R;
import com.scatl.uestcbbs.annotation.UserPostType;
import com.scatl.uestcbbs.entity.UserPostBean;
import com.scatl.uestcbbs.helper.glidehelper.GlideLoader4Common;
import com.scatl.uestcbbs.util.Constant;
import com.scatl.uestcbbs.util.TimeUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * author: sca_tl
 * description:
 * date: 2020/1/27 12:37
 */
public class UserPostAdapter extends BaseQuickAdapter<UserPostBean.ListBean, BaseViewHolder> {

    String type;
    int userId;
    boolean selfAccount;
    boolean hideAnonymousPost;

    public UserPostAdapter(int layoutResId, @UserPostType String type) {
        super(layoutResId);
        this.type = type;
    }

    public void init(int userId, boolean selfAccount, boolean hideAnonymousPost) {
        this.userId = userId;
        this.selfAccount = selfAccount;
        this.hideAnonymousPost = hideAnonymousPost;
    }

    public void addUserPostData(List<UserPostBean.ListBean> data, boolean refresh) {

        List<UserPostBean.ListBean> newList = new ArrayList<>();

        //对匿名帖进行过滤，忽略自己发表的帖子
        if (type.equals(UserPostType.TYPE_USER_POST)) {
            for (int i = 0; i < data.size(); i ++) {
                if (selfAccount) {//自己的主页则不隐藏自己的匿名贴
                    newList.add(data.get(i));
                } else {//别人的主页
                    if (hideAnonymousPost) {//进行了匿名处理
                        if (data.get(i).user_nick_name != null && data.get(i).user_nick_name.length() != 0) {
                            newList.add(data.get(i));
                        }
                    } else {
                        newList.add(data.get(i));
                    }
                }
            }
        } else if (type.equals(UserPostType.TYPE_USER_REPLY)) {//对回复的匿名贴进行匿名处理
            for (int i = 0; i < data.size(); i ++) {
                if (data.get(i).user_nick_name == null ||
                        data.get(i).user_nick_name.length() == 0 && hideAnonymousPost) {
                    data.get(i).user_nick_name = Constant.ANONYMOUS_NAME;
                    data.get(i).userAvatar = Constant.DEFAULT_AVATAR;
                    data.get(i).user_id = 0;
                }
                newList.add(data.get(i));
            }
        } else if (type.equals(UserPostType.TYPE_USER_FAVORITE)) { //对收藏的匿名贴进行匿名处理
            for (int i = 0; i < data.size(); i ++) {
                if (data.get(i).user_nick_name == null ||
                        data.get(i).user_nick_name.length() == 0 ||
                        "admin".equals(data.get(i).user_nick_name) && hideAnonymousPost) {
                    data.get(i).user_nick_name = Constant.ANONYMOUS_NAME;
                    data.get(i).userAvatar = Constant.DEFAULT_AVATAR;
                    data.get(i).user_id = 0;
                }
                newList.add(data.get(i));
            }
        }

        if (refresh) {
            setNewData(newList);
        } else {
            addData(newList);
        }
    }

    @Override
    protected void convert(BaseViewHolder helper, UserPostBean.ListBean item) {
        helper.setText(R.id.item_simple_post_user_name, item.user_nick_name)
                .setText(R.id.item_simple_post_board_name, item.board_name)
                .setText(R.id.item_simple_post_title, item.title)
                .setText(R.id.item_simple_post_comments_count, " " + item.replies)
                .setText(R.id.item_simple_post_zan_count, " 0")
                .setText(R.id.item_simple_post_content, String.valueOf(item.subject))
                .setText(R.id.item_simple_post_view_count, " " + item.hits)
                .setText(R.id.item_simple_post_time,
                        TimeUtil.formatTime(String.valueOf(item.last_reply_date), R.string.reply_time, mContext))
                .addOnClickListener(R.id.item_simple_post_user_avatar)
                .addOnClickListener(R.id.item_simple_post_board_name);

        helper.getView(R.id.item_simple_post_poll_rl).setVisibility(View.GONE);

        ImageView avatarImg = helper.getView(R.id.item_simple_post_user_avatar);
        if (item.user_id == 0 && "匿名".equals(item.user_nick_name)) {
            GlideLoader4Common.simpleLoad(mContext, R.drawable.ic_anonymous, avatarImg);
        } else {
            GlideLoader4Common.simpleLoad(mContext, item.userAvatar, avatarImg);
        }
    }
}
