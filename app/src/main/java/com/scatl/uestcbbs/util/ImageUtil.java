package com.scatl.uestcbbs.util;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.scatl.uestcbbs.R;
import com.scatl.uestcbbs.helper.glidehelper.GlideLoader4Common;
import com.scatl.widget.gallery.MediaEntity;
import com.scatl.widget.iamgeviewer.ImageViewer;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import cc.shinichi.library.ImagePreview;

//import cc.shinichi.library.ImagePreview;

public class ImageUtil {

    public static Bitmap getBitmapFromDisk(String path) {
        Bitmap bitmap = null;
        try {
            File file = new File(path);
            if (file.exists()) {
                bitmap = BitmapFactory.decodeFile(path);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    public static void showImages(Context context, List<String> urls, int selected) {
//        List<MediaEntity> entities = new ArrayList<>();
//        for (int i = 0; i < urls.size(); i ++) {
//            MediaEntity entity = new MediaEntity();
//            entity.setNet(true);
//            entity.setUri(Uri.parse(urls.get(i)));
//            entities.add(entity);
//        }
//        ImageViewer.Companion.getINSTANCE().with(context)
//                .setEnterIndex(selected)
//                .setMediaEntity(entities)
//                .show();
        ImagePreview
                .getInstance()
                .setContext(context)
                .setIndex(selected)
                .setImageList(urls)
                .setShowDownButton(true)
                .setDownIconResId(R.drawable.ic_save)
                .setEnableDragClose(true)
                .setEnableUpDragClose(true)
                .start();
    }

    public static int[] getImagePx(String path) {
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path);
        opts.inSampleSize = 1;
        opts.inJustDecodeBounds = false;

        int w = opts.outWidth;
        int h = opts.outHeight;

        return new int[]{w, h};
    }
}
