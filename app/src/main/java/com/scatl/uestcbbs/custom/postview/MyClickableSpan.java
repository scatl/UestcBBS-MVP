package com.scatl.uestcbbs.custom.postview;

import android.content.Context;
import android.content.Intent;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;

import androidx.annotation.NonNull;

import com.scatl.uestcbbs.R;
import com.scatl.uestcbbs.module.post.view.PostDetailActivity;
import com.scatl.uestcbbs.module.user.view.UserDetailActivity;
import com.scatl.uestcbbs.util.CommonUtil;
import com.scatl.uestcbbs.util.Constant;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MyClickableSpan extends ClickableSpan {
    private String url;
    private Context context;

    public MyClickableSpan(Context context, String url) {
        this.context = context;
        this.url = url.replaceAll(" ", "").replaceAll("\n", "");
    }

    @Override
    public void updateDrawState(TextPaint ds) {
        ds.setUnderlineText(true);
        ds.setColor(CommonUtil.getAttrColor(context, R.attr.colorPrimary));
    }

    @Override
    public void onClick(@NonNull View widget) {
        //点击跳转到链接
        //河畔帖子链接：(1)http://bbs.uestc.edu.cn/forum.php?mod=viewthread&tid=xxxxx(或者xxxxx&yyyyy)
        //河畔帖子链接：(2)http://bbs.uestc.edu.cn/forum.php?mod=redirect&goto=findpost&ptid=xxxx&pid=xxxx
        //河畔帖子链接：(3)http://bbs.stuhome.net/forum.php?mod=viewthread&tid=xxxxxx
        //河畔帖子链接：(4)http://bbs.stuhome.net/read.php?tid=xxxx
        //个人详情链接：http://bbs.uestc.edu.cn/home.php?mod=space&uid=xxx(或者xxxxx&yyyyy)
        Matcher post_matcher1 = Pattern.compile("http://bbs\\.uestc\\.edu\\.cn/forum\\.php\\?mod=viewthread&tid=(\\d+)").matcher(url);
        Matcher post_matcher2 = Pattern.compile("http://bbs\\.uestc\\.edu\\.cn/forum\\.php\\?mod=viewthread&tid=(\\d+)(&)(.*)").matcher(url);
        Matcher post_matcher3 = Pattern.compile("http://bbs\\.uestc\\.edu\\.cn/forum\\.php\\?mod=redirect&goto=findpost&ptid=(\\d+)").matcher(url);
        Matcher post_matcher4 = Pattern.compile("http://bbs\\.uestc\\.edu\\.cn/forum\\.php\\?mod=redirect&goto=findpost&ptid=(\\d+)(&)(.*)").matcher(url);
        Matcher post_matcher5 = Pattern.compile("http://bbs\\.stuhome\\.net/forum\\.php\\?mod=viewthread&tid=(\\d+)").matcher(url);
        Matcher post_matcher6 = Pattern.compile("http://bbs\\.stuhome\\.net/forum\\.php\\?mod=viewthread&tid=(\\d+)(&)(.*)").matcher(url);
        Matcher post_matcher7 = Pattern.compile("http://bbs\\.stuhome\\.net/read\\.php\\?tid=(\\d+)").matcher(url);
        Matcher post_matcher8 = Pattern.compile("http://bbs\\.stuhome\\.net/read\\.php\\?tid=(\\d+)(&)(.*)").matcher(url);

        Matcher user_matcher1 = Pattern.compile("http://bbs\\.uestc\\.edu\\.cn/home\\.php\\?mod=space&uid=(\\d+)").matcher(url);
        Matcher user_matcher2 = Pattern.compile("http://bbs\\.uestc\\.edu\\.cn/home\\.php\\?mod=space&uid=(\\d+)(&)(.*)").matcher(url);

        if (post_matcher1.find()) {
            int topic_id = Integer.valueOf(post_matcher1.group(1));
            Intent intent = new Intent(context, PostDetailActivity.class);
            intent.putExtra("topic_id", topic_id);
            context.startActivity(intent);
        } else if (post_matcher2.find()) {
            int topic_id = Integer.valueOf(post_matcher2.group(1));
            Intent intent = new Intent(context, PostDetailActivity.class);
            intent.putExtra("topic_id", topic_id);
            context.startActivity(intent);
        } else if (post_matcher3.find()) {
            int topic_id = Integer.valueOf(post_matcher3.group(1));
            Intent intent = new Intent(context, PostDetailActivity.class);
            intent.putExtra("topic_id", topic_id);
            context.startActivity(intent);
        } else if (post_matcher4.find()) {
            int topic_id = Integer.valueOf(post_matcher4.group(1));
            Intent intent = new Intent(context, PostDetailActivity.class);
            intent.putExtra("topic_id", topic_id);
            context.startActivity(intent);
        } else if (post_matcher5.find()) {
            int topic_id = Integer.valueOf(post_matcher5.group(1));
            Intent intent = new Intent(context, PostDetailActivity.class);
            intent.putExtra("topic_id", topic_id);
            context.startActivity(intent);
        } else if (post_matcher6.find()) {
            int topic_id = Integer.valueOf(post_matcher6.group(1));
            Intent intent = new Intent(context, PostDetailActivity.class);
            intent.putExtra("topic_id", topic_id);
            context.startActivity(intent);
        } else if (post_matcher7.find()) {
            int topic_id = Integer.valueOf(post_matcher7.group(1));
            Intent intent = new Intent(context, PostDetailActivity.class);
            intent.putExtra("topic_id", topic_id);
            context.startActivity(intent);
        } else if (post_matcher8.find()) {
            int topic_id = Integer.valueOf(post_matcher8.group(1));
            Intent intent = new Intent(context, PostDetailActivity.class);
            intent.putExtra("topic_id", topic_id);
            context.startActivity(intent);
        } else if (user_matcher1.find()) {
            int uid = Integer.valueOf(user_matcher1.group(1));
            Intent intent = new Intent(context, UserDetailActivity.class);
            intent.putExtra("user_id", uid);
            context.startActivity(intent);
        } else if (user_matcher2.find()) {
            int uid = Integer.valueOf(user_matcher2.group(1));
            Intent intent = new Intent(context, UserDetailActivity.class);
            intent.putExtra("user_id", uid);
            context.startActivity(intent);
        } else {
            CommonUtil.openBrowser(context, url);
        }
    }
}
