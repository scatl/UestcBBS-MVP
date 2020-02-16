package com.scatl.uestcbbs.module.post.view;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.scatl.uestcbbs.R;
import com.scatl.uestcbbs.base.BaseActivity;
import com.scatl.uestcbbs.base.BaseEvent;
import com.scatl.uestcbbs.base.BasePresenter;
import com.scatl.uestcbbs.custom.MyLinearLayoutManger;
import com.scatl.uestcbbs.custom.emoticon.EmoticonPanelLayout;
import com.scatl.uestcbbs.custom.posteditor.ContentEditor;
import com.scatl.uestcbbs.entity.PostDraftBean;
import com.scatl.uestcbbs.entity.SendPostBean;
import com.scatl.uestcbbs.entity.UploadResultBean;
import com.scatl.uestcbbs.helper.glidehelper.GlideLoader4Matisse;
import com.scatl.uestcbbs.module.post.adapter.CreatePostPollAdapter;
import com.scatl.uestcbbs.module.post.presenter.CreatePostPresenter;
import com.scatl.uestcbbs.module.user.view.AtUserListActivity;
import com.scatl.uestcbbs.module.user.view.AtUserListFragment;
import com.scatl.uestcbbs.util.CommonUtil;
import com.scatl.uestcbbs.util.Constant;
import com.scatl.uestcbbs.util.TimeUtil;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;

import org.litepal.LitePal;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class CreatePostActivity extends BaseActivity implements CreatePostView{

    private Toolbar toolbar;
    private CoordinatorLayout coordinatorLayout;
    private ImageView addEmotionBtn, atBtn, addPhotoBtn, sendBtn, addPollBtn;
    private EmoticonPanelLayout emoticonPanelLayout;
    private AppCompatEditText postTitle;
    private TextView boardName, autoSaveText;
    private ContentEditor contentEditor;
    private ProgressDialog progressDialog;

    private RecyclerView pollRv;
    private CreatePostPollAdapter createPostPollAdapter;
    private LinearLayout pollLayout;
    private TextView pollDesp;

    private CreatePostPresenter createPostPresenter;

    private static final int ACTION_ADD_PHOTO = 14;
    private static final int AT_USER_REQUEST = 110;

    private int currentBoardId, currentFilterId;
    private String currentBoardName, currentFilterName;
    private long createTime;
    private String currentTitle, currentContent;

    private List<String> currentPollOptions;
    private int currentPollExp, currentPollChoice;
    private boolean currentPollVisible, currentPollShowVoters;

    @Override
    protected void getIntent(Intent intent) {
        super.getIntent(intent);
        currentBoardId = intent.getIntExtra(Constant.IntentKey.BOARD_ID, 0);
        currentFilterId = intent.getIntExtra(Constant.IntentKey.FILTER_ID, 0);
        currentBoardName = intent.getStringExtra(Constant.IntentKey.BOARD_NAME) == null ? "" : intent.getStringExtra(Constant.IntentKey.BOARD_NAME);
        currentFilterName = intent.getStringExtra(Constant.IntentKey.FILTER_NAME) == null ? "" : intent.getStringExtra(Constant.IntentKey.FILTER_NAME);
        currentTitle = intent.getStringExtra(Constant.IntentKey.TITLE) == null ? "" : intent.getStringExtra(Constant.IntentKey.TITLE);
        currentContent = intent.getStringExtra(Constant.IntentKey.CONTENT) == null ? "" : intent.getStringExtra(Constant.IntentKey.CONTENT);
        createTime = intent.getLongExtra(Constant.IntentKey.TIME, TimeUtil.getLongMs());
        currentPollOptions = intent.getStringArrayListExtra(Constant.IntentKey.POLL_OPTIONS);
        currentPollExp = intent.getIntExtra(Constant.IntentKey.POLL_EXPIRATION, 1);
        currentPollChoice = intent.getIntExtra(Constant.IntentKey.POLL_CHOICES, 1);
        currentPollVisible = intent.getBooleanExtra(Constant.IntentKey.POLL_VISIBLE, true);
        currentPollShowVoters = intent.getBooleanExtra(Constant.IntentKey.POLL_SHOW_VOTERS, true);
    }

    @Override
    protected int setLayoutResourceId() {
        return R.layout.activity_create_post;
    }

    @Override
    protected void findView() {
        coordinatorLayout = findViewById(R.id.create_post_coor_layout);
        toolbar = findViewById(R.id.create_post_toolbar);
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
    }

    @Override
    protected void initView() {

        createPostPresenter = (CreatePostPresenter) presenter;

        CommonUtil.showSoftKeyboard(this, postTitle, 0);

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("发表帖子");
        progressDialog.setCancelable(false);

        addEmotionBtn.setOnClickListener(this);
        atBtn.setOnClickListener(this::onClickListener);
        addPhotoBtn.setOnClickListener(this::onClickListener);
        sendBtn.setOnClickListener(this::onClickListener);
        boardName.setOnClickListener(this::onClickListener);
        addPollBtn.setOnClickListener(this::onClickListener);

        createPostPollAdapter = new CreatePostPollAdapter(R.layout.item_create_post_poll);
        pollRv.setLayoutManager(new MyLinearLayoutManger(this));
        pollRv.setAdapter(createPostPollAdapter);

        countDownTimer.start();
        postTitle.setText(currentTitle);
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

            pollDesp.setText(a + b + c + d);
        } else {
            currentPollOptions = new ArrayList<>();
        }
    }

    @Override
    protected BasePresenter initPresenter() {
        return new CreatePostPresenter();
    }

    @Override
    protected void onClickListener(View view) {
        if (view.getId() == R.id.create_post_add_emotion_btn) {
            emoticonPanelLayout.setVisibility(emoticonPanelLayout.getVisibility() == View.GONE ? View.VISIBLE : View.GONE);
        }
        if (view.getId() == R.id.create_post_at_btn) {
            Intent intent = new Intent(this, AtUserListActivity.class);
            startActivityForResult(intent, AT_USER_REQUEST);
        }
        if (view.getId() == R.id.create_post_add_image_btn) {
            createPostPresenter.requestPermission(this, ACTION_ADD_PHOTO, Manifest.permission.READ_EXTERNAL_STORAGE);
        }
        if (view.getId() == R.id.create_post_add_poll_btn) {
            if (createPostPollAdapter.getData().size() == 0) {
                AddPollFragment.getInstance(null).show(getSupportFragmentManager(),TimeUtil.getStringMs());
            } else {
                Bundle bundle = new Bundle();
                bundle.putStringArrayList(Constant.IntentKey.POLL_OPTIONS, (ArrayList<String>) createPostPollAdapter.getData());
                bundle.putInt(Constant.IntentKey.POLL_EXPIRATION, currentPollExp);
                bundle.putInt(Constant.IntentKey.POLL_CHOICES, currentPollChoice);
                bundle.putBoolean(Constant.IntentKey.POLL_VISIBLE, currentPollVisible);
                bundle.putBoolean(Constant.IntentKey.POLL_SHOW_VOTERS, currentPollShowVoters);
                AddPollFragment.getInstance(bundle).show(getSupportFragmentManager(),TimeUtil.getStringMs());
            }
        }

        if (view.getId() == R.id.create_post_board_name) {
            SelectBoardFragment.getInstance(null)
                    .show(getSupportFragmentManager(), TimeUtil.getStringMs());
        }
        if (view.getId() == R.id.create_post_send_btn) {
            if (currentBoardId == 0){
                showSnackBar(coordinatorLayout, "请选择板块");
            } else {
                if (contentEditor.getImgPathList().size() == 0){//没有图片
                    progressDialog.setMessage("正在发表帖子，请稍候...");
                    progressDialog.show();

                    createPostPresenter.sendPost(contentEditor,
                            currentBoardId, currentFilterId, postTitle.getText().toString(),
                            new ArrayList<>(), new ArrayList<>(),
                            currentPollOptions, currentPollChoice, currentPollExp,
                            currentPollVisible, currentPollShowVoters, this);
                } else {//有图片
                    progressDialog.setMessage("正在压缩图片，请稍候...");
                    progressDialog.show();

                    createPostPresenter.compressImage(this, contentEditor.getImgPathList());
                }
            }
        }
    }

    @Override
    public void onSendPostSuccess(SendPostBean sendPostBean) {
        progressDialog.dismiss();
        showToast(sendPostBean.head.errInfo);
        finish();
    }

    @Override
    public void onSendPostError(String msg) {
        progressDialog.dismiss();
        showSnackBar(coordinatorLayout, "发表帖子失败：" + msg);
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

        createPostPresenter.sendPost(contentEditor,
                currentBoardId,
                currentFilterId,
                postTitle.getText().toString(),
                imgUrls, imgIds,
                currentPollOptions, currentPollChoice, currentPollExp,
                currentPollVisible, currentPollShowVoters, this);
    }

    @Override
    public void onUploadError(String msg) {
        progressDialog.dismiss();
        showSnackBar(coordinatorLayout, "上传图片失败：" + msg);
    }

    @Override
    public void onCompressImageSuccess(List<File> compressedFiles) {
        progressDialog.setMessage("图片压缩成功，正在上传图片，请稍候...");

        createPostPresenter.upload(compressedFiles, "forum", "image", this);
    }

    @Override
    public void onCompressImageFail(String msg) {
        progressDialog.dismiss();
        showSnackBar(coordinatorLayout, "压缩图片失败：" + msg);
    }

    @Override
    public void onPermissionGranted(int action) {
        Matisse.from(this)
                .choose(MimeType.of(MimeType.JPEG, MimeType.PNG))
                .countable(true)
                .maxSelectable(20)
                .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
                .imageEngine(new GlideLoader4Matisse())
                .forResult(action);
    }

    @Override
    public void onPermissionRefused() {
        showSnackBar(coordinatorLayout, getString(R.string.permission_request));
    }

    @Override
    public void onPermissionRefusedWithNoMoreRequest() {
        showSnackBar(coordinatorLayout, getString(R.string.permission_refuse));
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
            boardName.setText(currentBoardName + "->" + currentFilterName);
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
            pollDesp.setText(a + b + c + d);
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ACTION_ADD_PHOTO && resultCode == Activity.RESULT_OK && data != null) {
            final List<String> path = Matisse.obtainPathResult(data);
            for (int i = 0; i < path.size(); i ++) {
                contentEditor.insertImage(path.get(i), 1000);
            }
        }
        if (requestCode == AT_USER_REQUEST && resultCode == AtUserListFragment.AT_USER_RESULT && data != null) {
            contentEditor.insertText(data.getStringExtra(Constant.IntentKey.AT_USER));
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
        postDraftBean.cat_id = currentFilterId;
        postDraftBean.title = postTitle.getText().toString();
        postDraftBean.content = jsonArray.toJSONString();
        postDraftBean.board_name = currentBoardName;
        postDraftBean.cat_name = currentFilterName;
        postDraftBean.time = createTime;
        postDraftBean.poll_options = currentPollOptions.toString();
        postDraftBean.poll_choices = currentPollChoice;
        postDraftBean.poll_exp = currentPollExp;
        postDraftBean.poll_visible = currentPollVisible;
        postDraftBean.poll_show_voters = currentPollShowVoters;

        List<PostDraftBean> list = LitePal
                .where("time = ?", String.valueOf(createTime))
                .find(PostDraftBean.class);
        if (list.size() != 0) {
            postDraftBean.updateAll("time = ?", String.valueOf(createTime));
        } else {
            postDraftBean.save();
        }

    }


    private CountDownTimer countDownTimer = new CountDownTimer(3000, 1000) {
        @Override
        public void onTick(long l) { }

        @Override
        public void onFinish() {
            onSaveDraftData();
            autoSaveText.setText( TimeUtil.getFormatDate(TimeUtil.getLongMs(), "HH:mm:ss") + "  已自动保存");
            new Handler().postDelayed(() -> countDownTimer.start(), 1000);
        }
    };

    @Override
    protected void onDestroy() {
        if (countDownTimer != null) countDownTimer.cancel();
        super.onDestroy();
    }
}
