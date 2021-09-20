package com.scatl.uestcbbs.module.post.view;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.scatl.uestcbbs.R;
import com.scatl.uestcbbs.annotation.ToastType;
import com.scatl.uestcbbs.base.BaseDialogFragment;
import com.scatl.uestcbbs.base.BaseEvent;
import com.scatl.uestcbbs.base.BasePresenter;
import com.scatl.uestcbbs.custom.MyLinearLayoutManger;
import com.scatl.uestcbbs.custom.emoticon.EmoticonPanelLayout;
import com.scatl.uestcbbs.entity.AttachmentBean;
import com.scatl.uestcbbs.entity.ReplyDraftBean;
import com.scatl.uestcbbs.entity.SendPostBean;
import com.scatl.uestcbbs.entity.UploadResultBean;
import com.scatl.uestcbbs.helper.glidehelper.GlideEngineForPictureSelector;
import com.scatl.uestcbbs.module.post.adapter.AttachmentAdapter;
import com.scatl.uestcbbs.module.post.adapter.CreateCommentImageAdapter;
import com.scatl.uestcbbs.module.post.presenter.CreateCommentPresenter;
import com.scatl.uestcbbs.module.user.view.AtUserListActivity;
import com.scatl.uestcbbs.module.user.view.AtUserListFragment;
import com.scatl.uestcbbs.util.CommonUtil;
import com.scatl.uestcbbs.util.Constant;

import com.scatl.uestcbbs.util.FileUtil;
import com.scatl.uestcbbs.util.SharePrefUtil;

import org.greenrobot.eventbus.EventBus;
import org.litepal.LitePal;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * author: sca_tl
 * description:
 * date: 2020/1/25 13:04
 */
public class CreateCommentFragment extends BaseDialogFragment implements CreateCommentView, CompoundButton.OnCheckedChangeListener {

    private static final String TAG = "CreateCommentFragment";

    private AppCompatEditText content;
    private TextView cancelText, replyText;
    private ImageView atBtn, addImgBtn, addEmotionBtn, replyBtn, addAttachment;
    private RecyclerView imageRecyclerView, attachmentRecyclerView;
    private CreateCommentImageAdapter imageAdapter;
    private AttachmentAdapter attachmentAdapter;
    private ProgressDialog progressDialog;
    private EmoticonPanelLayout emoticonPanelLayout;
    private CheckBox anonymous, refreshAfterSend;

    private CreateCommentPresenter createCommentPresenter;

    private int board_id, topic_id, quote_id;
    private boolean is_quote;
    private String user_name;//被回复的人的昵称

    private Map<String, Integer> attachments = new LinkedHashMap<>(); //附件

    private static final int ACTION_ADD_PHOTO = 12;
    private static final int AT_USER_REQUEST = 16;
    private static final int ACTION_ADD_ATTACHMENT = 19;
    private static final int ADD_ATTACHMENT_REQUEST = 23;

    private boolean sendSuccess;

    public static CreateCommentFragment getInstance(Bundle bundle) {
        CreateCommentFragment createCommentFragment = new CreateCommentFragment();
        createCommentFragment.setArguments(bundle);
        return createCommentFragment;
    }

    @Override
    protected void getBundle(Bundle bundle) {
        if (bundle != null) {
            board_id = bundle.getInt(Constant.IntentKey.BOARD_ID, Integer.MAX_VALUE);
            topic_id = bundle.getInt(Constant.IntentKey.TOPIC_ID, Integer.MAX_VALUE);
            quote_id = bundle.getInt(Constant.IntentKey.QUOTE_ID, Integer.MAX_VALUE);
            is_quote = bundle.getBoolean(Constant.IntentKey.IS_QUOTE, false);
            user_name = bundle.getString(Constant.IntentKey.USER_NAME);
        }
    }

    @Override
    protected int setLayoutResourceId() {
        return R.layout.fragment_post_create_comment;
    }

    @Override
    protected void findView() {
        content = view.findViewById(R.id.post_create_comment_fragment_content);
        cancelText = view.findViewById(R.id.post_create_comment_fragment_cancel);
        replyText = view.findViewById(R.id.post_create_comment_fragment_reply);
        atBtn = view.findViewById(R.id.post_create_comment_fragment_at_btn);
        addImgBtn = view.findViewById(R.id.post_create_comment_fragment_add_image_btn);
        addEmotionBtn = view.findViewById(R.id.post_create_comment_fragment_add_emotion_btn);
        replyBtn = view.findViewById(R.id.post_create_comment_fragment_send_btn);
        imageRecyclerView = view.findViewById(R.id.post_create_comment_fragment_image_rv);
        emoticonPanelLayout = view.findViewById(R.id.post_create_comment_emoticon_layout);
        addAttachment = view.findViewById(R.id.post_create_comment_fragment_add_attachment_btn);
        attachmentRecyclerView = view.findViewById(R.id.post_create_comment_fragment_attachment_rv);
        anonymous = view.findViewById(R.id.post_create_comment_fragment_anonymous);
        refreshAfterSend = view.findViewById(R.id.post_create_comment_fragment_refresh_after_send);
    }

    @Override
    protected void initView() {
        createCommentPresenter = (CreateCommentPresenter) presenter;

        atBtn.setOnClickListener(this);
        addImgBtn.setOnClickListener(this);
        addEmotionBtn.setOnClickListener(this);
        replyBtn.setOnClickListener(this);
        cancelText.setOnClickListener(this);
        replyText.setOnClickListener(this);
        addAttachment.setOnClickListener(this);
        content.setOnClickListener(this);
        anonymous.setOnCheckedChangeListener(this);
        refreshAfterSend.setOnCheckedChangeListener(this);

        refreshAfterSend.setChecked(SharePrefUtil.isRefreshOnReplySuccess(mActivity));

        anonymous.setVisibility(board_id == Constant.MIYU_BOARD_ID ? View.VISIBLE : View.GONE);

        content.setHint("回复：" + user_name);
        CommonUtil.showSoftKeyboard(mActivity, content, 100);

        //图片
        imageAdapter = new CreateCommentImageAdapter(R.layout.item_post_create_comment_image);
        imageAdapter.setHasStableIds(true);
        LinearLayoutManager linearLayoutManager = new MyLinearLayoutManger(mActivity);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        imageRecyclerView.setLayoutManager(linearLayoutManager);
        imageRecyclerView.setAdapter(imageAdapter);

        //附件
        attachmentAdapter = new AttachmentAdapter(R.layout.item_attachment);
        LinearLayoutManager linearLayoutManager1 = new MyLinearLayoutManger(mActivity);
        linearLayoutManager1.setOrientation(LinearLayoutManager.HORIZONTAL);
        attachmentRecyclerView.setLayoutManager(linearLayoutManager1);
        attachmentRecyclerView.setAdapter(attachmentAdapter);

        progressDialog = new ProgressDialog(mActivity);
        progressDialog.setTitle("发送消息");
        progressDialog.setCancelable(false);

        List<ReplyDraftBean> data = LitePal
                .where("reply_id = ?", String.valueOf(is_quote ? quote_id : topic_id))
                .find(ReplyDraftBean.class);
        if (data != null && data.size() > 0) {
            content.setText(data.get(0).content);
            content.setSelection(content.length());
            imageAdapter.setNewData(CommonUtil.toList(data.get(0).images));
        }
    }

    @Override
    protected BasePresenter initPresenter() {
        return new CreateCommentPresenter();
    }

    @Override
    protected void setOnItemClickListener() {
        imageAdapter.setOnItemChildClickListener((adapter, view, position) -> {
            if (view.getId() == R.id.item_post_create_comment_deleta_img){
                imageAdapter.delete(position);
            }
        });

        attachmentAdapter.setOnItemChildClickListener((adapter, view1, position) -> {
            if (view1.getId() == R.id.item_attachment_delete_file) {
                attachments.remove(attachmentAdapter.getData().get(position).localPath);
                attachmentAdapter.delete(position);
            }
        });
    }

    @Override
    protected void onClickListener(View view) {
        if(view.getId() == R.id.post_create_comment_fragment_cancel) {
            dismiss();
        }

        if (view.getId() == R.id.post_create_comment_fragment_reply || view.getId() == R.id.post_create_comment_fragment_send_btn) {
            progressDialog.show();
            if (imageAdapter.getData().size() == 0) { //没有图片

                progressDialog.setMessage("正在发表，请稍候...");
                createCommentPresenter.sendComment(board_id,
                        topic_id, quote_id, is_quote,anonymous.isChecked(),
                        content.getText().toString(),
                        null, null, attachments, mActivity,
                        SharePrefUtil.getUid(mActivity));

            } else {  //有图片
                progressDialog.setMessage("正在压缩图片，请稍候...");
                createCommentPresenter.compressImage(mActivity, imageAdapter.getData());
            }
        }

        if (view.getId() == R.id.post_create_comment_fragment_add_image_btn) {
            createCommentPresenter.requestPermission(getActivity(), ACTION_ADD_PHOTO, Manifest.permission.READ_EXTERNAL_STORAGE);
        }

        if (view.getId() == R.id.post_create_comment_fragment_add_attachment_btn) {
            createCommentPresenter.requestPermission(getActivity(), ACTION_ADD_ATTACHMENT, Manifest.permission.READ_EXTERNAL_STORAGE);
        }

        if (view.getId() == R.id.post_create_comment_fragment_at_btn) {
            Intent intent = new Intent(mActivity, AtUserListActivity.class);
            startActivityForResult(intent, AT_USER_REQUEST);
        }

        if (view.getId() == R.id.post_create_comment_fragment_add_emotion_btn) {
            if (emoticonPanelLayout.getVisibility() == View.GONE) {
                CommonUtil.hideSoftKeyboard(mActivity, content);
                emoticonPanelLayout.postDelayed(() -> {
                    emoticonPanelLayout.setVisibility(View.VISIBLE);
                }, 100);

            } else if (emoticonPanelLayout.getVisibility() == View.VISIBLE) {
                CommonUtil.showSoftKeyboard(mActivity, content, 100);
                emoticonPanelLayout.setVisibility(View.GONE);
            }
        }
        if (view.getId() == R.id.post_create_comment_fragment_content) {
            emoticonPanelLayout.setVisibility(View.GONE);
        }

    }

    @Override
    public void onCompressImageSuccess(List<File> compressedFiles) {
        progressDialog.show();
        progressDialog.setMessage("正在上传图片，请稍候...");

        createCommentPresenter.uploadImg(compressedFiles, "forum", "image", mActivity);
    }

    @Override
    public void onCompressImageFail(String msg) {
        progressDialog.dismiss();
        showToast(msg, ToastType.TYPE_ERROR);
    }

    @Override
    public void onUploadSuccess(UploadResultBean uploadResultBean) {
        progressDialog.setMessage("正在发表，请稍候...");

        List<Integer> imgIds = new ArrayList<>();
        List<String> imgUrls = new ArrayList<>();

        for (int i = 0; i < uploadResultBean.body.attachment.size(); i ++) {
            imgIds.add(uploadResultBean.body.attachment.get(i).id);
            imgUrls.add(uploadResultBean.body.attachment.get(i).urlName);
        }

        createCommentPresenter.sendComment(board_id,
                topic_id, quote_id, is_quote,anonymous.isChecked(),
                content.getText().toString(),
                imgUrls, imgIds, attachments, mActivity,
                SharePrefUtil.getUid(mActivity));

    }

    @Override
    public void onUploadError(String msg) {
        progressDialog.dismiss();
        showToast(msg, ToastType.TYPE_ERROR);
    }

    @Override
    public void onSendCommentSuccess(SendPostBean sendPostBean, List<String> uploadedImgUrls) {
        progressDialog.dismiss();
//        showToast(sendPostBean.head.errInfo);
        sendSuccess = true;

        EventBus.getDefault().post(new BaseEvent<>(BaseEvent.EventCode.SEND_COMMENT_SUCCESS));

        dismissAllowingStateLoss();
    }

    @Override
    public void onSendCommentError(String msg) {
        progressDialog.dismiss();
        showToast(msg, ToastType.TYPE_ERROR);
    }

    @Override
    public void onStartUploadAttachment() {
        progressDialog.show();
        progressDialog.setMessage("正在上传附件，请稍候...");
    }

    @Override
    public void onUploadAttachmentSuccess(AttachmentBean attachmentBean, String msg) {
        progressDialog.dismiss();
        attachments.put(attachmentBean.localPath, attachmentBean.aid);
        attachmentAdapter.addData(attachmentBean);
    }

    @Override
    public void onUploadAttachmentError(String msg) {
        progressDialog.dismiss();
        showToast(msg, ToastType.TYPE_ERROR);
    }

    @Override
    public void onPermissionGranted(int action) {
        if (action == ACTION_ADD_PHOTO) {
            PictureSelector.create(this)
                    .openGallery(PictureMimeType.ofImage())
                    .isCamera(true)
                    .isGif(false)
                    .showCropFrame(false)
                    .hideBottomControls(false)
                    .maxSelectNum(20)
                    .isEnableCrop(false)
                    .imageEngine(GlideEngineForPictureSelector.createGlideEngine())
                    .forResult(action);
        } else if (action == ACTION_ADD_ATTACHMENT) {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("*/*");
            this.startActivityForResult(intent, ADD_ATTACHMENT_REQUEST);
        }

    }

    @Override
    public void onPermissionRefused() {
        showToast(getString(R.string.permission_request), ToastType.TYPE_WARNING);
    }

    @Override
    public void onPermissionRefusedWithNoMoreRequest() {
        showToast(getString(R.string.permission_refuse), ToastType.TYPE_ERROR);
    }

    @Override
    public void onExit() {
        CommonUtil.hideSoftKeyboard(mActivity, content);
        dismissAllowingStateLoss();
    }

    @Override
    protected boolean registerEventBus() {
        return true;
    }

    @Override
    protected void receiveEventBusMsg(BaseEvent baseEvent) {
        if (baseEvent.eventCode == BaseEvent.EventCode.INSERT_EMOTION) {
            createCommentPresenter.insertEmotion(mActivity, content, (String) baseEvent.eventData);
        }
        if (baseEvent.eventCode == BaseEvent.EventCode.AT_USER) {
            CommonUtil.showSoftKeyboard(mActivity, content, 10);
            content.getText().append((String)baseEvent.eventData);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ACTION_ADD_PHOTO && resultCode == Activity.RESULT_OK && data != null) {
            List<LocalMedia> selectList = PictureSelector.obtainMultipleResult(data);
            for (int i = 0; i < selectList.size(); i ++) {
                imageAdapter.addData(selectList.get(i).getRealPath());
            }
            CommonUtil.showSoftKeyboard(mActivity, content, 10);
            imageRecyclerView.smoothScrollToPosition(imageAdapter.getData().size() - 1);
        }
        if (requestCode == AT_USER_REQUEST && resultCode == AtUserListFragment.AT_USER_RESULT && data != null) {
            content.requestFocus();
            content.getText().append(data.getStringExtra(Constant.IntentKey.AT_USER));
        }
        if (requestCode == ADD_ATTACHMENT_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            Uri uri = data.getData();
            String path = FileUtil.getRealPathFromUri(mActivity, uri);
            if (! attachments.containsKey(path)) {
                createCommentPresenter.readyUploadAttachment(mActivity, path, board_id);
            } else {
                showToast("已添加该文件，无需重复添加", ToastType.TYPE_NORMAL);
            }
        }
    }

    @Override
    public void onStop() {
        //只要内容为空就不保存，无论是修改后还是第一次创建
        if ((!TextUtils.isEmpty(content.getText()) || imageAdapter.getData().size() != 0) && !sendSuccess) {
            ReplyDraftBean replyDraftBean = new ReplyDraftBean();
            replyDraftBean.reply_id = is_quote ? quote_id : topic_id;
            replyDraftBean.content = content.getText().toString();
            replyDraftBean.images = imageAdapter.getData().toString();

            replyDraftBean.saveOrUpdate("reply_id = ?", String.valueOf(is_quote ? quote_id : topic_id));
            showToast("评论保存成功", ToastType.TYPE_SUCCESS);
        }

        super.onStop();
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (buttonView.getId() == R.id.post_create_comment_fragment_anonymous &&
                board_id != Constant.MIYU_BOARD_ID) {
            showToast("仅支持密语板块匿名", ToastType.TYPE_ERROR);
            anonymous.setChecked(false);
        }
        if (buttonView.getId() == R.id.post_create_comment_fragment_refresh_after_send) {
            SharePrefUtil.setRefreshOnReplySuccess(mActivity, isChecked);
        }
    }
}
