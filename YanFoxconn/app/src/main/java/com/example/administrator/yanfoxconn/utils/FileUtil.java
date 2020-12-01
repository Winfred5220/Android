/*
 * Copyright (C) 2017 Baidu, Inc. All Rights Reserved.
 */
package com.example.administrator.yanfoxconn.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;

import java.io.File;

public class FileUtil {

    private static String filepath = Environment.getExternalStorageDirectory() + File.separator + "YanFoxconn" + File.separator + "Photos" + File.separator;
    private static String filepath2 = Environment.getExternalStorageDirectory() + File.separator + "Pictures" + File.separator + "YanFoxconn" + File.separator;

    public static File getSaveFile(Context context) {
        File file = new File(context.getFilesDir(), "pic.jpg");
        return file;
    }

    //删除文件夹和文件夹里面的文件
    public static void deleteDir(final String pPath,Context mcontext) {

        File dir = new File(pPath);
        deleteDirWihtFile(dir,mcontext);
    }

    //删除文件
    public static void deleteDirWihtFile(File dir,Context mcontext) {
        if (dir == null || !dir.exists() || !dir.isDirectory())
            return;
        for (File file : dir.listFiles()) {
            if (file.isFile()){
                String pathfile = file.getAbsolutePath();
                file.delete(); // 删除所有文件
                Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                Uri uri = Uri.fromFile(new File(pathfile));
                intent.setData(uri);
                mcontext.sendBroadcast(intent);
                //这个广播的目的就是更新图库，发了这个广播进入相册就可以找到你保存的图片了！，记得要传你更新的file哦
            }
            else if (file.isDirectory())
                deleteDirWihtFile(file,mcontext); // 递规的方式删除文件夹
        }
        dir.delete();// 删除目录本身
    }
    //删除文件夹和文件夹里面的文件
    public static void deleteDir(final String pPath) {
        File dir = new File(pPath);
        deleteDirWihtFile(dir);
    }

    public static void deleteDirWihtFile(File dir) {
        if (dir == null || !dir.exists() || !dir.isDirectory())
            return;
        for (File file : dir.listFiles()) {
            if (file.isFile())
                file.delete(); // 删除所有文件
            else if (file.isDirectory())
                deleteDirWihtFile(file); // 递规的方式删除文件夹
        }
        dir.delete();// 删除目录本身
    }

    //上傳完删除圖片Photos
    public static void deletePhotos(Context mcontext) {
        //刪除壓縮圖片
        File dir = new File(filepath);
        deleteDirWihtFile(dir,mcontext);
        //刪除拍照圖片
        File dir2 = new File(filepath2);
        deleteDirWihtFile(dir2,mcontext);
    }

}
