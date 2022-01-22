package com.scatl.uestcbbs.module.user.view;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.scatl.uestcbbs.R;
import com.scatl.uestcbbs.annotation.ToastType;
import com.scatl.uestcbbs.base.BaseActivity;
import com.scatl.uestcbbs.base.BasePresenter;
import com.scatl.uestcbbs.helper.glidehelper.GlideEngineForPictureSelector;
import com.scatl.uestcbbs.helper.glidehelper.GlideLoader4Common;
import com.scatl.uestcbbs.module.main.view.MainActivity;
import com.scatl.uestcbbs.module.user.presenter.ModifyAvatarPresenter;
import com.scatl.uestcbbs.util.Constant;
import com.scatl.uestcbbs.util.FileUtil;
import com.scatl.uestcbbs.util.ImageUtil;
import com.scatl.uestcbbs.util.SharePrefUtil;

import java.net.URLDecoder;
import java.util.List;

public class ModifyAvatarActivity extends BaseActivity implements ModifyAvatarView{

    Toolbar toolbar;
    TextView hint, restartBtn;
    View layout;
    LottieAnimationView loading;
    ImageView avatarPreview1, avatarPreview2, avatarPreview3;
    Button selectAvatar, uploadAvatar;

    String agent, input;
    String avatar1Base64, avatar2Base64, avatar3Base64;

    boolean avatarSelected;

    ModifyAvatarPresenter modifyAvatarPresenter;

    @Override
    protected int setLayoutResourceId() {
        return R.layout.activity_modify_avatar;
    }

    @Override
    protected void findView() {
        toolbar = findViewById(R.id.modify_avatar_toolbar);
        hint = findViewById(R.id.modify_avatar_hint);
        loading = findViewById(R.id.modify_avatar_loading);
        layout = findViewById(R.id.modify_avatar_layout);
        avatarPreview1 = findViewById(R.id.modify_avatar_preview_avatar1);
        avatarPreview2 = findViewById(R.id.modify_avatar_preview_avatar2);
        avatarPreview3 = findViewById(R.id.modify_avatar_preview_avatar3);
        selectAvatar = findViewById(R.id.modify_avatar_select_avatar_btn);
        uploadAvatar = findViewById(R.id.modify_avatar_upload_avatar_btn);
        restartBtn = findViewById(R.id.modify_avatar_restart_btn);
    }

    @Override
    protected void initView() {
        modifyAvatarPresenter = (ModifyAvatarPresenter) presenter;
        selectAvatar.setOnClickListener(this);
        uploadAvatar.setOnClickListener(this::onClickListener);
        restartBtn.setOnClickListener(this::onClickListener);
        layout.setVisibility(View.GONE);

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        GlideLoader4Common.simpleLoad(this, SharePrefUtil.isLogin(this) ? SharePrefUtil.getAvatar(this) : Constant.DEFAULT_AVATAR, avatarPreview1);
        GlideLoader4Common.simpleLoad(this, SharePrefUtil.isLogin(this) ? SharePrefUtil.getAvatar(this) : Constant.DEFAULT_AVATAR, avatarPreview2);
        GlideLoader4Common.simpleLoad(this, SharePrefUtil.isLogin(this) ? SharePrefUtil.getAvatar(this) : Constant.DEFAULT_AVATAR, avatarPreview3);

        modifyAvatarPresenter.getPare();
    }

    @Override
    protected BasePresenter initPresenter() {
        return new ModifyAvatarPresenter();
    }

    @Override
    protected void onClickListener(View view) {
        if (view.getId() == R.id.modify_avatar_select_avatar_btn) {
            PictureSelector.create(this)
                    .openGallery(PictureMimeType.ofImage())
                    .isCamera(false)
                    .isGif(false)
                    .showCropFrame(true)
                    .hideBottomControls(false)
                    .theme(com.luck.picture.lib.R.style.picture_WeChat_style)
                    .maxSelectNum(1)
                    .isEnableCrop(true)
                    .cropImageWideHigh(200, 200)
                    .withAspectRatio(1, 1)
                    .imageEngine(GlideEngineForPictureSelector.createGlideEngine())
                    .forResult(PictureConfig.CHOOSE_REQUEST);
        }
        if (view.getId() == R.id.modify_avatar_upload_avatar_btn) {
            if (avatarSelected) {
                uploadAvatar.setEnabled(false);
                uploadAvatar.setText("请稍候...");
                modifyAvatarPresenter.modifyAvatar(agent, input, avatar1Base64, avatar2Base64, avatar3Base64);
            } else {
                showToast("请选择头像", ToastType.TYPE_WARNING);
            }
        }
        if (view.getId() == R.id.modify_avatar_restart_btn) {
            FileUtil.deleteDir(getCacheDir(), false);
            FileUtil.deleteDir(getExternalFilesDir(Constant.AppPath.TEMP_PATH), false);

            Intent killIntent = new Intent(this,MainActivity.class);
            killIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(killIntent);
            overridePendingTransition(R.anim.switch_night_mode_fade_in, R.anim.switch_night_mode_fade_out);
            android.os.Process.killProcess(android.os.Process.myPid());
            finish();
            System.exit(1);
        }
    }

    @Override
    public void onGetParaSuccess(String agent, String input) {
        //此处需要先解码，防止自动转码导致字符串请求和获取的不一致
        try {
            this.agent = URLDecoder.decode(agent, "GBK");
            this.input = URLDecoder.decode(input, "GBK");
        } catch (Exception e) {
            e.printStackTrace();
        }

        layout.setVisibility(View.VISIBLE);
        loading.setVisibility(View.GONE);
        hint.setText("");
    }

    @Override
    public void onGetParaError(String msg) {
        layout.setVisibility(View.GONE);
        loading.setVisibility(View.GONE);
        hint.setText(msg);
    }

    @Override
    public void onUploadSuccess(String msg) {
        uploadAvatar.setEnabled(true);
        uploadAvatar.setText("确认更改");
        showToast(msg, ToastType.TYPE_SUCCESS);
    }

    @Override
    public void onUploadError(String msg) {
        uploadAvatar.setEnabled(true);
        uploadAvatar.setText("确认更改");
        showToast(msg, ToastType.TYPE_ERROR);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == PictureConfig.CHOOSE_REQUEST) {
            List<LocalMedia> selectList = PictureSelector.obtainMultipleResult(data);
            if (selectList.size() != 0 && selectList.get(0).isCut()) {

                try {

                    Bitmap bitmap_200 = BitmapFactory.decodeFile(selectList.get(0).getCutPath());
                    int original_w = bitmap_200.getWidth();
                    int original_h = bitmap_200.getHeight();

                    if (original_w < 200 || original_h < 200) {
                        showToast("抱歉，图片尺寸不合法，请选择大于200X200的图片", ToastType.TYPE_WARNING);
                    } else {
                        avatarPreview1.setImageBitmap(bitmap_200);

                        Matrix matrix = new Matrix();
                        matrix.setScale(0.6f, 0.6f);
                        Bitmap bitmap_120 = Bitmap.createBitmap(bitmap_200, 0, 0, original_w, original_h, matrix, true);
                        avatarPreview2.setImageBitmap(bitmap_120);

                        matrix.setScale(0.24f, 0.24f);
                        Bitmap bitmap_48 = Bitmap.createBitmap(bitmap_200, 0, 0, original_w, original_h, matrix, true);
                        avatarPreview3.setImageBitmap(bitmap_48);


                        avatar1Base64 = ImageUtil.bitmapToBase64(bitmap_200);
                        avatar2Base64 = ImageUtil.bitmapToBase64(bitmap_120);
                        avatar3Base64 = ImageUtil.bitmapToBase64(bitmap_48);

                        avatarSelected = true;

                    }

                } catch (Exception e) {
                    showToast("抱歉，出现了一个错误：" +e.getMessage(), ToastType.TYPE_ERROR);
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        avatar1Base64 = null;
        avatar2Base64 = null;
        avatar3Base64 = null;
    }
}