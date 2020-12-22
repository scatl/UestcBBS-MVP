package com.scatl.uestcbbs.util;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.scatl.uestcbbs.R;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import cc.shinichi.library.ImagePreview;

public class ImageUtil {
    /**
     * author: TanLei
     * description: 图片模糊
     */
    public static Bitmap blurPhoto(Context context, Bitmap bitmap, float radius){
        Bitmap result = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        RenderScript renderScript = RenderScript.create(context);
        ScriptIntrinsicBlur blur = ScriptIntrinsicBlur.create(renderScript, Element.U8_4(renderScript));
        Allocation in = Allocation.createFromBitmap(renderScript, bitmap);
        Allocation out = Allocation.createFromBitmap(renderScript, result);
        blur.setRadius(radius);
        blur.setInput(in);
        blur.forEach(out);
        out.copyTo(result);
        renderScript.destroy();
        return result;
    }

    public static Drawable bitmap2Drawable(Bitmap bitmap) {
        return new BitmapDrawable(bitmap);
    }

    public static Bitmap drawable2Bitmap(Drawable drawable) {
        return ((BitmapDrawable) drawable).getBitmap();
    }

    /**
     * author: TanLei
     * description: 多图浏览
     */
    public static void showImages(Context context, List<String> urls, int selected) {
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

    public static String imageFileToBase64(String path){
        if(TextUtils.isEmpty(path)){
            return null;
        }
        InputStream is = null;
        byte[] data = null;
        String result = null;
        try{
            is = new FileInputStream(path);
            //创建一个字符流大小的数组。
            data = new byte[is.available()];
            //写入数组
            is.read(data);
            //用默认的编码格式进行编码
            result = Base64.encodeToString(data,Base64.NO_WRAP);
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if(null !=is){
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
        return result;
    }

    public static String bitmapToBase64(Bitmap bitmap) {
        String result = null;
        ByteArrayOutputStream baos = null;
        try {
            if (bitmap != null) {
                baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);

                baos.flush();
                baos.close();

                byte[] bitmapBytes = baos.toByteArray();
                result = Base64.encodeToString(bitmapBytes, Base64.NO_WRAP);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (baos != null) {
                    baos.flush();
                    baos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
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

    /**
     * author: sca_tl
     * description: 图片压缩
     */
    public static Bitmap bitmapCompress(Bitmap bitmap) {
        int height = bitmap.getHeight();
        int width = bitmap.getWidth();
        int bitmap_size = bitmap.getByteCount() / 1024 / 1024;

        if (bitmap_size < 5) {  //占用＜5MB，不压缩
            return bitmap;

        } else if (bitmap_size <= 7){
            if (height / width > 3) {
                Bitmap new_bitmap = Bitmap.createScaledBitmap(bitmap, (int)(width / 1.2), (int)(height / 1.2), true);
                new_bitmap.setHeight(new_bitmap.getHeight() / 2);
                return new_bitmap;
            } else {
                Bitmap new_bitmap = Bitmap.createScaledBitmap(bitmap, width / 2, height / 2, true);
                return new_bitmap;
            }

        } else if (bitmap_size <= 10) {
            if (height / width > 3) {
                Bitmap new_bitmap = Bitmap.createScaledBitmap(bitmap, (int)(width / 1.4), (int)(height / 1.4), true);
                new_bitmap.setHeight(new_bitmap.getHeight() / 2);
                return new_bitmap;
            } else {
                Bitmap new_bitmap = Bitmap.createScaledBitmap(bitmap, (int)(width / 2), (int)(height / 2), true);
                return new_bitmap;
            }

        } else {
            if (height / width > 3) {
                Bitmap new_bitmap = Bitmap.createScaledBitmap(bitmap, (int)(width / 1.6), (int)(height / 1.6), true);
                new_bitmap.setHeight(new_bitmap.getHeight() / 2);
                return new_bitmap;
            } else {
                Bitmap new_bitmap = Bitmap.createScaledBitmap(bitmap, (int)(width / 2), (int)(height / 2), true);
                return new_bitmap;
            }
        }
    }
}
