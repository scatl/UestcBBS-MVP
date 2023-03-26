package com.scatl.uestcbbs.module.message.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.scatl.uestcbbs.R;
import com.scatl.uestcbbs.annotation.ToastType;
import com.scatl.uestcbbs.base.BaseActivity;
import com.scatl.uestcbbs.base.BaseEvent;
import com.scatl.uestcbbs.module.message.MessageManager;
import com.scatl.uestcbbs.widget.MyLinearLayoutManger;
import com.scatl.uestcbbs.widget.emoticon.EmoticonPanelLayout;
import com.scatl.uestcbbs.entity.PrivateChatBean;
import com.scatl.uestcbbs.entity.SendPrivateMsgResultBean;
import com.scatl.uestcbbs.entity.UploadResultBean;
import com.scatl.uestcbbs.helper.glidehelper.GlideEngineForPictureSelector;
import com.scatl.uestcbbs.module.message.adapter.PrivateChatAdapter;
import com.scatl.uestcbbs.module.message.presenter.PrivateChatPresenter;
import com.scatl.uestcbbs.util.Constant;
import com.scatl.uestcbbs.util.ImageUtil;
import com.scatl.uestcbbs.util.ToastUtil;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import am.widget.smoothinputlayout.SmoothInputLayout;

public class PrivateChatActivity extends BaseActivity<PrivateChatPresenter> implements PrivateChatView{

    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private PrivateChatAdapter privateChatAdapter;
    private ImageView addImageBtn, addEmoticonBtn, senBtn;
    private EditText chatContent;
    private EmoticonPanelLayout emoticonPanelLayout;
    private SmoothInputLayout lytContent;

    private int hisId;
    private String hisName, sendType, sendContent;
    private boolean isNewPm = false;

    @Override
    protected void getIntent(Intent intent) {
        super.getIntent(intent);
        hisId = intent.getIntExtra(Constant.IntentKey.USER_ID, Integer.MAX_VALUE);
        hisName = intent.getStringExtra(Constant.IntentKey.USER_NAME);
        isNewPm = intent.getBooleanExtra(Constant.IntentKey.IS_NEW_PM, false);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int setLayoutResourceId() {
        return R.layout.activity_private_chat;
    }

    @Override
    protected void findView() {
        toolbar = findViewById(R.id.toolbar);
        recyclerView = findViewById(R.id.private_chat_rv);
        addImageBtn = findViewById(R.id.private_chat_add_photo);
        addEmoticonBtn = findViewById(R.id.private_chat_add_emoticon);
        chatContent = findViewById(R.id.private_chat_edittext);
        senBtn = findViewById(R.id.private_chat_send_btn);
        emoticonPanelLayout = findViewById(R.id.private_chat_emoticon_layout);
        lytContent = findViewById(R.id.sil_lyt_content);
    }

    @Override
    protected void initView() {
        super.initView();
        toolbar.setTitle(hisName);

        senBtn.setOnClickListener(this);
        addImageBtn.setOnClickListener(this);
        addEmoticonBtn.setOnClickListener(this::onClickListener);
        chatContent.setOnClickListener(this::onClickListener);

        privateChatAdapter = new PrivateChatAdapter(R.layout.item_private_chat);
        privateChatAdapter.setHasStableIds(true);
        MyLinearLayoutManger myLinearLayoutManger = new MyLinearLayoutManger(this);
        myLinearLayoutManger.setStackFromEnd(true);
        recyclerView.setLayoutManager(myLinearLayoutManger);
        recyclerView.setAdapter(privateChatAdapter);
        recyclerView.setLayoutAnimation(AnimationUtils.loadLayoutAnimation(this, R.anim.layout_animation_scale_in));

        presenter.getPrivateMsg(hisId, this);
    }

    @Override
    protected PrivateChatPresenter initPresenter() {
        return new PrivateChatPresenter();
    }

    @Override
    protected void onClickListener(View view) {

        if (view.getId() == R.id.private_chat_add_photo) {
            PictureSelector.create(this)
                    .openGallery(PictureMimeType.ofImage())
                    .isCamera(true)
                    .isGif(false)
                    .showCropFrame(false)
                    .hideBottomControls(false)
                    .theme(com.luck.picture.lib.R.style.picture_WeChat_style)
                    .maxSelectNum(1)
                    .isEnableCrop(false)
                    .imageEngine(GlideEngineForPictureSelector.createGlideEngine())
                    .forResult(PictureConfig.CHOOSE_REQUEST);
        }

        if (view.getId() == R.id.private_chat_add_emoticon) {
            if (emoticonPanelLayout.getVisibility() == View.GONE) {
                lytContent.closeKeyboard(true);// 关闭键盘
                lytContent.showInputPane(true);//显示面板
            } else {
                lytContent.closeInputPane();// 关闭面板
                lytContent.showKeyboard();// 显示键盘
            }
        }

        if (view.getId() == R.id.private_chat_edittext) {
            lytContent.showKeyboard();// 显示键盘
        }

        if (view.getId() == R.id.private_chat_send_btn) {
            sendType = "text";
            sendContent = chatContent.getText().toString();
            presenter.sendPrivateMsg(
                    sendContent,
                    sendType, hisId, this );
        }
    }

    @Override
    protected void setOnItemClickListener() {
        privateChatAdapter.setOnItemChildClickListener((adapter, view, position) -> {
            if (view.getId() == R.id.item_private_chat_his_img || view.getId() == R.id.item_private_chat_mine_img) {
                List<String> urls = new ArrayList<>();
                urls.add(privateChatAdapter.getData().get(position).content);
                ImageUtil.showImages(this, urls, 0);
            }
        });

        privateChatAdapter.setOnItemLongClickListener((adapter, view, position) -> {
            presenter.showDeletePrivateMsgDialog(PrivateChatActivity.this,
                    privateChatAdapter.getData().get(position).mid, hisId, position);
            return true;
        });

    }

    @Override
    public void onGetPrivateListSuccess(PrivateChatBean privateChatBean) {
        privateChatAdapter.setHisInfo(privateChatBean.body.pmList.get(0).name,
                privateChatBean.body.pmList.get(0).avatar,
                privateChatBean.body.pmList.get(0).fromUid);
        recyclerView.scheduleLayoutAnimation();
        privateChatAdapter.setNewData(privateChatBean.body.pmList.get(0).msgList);
    }

    @Override
    public void onGetPrivateListError(String msg) {
        showToast(msg, ToastType.TYPE_ERROR);
    }

    @Override
    public void onSendPrivateChatMsgSuccess(SendPrivateMsgResultBean sendPrivateMsgResultBean) {
        chatContent.setText("");

        privateChatAdapter.insertMsg(this, sendContent, sendType);
        recyclerView.scrollToPosition(privateChatAdapter.getData().size() - 1);
        showToast(sendPrivateMsgResultBean.head.errInfo, ToastType.TYPE_SUCCESS);
    }

    @Override
    public void onSendPrivateChatMsgError(String msg) {
        showToast(msg, ToastType.TYPE_ERROR);
    }

    @Override
    public void onCompressImageSuccess(List<File> compressedFiles) {
        presenter.uploadImages(compressedFiles, "pm", "image", this);
    }

    @Override
    public void onCompressImageFail(String msg) {
        showToast(msg, ToastType.TYPE_ERROR);
    }

    @Override
    public void onUploadSuccess(UploadResultBean uploadResultBean) {
        sendType = "image";
        sendContent = uploadResultBean.body.attachment.get(0).urlName;
        presenter.sendPrivateMsg(sendContent, sendType, hisId, this);
    }

    @Override
    public void onUploadError(String msg) {
        showToast(msg, ToastType.TYPE_ERROR);
    }

    @Override
    public void onDeleteSinglePmSuccess(String msg, int position) {
        privateChatAdapter.deleteMsg(position);
        ToastUtil.showToast(this, msg, ToastType.TYPE_SUCCESS);
    }

    @Override
    public void onDeleteSinglePmError(String msg) {
        showToast(msg, ToastType.TYPE_ERROR);
    }

    @Override
    public void showMsg(String msg) {
        showToast(msg, ToastType.TYPE_NORMAL);
    }

    @Override
    protected boolean registerEventBus() {
        return true;
    }

    @Override
    protected void receiveEventBusMsg(BaseEvent baseEvent) {
        if (baseEvent.eventCode == BaseEvent.EventCode.INSERT_EMOTION) {
            presenter.insertEmotion(this, chatContent, (String) baseEvent.eventData);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == PictureConfig.CHOOSE_REQUEST) {
            List<LocalMedia> selectList = PictureSelector.obtainMultipleResult(data);
            List<String> files = new ArrayList<>();
            for (int i = 0; i < selectList.size(); i ++) {
                files.add(selectList.get(i).getRealPath());
            }
            presenter.checkBeforeSendImage(this, files);
        }
    }
}
