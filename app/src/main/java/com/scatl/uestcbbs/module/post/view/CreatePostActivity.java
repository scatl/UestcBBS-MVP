package com.scatl.uestcbbs.module.post.view;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import com.jaeger.library.StatusBarUtil;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.scatl.uestcbbs.R;
import com.scatl.uestcbbs.annotation.ToastType;
import com.scatl.uestcbbs.base.BaseActivity;
import com.scatl.uestcbbs.base.BaseEvent;
import com.scatl.uestcbbs.custom.MyLinearLayoutManger;
import com.scatl.uestcbbs.custom.emoticon.EmoticonPanelLayout;
import com.scatl.uestcbbs.custom.posteditor.ContentEditor;
import com.scatl.uestcbbs.entity.AttachmentBean;
import com.scatl.uestcbbs.entity.PostDraftBean;
import com.scatl.uestcbbs.entity.SendPostBean;
import com.scatl.uestcbbs.entity.UploadResultBean;
import com.scatl.uestcbbs.entity.UserPostBean;
import com.scatl.uestcbbs.helper.glidehelper.GlideEngineForPictureSelector;
import com.scatl.uestcbbs.module.post.adapter.AttachmentAdapter;
import com.scatl.uestcbbs.module.post.adapter.CreatePostPollAdapter;
import com.scatl.uestcbbs.module.post.presenter.CreatePostPresenter;
import com.scatl.uestcbbs.module.user.view.AtUserListActivity;
import com.scatl.uestcbbs.module.user.view.UserDetailActivity;
import com.scatl.uestcbbs.util.ColorUtil;
import com.scatl.uestcbbs.util.CommonUtil;
import com.scatl.uestcbbs.util.Constant;
import com.scatl.uestcbbs.util.SharePrefUtil;
import com.scatl.uestcbbs.util.TimeUtil;
import com.scatl.uestcbbs.util.ToastUtil;

import org.greenrobot.eventbus.EventBus;
import org.litepal.LitePal;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import am.widget.smoothinputlayout.SmoothInputLayout;

public class CreatePostActivity extends BaseActivity<CreatePostPresenter> implements CreatePostView{

    private Toolbar toolbar;
    private CoordinatorLayout coordinatorLayout;
    private ImageView addEmotionBtn, atBtn, addPhotoBtn, sendBtn, addPollBtn, addAttachmentBtn, moreOptionsBtn;
    private EmoticonPanelLayout emoticonPanelLayout;
    private AppCompatEditText postTitle;
    private TextView boardName, autoSaveText;
    private ContentEditor contentEditor;
    private ProgressDialog progressDialog;
    private TextView sendBtn1;

    private RecyclerView pollRv, attachmentRv;
    private CreatePostPollAdapter createPostPollAdapter;
    private AttachmentAdapter attachmentAdapter;
    private LinearLayout pollLayout;
    private TextView pollDesp;

    private SmoothInputLayout lytContent;

    private static final int ACTION_ADD_PHOTO = 14;
    private static final int AT_USER_REQUEST = 110;
    private static final int ACTION_ADD_ATTACHMENT = 119;
    private static final int ADD_ATTACHMENT_REQUEST = 120;

    private int currentBoardId, currentFilterId;
    private String currentBoardName, currentFilterName;
    private long createTime;
    private String currentTitle, currentContent;

    private List<String> currentPollOptions;
    private int currentPollExp, currentPollChoice;
    private boolean currentPollVisible, currentPollShowVoters, currentAnonymous, currentOnlyAuthor, currentOriginalPic;

    private boolean sendPostSuccess;

    private Map<Uri, Integer> attachments = new LinkedHashMap<>(); //附件aid

    @Override
    protected void getIntent(Intent intent) {
        super.getIntent(intent);
        createTime = TimeUtil.getLongMs();
        PostDraftBean postDraftBean = (PostDraftBean) intent.getSerializableExtra(Constant.IntentKey.DATA_2);
        if (postDraftBean != null) {
            currentBoardId = postDraftBean.board_id;
            currentFilterId = postDraftBean.filter_id;
            currentBoardName = postDraftBean.board_name;
            currentFilterName = postDraftBean.filter_name;
            currentTitle = postDraftBean.title;
            currentContent = postDraftBean.content;
            createTime = postDraftBean.time;
            currentPollOptions = CommonUtil.toList(postDraftBean.poll_options);
            currentPollExp = postDraftBean.poll_exp;
            currentPollChoice = postDraftBean.poll_choices;
            currentPollVisible = postDraftBean.poll_visible;
            currentPollShowVoters = postDraftBean.poll_show_voters;
            currentAnonymous = postDraftBean.anonymous;
            currentOnlyAuthor = postDraftBean.only_user;
        }
    }

    @Override
    protected int setLayoutResourceId() {
        return R.layout.activity_create_post;
    }

    @Override
    protected void findView() {
        coordinatorLayout = findViewById(R.id.create_post_coor_layout);
        toolbar = findViewById(R.id.toolbar);
        addEmotionBtn = findViewById(R.id.create_post_add_emotion_btn);
        atBtn = findViewById(R.id.create_post_at_btn);
        addPhotoBtn = findViewById(R.id.create_post_add_image_btn);
        sendBtn = findViewById(R.id.create_post_send_btn);
        emoticonPanelLayout = findViewById(R.id.create_post_emoticon_layout);
        postTitle = findViewById(R.id.create_post_title);
        contentEditor = findViewById(R.id.create_post_content_editor);
        boardName = findViewById(R.id.create_post_board_name);
        autoSaveText = findViewById(R.id.create_post_auto_save_text);
        addPollBtn = findViewById(R.id.create_post_add_poll_btn);
        pollRv = findViewById(R.id.create_post_poll_rv);
        pollLayout = findViewById(R.id.create_post_poll_info);
        pollDesp = findViewById(R.id.create_post_poll_desp);
        addAttachmentBtn = findViewById(R.id.create_post_add_attachment_btn);
        moreOptionsBtn = findViewById(R.id.create_post_more_options_btn);
        attachmentRv = findViewById(R.id.create_post_attachment_rv);
        lytContent = findViewById(R.id.sil_lyt_content);
        sendBtn1 = findViewById(R.id.create_post_send_btn_1);
    }

    @Override
    protected void initView() {
        super.initView();

        CommonUtil.showSoftKeyboard(this, postTitle, 100);

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("发表帖子");
        progressDialog.setCancelable(false);

        addEmotionBtn.setOnClickListener(this);
        atBtn.setOnClickListener(this::onClickListener);
        addPhotoBtn.setOnClickListener(this::onClickListener);
        sendBtn.setOnClickListener(this::onClickListener);
        boardName.setOnClickListener(this::onClickListener);
        addPollBtn.setOnClickListener(this::onClickListener);
        addAttachmentBtn.setOnClickListener(this::onClickListener);
        moreOptionsBtn.setOnClickListener(this);
        sendBtn1.setOnClickListener(this);

        //投票
        createPostPollAdapter = new CreatePostPollAdapter(R.layout.item_create_post_poll);
        pollRv.setLayoutManager(new MyLinearLayoutManger(this));
        pollRv.setAdapter(createPostPollAdapter);

        //附件
        attachmentAdapter = new AttachmentAdapter(R.layout.item_attachment);
        LinearLayoutManager linearLayoutManager1 = new MyLinearLayoutManger(this);
        linearLayoutManager1.setOrientation(LinearLayoutManager.HORIZONTAL);
        attachmentRv.setLayoutManager(linearLayoutManager1);
        attachmentRv.setAdapter(attachmentAdapter);

        postTitle.setText(TextUtils.isEmpty(currentTitle) ? "" : currentTitle);
        boardName.setText(TextUtils.isEmpty(currentBoardName) && TextUtils.isEmpty(currentFilterName) ? "请选择板块" :
                currentBoardName + "->" + currentFilterName);

        //若内容不为空，则说明是草稿，直接显示内容
        if (! TextUtils.isEmpty(currentContent)) {
            contentEditor.setEditorData(currentContent);
        }
        if (currentPollOptions != null && currentPollOptions.size() > 0) {
            pollLayout.setVisibility(View.VISIBLE);
            createPostPollAdapter.setNewData(currentPollOptions);
            String a = "可选" + currentPollChoice + "项，";
            String b = "有效期" + currentPollExp + "天，";
            String c = "投票" + (currentPollVisible ? "后结果可见，" : "前结果可见");
            String d = (currentPollShowVoters ? "公开" : "不公开") + "投票参与人";

            pollDesp.setText(new StringBuilder().append(a).append(b).append(c).append(d));
        } else {
            currentPollOptions = new ArrayList<>();
        }

        countDownTimer.start();
    }

    @Override
    protected CreatePostPresenter initPresenter() {
        return new CreatePostPresenter();
    }

    @Override
    protected void onClickListener(View view) {
        if (view.getId() == R.id.create_post_add_emotion_btn) {
            if (emoticonPanelLayout.getVisibility() == View.GONE) {
                lytContent.closeKeyboard(true);// 关闭键盘
                lytContent.showInputPane(true);//显示面板
            } else {
                lytContent.closeInputPane();// 关闭面板
                lytContent.showKeyboard();// 显示键盘
            }
        }
        if (view.getId() == R.id.create_post_at_btn) {
            Intent intent = new Intent(this, AtUserListActivity.class);
            startActivityForResult(intent, AT_USER_REQUEST);
        }
        if (view.getId() == R.id.create_post_add_image_btn) {
            presenter.requestPermission(this, ACTION_ADD_PHOTO, Manifest.permission.READ_EXTERNAL_STORAGE);
        }
        if (view.getId() == R.id.create_post_add_attachment_btn) {
            presenter.requestPermission(this, ACTION_ADD_ATTACHMENT, Manifest.permission.READ_EXTERNAL_STORAGE);
        }
        if (view.getId() == R.id.create_post_add_poll_btn) {
            if (createPostPollAdapter.getData().size() == 0) {
                startActivity(new Intent(this, AddPollActivity.class));
            } else {
                Intent intent = new Intent(this, AddPollActivity.class);
                intent.putStringArrayListExtra(Constant.IntentKey.POLL_OPTIONS, (ArrayList<String>) createPostPollAdapter.getData());
                intent.putExtra(Constant.IntentKey.POLL_EXPIRATION, currentPollExp);
                intent.putExtra(Constant.IntentKey.POLL_CHOICES, currentPollChoice);
                intent.putExtra(Constant.IntentKey.POLL_VISIBLE, currentPollVisible);
                intent.putExtra(Constant.IntentKey.POLL_SHOW_VOTERS, currentPollShowVoters);
                startActivity(intent);
            }
        }

        if (view.getId() == R.id.create_post_board_name) {
            SelectBoardFragment.getInstance(null)
                    .show(getSupportFragmentManager(), TimeUtil.getStringMs());
        }
        if (view.getId() == R.id.create_post_send_btn || view.getId() == R.id.create_post_send_btn_1) {
            if (currentBoardId == 0){
                showToast("请选择板块", ToastType.TYPE_WARNING);
            } else if (currentAnonymous && currentBoardId != 371) {
                showToast("您勾选了匿名，请选择密语板块（成电校园->水手之家->密语）", ToastType.TYPE_WARNING);
            } else {
                if (contentEditor.getImgPathList().size() == 0){//没有图片
                    progressDialog.setMessage("正在发表帖子，请稍候...");
                    progressDialog.show();

                    presenter.sendPost(contentEditor,
                            currentBoardId, currentFilterId, postTitle.getText().toString(),
                            new ArrayList<>(), new ArrayList<>(),
                            currentPollOptions, attachments, currentPollChoice, currentPollExp,
                            currentPollVisible, currentPollShowVoters, currentAnonymous, currentOnlyAuthor,
                            this);
                } else {//有图片
                    if (!currentOriginalPic) {
                        progressDialog.setMessage("正在压缩图片，请稍候...");
                        progressDialog.show();

                        presenter.compressImage(this, contentEditor.getImgPathList());
                    } else {

                        progressDialog.setMessage("正在上传原图，请稍候...");
                        progressDialog.show();

                        List<File> originalPicFiles = new ArrayList<>();
                        List<String> imgs = contentEditor.getImgPathList();
                        for (int i = 0; i < imgs.size(); i ++) {
                            File file = new File(imgs.get(i));
                            originalPicFiles.add(file);
                        }
                        presenter.uploadImages(originalPicFiles, "forum", "image", this);
                    }

                }
            }
        }

        if (view.getId() == R.id.create_post_more_options_btn) {
            presenter.showCreatePostMoreOptionsDialog(this, currentAnonymous, currentOnlyAuthor, currentOriginalPic);
        }
    }

    @Override
    protected void setOnItemClickListener() {
        attachmentAdapter.setOnItemChildClickListener((adapter, view1, position) -> {
            if (view1.getId() == R.id.item_attachment_delete_file) {
                attachments.remove(attachmentAdapter.getData().get(position).localPath);
                attachmentAdapter.delete(position);
            }
        });
    }

    @Override
    public void onSendPostSuccessBack() {
        CommonUtil.hideSoftKeyboard(this, contentEditor);
    }

    @Override
    public void onSendPostSuccessViewPost() {
        progressDialog.show();
        progressDialog.setMessage("请稍候...");
        presenter.userPost(SharePrefUtil.getUid(this), this);
    }

    @Override
    public void onSendPostSuccess(SendPostBean sendPostBean) {
        sendPostSuccess = true;
        progressDialog.dismiss();

        if (currentAnonymous) {
            ToastUtil.showToast(this, "发帖成功", ToastType.TYPE_SUCCESS);
            finish();
        } else {
            presenter.showCreatePostSuccessDialog(this);
        }
    }

    @Override
    public void onSendPostError(String msg) {
        progressDialog.dismiss();
        showToast("发表帖子失败：" + msg, ToastType.TYPE_ERROR);
    }

    @Override
    public void onUploadSuccess(UploadResultBean uploadResultBean) {
        progressDialog.setMessage("正在发表帖子，请稍候...");

        List<Integer> imgIds = new ArrayList<>();
        List<String> imgUrls = new ArrayList<>();

        for (int i = 0; i < uploadResultBean.body.attachment.size(); i ++) {
            imgIds.add(uploadResultBean.body.attachment.get(i).id);
            imgUrls.add(uploadResultBean.body.attachment.get(i).urlName);
        }

        if (imgUrls.size() != contentEditor.getImgPathList().size()) {
            onUploadError("部分图片上传失败，请重试！若频繁出现此错误，请重新添加图片！");
        } else {
            presenter.sendPost(contentEditor,
                    currentBoardId, currentFilterId,
                    postTitle.getText().toString(),
                    imgUrls, imgIds,
                    currentPollOptions, attachments, currentPollChoice, currentPollExp,
                    currentPollVisible, currentPollShowVoters, currentAnonymous, currentOnlyAuthor,
                    this);
        }
    }

    @Override
    public void onUploadError(String msg) {
        progressDialog.dismiss();
        showToast("上传图片失败：" + msg, ToastType.TYPE_ERROR);
    }

    @Override
    public void onCompressImageSuccess(List<File> compressedFiles) {
        progressDialog.setMessage("图片压缩成功，正在上传图片，请稍候...");
        presenter.uploadImages(compressedFiles, "forum", "image", this);
    }

    @Override
    public void onCompressImageFail(String msg) {
        progressDialog.dismiss();
        showToast("压缩图片失败：" + msg, ToastType.TYPE_ERROR);
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
    public void onStartUploadAttachment() {
        progressDialog.show();
        progressDialog.setMessage("正在上传附件，请稍候...");
    }

    @Override
    public void onUploadAttachmentSuccess(AttachmentBean attachmentBean, String msg) {
        progressDialog.dismiss();
        attachments.put(attachmentBean.uri, attachmentBean.aid);
        attachmentAdapter.addData(attachmentBean);
    }

    @Override
    public void onUploadAttachmentError(String msg) {
        progressDialog.dismiss();
        showToast(msg, ToastType.TYPE_ERROR);
    }

    @Override
    public void onGetUserPostSuccess(UserPostBean userPostBean) {
        if (userPostBean != null && userPostBean.list != null && userPostBean.list.size() > 0) {
            int tid = userPostBean.list.get(0).topic_id;
            Intent intent = new Intent(this, NewPostDetailActivity.class);
            intent.putExtra(Constant.IntentKey.TOPIC_ID, tid);
            startActivity(intent);
        }
        progressDialog.dismiss();
        finish();
    }

    @Override
    public void onGetUserPostError(String msg) {
        Intent intent = new Intent(this, UserDetailActivity.class);
        intent.putExtra(Constant.IntentKey.USER_ID, SharePrefUtil.getUid(this));
        startActivity(intent);
        progressDialog.dismiss();
        finish();
    }

    @Override
    public void onMoreOptionsChanged(boolean isAnonymous, boolean isOnlyAuthor, boolean originalPic) {
        this.currentAnonymous = isAnonymous;
        this.currentOnlyAuthor = isOnlyAuthor;
        this.currentOriginalPic = originalPic;
        sendBtn1.setText(currentAnonymous ? "匿名发表" : "发表");
    }

    @Override
    protected boolean registerEventBus() {
        return true;
    }

    @Override
    public void onEventBusReceived(BaseEvent baseEvent) {
        if (baseEvent.eventCode == BaseEvent.EventCode.INSERT_EMOTION) {
            contentEditor.insertEmotion((String) baseEvent.eventData);
        }
        if (baseEvent.eventCode == BaseEvent.EventCode.BOARD_SELECTED) {
            BaseEvent.BoardSelected boardSelected = (BaseEvent.BoardSelected)baseEvent.eventData;
            currentBoardId = boardSelected.boardId;
            currentBoardName = boardSelected.boardName;
            currentFilterId = boardSelected.filterId;
            currentFilterName = boardSelected.filterName;
            boardName.setText(new StringBuilder().append(currentBoardName).append("->").append(currentFilterName));
        }
        if (baseEvent.eventCode == BaseEvent.EventCode.DELETE_POLL) {
            pollLayout.setVisibility(View.GONE);
            currentPollOptions = new ArrayList<>();
            createPostPollAdapter.setNewData(currentPollOptions);
        }
        if (baseEvent.eventCode == BaseEvent.EventCode.ADD_POLL) {
            pollLayout.setVisibility(View.VISIBLE);
            BaseEvent.AddPoll addPoll = (BaseEvent.AddPoll)baseEvent.eventData;
            currentPollOptions = addPoll.pollOptions;
            currentPollChoice = addPoll.pollChoice;
            currentPollExp = addPoll.pollExp;
            currentPollVisible = addPoll.pollVisible;
            currentPollShowVoters = addPoll.showVoters;

            createPostPollAdapter.setNewData(addPoll.pollOptions);

            String a = "可选" + addPoll.pollChoice + "项，";
            String b = "有效期" + addPoll.pollExp + "天，";
            String c = "投票" + (addPoll.pollVisible ? "后结果可见，" : "前结果可见，");
            String d = (addPoll.showVoters ? "公开" : "不公开") + "投票参与人";
            pollDesp.setText(new StringBuilder().append(a).append(b).append(c).append(d));
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ACTION_ADD_PHOTO && resultCode == Activity.RESULT_OK && data != null) {
            List<LocalMedia> selectList = PictureSelector.obtainMultipleResult(data);
            for (int i = 0; i < selectList.size(); i ++) {
                contentEditor.insertImage(selectList.get(i).getRealPath(), 1000);
            }
        }
        if (requestCode == AT_USER_REQUEST && resultCode == AtUserListActivity.AT_USER_RESULT && data != null) {
            contentEditor.insertText(data.getStringExtra(Constant.IntentKey.AT_USER));
        }
        if (requestCode == ADD_ATTACHMENT_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            Uri uri = data.getData();
            if (!attachments.containsKey(uri)) {
                presenter.readyUploadAttachment(this, uri, currentBoardId);
            } else {
                showToast("已添加该文件，无需重复添加", ToastType.TYPE_NORMAL);
            }
        }
    }


    /**
     * author: sca_tl
     * description: 保存草稿
     */
    private void onSaveDraftData() {
        List<ContentEditor.EditData> dataList = contentEditor.buildEditorData();
        JSONArray jsonArray = new JSONArray();
        for (int i = 0; i < dataList.size(); i ++) {
            JSONObject content_json = new JSONObject();
            if (dataList.get(i).content_type == ContentEditor.CONTENT_TYPE_TEXT) {
                content_json.put("content_type", ContentEditor.CONTENT_TYPE_TEXT);
                content_json.put("content", dataList.get(i).inputStr);
            }
            if (dataList.get(i).content_type == ContentEditor.CONTENT_TYPE_IMAGE) {
                content_json.put("content_type", ContentEditor.CONTENT_TYPE_IMAGE);
                content_json.put("content", dataList.get(i).imagePath);
            }
            jsonArray.add(content_json);
        }

        PostDraftBean postDraftBean = new PostDraftBean();
        postDraftBean.board_id = currentBoardId;
        postDraftBean.filter_id = currentFilterId;
        postDraftBean.title = postTitle.getText().toString();
        postDraftBean.content = jsonArray.toJSONString();
        postDraftBean.board_name = currentBoardName;
        postDraftBean.filter_name = currentFilterName;
        postDraftBean.time = createTime;
        postDraftBean.poll_options = currentPollOptions.toString();
        postDraftBean.poll_choices = currentPollChoice;
        postDraftBean.poll_exp = currentPollExp;
        postDraftBean.poll_visible = currentPollVisible;
        postDraftBean.poll_show_voters = currentPollShowVoters;
        postDraftBean.anonymous = currentAnonymous;
        postDraftBean.only_user = currentOnlyAuthor;

        postDraftBean.saveOrUpdate("time = ?", String.valueOf(createTime));
    }

    private CountDownTimer countDownTimer = new CountDownTimer(3000, 1000) {
        @Override
        public void onTick(long l) { }

        @Override
        public void onFinish() {
            onSaveDraftData();
            autoSaveText.setText(String.valueOf(TimeUtil.getFormatDate(TimeUtil.getLongMs(), "HH:mm:ss") + "  已自动保存"));
            countDownTimer.start();
        }
    };

    @Override
    protected void onDestroy() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }

        if (sendPostSuccess) {
            //删除草稿数据
            LitePal.deleteAll(PostDraftBean.class, "time = " + createTime);
        } else {
            onSaveDraftData();
            showToast("已保存至草稿", ToastType.TYPE_SUCCESS);
        }
        EventBus.getDefault().post(new BaseEvent<>(BaseEvent.EventCode.EXIT_CREATE_POST));
        super.onDestroy();
    }

    @Override
    protected void setStatusBar() {
        StatusBarUtil.setColor(
                this,
                ColorUtil.getAttrColor(this, R.attr.colorOnSurfaceInverse), 0);
    }
}
