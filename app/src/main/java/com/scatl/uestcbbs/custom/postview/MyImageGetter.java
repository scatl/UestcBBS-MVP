package com.scatl.uestcbbs.custom.postview;

import android.content.Context;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.Request;
import com.bumptech.glide.request.target.ViewTarget;
import com.bumptech.glide.request.transition.Transition;
import com.scatl.uestcbbs.R;

import java.util.HashSet;
import java.util.Set;

/**
 * author: sca_tl
 * description: from：https://blog.csdn.net/u013836857/article/details/80826697
 * date: 2019/07/20 9:27
 */
public class MyImageGetter implements Html.ImageGetter, Drawable.Callback{

    private  Context context;
    private TextView textView;
    private Set<ImageGetterTarget> imageGetterTargets;

    public static MyImageGetter get(View view) {
        return (MyImageGetter) view.getTag(R.id.drawable_tag);
    }

    public MyImageGetter(Context context, TextView textView){
        this.context = context;
        this.textView = textView;
        imageGetterTargets = new HashSet<>();
        this.textView.setTag(R.id.drawable_tag, this);
    }

    @Override
    public Drawable getDrawable(String s) {
        final GlideUrlDrawable glideUrlDrawable = new GlideUrlDrawable();
        Glide
            .with(context)
            .load(s)
            .into(new ImageGetterTarget(textView, glideUrlDrawable));
        return glideUrlDrawable;
    }


    @Override
    public void invalidateDrawable(@NonNull Drawable drawable) {
        textView.invalidate();
    }

    @Override
    public void scheduleDrawable(@NonNull Drawable drawable, @NonNull Runnable runnable, long l) { }

    @Override
    public void unscheduleDrawable(@NonNull Drawable drawable, @NonNull Runnable runnable) { }

    private class ImageGetterTarget extends ViewTarget<TextView, Drawable> {
        private GlideUrlDrawable glideUrlDrawable;
        private Request request;

        @Override
        public void setRequest(@Nullable Request request) {
            this.request = request;
        }

        @Nullable
        @Override
        public Request getRequest() {
            return request;
        }

        private ImageGetterTarget(TextView view, GlideUrlDrawable glideUrlDrawable) {
            super(view);
            this.glideUrlDrawable = glideUrlDrawable;
            imageGetterTargets.add(this);
        }

        @Override
        public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {

            resource.setBounds(10, 10, 85, 85);
            glideUrlDrawable.setBounds(10, 10, 85, 85);

            glideUrlDrawable.setDrawable(resource);
            if (resource instanceof Animatable) {
                glideUrlDrawable.setCallback(get(getView()));
                ((Animatable)resource).start();
            }
            getView().setText(getView().getText());
            getView().invalidate();
        }


    }
}
