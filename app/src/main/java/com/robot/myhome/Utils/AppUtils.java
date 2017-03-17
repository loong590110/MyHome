package com.robot.myhome.Utils;

import android.app.WallpaperManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;

import com.robot.myhome.been.AppBean;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zailong shi on 2017/3/16 0016.
 */

public class AppUtils
{
    private static volatile AppUtils instance;
    private List<AppBean> apps;
    private List<AppBean> dockApps;
    private SoftReference<Bitmap> blurBackground;

    private static synchronized void initInstance()
    {
        if (instance == null)
        {
            instance = new AppUtils();
        }
    }

    public static AppUtils getInstance()
    {
        if (instance == null)
        {
            initInstance();
        }
        return instance;
    }

    private synchronized void loadApps(Context context)
    {
        if (apps == null)
        {
            Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
            mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
            List<ResolveInfo> resolveInfos = context.getPackageManager().queryIntentActivities(mainIntent, 0);
            apps = new ArrayList<>();
            dockApps = new ArrayList<>();
            for (ResolveInfo resolveInfo : resolveInfos)
            {
                if (resolveInfo != null)
                {
                    AppBean appBean = new AppBean();
                    appBean.setIcon(resolveInfo.loadIcon(context.getPackageManager()));
                    appBean.setLabel((String) resolveInfo.loadLabel(context.getPackageManager()));
                    appBean.setPackageName(resolveInfo.activityInfo.packageName);
                    apps.add(appBean);
                    if (appBean.getLabel().equals("电话") || appBean.getLabel().equals("信息")
                            || appBean.getLabel().equals("互联网") || appBean.getLabel().equals("相机"))
                    {
                        dockApps.add(appBean);
                    }
                }
            }
        }
    }

    private synchronized void loadDockApps(Context context)
    {
        if (dockApps == null)
        {
            Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
            mainIntent.addCategory(Intent.CATEGORY_DESK_DOCK);
            List<ResolveInfo> resolveInfos = context.getPackageManager().queryIntentActivities(mainIntent, 0);
            dockApps = new ArrayList<>();
            for (ResolveInfo resolveInfo : resolveInfos)
            {
                if (resolveInfo != null)
                {
                    AppBean appBean = new AppBean();
                    appBean.setIcon(resolveInfo.loadIcon(context.getPackageManager()));
                    appBean.setLabel((String) resolveInfo.loadLabel(context.getPackageManager()));
                    appBean.setPackageName(resolveInfo.activityInfo.packageName);
                    dockApps.add(appBean);
                }
            }
        }
    }

    public List<AppBean> getApps(Context context)
    {
        if (apps == null)
        {
            loadApps(context);
        }
        return apps;
    }

    public List<AppBean> getDockApps(Context context)
    {
        if (dockApps == null)
        {
            //loadDockApps(context);
            loadApps(context);
        }
        return dockApps;
    }

    private synchronized void initWallpaper(Context context)
    {
        if (blurBackground == null || blurBackground.get() == null)
        {
            // 获取壁纸管理器
            WallpaperManager wallpaperManager = WallpaperManager.getInstance(context);
            Drawable wallpaperDrawable = wallpaperManager.getDrawable();
            Bitmap bm = ((BitmapDrawable) wallpaperDrawable).getBitmap();
            DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
            int screenWidth = displayMetrics.widthPixels;
            int screenHeight = displayMetrics.heightPixels;
            blurBackground = new SoftReference<>(new VisualUtil().fastBlurAuto(Bitmap.createBitmap(bm, 0, 0,
                    screenWidth, screenHeight), 16));
        }
    }

    public Bitmap getWallpaper(Context context)
    {
        if (blurBackground == null || blurBackground.get() == null)
        {
            initWallpaper(context);
        }
        return blurBackground.get();
    }

}
