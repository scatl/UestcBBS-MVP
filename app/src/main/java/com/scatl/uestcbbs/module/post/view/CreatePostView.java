package com.scatl.uestcbbs.module.post.view;

import com.scatl.uestcbbs.entity.AttachmentBean;
import com.scatl.uestcbbs.entity.CommonPostBean;
import com.scatl.uestcbbs.entity.SendPostBean;
import com.scatl.uestcbbs.entity.UploadResultBean;
import com.scatl.uestcbbs.entity.UserDetailBean;
import com.scatl.uestcbbs.entity.UserPostBean;

import java.io.File;
import java.util.List;

/**
 * author: sca_tl
 * description:
 * date: 2020/2/9 13:52
 */
public interface CreatePostView {
    void onSendPostSuccess(SendPostBean sendPostBean);
    void onSendPostError(String msg);
    void onUploadSuccess(UploadResultBean uploadResultBean);
    void onUploadError(String msg);
    void onCompressImageSuccess(List<File> compressedFiles);
    void onCompressImageFail(String msg);
    void onPermissionGranted(int action);
    void onPermissionRefused();
    void onPermissionRefusedWithNoMoreRequest();
    void onStartUploadAttachment();
    void onUploadAttachmentSuccess(AttachmentBean attachmentBean, String msg);
    void onUploadAttachmentError(String msg);
    void onSendPostSuccessBack();
    void onSendPostSuccessViewPost();
    void onGetUserPostSuccess(CommonPostBean userPostBean);
    void onGetUserPostError(String msg);
    void onMoreOptionsChanged(boolean isAnonymous, boolean isOnlyAuthor, boolean originalPic);
    void onSanShuiSuccess(int tid);
    void onSanShuiError(String msg);
    void onGetUserDetailSuccess(UserDetailBean userDetailBean);
}
