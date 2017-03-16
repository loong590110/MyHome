package com.robot.myhome;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;


/**
 * Created by zailong shi on 2016/7/26 0026.
 */
public class FileUtils
{
    public final static int PICTURE_TYPE = 0;
    public final static int AUDIO_TYPE = 1;
    public final static int VIDEO_TYPE = 2;
    private static final String PACKAGE_NAME = "com.hebao.yidi";

    public static boolean clearCache(Context context)
    {
        String packageName = (context.getPackageName() == null ? PACKAGE_NAME : context.getPackageName());
        String ownCache = getOwnCacheDirectory(context, packageName + "/temp").getAbsolutePath();
        //清空包下所有的文件目录
        return delAllFile(ownCache);
    }

    //删除文件夹
    //param folderPath 文件夹完整绝对路径
    public static void delFolder(String folderPath)
    {
        try
        {
            delAllFile(folderPath); //删除完里面所有内容
            String filePath = folderPath;
            filePath = filePath.toString();
            File myFilePath = new File(filePath);
            myFilePath.delete(); //删除空文件夹
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    //删除指定文件夹下所有文件
    //param path 文件夹完整绝对路径
    public static boolean delAllFile(String path)
    {
        boolean flag = false;
        File file = new File(path);
        if (!file.exists())
        {
            return flag;
        }
        if (!file.isDirectory())
        {
            return flag;
        }
        String[] tempList = file.list();
        File temp = null;
        for (int i = 0; i < tempList.length; i++)
        {
            if (path.endsWith(File.separator))
            {
                temp = new File(path + tempList[i]);
            } else
            {
                temp = new File(path + File.separator + tempList[i]);
            }
            if (temp.isFile())
            {
                temp.delete();
            }
            if (temp.isDirectory())
            {
                delAllFile(path + "/" + tempList[i]);//先删除文件夹里面的文件
                delFolder(path + "/" + tempList[i]);//再删除空文件夹
                flag = true;
            }
        }
        return flag;
    }

    public static File getOwnCacheDirectory(Context context, String cacheDir)
    {

        File appCacheDir = null;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()) && hasExternalStoragePermission(context))
        {
            appCacheDir = new File(Environment.getExternalStorageDirectory(), cacheDir);
        }
        if (appCacheDir == null || (!appCacheDir.exists() && !appCacheDir.mkdirs()))
        {
            appCacheDir = context.getCacheDir();
        }
        return appCacheDir;
    }

    private static boolean hasExternalStoragePermission(Context context)
    {

        int perm = context.checkCallingOrSelfPermission("android.permission.WRITE_EXTERNAL_STORAGE");
        return perm == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * 根据文件的名称和类型获取文件的存储路径
     *
     * @param context
     * @param fileName
     * @param type     0：表示图片文件 1：表示语音文件 2：表示视频文件
     * @return
     */
    public static String getFileSavePath(Context context, String fileName, int type)
    {
        File cacheDir = null;
        if (type == PICTURE_TYPE)
        {
            cacheDir = getOwnCacheDirectory(context, context.getPackageName() + "/PcitureCache");

        } else if (type == AUDIO_TYPE)
        {
            cacheDir = getOwnCacheDirectory(context, context.getPackageName() + "/AudioCache");

        } else if (type == VIDEO_TYPE)
        {
            cacheDir = getOwnCacheDirectory(context, context.getPackageName() + "/VideoCache");
        }  else
        {
            cacheDir = getOwnCacheDirectory(context, context.getPackageName() + "/temp");
        }
        // 文件保存的路径
        String saveFilePath = cacheDir.getPath() + "/" + fileName;

        return saveFilePath;
    }


    public static void saveFile(Context context, String str)
    {
        String filePath = getFileSavePath(context, "hello.txt", -1);
        try
        {
            File file = new File(filePath);
            if (!file.exists())
            {
                File dir = new File(file.getParent());
                dir.mkdirs();
                file.createNewFile();
            }
            FileOutputStream outStream = new FileOutputStream(file);
            outStream.write(str.getBytes());
            outStream.close();
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
