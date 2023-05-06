package com.scatl.uestcbbs.entity;

import java.util.List;

/**
 * author: sca_tl
 * date: 2020/5/4 13:19
 * description:
 */
public class CollectionDetailBean {

    public String collectionTitle;
    public boolean isSubscribe;
    public String subscribeCount;
    public float ratingScore;
    public String ratingTitle;
    public String collectionDsp;
    public List<String> collectionTags;
    public String collectionAuthorName;
    public String collectionAuthorAvatar;
    public int collectionAuthorId;
    public String collectionAuthorLink;
    public List<String> maintainerName;

    public List<PostListBean> postListBean;
    public List<RecentSubscriberBean> mRecentSubscriberBean;
    public List<AuthorOtherCollection> authorOtherCollection;
    public List<SameOwnerCollection> mSameOwnerCollection;

    public static class SameOwnerCollection {
        public int cid;
        public String name;
    }

    public static class PostListBean {
        public String topicTitle;
        public String topicLink;
        public int topicId;
        public boolean hasPic;
        public boolean hasAttach;
        public String authorName;
        public int authorId;
        public String authorLink;
        public String authorAvatar;
        public String postDate;
        public String commentCount;
        public String viewCount;
        public String lastPostAuthorName;
        public int lastPostAuthorId;
        public String lastPostAuthorAvatar;
        public String lastPostAuthorLink;
        public String lastPostDate;
    }

    public static class RecentSubscriberBean {
        public String userName;
        public String userAvatar;
        public int userId;
    }

    public static class AuthorOtherCollection {
        public String title;
        public String link;
        public int postCount;
        public int subscribeCount;
        public int commentCount;
    }

}
