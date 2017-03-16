package com.robot.myhome;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;

import java.util.List;

/**
 * Created by zailong shi on 2017/3/16 0016.
 */

public class AppUtils
{
    private static volatile AppUtils instance;
    private List<ResolveInfo> apps;

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
            apps = context.getPackageManager().queryIntentActivities(mainIntent, 0);
        }
    }

    public List<ResolveInfo> getApps(Context context)
    {
        if (apps == null)
        {
            loadApps(context);
        }
        return apps;
    }
}
