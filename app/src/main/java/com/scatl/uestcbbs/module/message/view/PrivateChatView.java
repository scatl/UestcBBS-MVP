package com.scatl.uestcbbs.module.message.view;

import com.scatl.uestcbbs.entity.PrivateChatBean;
import com.scatl.uestcbbs.entity.SendPrivateMsgResultBean;
import com.scatl.uestcbbs.entity.UploadResultBean;

import java.io.File;
import java.util.List;

/**
 * author: sca_tl
 * description:
 * date: 2020/1/29 19:24
 */
public interface PrivateChatView {
    void onGetPrivateListSuccess(PrivateChatBean privateChatBean);
    void onGetPrivateListError(String msg);
    void onSendPrivateChatMsgSuccess(SendPrivateMsgResultBean sendPrivateMsgResultBean);
    void onSendPrivateChatMsgError(String msg);
    void onCompressImageSuccess(List<File> compressedFiles);
    void onCompressImageFail(String msg);
    void onUploadSuccess(UploadResultBean uploadResultBean);
    void onUploadError(String msg);
    void showMsg(String msg);
    void onDeleteSinglePmSuccess(String msg, int position);
    void onDeleteSinglePmError(String msg);
}
