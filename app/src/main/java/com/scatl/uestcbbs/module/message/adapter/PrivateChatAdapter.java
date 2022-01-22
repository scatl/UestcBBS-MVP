package com.scatl.uestcbbs.module.message.adapter;

import android.content.Context;
import android.text.Html;
import android.view.View;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.scatl.uestcbbs.R;
import com.scatl.uestcbbs.custom.postview.MyImageGetter;
import com.scatl.uestcbbs.entity.PrivateChatBean;
import com.scatl.uestcbbs.helper.glidehelper.GlideLoader4Common;
import com.scatl.uestcbbs.util.SharePrefUtil;
import com.scatl.uestcbbs.util.TimeUtil;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * author: sca_tl
 * description:
 * date: 2020/1/29 19:13
 */
public class PrivateChatAdapter extends BaseQuickAdapter<PrivateChatBean.BodyBean.PmListBean.MsgListBean, BaseViewHolder> {

    private String hisName, hisAvatar;
    private int hisUid;

    public PrivateChatAdapter(int layoutResId) {
        super(layoutResId);
    }

    public void setHisInfo(String hisName, String hisAvatar, int hisUid) {
        this.hisName = hisName;
        this.hisAvatar = hisAvatar;
        this.hisUid = hisUid;
    }

    public void insertMsg(Context context, String content, String type) {

        PrivateChatBean.BodyBean.PmListBean.MsgListBean msgListBean = new PrivateChatBean.BodyBean.PmListBean.MsgListBean();
        msgListBean.type = type;
        msgListBean.sender = SharePrefUtil.getUid(context);
        msgListBean.time = String.valueOf(System.currentTimeMillis());
        msgListBean.content = content;

        addData(msgListBean);
    }

    public void deleteMsg(int position) {
        getData().remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, getData().size());
    }

    @Override
    protected void convert(BaseViewHolder helper, PrivateChatBean.BodyBean.PmListBean.MsgListBean item) {

        int mine_id = SharePrefUtil.getUid(mContext);
        String mine_avatar = SharePrefUtil.getAvatar(mContext);

        if (item.sender == mine_id) {
            helper.addOnClickListener(R.id.item_private_chat_mine_img);
            helper.getView(R.id.item_private_chat_his_rl).setVisibility(View.GONE);
            helper.getView(R.id.item_private_chat_mine_rl).setVisibility(View.VISIBLE);

            GlideLoader4Common.simpleLoad(mContext, mine_avatar, helper.getView(R.id.item_private_chat_mine_icon));

            if (item.type.equals("text")) {
                helper.getView(R.id.item_private_chat_mine_content).setVisibility(View.VISIBLE);
                helper.getView(R.id.item_private_chat_mine_img).setVisibility(View.GONE);
//                helper.setText(R.id.item_private_chat_mine_content, item.content);
                setTextWithEmotion(helper.getView(R.id.item_private_chat_mine_content), item.content, false);

            }
            if (item.type.equals("image")) {
                helper.getView(R.id.item_private_chat_mine_content).setVisibility(View.GONE);
                helper.getView(R.id.item_private_chat_mine_img).setVisibility(View.VISIBLE);
                GlideLoader4Common.simpleLoad(mContext, item.content, helper.getView(R.id.item_private_chat_mine_img));
            }

            helper.setText(R.id.item_private_chat_mine_time, TimeUtil.formatTime(item.time, R.string.post_time1, mContext));

        } else {
            helper.addOnClickListener(R.id.item_private_chat_his_img);
            helper.getView(R.id.item_private_chat_mine_rl).setVisibility(View.GONE);
            helper.getView(R.id.item_private_chat_his_rl).setVisibility(View.VISIBLE);

            if (item.type.equals("text")) {
                helper.getView(R.id.item_private_chat_his_content).setVisibility(View.VISIBLE);
                helper.getView(R.id.item_private_chat_his_img).setVisibility(View.GONE);
//                helper.setText(R.id.item_private_chat_his_content, item.content);
                setTextWithEmotion(helper.getView(R.id.item_private_chat_his_content), item.content, false);
            }
            if (item.type.equals("image")) {
                helper.getView(R.id.item_private_chat_his_content).setVisibility(View.GONE);
                helper.getView(R.id.item_private_chat_his_img).setVisibility(View.VISIBLE);
                GlideLoader4Common.simpleLoad(mContext, item.content, helper.getView(R.id.item_private_chat_his_img));
            }

            GlideLoader4Common.simpleLoad(mContext, hisAvatar, helper.getView(R.id.item_private_chat_his_icon));

            helper.setText(R.id.item_private_chat_his_time,
                    TimeUtil.formatTime(item.time, R.string.post_time1, mContext));

        }
    }

    private void setTextWithEmotion(final TextView textView, String text, boolean append) {
        final Matcher matcher = Pattern.compile("(\\[mobcent_phiz=(.*?)])").matcher(text);

        if (matcher.find()) {
            do {
                text = text.replace(matcher.group(0)+"", "<img src = " + matcher.group(2) + ">");
            } while (matcher.find());
            text = text.replaceAll("\n", "<br>");
            if (append) {
                textView.append(Html.fromHtml(text, new MyImageGetter(mContext, textView), null));
            } else {
                textView.setText(Html.fromHtml(text, new MyImageGetter(mContext, textView), null));
            }

        } else {
            if (append) {
                textView.append(text);
            } else {
                textView.setText(text);
            }
        }

    }
}
