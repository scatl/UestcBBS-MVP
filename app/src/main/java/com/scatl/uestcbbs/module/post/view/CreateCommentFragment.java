package com.scatl.uestcbbs.module.post.view;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ImageSpan;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.scatl.uestcbbs.R;
import com.scatl.uestcbbs.base.BaseDialogFragment;
import com.scatl.uestcbbs.base.BaseEvent;
import com.scatl.uestcbbs.base.BasePresenter;
import com.scatl.uestcbbs.custom.MyLinearLayoutManger;
import com.scatl.uestcbbs.custom.emoticon.EmoticonPanelLayout;
import com.scatl.uestcbbs.entity.SendPostBean;
import com.scatl.uestcbbs.entity.UploadResultBean;
import com.scatl.uestcbbs.helper.glidehelper.GlideLoader4Matisse;
import com.scatl.uestcbbs.module.post.adapter.CreateCommentImageAdapter;
import com.scatl.uestcbbs.module.post.presenter.CreateCommentPresenter;
import com.scatl.uestcbbs.module.user.view.AtUserListActivity;
import com.scatl.uestcbbs.module.user.view.AtUserListFragment;
import com.scatl.uestcbbs.util.CommonUtil;
import com.scatl.uestcbbs.util.Constant;
import com.scatl.uestcbbs.util.ImageUtil;

import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * author: sca_tl
 * description:
 * date: 2020/1/25 13:04
 */
public class CreateCommentFragment extends BaseDialogFragment implements CreateCommentView {

    private static final String TAG = "CreateCommentFragment";

    private AppCompatEditText content;
    private TextView cancelText, replyText;
    private ImageView atBtn, addImgBtn, addEmotionBtn, replyBtn;
    private RecyclerView imageRecyclerView;
    private CreateCommentImageAdapter imageAdapter;
    private ProgressDialog progressDialog;
    private EmoticonPanelLayout emoticonPanelLayout;

    private CreateCommentPresenter createCommentPresenter;

    private int board_id, topic_id, quote_id;
    private boolean is_quote;
    private String user_name;

    private static final int ACTION_ADD_PHOTO = 12;
    private static final int AT_USER_REQUEST = 16;

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
    }

    @Override
    protected void initView() {
        createCommentPresenter = (CreateCommentPresenter) presenter;

        setCancelable(false);

        atBtn.setOnClickListener(this);
        addImgBtn.setOnClickListener(this);
        addEmotionBtn.setOnClickListener(this);
        replyBtn.setOnClickListener(this);
        cancelText.setOnClickListener(this);
        replyText.setOnClickListener(this);

        CommonUtil.showSoftKeyboard(mActivity, content, 10);
        content.setHint("回复：" + user_name);
        content.setOnClickListener(this);

        imageAdapter = new CreateCommentImageAdapter(R.layout.item_post_create_comment_image);
        imageAdapter.setHasStableIds(true);
        LinearLayoutManager linearLayoutManager = new MyLinearLayoutManger(mActivity);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        imageRecyclerView.setLayoutManager(linearLayoutManager);
        imageRecyclerView.setAdapter(imageAdapter);

        progressDialog = new ProgressDialog(mActivity);
        progressDialog.setTitle("发送消息");
        progressDialog.setCancelable(false);
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
    }

    @Override
    protected void onClickListener(View view) {
        switch (view.getId()) {
            case R.id.post_create_comment_fragment_cancel:
                createCommentPresenter.checkBeforeExit(mActivity,
                        TextUtils.isEmpty(content.getText()) && imageAdapter.getData().size() == 0);
                break;

            case R.id.post_create_comment_fragment_reply: //发送消息
            case R.id.post_create_comment_fragment_send_btn:
                progressDialog.show();
                if (imageAdapter.getData().size() == 0) { //没有图片

                    progressDialog.setMessage("正在发表，请稍候...");
                    createCommentPresenter.sendComment(board_id,
                            topic_id, quote_id, is_quote,
                            content.getText().toString(),
                            null, null, mActivity);

                } else {  //有图片
                    progressDialog.setMessage("正在压缩图片，请稍候...");
                    createCommentPresenter.compressImage(mActivity, imageAdapter.getData());
                }

                break;

            case R.id.post_create_comment_fragment_add_image_btn:  //添加图片
                createCommentPresenter.requestPermission(getActivity(), ACTION_ADD_PHOTO, Manifest.permission.READ_EXTERNAL_STORAGE);
                break;

            case R.id.post_create_comment_fragment_at_btn:  //at列表
                Intent intent = new Intent(mActivity, AtUserListActivity.class);
                startActivityForResult(intent, AT_USER_REQUEST);
                break;

            case R.id.post_create_comment_fragment_add_emotion_btn:
                if (emoticonPanelLayout.getVisibility() == View.GONE) {
                    CommonUtil.hideSoftKeyboard(mActivity, content);
                    emoticonPanelLayout.postDelayed(() -> {
                        emoticonPanelLayout.setVisibility(View.VISIBLE);
                    }, 100);

                } else if (emoticonPanelLayout.getVisibility() == View.VISIBLE) {
                    CommonUtil.showSoftKeyboard(mActivity, content, 100);
                    emoticonPanelLayout.setVisibility(View.GONE);
                }
                break;

            case R.id.post_create_comment_fragment_content:
                emoticonPanelLayout.setVisibility(View.GONE);
                break;

            default:
                break;
        }
    }

    @Override
    public void onCompressImageSuccess(List<File> compressedFiles) {
        progressDialog.show();
        progressDialog.setMessage("正在上传图片，请稍候...");

        createCommentPresenter.upload(compressedFiles, "forum", "image", mActivity);
    }

    @Override
    public void onCompressImageFail(String msg) {
        progressDialog.dismiss();
        showSnackBar(getView(), msg);
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
                topic_id, quote_id, is_quote,
                content.getText().toString(),
                imgUrls, imgIds, mActivity);

    }

    @Override
    public void onUploadError(String msg) {
        progressDialog.dismiss();
        showSnackBar(getView(), msg);
    }

    @Override
    public void onSendCommentSuccess(SendPostBean sendPostBean) {
        progressDialog.dismiss();
        showSnackBar(getView(), sendPostBean.head.errInfo);
        dismiss();
    }

    @Override
    public void onSendCommentError(String msg) {
        progressDialog.dismiss();
        showSnackBar(getView(), msg);
    }

    @Override
    public void onPermissionGranted(int action) {
        Matisse.from(mActivity)
                .choose(MimeType.of(MimeType.JPEG, MimeType.PNG))
                .countable(true)
                .maxSelectable(20)
                .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
                .imageEngine(new GlideLoader4Matisse())
                .forResult(action);
    }

    @Override
    public void onPermissionRefused() {
        showSnackBar(getView(), getString(R.string.permission_request));
    }

    @Override
    public void onPermissionRefusedWithNoMoreRequest() {
        showSnackBar(getView(), getString(R.string.permission_refuse));
    }

    @Override
    public void onExit() {
        CommonUtil.hideSoftKeyboard(mActivity, content);
        dismiss();

    }

    /**
     * author: sca_tl
     * description: 插入表情
     */
    private void insertEmotion(String emotion_path) {
        String emotion_name = emotion_path.substring(emotion_path.lastIndexOf("/") + 1).replace("_", ":").replace(".gif", "");
        SpannableString spannableString = new SpannableString(emotion_name);

        Bitmap bitmap = null;
        try {
            String rePath = emotion_path.replace("file:///android_asset/", "");
            InputStream is = getResources().getAssets().open(rePath);
            bitmap = BitmapFactory.decodeStream(is);
            is.close();
        } catch (IOException e){
            e.printStackTrace();
        }

        Drawable drawable = ImageUtil.bitmap2Drawable(bitmap);
        drawable.setBounds(10, 10, 80, 80);
        ImageSpan imageSpan = new ImageSpan(drawable, ImageSpan.ALIGN_BOTTOM);
        spannableString.setSpan(imageSpan, 0, emotion_name.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        content.getText().insert(content.getSelectionStart(), spannableString);
    }

    @Override
    protected boolean registerEventBus() {
        return true;
    }

    @Override
    protected void receiveEventBusMsg(BaseEvent baseEvent) {
        if (baseEvent.eventCode == BaseEvent.EventCode.INSERT_EMOTION) {
            insertEmotion((String) baseEvent.eventData);
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
            CommonUtil.showSoftKeyboard(mActivity, content, 10);
            imageAdapter.addData(Matisse.obtainPathResult(data));
            imageRecyclerView.smoothScrollToPosition(imageAdapter.getData().size() - 1);
        }
        if (requestCode == AT_USER_REQUEST && resultCode == AtUserListFragment.AT_USER_RESULT && data != null) {
            content.requestFocus();
            content.getText().append(data.getStringExtra(Constant.IntentKey.AT_USER));
        }
    }

}
