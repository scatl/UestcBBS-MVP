package com.scatl.uestcbbs.helper.glidehelper;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.scatl.uestcbbs.R;

public class GlideLoader4Common {

    public static <T extends ImageView> void simpleLoad(Context context, String url, final T imageView) {
        Glide.with(context)
//                .applyDefaultRequestOptions(new RequestOptions()
//                        .placeholder(R.drawable.img_loading_img))
                .load(url).into(imageView);
    }

    public static <T extends ImageView> void simpleLoad(Context context, int resId, final T imageView) {
        Glide.with(context).load(resId).into(imageView);
    }

    public static <T extends ImageView> void loadIntoTarget(Context context, String url, final T imageView) {
        Glide
                .with(context)
                .load(url)
                .placeholder(R.drawable.img_loading_img)
                .dontAnimate()
                .into(new CustomTarget<Drawable>() {
                    @Override
                    public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                        imageView.setImageDrawable(resource);
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {

                    }

                    @Override
                    public void onLoadStarted(@Nullable Drawable placeholder) {
                        super.onLoadStarted(placeholder);
                        if (placeholder != null) imageView.setImageDrawable(placeholder);
                    }

                    @Override
                    public void onLoadFailed(@Nullable Drawable errorDrawable) {
                        super.onLoadFailed(errorDrawable);
                    }
                });
    }

    public static <T extends ImageView> void loadIntoTarget(Context context, int resId, final T imageView) {
        Glide
                .with(context)
                .load(resId)
                .placeholder(R.drawable.img_loading_img)
                .dontAnimate()
                .into(new CustomTarget<Drawable>() {
                    @Override
                    public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                        imageView.setImageDrawable(resource);
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {

                    }

                    @Override
                    public void onLoadStarted(@Nullable Drawable placeholder) {
                        super.onLoadStarted(placeholder);
                        if (placeholder != null) imageView.setImageDrawable(placeholder);
                    }

                    @Override
                    public void onLoadFailed(@Nullable Drawable errorDrawable) {
                        super.onLoadFailed(errorDrawable);
                    }
                });
    }

}
