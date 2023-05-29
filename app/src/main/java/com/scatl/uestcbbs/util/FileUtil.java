package com.scatl.uestcbbs.util;

import android.annotation.SuppressLint;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.TextUtils;

import androidx.core.content.FileProvider;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.text.DecimalFormat;
import java.util.SplittableRandom;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * author: sca_tl
 * description:
 * date: 2019/07/17 14:12
 */
public class FileUtil {

    /**
     * 获取去掉拓展名的文件名
     * @param name
     * @return: String
     */
    public static String getFileName(String name) {
        if (name != null && name.length() > 0) {
            int j = name.lastIndexOf(".");
            if ((j > -1) && (j < name.length())) {
                return name.substring(0, j);
            }
        }
        return name;
    }

    public static String getFileExtension(String name) {
        if (name != null && name.length() > 0) {
            int j = name.lastIndexOf(".");
            if ((j > -1) && (j < name.length() - 1)) {
                return name.substring(j + 1);
            }
        }
        return name;
    }

    /**
     * author: sca_tl
     * description: 删除文件夹
     * @param dir 路径
     * @param delete_self 是否删除自己
     */
    public static void deleteDir(File dir, boolean delete_self) {
        if (dir == null || !dir.exists() || !dir.isDirectory()) {
            return;
        }

        try {
            for (File file : dir.listFiles()) {
                if (file.isFile())
                    file.delete(); // 删除所有文件
                else if (file.isDirectory())
                    deleteDir(file, false); // 递规的方式删除文件夹
            }
            if (delete_self) dir.delete();// 删除目录本身
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * author: sca_tl
     * description: 删除文件
     */
    public static void deleteFile(File file) {
        if (file != null && file.isFile() && file.exists()) {
            file.delete();
        }
    }

    /**
     * author: sca_tl
     * description: 获取文件MD5
     */
    public static String getFileMD5(File file) {

        if (!file.isFile()) {
            return null;
        }
        MessageDigest digest;
        FileInputStream in;
        byte[] buffer = new byte[1024];
        int len;
        try {
            digest = MessageDigest.getInstance("MD5");
            in = new FileInputStream(file);
            while ((len = in.read(buffer, 0, 1024)) != -1) {
                digest.update(buffer, 0, len);
            }
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return bytesToHexString(digest.digest());
    }

    private static String bytesToHexString(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder();
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }

    /**
     * author: sca_tl
     * description: 解压文件
     * @param zipPath 压缩文件路径
     * @param outputPath 输出路径
     * @param deleteSourceFile 完成后是否删除压缩文件
     */
    public static void unzipFile(String zipPath, String outputPath, boolean deleteSourceFile) {
        try {

            // 创建解压目标目录
            File file = new File(outputPath);
            // 如果目标目录不存在，则创建
            if (!file.exists()) { file.mkdirs(); }
            // 打开压缩文件
            InputStream inputStream = new FileInputStream(zipPath);
            ZipInputStream zipInputStream = new ZipInputStream(inputStream);

            // 读取一个进入点
            ZipEntry zipEntry = zipInputStream.getNextEntry();
            // 使用1Mbuffer
            byte[] buffer = new byte[1024 * 1024];
            // 解压时字节计数
            int count = 0;
            // 如果进入点为空说明已经遍历完所有压缩包中文件和目录
            while (zipEntry != null) {
                if (!zipEntry.isDirectory()) {  //如果是一个文件
                    // 如果是文件
                    String fileName = zipEntry.getName();
                    fileName = fileName.substring(fileName.lastIndexOf("/") + 1);  //截取文件的名字 去掉原文件夹名字
                    file = new File(outputPath + File.separator + fileName);  //放到新的解压的文件路径
                    file.createNewFile();
                    FileOutputStream fileOutputStream = new FileOutputStream(file);
                    while ((count = zipInputStream.read(buffer)) > 0) {
                        fileOutputStream.write(buffer, 0, count);
                    }
                    fileOutputStream.close();

                }
                // 定位到下一个文件入口
                zipEntry = zipInputStream.getNextEntry();
            }
            zipInputStream.close();

            if (deleteSourceFile) deleteFile(new File(zipPath));

        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    /**
     * author: sca_tl
     * description: 保存字符串为文件
     */
    public static void saveStringToFile(String s, File outFile) {
        FileWriter writer;
        try {
            writer = new FileWriter(outFile);
            writer.write("");//清空原文件内容
            writer.write(s);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * author: sca_tl
     * description: 读取文本文件
     */
    public static String readTextFile(File file) {
        String jsonStr;
        try {

            FileReader fileReader = new FileReader(file);

            Reader reader = new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8);
            int ch;
            StringBuilder sb = new StringBuilder();
            while ((ch = reader.read()) != -1) {
                sb.append((char) ch);
            }
            fileReader.close();
            reader.close();
            jsonStr = sb.toString();
            return jsonStr;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String readAssetFile(Context context, String name){
        String data = "";
        InputStream is;
        try {
            is = context.getAssets().open(name);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            int len;
            byte[] buffer = new byte[1024];
            while ((len = is.read(buffer)) != -1) {
                baos.write(buffer, 0, len);
            }
            data = baos.toString();
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return data;
    }

    /**
     * author: sca_tl
     * description: 根据拓展名判断文件类型
     */
    public static boolean isPicture(String filename) {
        return filename != null && (filename.endsWith(".png") || filename.endsWith(".jpg") || filename.endsWith(".jpe")
                || filename.endsWith("jpeg") || filename.endsWith(".gif") || filename.endsWith(".PNG")
                || filename.endsWith(".JPG") || filename.endsWith(".JPE")
                || filename.endsWith("JPEG") || filename.endsWith(".GIF"));
    }

    public static boolean isVideo(String filename) {
        return filename != null && (filename.endsWith(".flv") || filename.endsWith(".mp4") || filename.endsWith(".FLV") ||
                filename.endsWith(".MP4"));
    }

    public static boolean isAudio(String filename) {
        return filename != null && (filename.endsWith(".mp3") || filename.endsWith(".MP3"));
    }

    public static boolean isCompressed(String filename) {
        return filename != null && (filename.endsWith(".zip") || filename.endsWith(".rar") || filename.endsWith(".tar")
                || filename.endsWith(".gz") || filename.endsWith(".xz") || filename.endsWith(".bz2")
                || filename.endsWith(".7z") || filename.endsWith(".ZIP") || filename.endsWith(".RAR")
                || filename.endsWith(".TAR") || filename.endsWith(".GZ") || filename.endsWith(".XZ")
                || filename.endsWith(".BZ2") || filename.endsWith(".7Z"));
    }

    public static boolean isApplication(String filename) {
        return filename != null && (filename.endsWith(".apk") || filename.endsWith(".ipa") || filename.endsWith(".APK")
                || filename.endsWith(".IPA"));
    }

    public static boolean isPlugIn(String filename) {
        return filename != null && (filename.endsWith(".crx") || filename.endsWith(".CRX"));
    }

    public static boolean isPdf(String filename) {
        return filename != null && (filename.endsWith(".pdf") || filename.endsWith(".PDF"));
    }

    public static boolean isDocument(String filename) {
        return filename != null && (filename.endsWith(".caj") || filename.endsWith(".ppt") || filename.endsWith(".pptx")
                || filename.endsWith(".doc") || filename.endsWith(".docx") || filename.endsWith(".xls")
                || filename.endsWith(".xlsx") || filename.endsWith(".txt") || filename.endsWith(".CAJ")
                || filename.endsWith(".PPT") || filename.endsWith(".PPTX") || filename.endsWith(".DOC")
                || filename.endsWith(".DOCX") || filename.endsWith(".XLS") || filename.endsWith(".XLSX")
                || filename.endsWith(".TXT"));
    }

    public static String getFileType(String fileName) {
        if (TextUtils.isEmpty(fileName)) return "";

        int index = fileName.lastIndexOf(".");

        return index == -1 ? "" : fileName.substring(index + 1);
    }
}

