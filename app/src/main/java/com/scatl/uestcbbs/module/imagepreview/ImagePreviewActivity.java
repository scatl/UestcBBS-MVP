package com.scatl.uestcbbs.module.imagepreview;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.DrawableImageViewTarget;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.scatl.uestcbbs.R;
import com.scatl.uestcbbs.base.BaseActivity;
import com.scatl.uestcbbs.base.BasePresenter;
import com.scatl.uestcbbs.util.Constant;

public class ImagePreviewActivity extends BaseActivity {

    ImageView previewImage;

    String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //postponeEnterTransition();

        //getWindow().setBackgroundDrawableResource(android.R.color.transparent);
    }

    @Override
    protected void getIntent(Intent intent) {
        if (intent != null) {
            url = intent.getStringExtra(Constant.IntentKey.URL);
        }
    }

    @Override
    protected int setLayoutResourceId() {
        return R.layout.activity_image_preview;
    }

    @Override
    protected void findView() {
        previewImage = findViewById(R.id.image_preview_iamge);
    }

    @Override
    protected void initView() {
        Glide.with(this)
                .load(url)
                .into(new SimpleTarget<Drawable>() {
                    @Override
                    public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                        previewImage.setImageDrawable(resource);
                        //supportStartPostponedEnterTransition();
                    }
                });
    }

    @Override
    protected BasePresenter initPresenter() {
        return null;
    }

    @Override
    public void onBackPressed() {
        supportFinishAfterTransition();
    }
}