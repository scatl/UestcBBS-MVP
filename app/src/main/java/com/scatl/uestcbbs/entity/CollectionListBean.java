package com.scatl.uestcbbs.entity;

import androidx.annotation.NonNull;

import com.scatl.uestcbbs.util.CommonUtil;

import java.lang.reflect.Field;
import java.util.List;

/**
 * author: sca_tl
 * date: 2020/5/3 11:13
 * description:
 */
public class CollectionListBean {
    public String collectionLink;//专辑链接
    public String collectionDsp;//专辑描述
    public int collectionId;//专辑id
    public String postCount;//主题数
    public String collectionTitle;//专辑标题
    public int authorId;//作者id
    public String authorName;//作者昵称
    public String authorLink;//作者链接
    public String authorAvatar;//作者头像
    public String subscribeCount;//订阅数
    public String commentCount;//评论数
    public String latestUpdateDate;//最近更新时间
    public String latestPostTitle;//最新主题标题
    public String latestPostLink;//最新主题链接
    public int latestPostId;//最新主题id
    public List<String> collectionTags;
    public boolean createByMe;
    public boolean subscribeByMe;
    public boolean hasUnreadPost;
}
