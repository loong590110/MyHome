package com.robot.myhome.Utils;

import android.app.WallpaperManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.ArrayMap;
import android.util.DisplayMetrics;

import com.robot.myhome.been.AppBean;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zailong shi on 2017/3/16 0016.
 */

public class AppUtils
{
    public static final String PHONE = "phone";
    public static final String MSG = "msg";
    public static final String BROWSER = "browser";
    public static final String CAMERA = "camera";
    private static volatile AppUtils instance;
    private List<AppBean> apps;
    private Map<String, AppBean> dockApps;
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

    private synchronized void loadApps(Context context, boolean refresh)
    {
        if (apps == null || refresh)
        {
            Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
            mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
            List<ResolveInfo> resolveInfos = context.getPackageManager().queryIntentActivities(mainIntent, 0);
            apps = new ArrayList<>();
            dockApps = new HashMap<>();
            for (ResolveInfo resolveInfo : resolveInfos)
            {
                if (resolveInfo != null)
                {
                    AppBean appBean = new AppBean();
                    appBean.setIcon(resolveInfo.loadIcon(context.getPackageManager()));
                    appBean.setLabel((String) resolveInfo.loadLabel(context.getPackageManager()));
                    appBean.setPackageName(resolveInfo.activityInfo.packageName);
                    apps.add(appBean);
                    if (appBean.getLabel().equals("电话"))
                    {
                        dockApps.put(PHONE, appBean);
                    } else if (appBean.getLabel().equals("信息"))
                    {
                        dockApps.put(MSG, appBean);
                    } else if (appBean.getLabel().equals("互联网"))
                    {
                        dockApps.put(BROWSER, appBean);
                    } else if (appBean.getLabel().equals("相机"))
                    {
                        dockApps.put(CAMERA, appBean);
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
            dockApps = new HashMap<>();
            for (ResolveInfo resolveInfo : resolveInfos)
            {
                if (resolveInfo != null)
                {
                    AppBean appBean = new AppBean();
                    appBean.setIcon(resolveInfo.loadIcon(context.getPackageManager()));
                    appBean.setLabel((String) resolveInfo.loadLabel(context.getPackageManager()));
                    appBean.setPackageName(resolveInfo.activityInfo.packageName);
                    dockApps.put("", appBean);
                }
            }
        }
    }

    public List<AppBean> getApps(Context context)
    {
        return getApps(context, false);
    }

    public List<AppBean> getApps(Context context, boolean refresh)
    {
        if (refresh || apps == null)
        {
            loadApps(context, refresh);
        }
        return apps;
    }

    public Map<String, AppBean> getDockApps(Context context)
    {
        return getDockApps(context, false);
    }

    public Map<String, AppBean> getDockApps(Context context, boolean refresh)
    {
        if (refresh || dockApps == null)
        {
            //loadDockApps(context);
            loadApps(context, refresh);
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
